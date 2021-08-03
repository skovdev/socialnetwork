package local.socialnetwork.userservice.client;

import local.socialnetwork.userservice.configuration.FromUrlEncodedClientConfiguration;

import local.socialnetwork.userservice.constants.ApplicationConstants;

import local.socialnetwork.userservice.model.dto.profile.ProfileDto;

import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(
        name = "profile-service",
        url = ApplicationConstants.HOST + ":" + ApplicationConstants.PORT + "/" + ApplicationConstants.Services.PROFILE_SERVICE_URL,
        configuration = FromUrlEncodedClientConfiguration.class
)
public interface ProfileProxyService {

    @RequestMapping(value = "/profiles", method = RequestMethod.POST)
    void save(@RequestBody ProfileDto profile);

}