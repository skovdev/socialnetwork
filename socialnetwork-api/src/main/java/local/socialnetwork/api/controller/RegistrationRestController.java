package local.socialnetwork.api.controller;

import local.socialnetwork.core.service.UserService;

import local.socialnetwork.model.user.CustomUser;

import local.socialnetwork.model.dto.RegistrationDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;

@RestController
@RequestMapping(value = "/api/v1/registration")
public class RegistrationRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationRestController.class);

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public boolean registration(@RequestBody RegistrationDto registrationDTO) throws IOException {

        CustomUser customUser = userService.findByName(registrationDTO.getUsername());

        if (customUser != null && customUser.getUsername().equalsIgnoreCase(registrationDTO.getUsername())) {
            LOGGER.info("This is user with such of username already exist in database");
            return false;
        } else {
            userService.registration(registrationDTO);
            LOGGER.info("New user has successfully registered");
            return true;
        }
    }
}