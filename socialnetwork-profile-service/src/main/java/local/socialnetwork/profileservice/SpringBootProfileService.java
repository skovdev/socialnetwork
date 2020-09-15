package local.socialnetwork.profileservice;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class SpringBootProfileService {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootProfileService.class);
    }
}