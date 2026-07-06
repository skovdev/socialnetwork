package local.socialnetwork.auth.service;

import local.socialnetwork.auth.dto.http.request.LoginRequest;
import local.socialnetwork.auth.dto.http.request.VerifyRequest;
import local.socialnetwork.auth.dto.http.request.RefreshRequest;
import local.socialnetwork.auth.dto.http.request.RegisterRequest;
import local.socialnetwork.auth.dto.http.request.DeleteAccountRequest;
import local.socialnetwork.auth.dto.http.request.ChangePasswordRequest;
import local.socialnetwork.auth.dto.http.request.ResendVerificationRequest;

import local.socialnetwork.auth.dto.http.response.TokenResponse;

import java.util.UUID;

public interface AuthUserService {

    void register(RegisterRequest request);
    void verify(VerifyRequest request);
    void resendVerification(ResendVerificationRequest request);
    TokenResponse login(LoginRequest request);
    TokenResponse refresh(RefreshRequest request);
    void logout(UUID userId);
    void changePassword(UUID userId, ChangePasswordRequest request);
    void deleteAccount(UUID userId, DeleteAccountRequest request);
}
