package local.socialnetwork.authserver.kafka.saga.signup.producer.user;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.ObjectMapper;

import local.socialnetwork.authserver.dto.SignUpDto;

import local.socialnetwork.authserver.dto.user.UserDto;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.core.KafkaTemplate;

import org.springframework.kafka.support.SendResult;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class UserDetailsCreationProducer {

    final KafkaTemplate<String, String> kafkaTemplate;
    final ObjectMapper objectMapper;

    public void sendUserDetailsToCreate(String topic, SignUpDto signUpDTO, UUID authUserId) {
        try {
            log.info("Attempting to send user details to topic: {}", topic);
            UserDto userDto = buildUserDto(signUpDTO, authUserId);
            this.kafkaTemplate.send(topic, objectMapper.writeValueAsString(userDto)).whenComplete(this::loggingResult);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize user details. Error: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to serialize user details: " + e.getMessage(), e);
        }
    }

    private void loggingResult(SendResult<String, String> result, Throwable exception) {
        if (exception != null) {
            log.error("Failed to send user details to topic: {}. Exception: {}", result.getRecordMetadata().topic(), exception.getMessage());
        } else {
            log.info("User details sent successfully to topic: {}", result.getRecordMetadata().topic());
        }
    }

    private UserDto buildUserDto(SignUpDto signUpDTO, UUID authUserId) {
        return new UserDto(
                signUpDTO.firstName(),
                signUpDTO.lastName(),
                signUpDTO.country(),
                signUpDTO.city(),
                signUpDTO.address(),
                signUpDTO.phone(),
                signUpDTO.birthDay(),
                signUpDTO.familyStatus(),
                authUserId);
    }
}