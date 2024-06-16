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
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

@Slf4j
public class CreateGroupTest {

    private static GroupMngClient group01 = new GroupMngClient(
            null,
            0,
            "test_group_01",
            "暂无公告",
            "test_avatar_01",
            null
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
        ResponseEntity<String> response = group01.createGroup();
        assertTrue(JSONObject.parseObject(response.getBody()).getJSONObject("data").getJSONArray("members").size() == 4);

        user02.login();
        user02.deregister();
        response = group01.createGroup();
        assertTrue(JSONObject.parseObject(response.getBody()).getJSONObject("data").getJSONArray("members").size() == 3);

        user03.login();
        user03.deregister();
        response = group01.createGroup();
        assertTrue(Integer.valueOf(JSONObject.parseObject(response.getBody()).getString("code")) == 501);

        user04.login();
        user04.deregister();
        response = group01.createGroup();
        assertTrue(Integer.valueOf(JSONObject.parseObject(response.getBody()).getString("code")) == 501);

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
