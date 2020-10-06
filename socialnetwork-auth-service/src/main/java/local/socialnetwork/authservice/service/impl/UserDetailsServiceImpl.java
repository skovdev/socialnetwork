package local.socialnetwork.authservice.service.impl;

import local.socialnetwork.authservice.client.UserServiceProxy;

import local.socialnetwork.authservice.model.CustomUser;
import local.socialnetwork.authservice.model.springsecurity.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserServiceProxy userServiceProxy;

    @Autowired
    public void setUserServiceProxy(UserServiceProxy userServiceProxy) {
        this.userServiceProxy = userServiceProxy;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        CustomUser user = userServiceProxy.findUserByUsername(username);

        Optional.ofNullable(user)
                .orElseThrow(() -> new UsernameNotFoundException(username + " is not found"));

        List<GrantedAuthority> roles = new ArrayList<>();

        user.getRoles().forEach(r -> roles.add(new SimpleGrantedAuthority("ROLE_" + r.getAuthority())));

        return new UserPrincipal(user.getUsername(), user.getPassword(), roles, user);

    }
}
