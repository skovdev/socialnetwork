package local.socialnetwork.profileservice.kafka.producer.user;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.kafka.core.KafkaTemplate;

import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserProducer {

    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public void setKafkaTemplate(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(String topic, Object value) {
        log.info(String.format("Producing topic: %s and value: %s", topic, value));
        this.kafkaTemplate.send(topic, value);
    }
}