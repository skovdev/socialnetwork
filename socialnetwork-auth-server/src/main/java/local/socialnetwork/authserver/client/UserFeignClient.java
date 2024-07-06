package local.socialnetwork.authserver.client;

import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(value = "user-service", url = "${sn.feign.client.user-service.url}")
public interface UserFeignClient {
    @GetMapping(value = "/users/{authUserId}/id", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    UUID findUserIdByAuthUserId(@PathVariable("authUserId") UUID authUserId);
}