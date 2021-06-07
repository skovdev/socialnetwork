package local.socialnetwork.profileservice.events;

import local.socialnetwork.profileservice.model.dto.user.UserDto;

import java.util.UUID;

public class ProfileCreatedEvent {

    private UUID id;
    private boolean isActive;
    private String avatar;
    private UserDto user;


    public ProfileCreatedEvent(UUID id, boolean isActive, String avatar, UserDto user) {
        this.id = id;
        this.isActive = isActive;
        this.avatar = avatar;
        this.user = user;
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

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }
}