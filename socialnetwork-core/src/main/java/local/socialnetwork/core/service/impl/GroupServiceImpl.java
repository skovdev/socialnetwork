package local.socialnetwork.core.service.impl;

import local.socialnetwork.common.auth.AuthenticationHelper;

import local.socialnetwork.core.repository.GroupRepository;

import local.socialnetwork.core.service.GroupService;
import local.socialnetwork.core.service.UserService;

import local.socialnetwork.core.util.ResourceUtil;

import local.socialnetwork.model.dto.GroupDto;

import local.socialnetwork.model.group.Group;
import local.socialnetwork.model.group.GroupStatus;

import local.socialnetwork.model.user.CustomUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.io.IOException;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class GroupServiceImpl implements GroupService {

    private static final Logger LOG = LoggerFactory.getLogger(GroupServiceImpl.class);

    @Value("${sn.group.default.avatar.dir}")
    private String pathDefaultGroupAvatar;
    
    private AuthenticationHelper authenticationHelper;

    @Autowired
    public void setAuthenticationHelper(AuthenticationHelper authenticationHelper) {
        this.authenticationHelper = authenticationHelper;
    }

    private ResourceUtil resourceUtil;

    @Autowired
    public void setResourceUtil(ResourceUtil resourceUtil) {
        this.resourceUtil = resourceUtil;
    }

    public GroupRepository groupRepository;

    @Autowired
    public void setGroupRepository(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Transactional
    @Override
    public void createGroup(GroupDto groupDto) throws IOException {

        CustomUser authenticatedUser = authenticationHelper.getAuthenticatedUser();

        if (authenticatedUser != null) {

            Group group = new Group();

            String encodedGroupAvatar = resourceUtil.getEncodedResource(pathDefaultGroupAvatar);

            group.setName(groupDto.getGroupName());
            group.setAvatar(encodedGroupAvatar);
            group.setGroupType(groupDto.getGroupType());
            group.setGroupStatus(GroupStatus.ACTIVE);
            group.setGroupAmountUsers(1);

            group.addUser(authenticatedUser);

            groupRepository.save(group);

            LOG.info("Group {} has saved", groupDto.getGroupName());

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

    @Override
    public Set<Group> findAllByUsername(String username) {

        var user = userService.findByName(username);

        if (user != null) {
            return user.getGroups();
        }

        return Collections.emptySet();

    }
}