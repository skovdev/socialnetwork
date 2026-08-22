package local.socialnetwork.comments.service;

import local.socialnetwork.comments.dto.ReplyTone;

import local.socialnetwork.comments.entity.Comment;

import local.socialnetwork.comments.repository.CommentRepository;

import local.socialnetwork.comments.service.impl.CommentReplySuggestionServiceImpl;

import local.socialnetwork.core.ai.AiChatService;

import local.socialnetwork.posts.dto.http.response.PostResponse;

import local.socialnetwork.posts.entity.Post;

import local.socialnetwork.posts.service.PostService;

import local.socialnetwork.shared.exception.AiChatException;
import local.socialnetwork.shared.exception.CommentNotFoundException;
import local.socialnetwork.shared.exception.CommentSuggestionGenerationException;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.ArgumentCaptor;

import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentReplySuggestionServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostService postService;

    @Mock
    private AiChatService aiChatService;

    @InjectMocks
    private CommentReplySuggestionServiceImpl service;

    private Post post(UUID postId, String content) {
        var post = new Post();
        post.setId(postId);
        post.setContent(content);
        return post;
    }

    private Comment comment(UUID commentId, Post post, Comment parent, String content) {
        var comment = new Comment();
        comment.setId(commentId);
        comment.setPost(post);
        comment.setParent(parent);
        comment.setContent(content);
        return comment;
    }

    private static final String THREE_LINE_RESPONSE = """
            1. Thanks so much!
            2. Really appreciate that.
            3. Glad you liked it!""";

    @Test
    void generateReplySuggestions_whenCommentNotFound_throwsCommentNotFoundException() {
        var commentId = UUID.randomUUID();
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.generateReplySuggestions(UUID.randomUUID(), commentId, null))
                .isInstanceOf(CommentNotFoundException.class)
                .hasMessageContaining(commentId.toString());
    }

    @Test
    void generateReplySuggestions_forTopLevelComment_returnsParsedSuggestionsAndOmitsParentFromPrompt() {
        var postId = UUID.randomUUID();
        var commentId = UUID.randomUUID();
        var post = post(postId, "Just launched my new project!");
        var comment = comment(commentId, post, null, "Congrats, looks great!");

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentRepository.findByParentIdOrderByCreatedAtDesc(eq(commentId), any(Pageable.class)))
                .thenReturn(List.of());
        when(postService.getPost(postId)).thenReturn(PostResponse.from(post, null));
        when(aiChatService.chat(anyString(), anyString())).thenReturn(THREE_LINE_RESPONSE);

        var result = service.generateReplySuggestions(UUID.randomUUID(), commentId, ReplyTone.FRIENDLY);

        assertThat(result).containsExactly(
                "Thanks so much!", "Really appreciate that.", "Glad you liked it!");

        var userPromptCaptor = ArgumentCaptor.forClass(String.class);
        verify(aiChatService).chat(anyString(), userPromptCaptor.capture());
        assertThat(userPromptCaptor.getValue())
                .contains("Just launched my new project!")
                .contains("Congrats, looks great!")
                .doesNotContain("Parent comment");
    }

    @Test
    void generateReplySuggestions_forReplyComment_includesParentCommentInPrompt() {
        var postId = UUID.randomUUID();
        var parentId = UUID.randomUUID();
        var commentId = UUID.randomUUID();
        var post = post(postId, "Anyone tried the new API?");
        var parent = comment(parentId, post, null, "I did, works great.");
        var reply = comment(commentId, post, parent, "How fast was it?");

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(reply));
        when(commentRepository.findByParentIdOrderByCreatedAtDesc(eq(parentId), any(Pageable.class)))
                .thenReturn(List.of());
        when(postService.getPost(postId)).thenReturn(PostResponse.from(post, null));
        when(aiChatService.chat(anyString(), anyString())).thenReturn(THREE_LINE_RESPONSE);

        service.generateReplySuggestions(UUID.randomUUID(), commentId, null);

        var userPromptCaptor = ArgumentCaptor.forClass(String.class);
        verify(aiChatService).chat(anyString(), userPromptCaptor.capture());
        assertThat(userPromptCaptor.getValue())
                .contains("Parent comment")
                .contains("I did, works great.")
                .contains("How fast was it?");
    }

    @Test
    void generateReplySuggestions_whenToneOmitted_defaultsToNeutral() {
        var postId = UUID.randomUUID();
        var commentId = UUID.randomUUID();
        var post = post(postId, "Post content");
        var comment = comment(commentId, post, null, "Comment content");

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentRepository.findByParentIdOrderByCreatedAtDesc(eq(commentId), any(Pageable.class)))
                .thenReturn(List.of());
        when(postService.getPost(postId)).thenReturn(PostResponse.from(post, null));
        when(aiChatService.chat(anyString(), anyString())).thenReturn(THREE_LINE_RESPONSE);

        service.generateReplySuggestions(UUID.randomUUID(), commentId, null);

        var systemPromptCaptor = ArgumentCaptor.forClass(String.class);
        verify(aiChatService).chat(systemPromptCaptor.capture(), anyString());
        assertThat(systemPromptCaptor.getValue()).contains(ReplyTone.NEUTRAL.promptDescriptor());
    }

    @Test
    void generateReplySuggestions_whenAiReturnsUnparsableResponse_throwsCommentSuggestionGenerationException() {
        var postId = UUID.randomUUID();
        var commentId = UUID.randomUUID();
        var post = post(postId, "Post content");
        var comment = comment(commentId, post, null, "Comment content");

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentRepository.findByParentIdOrderByCreatedAtDesc(eq(commentId), any(Pageable.class)))
                .thenReturn(List.of());
        when(postService.getPost(postId)).thenReturn(PostResponse.from(post, null));
        when(aiChatService.chat(anyString(), anyString())).thenReturn("   ");

        assertThatThrownBy(() -> service.generateReplySuggestions(UUID.randomUUID(), commentId, null))
                .isInstanceOf(CommentSuggestionGenerationException.class);
    }

    @Test
    void generateReplySuggestions_whenAiChatServiceThrows_wrapsInCommentSuggestionGenerationException() {
        var postId = UUID.randomUUID();
        var commentId = UUID.randomUUID();
        var post = post(postId, "Post content");
        var comment = comment(commentId, post, null, "Comment content");

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentRepository.findByParentIdOrderByCreatedAtDesc(eq(commentId), any(Pageable.class)))
                .thenReturn(List.of());
        when(postService.getPost(postId)).thenReturn(PostResponse.from(post, null));
        when(aiChatService.chat(anyString(), anyString())).thenThrow(new AiChatException("boom"));

        assertThatThrownBy(() -> service.generateReplySuggestions(UUID.randomUUID(), commentId, null))
                .isInstanceOf(CommentSuggestionGenerationException.class)
                .hasCauseInstanceOf(AiChatException.class);
    }
}
