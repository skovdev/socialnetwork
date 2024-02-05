package local.socialnetwork.userservice.service.impl;

import local.socialnetwork.userservice.kafka.producer.profile.ProfileProducer;

import local.socialnetwork.userservice.model.dto.user.UserDto;

import local.socialnetwork.userservice.model.entity.user.User;

import local.socialnetwork.userservice.repository.UserRepository;

import local.socialnetwork.userservice.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Value("${sn.kafka.topic.profile.new}")
    String kafkaTopicNewProfile;

    final UserRepository userRepository;
    final ProfileProducer profileProducer;

    @Override
    public UserDto findById(UUID id) {
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
                ).findFirst().orElse(null);
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
        profileProducer.sendProfileAndSave(kafkaTopicNewProfile, user.getId());
    }

    @Override
    public void deleteById(UUID id) {
        userRepository.deleteById(id);
    }
}