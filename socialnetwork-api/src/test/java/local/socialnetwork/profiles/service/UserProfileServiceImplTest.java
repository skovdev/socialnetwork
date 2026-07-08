package local.socialnetwork.profiles.service;

import local.socialnetwork.profiles.dto.http.request.UpdateProfileRequestDto;

import local.socialnetwork.profiles.entity.UserProfile;

import local.socialnetwork.profiles.repository.UserProfileRepository;


import local.socialnetwork.profiles.service.impl.UserProfileServiceImpl;

import local.socialnetwork.shared.exception.AvatarNotFoundException;
import local.socialnetwork.shared.exception.UserNotFoundException;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.InjectMocks;

import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.mock.web.MockMultipartFile;

import java.util.UUID;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceImplTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private AvatarStorageService avatarStorageService;

    @InjectMocks
    private UserProfileServiceImpl userProfileService;

    @Test
    void findByUsername_whenExists_returnsProfile() {
        var profile = new UserProfile();
        profile.setUsername("alice");
        when(userProfileRepository.findByUsername("alice")).thenReturn(Optional.of(profile));

        var result = userProfileService.findByUsername("alice");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("alice");
    }

    @Test
    void findByUsername_whenNotExists_returnsEmpty() {
        when(userProfileRepository.findByUsername("nobody")).thenReturn(Optional.empty());

        var result = userProfileService.findByUsername("nobody");

        assertThat(result).isEmpty();
    }

    @Test
    void findByAuthUserId_whenExists_returnsProfile() {
        var userId = UUID.randomUUID();
        var profile = new UserProfile();
        when(userProfileRepository.findByAuthUserId(userId)).thenReturn(Optional.of(profile));

        var result = userProfileService.findByAuthUserId(userId);

        assertThat(result).isPresent();
    }

    @Test
    void findByAuthUserId_whenNotExists_returnsEmpty() {
        var userId = UUID.randomUUID();
        when(userProfileRepository.findByAuthUserId(userId)).thenReturn(Optional.empty());

        var result = userProfileService.findByAuthUserId(userId);

        assertThat(result).isEmpty();
    }

    @Test
    void update_whenProfileExists_appliesChangesAndReturnsUpdatedProfile() {
        var userId = UUID.randomUUID();
        var existing = new UserProfile();
        existing.setDisplayName("Old Name");

        when(userProfileRepository.findByAuthUserId(userId)).thenReturn(Optional.of(existing));
        when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        var request = new UpdateProfileRequestDto(
                "New Name", "Bio text", null, null, "France", "Paris", null, null);

        var result = userProfileService.update(userId, request);

        assertThat(result.getDisplayName()).isEqualTo("New Name");
        assertThat(result.getBiography()).isEqualTo("Bio text");
        assertThat(result.getCountry()).isEqualTo("France");
        assertThat(result.getCity()).isEqualTo("Paris");
        verify(userProfileRepository).save(existing);
    }

    @Test
    void update_whenProfileNotExists_throwsUserNotFoundException() {
        var userId = UUID.randomUUID();
        when(userProfileRepository.findByAuthUserId(userId)).thenReturn(Optional.empty());

        var request = new UpdateProfileRequestDto(
                "Name", null, null, null, null, null, null, null);

        assertThatThrownBy(() -> userProfileService.update(userId, request))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(userId.toString());
    }

    @Test
    void updateAvatar_whenProfileHasNoExistingAvatar_uploadsAndSavesKey() {
        var userId = UUID.randomUUID();
        var existing = new UserProfile();
        var file = new MockMultipartFile("file", "avatar.png", "image/png", "content".getBytes());

        when(userProfileRepository.findByAuthUserId(userId)).thenReturn(Optional.of(existing));
        when(avatarStorageService.upload(eq(userId), any())).thenReturn("avatars/key.png");
        when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        var result = userProfileService.updateAvatar(userId, file);

        assertThat(result.getAvatarUrl()).isEqualTo("avatars/key.png");
    }

    @Test
    void updateAvatar_whenProfileHasExistingAvatar_deletesOldBeforeUploadingNew() {
        var userId = UUID.randomUUID();
        var existing = new UserProfile();
        existing.setAvatarUrl("avatars/old-key.png");
        var file = new MockMultipartFile("file", "avatar.png", "image/png", "content".getBytes());

        when(userProfileRepository.findByAuthUserId(userId)).thenReturn(Optional.of(existing));
        when(avatarStorageService.upload(eq(userId), any())).thenReturn("avatars/new-key.png");
        when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        var result = userProfileService.updateAvatar(userId, file);

        assertThat(result.getAvatarUrl()).isEqualTo("avatars/new-key.png");
        verify(avatarStorageService).delete("avatars/old-key.png");
    }

    @Test
    void updateAvatar_whenProfileNotExists_throwsUserNotFoundException() {
        var userId = UUID.randomUUID();
        var file = new MockMultipartFile("file", "avatar.png", "image/png", "content".getBytes());
        when(userProfileRepository.findByAuthUserId(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userProfileService.updateAvatar(userId, file))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void deleteAvatar_whenAvatarSet_deletesFromStorageAndClearsField() {
        var userId = UUID.randomUUID();
        var existing = new UserProfile();
        existing.setAvatarUrl("avatars/key.png");

        when(userProfileRepository.findByAuthUserId(userId)).thenReturn(Optional.of(existing));
        when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        userProfileService.deleteAvatar(userId);

        verify(avatarStorageService).delete("avatars/key.png");
        assertThat(existing.getAvatarUrl()).isNull();
    }

    @Test
    void deleteAvatar_whenNoAvatarSet_throwsAvatarNotFoundException() {
        var userId = UUID.randomUUID();
        var existing = new UserProfile();

        when(userProfileRepository.findByAuthUserId(userId)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> userProfileService.deleteAvatar(userId))
                .isInstanceOf(AvatarNotFoundException.class);
    }

    @Test
    void deleteAvatar_whenProfileNotExists_throwsUserNotFoundException() {
        var userId = UUID.randomUUID();
        when(userProfileRepository.findByAuthUserId(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userProfileService.deleteAvatar(userId))
                .isInstanceOf(UserNotFoundException.class);
    }
}
