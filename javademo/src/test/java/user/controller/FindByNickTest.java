package user.controller;

import com.alibaba.fastjson.JSONObject;
import com.hibob.anyim.client.UserClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertTrue;

@Slf4j

public class FindByNickTest {

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
    private static UserClient user03 = new UserClient(
            "account_test03",
            "clientId_test03",
            "avatar_test03",
            "inviteCode_test03",
            "nickName_test03",
            "password_test03",
            "phoneNum_test03"
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

        if (user03.validateAccount()) {
            user03.login();
            user03.deregister();
        }
    }

    /**
     * 1.注册user01 -> 2.user01登录 -> 3.查询（nick_name）-> 4.注册user02 -> 5.查询（nick_name） -> 6.注册user03 -> 7.查询（nick_name） -> 8.查询（other_name）
     * @throws Exception
     */
    @Test
    public void test01() throws Exception {
        log.info("===>正在执行Test，Class: [{}]，Method: [{}]", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());
        // 1.注册user01
        ResponseEntity<String> response1 = user01.register();
        // 2.user01登录
        ResponseEntity<String> response2 = user01.login();
        // 3.查询（nick_name）
        ResponseEntity<String> response3 = user01.findByNick("test0");
        // 4.注册user02
        ResponseEntity<String> response4 = user02.register();
        // 5.查询（nick_name）
        ResponseEntity<String> response5 = user01.findByNick("test0");
        // 6.注册user03
        ResponseEntity<String> response6 = user03.register();
        // 7.查询（nick_name）
        ResponseEntity<String> response7 = user01.findByNick("test0");

        assertTrue(JSONObject.parseObject(response3.getBody()).getJSONArray("data").size() == 1);
        assertTrue(JSONObject.parseObject(response5.getBody()).getJSONArray("data").size() == 2);
        assertTrue(JSONObject.parseObject(response7.getBody()).getJSONArray("data").size() == 3);
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

        if (user03.validateAccount()) {
            user03.login();
            user03.deregister();
        }
    }

}
