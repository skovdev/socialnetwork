package local.socialnetwork.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import io.swagger.v3.oas.annotations.media.Content;

import io.swagger.v3.oas.annotations.responses.ApiResponse;

import io.swagger.v3.oas.annotations.tags.Tag;

import local.socialnetwork.userservice.model.entity.user.CustomUser;

import local.socialnetwork.userservice.service.UserService;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "UserRestController")
@RestController
@RequestMapping("/users")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class UserRestController {

    UserService userService;

    @Operation(summary = "Get the user by id")
    @ApiResponse(description = "Found the user by id", content = { @Content(mediaType = "application/json") }, responseCode = "200")
    @GetMapping("/{id}")
    public ResponseEntity<CustomUser> findById(@PathVariable("id") @Parameter(description = "Id of user for getting of user by id") UUID id) {
        CustomUser user = userService.findById(id).orElseThrow(NullPointerException::new);
        return ResponseEntity.ok().body(user);
    }

    @Operation(summary = "Get the user by username")
    @ApiResponse(description = "Found the user by username", content = { @Content(mediaType = "application/json") }, responseCode = "200")
    @GetMapping
    public ResponseEntity<CustomUser> findByUsername(@RequestParam("username") @Parameter(description = "Username of user for getting of user by username") String username) {
        CustomUser user = userService.findByUsername(username).orElseThrow(NullPointerException::new);
        return ResponseEntity.ok().body(user);
    }

    @Operation(summary = "Change user password by username")
    @ApiResponse(description = "Change user password by username", content = { @Content(mediaType = "application/json") }, responseCode = "200")
    @PostMapping("/password")
    public ResponseEntity<String> changePassword(@RequestParam("username") @Parameter(description = "Username of user for changing password by username") String username, @RequestParam("newPassword") @Parameter(description = "New password of user for changing password by username") String newPassword) {
        userService.changePassword(username, newPassword);
        return new ResponseEntity<>("Password has changed successfully", HttpStatus.OK);
    }
}