package local.socialnetwork.authservice.controller;

import local.socialnetwork.authservice.client.UserServiceProxy;

import local.socialnetwork.authservice.config.jwt.JwtTokenProvider;

import local.socialnetwork.authservice.dto.AuthenticationUserDto;

import local.socialnetwork.authservice.exception.AuthenticationUserException;

import local.socialnetwork.authservice.model.Role;
import local.socialnetwork.authservice.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationRestController.class);

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

    private UserServiceProxy userServiceProxy;

    @Autowired
    public void setUserServiceProxy(UserServiceProxy userServiceProxy) {
        this.userServiceProxy = userServiceProxy;
    }

    @PostMapping("/sign")
    public ResponseEntity<Map<Object, Object>> authentication(@RequestBody AuthenticationUserDto authenticationUserDto) {

        User user = userServiceProxy.findUserByUsername(authenticationUserDto.getUsername());

        try {

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationUserDto.getUsername(), authenticationUserDto.getPassword()));

            List<String> roles = user.getRoles().stream()
                                     .map(Role::getAuthority)
                                     .collect(Collectors.toList());

            String token = jwtTokenProvider.createToken(user.getId(), user.getUsername(), roles);

            Map<Object, Object> model = new HashMap<>();

            model.put("username", user.getUsername());
            model.put("token", token);

            LOGGER.info("User { id: {}, username: {} } has successfully authenticate", user.getId(), user.getUsername());

            return new ResponseEntity<>(model, HttpStatus.OK);

        } catch (RuntimeException e) {
            throw new AuthenticationUserException(e.getMessage());
        }
    }

    @ExceptionHandler(AuthenticationUserException.class)
    public ResponseEntity<?> handlerAuthenticationException(RuntimeException e) {
        LOGGER.info("Authorization Error: {}", e.getMessage());
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}
