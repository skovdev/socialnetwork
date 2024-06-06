package local.socialnetwork.profileservice.kafka.saga.signup.consumer.profile;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.core.type.TypeReference;

import com.fasterxml.jackson.databind.ObjectMapper;

import local.socialnetwork.profileservice.dto.profile.ProfileDto;

import local.socialnetwork.profileservice.kafka.constant.KafkaTopics;

import local.socialnetwork.profileservice.service.ProfileService;

import local.socialnetwork.profileservice.util.MapUtil;

import local.socialnetwork.profileservice.util.UUIDUtil;
import lombok.AccessLevel;

import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import org.springframework.kafka.annotation.KafkaListener;

import org.springframework.kafka.core.KafkaTemplate;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.HashMap;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ProfileRegistrationCompletedConsumer {

    static final String PROFILE_GROUP_ID = "profile-default-group-id";

    final ProfileService profileService;
    final KafkaTemplate<String, String> kafkaTemplate;
    final ObjectMapper objectMapper;

    @KafkaListener(topics = KafkaTopics.PROFILE_COMPLETED_TOPIC, groupId = PROFILE_GROUP_ID)
    public void receiveProfileDataToCreate(ConsumerRecord<String, String> consumerRecord) {
        log.info("Received profile data to create. Topic: {} - Timestamp: {}", consumerRecord.topic(), consumerRecord.timestamp());
        buildProfile(parseJsonToMap(consumerRecord));
    }

    private void buildProfile(Map<String, Object> dataMap) {
        try {
            ProfileDto profileDto = buildProfileDto(dataMap);
            log.info("Attempting to save a profile");
            profileService.save(profileDto);
        } catch (Exception e) {
            log.error("Failed to process saving of the profile");
            handleProfileCompletedFailed(dataMap);
        }
    }

    private Map<String, Object> parseJsonToMap(ConsumerRecord<String, String> consumerRecord) {
        try {
            String json = consumerRecord.value();
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            log.error("Failed to read the json object. Error: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to read the json object: " + e.getMessage(), e);
        }
    }

    private ProfileDto buildProfileDto(Map<String, Object> dataMap) {
        return new ProfileDto(
                UUIDUtil.getUUIDValueFromMap("id", dataMap),
                MapUtil.getValue(dataMap, "isActive", Boolean.class),
                MapUtil.getValue(dataMap, "avatar", String.class),
                UUIDUtil.getUUIDValueFromMap("userId", dataMap));
    }

    private void handleProfileCompletedFailed(Map<String, Object> dataMap) {
        Map<String, Object> dataToSend = prepareDataToSend(dataMap);
        try {
            String message = objectMapper.writeValueAsString(dataToSend);
            kafkaTemplate.send(KafkaTopics.PROFILE_COMPLETED_FAILED_TOPIC, message);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize the json object. Error: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to serialize the json object: " + e.getMessage(), e);
        }
    }

    private Map<String, Object> prepareDataToSend(Map<String, Object> dataMap) {
        Map<String, Object> dataToSend = new HashMap<>();
        dataToSend.put("authUserId", UUIDUtil.getUUIDValueFromMap("authUserId", dataMap));
        dataToSend.put("userId", UUIDUtil.getUUIDValueFromMap("userId", dataMap));
        return dataToSend;
    }
}