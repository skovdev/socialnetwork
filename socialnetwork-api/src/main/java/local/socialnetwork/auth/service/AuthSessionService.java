package local.socialnetwork.auth.service;

import local.socialnetwork.auth.dto.http.request.LoginRequest;
import local.socialnetwork.auth.dto.http.request.RefreshRequest;

import local.socialnetwork.auth.dto.http.response.TokenResponse;

import java.util.UUID;

/**
 * Sign-in sessions: login, refresh-token rotation and logout.
 */
public interface AuthSessionService {
    TokenResponse login(LoginRequest request, String clientIp);
    TokenResponse refresh(RefreshRequest request);
    void logout(UUID userId);
}
