package local.socialnetwork.core.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;

import io.swagger.v3.oas.annotations.info.Info;

import io.swagger.v3.oas.annotations.security.SecurityScheme;

import org.springframework.context.annotation.Configuration;

/**
 * Global OpenAPI 3 configuration: API metadata and bearer-auth security scheme.
 */
@OpenAPIDefinition(
        info = @Info(
                title = "SocialNetwork API",
                version = "v1",
                description = "SocialNetwork REST API"
        )
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
@Configuration
public class OpenApiConfig {}
