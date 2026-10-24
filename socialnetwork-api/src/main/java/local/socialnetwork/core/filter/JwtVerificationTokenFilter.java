package local.socialnetwork.core.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import local.socialnetwork.core.config.jwt.JwtTokenProvider;

import local.socialnetwork.core.config.security.principal.UserPrincipal;

import local.socialnetwork.shared.exception.InvalidJwtAuthenticationException;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.Authentication;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Servlet filter that authenticates each request by validating a bearer JWT
 * and populating the security context.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtVerificationTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        var token = jwtTokenProvider.resolveToken(request);
        if (token != null) {
            try {
                SecurityContextHolder.getContext().setAuthentication(authenticate(token));
            } catch (InvalidJwtAuthenticationException e) {
                log.debug("Rejected invalid JWT: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            } catch (UsernameNotFoundException e) {
                log.warn("JWT references a non-existent user — treating request as unauthenticated: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }

    private Authentication authenticate(String token) {
        var identity = jwtTokenProvider.verify(token);
        var userDetails = userDetailsService.loadUserByUsername(identity.username());
        // The subject pins the token to one account: a recreated username must not inherit old tokens,
        // and an account that is no longer ACTIVE loses access as soon as the principal cache refreshes.
        if (!(userDetails instanceof UserPrincipal principal)
                || !principal.isActive()
                || !principal.getId().toString().equals(identity.subject())) {
            throw new InvalidJwtAuthenticationException("JWT does not match an active account");
        }
        return new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());
    }
}
