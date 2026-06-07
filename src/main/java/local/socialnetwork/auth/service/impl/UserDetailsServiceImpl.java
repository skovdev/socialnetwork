package local.socialnetwork.auth.service.impl;

import local.socialnetwork.auth.entity.AuthUser;

import local.socialnetwork.core.config.security.principal.UserPrincipal;

import local.socialnetwork.profiles.repository.UserProfileRepository;

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

@Slf4j
@Service("userDetailsService")
@Transactional
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserProfileRepository userProfileRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        var userProfile = userProfileRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found for username: {}", username);
                    return new UsernameNotFoundException(username + " is not found");
                });

        var authUser = userProfile.getAuthUser();

        if (authUser == null) {
            log.warn("UserProfile '{}' has no linked AuthUser", username);
            throw new UsernameNotFoundException("Auth record missing for username: " + username);
        }

        log.debug("Loaded user details for username: {}", username);

        return new UserPrincipal(authUser.getId(), username, authUser.getPasswordHash(), getAuthorities(authUser));
    }

    private List<GrantedAuthority> getAuthorities(AuthUser authUser) {
        return authUser.getAuthUserRoles().stream()
                .map(r -> new SimpleGrantedAuthority(r.getAuthority()))
                .collect(Collectors.toList());
    }
}
