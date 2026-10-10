package local.socialnetwork.comments.service.impl;

import local.socialnetwork.comments.dto.ReplyTone;

import local.socialnetwork.comments.entity.Comment;

import local.socialnetwork.comments.prompt.CommentReplyPrompts;

import local.socialnetwork.comments.repository.CommentRepository;

import local.socialnetwork.comments.service.CommentReplySuggestionService;

import local.socialnetwork.core.ai.AiChatService;
import local.socialnetwork.core.ai.AiRequestLimiter;

import local.socialnetwork.posts.service.PostService;

import local.socialnetwork.shared.exception.AiChatException;
import local.socialnetwork.shared.exception.CommentNotFoundException;
import local.socialnetwork.shared.exception.CommentSuggestionGenerationException;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageRequest;

import org.springframework.stereotype.Service;

import org.springframework.transaction.support.TransactionOperations;

import java.util.List;
import java.util.UUID;
import java.util.Arrays;
import java.util.Objects;

/**
 * Default implementation of {@link CommentReplySuggestionService}. Composes the reply-suggestions
 * prompt from persisted comment/post/thread data and delegates the actual model call to
 * {@link AiChatService}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentReplySuggestionServiceImpl implements CommentReplySuggestionService {

    private static final int THREAD_CONTEXT_LIMIT = 10;

    private final CommentRepository commentRepository;
    private final PostService postService;
    private final AiChatService aiChatService;
    private final AiRequestLimiter aiRequestLimiter;
    private final TransactionOperations transactionOperations;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> generateReplySuggestions(UUID authUserId, UUID commentId, ReplyTone tone) {
        var effectiveTone = Objects.requireNonNullElse(tone, ReplyTone.NEUTRAL);
        var userPrompt = transactionOperations.execute(status -> loadUserPrompt(commentId));
        var systemPrompt = CommentReplyPrompts.systemPrompt(effectiveTone);
        aiRequestLimiter.acquire(authUserId);
        try {
            log.info("Generating reply suggestions for comment {} (tone={}) requested by auth user id: {}",
                    commentId, effectiveTone, authUserId);
            var response = aiChatService.chat(systemPrompt, userPrompt);
            var suggestions = parseSuggestions(response);
            if (suggestions.isEmpty()) {
                throw new CommentSuggestionGenerationException(
                        "AI provider returned no usable suggestions for comment: " + commentId);
            }
            return suggestions;
        } catch (CommentSuggestionGenerationException | AiChatException e) {
            log.error("Failed to generate reply suggestions for comment '{}': {}", commentId, e.getMessage(), e);
            throw new CommentSuggestionGenerationException(
                    "An error occurred while generating comment reply suggestions", e);
        }
    }

    private String loadUserPrompt(UUID commentId) {
        var comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found for id: " + commentId));
        var post = postService.getPost(comment.getPost().getId());
        return buildUserPrompt(post.content(), comment, resolveThreadContext(comment));
    }

    private List<String> resolveThreadContext(Comment comment) {
        var threadRootId = comment.getParent() != null ? comment.getParent().getId() : comment.getId();
        return commentRepository
                .findByParentIdOrderByCreatedAtDesc(threadRootId, PageRequest.of(0, THREAD_CONTEXT_LIMIT + 1))
                .stream()
                .filter(reply -> !reply.getId().equals(comment.getId()))
                .limit(THREAD_CONTEXT_LIMIT)
                .map(Comment::getContent)
                .toList();
    }

    private String buildUserPrompt(String postContent, Comment comment, List<String> threadContext) {
        var prompt = new StringBuilder()
                .append("Post: ").append(postContent).append("\n");
        if (comment.getParent() != null) {
            prompt.append("Parent comment: ").append(comment.getParent().getContent()).append("\n");
        }
        if (threadContext.isEmpty()) {
            prompt.append("Thread context: none\n");
        } else {
            prompt.append("Thread context (other replies in this thread, most recent first):\n");
            for (int i = 0; i < threadContext.size(); i++) {
                prompt.append(i + 1).append(". ").append(threadContext.get(i)).append("\n");
            }
        }
        prompt.append("Comment to reply to: ").append(comment.getContent());
        return prompt.toString();
    }

    /**
     * Parses the model's numbered-list response into individual suggestion strings, stripping any
     * leading numbering (e.g. {@code "1. "} or {@code "1) "}).
     */
    private List<String> parseSuggestions(String response) {
        return Arrays.stream(response.split("\n"))
                .map(String::trim)
                .map(line -> line.replaceFirst("^\\d+[.)]\\s*", ""))
                .filter(line -> !line.isBlank())
                .toList();
    }
}
