package local.socialnetwork.authserver.kafka.saga.signup.producer.user;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.ObjectMapper;

import local.socialnetwork.authserver.dto.SignUpDto;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;

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
public class UserDetailsCreationProducer {

    final KafkaTemplate<String, String> kafkaTemplate;
    final ObjectMapper objectMapper;

    public void sendUserDetailsToCreate(String topic, SignUpDto signUpDTO, UUID authUserId) {
        Map<String, Object> data = buildDataMap(signUpDTO, authUserId);
        try {
            log.info("Attempting to send user details to topic: {}", topic);
            String message = objectMapper.writeValueAsString(data);
            this.kafkaTemplate.send(topic, message).whenComplete(this::loggingResult);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize the json object. Error: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to serialize the json object: " + e.getMessage(), e);
        }
    }

    private Map<String, Object> buildDataMap(SignUpDto signUpDTO, UUID authUserId) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", UUID.randomUUID());
        data.put("firstName", signUpDTO.firstName());
        data.put("lastName", signUpDTO.lastName());
        data.put("country", signUpDTO.country());
        data.put("city", signUpDTO.city());
        data.put("address", signUpDTO.address());
        data.put("phone", signUpDTO.phone());
        data.put("birthDay", signUpDTO.birthDay());
        data.put("familyStatus", signUpDTO.familyStatus());
        data.put("authUserId", authUserId);
        return data;
    }

    private void loggingResult(SendResult<String, String> result, Throwable exception) {
        if (exception != null) {
            log.error("Failed to send user details to topic: {}. Exception: {}", result.getRecordMetadata().topic(), exception.getMessage());
        } else {
            log.info("User details sent successfully to topic: {}", result.getRecordMetadata().topic());
        }
    }
}