package local.socialnetwork.config;

import org.springframework.boot.test.context.TestConfiguration;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;

import org.springframework.context.annotation.Bean;

import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Provides a PostgreSQL Testcontainer wired via {@code @ServiceConnection}
 * so Liquibase and JPA connect to the real container automatically.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestContainersConfig {

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>("postgres:16-alpine");
    }
}
