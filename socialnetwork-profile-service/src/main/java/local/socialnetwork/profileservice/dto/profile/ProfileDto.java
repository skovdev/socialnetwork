package local.socialnetwork.profileservice.dto.profile;

import local.socialnetwork.profileservice.dto.user.UserDto;

public class ProfileDto {

    private boolean isActive;
    private UserDto user;

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