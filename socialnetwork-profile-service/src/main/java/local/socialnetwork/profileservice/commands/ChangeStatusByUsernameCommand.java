package local.socialnetwork.profileservice.commands;

public class ChangeStatusByUsernameCommand {

    private String username;
    private boolean isActive;

    public ChangeStatusByUsernameCommand(String username, boolean isActive) {
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