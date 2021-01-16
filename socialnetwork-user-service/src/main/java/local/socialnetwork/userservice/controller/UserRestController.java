package local.socialnetwork.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import local.socialnetwork.userservice.model.entity.user.CustomUser;

import local.socialnetwork.userservice.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "UserRestController")
@RestController
@RequestMapping("/users")
public class UserRestController {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

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
}