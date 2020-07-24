package local.socialnetwork.model.dto;

import local.socialnetwork.model.group.GroupType;

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