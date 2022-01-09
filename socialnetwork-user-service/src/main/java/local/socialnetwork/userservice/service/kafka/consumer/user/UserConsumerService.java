package local.socialnetwork.userservice.service.kafka.consumer.user;

import local.socialnetwork.kafka.common.model.dto.profile.EditProfileDto;
import local.socialnetwork.userservice.service.UserService;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class UserConsumerService {

    static final String USER_DEFAULT_GROUP_ID = "user-default-group-id";
    static final String TOPIC_USER_UPDATE = "topic.user.update";
    static final String TOPIC_USER_DELETE = "topic.user.delete";

    UserService userService;

    @KafkaListener(topics = TOPIC_USER_UPDATE, groupId = USER_DEFAULT_GROUP_ID)
    public void receiveUserForUpdate(EditProfileDto editProfileDto) {
        log.info(String.format("### -> Received fields of user for updating: %s", editProfileDto));
        userService.update(editProfileDto);
    }

    @KafkaListener(topics = TOPIC_USER_DELETE, groupId = USER_DEFAULT_GROUP_ID)
    public void receiveUserIdForDeleteUser(UUID userId) {
        log.info(String.format("### -> Received id of user: %s <- ###", userId));
        userService.deleteById(userId);
    }
}