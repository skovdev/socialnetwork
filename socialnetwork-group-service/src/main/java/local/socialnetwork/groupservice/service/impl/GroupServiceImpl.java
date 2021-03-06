package local.socialnetwork.groupservice.service.impl;

import local.socialnetwork.groupservice.client.UserProxyService;

import local.socialnetwork.groupservice.kafka.producer.UserProducer;

import local.socialnetwork.groupservice.model.dto.group.GroupDto;

import local.socialnetwork.groupservice.model.entity.GroupUser;

import local.socialnetwork.groupservice.model.entity.group.Group;

import local.socialnetwork.groupservice.repository.GroupRepository;

import local.socialnetwork.groupservice.service.GroupService;
import local.socialnetwork.groupservice.service.GroupUserService;

import local.socialnetwork.groupservice.type.GroupStatus;

import local.socialnetwork.groupservice.util.ResourceUtil;

import local.socialnetwork.kafka.model.dto.GroupUserIdsDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GroupServiceImpl implements GroupService {

    private static final Logger LOG = LoggerFactory.getLogger(GroupServiceImpl.class);

    @Value("${sn.group.default.avatar.path}")
    private String pathDefaultGroupAvatar;

    @Value("${sn.kafka.topic.group.relationship.user}")
    private String topicGroupRelationshipUser;

    private GroupRepository groupRepository;

    @Autowired
    public void setGroupRepository(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    private GroupUserService groupUserService;

    @Autowired
    public void setGroupUserService(GroupUserService groupUserService) {
        this.groupUserService = groupUserService;
    }

    private UserProxyService userProxyService;

    @Autowired
    public void setUserProxyService(UserProxyService userProxyService) {
        this.userProxyService = userProxyService;
    }

    private UserProducer userProducer;

    @Autowired
    public void setUserProducer(UserProducer userProducer) {
        this.userProducer = userProducer;
    }

    private ResourceUtil resourceUtil;

    @Autowired
    public void setResourceUtil(ResourceUtil resourceUtil) {
        this.resourceUtil = resourceUtil;
    }

    @Override
    public void createGroup(String username, GroupDto groupDto) throws IOException {

        var user = userProxyService.findUserByUsername(username);

        if (user != null) {

            Group group = new Group();

            String encodedGroupAvatar = resourceUtil.getEncodedResource(pathDefaultGroupAvatar);

            group.setName(groupDto.getGroupName());
            group.setAvatar(encodedGroupAvatar);
            group.setGroupType(groupDto.getGroupType());
            group.setGroupStatus(GroupStatus.ACTIVE);
            group.setGroupAmountUsers(1);

            groupRepository.save(group);

            Group savedGroup = groupRepository.findByName(group.getName());

            if (savedGroup != null) {

                GroupUser groupUser = new GroupUser();

                groupUser.setUserId(user.getId());
                groupUser.setGroupId(savedGroup.getId());

                groupUserService.save(groupUser);

                GroupUserIdsDto groupUserIdsDto = new GroupUserIdsDto();

                groupUserIdsDto.setGroupId(savedGroup.getId());
                groupUserIdsDto.setUserId(user.getId());

                userProducer.send(topicGroupRelationshipUser, groupUserIdsDto);

                LOG.info("Group {} has saved", groupDto.getGroupName());

            }
        }
    }

    @Override
    public Optional<Group> findById(UUID uuid) {
        return groupRepository.findById(uuid);
    }

    @Transactional
    @Override
    public Group findByName(String name) {
        return groupRepository.findByName(name);
    }

    @Transactional
    @Override
    public List<Group> findAllByUsername(String username) {

        var user = userProxyService.findUserByUsername(username);

        if (user != null) {

            List<GroupUser> groupUsers = groupUserService.findAllGroupIdsByUserId(user.getId());

            List<Group> returnedGroups = new ArrayList<>();

            for (GroupUser groupUser : groupUsers) {
                Group group = groupRepository.findGroupById(groupUser.getGroupId());
                returnedGroups.add(group);
            }

            return returnedGroups;

        }

        return Collections.emptyList();

    }
}