package local.socialnetwork.profileservice.events;

public class StatusChangedEvent {

    private boolean isActive;
    private String username;

    public StatusChangedEvent(boolean isActive, String username) {
        this.isActive = isActive;
        this.username = username;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}