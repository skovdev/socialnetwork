package local.socialnetwork.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.Parameter;

import io.swagger.v3.oas.annotations.media.Content;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import io.swagger.v3.oas.annotations.tags.Tag;

import local.socialnetwork.userservice.aspect.annotation.LogMethodController;

import local.socialnetwork.userservice.constant.VersionAPI;

import local.socialnetwork.userservice.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Controller class responsible for handling user-related CRUD operations.
 *
 * @author Stanislav Kovalenko
 */
@Tag(name = "UserRestController", description = "Controller for processing user-related CRUD operations")
@Slf4j
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequestMapping(VersionAPI.API_V1 + "/users")
@RequiredArgsConstructor
public class UserRestController {

    /**
     * Service layer dependency for user-related operations.
     */
    final UserService userService;

    /**
     * Endpoint to find a user by their unique identifier.
     *
     * @param userId The unique identifier of the user.
     * @return A ResponseEntity indicating the outcome of the user retrieval process.
     *         Returns OK (200) with the found user if the user exists,
     *         or NOT FOUND (404) if the user does not exist.
     */
    @LogMethodController
    @Operation(summary = "Find the user by id of user")
    @ApiResponses(value = {
            @ApiResponse(description = "Return a found user", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) }, responseCode = "200"),
            @ApiResponse(description = "Return error message if user does not exist", responseCode = "404")
    })
    @GetMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findByUserId(@Parameter(description = "This parameter represents id of user") @PathVariable("userId") UUID userId) {
        return userService.findById(userId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}