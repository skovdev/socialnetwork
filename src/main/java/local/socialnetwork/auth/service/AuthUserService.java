package local.socialnetwork.auth.service;

import local.socialnetwork.auth.dto.http.request.LoginRequest;
import local.socialnetwork.auth.dto.http.request.VerifyRequest;
import local.socialnetwork.auth.dto.http.request.RefreshRequest;
import local.socialnetwork.auth.dto.http.response.TokenResponse;
import local.socialnetwork.auth.dto.http.request.RegisterRequest;

import java.util.UUID;

public interface AuthUserService {
    void register(RegisterRequest request);
    void verify(VerifyRequest request);
    TokenResponse login(LoginRequest request);
    TokenResponse refresh(RefreshRequest request);
    void logout(UUID userId);
}
