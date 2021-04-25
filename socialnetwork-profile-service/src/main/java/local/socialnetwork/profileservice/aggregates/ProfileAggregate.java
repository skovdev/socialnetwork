package local.socialnetwork.profileservice.aggregates;

import local.socialnetwork.profileservice.commands.ChangePasswordCommand;
import local.socialnetwork.profileservice.commands.ChangeStatusByUsernameCommand;
import local.socialnetwork.profileservice.commands.CreateProfileCommand;

import local.socialnetwork.profileservice.events.PasswordChangedEvent;
import local.socialnetwork.profileservice.events.ProfileCreatedEvent;

import local.socialnetwork.profileservice.events.StatusChangedEvent;

import org.axonframework.commandhandling.CommandHandler;

import org.axonframework.eventsourcing.EventSourcingHandler;

import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;

import org.axonframework.spring.stereotype.Aggregate;

import java.util.UUID;

@Aggregate
public class ProfileAggregate {

    @AggregateIdentifier
    private UUID id;
    private boolean isActive;
    private String avatar;
    private UUID userId;
    private String username;
    private String newPassword;

    public ProfileAggregate() {

    }

    public ProfileAggregate(UUID id, boolean isActive, String avatar, UUID userId) {
        this.id = id;
        this.isActive = isActive;
        this.avatar = avatar;
        this.userId = userId;
    }

    @CommandHandler
    public ProfileAggregate(CreateProfileCommand createProfileCommand) {

        AggregateLifecycle.apply(new ProfileCreatedEvent(
                createProfileCommand.getId(),
                createProfileCommand.isActive(),
                createProfileCommand.getAvatar(),
                createProfileCommand.getUserId()));

    }

    @CommandHandler
    public void handle(ChangeStatusByUsernameCommand command) {

        AggregateLifecycle.apply(new ChangeStatusByUsernameCommand(
                command.isActive(),
                command.getUsername()
        ));
    }

    @CommandHandler
    public void handle(ChangePasswordCommand command) {

        AggregateLifecycle.apply(new ChangePasswordCommand(
                command.getUsername(),
                command.getNewPassword())
        );
    }

    @EventSourcingHandler
    public void on(ProfileCreatedEvent profileCreatedEvent) {
        this.id = profileCreatedEvent.getId();
        this.isActive = profileCreatedEvent.isActive();
        this.avatar = profileCreatedEvent.getAvatar();
        this.userId = profileCreatedEvent.getUserId();
    }

    @EventSourcingHandler
    public void on(StatusChangedEvent statusChangedEvent) {
        this.isActive = statusChangedEvent.isActive();
        this.username = statusChangedEvent.getUsername();
    }

    @EventSourcingHandler
    public void on(PasswordChangedEvent passwordChangedEvent) {
        this.username = passwordChangedEvent.getUsername();
        this.newPassword = passwordChangedEvent.getNewPassword();
    }

    public UUID getId() {
        return id;
    }

    public boolean isActive() {
        return isActive;
    }

    public String getAvatar() {
        return avatar;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }
}