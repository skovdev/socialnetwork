package local.socialnetwork.groupservice.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import local.socialnetwork.groupservice.model.AbstractBaseModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "sn_group_user")
public class GroupUser extends AbstractBaseModel {

    @Column(name = "group_id")
    private UUID groupId;

    @Column(name = "user_id")
    private UUID userId;

    public UUID getGroupId() {
        return groupId;
    }

    @JsonProperty(value = "groupId")
    public void setGroupId(UUID groupId) {
        this.groupId = groupId;
    }

    public UUID getUserId() {
        return userId;
    }

    @JsonProperty(value = "userId")
    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}