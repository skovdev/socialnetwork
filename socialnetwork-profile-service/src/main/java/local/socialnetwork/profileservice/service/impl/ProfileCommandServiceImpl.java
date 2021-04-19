package local.socialnetwork.profileservice.service.impl;

import local.socialnetwork.profileservice.commands.CreateProfileCommand;

import local.socialnetwork.profileservice.model.dto.profile.ProfileDto;

import local.socialnetwork.profileservice.service.ProfileCommandService;

import org.axonframework.commandhandling.gateway.CommandGateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class ProfileCommandServiceImpl implements ProfileCommandService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileCommandServiceImpl.class);

    private CommandGateway commandGateway;

    @Autowired
    public void setCommandGateway(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @Override
    public CompletableFuture<String> createProfile(ProfileDto profileDto) {

        LOGGER.info("### Creating profile ###");

        return this.commandGateway.send(new CreateProfileCommand(
                profileDto.getId(),
                profileDto.isActive(),
                profileDto.getAvatar(),
                profileDto.getUser().getId()));

    }
}