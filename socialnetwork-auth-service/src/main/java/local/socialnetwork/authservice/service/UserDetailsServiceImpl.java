package local.socialnetwork.authservice.service;

import local.socialnetwork.authservice.client.UserServiceProxy;

import local.socialnetwork.authservice.model.CustomUser;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserServiceProxy userServiceProxy;

    @Autowired
    public void setUserServiceProxy(UserServiceProxy userServiceProxy) {
        this.userServiceProxy = userServiceProxy;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        CustomUser user = userServiceProxy.findUserByUsername(username);

        return null;
    }
}
