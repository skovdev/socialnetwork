package local.socialnetwork.profileservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import io.swagger.v3.oas.annotations.media.Content;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import io.swagger.v3.oas.annotations.tags.Tag;

import local.socialnetwork.profileservice.constant.VersionAPI;

import local.socialnetwork.profileservice.dto.profile.ProfileDto;

import local.socialnetwork.profileservice.service.ProfileService;

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

/**
 * Controller class responsible for handling profile-related CRUD operations.
 *
 * @author Stanislav Kovalenko
 */
@Tag(name = "ProfileRestController", description = "Controller for processing profile-related CRUD operations")
@Slf4j
@RestController
@RequestMapping(VersionAPI.API_V1 + "/profiles")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ProfileRestController {

    /**
     * Service layer dependency for profile-related operations.
     */
    ProfileService profileService;

    /**
     * Fetches all profiles
     *
     * @return A List of {@link ProfileDto}. If the database contains profiles, returns a non-empty list.
     *         Returns an empty list if no profiles are found.
     */
    @Operation(summary = "Find all the profiles")
    @ApiResponses(value = {
            @ApiResponse(description = "Return all found the profiles", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) }, responseCode = "200"),
            @ApiResponse(description = "Return an empty list if the profiles do not exist", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) }, responseCode = "200")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ProfileDto> findAll() {
        List<ProfileDto> profiles = profileService.findAll();
        if (profiles != null && !profiles.isEmpty()) {
            return profiles;
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Finds a specific profile by its ID.
     *
     * @param profileId The ID of the profile to find.
     * @return A ResponseEntity indicating the outcome of the profile retrieval process.
     *         Returns OK (200) with the found ProfileDto if the profile exists,
     *         or NOT FOUND (404) if the profile does not exist.
     */
    @Operation(summary = "Find the profile by id of profile")
    @ApiResponses(value = {
            @ApiResponse(description = "Return found the profile", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) }, responseCode = "200"),
            @ApiResponse(description = "Return the error if the profile does not exist", responseCode = "404")
    })
    @GetMapping(value = "/{profileId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findByProfileId(@Parameter(description = "This parameter represents id of profile") @PathVariable("profileId") UUID profileId) {
        return profileService.findById(profileId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Finds profile information based on profile ID and user ID.
     *
     * @param profileId The ID of the profile.
     * @param userId The ID of the user.
     * @return A ResponseEntity indicating the outcome of the profile info retrieval process.
     *         Returns OK (200) with profile information if found,
     *         or NOT FOUND (404) if the information does not exist.
     */
    @Operation(summary = "Find the profile info by id of profile and id of user")
    @ApiResponses(value = {
            @ApiResponse(description = "Return found the profile info", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) }, responseCode = "200"),
            @ApiResponse(description = "Return the error if the profile info does not exist", responseCode = "404")
    })
    @GetMapping(value = "/{profileId}/users/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findProfileInfoByProfileIdAndUserId(@Parameter(description = "This parameter represents id of profile") @PathVariable("profileId") UUID profileId,
                                                                 @Parameter(description = "This parameter represents id of user") @PathVariable("userId") UUID userId) {
        return profileService.findProfileInfoByProfileIdAndUserId(profileId, userId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Finds profile information for editing based on profile ID and user ID.
     *
     * @param profileId The ID of the profile.
     * @param userId The ID of the user.
     * @return A ResponseEntity indicating the outcome of the editable profile info retrieval process.
     *         Returns OK (200) with editable profile information if found,
     *         or NOT FOUND (404) if the information does not exist.
     */
    @Operation(summary = "Find the profile info to edit by id of profile and id of user")
    @ApiResponses(value = {
            @ApiResponse(description = "Return found the profile info to edit", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) }, responseCode = "200"),
            @ApiResponse(description = "Return the error if the profile info to be edited does not exist", responseCode = "404")
    })
    @GetMapping(value = "/{profileId}/users/{userId}/edit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findProfileInfoToEditByProfileIdAndUserId(@Parameter(description = "This parameter represents id of profile") @PathVariable("profileId") UUID profileId,
                                                                       @Parameter(description = "This parameter represents id of user") @PathVariable("userId") UUID userId) {
        return profileService.findProfileInfoToEditByProfileIdAndUserId(profileId, userId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Finds a specific profile identifier by user identifier.
     *
     * @param userId The user unique identifier.
     * @return A ResponseEntity indicating the outcome of the profile identifier retrieval process.
     *         Returns OK (200) with the found profile identifier if the profile identifier exists,
     *         or NOT FOUND (404) if the profile identifier does not exist.
     */
    @Operation(summary = "Find the profile identifier by the user identifier")
    @ApiResponses(value = {
            @ApiResponse(description = "Return found the profile identifier", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) }, responseCode = "200"),
            @ApiResponse(description = "Return the error if the profile identifier does not exist", responseCode = "404")
    })
    @GetMapping(value = "/{userId}/id", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findProfileIdByUserId(@Parameter(description = "This parameter represents the user identifier")
                                                     @PathVariable("userId") UUID userId) {
        return profileService.findProfileIdByUserId(userId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    /**
     * Creates a new profile with the given data.
     *
     * @param profileDto Data transfer object containing the profile information to be saved.
     * @return A ResponseEntity indicating the outcome of the profile creation process.
     *         Returns CREATED (201) with a success message if the profile is created successfully,
     *         or BAD REQUEST (400) if the profile cannot be created.
     */
    @Operation(summary = "Create the new profile")
    @ApiResponses(value = {
            @ApiResponse(description = "Return the success message if the profile is created", responseCode = "201"),
            @ApiResponse(description = "Return the error if the profile is not created", responseCode = "400")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> save(@Parameter(description = "This parameter represents the data for saving the profile")
                                           @RequestBody ProfileDto profileDto) {
        try {
            profileService.save(profileDto);
            return ResponseEntity.status(HttpStatus.CREATED).body("Profile is saved successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Profile is not saved");
        }
    }

    /**
     * Finds the avatar for a specific profile by its ID.
     *
     * @param profileId The ID of the profile to find the avatar for.
     * @return A ResponseEntity indicating the outcome of the avatar retrieval process.
     *         Returns OK (200) with the avatar if found,
     *         or NOT FOUND (404) if the avatar does not exist.
     */
    @Operation(summary = "Find the profile avatar by id of profile")
    @ApiResponses(value = {
            @ApiResponse(description = "Return the profile avatar", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) }, responseCode = "200"),
            @ApiResponse(description = "Return the error if the profile avatar does not exist", responseCode = "404")
    })
    @GetMapping(value = "/{profileId}/avatar", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> findAvatarById(@Parameter(description = "This parameter represents id of profile") @PathVariable("profileId") UUID profileId) {
        return profileService.findAvatarById(profileId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());

    }

    /**
     * Updates the avatar for a specific profile.
     *
     * @param profileId The ID of the profile for which to update the avatar.
     * @param multipartFile The new avatar file as a MultipartFile.
     * @return A ResponseEntity indicating the outcome of the avatar update process.
     *         Returns OK (200) with a success message if the avatar is updated successfully,
     *         or BAD REQUEST (400) if the update fails.
     */
    @Operation(summary = "Update the profile avatar")
    @ApiResponses(value = {
            @ApiResponse(description = "Return the success message if the profile avatar is updated", responseCode = "200"),
            @ApiResponse(description = "Return the error message if the profile avatar is not updated", responseCode = "400")
    })
    @PutMapping(value = "/{profileId}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateAvatar(@Parameter(description = "This parameter represents id of profile") @PathVariable("profileId") UUID profileId,
                                               @Parameter(description = "This parameter represents the user's photo to update the profile avatar") @RequestParam("file") MultipartFile multipartFile) {
        try {
            profileService.updateAvatarProfile(profileId, multipartFile);
            return ResponseEntity.ok("Profile avatar is updated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Profile avatar is not updated");
        }
    }

    /**
     * Deletes the avatar for a specific profile by its ID.
     *
     * @param profileId The ID of the profile for which to delete the avatar.
     * @return A ResponseEntity indicating the outcome of the avatar deletion process.
     *         Returns OK (200) with a success message if the avatar is deleted successfully,
     *         or NOT FOUND (404) if the avatar does not exist.
     */
    @Operation(summary = "Delete the profile avatar")
    @ApiResponses(value = {
            @ApiResponse(description = "Return the success message if the profile avatar is deleted", responseCode = "200"),
            @ApiResponse(description = "Return the error message if the profile avatar is not deleted", responseCode = "400")
    })
    @DeleteMapping(value = "/{profileId}/avatar", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteAvatar(@Parameter(description = "This parameter represents id of profile") @PathVariable("profileId") UUID profileId) {
        try {
            profileService.setDefaultAvatar(profileId);
            return ResponseEntity.ok("Profile avatar is deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Profile avatar is not deleted");
        }
    }

    /**
     * Changes the status of a profile.
     *
     * @param profileId The ID of the profile for which to change the status.
     * @param isActive The new status to be applied to the profile.
     * @return A ResponseEntity indicating the outcome of the status change process.
     *         Returns OK (200) with a success message if the status is changed successfully,
     *         or NOT FOUND (404) if the profile does not exist.
     */
    @Operation(summary = "Change the profile status")
    @ApiResponses(value = {
            @ApiResponse(description = "Return the success message if the profile status is changed", responseCode = "200"),
            @ApiResponse(description = "Return the error message if the profile status is not changed", responseCode = "400")
    })
    @PutMapping(value = "/{profileId}/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> changeStatus(@Parameter(description = "This parameter represents id of profile") @PathVariable("profileId") UUID profileId,
                                               @Parameter(description = "This parameter represents whether the profile is active or not") @RequestParam("isActive") boolean isActive) {
        return profileService.changeStatus(profileId, isActive) ?
                ResponseEntity.ok("Status is changed successfully") :
                ResponseEntity.badRequest().body("Status is not changed");
    }
}