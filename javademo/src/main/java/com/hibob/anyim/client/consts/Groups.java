package com.hibob.anyim.client.consts;

import com.hibob.anyim.client.client.GroupClient;

public class Groups {
    public static final GroupClient GROUP_1 = new GroupClient(
            Users.ACCOUNT_01_CLIENTID_01,
            0,
            "test_group_01",
            "暂无公告",
            "test_avatar_01",
            null
    );

    public static final GroupClient GROUP_2 = new GroupClient(
            Users.ACCOUNT_01_CLIENTID_01,
            0,
            "test_group_02",
            "暂无公告",
            "test_avatar_02",
            null
    );

    public static final GroupClient GROUP_3 = new GroupClient(
            Users.ACCOUNT_02_CLIENTID_01,
            0,
            "test_group_03",
            "暂无公告",
            "test_avatar_03",
            null
    );

    public static final GroupClient GROUP_4 = new GroupClient(
            Users.ACCOUNT_02_CLIENTID_01,
            0,
            "test_group_04",
            "暂无公告",
            "test_avatar_04",
            null
    );
}
