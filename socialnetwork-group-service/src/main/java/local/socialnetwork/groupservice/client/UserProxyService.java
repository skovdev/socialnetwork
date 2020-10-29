package local.socialnetwork.groupservice.client;

import local.socialnetwork.groupservice.constants.ApplicationConstants;

import local.socialnetwork.groupservice.model.dto.user.UserDto;

import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "user-service", url = ApplicationConstants.HOST + "/" + ApplicationConstants.Services.USER_SERVICE_URL)
public interface UserProxyService {

    @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
    UserDto findUserById(@PathVariable("id") UUID id);

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    UserDto findUserByUsername(@RequestParam("username") String username);

}