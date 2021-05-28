package local.socialnetwork.profileservice.events;

public class StatusChangedEvent {

    private String username;
    private boolean isActive;

    public StatusChangedEvent(String username, boolean isActive) {
        this.username = username;
        this.isActive = isActive;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}