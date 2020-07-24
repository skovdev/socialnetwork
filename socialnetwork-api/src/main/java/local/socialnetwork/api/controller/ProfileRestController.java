package local.socialnetwork.api.controller;

import local.socialnetwork.common.auth.AuthenticationHelper;

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
@RequestMapping(value = "/api/v1/profile")
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

    @GetMapping("/test")
    public Profile findByUserId(@RequestParam("userId") UUID userId) {
        return profileService.findProfileByUserId(userId);
    }

    @GetMapping("/{username}")
    public Profile findProfileByUsername(@PathVariable("username") String username) {

        CustomUser authenticatedUser = authenticationHelper.getAuthenticatedUser();

        if (authenticatedUser != null && authenticatedUser.getUsername().equalsIgnoreCase(username)) {
            return authenticatedUser.getProfile();
        }

        return null;

    }

    @GetMapping("/{username}/edit")
    public EditProfileDto editProfileByUsername(@PathVariable("username") String username) {
        return profileService.editProfileByUsername(username);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable("id") UUID id, @RequestBody EditProfileDto editProfileDto) {

        var profile = profileService.findProfileByUserId(id);

        if (profile != null) {
            profileService.update(profile, editProfileDto);
            return new ResponseEntity<>(new ApiResponse("Profile with ID: " + profile.getId() + " has updated successfully"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse("Profile is not found"), HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping("/avatar")
    public String getAvatar() {

        CustomUser customUser = authenticationHelper.getAuthenticatedUser();

        if (customUser != null) {
            return customUser.getProfile().getAvatar();
        }

        return null;

    }

    @PostMapping("/avatar")
    public ResponseEntity<ApiResponse> updateAvatar(@RequestParam("profileAvatar") MultipartFile multipartFile) throws IOException {
        profileService.updateAvatarProfile(multipartFile);
        return new ResponseEntity<>(new ApiResponse("Avatar has updated successfully"), HttpStatus.OK);
    }

    @DeleteMapping("/avatar")
    public String defaultAvatar(@RequestParam("username") String username) throws IOException {
        return profileService.setDefaultAvatar(username);
    }

    @PutMapping
    public ResponseEntity<ApiResponse> changeStatus(@RequestParam("username") String username, @RequestParam("isActive") boolean isActive) {
        profileService.changeStatus(username, isActive);
        return new ResponseEntity<>(new ApiResponse("Status has changed successfully"), HttpStatus.OK);
    }
}