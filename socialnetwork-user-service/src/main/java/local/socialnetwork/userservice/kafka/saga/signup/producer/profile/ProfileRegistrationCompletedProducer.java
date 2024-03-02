package local.socialnetwork.userservice.kafka.saga.signup.producer.profile;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.ObjectMapper;

import local.socialnetwork.userservice.dto.profile.ProfileDto;

import local.socialnetwork.userservice.dto.user.UserProfileDto;
import local.socialnetwork.userservice.util.ResourceUtil;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.kafka.core.KafkaTemplate;

import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.io.IOException;

import java.util.UUID;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ProfileRegistrationCompletedProducer {

    private static final boolean PROFILE_ACTIVE = true;

    @Value("${sn.profile.default.avatar.path}")
    String pathDefaultAvatar;

    final KafkaTemplate<String, String> kafkaTemplate;
    final ResourceUtil resourceUtil;
    final ObjectMapper objectMapper;

    public void sendProfileDataToCreate(String topic, UUID authUserId, UUID userId) {
        try {
            log.info("Attempting to send profile data to topic: {}", topic);
            UserProfileDto userProfileDto = buildUserProfileDto(authUserId, userId);
            this.kafkaTemplate.send(topic, objectMapper.writeValueAsString(userProfileDto)).whenComplete(this::loggingResult);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize profile data. Error: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to serialize profile data: " + e.getMessage(), e);
        }
    }

    private void loggingResult(SendResult<String, String> result, Throwable exception) {
        if (exception != null) {
            log.error("Failed to send profile data to topic: {}. Exception: {}", result.getRecordMetadata().topic(), exception.getMessage());
        } else {
            log.info("Profile data sent successfully to topic: {}", result.getRecordMetadata().topic());
        }
    }

    private UserProfileDto buildUserProfileDto(UUID authUserId, UUID userId) {
        return new UserProfileDto(buildProfileDto(userId), authUserId);
    }

    private ProfileDto buildProfileDto(UUID userId) {
        try {
            return new ProfileDto(UUID.randomUUID(), PROFILE_ACTIVE, resourceUtil.getEncodedResource(pathDefaultAvatar), userId);
        } catch (IOException e) {
            log.error("Failed to encode avatar for profile. Error: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}