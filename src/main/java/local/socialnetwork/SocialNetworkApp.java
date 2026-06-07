package local.socialnetwork;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
@ConfigurationPropertiesScan
public class SocialNetworkApp {
    static void main(String[] args) {
        SpringApplication.run(SocialNetworkApp.class, args);
    }
}

