package local.socialnetwork.profileservice.controller;

import local.socialnetwork.profileservice.exception.ProfileServiceException;

import local.socialnetwork.profileservice.model.dto.profile.ChangePasswordDto;
import local.socialnetwork.profileservice.model.dto.profile.EditProfileDto;
import local.socialnetwork.profileservice.model.dto.profile.ProfileDto;

import local.socialnetwork.profileservice.model.dto.profile.ProfileInfoDto;

import local.socialnetwork.profileservice.service.ProfileService;

import local.socialnetwork.profileservice.util.ResourceUtil;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import lombok.experimental.FieldDefaults;

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

@RestController
@RequestMapping("/profiles")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ProfileRestController {

    ProfileService profileService;
    ResourceUtil resourceUtil;

    @GetMapping
    public List<ProfileDto> findAll() {
        return profileService.findAll();
    }

    @GetMapping("/{profileId}")
    public ProfileDto findByProfileId(@PathVariable("profileId") UUID profileId) {
        return profileService.findByProfileId(profileId);
    }

    @GetMapping("/user")
    public ProfileDto findByUserId(@RequestParam("userId") UUID id) {
        return profileService.findByUserId(id);
    }

    @GetMapping("/user/{username}")
    public ProfileInfoDto findByUsername(@PathVariable("username") String username) {
        return profileService.findByUsername(username);
    }

    @GetMapping("/user/{username}/edit")
    public EditProfileDto findEditProfileByUsername(@PathVariable("username") String username) {
        return profileService.findEditProfileByUsername(username);
    }

    @GetMapping("/avatar")
    public String findAvatarByUsername(@RequestParam("username") String username) {
        return profileService.findAvatarByUsername(username);
    }

    @PostMapping
    public ResponseEntity<String> save(@RequestBody String encodedProfile) throws IOException, ClassNotFoundException {

        if (encodedProfile != null) {

            ProfileDto profileDto = (ProfileDto) resourceUtil.convertFromString(encodedProfile);
            profileService.createProfile(profileDto);
            return new ResponseEntity<>("Profile has saved successfully", HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable("id") UUID id, @RequestBody EditProfileDto editProfileDto) throws ProfileServiceException {
        profileService.updateProfile(id, editProfileDto);
        return new ResponseEntity<>("Profile with ID: " + id + " has updated successfully", HttpStatus.OK);
    }

    @PutMapping("/avatar")
    public ResponseEntity<String> updateAvatar(@RequestParam("username") String username,  @RequestParam("profileAvatar") MultipartFile multipartFile) throws ProfileServiceException, IOException {
        profileService.updateAvatarProfile(username, multipartFile);
        return new ResponseEntity<>("Avatar has updated successfully", HttpStatus.OK);
    }

    @DeleteMapping("/avatar")
    public ResponseEntity<String> deleteAvatar(@RequestParam("username") String username) throws ProfileServiceException, IOException {
        return new ResponseEntity<>(profileService.setDefaultAvatar(username), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<String> changeStatus(@RequestParam("username") String username, boolean isActive) {

        if (profileService.changeStatus(username, isActive)) {
            return new ResponseEntity<>("Status has changed successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/password/change")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordDto changePasswordDto) {

        if (profileService.checkIfValidOldPassword(changePasswordDto)) {
            profileService.changePassword(changePasswordDto.getUsername(), changePasswordDto.getNewPassword());
            return new ResponseEntity<>("Password has changed successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Old password is not matched", HttpStatus.NOT_FOUND);
        }
    }
}