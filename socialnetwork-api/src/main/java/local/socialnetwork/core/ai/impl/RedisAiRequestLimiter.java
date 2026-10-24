package local.socialnetwork.core.ai.impl;

import local.socialnetwork.core.ai.AiRequestLimiter;

import local.socialnetwork.core.config.AiProperties;

import local.socialnetwork.core.ratelimit.RateLimiter;

import local.socialnetwork.shared.exception.TooManyAiRequestsException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Caps AI-backed requests per user within a fixed window.
 */
@Component
@RequiredArgsConstructor
public class RedisAiRequestLimiter implements AiRequestLimiter {

    private final RateLimiter rateLimiter;
    private final AiProperties properties;

    @Override
    public void acquire(UUID authUserId) {
        var rateLimit = properties.rateLimit();
        var result = rateLimiter.hit("ai:" + authUserId, rateLimit.window());
        if (result.count() > rateLimit.maxRequests()) {
            throw new TooManyAiRequestsException(
                    "Too many AI requests. Please try again later.", result.retryAfterSeconds());
        }
    }
}
