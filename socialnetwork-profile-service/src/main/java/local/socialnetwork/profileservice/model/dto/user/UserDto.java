package local.socialnetwork.profileservice.model.dto.user;

import local.socialnetwork.profileservice.model.dto.profile.ProfileDto;

import java.util.List;
import java.util.UUID;

public class UserDto {

    private UUID id;
    private String firstName;
    private String lastName;
    private String username;
    private UserDetailsDto userDetails;
    private List<RoleDto> roles;
    private ProfileDto profile;

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

    public UserDetailsDto getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetailsDto userDetails) {
        this.userDetails = userDetails;
    }

    public List<RoleDto> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleDto> roles) {
        this.roles = roles;
    }

    public ProfileDto getProfile() {
        return profile;
    }

    public void setProfile(ProfileDto profile) {
        this.profile = profile;
    }
}