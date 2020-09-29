package local.socialnetwork.userservice.service.impl;

import local.socialnetwork.userservice.model.user.CustomUser;

import local.socialnetwork.userservice.repository.UserRepository;

import local.socialnetwork.userservice.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<CustomUser> findById(UUID id) {
        return userRepository.findById(id);
    }
}