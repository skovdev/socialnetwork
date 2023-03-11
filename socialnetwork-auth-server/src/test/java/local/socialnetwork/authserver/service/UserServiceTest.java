package local.socialnetwork.authserver.service;

import local.socialnetwork.authserver.model.entity.Role;
import local.socialnetwork.authserver.model.entity.User;

import local.socialnetwork.authserver.repository.RoleRepository;
import local.socialnetwork.authserver.repository.UserRepository;

import local.socialnetwork.authserver.service.impl.UserServiceImpl;

import org.apache.commons.lang.RandomStringUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private final static String USERNAME = "Test";

    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, roleRepository, passwordEncoder);
    }

    @Test
    public void testFindByUsername() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(createUser(USERNAME)));
        Optional<User> user = userService.findByUsername(USERNAME);
        assertNotNull(user);
        assertTrue(user.isPresent());
        assertEquals(user.get().getUsername(), "Test");
    }

    private User createUser(String name) {

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(name);
        user.setPassword(RandomStringUtils.randomAlphabetic(10));

        Role role = new Role();
        role.setId(UUID.randomUUID());
        role.setAuthority("USER");
        role.setUser(user);

        user.setRoles(Collections.singletonList(role));

        return user;

    }
}