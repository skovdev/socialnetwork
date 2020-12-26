package local.socialnetwork.kafka.model.dto;

import java.util.UUID;

public class GroupUserIdsDto {

    private UUID groupId;
    private UUID userId;

    public UUID getGroupId() {
        return groupId;
    }

    public void setGroupId(UUID groupId) {
        this.groupId = groupId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}