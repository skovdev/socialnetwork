package local.socialnetwork.userservice.service.impl;

import local.socialnetwork.userservice.event.ProfileCompletedEvent;
import local.socialnetwork.userservice.kafka.constant.KafkaTopics;

import local.socialnetwork.userservice.kafka.saga.signup.producer.profile.ProfileRegistrationCompletedProducer;

import local.socialnetwork.userservice.dto.user.UserDto;

import local.socialnetwork.userservice.entity.user.User;

import local.socialnetwork.userservice.repository.UserRepository;

import local.socialnetwork.userservice.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {

    final UserRepository userRepository;
    final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public Optional<UserDto> findById(UUID id) {
        return userRepository.findById(id).stream()
                .map(user -> new UserDto(
                        user.getId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getCountry(),
                        user.getCity(),
                        user.getAddress(),
                        user.getPhone(),
                        user.getBirthDay(),
                        user.getFamilyStatus(),
                        user.getAuthUserId())
                ).findFirst();
    }

    @Override
    public Optional<UUID> findUserIdByAuthUserId(UUID authUserId) {
        return userRepository.findUserByAuthUserId(authUserId).map(User::getId);
    }

    @Override
    public void save(UserDto userDto) {
        User user = userRepository.save(convertDtoToUserEntity(userDto));
        applicationEventPublisher.publishEvent(new ProfileCompletedEvent(user));
        log.info("User is saved successfully. UserID: {}", user.getId());
    }

    @Override
    public void deleteById(UUID id) {
        userRepository.deleteById(id);
        log.info("User is deleted. UserID: {}", id);
    }

    @Override
    public void deleteByAuthUserId(UUID authUserId) {
        userRepository.deleteByAuthUserId(authUserId);
        log.info("User is deleted. AuthUserID: {}", authUserId);
    }

    private User convertDtoToUserEntity(UserDto userDto) {
        User user = new User();
        user.setId(userDto.id());
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