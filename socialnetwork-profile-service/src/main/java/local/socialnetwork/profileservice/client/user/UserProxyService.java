package local.socialnetwork.profileservice.client.user;

import local.socialnetwork.profileservice.constants.AppConstants;

import local.socialnetwork.profileservice.model.dto.user.UserDto;

import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "user-service", url = AppConstants.HOST + ":" + AppConstants.PORT)
public interface UserProxyService {

    @RequestMapping(value = "/users/{userId}", method = RequestMethod.GET)
    UserDto findUserByUserId(@PathVariable("userId") UUID userId);

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    UserDto findUserByUsername(@RequestParam("username") String username);

    @RequestMapping(value = "/users/password", method = RequestMethod.POST)
    void changePassword(@RequestParam("username") String username, @RequestParam("newPassword") String newPassword);
}