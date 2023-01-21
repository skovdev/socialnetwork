package local.socialnetwork.apigateway.config;

import local.socialnetwork.apigateway.filter.ValidationAuthHeaderGatewayPreFilter;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.cloud.gateway.route.RouteLocator;

import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGatewayConfig {

    private ValidationAuthHeaderGatewayPreFilter validationAuthHeaderGatewayPreFilter;

    @Autowired
    public void setCheckAuthHeaderGlobalPreFilter(ValidationAuthHeaderGatewayPreFilter validationAuthHeaderGatewayPreFilter) {
        this.validationAuthHeaderGatewayPreFilter = validationAuthHeaderGatewayPreFilter;
    }

    @Bean
    public RouteLocator configureRoute(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r.path("/auth/**")
                        .uri("http://socialnetwork-auth-service:8080"))
                .route("user-service", r -> r.path("/registration/**", "/users/**")
                        .filters(filter -> filter.filter(validationAuthHeaderGatewayPreFilter))
                        .uri("http://socialnetwork-user-service:8084"))
                .route("profile-service", r -> r.path("/profiles/**")
                        .filters(filter -> filter.filter(validationAuthHeaderGatewayPreFilter))
                        .uri("http://socialnetwork-profile-service:8081"))
                .build();
    }
}