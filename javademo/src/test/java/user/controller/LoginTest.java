package user.controller;

import com.alibaba.fastjson.JSONObject;
import com.hibob.anyim.client.UserClient;
import com.hibob.anyim.consts.Users;
import com.hibob.anyim.entity.User;
import com.hibob.anyim.enums.ServiceErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertTrue;

@Slf4j
public class LoginTest {

    private static User user01 = Users.ACCOUNT_01_CLIENTID_01;
    private static User user01_errorPwd = new User(user01);

    @Before
    public void beforeTest() throws Exception {
        user01_errorPwd.setPassword("error_password");
        if (UserClient.validateAccount(user01)) {
            UserClient.login(user01);
            UserClient.deregister(user01);
        }
    }

    /**
     * 未注册登录 -> 注册 -> 登录密码错误 -> 登录成功 -> 重复登录失败
     * @throws Exception
     */
    @Test
    public void test01() throws Exception {
        log.info("===>正在执行Test，Class: [{}]，Method: [{}]", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

        ResponseEntity<String> response1 = UserClient.login(user01);
        ResponseEntity<String> response2 = UserClient.register(user01);
        ResponseEntity<String> response3 = UserClient.login(user01_errorPwd);
        ResponseEntity<String> response4 = UserClient.login(user01);
        ResponseEntity<String> response5 = UserClient.login(user01);

        assertTrue(JSONObject.parseObject(response1.getBody()).getInteger("code") == ServiceErrorCode.ERROR_LOGIN.code());
        assertTrue(JSONObject.parseObject(response3.getBody()).getInteger("code") == ServiceErrorCode.ERROR_LOGIN.code());
        assertTrue(Integer.valueOf(JSONObject.parseObject(response4.getBody()).getString("code")) == 0);
        assertTrue(Integer.valueOf(JSONObject.parseObject(response5.getBody()).getString("code")) == 0);
    }

    @After
    public void afterTest() throws Exception {
        if (UserClient.validateAccount(user01)) {
            UserClient.login(user01);
            UserClient.deregister(user01);
        }
    }

}
