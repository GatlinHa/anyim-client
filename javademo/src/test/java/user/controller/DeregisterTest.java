package user.controller;

import com.alibaba.fastjson.JSONObject;
import com.hibob.anyim.client.UserClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertTrue;

@Slf4j
public class DeregisterTest {

    private static UserClient user01 = new UserClient(
            "account_test01",
            "clientId_test01",
            "avatar_test01",
            "inviteCode_test01",
            "nickName_test01",
            "password_test01",
            "phoneNum_test01"
    );

    @Before
    public void beforeTest() throws Exception {
        if (user01.validateAccount()) {
            user01.login();
            user01.deregister();
        }
    }

    /**
     * 注册 -> 未登录注销 -> 登录 -> 注销 -> 校验账号唯一性 -> 重复注销
     * @throws Exception
     */
    @Test
    public void test01() throws Exception {
        log.info("===>正在执行Test，Class: [{}]，Method: [{}]", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());
        user01.register();
        ResponseEntity<String> response1 = user01.deregister();
        user01.login();
        ResponseEntity<String> response2 = user01.deregister();
        Boolean validated = user01.validateAccount();
        ResponseEntity<String> response3 = user01.deregister();


        assertTrue(response1.getStatusCode() == HttpStatus.UNAUTHORIZED);
        assertTrue(Integer.valueOf(JSONObject.parseObject(response2.getBody()).getString("code")) == 0);
        assertTrue(validated == false);
        assertTrue(response3.getStatusCode() == HttpStatus.UNAUTHORIZED);
    }

    @After
    public void afterTest() throws Exception {
        if (user01.validateAccount()) {
            user01.login();
            user01.deregister();
        }
    }

}
