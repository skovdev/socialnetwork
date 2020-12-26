package local.socialnetwork.userservice.service.impl;

import local.socialnetwork.userservice.model.entity.UserGroup;

import local.socialnetwork.userservice.repository.UserGroupRepository;

import local.socialnetwork.userservice.service.UserGroupService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

@Service
public class UserGroupServiceImpl implements UserGroupService {

    private UserGroupRepository userGroupRepository;

    @Autowired
    public void setUserGroupRepository(UserGroupRepository userGroupRepository) {
        this.userGroupRepository = userGroupRepository;
    }

    @Override
    public void save(UserGroup userGroup) {
        userGroupRepository.save(userGroup);
    }
}