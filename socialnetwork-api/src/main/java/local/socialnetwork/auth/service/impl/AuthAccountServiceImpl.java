package local.socialnetwork.auth.service.impl;

import local.socialnetwork.auth.dto.http.request.DeleteAccountRequest;
import local.socialnetwork.auth.dto.http.request.ChangePasswordRequest;

import local.socialnetwork.auth.repository.AuthUserRepository;
import local.socialnetwork.auth.repository.AuthRefreshTokenRepository;
import local.socialnetwork.auth.repository.AuthEmailVerificationTokenRepository;

import local.socialnetwork.auth.service.AuthAccountService;

import local.socialnetwork.profiles.service.UserProfileService;

import local.socialnetwork.shared.exception.UserNotFoundException;
import local.socialnetwork.shared.exception.InvalidCurrentPasswordException;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Default {@link AuthAccountService}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthAccountServiceImpl implements AuthAccountService {

    private final AuthUserRepository authUserRepository;
    private final UserProfileService userProfileService;
    private final AuthEmailVerificationTokenRepository emailTokenRepository;
    private final AuthRefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserPrincipalCache userPrincipalCache;

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
        userProfileService.purgeAvatar(userId);
        userProfileService.findUsernameByAuthUserId(userId)
                .ifPresent(username -> userPrincipalCache.evictAfterCommit(username.toLowerCase()));
        authUserRepository.delete(authUser);
        log.info("Account deleted for user id: {}", userId);
    }
}
