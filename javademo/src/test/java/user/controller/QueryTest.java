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
public class QueryTest {
    private static User user01 = Users.ACCOUNT_01_CLIENTID_01;

    private static User user02 = Users.ACCOUNT_02_CLIENTID_01;

    @Before
    public void beforeTest() throws Exception {
        if (UserClient.validateAccount(user01)) {
            UserClient.login(user01);
            UserClient.deregister(user01);
        }

        if (UserClient.validateAccount(user02)) {
            UserClient.login(user02);
            UserClient.deregister(user02);
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
        ResponseEntity<String> response1 = UserClient.query(user01, user02.getAccount());
        // 2.注册user01
        ResponseEntity<String> response2 = UserClient.register(user01);
        // 3.查询user02
        ResponseEntity<String> response3 = UserClient.query(user01, user02.getAccount());
        // 4.user01登录
        ResponseEntity<String> response4 = UserClient.login(user01);
        // 5.查询user02
        ResponseEntity<String> response5 = UserClient.query(user01, user02.getAccount());
        // 6.注册user02
        ResponseEntity<String> response6 = UserClient.register(user02);
        // 7.查询user02
        ResponseEntity<String> response7 = UserClient.query(user01, user02.getAccount());
        // 8.注销user02
        UserClient.login(user02);
        UserClient.deregister(user02);
        // 9.查询user02
        ResponseEntity<String> response8 = UserClient.query(user01, user02.getAccount());

        assertTrue(response1.getStatusCode() == HttpStatus.UNAUTHORIZED);
        assertTrue(response3.getStatusCode() == HttpStatus.UNAUTHORIZED);
        assertTrue(JSONObject.parseObject(response5.getBody()).getString("data") == null);
        assertTrue(JSONObject.parseObject(response7.getBody()).getJSONObject("data").getString("nickName").equals(user02.getNickName()));
        assertTrue(JSONObject.parseObject(response8.getBody()).getString("data") == null);
    }

    @After
    public void afterTest() throws Exception {
        if (UserClient.validateAccount(user01)) {
            UserClient.login(user01);
            UserClient.deregister(user01);
        }

        if (UserClient.validateAccount(user02)) {
            UserClient.login(user02);
            UserClient.deregister(user02);
        }
    }

}
