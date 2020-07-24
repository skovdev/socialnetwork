package local.socialnetwork.model.springsecurity;

import local.socialnetwork.model.user.CustomUser;

import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.core.userdetails.User;

import java.util.List;

public class UserPrincipal extends User {

    private static final long serialVersionUID = 26032019L;

    private final CustomUser customUser;

    public UserPrincipal(String username, String password, List<GrantedAuthority> roles, CustomUser customUser) {
        super(username, password, roles);
        this.customUser = customUser;
    }

    public CustomUser getCustomUser() {
        return customUser;
    }
}