package local.socialnetwork.profiles.dto;

import local.socialnetwork.shared.dto.response.AuthorSummary;

import java.util.UUID;

/**
 * A profile's owning auth-user ID together with its public {@link AuthorSummary},
 * for callers that need to look up content by the same user.
 */
public record AuthorProfile(UUID authUserId, AuthorSummary summary) {
}
