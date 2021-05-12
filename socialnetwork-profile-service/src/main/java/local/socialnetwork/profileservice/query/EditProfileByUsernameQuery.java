package local.socialnetwork.profileservice.query;

public class EditProfileByUsernameQuery {

    private String username;

    public EditProfileByUsernameQuery(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
