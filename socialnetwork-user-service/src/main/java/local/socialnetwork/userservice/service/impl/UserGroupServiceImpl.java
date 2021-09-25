package local.socialnetwork.userservice.service.impl;

import local.socialnetwork.userservice.model.entity.UserGroup;

import local.socialnetwork.userservice.repository.UserGroupRepository;

import local.socialnetwork.userservice.service.UserGroupService;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import lombok.experimental.FieldDefaults;

import org.springframework.stereotype.Service;

@Service
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserGroupServiceImpl implements UserGroupService {

    UserGroupRepository userGroupRepository;

    @Override
    public void save(UserGroup userGroup) {
        userGroupRepository.save(userGroup);
    }
}