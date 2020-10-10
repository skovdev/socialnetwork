package local.socialnetwork.userservice.model.user;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import local.socialnetwork.userservice.model.AbstractBaseModel;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

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
    private CustomUserDetails userDetails;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<CustomRole> roles;

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
    public CustomUserDetails getUserDetails() {
        return userDetails;
    }

    @JsonProperty(value = "userDetails")
    public void setUserDetails(CustomUserDetails userDetails) {
        this.userDetails = userDetails;
    }

    @JsonManagedReference
    public List<CustomRole> getRoles() {
        return roles;
    }

    @JsonProperty(value = "roles")
    public void setRoles(List<CustomRole> roles) {
        this.roles = roles;
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