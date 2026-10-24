package local.socialnetwork.core.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import local.socialnetwork.core.config.RequestRateLimitProperties;

import local.socialnetwork.core.ratelimit.RateLimiter;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;

import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import java.util.Set;

/**
 * Throttles the unauthenticated endpoints that cost money or CPU (SES email, token rotation) per client IP,
 * so they cannot be used for email bombing or brute-forcing refresh tokens.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PublicEndpointRateLimitFilter extends OncePerRequestFilter {

    private static final Set<String> THROTTLED_PATHS = Set.of(
            "/api/v1/auth/register",
            "/api/v1/auth/refresh",
            "/api/v1/auth/resend-verification");

    private final RateLimiter rateLimiter;
    private final RequestRateLimitProperties properties;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !HttpMethod.POST.matches(request.getMethod()) || !THROTTLED_PATHS.contains(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        var key = "public:" + request.getRequestURI() + ":" + request.getRemoteAddr();
        var result = rateLimiter.hit(key, properties.window());
        if (result.count() > properties.maxRequests()) {
            log.warn("Rate limit exceeded for {} from {}", request.getRequestURI(), request.getRemoteAddr());
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setHeader(HttpHeaders.RETRY_AFTER, String.valueOf(result.retryAfterSeconds()));
            response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
            response.getWriter().write("{\"status\":429,\"title\":\"Too Many Requests\","
                    + "\"detail\":\"Too many requests. Please try again later.\",\"errorCode\":\"TOO_MANY_REQUESTS\"}");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
