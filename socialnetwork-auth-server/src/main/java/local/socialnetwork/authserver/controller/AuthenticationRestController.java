package local.socialnetwork.authserver.controller;

import local.socialnetwork.authserver.config.jwt.JwtTokenProvider;

import local.socialnetwork.authserver.dto.AuthenticationUserDTO;

import local.socialnetwork.authserver.exception.AuthenticationUserException;

import local.socialnetwork.authserver.exception.UserNotFoundException;

import local.socialnetwork.authserver.model.entity.Role;
import local.socialnetwork.authserver.model.entity.User;

import local.socialnetwork.authserver.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;

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

@Slf4j
@RestController
@RequestMapping("/signin")
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class AuthenticationRestController {

    final UserService userService;
    final AuthenticationManager authenticationManager;
    final JwtTokenProvider jwtTokenProvider;

    @PostMapping
    public ResponseEntity<Map<Object, Object>> authentication(@RequestBody AuthenticationUserDTO authenticationUserDto) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationUserDto.getUsername(), authenticationUserDto.getPassword()));
            User user = userService.findByUsername(authenticationUserDto.getUsername())
                    .orElseThrow(() -> new UserNotFoundException("User is not found"));
            List<String> roles = getRoles(user.getRoles());
            String token = jwtTokenProvider.createToken(user.getId(), user.getUsername(), roles);
            Map<Object, Object> model = new HashMap<>();
            model.put("username", user.getUsername());
            model.put("token", token);
            log.info("User { id: {}, username: {} } has successfully authenticate", user.getId(), user.getUsername());
            return new ResponseEntity<>(model, HttpStatus.OK);
        } catch (RuntimeException e) {
            throw new AuthenticationUserException(e.getMessage());
        }
    }

    private List<String> getRoles(List<Role> roles) {
        return roles.stream()
                .map(Role::getAuthority)
                .collect(Collectors.toList());
    }

    @ExceptionHandler(AuthenticationUserException.class)
    public ResponseEntity<?> handlerAuthenticationException(RuntimeException e) {
        log.info("Authorization Error: {}", e.getMessage());
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}
