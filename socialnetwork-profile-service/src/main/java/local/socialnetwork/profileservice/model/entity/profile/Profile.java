package local.socialnetwork.profileservice.model.entity.profile;

import com.fasterxml.jackson.annotation.JsonProperty;

import local.socialnetwork.profileservice.model.entity.AbstractBaseModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "sn_profile")
public class Profile extends AbstractBaseModel {

    @Column(name = "is_active")
    private boolean isActive;

    @Lob
    @Column(name = "avatar")
    private String avatar;

    @Column(name = "auth_user_id", nullable = false, unique = true)
    private UUID authUserId;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    public boolean isActive() {
        return isActive;
    }

    @JsonProperty(value = "isActive")
    public void setActive(boolean active) {
        isActive = active;
    }

    public String getAvatar() {
        return avatar;
    }

    @JsonProperty(value = "avatar")
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public UUID getAuthUserId() {
        return authUserId;
    }

    @JsonProperty(value = "authUserId")
    public void setAuthUserId(UUID authUserId) {
        this.authUserId = authUserId;
    }

    public UUID getUserId() {
        return userId;
    }

    @JsonProperty(value = "userId")
    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}