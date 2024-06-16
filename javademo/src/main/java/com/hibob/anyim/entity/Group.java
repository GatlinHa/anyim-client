package com.hibob.anyim.entity;

import com.hibob.anyim.client.UserClient;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Group {
    private User userLocal;

    private long groupId;
    private int groupType;
    private String groupName;
    private String announcement;
    private String avatar;
    private List<Map<String, Object>> members;

    public Group(User user,
                 int groupType,
                 String groupName,
                 String announcement,
                 String avatar,
                 List<Map<String, Object>> members) {
        this.userLocal = user;
        this.groupType = groupType;
        this.groupName = groupName;
        this.announcement = announcement;
        this.avatar = avatar;
        this.members = members;
    }

    public Group(Group group) {
        this.groupId = group.getGroupId();
        this.userLocal = group.getUserLocal();
        this.groupType = group.getGroupType();
        this.groupName = group.groupName;
        this.announcement = group.getAnnouncement();
        this.avatar = group.getAvatar();
        this.members = group.getMembers();
    }
}
