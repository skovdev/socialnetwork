package local.socialnetwork.restapi.service.profile;

import local.socialnetwork.restapi.model.profile.dto.ProfileDTO;

import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "profile-service", url = "http://localhost:8081")
public interface ProfileAPIService {
    @RequestMapping(value = "/api/v1/profiles/{profileId}", produces = "application/json", consumes = "application/json")
    ProfileDTO getProfileInfoByProfileId(@PathVariable("profileId") String profileId);
}