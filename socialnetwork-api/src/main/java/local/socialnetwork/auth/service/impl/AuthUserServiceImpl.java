package local.socialnetwork.auth.service.impl;

import local.socialnetwork.auth.dto.http.request.LoginRequest;
import local.socialnetwork.auth.dto.http.request.VerifyRequest;
import local.socialnetwork.auth.dto.http.request.RefreshRequest;
import local.socialnetwork.auth.dto.http.request.RegisterRequest;
import local.socialnetwork.auth.dto.http.request.DeleteAccountRequest;
import local.socialnetwork.auth.dto.http.request.ChangePasswordRequest;
import local.socialnetwork.auth.dto.http.request.ResendVerificationRequest;

import local.socialnetwork.auth.dto.http.response.TokenResponse;

import local.socialnetwork.auth.entity.AuthUser;
import local.socialnetwork.auth.entity.AuthStatus;
import local.socialnetwork.auth.entity.AuthUserRole;
import local.socialnetwork.auth.entity.AuthRefreshToken;
import local.socialnetwork.auth.entity.AuthEmailVerificationToken;

import local.socialnetwork.auth.repository.AuthUserRepository;
import local.socialnetwork.auth.repository.AuthRefreshTokenRepository;
import local.socialnetwork.auth.repository.AuthEmailVerificationTokenRepository;

import local.socialnetwork.auth.service.EmailService;
import local.socialnetwork.auth.service.TokenService;
import local.socialnetwork.auth.service.AuthUserService;
import local.socialnetwork.auth.service.LoginRateLimiterService;

import local.socialnetwork.core.config.jwt.JwtTokenProvider;

import local.socialnetwork.profiles.entity.UserProfile;

import local.socialnetwork.profiles.repository.UserProfileRepository;

import local.socialnetwork.shared.exception.UserNotFoundException;
import local.socialnetwork.shared.exception.TokenExpiredException;
import local.socialnetwork.shared.exception.TokenNotFoundException;
import local.socialnetwork.shared.exception.TokenAlreadyUsedException;
import local.socialnetwork.shared.exception.EmailAlreadyExistsException;
import local.socialnetwork.shared.exception.AccountNotVerifiedException;
import local.socialnetwork.shared.exception.UsernameAlreadyExistsException;
import local.socialnetwork.shared.exception.InvalidCurrentPasswordException;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.Duration;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthUserServiceImpl implements AuthUserService {

    private static final Duration EMAIL_TOKEN_TTL = Duration.ofHours(1);
    private static final Duration REFRESH_TOKEN_TTL = Duration.ofDays(7);
    private static final long ACCESS_TOKEN_EXPIRES_IN = 3600L;
    private static final String ROLE_USER = "ROLE_USER";

    private final AuthUserRepository authUserRepository;
    private final UserProfileRepository userProfileRepository;
    private final AuthEmailVerificationTokenRepository emailTokenRepository;
    private final AuthRefreshTokenRepository refreshTokenRepository;
    private final LoginRateLimiterService loginRateLimiterService;
    private final TokenService tokenService;
    private final EmailService emailService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void register(RegisterRequest request) {
        validateUsernameNotExists(request.username());
        validateEmailNotExists(request.email());
        var authUser = buildAuthUser(request);
        var authUserRole = buildAuthUserRole(authUser);
        var userProfile = buildUserProfile(request, authUser);
        authUser.setAuthUserRoles(Set.of(authUserRole));
        authUser.setUserProfile(userProfile);
        authUserRepository.save(authUser);
        issueAndSendVerificationToken(authUser);
        log.info("User registered: id={}", authUser.getId());
    }

    @Override
    @Transactional
    public void verify(VerifyRequest request) {
        var hashedToken = tokenService.hashToken(request.token());
        var emailToken = emailTokenRepository.findByToken(hashedToken)
                .orElseThrow(() -> new TokenNotFoundException("Verification token not found"));

        if (emailToken.getExpiresAt().isBefore(Instant.now())) {
            throw new TokenExpiredException("Verification token has expired");
        }
        if (emailToken.getUsedAt() != null) {
            throw new TokenAlreadyUsedException("Verification token has already been used");
        }

        emailToken.setUsedAt(Instant.now());
        emailToken.getAuthUser().setAuthStatus(AuthStatus.ACTIVE);
        log.info("Email verified for user id: {}", emailToken.getAuthUser().getId());
    }

    @Override
    @Transactional
    public TokenResponse login(LoginRequest request) {
        var rateLimitKey = normalizeUsername(request.username());
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
        var accessToken = createAccessToken(authentication.getName());
        var refreshToken = createAndPersistRefreshToken(authUser);
        log.info("User logged in: id={}", authUser.getId());
        return new TokenResponse(accessToken, refreshToken.toString(), ACCESS_TOKEN_EXPIRES_IN);
    }

    @Override
    @Transactional
    public TokenResponse refresh(RefreshRequest request) {
        var jti = parseJti(request.refreshToken());
        var storedToken = refreshTokenRepository.findByJti(jti)
                .orElseThrow(() -> new TokenNotFoundException("Refresh token not found"));

        if (storedToken.getExpiresAt().isBefore(Instant.now())) {
            refreshTokenRepository.delete(storedToken);
            throw new TokenExpiredException("Refresh token has expired");
        }

        var authUser = storedToken.getUser();
        refreshTokenRepository.delete(storedToken);

        var username = userProfileRepository.findByAuthUserId(authUser.getId())
                .map(UserProfile::getUsername)
                .orElseThrow(() -> new UserNotFoundException("Profile not found for user id: " + authUser.getId()));
        var accessToken = createAccessToken(username);
        var newRefreshToken = createAndPersistRefreshToken(authUser);
        log.info("Tokens refreshed for user id: {}", authUser.getId());
        return new TokenResponse(accessToken, newRefreshToken.toString(), ACCESS_TOKEN_EXPIRES_IN);
    }

    @Override
    @Transactional
    public void logout(UUID userId) {
        refreshTokenRepository.deleteByUserId(userId);
        log.info("All refresh tokens deleted for user id: {}", userId);
    }

    @Override
    @Transactional
    public void resendVerification(ResendVerificationRequest request) {
        authUserRepository.findByEmail(normalizeEmail(request.email())).ifPresent(authUser -> {
            if (authUser.getAuthStatus() != AuthStatus.ACTIVE) {
                emailTokenRepository.deleteByAuthUser(authUser);
                issueAndSendVerificationToken(authUser);
                log.info("Verification email resent for user id: {}", authUser.getId());
            }
        });
    }

    @Override
    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequest request) {
        var authUser = authUserRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
        if (!passwordEncoder.matches(request.currentPassword(), authUser.getPasswordHash())) {
            throw new InvalidCurrentPasswordException("Current password is incorrect");
        }
        authUser.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        refreshTokenRepository.deleteByUserId(userId);
        log.info("Password changed for user id: {}", userId);
    }

    @Override
    @Transactional
    public void deleteAccount(UUID userId, DeleteAccountRequest request) {
        var authUser = authUserRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
        if (!passwordEncoder.matches(request.password(), authUser.getPasswordHash())) {
            throw new InvalidCurrentPasswordException("Password is incorrect");
        }
        refreshTokenRepository.deleteByUserId(userId);
        emailTokenRepository.deleteByAuthUser(authUser);
        authUserRepository.delete(authUser);
        log.info("Account deleted for user id: {}", userId);
    }

    private void issueAndSendVerificationToken(AuthUser authUser) {
        var rawToken = tokenService.generateAuthToken();
        var hashedToken = tokenService.hashToken(rawToken);
        var emailToken = new AuthEmailVerificationToken();
        emailToken.setToken(hashedToken);
        emailToken.setExpiresAt(Instant.now().plus(EMAIL_TOKEN_TTL));
        emailToken.setAuthUser(authUser);
        tokenService.save(emailToken);
        emailService.sendVerificationEmail(authUser.getEmail(), rawToken);
    }

    private Authentication authenticate(String username, String password) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
    }

    private AuthUser resolveAuthUser(String username) {
        return userProfileRepository.findByUsername(normalizeUsername(username))
                .map(UserProfile::getAuthUser)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    private String createAccessToken(String username) {
        return jwtTokenProvider.createToken(Map.of("username", username));
    }

    private UUID createAndPersistRefreshToken(AuthUser authUser) {
        var jti = UUID.randomUUID();
        var now = Instant.now();
        var refreshToken = new AuthRefreshToken();
        refreshToken.setJti(jti);
        refreshToken.setUser(authUser);
        refreshToken.setIssuedAt(now);
        refreshToken.setExpiresAt(now.plus(REFRESH_TOKEN_TTL));
        refreshTokenRepository.save(refreshToken);
        return jti;
    }

    private UUID parseJti(String refreshToken) {
        try {
            return UUID.fromString(refreshToken);
        } catch (IllegalArgumentException e) {
            throw new TokenNotFoundException("Invalid refresh token format");
        }
    }

    private void validateUsernameNotExists(String username) {
        if (userProfileRepository.existsByUsername(normalizeUsername(username))) {
            throw new UsernameAlreadyExistsException("Username '" + username + "' already exists");
        }
    }

    private void validateEmailNotExists(String email) {
        if (authUserRepository.existsByEmail(normalizeEmail(email))) {
            throw new EmailAlreadyExistsException("Email '" + email + "' already exists");
        }
    }

    private AuthUser buildAuthUser(RegisterRequest request) {
        var authUser = new AuthUser();
        authUser.setEmail(normalizeEmail(request.email()));
        authUser.setAuthStatus(AuthStatus.PENDING_VERIFICATION);
        authUser.setPasswordHash(passwordEncoder.encode(request.password()));
        return authUser;
    }

    private AuthUserRole buildAuthUserRole(AuthUser authUser) {
        var authUserRole = new AuthUserRole();
        authUserRole.setAuthUser(authUser);
        authUserRole.setAuthority(ROLE_USER);
        return authUserRole;
    }

    private UserProfile buildUserProfile(RegisterRequest request, AuthUser authUser) {
        var userProfile = new UserProfile();
        userProfile.setFirstName(request.firstName());
        userProfile.setLastName(request.lastName());
        userProfile.setDisplayName(request.firstName() + " " + request.lastName());
        userProfile.setUsername(normalizeUsername(request.username()));
        userProfile.setBirthDate(request.birthDate());
        userProfile.setPhoneNumber(request.phoneNumber());
        userProfile.setAuthUser(authUser);
        return userProfile;
    }

    private static String normalizeEmail(String email) {
        return email == null ? null : email.toLowerCase();
    }

    private static String normalizeUsername(String username) {
        return username == null ? null : username.toLowerCase();
    }

}
