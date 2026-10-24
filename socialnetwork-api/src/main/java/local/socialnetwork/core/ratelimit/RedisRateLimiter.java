package local.socialnetwork.core.ratelimit;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.StringRedisTemplate;

import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * {@link RateLimiter} backed by Redis {@code INCR} + {@code EXPIRE}. It fails open: if Redis is
 * unreachable the request is allowed and the problem is logged, because throttling is a safeguard
 * and must not take the API down with it.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisRateLimiter implements RateLimiter {

    private static final String KEY_PREFIX = "ratelimit:";

    private final StringRedisTemplate redis;

    @Override
    public Result hit(String key, Duration window) {
        var redisKey = KEY_PREFIX + key;
        try {
            var count = redis.opsForValue().increment(redisKey);
            if (count == null) {
                return Result.EMPTY;
            }
            var ttl = redis.getExpire(redisKey);
            if (count == 1 || ttl == null || ttl < 0) {
                redis.expire(redisKey, window);
                ttl = window.toSeconds();
            }
            return new Result(count, Math.max(ttl, 1));
        } catch (RuntimeException e) {
            log.warn("Rate limiter unavailable, allowing request for key '{}': {}", key, e.getMessage());
            return Result.EMPTY;
        }
    }

    @Override
    public Result peek(String key) {
        var redisKey = KEY_PREFIX + key;
        try {
            var value = redis.opsForValue().get(redisKey);
            if (value == null) {
                return Result.EMPTY;
            }
            var ttl = redis.getExpire(redisKey);
            return new Result(Long.parseLong(value), Math.max(ttl == null ? 1 : ttl, 1));
        } catch (RuntimeException e) {
            log.warn("Rate limiter unavailable, allowing request for key '{}': {}", key, e.getMessage());
            return Result.EMPTY;
        }
    }

    @Override
    public void reset(String key) {
        try {
            redis.delete(KEY_PREFIX + key);
        } catch (RuntimeException e) {
            log.warn("Rate limiter unavailable, could not reset key '{}': {}", key, e.getMessage());
        }
    }
}
