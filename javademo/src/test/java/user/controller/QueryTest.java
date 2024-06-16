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
public class QueryTest {
    private static UserClient user01 = new UserClient(
            "account_test01",
            "clientId_test01",
            "avatar_test01",
            "inviteCode_test01",
            "nickName_test01",
            "password_test01",
            "phoneNum_test01"
    );

    private static UserClient user02 = new UserClient(
            "account_test02",
            "clientId_test02",
            "avatar_test02",
            "inviteCode_test02",
            "nickName_test02",
            "password_test02",
            "phoneNum_test02"
    );

    @Before
    public void beforeTest() throws Exception {
        if (user01.validateAccount()) {
            user01.login();
            user01.deregister();
        }

        if (user02.validateAccount()) {
            user02.login();
            user02.deregister();
        }
    }

    /**
     * 1.查询user02 -> 2.注册user01 -> 3.查询user02 -> 4.user01登录 -> 5.查询user02 -> 6.注册user02 -> 7.查询user02（这个才成功） -> 8.注销user02 -> 9.查询user02
     * @throws Exception
     */
    @Test
    public void test01() throws Exception {
        log.info("===>正在执行Test，Class: [{}]，Method: [{}]", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());
        // 1.查询user02
        ResponseEntity<String> response1 = user01.query(user02.getAccount());
        // 2.注册user01
        ResponseEntity<String> response2 = user01.register();
        // 3.查询user02
        ResponseEntity<String> response3 = user01.query(user02.getAccount());
        // 4.user01登录
        ResponseEntity<String> response4 = user01.login();
        // 5.查询user02
        ResponseEntity<String> response5 = user01.query(user02.getAccount());
        // 6.注册user02
        ResponseEntity<String> response6 = user02.register();
        // 7.查询user02
        ResponseEntity<String> response7 = user01.query(user02.getAccount());
        // 8.注销user02
        user02.login();
        user02.deregister();
        // 9.查询user02
        ResponseEntity<String> response8 = user01.query(user02.getAccount());

        assertTrue(response1.getStatusCode() == HttpStatus.UNAUTHORIZED);
        assertTrue(response3.getStatusCode() == HttpStatus.UNAUTHORIZED);
        assertTrue(JSONObject.parseObject(response5.getBody()).getString("data") == null);
        assertTrue(JSONObject.parseObject(response7.getBody()).getJSONObject("data").getString("nickName").equals(user02.getNickName()));
        assertTrue(JSONObject.parseObject(response8.getBody()).getString("data") == null);
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
    }

}
