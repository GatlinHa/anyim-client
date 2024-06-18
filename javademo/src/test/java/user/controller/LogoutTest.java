package user.controller;

import com.alibaba.fastjson.JSONObject;
import com.hibob.anyim.client.UserClient;
import com.hibob.anyim.consts.Users;
import com.hibob.anyim.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertTrue;

@Slf4j
public class LogoutTest {

    private static final User user01 = Users.ACCOUNT_01_CLIENTID_01;

    @Before
    public void beforeTest() throws Exception {
        if (UserClient.validateAccount(user01)) {
            UserClient.login(user01);
            UserClient.deregister(user01);
        }
    }

    /**
     * 注册 -> 登录 -> 登出 -> 重复登出 -> 查询
     * @throws Exception
     */
    @Test
    public void test01() throws Exception {
        log.info("===>正在执行Test，Class: [{}]，Method: [{}]", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());
        UserClient.register(user01);
        UserClient.login(user01);
        ResponseEntity<String> response1 = UserClient.logout(user01);
        ResponseEntity<String> response2 = UserClient.logout(user01);
        ResponseEntity<String> response3 = UserClient.querySelf(user01);

        assertTrue(Integer.valueOf(JSONObject.parseObject(response1.getBody()).getString("code")) == 0);
        assertTrue(response2.getStatusCode() == HttpStatus.UNAUTHORIZED);
        assertTrue(response3.getStatusCode() == HttpStatus.UNAUTHORIZED);
    }

    @After
    public void afterTest() throws Exception {
        if (UserClient.validateAccount(user01)) {
            UserClient.login(user01);
            UserClient.deregister(user01);
        }
    }

}
