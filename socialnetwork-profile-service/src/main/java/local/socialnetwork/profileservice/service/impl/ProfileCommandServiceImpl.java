package local.socialnetwork.profileservice.service.impl;

import local.socialnetwork.profileservice.commands.ChangePasswordCommand;
import local.socialnetwork.profileservice.commands.ChangeStatusByUsernameCommand;
import local.socialnetwork.profileservice.commands.CheckValidOldPasswordCommand;
import local.socialnetwork.profileservice.commands.CreateProfileCommand;

import local.socialnetwork.profileservice.commands.DeleteAvatarCommand;
import local.socialnetwork.profileservice.commands.UpdateAvatarCommand;
import local.socialnetwork.profileservice.commands.UpdateProfileCommand;

import local.socialnetwork.profileservice.model.dto.profile.ChangePasswordDto;
import local.socialnetwork.profileservice.model.dto.profile.EditProfileDto;
import local.socialnetwork.profileservice.model.dto.profile.ProfileDto;

import local.socialnetwork.profileservice.service.ProfileCommandService;

import org.axonframework.commandhandling.gateway.CommandGateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class ProfileCommandServiceImpl implements ProfileCommandService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileCommandServiceImpl.class);

    private CommandGateway commandGateway;

    @Autowired
    public void setCommandGateway(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @Override
    public void createProfile(ProfileDto profileDto) {

        LOGGER.info("### Creating profile ###");

        this.commandGateway.send(new CreateProfileCommand(
                profileDto.getId(),
                profileDto.isActive(),
                profileDto.getAvatar(),
                profileDto.getUser()
        ));
    }

    @Override
    public void updateProfile(UUID id, EditProfileDto editProfileDto) {

        LOGGER.info("### Updating profile ###");

        this.commandGateway.send(new UpdateProfileCommand(
                id,
                editProfileDto.getFirstName(),
                editProfileDto.getLastName(),
                editProfileDto.getCountry(),
                editProfileDto.getCity(),
                editProfileDto.getAddress(),
                editProfileDto.getPhone(),
                editProfileDto.getBirthday(),
                editProfileDto.getFamilyStatus()
        ));
    }

    @Override
    public void updateAvatarProfile(String username, MultipartFile multipartFile) {

        LOGGER.info("### Updating avatar profile ###");

        this.commandGateway.send(new UpdateAvatarCommand(
                username,
                multipartFile)
        );
    }

    @Override
    public String setDefaultAvatar(String username) {
        return this.commandGateway.sendAndWait(new DeleteAvatarCommand(username));
    }

    @Override
    public boolean changeStatus(String username, boolean isActive) {
        return this.commandGateway.sendAndWait(new ChangeStatusByUsernameCommand(username, isActive));
    }

    @Override
    public boolean checkIfValidOldPassword(ChangePasswordDto changePasswordDto) {

        return this.commandGateway.sendAndWait(new CheckValidOldPasswordCommand(
                changePasswordDto.getUsername(),
                changePasswordDto.getOldPassword(),
                changePasswordDto.getNewPassword()
        ));
    }

    @Override
    public void changePassword(String username, String newPassword) {
        this.commandGateway.send(new ChangePasswordCommand(username, newPassword));
    }
}