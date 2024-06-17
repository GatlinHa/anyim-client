package groupmng.controller;

import com.alibaba.fastjson.JSONArray;
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
public class OwnerTransferTest {

    private static Group group01 = new Group(
            null,
            0,
            "test_group_01",
            "暂无公告",
            "test_avatar_01",
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

        ResponseEntity<String> response = GroupMngClient.createGroup(group01);
        Long groupId = JSONObject.parseObject(response.getBody()).getJSONObject("data").getJSONObject("groupInfo").getLong("groupId");
        group01.setGroupId(groupId);

        // user02不是群主，转让失败
        UserClient.login(user02);
        response = GroupMngClient.ownerTransfer(group01, user02, user03);
        assertTrue(JSONObject.parseObject(response.getBody()).getInteger("code") == 506);

        // user04不在此群，转让失败
        response = GroupMngClient.ownerTransfer(group01, user01, user04);
        assertTrue(JSONObject.parseObject(response.getBody()).getInteger("code") == 507);

        // user01转让给user02，转让成功
        response = GroupMngClient.ownerTransfer(group01, user01, user02);
        assertTrue(JSONObject.parseObject(response.getBody()).getInteger("code") == 0);

        response = GroupMngClient.queryGroupInfo(group01);
        JSONArray array = JSONObject.parseObject(response.getBody()).getJSONObject("data").getJSONArray("members");
        boolean ret1 = false;
        boolean ret2 = false;
        for (Object o : array) {
            JSONObject member = (JSONObject) o;
            if (member.getString("memberAccount").equals(user01.getAccount()) && member.getInteger("memberRole") == 2) {
                ret1 = true;
            }
            if (member.getString("memberAccount").equals(user02.getAccount()) && member.getInteger("memberRole") == 3) {
                ret2 = true;
            }
        }
        assertTrue(ret1 && ret2);

    }

    @After
    public void afterTest() throws Exception {
        //删除这个用户创建的群组
        ResponseEntity<String> response = GroupMngClient.queryGroupList(user02);
        JSONArray array = JSONObject.parseObject(response.getBody()).getJSONArray("data");
        if (array != null && !array.isEmpty()) {
            for (Object o : array) {
                JSONObject group = (JSONObject) o;
                Long groupId = group.getLong("groupId");
                GroupMngClient.delGroup(user02, groupId);
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

    }

}
