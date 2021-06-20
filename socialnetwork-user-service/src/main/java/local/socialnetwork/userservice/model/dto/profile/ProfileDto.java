package local.socialnetwork.userservice.model.dto.profile;

import local.socialnetwork.userservice.model.dto.user.UserDto;

import java.util.UUID;

public class ProfileDto {

    private UUID id;
    private String avatar;
    private boolean isActive;
    private UserDto user;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }
}