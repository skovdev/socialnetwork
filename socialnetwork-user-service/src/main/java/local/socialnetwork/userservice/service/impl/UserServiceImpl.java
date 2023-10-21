package local.socialnetwork.userservice.service.impl;

import local.socialnetwork.userservice.kafka.producer.profile.ProfileProducer;
import local.socialnetwork.userservice.model.entity.User;

import local.socialnetwork.userservice.repository.UserRepository;

import local.socialnetwork.userservice.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import java.util.UUID;

@Service
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Value("${sn.kafka.topic.profile.new}")
    String kafkaTopicNewProfile;

    final UserRepository userRepository;
    final ProfileProducer profileProducer;

    @Override
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
        profileProducer.sendProfileAndSave(kafkaTopicNewProfile, user.getAuthUserId(), user.getId());
    }

    @Override
    public void deleteById(UUID id) {
        userRepository.deleteById(id);
    }
}