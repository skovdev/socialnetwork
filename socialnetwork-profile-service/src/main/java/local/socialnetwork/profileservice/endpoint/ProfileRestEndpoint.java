package local.socialnetwork.profileservice.endpoint;

import local.socialnetwork.profileservice.model.dto.profile.ChangePasswordDto;
import local.socialnetwork.profileservice.model.dto.profile.EditProfileDto;
import local.socialnetwork.profileservice.model.dto.profile.ProfileDto;

import local.socialnetwork.profileservice.service.ProfileCommandService;
import local.socialnetwork.profileservice.service.ProfileQueryService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

import java.util.List;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/profiles")
public class ProfileRestEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileRestEndpoint.class);

    private ProfileCommandService profileCommandService;

    @Autowired
    public void setProfileCommandService(ProfileCommandService profileCommandService) {
        this.profileCommandService = profileCommandService;
    }

    private ProfileQueryService profileQueryService;

    @Autowired
    public void setProfileQueryService(ProfileQueryService profileQueryService) {
        this.profileQueryService = profileQueryService;
    }

    @GetMapping
    public List<ProfileDto> findAll() throws ExecutionException, InterruptedException {
        return profileQueryService.findAll();
    }

    @GetMapping("/user")
    public ProfileDto findByUserId(@RequestParam("userId") UUID id) throws ExecutionException, InterruptedException {
        return profileQueryService.findByUserId(id);
    }

    @GetMapping("/user/{username}")
    public ProfileDto findByUsername(@PathVariable("username") String username) throws ExecutionException, InterruptedException {
        return profileQueryService.findByUsername(username);
    }

    @GetMapping("/user/{username}/edit")
    public EditProfileDto findEditProfileByUsername(@PathVariable("username") String username) throws ExecutionException, InterruptedException {
        return profileQueryService.findEditProfileByUsername(username);
    }

    @GetMapping("/avatar")
    public String findAvatarByUsername(@RequestParam("username") String username) throws ExecutionException, InterruptedException {
        return profileQueryService.findAvatarByUsername(username);
    }

    @PostMapping
    public ResponseEntity<String> save(@RequestBody ProfileDto profileDto) {

        if (profileDto != null) {
            profileCommandService.createProfile(profileDto);
            return new ResponseEntity<>("Profile has saved successfully", HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable("id") UUID id, @RequestBody EditProfileDto editProfileDto) {
        profileCommandService.updateProfile(id, editProfileDto);
        return new ResponseEntity<>("Profile with ID: " + id + " has updated successfully", HttpStatus.OK);
    }

    @PutMapping("/avatar")
    public ResponseEntity<String> updateAvatar(@RequestParam("username") String username,  @RequestParam("profileAvatar") MultipartFile multipartFile) {
        profileCommandService.updateAvatarProfile(username, multipartFile);
        return new ResponseEntity<>("Avatar has updated successfully", HttpStatus.OK);
    }

    @DeleteMapping("/avatar")
    public ResponseEntity<String> deleteAvatar(@RequestParam("username") String username) {
        return new ResponseEntity<>(profileCommandService.setDefaultAvatar(username), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<String> changeStatus(@RequestParam("username") String username, boolean isActive) {

        if (profileCommandService.changeStatus(username, isActive)) {
            return new ResponseEntity<>("Status has changed successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/password/change")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordDto changePasswordDto) {

        if (profileCommandService.checkIfValidOldPassword(changePasswordDto)) {
            profileCommandService.changePassword(changePasswordDto.getUsername(), changePasswordDto.getNewPassword());
            return new ResponseEntity<>("Password has changed successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Old password is not matched", HttpStatus.NOT_FOUND);
        }
    }
}