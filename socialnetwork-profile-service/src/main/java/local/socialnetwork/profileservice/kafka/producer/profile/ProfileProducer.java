package local.socialnetwork.profileservice.kafka.producer.profile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.kafka.core.KafkaTemplate;

import org.springframework.stereotype.Component;

@Component
public class ProfileProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileProducer.class);

    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public void setKafkaTemplate(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(String topic, Object value) {
        LOGGER.info(String.format("Producing topic: %s and object: %s", topic, value));
        kafkaTemplate.send(topic, value);
    }
}