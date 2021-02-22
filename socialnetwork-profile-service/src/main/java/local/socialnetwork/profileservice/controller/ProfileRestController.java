package local.socialnetwork.profileservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import io.swagger.v3.oas.annotations.media.Content;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import io.swagger.v3.oas.annotations.tags.Tag;

import local.socialnetwork.kafka.model.dto.profile.EditProfileDto;

import local.socialnetwork.profileservice.client.user.UserProxyService;

import local.socialnetwork.profileservice.model.dto.profile.ProfileDto;

import local.socialnetwork.profileservice.model.dto.user.UserDetailsDto;
import local.socialnetwork.profileservice.model.dto.user.UserDto;

import local.socialnetwork.profileservice.model.entity.profile.Profile;

import local.socialnetwork.profileservice.exception.ProfileServiceException;


import local.socialnetwork.profileservice.service.ProfileService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.List;
import java.util.UUID;

@Tag(name = "ProfileRestController")
@RestController
@RequestMapping("/profiles")
public class ProfileRestController {

    private UserProxyService userProxyService;

    @Autowired
    public void setUserProxyService(UserProxyService userProxyService) {
        this.userProxyService = userProxyService;
    }

    private ProfileService profileService;

    @Autowired
    public void setProfileService(ProfileService profileService) {
        this.profileService = profileService;
    }

    @Operation(summary = "Get all profiles")
    @ApiResponse(description = "Found the profiles", content = { @Content(mediaType = "application/json") }, responseCode = "200")
    @GetMapping
    public List<ProfileDto> findAll() {
        return profileService.findAll();
    }

    @Operation(summary = "Create a new profile")
    @ApiResponses(value = {
            @ApiResponse(description = "Profile has created", content = { @Content(mediaType = "application/json") }, responseCode = "200"),
            @ApiResponse(description = "Profile not found", content = { @Content(mediaType = "application/json") }, responseCode = "404")
    })
    @PostMapping
    public ResponseEntity<String> save(@RequestBody @Parameter(description = "Object of profile for creating a new profile") Profile profile) {

        if (profile != null) {
            profileService.save(profile);
            return new ResponseEntity<>("Profile has saved successfully", HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get the profile by user id")
    @ApiResponse(description = "Found the profile", content = { @Content(mediaType = "application/json") }, responseCode = "200")
    @GetMapping("/user")
    public Profile   findByUserId(@RequestParam("userId") @Parameter(description = "Id of user for getting of profile") UUID userId) {
        return profileService.findProfileByUserId(userId);
    }

    @Operation(summary = "Get a profile by username")
    @ApiResponses(value = {
            @ApiResponse(description = "Found the profile", content = { @Content(mediaType = "application/json") }, responseCode = "200")

    })
    @GetMapping("/user/{username}")
    public ResponseEntity<ProfileDto> findProfileByUsername(@PathVariable("username") @Parameter(description = "Username of user for finding of profile by username") String username) {

        var user = userProxyService.findUserByUsername(username);

        if (user != null && user.getUsername().equalsIgnoreCase(username)) {

            Profile profile = profileService.findProfileByUserId(user.getId());

            if (profile != null) {

                UserDto userDto = new UserDto();

                userDto.setId(user.getId());
                userDto.setUsername(user.getUsername());
                userDto.setFirstName(user.getFirstName());
                userDto.setLastName(user.getLastName());

                UserDetailsDto userDetailsDto = new UserDetailsDto();

                userDetailsDto.setCountry(user.getUserDetails().getCountry());
                userDetailsDto.setCity(user.getUserDetails().getCity());
                userDetailsDto.setAddress(user.getUserDetails().getAddress());
                userDetailsDto.setPhone(user.getUserDetails().getPhone());
                userDetailsDto.setBirthday(user.getUserDetails().getBirthday());
                userDetailsDto.setFamilyStatus(user.getUserDetails().getFamilyStatus());

                userDto.setUserDetails(userDetailsDto);

                userDto.setRoles(user.getRoles());

                ProfileDto profileDto = new ProfileDto();

                profileDto.setId(profile.getId());
                profileDto.setAvatar(profile.getAvatar());
                profileDto.setActive(profile.isActive());
                profileDto.setUser(userDto);

                return new ResponseEntity<>(profileDto, HttpStatus.OK);

            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Edit the profile by username")
    @ApiResponses(value = {
            @ApiResponse(description = "Profile has edited", content = { @Content(mediaType = "application/json") }, responseCode = "200"),
            @ApiResponse(description = "Profile has not edited", content = { @Content(mediaType = "application/json") }, responseCode = "404")
    })
    @GetMapping("/user/{username}/edit")
    public ResponseEntity<EditProfileDto> editProfileByUsername(@PathVariable("username") @Parameter(description = "Username of user for editing of profile by username") String username) {

        try {
            return new ResponseEntity<>(profileService.editProfileByUsername(username), HttpStatus.OK);
        } catch (ProfileServiceException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Update the profile by id")
    @ApiResponses(value = {
            @ApiResponse(description = "Profile has updated successfully", content = { @Content(mediaType = "application/json") }, responseCode = "200"),
            @ApiResponse(description = "Profile has not updated", content = { @Content(mediaType = "application/json") }, responseCode = "404")
    })
    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable("id") @Parameter(description = "Id of profile for updating of profile by id") UUID id, @RequestBody @Parameter(description = "DTO for updating of profile by id") EditProfileDto editProfileDto) {

        try {
            profileService.update(id, editProfileDto);
            return new ResponseEntity<>("Profile with ID: " + id + " has updated successfully", HttpStatus.OK);
        } catch (ProfileServiceException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Get the avatar of profile")
    @ApiResponses(value = {
            @ApiResponse(description = "Found the avatar of profile", content = { @Content(mediaType = "application/json") }, responseCode = "200"),
            @ApiResponse(description = "Profile avatar has not found", content = { @Content(mediaType = "application/json") }, responseCode = "404")
    })
    @GetMapping("/avatar")
    public ResponseEntity<String> getAvatar(@RequestParam("username") @Parameter(description = "Username of user for finding of avatar of profile by username") String username) {

        var user = profileService.findProfileByUsername(username);

        if (user != null) {
            return new ResponseEntity<>(user.getAvatar(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Update the avatar of profile")
    @ApiResponses(value = {
            @ApiResponse(description = "Profile avatar has updated", content = { @Content(mediaType = "application/json") }, responseCode = "200"),
            @ApiResponse(description = "Profile avatar has not updated", content = { @Content(mediaType = "application/json") }, responseCode = "404")
    })
    @PutMapping("/avatar")
    public ResponseEntity<String> updateAvatar(@RequestParam("username") @Parameter(description = "Username of user for updating of avatar of profile by username") String username, @Parameter(description = "Param for getting of avatar by username") @RequestParam("profileAvatar") MultipartFile multipartFile) {

        try {
            profileService.updateAvatarProfile(username, multipartFile);
            return new ResponseEntity<>("Avatar has updated successfully", HttpStatus.OK);
        } catch (ProfileServiceException | IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Delete the avatar of profile")
    @ApiResponses(value = {
            @ApiResponse(description = "Profile avatar has deleted", content = { @Content(mediaType = "application/json") }, responseCode = "200"),
            @ApiResponse(description = "Profile avatar has not deleted", content = { @Content(mediaType = "application/json") }, responseCode = "404")
    })
    @DeleteMapping("/avatar")
    public ResponseEntity<String> deleteAvatar(@RequestParam("username") @Parameter(description = "Username of user for deleting of avatar by username") String username) {
        try {
            return new ResponseEntity<>(profileService.setDefaultAvatar(username), HttpStatus.OK);
        } catch (IOException | ProfileServiceException  e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Change the status of profile")
    @ApiResponses(value = {
            @ApiResponse(description = "Profile status has changed", content = { @Content(mediaType = "application/json") }, responseCode = "200"),
            @ApiResponse(description = "Profile status has not changed", content = { @Content(mediaType = "application/json") }, responseCode = "404")
    })
    @PutMapping
    public ResponseEntity<String> changeStatus(@RequestParam("username") @Parameter(description = "Username of user for changing of profile status by username") String username, @Parameter(description = "Param for changing of status of profile") @RequestParam("isActive") boolean isActive) {

        if (profileService.changeStatus(username, isActive)) {
            return new ResponseEntity<>("Status has changed successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @PutMapping("/password/change")
    public ResponseEntity<String> changePassword(String oldPassword, String newPassword) {
        return null;
    }
}