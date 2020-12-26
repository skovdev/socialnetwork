package local.socialnetwork.groupservice.model.entity.group;

import com.fasterxml.jackson.annotation.JsonProperty;

import local.socialnetwork.groupservice.model.AbstractBaseModel;

import local.socialnetwork.groupservice.type.GroupStatus;
import local.socialnetwork.groupservice.type.GroupType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "sn_group")
public class Group extends AbstractBaseModel {

    @Column(name = "group_name")
    private String name;

    @Lob
    @Column(name = "group_avatar")
    private String avatar;

    @Column(name = "group_type")
    private GroupType groupType;

    @Column(name = "group_status")
    private GroupStatus groupStatus;

    @Column(name = "group_amount_users")
    private long groupAmountUsers;

    public Group() {

    }

    public String getName() {
        return name;
    }

    @JsonProperty(value = "groupName")
    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    @JsonProperty(value = "avatar")
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public GroupType getGroupType() {
        return groupType;
    }

    @JsonProperty(value = "groupType")
    public void setGroupType(GroupType groupType) {
        this.groupType = groupType;
    }

    public GroupStatus getGroupStatus() {
        return groupStatus;
    }

    @JsonProperty(value = "groupStatus")
    public void setGroupStatus(GroupStatus groupStatus) {
        this.groupStatus = groupStatus;
    }

    public long getGroupAmountUsers() {
        return groupAmountUsers;
    }

    @JsonProperty(value = "groupAmountUsers")
    public void setGroupAmountUsers(long groupAmountUsers) {
        this.groupAmountUsers = groupAmountUsers;
    }
}