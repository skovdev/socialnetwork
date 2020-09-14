package local.socialnetwork.adminservice;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class SpringBootRunAdminService {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootRunAdminService.class);
    }
}
