package local.socialnetwork.authservice.model.dto;

import java.util.UUID;

public class RoleDto {

    private UUID id;
    private String authority;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}