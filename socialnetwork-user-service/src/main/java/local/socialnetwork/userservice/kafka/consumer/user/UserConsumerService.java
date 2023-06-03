package local.socialnetwork.userservice.kafka.consumer.user;

import com.fasterxml.jackson.databind.ObjectMapper;

import local.socialnetwork.userservice.model.dto.UserDTO;

import local.socialnetwork.userservice.model.entity.User;
import local.socialnetwork.userservice.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import org.springframework.kafka.annotation.KafkaListener;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class UserConsumerService {

    static final String USER_DEFAULT_GROUP_ID = "user-default-group-id";
    static final String TOPIC_USER_NEW = "topic.user.new";

    final UserService userService;
    final ObjectMapper objectMapper;

    @KafkaListener(topics = TOPIC_USER_NEW, groupId = USER_DEFAULT_GROUP_ID)
    public void receiveUserToCreate(ConsumerRecord<String, String> consumerRecord) {
        log.info(String.format("### -> Receive user to create: %s", consumerRecord.value()));
        UserDTO userDTO = getUserDTO(consumerRecord.value());
        if (userDTO != null) {
            userService.save(convertDtoToUserEntity(userDTO));
        }
    }

    private UserDTO getUserDTO(String json) {
        try {
            return objectMapper.readValue(json, UserDTO.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private User convertDtoToUserEntity(UserDTO userDTO) {
        User user = new User();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setCountry(userDTO.getCountry());
        user.setCity(userDTO.getCity());
        user.setAddress(userDTO.getAddress());
        user.setPhone(userDTO.getPhone());
        user.setBirthDay(userDTO.getBirthDay());
        user.setFamilyStatus(userDTO.getFamilyStatus());
        user.setAuthUserId(userDTO.getAuthUserId());
        return user;

    }
}