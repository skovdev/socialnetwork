package local.socialnetwork.authservice.client;

import local.socialnetwork.authservice.constants.ApplicationConstants;

import local.socialnetwork.authservice.model.CustomUser;

import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", url = ApplicationConstants.HOST + "/" + ApplicationConstants.Services.USER_SERVICE_URL)
public interface UserProxyService {

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    CustomUser findUserByUsername(@RequestParam("username") String username);

}