package local.socialnetwork.model.dto;

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