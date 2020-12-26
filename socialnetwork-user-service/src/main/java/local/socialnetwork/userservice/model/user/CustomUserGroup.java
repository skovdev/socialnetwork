package local.socialnetwork.userservice.model.user;

import com.fasterxml.jackson.annotation.JsonProperty;

import local.socialnetwork.userservice.model.AbstractBaseModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "sn_user_group")
public class CustomUserGroup extends AbstractBaseModel {

    @Column(name = "group_id")
    private UUID groupId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private CustomUser user;

    public UUID getGroupId() {
        return groupId;
    }

    @JsonProperty(value = "groupId")
    public void setGroupId(UUID groupId) {
        this.groupId = groupId;
    }

    public CustomUser getUser() {
        return user;
    }

    @JsonProperty(value = "user")
    public void setUser(CustomUser user) {
        this.user = user;
    }
}