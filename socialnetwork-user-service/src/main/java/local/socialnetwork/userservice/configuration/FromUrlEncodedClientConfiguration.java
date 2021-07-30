package local.socialnetwork.userservice.configuration;

import feign.codec.Encoder;

import feign.form.FormEncoder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FromUrlEncodedClientConfiguration {

    @Bean
    public Encoder encoder() {
        return new FormEncoder();
    }
}
