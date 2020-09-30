package local.socialnetwork.authservice.model;

public class CustomRole {

    private String authority;
    private CustomUser userId;

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public CustomUser getUserId() {
        return userId;
    }

    public void setUserId(CustomUser userId) {
        this.userId = userId;
    }
}