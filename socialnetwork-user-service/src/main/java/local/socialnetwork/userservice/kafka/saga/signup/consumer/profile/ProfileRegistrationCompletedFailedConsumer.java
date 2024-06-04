package local.socialnetwork.userservice.kafka.saga.signup.consumer.profile;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.core.type.TypeReference;

import com.fasterxml.jackson.databind.ObjectMapper;

import local.socialnetwork.userservice.kafka.constant.KafkaTopics;

import local.socialnetwork.userservice.service.UserService;

import local.socialnetwork.userservice.util.UUIDUtil;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import org.springframework.kafka.annotation.KafkaListener;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ProfileRegistrationCompletedFailedConsumer {

    static final String USER_DEFAULT_GROUP_ID = "user-default-group-id";

    final UserService userService;
    final ObjectMapper objectMapper;

    @KafkaListener(topics = KafkaTopics.PROFILE_COMPLETED_FAILED_TOPIC, groupId = USER_DEFAULT_GROUP_ID)
    public void onProfileCompletedFailed(ConsumerRecord<String, String> consumerRecord) {
        log.info("Profile completed is failed. Attempting to delete the user");
        deleteUser(parseJsonToMap(consumerRecord));
    }

    private Map<String, Object> parseJsonToMap(ConsumerRecord<String, String> consumerRecord) {
        try {
            String json = consumerRecord.value();
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            log.error("Failed to read json object. Error: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to read json object: " + e.getMessage(), e);
        }
    }

    private void deleteUser(Map<String, Object> dataMap) {
        try {
            UUID userId = getUserId(dataMap);
            log.info("Attempting to delete the user");
            userService.deleteById(userId);
        } catch (Exception e) {
            log.error("Failed to process deleting the user. UserID: {}", getUserId(dataMap));
            throw new RuntimeException("Failed to delete the user", e);
        }
    }

    private UUID getUserId(Map<String, Object> dataMap) {
        return UUIDUtil.getUUIDValueFromMap("userId", dataMap);
    }
}
