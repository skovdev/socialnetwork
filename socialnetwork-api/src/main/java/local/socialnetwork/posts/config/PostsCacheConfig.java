package local.socialnetwork.posts.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import local.socialnetwork.core.config.PostsCacheProperties;

import local.socialnetwork.posts.dto.CachedPost;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.data.redis.cache.RedisCacheConfiguration;

import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

/**
 * Redis cache configuration. Values use a typed JSON serializer (no class names stored in Redis, so no
 * polymorphic deserialization), and the cache name is versioned so a changed value shape never meets old entries.
 */
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class PostsCacheConfig {

    public static final String POSTS_CACHE_NAME = "posts-v2";

    private final PostsCacheProperties postsCacheProperties;

    @Bean
    RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        var mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        var postSerializer = new Jackson2JsonRedisSerializer<>(mapper, CachedPost.class);
        var postsCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(postSerializer))
                .disableCachingNullValues()
                .entryTtl(postsCacheProperties.ttl());
        return builder -> builder.withCacheConfiguration(POSTS_CACHE_NAME, postsCacheConfig);
    }
}
