package local.socialnetwork.core.config;

import io.lettuce.core.ClientOptions;

import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * While Redis is unreachable, reject commands immediately instead of queueing them until they time out, so the
 * cache and rate limiter degrade at once and the connection recovers on its own when Redis returns.
 */
@Configuration(proxyBeanMethods = false)
public class RedisClientConfig {

    @Bean
    LettuceClientConfigurationBuilderCustomizer redisFailFastCustomizer() {
        return builder -> builder.clientOptions(ClientOptions.builder()
                .autoReconnect(true)
                .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
                .build());
    }
}
