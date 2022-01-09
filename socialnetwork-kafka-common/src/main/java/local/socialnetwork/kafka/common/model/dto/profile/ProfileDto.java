package local.socialnetwork.kafka.common.model.dto.profile;

import java.util.UUID;

public class ProfileDto {

    private String avatar;
    private boolean isActive;
    private UUID userId;

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

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}