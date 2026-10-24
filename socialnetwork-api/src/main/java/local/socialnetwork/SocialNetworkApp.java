package local.socialnetwork;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import org.springframework.cache.annotation.EnableCaching;

import org.springframework.retry.annotation.EnableRetry;

import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Spring Boot entry point for the SocialNetwork API.
 */
@EnableAsync
@EnableRetry
@EnableCaching
@EnableScheduling
@SpringBootApplication
@ConfigurationPropertiesScan
public class SocialNetworkApp {
    public static void main(String[] args) {
        SpringApplication.run(SocialNetworkApp.class, args);
    }
}

