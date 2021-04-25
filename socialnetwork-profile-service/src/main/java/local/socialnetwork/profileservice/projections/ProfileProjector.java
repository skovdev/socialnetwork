package local.socialnetwork.profileservice.projections;

import local.socialnetwork.profileservice.events.ProfileCreatedEvent;

import local.socialnetwork.profileservice.model.entity.profile.Profile;

import local.socialnetwork.profileservice.repository.ProfileRepository;

import org.axonframework.eventhandling.EventHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
public class ProfileProjector {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileProjector.class);

    private final ProfileRepository profileRepository;

    public ProfileProjector(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @EventHandler
    public void createProfile(ProfileCreatedEvent profileCreatedEvent) {

        Profile profile = new Profile();

        profile.setId(profileCreatedEvent.getId());
        profile.setActive(profileCreatedEvent.isActive());
        profile.setAvatar(profileCreatedEvent.getAvatar());
        profile.setUserId(profileCreatedEvent.getUserId());

        profileRepository.save(profile);

    }
}