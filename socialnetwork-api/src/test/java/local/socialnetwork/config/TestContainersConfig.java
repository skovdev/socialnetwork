package local.socialnetwork.config;

import com.redis.testcontainers.RedisContainer;

import org.springframework.boot.test.context.TestConfiguration;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;

import org.springframework.context.annotation.Bean;

import org.testcontainers.containers.PostgreSQLContainer;

import org.testcontainers.utility.DockerImageName;

/**
 * Provides PostgreSQL and Redis Testcontainers wired via {@code @ServiceConnection}
 * so JPA/Liquibase and the cache abstraction connect to real containers automatically.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestContainersConfig {

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>("postgres:16-alpine");
    }

    @Bean
    @ServiceConnection
    RedisContainer redisContainer() {
        return new RedisContainer(DockerImageName.parse("redis:7-alpine"));
    }
}
