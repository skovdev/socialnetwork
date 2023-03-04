package local.socialnetwork.authserver.model.springsecurity;

import lombok.Getter;

import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.core.userdetails.User;

import java.io.Serial;

import java.util.Collection;
import java.util.UUID;

@Getter
public class UserPrincipal extends User {

    @Serial
    private static final long serialVersionUID = -3020152727834643516L;

    private final UUID id;

    public UserPrincipal(UUID id, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.id = id;
    }
}