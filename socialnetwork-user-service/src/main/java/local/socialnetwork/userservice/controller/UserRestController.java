package local.socialnetwork.userservice.controller;

import local.socialnetwork.userservice.model.user.CustomUser;

import local.socialnetwork.userservice.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserRestController {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomUser> findById(@PathVariable("id") UUID id) {
        CustomUser user = userService.findById(id).orElseThrow(NullPointerException::new);
        return ResponseEntity.ok().body(user);
    }

    @GetMapping
    public ResponseEntity<CustomUser> findByUsername(@RequestParam("username") String username) {
        CustomUser user = userService.findByUsername(username).orElseThrow(NullPointerException::new);
        return ResponseEntity.ok().body(user);
    }

    @PutMapping
    public ResponseEntity<CustomUser> update(@RequestBody CustomUser user) {

        if (user != null) {
            userService.update(user);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}