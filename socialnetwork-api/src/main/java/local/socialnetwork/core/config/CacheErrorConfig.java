package local.socialnetwork.core.config;

import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.Cache;

import org.springframework.cache.annotation.CachingConfigurer;

import org.springframework.cache.interceptor.CacheErrorHandler;

import org.springframework.context.annotation.Configuration;

/**
 * Treats the cache as an optimization: a Redis failure is logged and the call falls through to the
 * database instead of failing the request.
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class CacheErrorConfig implements CachingConfigurer {

    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException e, Cache cache, Object key) {
                log.warn("Cache '{}' get failed for key {}, falling back to source: {}", cache.getName(), key, e.getMessage());
            }

            @Override
            public void handleCachePutError(RuntimeException e, Cache cache, Object key, Object value) {
                log.warn("Cache '{}' put failed for key {}: {}", cache.getName(), key, e.getMessage());
            }

            @Override
            public void handleCacheEvictError(RuntimeException e, Cache cache, Object key) {
                log.warn("Cache '{}' evict failed for key {}: {}", cache.getName(), key, e.getMessage());
            }

            @Override
            public void handleCacheClearError(RuntimeException e, Cache cache) {
                log.warn("Cache '{}' clear failed: {}", cache.getName(), e.getMessage());
            }
        };
    }
}
