package local.socialnetwork.userservice.controller;

import local.socialnetwork.userservice.model.dto.RegistrationDto;

import local.socialnetwork.userservice.model.user.CustomUser;

import local.socialnetwork.userservice.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/registration")
public class RegistrationRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationRestController.class);

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public boolean registration(@RequestBody RegistrationDto registrationDTO) throws IOException {

        Optional<CustomUser> user = userService.findByUsername(registrationDTO.getUsername());

        if (user.isPresent() && user.get().getUsername().equalsIgnoreCase(registrationDTO.getUsername())) {
            LOGGER.info("This is user with such of username already exist in database");
            return false;
        } else {
            userService.registration(registrationDTO);
            LOGGER.info("New user has successfully registered");
            return true;
        }
    }
}
