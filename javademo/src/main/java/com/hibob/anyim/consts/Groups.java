package com.hibob.anyim.consts;

import com.hibob.anyim.client.GroupMngClient;

public class Groups {
    public static final GroupMngClient GROUP_1 = new GroupMngClient(
            Users.ACCOUNT_01_CLIENTID_01,
            0,
            "test_group_01",
            "暂无公告",
            "test_avatar_01",
            null
    );

    public static final GroupMngClient GROUP_2 = new GroupMngClient(
            Users.ACCOUNT_01_CLIENTID_01,
            0,
            "test_group_02",
            "暂无公告",
            "test_avatar_02",
            null
    );

    public static final GroupMngClient GROUP_3 = new GroupMngClient(
            Users.ACCOUNT_02_CLIENTID_01,
            0,
            "test_group_03",
            "暂无公告",
            "test_avatar_03",
            null
    );

    public static final GroupMngClient GROUP_4 = new GroupMngClient(
            Users.ACCOUNT_02_CLIENTID_01,
            0,
            "test_group_04",
            "暂无公告",
            "test_avatar_04",
            null
    );
}
