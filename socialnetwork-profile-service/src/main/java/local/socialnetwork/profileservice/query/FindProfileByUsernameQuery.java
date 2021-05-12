package local.socialnetwork.profileservice.query;

public class FindProfileByUsernameQuery {

    private String username;

    public FindProfileByUsernameQuery(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
