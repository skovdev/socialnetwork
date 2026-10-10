package local.socialnetwork.core.ai.impl;

import local.socialnetwork.core.config.AiProperties;

import local.socialnetwork.shared.exception.TooManyAiRequestsException;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InMemoryAiRequestLimiterTest {

    private static InMemoryAiRequestLimiter limiter(int maxRequests, Duration window) {
        return new InMemoryAiRequestLimiter(new AiProperties(
                new AiProperties.RateLimit(maxRequests, window),
                new AiProperties.Timeout(Duration.ofSeconds(1), Duration.ofSeconds(1))));
    }

    @Test
    void acquire_withinLimit_doesNotThrow() {
        var limiter = limiter(2, Duration.ofMinutes(1));
        var user = UUID.randomUUID();

        assertThatCode(() -> {
            limiter.acquire(user);
            limiter.acquire(user);
        }).doesNotThrowAnyException();
    }

    @Test
    void acquire_overLimit_throwsWithPositiveRetryAfter() {
        var limiter = limiter(1, Duration.ofMinutes(1));
        var user = UUID.randomUUID();
        limiter.acquire(user);

        assertThatThrownBy(() -> limiter.acquire(user))
                .isInstanceOfSatisfying(TooManyAiRequestsException.class,
                        e -> assertThat(e.getRetryAfterSeconds()).isPositive());
    }

    @Test
    void acquire_limitIsPerUser() {
        var limiter = limiter(1, Duration.ofMinutes(1));
        limiter.acquire(UUID.randomUUID());

        assertThatCode(() -> limiter.acquire(UUID.randomUUID())).doesNotThrowAnyException();
    }

    @Test
    void acquire_afterWindowElapses_allowsAgain() throws InterruptedException {
        var limiter = limiter(1, Duration.ofMillis(50));
        var user = UUID.randomUUID();
        limiter.acquire(user);

        Thread.sleep(80);

        assertThatCode(() -> limiter.acquire(user)).doesNotThrowAnyException();
    }
}
