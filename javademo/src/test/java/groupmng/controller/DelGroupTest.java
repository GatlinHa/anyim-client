package groupmng.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hibob.anyim.client.GroupMngClient;
import com.hibob.anyim.client.UserClient;
import com.hibob.anyim.consts.Groups;
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
public class DelGroupTest {

    private static Group group01 = Groups.GROUP_1;

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
        UserClient.login(user02);
        UserClient.login(user03);
        UserClient.login(user04);

        group01.getMembers().clear();
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

        ResponseEntity<String> response_group01 = GroupMngClient.createGroup(group01);
        Long groupId = JSONObject.parseObject(response_group01.getBody()).getJSONObject("data").getJSONObject("groupInfo").getLong("groupId");
        assertTrue(groupId > 0);

        // user02不是群主，不能删除
        ResponseEntity<String> response = GroupMngClient.delGroup(user02, groupId);
        assertTrue(Integer.valueOf(JSONObject.parseObject(response.getBody()).getString("code")) == 504);

        // user04不是群成员，不能删除
        response = GroupMngClient.delGroup(user04, groupId);
        assertTrue(Integer.valueOf(JSONObject.parseObject(response.getBody()).getString("code")) == 504);

        // user01是群主，可以删除
        response = GroupMngClient.delGroup(user01, groupId);
        assertTrue(Integer.valueOf(JSONObject.parseObject(response.getBody()).getString("code")) == 0);

        // 删除后查询不到群组
        response = GroupMngClient.queryGroupInfo(group01);
        assertTrue(JSONObject.parseObject(response.getBody()).getJSONArray("data") == null);
    }

    @After
    public void afterTest() throws Exception {
        //删除这个用户创建的群组
        ResponseEntity<String> response = GroupMngClient.queryGroupList(user01);
        JSONArray array = JSONObject.parseObject(response.getBody()).getJSONArray("data");
        if (array != null && !array.isEmpty()) {
            for (Object o : array) {
                JSONObject group = (JSONObject) o;
                Long groupId = group.getLong("groupId");
                GroupMngClient.delGroup(user01, groupId);
            }
        }

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

    }

}
