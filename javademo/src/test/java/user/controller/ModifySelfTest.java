package user.controller;

import com.alibaba.fastjson.JSONObject;
import com.hibob.anyim.client.client.UserClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertTrue;

@Slf4j
public class ModifySelfTest {
    private static UserClient user01 = new UserClient(
            "account_test01",
            "clientId_test01",
            "headImage_test01",
            "inviteCode_test01",
            "nickName_test01",
            "password_test01",
            "phoneNum_test01"
    );
    private static UserClient user01_new = new UserClient(user01);

    @Before
    public void beforeTest() throws Exception {
        user01_new.setNickName("nick_name_test01_new");
        user01_new.setHeadImage("head_image_test01_new");
        user01_new.setHeadImageThumb("head_image_thumb_test01_new");
        user01_new.setSex("1");
        user01_new.setLevel("0");
        user01_new.setSignature("signature_test01_new");
        if (user01.validateAccount()) {
            user01.login();
            user01.deregister();
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
        ResponseEntity<String> response1 = user01.register();
        // 登录
        ResponseEntity<String> response2 = user01.login();
        // 查询self
        ResponseEntity<String> response3 = user01.querySelf();
        // 修改self
        ResponseEntity<String> response4 = user01.modifySelf(user01_new.getNickName(),
                user01_new.getHeadImage(),
                user01_new.getHeadImageThumb(),
                user01_new.getSex(),
                user01_new.getLevel(),
                user01_new.getSignature());
        // 查询self
        ResponseEntity<String> response5 = user01.querySelf();

        assertTrue(JSONObject.parseObject(response3.getBody()).getJSONObject("data").getString("nickName").equals(user01.getNickName()));
        assertTrue(JSONObject.parseObject(response5.getBody()).getJSONObject("data").getString("nickName").equals(user01_new.getNickName()));

    }

    @After
    public void afterTest() throws Exception {
        if (user01.validateAccount()) {
            user01.login();
            user01.deregister();
        }
    }

}
