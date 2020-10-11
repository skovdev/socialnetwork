package local.socialnetwork.profileservice.controller;

import local.socialnetwork.profileservice.entity.profile.Profile;

import local.socialnetwork.profileservice.service.ProfileService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/profiles")
public class ProfileRestController {

    private ProfileService profileService;

    @Autowired
    public void setProfileService(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public List<Profile> findAll() {
        return profileService.findAll();
    }
}