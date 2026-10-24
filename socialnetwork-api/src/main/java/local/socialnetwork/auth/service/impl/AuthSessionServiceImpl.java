package local.socialnetwork.auth.service.impl;

import local.socialnetwork.auth.dto.http.request.LoginRequest;
import local.socialnetwork.auth.dto.http.request.RefreshRequest;

import local.socialnetwork.auth.dto.http.response.TokenResponse;

import local.socialnetwork.auth.entity.AuthUser;
import local.socialnetwork.auth.entity.AuthStatus;
import local.socialnetwork.auth.entity.AuthRefreshToken;

import local.socialnetwork.auth.repository.AuthUserRepository;
import local.socialnetwork.auth.repository.AuthRefreshTokenRepository;

import local.socialnetwork.auth.service.TokenService;
import local.socialnetwork.auth.service.AuthSessionService;
import local.socialnetwork.auth.service.LoginRateLimiterService;

import local.socialnetwork.core.config.jwt.JwtTokenProvider;

import local.socialnetwork.profiles.service.UserProfileService;

import local.socialnetwork.shared.exception.UserNotFoundException;
import local.socialnetwork.shared.exception.TokenExpiredException;
import local.socialnetwork.shared.exception.TokenNotFoundException;
import local.socialnetwork.shared.exception.AccountNotVerifiedException;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.Duration;

import java.util.UUID;

/**
 * Default {@link AuthSessionService}: short-lived JWT access tokens plus rotating, single-use refresh tokens
 * that are stored only as hashes.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthSessionServiceImpl implements AuthSessionService {

    private static final Duration REFRESH_TOKEN_TTL = Duration.ofDays(7);
    private static final long ACCESS_TOKEN_EXPIRES_IN = 3600L;

    private final AuthUserRepository authUserRepository;
    private final UserProfileService userProfileService;
    private final AuthRefreshTokenRepository refreshTokenRepository;
    private final LoginRateLimiterService loginRateLimiterService;
    private final TokenService tokenService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Override
    public TokenResponse login(LoginRequest request, String clientIp) {
        // Not transactional on purpose: BCrypt is CPU-heavy and must not pin a DB connection.
        // Keyed by IP + username so a third party cannot lock a victim out from another address.
        var rateLimitKey = clientIp + "|" + AuthNormalizer.username(request.username());
        loginRateLimiterService.checkAllowed(rateLimitKey);

        Authentication authentication;
        try {
            authentication = authenticate(request.username(), request.password());
        } catch (AuthenticationException e) {
            loginRateLimiterService.recordFailedAttempt(rateLimitKey);
            throw e;
        }
        var authUser = resolveAuthUser(request.username());

        if (authUser.getAuthStatus() != AuthStatus.ACTIVE) {
            throw new AccountNotVerifiedException("Account is not verified. Please check your email.");
        }

        loginRateLimiterService.reset(rateLimitKey);
        var accessToken = jwtTokenProvider.createToken(authUser.getId(), authentication.getName());
        var refreshToken = createAndPersistRefreshToken(authUser);
        log.info("User logged in: id={}", authUser.getId());
        return new TokenResponse(accessToken, refreshToken, ACCESS_TOKEN_EXPIRES_IN);
    }

    @Override
    @Transactional
    public TokenResponse refresh(RefreshRequest request) {
        var tokenHash = tokenService.hashToken(request.refreshToken());
        var storedToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new TokenNotFoundException("Refresh token not found"));

        if (storedToken.getExpiresAt().isBefore(Instant.now())) {
            refreshTokenRepository.deleteByTokenHash(tokenHash);
            throw new TokenExpiredException("Refresh token has expired");
        }

        var authUser = storedToken.getUser();
        if (refreshTokenRepository.deleteByTokenHash(tokenHash) == 0) {
            throw new TokenNotFoundException("Refresh token was already used");
        }

        var username = userProfileService.findUsernameByAuthUserId(authUser.getId())
                .orElseThrow(() -> new UserNotFoundException("Profile not found for user id: " + authUser.getId()));
        var accessToken = jwtTokenProvider.createToken(authUser.getId(), username);
        var newRefreshToken = createAndPersistRefreshToken(authUser);
        log.info("Tokens refreshed for user id: {}", authUser.getId());
        return new TokenResponse(accessToken, newRefreshToken, ACCESS_TOKEN_EXPIRES_IN);
    }

    @Override
    @Transactional
    public void logout(UUID userId) {
        refreshTokenRepository.deleteByUserId(userId);
        log.info("All refresh tokens deleted for user id: {}", userId);
    }

    private Authentication authenticate(String username, String password) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
    }

    private AuthUser resolveAuthUser(String username) {
        return authUserRepository.findByUserProfileUsername(AuthNormalizer.username(username))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    private String createAndPersistRefreshToken(AuthUser authUser) {
        var rawToken = tokenService.generateAuthToken();
        var now = Instant.now();
        var refreshToken = new AuthRefreshToken();
        refreshToken.setTokenHash(tokenService.hashToken(rawToken));
        refreshToken.setUser(authUser);
        refreshToken.setIssuedAt(now);
        refreshToken.setExpiresAt(now.plus(REFRESH_TOKEN_TTL));
        refreshTokenRepository.save(refreshToken);
        return rawToken;
    }
}
