package local.socialnetwork.apigateway.filter;

import io.jsonwebtoken.MalformedJwtException;

import io.jsonwebtoken.security.SignatureException;

import local.socialnetwork.apigateway.util.JwtUtils;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;

import org.springframework.http.HttpStatus;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;

import org.springframework.stereotype.Component;

import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ValidationAuthHeaderGatewayPreFilter implements GatewayFilter {

    final JwtUtils jwtUtils;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();

        if (isAuthMissing(request)) {
            return onError(exchange, "Authentication information is missing.", HttpStatus.UNAUTHORIZED);
        }

        String bearerToken = getBearerToken(request);

        if (bearerToken == null) {
            return onError(exchange, "Bearer token is missing.", HttpStatus.UNAUTHORIZED);
        }

        return Mono.just(bearerToken)
                .flatMap(token -> {
                    if (!jwtUtils.isTokenExpired(token)) {
                        return chain.filter(exchange);
                    } else {
                        return onError(exchange, "JWT token is expired.", HttpStatus.UNAUTHORIZED);
                    }
                }).onErrorResume(e -> {
                    if (e instanceof SignatureException) {
                        return this.onError(exchange, "Invalid JWT signature.", HttpStatus.UNAUTHORIZED);
                    } else if (e instanceof MalformedJwtException) {
                        return this.onError(exchange, "JWT token is malformed.", HttpStatus.BAD_REQUEST);
                    } else {
                        return onError(exchange, "Authentication failed due to an internal error. Please try again.", HttpStatus.FORBIDDEN);
                    }
                });
    }

    private boolean isAuthMissing(ServerHttpRequest request) {
        return !request.getHeaders().containsKey("Authorization");
    }

    private String getBearerToken(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus httpStatus) {
        log.error("Authentication error: {}", message);
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }
}
