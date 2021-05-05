package local.socialnetwork.profileservice.aggregates;

import local.socialnetwork.profileservice.commands.ChangePasswordCommand;
import local.socialnetwork.profileservice.commands.ChangeStatusByUsernameCommand;
import local.socialnetwork.profileservice.commands.CreateProfileCommand;
import local.socialnetwork.profileservice.commands.UpdateProfileCommand;

import local.socialnetwork.profileservice.events.PasswordChangedEvent;
import local.socialnetwork.profileservice.events.ProfileCreatedEvent;
import local.socialnetwork.profileservice.events.ProfileUpdatedEvent;
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
    private String firstName;
    private String lastName;
    private String country;
    private String city;
    private String address;
    private String phone;
    private String birthday;
    private String familyStatus;

    public ProfileAggregate() {

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

    @CommandHandler
    public void handle(UpdateProfileCommand updateProfileCommand) {

        AggregateLifecycle.apply(new UpdateProfileCommand(
                updateProfileCommand.getId(),
                updateProfileCommand.getFirstName(),
                updateProfileCommand.getLastName(),
                updateProfileCommand.getCountry(),
                updateProfileCommand.getCity(),
                updateProfileCommand.getAddress(),
                updateProfileCommand.getPhone(),
                updateProfileCommand.getBirthday(),
                updateProfileCommand.getFamilyStatus()
        ));
    }

    @EventSourcingHandler
    public void on(ProfileUpdatedEvent profileUpdatedEvent) {
        this.id = profileUpdatedEvent.getId();
        this.firstName = profileUpdatedEvent.getFirstName();
        this.lastName = profileUpdatedEvent.getLastName();
        this.country = profileUpdatedEvent.getCountry();
        this.city = profileUpdatedEvent.getCity();
        this.address = profileUpdatedEvent.getAddress();
        this.phone = profileUpdatedEvent.getPhone();
        this.birthday = profileUpdatedEvent.getBirthday();
        this.familyStatus = profileUpdatedEvent.getFamilyStatus();
    }

    @CommandHandler
    public void handle(ChangeStatusByUsernameCommand command) {

        AggregateLifecycle.apply(new ChangeStatusByUsernameCommand(
                command.isActive(),
                command.getUsername()
        ));
    }

    @EventSourcingHandler
    public void on(StatusChangedEvent statusChangedEvent) {
        this.isActive = statusChangedEvent.isActive();
        this.username = statusChangedEvent.getUsername();
    }

    @CommandHandler
    public void handle(ChangePasswordCommand command) {

        AggregateLifecycle.apply(new ChangePasswordCommand(
                command.getUsername(),
                command.getNewPassword())
        );
    }

    @EventSourcingHandler
    public void on(PasswordChangedEvent passwordChangedEvent) {
        this.username = passwordChangedEvent.getUsername();
        this.newPassword = passwordChangedEvent.getNewPassword();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getFamilyStatus() {
        return familyStatus;
    }

    public void setFamilyStatus(String familyStatus) {
        this.familyStatus = familyStatus;
    }
}