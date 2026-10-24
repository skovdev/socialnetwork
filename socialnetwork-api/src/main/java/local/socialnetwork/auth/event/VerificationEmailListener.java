package local.socialnetwork.auth.event;

import local.socialnetwork.auth.service.EmailService;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Async;

import org.springframework.stereotype.Component;

import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Sends verification emails asynchronously once the issuing transaction has committed.
 * A delivery failure is logged; the user can request a new email via resend-verification.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class VerificationEmailListener {

    private final EmailService emailService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void on(VerificationEmailRequested event) {
        try {
            emailService.sendVerificationEmail(event.email(), event.rawToken());
        } catch (RuntimeException e) {
            log.error("Verification email could not be delivered: {}", e.getMessage(), e);
        }
    }
}
