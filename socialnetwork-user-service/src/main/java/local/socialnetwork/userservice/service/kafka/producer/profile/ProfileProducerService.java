package local.socialnetwork.userservice.service.kafka.producer.profile;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.ObjectMapper;

import local.socialnetwork.userservice.model.dto.profile.ProfileDto;

import lombok.AccessLevel;

import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.core.KafkaTemplate;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ProfileProducerService {

    final KafkaTemplate<String, String> kafkaTemplate;
    final ObjectMapper objectMapper;

    public void sendProfileAndSave(String topic, ProfileDto profileDto) {
        try {
            log.info(String.format("### -> Producing topic %s and object -> [%s] for saving of profile", topic, profileDto.toString()));
            this.kafkaTemplate.send(topic, objectMapper.writeValueAsString(profileDto));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }
}