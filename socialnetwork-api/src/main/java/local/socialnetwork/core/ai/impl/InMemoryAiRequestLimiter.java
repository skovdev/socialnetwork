package local.socialnetwork.core.ai.impl;

import local.socialnetwork.core.ai.AiRequestLimiter;

import local.socialnetwork.core.config.AiProperties;

import local.socialnetwork.shared.exception.TooManyAiRequestsException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.Duration;

import java.util.UUID;

import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory, single-instance fixed-window implementation of {@link AiRequestLimiter}. Expired windows
 * are pruned opportunistically; a multi-instance deployment would need a shared store instead.
 */
@Component
@RequiredArgsConstructor
public class InMemoryAiRequestLimiter implements AiRequestLimiter {

    private static final int PRUNE_THRESHOLD = 10_000;

    private final AiProperties properties;

    private final ConcurrentHashMap<UUID, Window> windowsByUser = new ConcurrentHashMap<>();

    @Override
    public void acquire(UUID authUserId) {
        var now = Instant.now();
        var window = windowsByUser.compute(authUserId, (id, current) ->
                current == null || !now.isBefore(current.start().plus(properties.rateLimit().window()))
                        ? new Window(now, 1)
                        : new Window(current.start(), current.count() + 1));
        if (window.count() > properties.rateLimit().maxRequests()) {
            var retryAfter = Duration.between(now, window.start().plus(properties.rateLimit().window())).getSeconds();
            throw new TooManyAiRequestsException(
                    "Too many AI requests. Please try again later.", Math.max(retryAfter, 1));
        }
        if (windowsByUser.size() > PRUNE_THRESHOLD) {
            windowsByUser.entrySet().removeIf(entry ->
                    !now.isBefore(entry.getValue().start().plus(properties.rateLimit().window())));
        }
    }

    private record Window(Instant start, int count) {
    }
}
