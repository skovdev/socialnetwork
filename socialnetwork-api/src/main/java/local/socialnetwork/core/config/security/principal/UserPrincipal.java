package local.socialnetwork.core.config.security.principal;

import lombok.Getter;

import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.core.userdetails.User;

import java.util.Collection;

import java.util.UUID;

/**
 * Spring Security {@link User} extended with the authenticated user's internal ID and whether the
 * account is {@code ACTIVE} (checked on every JWT request, so disabled accounts lose access).
 */
@Getter
public class UserPrincipal extends User {

    private final UUID id;
    private final boolean active;

    public UserPrincipal(UUID id, String username, String password, Collection<? extends GrantedAuthority> authorities,
                         boolean active) {
        super(username, password, authorities);
        this.id = id;
        this.active = active;
    }

    @Override
    public void eraseCredentials() {
    }
}
