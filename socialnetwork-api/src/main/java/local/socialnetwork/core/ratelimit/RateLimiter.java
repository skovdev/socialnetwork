package local.socialnetwork.core.ratelimit;

import java.time.Duration;

/**
 * Fixed-window counter shared by every feature that throttles callers (login failures, AI usage,
 * unauthenticated endpoints). Counters live in Redis so limits survive restarts and apply across instances.
 */
public interface RateLimiter {

    /**
     * Increments the counter for {@code key}, starting a new window of the given length if none is open.
     */
    Result hit(String key, Duration window);

    /**
     * Reads the counter for {@code key} without incrementing it.
     */
    Result peek(String key);

    /**
     * Clears the counter for {@code key}.
     */
    void reset(String key);

    /**
     * @param count             hits recorded in the current window
     * @param retryAfterSeconds seconds until the window closes (at least 1 when a window is open)
     */
    record Result(long count, long retryAfterSeconds) {

        public static final Result EMPTY = new Result(0, 0);
    }
}
