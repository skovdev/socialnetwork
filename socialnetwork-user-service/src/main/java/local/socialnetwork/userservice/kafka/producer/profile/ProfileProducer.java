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

    @Value("${sn.profile.default.avatar.path}")
    String pathDefaultAvatar;

    final KafkaTemplate<String, String> kafkaTemplate;
    final ResourceUtil resourceUtil;
    final ObjectMapper objectMapper;

    public void sendProfileAndSave(String topic, UUID userId) {
        try {
            this.kafkaTemplate.send(topic, objectMapper.writeValueAsString(buildProfile(userId)));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }

    private ProfileDto buildProfile(UUID userId) {
        ProfileDto profileDTO = new ProfileDto();
        profileDTO.setId(UUID.randomUUID());
        profileDTO.setActive(true);
        try {
            profileDTO.setAvatar(resourceUtil.getEncodedResource(pathDefaultAvatar));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        profileDTO.setUserId(userId);
        return profileDTO;
    }
}