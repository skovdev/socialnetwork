package local.socialnetwork.authservice.client;

import local.socialnetwork.authservice.constants.ApplicationConstants;

import local.socialnetwork.authservice.model.User;

import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = ApplicationConstants.HOST + "/" + ApplicationConstants.Services.USER_SERVICE_URL)
public interface UserServiceProxy {

    @GetMapping("/users/{username}")
    User findUserByUsername(@PathVariable String username);

}