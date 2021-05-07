package local.socialnetwork.profileservice.commands;

import org.springframework.web.multipart.MultipartFile;

public class UpdateAvatarCommand {

    private String username;
    private MultipartFile multipartFile;

    public UpdateAvatarCommand(String username, MultipartFile multipartFile) {
        this.username = username;
        this.multipartFile = multipartFile;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public MultipartFile getMultipartFile() {
        return multipartFile;
    }

    public void setMultipartFile(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
    }
}