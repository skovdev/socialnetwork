package local.socialnetwork.comments.service;

import local.socialnetwork.comments.dto.ReplyTone;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for AI-generated comment reply suggestions.
 */
public interface CommentReplySuggestionService {

    /**
     * Generates 3 AI-drafted reply suggestions for the comment identified by {@code commentId}, in
     * a single AI request. Suggestions are grounded only in that comment's own content and its
     * immediate thread context (the post it belongs to, its parent comment if it is itself a
     * reply, and recent other replies in the same thread). Suggestions are not persisted and are
     * never posted automatically.
     *
     * @param tone desired reply tone, or {@code null} to use {@link ReplyTone#NEUTRAL}
     * @return exactly 3 AI-generated reply suggestions
     * @throws local.socialnetwork.shared.exception.CommentNotFoundException             if no comment exists with the given ID
     * @throws local.socialnetwork.shared.exception.CommentSuggestionGenerationException if the AI provider fails to
     *                                                                                   produce suggestions
     */
    List<String> generateReplySuggestions(UUID authUserId, UUID commentId, ReplyTone tone);
}
