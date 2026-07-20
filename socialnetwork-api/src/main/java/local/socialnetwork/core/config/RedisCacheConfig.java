package local.socialnetwork.core.config;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.data.redis.cache.RedisCacheConfiguration;

import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

/**
 * Configures the Redis-backed {@link org.springframework.cache.CacheManager}.
 *
 * <p>Cache values are serialized as JSON (rather than the default JDK
 * serialization) so that response record types do not need to implement
 * {@link java.io.Serializable}. Each named cache may override the default
 * time-to-live via its own {@link org.springframework.boot.context.properties.ConfigurationProperties}.
 */
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class RedisCacheConfig {

    public static final String POSTS_CACHE_NAME = "posts";

    private final PostsCacheProperties postsCacheProperties;

    @Bean
    RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        var redisValueSerializer = new GenericJackson2JsonRedisSerializer()
                .configure(mapper -> mapper.registerModule(new JavaTimeModule()));
        var jsonSerializationPair = RedisSerializationContext.SerializationPair.fromSerializer(redisValueSerializer);
        var defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(jsonSerializationPair)
                .disableCachingNullValues();
        var postsCacheConfig = defaultCacheConfig.entryTtl(postsCacheProperties.ttl());
        return builder -> builder
                .cacheDefaults(defaultCacheConfig)
                .withCacheConfiguration(POSTS_CACHE_NAME, postsCacheConfig);
    }
}
