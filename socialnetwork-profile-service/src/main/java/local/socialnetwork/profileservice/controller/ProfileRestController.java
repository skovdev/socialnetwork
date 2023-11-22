package local.socialnetwork.profileservice.controller;

import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import io.swagger.v3.oas.annotations.tags.Tag;

import local.socialnetwork.profileservice.constant.VersionAPI;

import local.socialnetwork.profileservice.model.dto.profile.ProfileDto;
import local.socialnetwork.profileservice.model.dto.profile.ProfileInfoDto;
import local.socialnetwork.profileservice.model.dto.profile.ProfileInfoEditDto;

import local.socialnetwork.profileservice.service.ProfileService;

import local.socialnetwork.profileservice.util.ResourceUtil;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.Collections;

@Tag(name = "ProfileRestController", description = "Controller for processing profile-related CRUD operations")
@Slf4j
@RestController
@RequestMapping(VersionAPI.API_V1 + "/profiles")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ProfileRestController {

    ProfileService profileService;
    ResourceUtil resourceUtil;

    @Operation(summary = "Find all the profiles")
    @ApiResponses(value = {
            @ApiResponse(description = "Return all found the profiles", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) }, responseCode = "200"),
            @ApiResponse(description = "Return an empty list if the profiles do not exist", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) }, responseCode = "200")
    })
    @GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ProfileDto> findAll() {
        List<ProfileDto> profiles = profileService.findAll();
        if (profiles != null && !profiles.isEmpty()) {
            return profiles;
        } else {
            return Collections.emptyList();
        }
    }

    @Operation(summary = "Find the profile by id of profile")
    @ApiResponses(value = {
            @ApiResponse(description = "Return found the profile", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) }, responseCode = "200"),
            @ApiResponse(description = "Return the error if the profile does not exist", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) }, responseCode = "404")
    })
    @GetMapping(value = "/{profileId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findByProfileId(@Parameter(description = "This parameter represents id of profile") @PathVariable("profileId") UUID profileId) {
        ProfileDto profile = profileService.findById(profileId);
        if (profile != null) {
            return new ResponseEntity<>(profile, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Profile does not exist", HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Find the profile info by id of profile and id of user")
    @ApiResponses(value = {
            @ApiResponse(description = "Return found the profile info", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) }, responseCode = "200"),
            @ApiResponse(description = "Return the error if the profile info does not exist", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) }, responseCode = "404")
    })
    @GetMapping(value = "/{profileId}/users/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findProfileInfoByProfileIdAndUserId(@Parameter(description = "This parameter represents id of profile") @PathVariable("profileId") UUID profileId,
                                                                 @Parameter(description = "This parameter represents id of user") @PathVariable("userId") UUID userId) {
        ProfileInfoDto profileInfo = profileService.findProfileInfoByProfileIdAndUserId(profileId, userId);
        if (profileInfo != null) {
            return new ResponseEntity<>(profileInfo, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("There is no profile information", HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Find the profile info to edit by id of profile and id of user")
    @ApiResponses(value = {
            @ApiResponse(description = "Return found the profile info to edit", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) }, responseCode = "200"),
            @ApiResponse(description = "Return the error if the profile info to be edited does not exist", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) }, responseCode = "404")
    })
    @GetMapping(value = "/{profileId}/users/{userId}/edit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findProfileInfoToEditByProfileIdAndUserId(@Parameter(description = "This parameter represents id of profile") @PathVariable("profileId") UUID profileId,
                                                                       @Parameter(description = "This parameter represents id of user") @PathVariable("userId") UUID userId) {
        ProfileInfoEditDto profileInfoEdit = profileService.findProfileInfoToEditByProfileIdAndUserId(profileId, userId);
        if (profileInfoEdit != null) {
            return new ResponseEntity<>(profileInfoEdit, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("There is no profile information to edit", HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Create the new profile")
    @ApiResponses(value = {
            @ApiResponse(description = "Return the success message if the profile is created", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) }, responseCode = "201"),
            @ApiResponse(description = "Return the error if the profile is not created", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) }, responseCode = "400")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> save(@Parameter(description = "This parameter represents the encoded data for saving the profile") @RequestBody String encodedProfile) {
        try {
            ProfileDto profileDto = (ProfileDto) resourceUtil.convertFromString(encodedProfile);
            profileService.createProfile(profileDto);
            return new ResponseEntity<>("Profile is saved successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Profile is not saved", HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Find the profile avatar by id of profile")
    @ApiResponses(value = {
            @ApiResponse(description = "Return the profile avatar", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) }, responseCode = "200"),
            @ApiResponse(description = "Return the error if the profile avatar does not exist", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) }, responseCode = "404")
    })
    @GetMapping(value = "/{profileId}/avatar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> findAvatarById(@Parameter(description = "This parameter represents id of profile") @PathVariable("profileId") UUID profileId) {
        String profileAvatar = profileService.findAvatarByProfileId(profileId);
        if (profileAvatar != null) {
            return new ResponseEntity<>(profileAvatar, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Profile avatar does not exist", HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Update the profile avatar")
    @ApiResponses(value = {
            @ApiResponse(description = "Return the success message if the profile avatar is updated", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) }, responseCode = "200"),
            @ApiResponse(description = "Return the error message if the profile avatar is not updated", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) }, responseCode = "400")
    })
    @PutMapping(value = "/{profileId}/avatar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateAvatar(@Parameter(description = "This parameter represents id of profile") @PathVariable("profileId") UUID profileId,
                                               @Parameter(description = "This parameter represents the user's photo to update the profile avatar") @RequestBody MultipartFile multipartFile) {
        try {
            profileService.updateAvatarProfile(profileId, multipartFile);
            return new ResponseEntity<>("Profile avatar is updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Profile avatar is not updated", HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Delete the profile avatar")
    @ApiResponses(value = {
            @ApiResponse(description = "Return the success message if the profile avatar is deleted", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) }, responseCode = "200"),
            @ApiResponse(description = "Return the error message if the profile avatar is not deleted", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) }, responseCode = "400")
    })
    @DeleteMapping(value = "/{profileId}/avatar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteAvatar(@Parameter(description = "This parameter represents id of profile") @PathVariable("profileId") UUID profileId) {
        try {
            profileService.setDefaultAvatar(profileId);
            return new ResponseEntity<>("Profile avatar is deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Profile avatar is not deleted", HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Change the profile status")
    @ApiResponses(value = {
            @ApiResponse(description = "Return the success message if the profile status is changed", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) }, responseCode = "200"),
            @ApiResponse(description = "Return the error message if the profile status is not changed", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) }, responseCode = "400")
    })
    @PutMapping(value = "/{profileId}/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> changeStatus(@Parameter(description = "This parameter represents id of profile") @PathVariable("profileId") UUID profileId,
                                               @Parameter(description = "This parameter represents whether the profile is active or not") @RequestParam("isActive") boolean isActive) {
        if (profileService.changeStatus(profileId, isActive)) {
            return new ResponseEntity<>("Status is changed successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Status is not changed", HttpStatus.BAD_REQUEST);
        }
    }
}