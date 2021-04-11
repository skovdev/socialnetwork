package local.socialnetwork.profileservice.aggregates;

import local.socialnetwork.profileservice.commands.CreateProfileCommand;

import local.socialnetwork.profileservice.events.ProfileCreatedEvent;

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

    @EventSourcingHandler
    public void on(ProfileCreatedEvent profileCreatedEvent) {
        this.id = profileCreatedEvent.getId();
        this.isActive = profileCreatedEvent.isActive();
        this.avatar = profileCreatedEvent.getAvatar();
        this.userId = profileCreatedEvent.getUserId();
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
}