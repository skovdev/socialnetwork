package local.socialnetwork.userservice.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import local.socialnetwork.userservice.model.AbstractBaseModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "sn_user_group")
public class UserGroup extends AbstractBaseModel {

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "group_id")
    private UUID groupId;

    public UUID getUserId() {
        return userId;
    }

    @JsonProperty(value = "userId")
    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getGroupId() {
        return groupId;
    }

    @JsonProperty(value = "groupId")
    public void setGroupId(UUID groupId) {
        this.groupId = groupId;
    }
}