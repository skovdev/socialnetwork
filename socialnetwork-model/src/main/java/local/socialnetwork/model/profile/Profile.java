package local.socialnetwork.model.profile;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import local.socialnetwork.model.AbstractBaseModel;

import local.socialnetwork.model.user.CustomUser;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.OneToOne;
import javax.persistence.JoinColumn;
import javax.persistence.FetchType;
import javax.persistence.Lob;

@Entity
@Table(name = "sn_profile")
public class Profile extends AbstractBaseModel {

    @Column(name = "is_active")
    private boolean isActive;

    @Lob
    @Column(name = "avatar")
    private String avatar;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_user")
    private CustomUser customUser;

    public Profile() {

    }

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

    @JsonManagedReference
    public CustomUser getCustomUser() {
        return customUser;
    }

    @JsonProperty(value = "customUser")
    public void setCustomUser(CustomUser customUser) {
        this.customUser = customUser;
    }
}