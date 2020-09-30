package local.socialnetwork.authservice.model;

import java.util.List;
import java.util.UUID;

public class CustomUser {

    private UUID id;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private CustomUserDetails userDetails;
    private List<CustomRole> roles;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public CustomUserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(CustomUserDetails userDetails) {
        this.userDetails = userDetails;
    }

    public List<CustomRole> getRoles() {
        return roles;
    }

    public void setRoles(List<CustomRole> roles) {
        this.roles = roles;
    }
}