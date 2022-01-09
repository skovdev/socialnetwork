package local.socialnetwork.userservice.service.kafka.producer.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.core.KafkaTemplate;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class UserProducerService {

    private KafkaTemplate<String, Object> kafkaTemplate;

    public void send(String topic, Object value) {
        log.info(String.format("### -> Producing topic %s and object -> %s", topic, value));
        this.kafkaTemplate.send(topic, value);
    }
}