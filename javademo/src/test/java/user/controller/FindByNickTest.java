package user.controller;

import com.alibaba.fastjson.JSONObject;
import com.hibob.anyim.client.UserClient;
import com.hibob.anyim.consts.Users;
import com.hibob.anyim.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.Assert.assertTrue;

@Slf4j

public class FindByNickTest {

    private static User user01 = Users.ACCOUNT_01_CLIENTID_01;
    private static User user02 = Users.ACCOUNT_02_CLIENTID_01;
    private static User user03 = Users.ACCOUNT_03_CLIENTID_01;

    private static String nickNamePre = UUID.randomUUID().toString();

    @Before
    public void beforeTest() throws Exception {
        user01.setNickName(nickNamePre + "_01");
        user02.setNickName(nickNamePre + "_02");
        user03.setNickName(nickNamePre + "_03");

        if (UserClient.validateAccount(user01)) {
            UserClient.login(user01);
            UserClient.deregister(user01);
        }

        if (UserClient.validateAccount(user02)) {
            UserClient.login(user02);
            UserClient.deregister(user02);
        }

        if (UserClient.validateAccount(user03)) {
            UserClient.login(user03);
            UserClient.deregister(user03);
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
        ResponseEntity<String> response1 = UserClient.register(user01);
        // 2.user01登录
        ResponseEntity<String> response2 = UserClient.login(user01);
        // 3.查询（nick_name）
        ResponseEntity<String> response3 = UserClient.findByNick(user01, nickNamePre);
        // 4.注册user02
        ResponseEntity<String> response4 = UserClient.register(user02);
        // 5.查询（nick_name）
        ResponseEntity<String> response5 = UserClient.findByNick(user01, nickNamePre);
        // 6.注册user03
        ResponseEntity<String> response6 = UserClient.register(user03);
        // 7.查询（nick_name）
        ResponseEntity<String> response7 = UserClient.findByNick(user01, nickNamePre);

        assertTrue(JSONObject.parseObject(response3.getBody()).getJSONArray("data").size() == 1);
        assertTrue(JSONObject.parseObject(response5.getBody()).getJSONArray("data").size() == 2);
        assertTrue(JSONObject.parseObject(response7.getBody()).getJSONArray("data").size() == 3);
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

        if (UserClient.validateAccount(user03)) {
            UserClient.login(user03);
            UserClient.deregister(user03);
        }
    }

}
