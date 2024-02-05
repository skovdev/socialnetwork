package local.socialnetwork.userservice.kafka.consumer.user;

import com.fasterxml.jackson.databind.ObjectMapper;

import local.socialnetwork.userservice.model.dto.user.UserDto;

import local.socialnetwork.userservice.model.entity.user.User;

import local.socialnetwork.userservice.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import org.springframework.kafka.annotation.KafkaListener;

import org.springframework.stereotype.Component;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class UserConsumer {

    static final String USER_DEFAULT_GROUP_ID = "user-default-group-id";
    static final String TOPIC_USER_NEW = "topic.user.new";

    final UserService userService;
    final ObjectMapper objectMapper;

    @KafkaListener(topics = TOPIC_USER_NEW, groupId = USER_DEFAULT_GROUP_ID)
    public void receiveUserToCreate(ConsumerRecord<String, String> consumerRecord) {
        log.info(String.format("### -> Receive user to create: %s", consumerRecord.value()));
        UserDto userDto = getUserDTO(consumerRecord.value());
        if (userDto != null) {
            userService.save(convertDtoToUserEntity(userDto));
        }
    }

    private UserDto getUserDTO(String json) {
        try {
            return objectMapper.readValue(json, UserDto.class);
        } catch (Exception e) {
            log.error("Failed to read value. Error: {}", e.getMessage());
            return null;
        }
    }

    private User convertDtoToUserEntity(UserDto userDto) {
        User user = new User();
        user.setFirstName(userDto.firstName());
        user.setLastName(userDto.lastName());
        user.setCountry(userDto.country());
        user.setCity(userDto.city());
        user.setAddress(userDto.address());
        user.setPhone(userDto.phone());
        user.setBirthDay(userDto.birthDay());
        user.setFamilyStatus(userDto.familyStatus());
        user.setAuthUserId(userDto.authUserId());
        return user;
    }
}