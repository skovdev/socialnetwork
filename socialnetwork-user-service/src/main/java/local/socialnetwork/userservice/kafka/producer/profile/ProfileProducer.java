package local.socialnetwork.userservice.kafka.producer.profile;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.ObjectMapper;

import local.socialnetwork.userservice.model.dto.profile.ProfileDto;

import local.socialnetwork.userservice.util.ResourceUtil;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.kafka.core.KafkaTemplate;

import org.springframework.stereotype.Component;

import java.io.IOException;

import java.util.UUID;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ProfileProducer {

    private static final boolean PROFILE_ACTIVE = true;

    @Value("${sn.profile.default.avatar.path}")
    String pathDefaultAvatar;

    final KafkaTemplate<String, String> kafkaTemplate;
    final ResourceUtil resourceUtil;
    final ObjectMapper objectMapper;

    public void sendProfileAndSave(String topic, UUID userId) {
        try {
            this.kafkaTemplate.send(topic, objectMapper.writeValueAsString(buildProfile(userId)));
        } catch (JsonProcessingException e) {
            log.error("Failed to send profile. Error: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private ProfileDto buildProfile(UUID userId) {
        try {
            return new ProfileDto(UUID.randomUUID(), PROFILE_ACTIVE, resourceUtil.getEncodedResource(pathDefaultAvatar), userId);
        } catch (IOException e) {
            log.error("Failed to build profile. Error: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}