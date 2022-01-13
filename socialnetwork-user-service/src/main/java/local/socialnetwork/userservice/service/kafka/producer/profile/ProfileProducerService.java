package local.socialnetwork.userservice.service.kafka.producer.profile;

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
public class ProfileProducerService {

    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendProfileAndSave(String topic, Object value) {
        log.info(String.format("### -> Producing topic %s and object -> %s for saving of profile", topic, value));
        this.kafkaTemplate.send(topic, value);
    }
}