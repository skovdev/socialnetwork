package local.socialnetwork.core.service;

import local.socialnetwork.model.dto.GroupDto;

import local.socialnetwork.model.group.Group;

import java.io.IOException;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface GroupService {
    void createGroup(GroupDto groupDto) throws IOException;
    Optional<Group> findById(UUID uuid);
    Group findByName(String name);
    Set<Group> findAllByUsername(String username);
}