package local.socialnetwork.userservice.kafka.saga.signup.consumer.user;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.core.type.TypeReference;

import com.fasterxml.jackson.databind.ObjectMapper;

import local.socialnetwork.userservice.kafka.constant.KafkaTopics;

import local.socialnetwork.userservice.dto.user.UserDto;

import local.socialnetwork.userservice.service.UserService;

import local.socialnetwork.userservice.util.MapUtil;
import local.socialnetwork.userservice.util.UUIDUtil;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import org.springframework.kafka.annotation.KafkaListener;

import org.springframework.kafka.core.KafkaTemplate;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.HashMap;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class UserDetailsCreationConsumer {

    static final String USER_DEFAULT_GROUP_ID = "user-default-group-id";

    final UserService userService;
    final KafkaTemplate<String, String> kafkaTemplate;
    final ObjectMapper objectMapper;

    @KafkaListener(topics = KafkaTopics.USER_DETAILS_CREATED_TOPIC, groupId = USER_DEFAULT_GROUP_ID)
    public void receiveUserDataToCreate(ConsumerRecord<String, String> consumerRecord) {
        log.info("Received user data to create. Topic: {} - Timestamp: {}", consumerRecord.topic(), consumerRecord.timestamp());
        buildUserDetails(consumerRecord, parseJsonToMap(consumerRecord));
    }

    private Map<String, Object> parseJsonToMap(ConsumerRecord<String, String> consumerRecord) {
        try {
            String json = consumerRecord.value();
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            log.error("Failed to read json object. Error: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to read user details: " + e.getMessage(), e);
        }
    }

    private void buildUserDetails(ConsumerRecord<String, String> consumerRecord, Map<String, Object> dataMap) {
        try {
            UserDto userDto = buildUserDto(dataMap);
            log.info("Attempting to save a user");
            userService.save(userDto);
        } catch (Exception e) {
            log.error("Failed to process consumer record due to parsing error: '{}'", consumerRecord.value());
            handleUserDetailsFailed(dataMap);
        }
    }

    private UserDto buildUserDto(Map<String, Object> dataMap) {
        return new UserDto(
                UUIDUtil.getUUIDValueFromMap("id", dataMap),
                MapUtil.getValue(dataMap, "firstName", String.class),
                MapUtil.getValue(dataMap, "lastName", String.class),
                MapUtil.getValue(dataMap, "country", String.class),
                MapUtil.getValue(dataMap, "city", String.class),
                MapUtil.getValue(dataMap, "address", String.class),
                MapUtil.getValue(dataMap, "phone", String.class),
                MapUtil.getValue(dataMap, "birthDay", String.class),
                MapUtil.getValue(dataMap, "familyStatus", String.class),
                UUIDUtil.getUUIDValueFromMap("authUserId", dataMap));
    }

    private void handleUserDetailsFailed(Map<String, Object> dataMap) {
        Map<String, Object> dataToSend = prepareDataToSend(dataMap);
        try {
            String message = objectMapper.writeValueAsString(dataToSend);
            kafkaTemplate.send(KafkaTopics.USER_DETAILS_FAILED_TOPIC, message);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize the json object. Error: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to serialize the json object: " + e.getMessage(), e);
        }
    }

    private Map<String, Object> prepareDataToSend(Map<String, Object> dataMap) {
        Map<String, Object> dataToSend = new HashMap<>();
        dataToSend.put("authUserId", UUIDUtil.getUUIDValueFromMap("authUserId", dataMap));
        return dataToSend;
    }
}