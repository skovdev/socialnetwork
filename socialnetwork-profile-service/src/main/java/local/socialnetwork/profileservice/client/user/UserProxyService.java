package local.socialnetwork.profileservice.client.user;

import local.socialnetwork.profileservice.constants.ApplicationConstants;

import local.socialnetwork.profileservice.model.dto.profile.EditProfileDto;
import local.socialnetwork.profileservice.model.dto.user.UserDto;

import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "user-service", url = ApplicationConstants.HOST + "/" + ApplicationConstants.Services.USER_SERVICE_URL)
public interface UserProxyService {

    @RequestMapping(value = "/users/{userId}", method = RequestMethod.GET)
    UserDto findUserByUserId(@PathVariable("userId") UUID userId);

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    UserDto findUserByUsername(@RequestParam("username") String username);

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    List<UserDto> findUserByFirstName(@RequestParam("firstName") String username);

    @RequestMapping(value = "/users/{id}", method = RequestMethod.PUT)
    void updateProfile(@PathVariable("id") UUID id, @RequestBody EditProfileDto editProfile);

    @RequestMapping(value = "/users", method = RequestMethod.DELETE)
    void delete(@RequestBody UserDto user);

}