package local.socialnetwork.auth.service;

/**
 * Service for sending account-related emails.
 */
public interface EmailService {
    void sendVerificationEmail(String email, String token);
}
