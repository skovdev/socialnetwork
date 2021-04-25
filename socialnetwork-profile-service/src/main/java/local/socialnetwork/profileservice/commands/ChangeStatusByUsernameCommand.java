package local.socialnetwork.profileservice.commands;

public class ChangeStatusByUsernameCommand {

    private boolean isActive;
    private String username;

    public ChangeStatusByUsernameCommand() {

    }

    public ChangeStatusByUsernameCommand(boolean isActive, String username) {
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
