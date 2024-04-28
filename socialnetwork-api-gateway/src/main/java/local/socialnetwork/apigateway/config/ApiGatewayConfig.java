package local.socialnetwork.apigateway.config;

import local.socialnetwork.apigateway.filter.ValidationAuthHeaderGatewayPreFilter;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.context.properties.ConfigurationProperties;

import org.springframework.cloud.gateway.route.RouteLocator;

import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.HashMap;

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
                .route("user-service", r -> r.path("/api/v1/users/**")
                        .filters(filter -> filter.filter(validationAuthHeaderGatewayPreFilter))
                        .uri(serviceHosts().get("user-service")))
                .route("profile-service", r -> r.path("/api/v1/profiles/**")
                        .filters(filter -> filter.filter(validationAuthHeaderGatewayPreFilter))
                        .uri(serviceHosts().get("profile-service")))
                .build();
    }

    @Bean
    @ConfigurationProperties(prefix = "sn.api-gateway.route.hosts")
    public Map<String, String> serviceHosts() {
        return new HashMap<>();
    }
}