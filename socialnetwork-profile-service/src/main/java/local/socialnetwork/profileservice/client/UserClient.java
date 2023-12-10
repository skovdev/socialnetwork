package local.socialnetwork.profileservice.client;

import local.socialnetwork.profileservice.model.dto.user.UserDto;

import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(value = "user-service", url = "${sn.feignclient.userservice.url}")
public interface UserClient {
    @GetMapping("/users/{userId}")
    UserDto findUserByUserId(@PathVariable("userId") UUID userId);
}
