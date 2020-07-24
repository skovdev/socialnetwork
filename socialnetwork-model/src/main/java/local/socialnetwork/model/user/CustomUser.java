package local.socialnetwork.model.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import local.socialnetwork.model.AbstractBaseModel;

import local.socialnetwork.model.group.Group;

import local.socialnetwork.model.profile.Profile;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.ManyToMany;
import javax.persistence.CascadeType;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "sn_users")
public class CustomUser extends AbstractBaseModel {

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Embedded
    private CustomUserDetails customUserDetails;

    @OneToOne(mappedBy = "customUser", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Profile profile;

    @OneToMany(mappedBy = "customUser", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<CustomRole> customRoles;

    @ManyToMany(mappedBy = "users", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Group> groups = new HashSet<>();

    public CustomUser() {

    }

    public String getFirstName() {
        return firstName;
    }

    @JsonProperty(value = "firstName")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @JsonProperty(value = "lastName")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    @JsonProperty(value = "username")
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    @JsonProperty(value = "password")
    public void setPassword(String password) {
        this.password = password;
    }

    @JsonManagedReference
    public CustomUserDetails getCustomUserDetails() {
        return customUserDetails;
    }

    @JsonProperty(value = "customUserDetails")
    public void setCustomUserDetails(CustomUserDetails customUserDetails) {
        this.customUserDetails = customUserDetails;
    }

    @JsonBackReference
    public Profile getProfile() {
        return profile;
    }

    @JsonProperty(value = "profile")
    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    @JsonBackReference
    public List<CustomRole> getCustomRoles() {
        return customRoles;
    }

    @JsonProperty(value = "customRoles")
    public void setCustomRoles(List<CustomRole> customRoles) {
        this.customRoles = customRoles;
    }

    public void addGroup(Group group) {
        groups.add(group);
        group.getUsers().add(this);
    }

    @JsonBackReference
    public Set<Group> getGroups() {
        return groups;
    }

    @JsonProperty(value = "groups")
    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CustomUser that = (CustomUser) o;

        return username.equals(that.username);

    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}