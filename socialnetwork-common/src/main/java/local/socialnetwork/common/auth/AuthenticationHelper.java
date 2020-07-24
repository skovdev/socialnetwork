package local.socialnetwork.common.auth;

import local.socialnetwork.model.springsecurity.UserPrincipal;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Component;

@Component
public class AuthenticationHelper {

    @SuppressWarnings("unchecked")
    public <T> T getAuthenticatedUser() {

        return (T) ((UserPrincipal) SecurityContextHolder.getContext()
                                                         .getAuthentication()
                                                         .getPrincipal())
                                                         .getCustomUser();

    }
}
