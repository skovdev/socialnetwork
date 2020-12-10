package local.socialnetwork.profileservice.kafka.consumer.profile;


import local.socialnetwork.kafka.model.dto.profile.ProfileDto;
import local.socialnetwork.profileservice.model.entity.profile.Profile;

import local.socialnetwork.profileservice.service.ProfileService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.kafka.annotation.KafkaListener;

import org.springframework.stereotype.Component;

@Component
public class ProfileConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileConsumer.class);

    private static final String TOPIC_PROFILE_NEW = "topic.profile.new";
    private static final String GROUP_PROFILE_NEW = "group.profile.new";

    private ProfileService profileService;

    @Autowired
    public void setProfileService(ProfileService profileService) {
        this.profileService = profileService;
    }

    @KafkaListener(topics = TOPIC_PROFILE_NEW, groupId = GROUP_PROFILE_NEW)
    public void receiveProfileForSave(ProfileDto profileDto) {

        LOGGER.info(String.format("Received object: { %s }", profileDto));

        Profile profile = new Profile();

        profile.setAvatar(profileDto.getAvatar());
        profile.setActive(profileDto.isActive());
        profile.setUserId(profileDto.getUserId());

        profileService.save(profile);

    }
}