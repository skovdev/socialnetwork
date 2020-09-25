package local.socialnetwork.userservice;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class SpringBootRunUserService {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootRunUserService.class, args);
    }
}
