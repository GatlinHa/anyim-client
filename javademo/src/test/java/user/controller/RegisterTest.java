package user.controller;

import com.alibaba.fastjson.JSONObject;
import com.hibob.anyim.client.UserClient;
import com.hibob.anyim.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import static com.hibob.anyim.enums.ServiceErrorCode.ERROR_ACCOUNT_EXIST;
import static org.junit.Assert.assertTrue;

@Slf4j

public class RegisterTest {

    private static final User user01 = new User(
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
        if (UserClient.validateAccount(user01)) {
            UserClient.login(user01);
            UserClient.deregister(user01);
        }
    }


    /**
     * 注册 -> 重复注册
     * @throws Exception
     */
    @Test
    public void test01() throws Exception {
        log.info("===>正在执行Test，Class: [{}]，Method: [{}]", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());
        ResponseEntity<String> register1 = UserClient.register(user01);
        ResponseEntity<String> register2 = UserClient.register(user01);
        int code1 = Integer.valueOf(JSONObject.parseObject(register1.getBody()).getString("code"));
        int code2 = Integer.valueOf(JSONObject.parseObject(register2.getBody()).getString("code"));

        assertTrue(code1 == 0);
        assertTrue(code2 == ERROR_ACCOUNT_EXIST.code());
    }

    @After
    public void afterTest() throws Exception {
        if (UserClient.validateAccount(user01)) {
            UserClient.login(user01);
            UserClient.deregister(user01);
        }
    }

}
