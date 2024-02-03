package local.socialnetwork.apigateway;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class SpringBootRunApiGateway {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootRunApiGateway.class, args);
    }
}