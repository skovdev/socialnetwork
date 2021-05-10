package local.socialnetwork.profileservice.commands;

public class DeleteAvatarCommand {

    private String username;

    public DeleteAvatarCommand(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}