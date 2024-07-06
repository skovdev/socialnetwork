package local.socialnetwork.authserver.client;

import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(value = "profile-service", url = "${sn.feign.client.profile-service.url}")
public interface ProfileFeignClient {
    @GetMapping(value = "/profiles/{userId}/id", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    UUID findProfileIdByUserId(@PathVariable("userId") UUID userId);
}
