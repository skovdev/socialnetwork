package local.socialnetwork.userservice.kafka.consumer.user;

import local.socialnetwork.userservice.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.kafka.annotation.KafkaListener;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserConsumer.class);

    private static final String TOPIC_USER_DELETE = "topic.user.delete";
    private static final String USER_DEFAULT_GROUP_ID = "user-default-group-id";

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @KafkaListener(topics = TOPIC_USER_DELETE, groupId = USER_DEFAULT_GROUP_ID)
    public void receiveUserIdForDeleteUser(UUID userId) {
        LOGGER.info(String.format("### -> Received id of user: %s <- ###", userId));
        userService.deleteById(userId);
    }
}