package local.socialnetwork.userservice.kafka.saga.signup.producer.profile;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.ObjectMapper;

import local.socialnetwork.userservice.util.ResourceUtil;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.kafka.core.KafkaTemplate;

import org.springframework.kafka.support.SendResult;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.HashMap;

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
        Map<String, Object> data = buildDataMap(authUserId, userId);
        try {
            log.info("Attempting to send profile data to topic: {}", topic);
            String message = objectMapper.writeValueAsString(data);
            this.kafkaTemplate.send(topic, message).whenComplete(this::loggingResult);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize the json object. Error: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to serialize the json object: " + e.getMessage(), e);
        }
    }

    private Map<String, Object> buildDataMap(UUID authUserId, UUID userId) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", UUID.randomUUID());
        data.put("isActive", PROFILE_ACTIVE);
        data.put("avatar", resourceUtil.getEncodedResource(pathDefaultAvatar));
        data.put("userId", userId);
        data.put("authUserId", authUserId);
        return data;
    }

    private void loggingResult(SendResult<String, String> result, Throwable exception) {
        if (exception != null) {
            log.error("Failed to send profile data to topic: {}. Exception: {}", result.getRecordMetadata().topic(), exception.getMessage());
        } else {
            log.info("Profile data sent successfully to topic: {}", result.getRecordMetadata().topic());
        }
    }

}