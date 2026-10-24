package local.socialnetwork.auth.service.impl;

import local.socialnetwork.auth.service.LoginRateLimiterService;

import local.socialnetwork.core.config.LoginRateLimitProperties;

import local.socialnetwork.core.ratelimit.RateLimiter;

import local.socialnetwork.shared.exception.TooManyLoginAttemptsException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

/**
 * Login throttle that counts failed attempts per key (callers pass {@code ip|username}, so a third
 * party cannot lock a victim out of their own account from a different address).
 */
@Service
@RequiredArgsConstructor
public class RedisLoginRateLimiterServiceImpl implements LoginRateLimiterService {

    private static final String KEY_PREFIX = "login:";

    private final RateLimiter rateLimiter;
    private final LoginRateLimitProperties properties;

    @Override
    public void checkAllowed(String key) {
        var state = rateLimiter.peek(KEY_PREFIX + key);
        if (state.count() >= properties.maxAttempts()) {
            throw new TooManyLoginAttemptsException(
                    "Too many login attempts. Please try again later.", state.retryAfterSeconds());
        }
    }

    @Override
    public void recordFailedAttempt(String key) {
        rateLimiter.hit(KEY_PREFIX + key, properties.window());
    }

    @Override
    public void reset(String key) {
        rateLimiter.reset(KEY_PREFIX + key);
    }
}
