package local.socialnetwork.api.controller;

import local.socialnetwork.core.service.GroupService;

import local.socialnetwork.model.group.Group;

import local.socialnetwork.model.dto.GroupDto;

import local.socialnetwork.model.http.ApiResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/group")
public class GroupRestController {

    private static final Logger LOG = LoggerFactory.getLogger(GroupRestController.class);

    public GroupService groupService;

    @Autowired
    public void setGroupService(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping("/{groupId}")
    public Optional<Group> findById(@PathVariable("groupId") UUID groupId) {
        return groupService.findById(groupId);
    }

    @GetMapping
    public Set<Group> findAllByUsername(@RequestParam("username") String username) {
        return groupService.findAllByUsername(username);
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createGroup(@RequestBody GroupDto groupDto) throws IOException {

        var group = groupService.findByName(groupDto.getGroupName());

        if (group != null) {
            LOG.info("Group {} is exist", groupDto.getGroupName());
            return new ResponseEntity<>(new ApiResponse(groupDto.getGroupName() + " is exist"), HttpStatus.CONFLICT);
        } else {
            groupService.createGroup(groupDto);
            LOG.info("Group {} has created successfully", groupDto.getGroupName());
            return new ResponseEntity<>(new ApiResponse(groupDto.getGroupName() + " has created successfully"), HttpStatus.CREATED);
        }
    }
}