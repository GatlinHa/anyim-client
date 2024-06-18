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
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

@Slf4j
public class CreateGroupTest {

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
        group01.setUserLocal(user01);
        List<Map<String, Object>> members = new ArrayList<>();
        members.add(new HashMap<String, Object>(){{
            put("memberAccount", user01.getAccount());
            put("memberRole", 3);
        }});
        members.add(new HashMap<String, Object>(){{
            put("memberAccount", user02.getAccount());
            put("memberRole", 0);
        }});
        members.add(new HashMap<String, Object>(){{
            put("memberAccount", user03.getAccount());
            put("memberRole", 0);
        }});
        members.add(new HashMap<String, Object>(){{
            put("memberAccount", user04.getAccount());
            put("memberRole", 0);
        }});
        group01.setMembers(members);
    }

    @Test
    public void test01() throws Exception {
        log.info("===>正在执行Test，Class: [{}]，Method: [{}]", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());
        ResponseEntity<String> response = GroupMngClient.createGroup(group01);
        assertTrue(JSONObject.parseObject(response.getBody()).getJSONObject("data").getJSONArray("members").size() == 4);

        UserClient.login(user02);
        UserClient.deregister(user02);
        response = GroupMngClient.createGroup(group01);
        assertTrue(JSONObject.parseObject(response.getBody()).getJSONObject("data").getJSONArray("members").size() == 3);

        UserClient.login(user03);
        UserClient.deregister(user03);
        response = GroupMngClient.createGroup(group01);
        assertTrue(Integer.valueOf(JSONObject.parseObject(response.getBody()).getString("code")) == 501);

        UserClient.login(user04);
        UserClient.deregister(user04);
        response = GroupMngClient.createGroup(group01);
        assertTrue(Integer.valueOf(JSONObject.parseObject(response.getBody()).getString("code")) == 501);

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
