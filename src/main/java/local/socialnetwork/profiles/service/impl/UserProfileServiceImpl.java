package local.socialnetwork.profiles.service.impl;

import local.socialnetwork.profiles.dto.http.request.UpdateProfileRequestDto;

import local.socialnetwork.profiles.entity.UserProfile;

import local.socialnetwork.profiles.repository.UserProfileRepository;

import local.socialnetwork.profiles.service.UserProfileService;

import local.socialnetwork.shared.exception.UserNotFoundException;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.Optional;

/**
 * Default implementation of {@link UserProfileService}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;

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
    @Transactional
    public UserProfile update(UUID authUserId, UpdateProfileRequestDto request) {
        var profile = userProfileRepository.findByAuthUserId(authUserId)
                .orElseThrow(() -> new UserNotFoundException("Profile not found for user id: " + authUserId));
        buildProfile(request, profile);
        log.info("Profile updated for auth user id: {}", authUserId);
        return userProfileRepository.save(profile);
    }

    private void buildProfile(UpdateProfileRequestDto request, UserProfile profile) {
        profile.setDisplayName(request.displayName());
        profile.setBiography(request.bio());
        profile.setAvatarUrl(request.avatarUrl());
        profile.setBirthDate(request.birthDate());
        profile.setPhoneNumber(request.phoneNumber());
        profile.setCountry(request.country());
        profile.setCity(request.city());
        profile.setAddress(request.address());
        profile.setFamilyStatus(request.familyStatus());
    }
}
