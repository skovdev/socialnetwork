package local.socialnetwork.authservice.model.springsecurity;

import local.socialnetwork.authservice.model.dto.UserDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class UserPrincipal extends User {

    private static final long serialVersionUID = 8554249721167538019L;

    private final UserDto user;

    public UserPrincipal(String username, String password, Collection<? extends GrantedAuthority> authorities, UserDto user) {
        super(username, password, authorities);
        this.user = user;
    }

    public UserDto getUser() {
        return user;
    }
}