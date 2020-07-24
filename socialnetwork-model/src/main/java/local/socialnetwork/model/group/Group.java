package local.socialnetwork.model.group;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import local.socialnetwork.model.AbstractBaseModel;

import local.socialnetwork.model.user.CustomUser;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.ManyToMany;
import javax.persistence.FetchType;
import javax.persistence.CascadeType;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;

import java.util.Set;
import java.util.HashSet;
import java.util.Objects;

@Entity
@Table(name = "sn_groups")
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

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "sn_group_user",
               joinColumns = @JoinColumn(name = "group_id"),
               inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<CustomUser> users = new HashSet<>();

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

    @JsonProperty(value = "groupAvatar")
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

    public void addUser(CustomUser user) {
        users.add(user);
        user.getGroups().add(this);
    }

    @JsonManagedReference
    public Set<CustomUser> getUsers() {
        return users;
    }

    @JsonProperty(value = "users")
    public void setUsers(Set<CustomUser> users) {
        this.users = users;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Group group = (Group) o;

        return name.equals(group.name);

    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
