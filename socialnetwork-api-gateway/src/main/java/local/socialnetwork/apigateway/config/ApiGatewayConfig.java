package local.socialnetwork.apigateway.config;

import local.socialnetwork.apigateway.filter.ValidationAuthHeaderGatewayPreFilter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.cloud.gateway.route.RouteLocator;

import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGatewayConfig {

    @Value("${sn.api-gateway.route.host.user-service}")
    private String userServiceHost;

    @Value("${sn.api-gateway.route.host.profile-service}")
    private String profileServiceHost;

    private ValidationAuthHeaderGatewayPreFilter validationAuthHeaderGatewayPreFilter;

    @Autowired
    public void setCheckAuthHeaderGlobalPreFilter(ValidationAuthHeaderGatewayPreFilter validationAuthHeaderGatewayPreFilter) {
        this.validationAuthHeaderGatewayPreFilter = validationAuthHeaderGatewayPreFilter;
    }

    @Bean
    public RouteLocator configureRoute(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service", r -> r.path("/users/**")
                        .filters(filter -> filter.filter(validationAuthHeaderGatewayPreFilter))
                        .uri(userServiceHost))
                .route("profile-service", r -> r.path("/profiles/**")
                        .filters(filter -> filter.filter(validationAuthHeaderGatewayPreFilter))
                        .uri(profileServiceHost))
                .build();
    }
}