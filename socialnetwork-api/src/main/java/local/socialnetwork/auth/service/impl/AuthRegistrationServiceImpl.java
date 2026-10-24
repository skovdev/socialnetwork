package local.socialnetwork.auth.service.impl;

import local.socialnetwork.auth.dto.http.request.VerifyRequest;
import local.socialnetwork.auth.dto.http.request.RegisterRequest;
import local.socialnetwork.auth.dto.http.request.ResendVerificationRequest;

import local.socialnetwork.auth.entity.AuthUser;
import local.socialnetwork.auth.entity.AuthStatus;
import local.socialnetwork.auth.entity.AuthUserRole;
import local.socialnetwork.auth.entity.AuthEmailVerificationToken;

import local.socialnetwork.auth.event.VerificationEmailRequested;

import local.socialnetwork.auth.repository.AuthUserRepository;
import local.socialnetwork.auth.repository.AuthEmailVerificationTokenRepository;

import local.socialnetwork.auth.service.TokenService;
import local.socialnetwork.auth.service.AuthRegistrationService;

import local.socialnetwork.profiles.dto.NewProfileDetails;

import local.socialnetwork.profiles.service.UserProfileService;

import local.socialnetwork.shared.exception.TokenExpiredException;
import local.socialnetwork.shared.exception.TokenNotFoundException;
import local.socialnetwork.shared.exception.TokenAlreadyUsedException;
import local.socialnetwork.shared.exception.EmailAlreadyExistsException;
import local.socialnetwork.shared.exception.UsernameAlreadyExistsException;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationEventPublisher;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.transaction.support.TransactionOperations;

import java.time.Instant;
import java.time.Duration;

import java.util.Set;

/**
 * Default {@link AuthRegistrationService}: creates accounts and activates them via emailed tokens.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthRegistrationServiceImpl implements AuthRegistrationService {

    private static final Duration EMAIL_TOKEN_TTL = Duration.ofHours(1);
    private static final String ROLE_USER = "ROLE_USER";

    private final AuthUserRepository authUserRepository;
    private final UserProfileService userProfileService;
    private final AuthEmailVerificationTokenRepository emailTokenRepository;
    private final TokenService tokenService;
    private final ApplicationEventPublisher eventPublisher;
    private final PasswordEncoder passwordEncoder;
    private final UserPrincipalCache userPrincipalCache;
    private final TransactionOperations transactionOperations;

    @Override
    public void register(RegisterRequest request) {
        // BCrypt is CPU-heavy: hash before opening the transaction so no DB connection is held meanwhile.
        var passwordHash = passwordEncoder.encode(request.password());
        transactionOperations.executeWithoutResult(status -> createAccount(request, passwordHash));
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
        // A login attempt before verifying may have cached an inactive principal.
        userProfileService.findUsernameByAuthUserId(emailToken.getAuthUser().getId())
                .ifPresent(username -> userPrincipalCache.evictAfterCommit(username.toLowerCase()));
        log.info("Email verified for user id: {}", emailToken.getAuthUser().getId());
    }

    @Override
    @Transactional
    public void resendVerification(ResendVerificationRequest request) {
        authUserRepository.findByEmail(AuthNormalizer.email(request.email())).ifPresent(authUser -> {
            if (authUser.getAuthStatus() != AuthStatus.ACTIVE) {
                emailTokenRepository.deleteByAuthUser(authUser);
                issueVerificationToken(authUser);
                log.info("Verification email resent for user id: {}", authUser.getId());
            }
        });
    }

    private void createAccount(RegisterRequest request, String passwordHash) {
        validateUsernameNotExists(request.username());
        validateEmailNotExists(request.email());
        var authUser = buildAuthUser(request, passwordHash);
        var authUserRole = buildAuthUserRole(authUser);
        var userProfile = userProfileService.createProfile(authUser, new NewProfileDetails(
                request.firstName(), request.lastName(), AuthNormalizer.username(request.username()),
                request.birthDate(), request.phoneNumber()));
        authUser.setAuthUserRoles(Set.of(authUserRole));
        authUser.setUserProfile(userProfile);
        authUserRepository.save(authUser);
        issueVerificationToken(authUser);
        log.info("User registered: id={}", authUser.getId());
    }

    private void issueVerificationToken(AuthUser authUser) {
        var rawToken = tokenService.generateAuthToken();
        var emailToken = new AuthEmailVerificationToken();
        emailToken.setToken(tokenService.hashToken(rawToken));
        emailToken.setExpiresAt(Instant.now().plus(EMAIL_TOKEN_TTL));
        emailToken.setAuthUser(authUser);
        tokenService.save(emailToken);
        // Delivered after the transaction commits (see VerificationEmailListener).
        eventPublisher.publishEvent(new VerificationEmailRequested(authUser.getEmail(), rawToken));
    }

    private void validateUsernameNotExists(String username) {
        if (authUserRepository.existsByUserProfileUsername(AuthNormalizer.username(username))) {
            throw new UsernameAlreadyExistsException("Username '" + username + "' already exists");
        }
    }

    private void validateEmailNotExists(String email) {
        if (authUserRepository.existsByEmail(AuthNormalizer.email(email))) {
            throw new EmailAlreadyExistsException("Email '" + email + "' already exists");
        }
    }

    private AuthUser buildAuthUser(RegisterRequest request, String passwordHash) {
        var authUser = new AuthUser();
        authUser.setEmail(AuthNormalizer.email(request.email()));
        authUser.setAuthStatus(AuthStatus.PENDING_VERIFICATION);
        authUser.setPasswordHash(passwordHash);
        return authUser;
    }

    private AuthUserRole buildAuthUserRole(AuthUser authUser) {
        var authUserRole = new AuthUserRole();
        authUserRole.setAuthUser(authUser);
        authUserRole.setAuthority(ROLE_USER);
        return authUserRole;
    }
}
