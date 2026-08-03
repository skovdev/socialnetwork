package local.socialnetwork.auth.service.impl;

import local.socialnetwork.auth.entity.AuthUser;

import local.socialnetwork.auth.repository.AuthUserRepository;

import local.socialnetwork.core.config.security.principal.UserPrincipal;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import java.util.stream.Collectors;

/**
 * Spring Security {@link UserDetailsService} that loads a {@link UserPrincipal}
 * from the linked {@code UserProfile}/{@link AuthUser} for a given username.
 */
@Slf4j
@Service("userDetailsService")
@Transactional
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AuthUserRepository authUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var normalized = username != null ? username.toLowerCase() : null;
        var authUser = authUserRepository.findByUserProfileUsername(normalized)
                .orElseThrow(() -> {
                    log.warn("User not found for username: {}", username);
                    return new UsernameNotFoundException(username + " is not found");
                });
        log.debug("Loaded user details for username: {}", normalized);
        return new UserPrincipal(authUser.getId(), normalized, authUser.getPasswordHash(), getAuthorities(authUser));
    }

    private List<GrantedAuthority> getAuthorities(AuthUser authUser) {
        return authUser.getAuthUserRoles()
                .stream()
                .map(r -> new SimpleGrantedAuthority(r.getAuthority()))
                .collect(Collectors.toList());
    }
}
