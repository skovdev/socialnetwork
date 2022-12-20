package local.socialnetwork.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;

import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGatewayConfig {

    @Bean
    public RouteLocator configureRoute(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r.path("/auth/**").uri("http://socialnetwork-auth-service:8080"))
                .route("user-service", r -> r.path("/users/**").uri("http://socialnetwork-user-service:8084"))
                .build();
    }
}