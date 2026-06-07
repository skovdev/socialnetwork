package local.socialnetwork.profiles.service;

import local.socialnetwork.profiles.entity.UserProfile;

import java.util.Optional;

public interface UserProfileService {
    void save(UserProfile userProfile);
    Optional<UserProfile> findByUsername(String username);
}
