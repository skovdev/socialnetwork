package local.socialnetwork.profileservice.query;

import java.util.UUID;

public class FindProfileByUserIdQuery {

    private UUID userId;

    public FindProfileByUserIdQuery(UUID userId) {
        this.userId = userId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
