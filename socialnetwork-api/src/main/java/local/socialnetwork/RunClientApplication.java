package local.socialnetwork;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;

@SpringBootApplication
public class RunClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(RunClientApplication.class, args);
    }
}