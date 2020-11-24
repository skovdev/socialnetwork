package local.socialnetwork.kafka.controller;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.kafka.annotation.KafkaListener;

import org.springframework.kafka.core.KafkaTemplate;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/kafka")
public class KafkaController {

    private static final Logger logger = LoggerFactory.getLogger(KafkaController.class);

    @Value("${spring.kafka.topic}")
    private String topicName;

    private final List<String> kafkaMessages = new ArrayList<>();

    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public void setKafkaTemplate(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/send")
    public HttpEntity<String> sendMessageToKafkaTopic(@RequestParam("key") String key, @RequestBody String message) {
        logger.debug("[socialnetwork] ### Sending Message -> topic: '{}', key: {}, message: {}", topicName, key, message);
        kafkaTemplate.send(topicName, key, message);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @GetMapping("/receive")
    public HttpEntity<String> getKafkaMessages() {
        String result = String.join("\n", kafkaMessages);
        logger.debug("[socialnetwork] ### Consumed Message -> " + result);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @KafkaListener(topics = "${spring.kafka.topic}")
    public void listenKafkaTopic(ConsumerRecord<String, String> record) {
        String message = String.format("key: %s, partition: %s, offset: %s for message: %s", record.key(), record.partition(), record.offset(), record.value());
        kafkaMessages.add(message);
    }
}