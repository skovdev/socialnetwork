package local.socialnetwork.auth.service.impl;

import local.socialnetwork.core.config.LoginRateLimitProperties;

import local.socialnetwork.shared.exception.TooManyLoginAttemptsException;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InMemoryLoginRateLimiterServiceImplTest {

    @Test
    void checkAllowed_whenNoAttemptsRecorded_doesNotThrow() {
        var service = new InMemoryLoginRateLimiterServiceImpl(new LoginRateLimitProperties(3, Duration.ofMinutes(15)));

        assertThatCode(() -> service.checkAllowed("alice")).doesNotThrowAnyException();
    }

    @Test
    void checkAllowed_whenUnderLimit_doesNotThrow() {
        var service = new InMemoryLoginRateLimiterServiceImpl(new LoginRateLimitProperties(3, Duration.ofMinutes(15)));

        service.recordFailedAttempt("alice");
        service.recordFailedAttempt("alice");

        assertThatCode(() -> service.checkAllowed("alice")).doesNotThrowAnyException();
    }

    @Test
    void checkAllowed_whenLimitReached_throwsTooManyLoginAttemptsException() {
        var service = new InMemoryLoginRateLimiterServiceImpl(new LoginRateLimitProperties(3, Duration.ofMinutes(15)));

        service.recordFailedAttempt("alice");
        service.recordFailedAttempt("alice");
        service.recordFailedAttempt("alice");

        assertThatThrownBy(() -> service.checkAllowed("alice"))
                .isInstanceOf(TooManyLoginAttemptsException.class);
    }

    @Test
    void checkAllowed_isScopedPerKey() {
        var service = new InMemoryLoginRateLimiterServiceImpl(new LoginRateLimitProperties(1, Duration.ofMinutes(15)));

        service.recordFailedAttempt("alice");

        assertThatThrownBy(() -> service.checkAllowed("alice")).isInstanceOf(TooManyLoginAttemptsException.class);
        assertThatCode(() -> service.checkAllowed("bob")).doesNotThrowAnyException();
    }

    @Test
    void reset_clearsTrackedAttempts() {
        var service = new InMemoryLoginRateLimiterServiceImpl(new LoginRateLimitProperties(1, Duration.ofMinutes(15)));

        service.recordFailedAttempt("alice");
        service.reset("alice");

        assertThatCode(() -> service.checkAllowed("alice")).doesNotThrowAnyException();
    }

    @Test
    void checkAllowed_afterWindowExpires_resetsAutomatically() throws InterruptedException {
        var service = new InMemoryLoginRateLimiterServiceImpl(new LoginRateLimitProperties(1, Duration.ofMillis(50)));

        service.recordFailedAttempt("alice");
        assertThatThrownBy(() -> service.checkAllowed("alice")).isInstanceOf(TooManyLoginAttemptsException.class);

        Thread.sleep(100);

        assertThatCode(() -> service.checkAllowed("alice")).doesNotThrowAnyException();
    }

    @Test
    void checkAllowed_whenLimitReached_exceptionCarriesPositiveRetryAfter() {
        var service = new InMemoryLoginRateLimiterServiceImpl(new LoginRateLimitProperties(1, Duration.ofMinutes(15)));

        service.recordFailedAttempt("alice");

        assertThatThrownBy(() -> service.checkAllowed("alice"))
                .isInstanceOf(TooManyLoginAttemptsException.class)
                .satisfies(ex -> assertThat(((TooManyLoginAttemptsException) ex).getRetryAfterSeconds()).isPositive());
    }
}
