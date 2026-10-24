package local.socialnetwork.auth.service.impl;

import local.socialnetwork.auth.repository.AuthRefreshTokenRepository;
import local.socialnetwork.auth.repository.AuthEmailVerificationTokenRepository;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Periodically deletes expired refresh and email-verification tokens, which would otherwise accumulate forever.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExpiredTokenCleanupJob {

    private final AuthRefreshTokenRepository refreshTokenRepository;
    private final AuthEmailVerificationTokenRepository emailTokenRepository;

    @Scheduled(cron = "${socialnetwork.security.token-cleanup.cron:0 15 3 * * *}")
    @Transactional
    public void purgeExpiredTokens() {
        var now = Instant.now();
        var refreshTokens = refreshTokenRepository.deleteExpiredBefore(now);
        var emailTokens = emailTokenRepository.deleteExpiredBefore(now);
        log.info("Expired token cleanup removed {} refresh and {} verification tokens", refreshTokens, emailTokens);
    }
}
