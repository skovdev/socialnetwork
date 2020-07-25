package local.socialnetwork.api.controller;

import local.socialnetwork.common.auth.AuthenticationHelper;

import local.socialnetwork.core.exception.ProfileServiceException;

import local.socialnetwork.core.service.ProfileService;

import local.socialnetwork.model.dto.EditProfileDto;

import local.socialnetwork.model.user.CustomUser;

import local.socialnetwork.model.profile.Profile;

import local.socialnetwork.model.http.ApiResponse;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/profiles")
public class ProfileRestController {

    private ProfileService profileService;

    @Autowired
    public void setProfileService(ProfileService profileService) {
        this.profileService = profileService;
    }

    private AuthenticationHelper authenticationHelper;

    @Autowired
    public void setAuthenticationHelper(AuthenticationHelper authenticationHelper) {
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping
    public List<Profile> findAll() {
        return profileService.findAll();
    }

    @GetMapping("/user")
    public Profile findByUserId(@RequestParam("userId") UUID userId) {
        return profileService.findProfileByUserId(userId);
    }

    @GetMapping("/{username}")
    public ResponseEntity<Profile> findProfileByUsername(@PathVariable("username") String username) {

        CustomUser authenticatedUser = authenticationHelper.getAuthenticatedUser();

        if (authenticatedUser != null && authenticatedUser.getUsername().equalsIgnoreCase(username)) {
            return new ResponseEntity<>(authenticatedUser.getProfile(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{username}/edit")
    public ResponseEntity<EditProfileDto> editProfileByUsername(@PathVariable("username") String username) {

        try {
            return new ResponseEntity<>(profileService.editProfileByUsername(username), HttpStatus.OK);
        } catch (ProfileServiceException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable("id") UUID id, @RequestBody EditProfileDto editProfileDto) {

        try {
            profileService.update(id, editProfileDto);
            return new ResponseEntity<>(new ApiResponse("Profile with ID: " + id + " has updated successfully"), HttpStatus.OK);
        } catch (ProfileServiceException e) {
            return new ResponseEntity<>(new ApiResponse("Profile has not found"), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/avatar")
    public ResponseEntity<String> getAvatar() {

        CustomUser customUser = authenticationHelper.getAuthenticatedUser();

        if (customUser != null) {
            return new ResponseEntity<>(customUser.getProfile().getAvatar(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/avatar")
    public ResponseEntity<ApiResponse> updateAvatar(@RequestParam("username") String username, @RequestParam("profileAvatar") MultipartFile multipartFile) throws IOException {

        try {
            profileService.updateAvatarProfile(username, multipartFile);
            return new ResponseEntity<>(new ApiResponse("Avatar has updated successfully"), HttpStatus.OK);
        } catch (ProfileServiceException e) {
            return new ResponseEntity<>(new ApiResponse("Avatar has not updated"), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/avatar")
    public ResponseEntity<String> defaultAvatar(@RequestParam("username") String username) throws IOException {

        try {
            return new ResponseEntity<>(profileService.setDefaultAvatar(username), HttpStatus.OK);
        } catch (ProfileServiceException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping
    public ResponseEntity<ApiResponse> changeStatus(@RequestParam("username") String username, @RequestParam("isActive") boolean isActive) {

        if (profileService.changeStatus(username, isActive)) {
            return new ResponseEntity<>(new ApiResponse("Status has changed successfully"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse("Status has not changed"), HttpStatus.NOT_FOUND);
        }
    }
}