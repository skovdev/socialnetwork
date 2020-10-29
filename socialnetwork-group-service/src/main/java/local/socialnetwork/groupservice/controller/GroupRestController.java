package local.socialnetwork.groupservice.controller;

import local.socialnetwork.groupservice.model.dto.group.GroupDto;

import local.socialnetwork.groupservice.model.dto.http.ApiResponse;

import local.socialnetwork.groupservice.model.group.Group;

import local.socialnetwork.groupservice.service.GroupService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(value = "/groups")
public class GroupRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupRestController.class);

    private GroupService groupService;

    @Autowired
    public void setGroupService(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping("/{groupId}")
    public Optional<Group> findById(@PathVariable("groupId") UUID groupId) {
        return groupService.findById(groupId);
    }

    @GetMapping
    public List<Group> findAllByUsername(@RequestParam("username") String username) {
        return groupService.findAllByUsername(username);
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createGroup(@RequestParam("username") String username, @RequestBody GroupDto groupDto) throws IOException {

        var group = groupService.findByName(groupDto.getGroupName());

        if (group != null) {
            LOGGER.info("Group {} is exist", groupDto.getGroupName());
            return new ResponseEntity<>(new ApiResponse(groupDto.getGroupName() + " is exist"), HttpStatus.CONFLICT);
        } else {
            groupService.createGroup(username, groupDto);
            LOGGER.info("Group {} has created successfully", groupDto.getGroupName());
            return new ResponseEntity<>(new ApiResponse(groupDto.getGroupName() + " has created successfully"), HttpStatus.CREATED);
        }
    }
}