package local.socialnetwork.groupservice.kafka.producer;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.kafka.core.KafkaTemplate;

import org.springframework.stereotype.Component;

@Component
public class UserProducer {

    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public void setKafkaTemplate(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(String topic, Object value) {
        this.kafkaTemplate.send(topic, value);
    }
}