package local.socialnetwork.core.ratelimit;

import local.socialnetwork.BaseIntegrationTest;

import local.socialnetwork.auth.service.impl.RedisLoginRateLimiterServiceImpl;

import local.socialnetwork.core.ai.impl.RedisAiRequestLimiter;

import local.socialnetwork.core.config.AiProperties;

import local.socialnetwork.shared.exception.TooManyAiRequestsException;
import local.socialnetwork.shared.exception.TooManyLoginAttemptsException;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RedisRateLimiterIT extends BaseIntegrationTest {

    @Autowired
    private RateLimiter rateLimiter;

    @Autowired
    private RedisLoginRateLimiterServiceImpl loginLimiter;

    @Autowired
    private RedisAiRequestLimiter aiLimiter;

    @Autowired
    private AiProperties aiProperties;

    @Test
    void hit_countsWithinWindowAndReportsRetryAfter() {
        var key = "it:" + UUID.randomUUID();

        var first = rateLimiter.hit(key, Duration.ofMinutes(1));
        var second = rateLimiter.hit(key, Duration.ofMinutes(1));

        assertThat(first.count()).isEqualTo(1);
        assertThat(second.count()).isEqualTo(2);
        assertThat(second.retryAfterSeconds()).isBetween(1L, 60L);
    }

    @Test
    void peek_doesNotIncrement_andResetClears() {
        var key = "it:" + UUID.randomUUID();
        rateLimiter.hit(key, Duration.ofMinutes(1));

        assertThat(rateLimiter.peek(key).count()).isEqualTo(1);
        assertThat(rateLimiter.peek(key).count()).isEqualTo(1);

        rateLimiter.reset(key);

        assertThat(rateLimiter.peek(key)).isEqualTo(RateLimiter.Result.EMPTY);
    }

    @Test
    void hit_afterWindowExpires_startsFreshWindow() throws InterruptedException {
        var key = "it:" + UUID.randomUUID();
        rateLimiter.hit(key, Duration.ofSeconds(1));

        Thread.sleep(1200);

        assertThat(rateLimiter.hit(key, Duration.ofSeconds(1)).count()).isEqualTo(1);
    }

    @Test
    void loginLimiter_blocksAfterMaxFailures_isScopedPerKey_andResets() {
        var key = "10.0.0.1|" + UUID.randomUUID();
        for (int i = 0; i < 5; i++) {
            loginLimiter.recordFailedAttempt(key);
        }

        assertThatThrownBy(() -> loginLimiter.checkAllowed(key))
                .isInstanceOfSatisfying(TooManyLoginAttemptsException.class,
                        e -> assertThat(e.getRetryAfterSeconds()).isPositive());
        assertThatCode(() -> loginLimiter.checkAllowed("10.0.0.2|" + UUID.randomUUID())).doesNotThrowAnyException();

        loginLimiter.reset(key);

        assertThatCode(() -> loginLimiter.checkAllowed(key)).doesNotThrowAnyException();
    }

    @Test
    void aiLimiter_allowsUpToMaxThenRejects() {
        var user = UUID.randomUUID();
        var max = aiProperties.rateLimit().maxRequests();
        for (int i = 0; i < max; i++) {
            aiLimiter.acquire(user);
        }

        assertThatThrownBy(() -> aiLimiter.acquire(user))
                .isInstanceOfSatisfying(TooManyAiRequestsException.class,
                        e -> assertThat(e.getRetryAfterSeconds()).isPositive());
        assertThatCode(() -> aiLimiter.acquire(UUID.randomUUID())).doesNotThrowAnyException();
    }
}
