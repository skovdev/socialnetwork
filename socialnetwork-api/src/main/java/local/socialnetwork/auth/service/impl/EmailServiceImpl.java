package local.socialnetwork.auth.service.impl;

import local.socialnetwork.auth.service.EmailService;

import local.socialnetwork.core.config.EmailProperties;

import local.socialnetwork.shared.exception.EmailDeliveryException;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;

import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.sesv2.SesV2Client;

import software.amazon.awssdk.services.sesv2.model.Body;
import software.amazon.awssdk.services.sesv2.model.Message;
import software.amazon.awssdk.services.sesv2.model.Content;
import software.amazon.awssdk.services.sesv2.model.Destination;
import software.amazon.awssdk.services.sesv2.model.EmailContent;
import software.amazon.awssdk.services.sesv2.model.SesV2Exception;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final SesV2Client sesV2Client;
    private final EmailProperties emailProperties;

    @Override
    @Retryable(
            retryFor = SesV2Exception.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2.0, maxDelay = 10000)
    )
    public void sendVerificationEmail(String email, String token) {
        var link = emailProperties.verifyBaseUrl() + token;
        var message = buildMessage(link);
        var sendEmailRequest = buildEmailRequest(email, message);
        sesV2Client.sendEmail(sendEmailRequest);
        log.info("Verification email sent successfully");
    }

    @Recover
    public void recoverSendVerificationEmail(SesV2Exception ex, String email, String token) {
        log.error("Failed to send verification email after all retries: {}", ex.getMessage(), ex);
        throw new EmailDeliveryException("Failed to send verification email after all retries", ex);
    }

    private Message buildMessage(String link) {
        return Message.builder()
                .subject(Content.builder().data("Verify your email").build())
                .body(Body.builder()
                        .text(Content.builder()
                                .data("Click the link to verify your email:" + link)
                                .build())
                        .build())
                .build();
    }

    private SendEmailRequest buildEmailRequest(String email, Message message) {
        return SendEmailRequest.builder()
                .fromEmailAddress(emailProperties.from())
                .destination(Destination.builder().toAddresses(email).build())
                .content(EmailContent.builder().simple(message).build()).build();
    }
}
