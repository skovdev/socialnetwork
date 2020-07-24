package local.socialnetwork.core.service.impl;

import local.socialnetwork.core.repository.UserRepository;

import local.socialnetwork.model.user.CustomUser;

import local.socialnetwork.model.springsecurity.UserPrincipal;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        CustomUser customUser = userRepository.findByUsername(username);

        Optional.ofNullable(customUser)
                .orElseThrow(() -> new UsernameNotFoundException(username + " is not found"));

        List<GrantedAuthority> roles = new ArrayList<>();

        customUser.getCustomRoles().forEach(r -> roles.add(new SimpleGrantedAuthority("ROLE_" + r.getAuthority())));

        return new UserPrincipal(customUser.getUsername(), customUser.getPassword(), roles, customUser);

    }
}