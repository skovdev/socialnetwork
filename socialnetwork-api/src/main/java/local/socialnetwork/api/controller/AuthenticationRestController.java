package local.socialnetwork.api.controller;

import local.socialnetwork.api.config.jwt.JwtTokenProvider;

import local.socialnetwork.api.exception.AuthenticationUserException;

import local.socialnetwork.core.service.UserService;

import local.socialnetwork.model.user.CustomRole;
import local.socialnetwork.model.user.CustomUser;

import local.socialnetwork.model.dto.AuthenticationUserDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.util.List;
import java.util.HashMap;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationRestController.class);

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    private AuthenticationManager authenticationManager;

    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    public void setJwtTokenProvider(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping(value = "/signin")
    public ResponseEntity<Map<Object, Object>> authentication(@RequestBody AuthenticationUserDto authenticationUserDTO) {

        CustomUser user = userService.findByName(authenticationUserDTO.getUsername());

        try {

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationUserDTO.getUsername(), authenticationUserDTO.getPassword()));

            List<String> roles = user.getCustomRoles().stream()
                                                      .map(CustomRole::getAuthority)
                                                      .collect(Collectors.toList());

            String token = jwtTokenProvider.createToken(user.getId(), user.getUsername(), roles);

            Map<Object, Object> model = new HashMap<>();

            model.put("username", user.getUsername());
            model.put("token", token);

            LOGGER.info("User { username: {}, id: {} } has successfully authenticate", user.getUsername(), user.getId());

            return new ResponseEntity<>(model, HttpStatus.OK);

        } catch (RuntimeException e) {
            throw new AuthenticationUserException(e.getMessage());
        }
    }

    @ExceptionHandler({AuthenticationUserException.class})
    public ResponseEntity<?> handlerAuthenticationException(RuntimeException e) {
        LOGGER.info("Authorization Error: {}", e.getMessage());
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}