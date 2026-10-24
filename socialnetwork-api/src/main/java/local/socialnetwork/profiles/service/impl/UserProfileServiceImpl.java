package local.socialnetwork.profiles.service.impl;

import local.socialnetwork.auth.entity.AuthUser;

import local.socialnetwork.profiles.dto.AuthorProfile;
import local.socialnetwork.profiles.dto.NewProfileDetails;

import local.socialnetwork.profiles.dto.http.request.UpdateProfileRequestDto;

import local.socialnetwork.profiles.entity.UserProfile;

import local.socialnetwork.profiles.repository.UserProfileRepository;

import local.socialnetwork.profiles.service.UserProfileService;
import local.socialnetwork.profiles.service.AvatarStorageService;

import local.socialnetwork.shared.dto.response.AuthorSummary;

import local.socialnetwork.shared.exception.UserNotFoundException;
import local.socialnetwork.shared.exception.AvatarNotFoundException;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;
import java.util.Optional;
import java.util.Collection;

import java.util.stream.Collectors;

/**
 * Default implementation of {@link UserProfileService}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final AvatarStorageService avatarStorageService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void save(UserProfile userProfile) {
        userProfileRepository.save(userProfile);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<UserProfile> findByUsername(String username) {
        return userProfileRepository.findByUsername(username);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<UserProfile> findByAuthUserId(UUID authUserId) {
        return userProfileRepository.findByAuthUserId(authUserId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserProfile createProfile(AuthUser authUser, NewProfileDetails details) {
        var profile = new UserProfile();
        profile.setAuthUser(authUser);
        profile.setFirstName(details.firstName());
        profile.setLastName(details.lastName());
        profile.setDisplayName(details.firstName() + " " + details.lastName());
        profile.setUsername(details.username());
        profile.setBirthDate(details.birthDate());
        profile.setPhoneNumber(details.phoneNumber());
        return profile;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userProfileRepository.existsByUsername(username);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<String> findUsernameByAuthUserId(UUID authUserId) {
        return userProfileRepository.findByAuthUserId(authUserId).map(UserProfile::getUsername);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public AuthorSummary getAuthorSummary(UUID authUserId) {
        var profile = userProfileRepository.findByAuthUserId(authUserId)
                .orElseThrow(() -> new UserNotFoundException("Profile not found for user id: " + authUserId));
        return toAuthorSummary(profile);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Map<UUID, AuthorSummary> getAuthorSummaries(Collection<UUID> authUserIds) {
        return userProfileRepository.findByAuthUserIdIn(authUserIds).stream()
                .collect(Collectors.toMap(profile -> profile.getAuthUser().getId(), this::toAuthorSummary));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<AuthorProfile> findAuthorByUsername(String username) {
        return userProfileRepository.findSummaryByUsername(username)
                .map(profile -> new AuthorProfile(profile.getAuthUser().getId(), toAuthorSummary(profile)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public UserProfile update(UUID authUserId, UpdateProfileRequestDto request) {
        var profile = userProfileRepository.findByAuthUserId(authUserId)
                .orElseThrow(() -> new UserNotFoundException("Profile not found for user id: " + authUserId));
        buildProfile(request, profile);
        log.info("Profile updated for auth user id: {}", authUserId);
        return userProfileRepository.save(profile);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public UserProfile updateAvatar(UUID authUserId, MultipartFile file) {
        var profile = userProfileRepository.findByAuthUserId(authUserId)
                .orElseThrow(() -> new UserNotFoundException("Profile not found for user id: " + authUserId));
        var previousKey = profile.getAvatarUrl();
        // Upload first: if it fails, the user keeps the avatar they have. The old object is only removed
        // once the new key is committed, and the new object is removed again if the transaction rolls back.
        var key = avatarStorageService.upload(authUserId, file);
        profile.setAvatarUrl(key);
        deleteAfterCommit(previousKey);
        deleteOnRollback(key);
        log.info("Avatar updated for auth user id: {}", authUserId);
        return userProfileRepository.save(profile);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteAvatar(UUID authUserId) {
        var profile = userProfileRepository.findByAuthUserId(authUserId)
                .orElseThrow(() -> new UserNotFoundException("Profile not found for user id: " + authUserId));
        if (profile.getAvatarUrl() == null) {
            throw new AvatarNotFoundException("No avatar set for user id: " + authUserId);
        }
        deleteAfterCommit(profile.getAvatarUrl());
        profile.setAvatarUrl(null);
        userProfileRepository.save(profile);
        log.info("Avatar removed for auth user id: {}", authUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public void purgeAvatar(UUID authUserId) {
        userProfileRepository.findByAuthUserId(authUserId)
                .map(UserProfile::getAvatarUrl)
                .ifPresent(this::deleteAfterCommit);
    }

    private void deleteAfterCommit(String key) {
        if (key == null) {
            return;
        }
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    deleteQuietly(key);
                }
            });
        } else {
            deleteQuietly(key);
        }
    }

    private void deleteOnRollback(String key) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    if (status != STATUS_COMMITTED) {
                        deleteQuietly(key);
                    }
                }
            });
        }
    }

    private void deleteQuietly(String key) {
        try {
            avatarStorageService.delete(key);
        } catch (RuntimeException e) {
            log.warn("Could not delete avatar object '{}', it is now orphaned: {}", key, e.getMessage());
        }
    }

    private AuthorSummary toAuthorSummary(UserProfile profile) {
        return new AuthorSummary(
                profile.getUsername(), profile.getDisplayName(), avatarStorageService.presign(profile.getAvatarUrl()));
    }

    private void buildProfile(UpdateProfileRequestDto request, UserProfile profile) {
        profile.setDisplayName(request.displayName());
        profile.setBiography(request.bio());
        profile.setBirthDate(request.birthDate());
        profile.setPhoneNumber(request.phoneNumber());
        profile.setCountry(request.country());
        profile.setCity(request.city());
        profile.setAddress(request.address());
        profile.setFamilyStatus(request.familyStatus());
    }
}
