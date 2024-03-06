package local.socialnetwork.userservice.kafka.saga.signup.consumer.user;

import com.fasterxml.jackson.databind.ObjectMapper;

import local.socialnetwork.userservice.kafka.constant.KafkaTopics;

import local.socialnetwork.userservice.dto.user.UserDto;

import local.socialnetwork.userservice.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import org.springframework.kafka.annotation.KafkaListener;

import org.springframework.kafka.core.KafkaTemplate;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class UserDetailsCreationConsumer {

    static final String USER_DEFAULT_GROUP_ID = "user-default-group-id";

    final UserService userService;
    final KafkaTemplate<String, String> kafkaTemplate;
    final ObjectMapper objectMapper;

    @KafkaListener(topics = KafkaTopics.USER_DETAILS_CREATED, groupId = USER_DEFAULT_GROUP_ID)
    public void receiveUserToCreate(ConsumerRecord<String, String> consumerRecord) {
        log.info("Received user data to create. Topic: {} - Timestamp: {}", consumerRecord.topic(), consumerRecord.timestamp());
        parseUserDto(consumerRecord)
                .ifPresentOrElse(
                        this::createUser,
                        ()-> log.error("Failed to process consumer record due to parsing error: '{}'", consumerRecord.value())
                );
    }

    private Optional<UserDto> parseUserDto(ConsumerRecord<String, String> consumerRecord) {
        try {
            UserDto userDto = objectMapper.readValue(consumerRecord.value(), UserDto.class);
            return Optional.ofNullable(userDto);
        } catch (Exception e) {
            log.error("Failed to read consumer value. Error: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private void createUser(UserDto userDto) {
        try {
            log.info("Attempting to save a user");
            userService.save(userDto);
        } catch (Exception e) {
            handleUserDetailsFailed(userDto, e);
        }
    }

    private void handleUserDetailsFailed(UserDto userDto, Exception e) {
        log.error("Failed to save a user. Error: {}", e.getMessage());
        kafkaTemplate.send(KafkaTopics.USER_DETAILS_FAILED_TOPIC, String.valueOf(userDto.authUserId()));
    }
}