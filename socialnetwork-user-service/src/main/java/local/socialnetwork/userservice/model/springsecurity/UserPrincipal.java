package local.socialnetwork.userservice.model.springsecurity;

import local.socialnetwork.userservice.model.user.CustomUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class UserPrincipal extends User {

    private static final long serialVersionUID = -1439400347377942188L;

    private final CustomUser customUser;

    public UserPrincipal(String username, String password, Collection<? extends GrantedAuthority> authorities, CustomUser customUser) {
        super(username, password, authorities);
        this.customUser = customUser;
    }

    public CustomUser getCustomUser() {
        return customUser;
    }
}