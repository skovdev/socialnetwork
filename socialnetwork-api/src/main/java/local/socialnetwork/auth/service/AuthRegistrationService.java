package local.socialnetwork.auth.service;

import local.socialnetwork.auth.dto.http.request.VerifyRequest;
import local.socialnetwork.auth.dto.http.request.RegisterRequest;
import local.socialnetwork.auth.dto.http.request.ResendVerificationRequest;

/**
 * Account creation and email verification.
 */
public interface AuthRegistrationService {
    void register(RegisterRequest request);
    void verify(VerifyRequest request);
    void resendVerification(ResendVerificationRequest request);
}
