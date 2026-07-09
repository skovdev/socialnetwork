package local.socialnetwork.auth.service.impl;

import local.socialnetwork.auth.service.LoginRateLimiterService;

import local.socialnetwork.core.config.LoginRateLimitProperties;

import local.socialnetwork.shared.exception.TooManyLoginAttemptsException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.Duration;

import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory, single-instance implementation of {@link LoginRateLimiterService}.
 *
 * <p>State is kept in a {@link ConcurrentHashMap} and pruned opportunistically, so no
 * background scheduler is required. This is sufficient for a single application instance;
 * a multi-instance deployment would need a shared store (e.g. Redis) to enforce the limit
 * consistently across nodes.
 */
@Service
@RequiredArgsConstructor
public class InMemoryLoginRateLimiterServiceImpl implements LoginRateLimiterService {

    private static final int PRUNE_THRESHOLD = 10_000;

    private final LoginRateLimitProperties properties;

    private final ConcurrentHashMap<String, AttemptState> attemptsByKey = new ConcurrentHashMap<>();

    @Override
    public void checkAllowed(String key) {
        var state = attemptsByKey.get(key);
        if (state == null || isExpired(state)) {
            return;
        }
        if (state.count() >= properties.maxAttempts()) {
            var retryAfterSeconds = Duration.between(Instant.now(), windowEnd(state)).getSeconds();
            throw new TooManyLoginAttemptsException(
                    "Too many login attempts. Please try again later.", Math.max(retryAfterSeconds, 1));
        }
    }

    @Override
    public void recordFailedAttempt(String key) {
        var now = Instant.now();
        attemptsByKey.compute(key, (k, state) -> (state == null || isExpired(state))
                ? new AttemptState(1, now)
                : new AttemptState(state.count() + 1, state.windowStart()));
        pruneIfNeeded();
    }

    @Override
    public void reset(String key) {
        attemptsByKey.remove(key);
    }

    private boolean isExpired(AttemptState state) {
        return Instant.now().isAfter(windowEnd(state));
    }

    private Instant windowEnd(AttemptState state) {
        return state.windowStart().plus(properties.window());
    }

    private void pruneIfNeeded() {
        if (attemptsByKey.size() > PRUNE_THRESHOLD) {
            attemptsByKey.entrySet().removeIf(entry -> isExpired(entry.getValue()));
        }
    }

    private record AttemptState(int count, Instant windowStart) {
    }
}
