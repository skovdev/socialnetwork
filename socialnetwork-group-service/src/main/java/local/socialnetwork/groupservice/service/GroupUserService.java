package local.socialnetwork.groupservice.service;

import local.socialnetwork.groupservice.model.entity.GroupUser;

import java.util.List;
import java.util.UUID;

public interface GroupUserService {
    List<GroupUser> findAllGroupIdsByUserId(UUID userId);
    void save(GroupUser groupUser);
}
