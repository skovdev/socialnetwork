package local.socialnetwork.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.media.Content;

import io.swagger.v3.oas.annotations.responses.ApiResponse;

import io.swagger.v3.oas.annotations.tags.Tag;

import local.socialnetwork.userservice.aspect.annotation.LogMethodController;

import local.socialnetwork.userservice.model.dto.user.UserDto;

import local.socialnetwork.userservice.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "UserRestController")
@RestController
@RequestMapping("/users")
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class UserRestController {

    final UserService userService;

    @LogMethodController
    @Operation(summary = "Get the user by authUserId")
    @ApiResponse(description = "Found the user by authUserId", content = { @Content(mediaType = "application/json") }, responseCode = "200")
    @GetMapping("/{authUserId}")
    public ResponseEntity<UserDto> findByAuthUserId(@PathVariable("authUserId") UUID authUserId) {
        UserDto userDto = userService.findByAuthUserId(authUserId);
        return ResponseEntity.ok().body(userDto);
    }
}