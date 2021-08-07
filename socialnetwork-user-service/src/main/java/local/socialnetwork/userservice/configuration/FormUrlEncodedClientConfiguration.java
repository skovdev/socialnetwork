package local.socialnetwork.userservice.configuration;

import feign.codec.Encoder;

import feign.form.FormEncoder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class FormUrlEncodedClientConfiguration {

    @Bean
    @Scope("prototype")
    public Encoder encoder() {
        return new FormEncoder();
    }
}
