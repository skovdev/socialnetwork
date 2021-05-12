package local.socialnetwork.profileservice.query;

public class FindAvatarByUsernameQuery {

    private String username;

    public FindAvatarByUsernameQuery(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
