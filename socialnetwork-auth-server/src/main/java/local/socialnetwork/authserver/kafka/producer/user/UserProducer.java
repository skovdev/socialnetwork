package local.socialnetwork.authserver.kafka.producer.user;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.ObjectMapper;

import local.socialnetwork.authserver.dto.SignUpDto;

import local.socialnetwork.authserver.dto.user.UserDto;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.core.KafkaTemplate;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class UserProducer {

    final KafkaTemplate<String, String> kafkaTemplate;
    final ObjectMapper objectMapper;

    public void sendUserAndSave(String topic, SignUpDto signUpDTO, UUID authUserId) {
        try {
            this.kafkaTemplate.send(topic, objectMapper.writeValueAsString(buildUser(signUpDTO, authUserId)));
        } catch (JsonProcessingException e) {
            log.error("Failed to send user. Message: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private UserDto buildUser(SignUpDto signUpDTO, UUID authUserId) {
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