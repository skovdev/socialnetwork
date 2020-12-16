package local.socialnetwork.groupservice.service.impl;

import local.socialnetwork.groupservice.client.UserProxyService;
import local.socialnetwork.groupservice.model.dto.group.GroupDto;

import local.socialnetwork.groupservice.model.group.Group;

import local.socialnetwork.groupservice.repository.GroupRepository;

import local.socialnetwork.groupservice.service.GroupService;

import local.socialnetwork.groupservice.type.GroupStatus;
import local.socialnetwork.groupservice.util.ResourceUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GroupServiceImpl implements GroupService {

    private static final Logger LOG = LoggerFactory.getLogger(GroupServiceImpl.class);

    @Value("${sn.group.default.avatar.path}")
    private String pathDefaultGroupAvatar;

    private GroupRepository groupRepository;

    @Autowired
    public void setGroupRepository(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    private UserProxyService userProxyService;

    @Autowired
    public void setUserProxyService(UserProxyService userProxyService) {
        this.userProxyService = userProxyService;
    }

    private ResourceUtil resourceUtil;

    @Autowired
    public void setResourceUtil(ResourceUtil resourceUtil) {
        this.resourceUtil = resourceUtil;
    }

    @Override
    public void createGroup(String username, GroupDto groupDto) throws IOException {

        var user = userProxyService.findUserByUsername(username);

        Group group = new Group();

        String encodedGroupAvatar = resourceUtil.getEncodedResource(pathDefaultGroupAvatar);

        group.setName(groupDto.getGroupName());
        group.setAvatar(encodedGroupAvatar);
        group.setGroupType(groupDto.getGroupType());
        group.setGroupStatus(GroupStatus.ACTIVE);
        group.setGroupAmountUsers(1);
        group.setUserId(user.getId());

        groupRepository.save(group);

        LOG.info("Group {} has saved", groupDto.getGroupName());

    }

    @Override
    public Optional<Group> findById(UUID uuid) {
        return groupRepository.findById(uuid);
    }

    @Override
    public Group findByName(String name) {
        return groupRepository.findByName(name);
    }

    @Transactional
    @Override
    public List<Group> findAllByUsername(String username) {

        var user = userProxyService.findUserByUsername(username);

        if (user != null) {
            return groupRepository.findAllByUserId(user.getId());
        }

        return Collections.EMPTY_LIST;

    }
}