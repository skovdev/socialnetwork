package local.socialnetwork.userservice.configuration;

import feign.codec.Encoder;

import org.springframework.beans.factory.ObjectFactory;

import org.springframework.boot.autoconfigure.http.HttpMessageConverters;

import org.springframework.cloud.openfeign.support.SpringEncoder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.converter.FormHttpMessageConverter;

@Configuration
public class FormUrlEncodedClientConfiguration {

    @Bean
    public Encoder encoder() {

        ObjectFactory<HttpMessageConverters> objectFactory = () ->
                new HttpMessageConverters(new FormHttpMessageConverter());

        return new SpringEncoder(objectFactory);

    }
}
