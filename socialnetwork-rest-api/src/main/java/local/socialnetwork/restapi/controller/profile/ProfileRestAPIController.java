package local.socialnetwork.restapi.controller.profile;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;

import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

import io.swagger.v3.oas.annotations.servers.Server;

import local.socialnetwork.restapi.model.profile.dto.ProfileDTO;

import local.socialnetwork.restapi.service.profile.ProfileAPIService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@OpenAPIDefinition(
        info = @Info(
                title = "Profile REST API",
                contact = @Contact(
                        name = "Stanislav",
                        url = "https://skov.dev",
                        email = "mail@skov.dev"
                )
        ),
         servers = @Server(
                 url = "http://localhost:8080"
         )
)
@RestController
@RequestMapping(value = "/api/v1/profiles")
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ProfileRestAPIController {

    final ProfileAPIService profileAPIService;

    /**
     * The method returns profile info by profile id
     * @param profileId - Profile ID
     * @return ProfileDTO
     */
    @GetMapping(value = "/{profileId}")
    public ResponseEntity<ProfileDTO> getProfileInfo(@PathVariable String profileId) {
        ProfileDTO profile = profileAPIService.getProfileInfoByProfileId(profileId);
        return ResponseEntity.ok().body(profile);
    }
}