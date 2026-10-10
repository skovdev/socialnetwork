package local.socialnetwork.core.ai;

import java.util.UUID;

/**
 * Per-user guard that caps how often AI-backed endpoints may be called, since every call costs money.
 */
public interface AiRequestLimiter {

    /**
     * Records one AI request for the user.
     *
     * @param authUserId the requesting user's id
     * @throws local.socialnetwork.shared.exception.TooManyAiRequestsException if the user is over the limit
     */
    void acquire(UUID authUserId);
}
