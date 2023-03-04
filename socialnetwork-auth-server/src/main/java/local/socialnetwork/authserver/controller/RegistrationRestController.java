package local.socialnetwork.authserver.controller;

import local.socialnetwork.authserver.dto.RegistrationDTO;

import local.socialnetwork.authserver.model.entity.User;

import local.socialnetwork.authserver.service.UserService;

import lombok.AccessLevel;

import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/registration")
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class RegistrationRestController {

    final UserService userService;

    @PostMapping
    public ResponseEntity<String> registration(@RequestBody RegistrationDTO registrationDTO) {
        Optional<User> user = userService.findByUsername(registrationDTO.getUsername());
        if (user.isPresent() && user.get().getUsername().equalsIgnoreCase(registrationDTO.getUsername())) {
            log.info("User {} with such of username already exists in database", user.get().getUsername());
            return ResponseEntity.ok("User is exists in databases");
        } else {
            userService.registration(registrationDTO);
            log.info("User has registered successfully");
            return ResponseEntity.ok("Registration has passed successfully");
        }
    }
}