package local.socialnetwork.profileservice.events;

public class AvatarDeletedEvent {

    private String username;

    public AvatarDeletedEvent(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}