package local.socialnetwork.profileservice.controller;

import local.socialnetwork.profileservice.client.user.UserProxyService;

import local.socialnetwork.profileservice.model.dto.profile.EditProfileDto;
import local.socialnetwork.profileservice.model.dto.profile.ProfileDto;

import local.socialnetwork.profileservice.model.dto.user.UserDetailsDto;
import local.socialnetwork.profileservice.model.dto.user.UserDto;

import local.socialnetwork.profileservice.model.entity.profile.Profile;

import local.socialnetwork.profileservice.exception.ProfileServiceException;

import local.socialnetwork.profileservice.model.http.ApiResponse;

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

    @GetMapping
    public List<ProfileDto> findAll() {
        return profileService.findAll();
    }

    @PostMapping
    public ResponseEntity<ApiResponse> save(@RequestBody Profile profile) {

        if (profile != null) {
            profileService.save(profile);
            return new ResponseEntity<>(new ApiResponse("Profile has saved successfully"), HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user")
    public Profile   findByUserId(@RequestParam("userId") UUID userId) {
        return profileService.findProfileByUserId(userId);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<ProfileDto> findProfileByUsername(@PathVariable("username") String username) {

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

    @GetMapping("/user/{username}/edit")
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
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/avatar")
    public ResponseEntity<String> getAvatar(@RequestParam("username") String username) {

        var user = profileService.findProfileByUsername(username);

        if (user != null) {
            return new ResponseEntity<>(user.getAvatar(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/avatar")
    public ResponseEntity<ApiResponse> updateAvatar(@RequestParam("username") String username, @RequestParam("profileAvatar") MultipartFile multipartFile) {

        try {
            profileService.updateAvatarProfile(username, multipartFile);
            return new ResponseEntity<>(new ApiResponse("Avatar has updated successfully"), HttpStatus.OK);
        } catch (ProfileServiceException | IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/avatar")
    public ResponseEntity<String> deleteAvatar(@RequestParam("username") String username) {
        try {
            return new ResponseEntity<>(profileService.setDefaultAvatar(username), HttpStatus.OK);
        } catch (IOException | ProfileServiceException  e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping
    public ResponseEntity<ApiResponse> changeStatus(@RequestParam("username") String username, @RequestParam("isActive") boolean isActive) {

        if (profileService.changeStatus(username, isActive)) {
            return new ResponseEntity<>(new ApiResponse("Status has changed successfully"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}