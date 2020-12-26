package local.socialnetwork.groupservice.model.group;

import com.fasterxml.jackson.annotation.JsonProperty;

import local.socialnetwork.groupservice.model.AbstractBaseModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "sn_group_user")
public class GroupUser extends AbstractBaseModel {

    @Column(name = "user_id")
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    public UUID getUserId() {
        return userId;
    }

    @JsonProperty(value = "userId")
    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Group getGroup() {
        return group;
    }

    @JsonProperty(value = "group")
    public void setGroup(Group group) {
        this.group = group;
    }
}