package local.socialnetwork.groupservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import local.socialnetwork.groupservice.model.dto.group.GroupDto;

import local.socialnetwork.groupservice.model.entity.group.Group;

import local.socialnetwork.groupservice.service.GroupService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "GroupRestController")
@RestController
@RequestMapping(value = "/groups")
public class GroupRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupRestController.class);

    private GroupService groupService;

    @Autowired
    public void setGroupService(GroupService groupService) {
        this.groupService = groupService;
    }

    @Operation(summary = "Get the group by id")
    @ApiResponse(description = "Found the group", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) }, responseCode = "200")
    @GetMapping("/{groupId}")
    public Optional<Group> findById(@PathVariable("groupId") @Parameter(description = "Id of group for finding of group") UUID groupId) {
        return groupService.findById(groupId);
    }

    @Operation(summary = "Get the group by username")
    @ApiResponse(description = "Found the group", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) }, responseCode = "200")
    @GetMapping
    public List<Group> findAllByUsername(@RequestParam("username") @Parameter(description = "Username of group for finding of group") String username) {
        return Collections.emptyList();
    }

    @Operation(summary = "Create a new group")
    @ApiResponses(value = {
            @ApiResponse(description = "Group has created", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) }, responseCode = "200"),
            @ApiResponse(description = "Group not found", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) }, responseCode = "404")
    })
    @PostMapping
    public ResponseEntity<String> createGroup(@RequestParam("username") @Parameter(description = "Username of group for finding of group") String username, @RequestBody @Parameter(description = "Object of group for creating a new group") GroupDto groupDto) throws IOException {

        var group = groupService.findByName(groupDto.getGroupName());

        if (group != null) {
            LOGGER.info("Group {} is exist", groupDto.getGroupName());
            return new ResponseEntity<>(groupDto.getGroupName() + " is exist", HttpStatus.CONFLICT);
        } else {
            groupService.createGroup(username, groupDto);
            LOGGER.info("Group {} has created successfully", groupDto.getGroupName());
            return new ResponseEntity<>(groupDto.getGroupName() + " has created successfully", HttpStatus.CREATED);
        }
    }
}