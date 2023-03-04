package local.socialnetwork.authserver.service.impl;

import local.socialnetwork.authserver.model.entity.User;

import local.socialnetwork.authserver.model.springsecurity.UserPrincipal;

import local.socialnetwork.authserver.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;

import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("userDetailsService")
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + " is not found"));
        List<GrantedAuthority> roles = new ArrayList<>();
        user.getRoles().forEach(r -> roles.add(new SimpleGrantedAuthority("ROLE_" + r.getAuthority())));
        return new UserPrincipal(user.getId(), user.getUsername(), user.getPassword(), roles);
    }
}
