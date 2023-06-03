package local.socialnetwork.authserver.kafka.producer.user;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.ObjectMapper;

import local.socialnetwork.authserver.dto.SignUpDTO;

import local.socialnetwork.authserver.dto.user.UserDTO;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.core.KafkaTemplate;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class UserProducer {

    final KafkaTemplate<String, String> kafkaTemplate;
    final ObjectMapper objectMapper;

    public void sendUserAndSave(String topic, SignUpDTO signUpDTO, UUID authUserId) {
        try {
            this.kafkaTemplate.send(topic, objectMapper.writeValueAsString(buildUser(signUpDTO, authUserId)));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }

    private UserDTO buildUser(SignUpDTO signUpDTO, UUID authUserId) {
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName(signUpDTO.getFirstName());
        userDTO.setLastName(signUpDTO.getLastName());
        userDTO.setCountry(signUpDTO.getCountry());
        userDTO.setCity(signUpDTO.getCity());
        userDTO.setAddress(signUpDTO.getAddress());
        userDTO.setPhone(signUpDTO.getPhone());
        userDTO.setBirthDay(signUpDTO.getBirthDay());
        userDTO.setFamilyStatus(signUpDTO.getFamilyStatus());
        userDTO.setAuthUserId(authUserId);
        return userDTO;
    }
}