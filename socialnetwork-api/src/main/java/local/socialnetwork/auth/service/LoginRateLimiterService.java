package local.socialnetwork.auth.service;

import local.socialnetwork.shared.exception.TooManyLoginAttemptsException;

/**
 * Brute-force protection for the login endpoint. Tracks failed login attempts per key
 * (typically the normalized username) within a sliding window.
 */
public interface LoginRateLimiterService {

    /**
     * Verifies that {@code key} has not exceeded the allowed number of failed attempts.
     *
     * @throws TooManyLoginAttemptsException if the limit has been reached
     */
    void checkAllowed(String key);

    /**
     * Records a failed login attempt for {@code key}.
     */
    void recordFailedAttempt(String key);

    /**
     * Clears any tracked attempts for {@code key}, called after a successful login.
     */
    void reset(String key);
}
