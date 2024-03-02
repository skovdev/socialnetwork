package local.socialnetwork.profileservice.entity.profile;

import com.fasterxml.jackson.annotation.JsonProperty;

import local.socialnetwork.profileservice.entity.AbstractBaseModel;

import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import java.util.UUID;

@Entity
@Table(name = "sn_profile")
public class Profile extends AbstractBaseModel {

    @Column(name = "is_active")
    private boolean isActive;

    @Lob
    @Column(name = "avatar")
    private String avatar;

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

    public UUID getUserId() {
        return userId;
    }

    @JsonProperty(value = "userId")
    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}