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
public class ModifyPwdTest {

    private static User user01 = Users.ACCOUNT_01_CLIENTID_01;
    private static User user01_errorPwd = new User(user01);;
    private static User user01_newPwd = new User(user01);;

    @Before
    public void beforeTest() throws Exception {
        user01_errorPwd.setPassword("error_password");
        user01_newPwd.setPassword("new_password");
        if (UserClient.validateAccount(user01)) {
            UserClient.login(user01);
            UserClient.deregister(user01);
        }
    }

    /**
     * 注册 -> 登录 -> 修改密码失败 -> 修改密码成功 -> 无法使用查询 -> 使用旧密码登录 -> 使用新密码登录 -> 可以使用查询
     * @throws Exception
     */
    @Test
    public void test01() throws Exception {
        log.info("===>正在执行Test，Class: [{}]，Method: [{}]", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

        ResponseEntity<String> response1 = UserClient.register(user01);
        ResponseEntity<String> response2 = UserClient.login(user01);
        ResponseEntity<String> response3 = UserClient.modifyPwd(user01, user01_errorPwd.getPassword(), user01_newPwd.getPassword());
        ResponseEntity<String> response4 = UserClient.modifyPwd(user01, user01.getPassword(), user01_newPwd.getPassword());
        ResponseEntity<String> response5 = UserClient.querySelf(user01);
        ResponseEntity<String> response6 = UserClient.login(user01);
        ResponseEntity<String> response7 = UserClient.login(user01_newPwd);
        ResponseEntity<String> response8 = UserClient.querySelf(user01_newPwd);

        assertTrue(response3.getStatusCode() == HttpStatus.UNAUTHORIZED);
        assertTrue(Integer.valueOf(JSONObject.parseObject(response4.getBody()).getString("code")) == 0);
        assertTrue(response5.getStatusCode() == HttpStatus.UNAUTHORIZED);
        assertTrue(response6.getStatusCode() == HttpStatus.UNAUTHORIZED);
        assertTrue(Integer.valueOf(JSONObject.parseObject(response7.getBody()).getString("code")) == 0);
        assertTrue(JSONObject.parseObject(response8.getBody()).getJSONObject("data").getString("nickName").equals(user01.getNickName()));
    }

    @After
    public void afterTest() throws Exception {
        if (UserClient.validateAccount(user01)) {
            UserClient.login(user01_newPwd);
            UserClient.deregister(user01_newPwd);
        }
    }

}
