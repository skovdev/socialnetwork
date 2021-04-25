package local.socialnetwork.profileservice.events;

import java.util.UUID;

public class ProfileCreatedEvent {

    private UUID id;
    private boolean isActive;
    private String avatar;
    private UUID userId;

    public ProfileCreatedEvent(UUID id, boolean isActive, String avatar, UUID userId) {
        this.id = id;
        this.isActive = isActive;
        this.avatar = avatar;
        this.userId = userId;
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
}