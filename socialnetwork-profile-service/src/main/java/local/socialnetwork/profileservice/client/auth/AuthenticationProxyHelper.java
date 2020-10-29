package local.socialnetwork.profileservice.client.auth;

import local.socialnetwork.profileservice.constants.ApplicationConstants;

import local.socialnetwork.profileservice.model.dto.user.UserDto;

import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "auth-service", url = ApplicationConstants.HOST + "/" + ApplicationConstants.Services.AUTH_SERVICE_URL)
public interface AuthenticationProxyHelper {

    @RequestMapping(value = "/auth/current", method = RequestMethod.GET)
    UserDto getCurrent();

}
