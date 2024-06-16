package groupmng.controller;

import com.alibaba.fastjson.JSONObject;
import com.hibob.anyim.client.GroupMngClient;
import com.hibob.anyim.client.UserClient;
import com.hibob.anyim.consts.Users;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertTrue;

@Slf4j
public class QueryGroupListTest {

    private static GroupMngClient group01 = new GroupMngClient(
            null,
            0,
            "test_group_01",
            "暂无公告",
            "test_avatar_01",
            new ArrayList<>()
    );

    private static GroupMngClient group02 = new GroupMngClient(
            null,
            0,
            "test_group_02",
            "暂无公告",
            "test_avatar_02",
            new ArrayList<>()
    );

    private static GroupMngClient group03 = new GroupMngClient(
            null,
            0,
            "test_group_03",
            "暂无公告",
            "test_avatar_03",
            new ArrayList<>()
    );

    private static UserClient user01 = Users.ACCOUNT_01_CLIENTID_01;
    private static UserClient user02 = Users.ACCOUNT_02_CLIENTID_01;
    private static UserClient user03 = Users.ACCOUNT_03_CLIENTID_01;
    private static UserClient user04 = Users.ACCOUNT_04_CLIENTID_01;

    @Before
    public void beforeTest() throws Exception {
        if (!user01.validateAccount()) {
            user01.register();
        }
        if (!user02.validateAccount()) {
            user02.register();
        }
        if (!user03.validateAccount()) {
            user03.register();
        }
        if (!user04.validateAccount()) {
            user04.register();
        }
        user01.login();
    }

    private static void addMember(GroupMngClient group, UserClient user, int role) {
        group.getMembers().add(new HashMap<String, Object>(){{
            put("memberAccount", user01.getAccount());
            put("memberRole", 3);
        }});
    }

    @Test
    public void test01() throws Exception {
        log.info("===>正在执行Test，Class: [{}]，Method: [{}]", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());
        group01.setUserLocal(user01);
        addMember(group01, user01, 3);
        addMember(group01, user02, 0);
        addMember(group01, user03, 0);

        group02.setUserLocal(user01);
        addMember(group02, user01, 3);
        addMember(group02, user02, 0);
        addMember(group02, user04, 0);

        group03.setUserLocal(user01);
        addMember(group03, user01, 3);
        addMember(group03, user03, 0);
        addMember(group03, user04, 0);


        group01.createGroup();
        group02.createGroup();
        group03.createGroup();

        ResponseEntity<String> response = group01.queryGroupList();

        Long groupId = JSONObject.parseObject(response.getBody()).getJSONObject("data").getJSONObject("groupInfo").getLong("groupId");
        group01.setGroupId(groupId);

        response = group01.queryGroupInfo();
        assertTrue(JSONObject.parseObject(response.getBody()).getJSONObject("data").getJSONArray("members").size() == 3);

        group01.setUserLocal(user04);
        response = group01.queryGroupInfo();
        assertTrue(Integer.valueOf(JSONObject.parseObject(response.getBody()).getString("code")) == 502);
    }

    @After
    public void afterTest() throws Exception {
        if (user01.validateAccount()) {
            user01.login();
            user01.deregister();
        }
        if (user02.validateAccount()) {
            user02.login();
            user02.deregister();
        }
        if (user03.validateAccount()) {
            user03.login();
            user03.deregister();
        }
        if (user04.validateAccount()) {
            user04.login();
            user04.deregister();
        }
        //TODO 删除这个用户创建的群组
    }

}
