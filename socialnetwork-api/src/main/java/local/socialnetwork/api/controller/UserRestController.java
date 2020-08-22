package local.socialnetwork.api.controller;

import local.socialnetwork.api.exception.InvalidOldPasswordException;

import local.socialnetwork.core.service.UserService;

import local.socialnetwork.model.dto.ChangePasswordDto;

import local.socialnetwork.model.http.ApiResponse;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class UserRestController {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse> changePassword(@RequestBody ChangePasswordDto changePasswordDto) {

        if (!userService.checkIfValidOldPassword(changePasswordDto)) {
            throw new InvalidOldPasswordException("Old password is not valid");
        }

        userService.changePassword(changePasswordDto);

        return new ResponseEntity<>(new ApiResponse("Password has updated successfully"), HttpStatus.OK);

    }
}