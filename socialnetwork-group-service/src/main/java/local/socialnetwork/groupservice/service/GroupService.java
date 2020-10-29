package local.socialnetwork.groupservice.service;

import local.socialnetwork.groupservice.model.dto.group.GroupDto;
import local.socialnetwork.groupservice.model.group.Group;

import java.io.IOException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupService {
    void createGroup(String username, GroupDto groupDto) throws IOException;
    Optional<Group> findById(UUID uuid);
    Group findByName(String name);
    List<Group> findAllByUsername(String username);
}
