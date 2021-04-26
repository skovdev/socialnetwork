package local.socialnetwork.profileservice.endpoint;

import local.socialnetwork.profileservice.model.dto.profile.ProfileDto;

import local.socialnetwork.profileservice.service.ProfileCommandService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/profiles")
public class ProfileRestEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileRestEndpoint.class);

    private ProfileCommandService profileCommandService;

    @Autowired
    public void setProfileCommandService(ProfileCommandService profileCommandService) {
        this.profileCommandService = profileCommandService;
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

    @PutMapping
    public ResponseEntity<String> changeStatus(@RequestParam("username") String username, boolean isActive) {
        return null;
    }
}