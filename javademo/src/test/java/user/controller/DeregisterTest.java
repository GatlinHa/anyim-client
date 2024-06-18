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
public class DeregisterTest {

    private static User user01 = Users.ACCOUNT_01_CLIENTID_01;

    @Before
    public void beforeTest() throws Exception {
        if (UserClient.validateAccount(user01)) {
            UserClient.login(user01);
            UserClient.deregister(user01);
        }
    }

    /**
     * 注册 -> 未登录注销 -> 登录 -> 注销 -> 校验账号唯一性 -> 重复注销
     * @throws Exception
     */
    @Test
    public void test01() throws Exception {
        log.info("===>正在执行Test，Class: [{}]，Method: [{}]", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());
        UserClient.register(user01);
        ResponseEntity<String> response1 = UserClient.deregister(user01);
        UserClient.login(user01);
        ResponseEntity<String> response2 = UserClient.deregister(user01);
        Boolean validated = UserClient.validateAccount(user01);
        ResponseEntity<String> response3 = UserClient.deregister(user01);


        assertTrue(response1.getStatusCode() == HttpStatus.UNAUTHORIZED);
        assertTrue(Integer.valueOf(JSONObject.parseObject(response2.getBody()).getString("code")) == 0);
        assertTrue(validated == false);
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
