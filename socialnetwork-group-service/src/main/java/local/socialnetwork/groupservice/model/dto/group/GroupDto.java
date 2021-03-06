package local.socialnetwork.groupservice.model.dto.group;

import local.socialnetwork.groupservice.type.GroupType;

public class GroupDto {

    private String groupName;
    private GroupType groupType;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public GroupType getGroupType() {
        return groupType;
    }

    public void setGroupType(GroupType groupType) {
        this.groupType = groupType;
    }
}