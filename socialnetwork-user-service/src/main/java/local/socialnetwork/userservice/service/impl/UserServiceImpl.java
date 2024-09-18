package local.socialnetwork.userservice.service.impl;

import local.socialnetwork.userservice.event.ProfileCompletedEvent;

import local.socialnetwork.userservice.dto.user.UserDto;
import local.socialnetwork.userservice.dto.user.UserRequestUpdateDto;

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
    public void update(UUID id, UserRequestUpdateDto userRequestUpdateDto) {
        userRepository.findById(id).ifPresentOrElse(user -> {
            updateUserDetails(user, userRequestUpdateDto);
            userRepository.save(user);
            log.info("User is updated successfully. UserID: {}", id);
        }, () -> log.warn("User with ID: {} not found.", id));
    }

    private void updateUserDetails(User user, UserRequestUpdateDto userRequestUpdateDto) {
        user.setFirstName(userRequestUpdateDto.firstName() != null  && !userRequestUpdateDto.firstName().isEmpty() ?
                userRequestUpdateDto.firstName() : user.getFirstName());
        user.setLastName(userRequestUpdateDto.lastName() != null && !userRequestUpdateDto.lastName().isEmpty() ?
                userRequestUpdateDto.lastName() : user.getLastName());
        user.setCountry(userRequestUpdateDto.country() != null && !userRequestUpdateDto.country().isEmpty() ?
                userRequestUpdateDto.country() : user.getCountry());
        user.setCity(userRequestUpdateDto.city() != null && !userRequestUpdateDto.city().isEmpty() ?
                userRequestUpdateDto.city() : user.getCity());
        user.setAddress(userRequestUpdateDto.address() != null && !userRequestUpdateDto.address().isEmpty() ?
                userRequestUpdateDto.address() : user.getAddress());
        user.setPhone(userRequestUpdateDto.phone() != null && !userRequestUpdateDto.phone().isEmpty() ?
                userRequestUpdateDto.phone() : user.getPhone());
        user.setBirthDay(userRequestUpdateDto.birthDay() != null && !userRequestUpdateDto.birthDay().isEmpty() ?
                userRequestUpdateDto.birthDay() : user.getBirthDay());
        user.setFamilyStatus(userRequestUpdateDto.familyStatus() != null && !userRequestUpdateDto.familyStatus().name().isEmpty() ?
                userRequestUpdateDto.familyStatus() : user.getFamilyStatus());
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