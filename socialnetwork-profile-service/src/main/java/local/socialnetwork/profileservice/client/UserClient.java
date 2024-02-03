package local.socialnetwork.profileservice.client;

import local.socialnetwork.profileservice.model.dto.user.UserDto;

import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(value = "user-service", url = "${sn.feignclient.userservice.url}")
public interface UserClient {
    @GetMapping(value = "/users/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    UserDto findUserByUserId(@PathVariable("userId") UUID userId);
}
