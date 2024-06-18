package user.controller;

import com.alibaba.fastjson.JSONObject;
import com.hibob.anyim.client.UserClient;
import com.hibob.anyim.consts.Users;
import com.hibob.anyim.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertTrue;

@Slf4j
public class ModifySelfTest {
    private static User user01 = Users.ACCOUNT_01_CLIENTID_01;
    private static User user01_new = new User(user01);

    @Before
    public void beforeTest() throws Exception {
        user01_new.setNickName("nick_name_test01_new");
        user01_new.setAvatar("avatar_test01_new");
        user01_new.setAvatarThumb("avatar_thumb_test01_new");
        user01_new.setSex("1");
        user01_new.setLevel("0");
        user01_new.setSignature("signature_test01_new");
        if (UserClient.validateAccount(user01)) {
            UserClient.login(user01);
            UserClient.deregister(user01);
        }
    }

    /**
     * 注册 -> 登录 -> 查询self -> 修改self -> 查询self
     * @throws Exception
     */
    @Test
    public void test01() throws Exception {
        log.info("===>正在执行Test，Class: [{}]，Method: [{}]", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());
        // 注册
        ResponseEntity<String> response1 = UserClient.register(user01);
        // 登录
        ResponseEntity<String> response2 = UserClient.login(user01);
        // 查询self
        ResponseEntity<String> response3 = UserClient.querySelf(user01);
        // 修改self
        ResponseEntity<String> response4 = UserClient.modifySelf(user01, user01_new.getNickName(),
                user01_new.getAvatar(),
                user01_new.getAvatarThumb(),
                user01_new.getSex(),
                user01_new.getLevel(),
                user01_new.getSignature());
        // 查询self
        ResponseEntity<String> response5 = UserClient.querySelf(user01);

        assertTrue(JSONObject.parseObject(response3.getBody()).getJSONObject("data").getString("nickName").equals(user01.getNickName()));
        assertTrue(JSONObject.parseObject(response5.getBody()).getJSONObject("data").getString("nickName").equals(user01_new.getNickName()));

    }

    @After
    public void afterTest() throws Exception {
        if (UserClient.validateAccount(user01)) {
            UserClient.login(user01);
            UserClient.deregister(user01);
        }
    }

}
