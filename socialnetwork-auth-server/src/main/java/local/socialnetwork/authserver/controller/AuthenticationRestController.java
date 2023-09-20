package local.socialnetwork.authserver.controller;

import local.socialnetwork.authserver.config.jwt.JwtTokenProvider;

import local.socialnetwork.authserver.dto.SignInDTO;
import local.socialnetwork.authserver.dto.SignUpDTO;

import local.socialnetwork.authserver.exception.AuthenticationUserException;
import local.socialnetwork.authserver.exception.AuthenticationUserNotFoundException;

import local.socialnetwork.authserver.model.entity.AuthUser;
import local.socialnetwork.authserver.model.entity.AuthRole;

import local.socialnetwork.authserver.service.AuthUserService;

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

import java.util.Optional;

import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class AuthenticationRestController {

    final AuthUserService authUserService;
    final AuthenticationManager authenticationManager;
    final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody SignUpDTO signUpDTO) {
        Optional<AuthUser> authUser = authUserService.findByUsername(signUpDTO.getUsername());
        if (authUser.isPresent() && authUser.get().getUsername().equalsIgnoreCase(signUpDTO.getUsername())) {
            log.info("User '{}' with this username already exists in database", authUser.get().getUsername());
            return ResponseEntity.ok(authUser.get().getUsername() + " is exists in databases");
        } else {
            authUserService.signUp(signUpDTO);
            log.info("User signed up successfully");
            return ResponseEntity.ok("User signed up");
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<Map<Object, Object>> signIn(@RequestBody SignInDTO signInDTO) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInDTO.getUsername(), signInDTO.getPassword()));
            AuthUser authUser = authUserService.findByUsername(signInDTO.getUsername())
                    .orElseThrow(() -> new AuthenticationUserNotFoundException(signInDTO.getUsername() + " is not found"));
            List<String> roles = getRoles(authUser.getAuthRoles());
            String token = jwtTokenProvider.createToken(authUser.getId(), authUser.getUsername(), roles);
            Map<Object, Object> model = new HashMap<>();
            model.put("authUserId", authUser.getId());
            model.put("username", authUser.getUsername());
            model.put("token", token);
            log.info("User { id: {}, username: {} } authenticated successfully", authUser.getId(), authUser.getUsername());
            return new ResponseEntity<>(model, HttpStatus.OK);
        } catch (RuntimeException e) {
            throw new AuthenticationUserException(e.getMessage());
        }
    }

    private List<String> getRoles(List<AuthRole> authRoles) {
        return authRoles.stream()
                .map(AuthRole::getAuthority)
                .collect(Collectors.toList());
    }

    @ExceptionHandler(AuthenticationUserException.class)
    public ResponseEntity<?> handlerAuthenticationException(RuntimeException e) {
        log.info("Authorization Error: {}", e.getMessage());
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}
