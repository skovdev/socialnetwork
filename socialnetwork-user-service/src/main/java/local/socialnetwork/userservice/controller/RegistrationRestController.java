package local.socialnetwork.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.media.Content;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import local.socialnetwork.userservice.aspect.annotation.LogMethodController;

import local.socialnetwork.userservice.model.dto.RegistrationDto;

import local.socialnetwork.userservice.model.entity.user.CustomUser;

import local.socialnetwork.userservice.service.UserService;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/registration")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class RegistrationRestController {

    UserService userService;

    @LogMethodController
    @Operation(summary = "Create a new user")
    @ApiResponses(value = {
            @ApiResponse(description = "User has created successfully", content = { @Content(mediaType = "application/json") }),
            @ApiResponse(description = "User has not created")
    })
    @PostMapping
    public boolean registration(@RequestBody RegistrationDto registrationDTO) throws IOException {

        Optional<CustomUser> user = userService.findByUsername(registrationDTO.getUsername());

        if (user.isPresent() && user.get().getUsername().equalsIgnoreCase(registrationDTO.getUsername())) {
            log.info("This is user with such of username already exist in database");
            return false;
        } else {
            userService.registration(registrationDTO);
            log.info("New user has successfully registered");
            return true;
        }
    }
}
