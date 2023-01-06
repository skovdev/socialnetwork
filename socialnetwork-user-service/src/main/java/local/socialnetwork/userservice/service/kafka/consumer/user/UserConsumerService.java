package local.socialnetwork.userservice.service.kafka.consumer.user;

import com.fasterxml.jackson.databind.ObjectMapper;

import local.socialnetwork.userservice.model.dto.profile.EditProfileDto;

import local.socialnetwork.userservice.service.UserService;

import lombok.AccessLevel;

import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import org.springframework.kafka.annotation.KafkaListener;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class UserConsumerService {

    static final String USER_DEFAULT_GROUP_ID = "user-default-group-id";
    static final String TOPIC_USER_UPDATE = "topic.user.update";
    static final String TOPIC_USER_DELETE = "topic.user.delete";

    final UserService userService;
    final ObjectMapper objectMapper;

    @KafkaListener(topics = TOPIC_USER_UPDATE, groupId = USER_DEFAULT_GROUP_ID)
    public void receiveUserForUpdate(ConsumerRecord<String, String> consumerRecord) {
        log.info(String.format("### -> Received fields of user for updating: %s", consumerRecord.value()));
        EditProfileDto editProfileDto = getEditProfile(consumerRecord.value());
        userService.update(editProfileDto);
    }

    @KafkaListener(topics = TOPIC_USER_DELETE, groupId = USER_DEFAULT_GROUP_ID)
    public void receiveUserIdForDeleteUser(UUID userId) {
        log.info(String.format("### -> Received id of user: %s <- ###", userId));
        userService.deleteById(userId);
    }

    private EditProfileDto getEditProfile(String json) {
        try {
            return objectMapper.readValue(json, EditProfileDto.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }
}