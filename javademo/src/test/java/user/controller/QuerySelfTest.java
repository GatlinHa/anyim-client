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
public class QuerySelfTest {
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
     * 查询 -> 注册 -> 查询 -> 登录 -> 查询 -> 登出 -> 查询
     * @throws Exception
     */
    @Test
    public void test01() throws Exception {
        log.info("===>正在执行Test，Class: [{}]，Method: [{}]", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());
        ResponseEntity<String> response1 = user01.querySelf();
        ResponseEntity<String> response2 = user01.register();
        ResponseEntity<String> response3 = user01.querySelf();
        ResponseEntity<String> response4 = user01.login();
        ResponseEntity<String> response5 = user01.querySelf();
        ResponseEntity<String> response6 = user01.logout();
        ResponseEntity<String> response7 = user01.querySelf();

        assertTrue(response1.getStatusCode() == HttpStatus.UNAUTHORIZED);
        assertTrue(response3.getStatusCode() == HttpStatus.UNAUTHORIZED);
        assertTrue(Integer.valueOf(JSONObject.parseObject(response5.getBody()).getString("code")) == 0);
        assertTrue(JSONObject.parseObject(response5.getBody()).getJSONObject("data").getString("nickName").equals(user01.getNickName()));
        assertTrue(response7.getStatusCode() == HttpStatus.UNAUTHORIZED);
    }

    @After
    public void afterTest() throws Exception {
        if (user01.validateAccount()) {
            user01.login();
            user01.deregister();
        }
    }

}
