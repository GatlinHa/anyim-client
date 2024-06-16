package groupmng.controller;

import com.alibaba.fastjson.JSONObject;
import com.hibob.anyim.client.GroupMngClient;
import com.hibob.anyim.client.UserClient;
import com.hibob.anyim.consts.Users;
import com.hibob.anyim.entity.Group;
import com.hibob.anyim.entity.User;
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

    private static Group group01 = new Group(
            null,
            0,
            "test_group_01",
            "暂无公告",
            "test_avatar_01",
            new ArrayList<>()
    );

    private static Group group02 = new Group(
            null,
            0,
            "test_group_02",
            "暂无公告",
            "test_avatar_02",
            new ArrayList<>()
    );

    private static Group group03 = new Group(
            null,
            0,
            "test_group_03",
            "暂无公告",
            "test_avatar_03",
            new ArrayList<>()
    );

    private static User user01 = Users.ACCOUNT_01_CLIENTID_01;
    private static User user02 = Users.ACCOUNT_02_CLIENTID_01;
    private static User user03 = Users.ACCOUNT_03_CLIENTID_01;
    private static User user04 = Users.ACCOUNT_04_CLIENTID_01;

    @Before
    public void beforeTest() throws Exception {
        if (!UserClient.validateAccount(user01)) {
            UserClient.register(user01);
        }
        if (!UserClient.validateAccount(user02)) {
            UserClient.register(user02);
        }
        if (!UserClient.validateAccount(user03)) {
            UserClient.register(user03);
        }
        if (!UserClient.validateAccount(user04)) {
            UserClient.register(user04);
        }
        UserClient.login(user01);
    }

    private static void addMember(Group group, User user, int role) {
        group.getMembers().add(new HashMap<String, Object>(){{
            put("memberAccount", user.getAccount());
            put("memberRole", role);
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

        GroupMngClient.createGroup(group01);
        GroupMngClient.createGroup(group02);
        GroupMngClient.createGroup(group03);

        ResponseEntity<String> response = GroupMngClient.queryGroupList(user01);

        assertTrue(JSONObject.parseObject(response.getBody()).getJSONArray("data").size() == 3);

    }

    @After
    public void afterTest() throws Exception {
        if (UserClient.validateAccount(user01)) {
            UserClient.login(user01);
            UserClient.deregister(user01);
        }
        if (UserClient.validateAccount(user02)) {
            UserClient.login(user02);
            UserClient.deregister(user02);
        }
        if (UserClient.validateAccount(user03)) {
            UserClient.login(user03);
            UserClient.deregister(user03);
        }
        if (UserClient.validateAccount(user04)) {
            UserClient.login(user04);
            UserClient.deregister(user04);
        }
        //TODO 删除这个用户创建的群组
    }

}
