package local.socialnetwork.userservice.kafka.consumer.user;

import local.socialnetwork.kafka.model.dto.GroupUserIdsDto;
import local.socialnetwork.kafka.model.dto.profile.EditProfileDto;

import local.socialnetwork.userservice.model.entity.UserGroup;

import local.socialnetwork.userservice.service.UserGroupService;
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

    private static final String USER_DEFAULT_GROUP_ID = "user-default-group-id";
    private static final String TOPIC_USER_UPDATE = "topic.user.update";
    private static final String TOPIC_USER_DELETE = "topic.user.delete";
    private static final String TOPIC_GROUP_RELATIONSHIP_USER = "group.relationship.user";

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    private UserGroupService userGroupService;

    @Autowired
    public void setUserGroupService(UserGroupService userGroupService) {
        this.userGroupService = userGroupService;
    }

    @KafkaListener(topics = TOPIC_USER_UPDATE, groupId = USER_DEFAULT_GROUP_ID)
    public void receiveUserForUpdate(EditProfileDto editProfileDto) {
        LOGGER.info(String.format("### -> Received fields of user for updating: %s", editProfileDto));
        userService.update(editProfileDto);
    }

    @KafkaListener(topics = TOPIC_USER_DELETE, groupId = USER_DEFAULT_GROUP_ID)
    public void receiveUserIdForDeleteUser(UUID userId) {
        LOGGER.info(String.format("### -> Received id of user: %s <- ###", userId));
        userService.deleteById(userId);
    }

    @KafkaListener(topics = TOPIC_GROUP_RELATIONSHIP_USER, groupId = USER_DEFAULT_GROUP_ID)
    public void receiveUserForUserRelationShipGroup(GroupUserIdsDto groupUserIdsDto) {

        LOGGER.info(String.format("### -> Received ids of group %s and user: %s <- ###", groupUserIdsDto.getGroupId(), groupUserIdsDto.getUserId()));

        UserGroup userGroup = new UserGroup();

        userGroup.setUserId(groupUserIdsDto.getUserId());
        userGroup.setGroupId(groupUserIdsDto.getGroupId());

        userGroupService.save(userGroup);

    }
}