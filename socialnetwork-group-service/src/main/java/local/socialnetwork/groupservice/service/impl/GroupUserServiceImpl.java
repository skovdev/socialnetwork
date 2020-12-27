package local.socialnetwork.groupservice.service.impl;

import local.socialnetwork.groupservice.model.entity.GroupUser;

import local.socialnetwork.groupservice.repository.GroupUserRepository;

import local.socialnetwork.groupservice.service.GroupUserService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

@Service
public class GroupUserServiceImpl implements GroupUserService {

    private GroupUserRepository groupUserRepository;

    @Autowired
    public void setGroupUserRepository(GroupUserRepository groupUserRepository) {
        this.groupUserRepository = groupUserRepository;
    }

    @Override
    public void save(GroupUser groupUser) {
        groupUserRepository.save(groupUser);
    }
}