package local.socialnetwork.profileservice.kafka.consumer.profile;

import com.fasterxml.jackson.databind.ObjectMapper;

import local.socialnetwork.profileservice.model.dto.profile.ProfileDto;

import local.socialnetwork.profileservice.service.ProfileService;

import lombok.AccessLevel;

import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import org.springframework.kafka.annotation.KafkaListener;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ProfileConsumer {

    static final String PROFILE_GROUP_ID = "default-group-id";
    static final String TOPIC_PROFILE_NEW = "topic.profile.new";

    final ProfileService profileService;
    final ObjectMapper objectMapper;

    @KafkaListener(topics = TOPIC_PROFILE_NEW, groupId = PROFILE_GROUP_ID)
    public void consumeProfileToSave(ConsumerRecord<String, String> consumerRecord) {
        log.info("### -> Received new profile to save <- ###");
        ProfileDto profileDto = getProfile(consumerRecord.value());
        profileService.createProfile(profileDto);
    }

    private ProfileDto getProfile(String jsonProfile) {
        try {
            return objectMapper.readValue(jsonProfile, ProfileDto.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }
}