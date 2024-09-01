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
     * 注册 -> 登录 -> 修改密码失败（密码错误） -> 修改密码失败（新密码与旧密码一致） -> 修改密码成功 -> 登出 -> 使用旧密码登录 -> 使用新密码登录 -> 可以使用查询
     * @throws Exception
     */
    @Test
    public void test01() throws Exception {
        log.info("===>正在执行Test，Class: [{}]，Method: [{}]", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

        ResponseEntity<String> response1 = UserClient.register(user01);
        ResponseEntity<String> response2 = UserClient.login(user01);
        ResponseEntity<String> response3_1 = UserClient.modifyPwd(user01, user01_errorPwd.getPassword(), user01_newPwd.getPassword());
        ResponseEntity<String> response3_2 = UserClient.modifyPwd(user01, user01.getPassword(), user01.getPassword());
        ResponseEntity<String> response4 = UserClient.modifyPwd(user01, user01.getPassword(), user01_newPwd.getPassword());
        ResponseEntity<String> response5 = UserClient.logout(user01);
        ResponseEntity<String> response6 = UserClient.login(user01);
        ResponseEntity<String> response7 = UserClient.login(user01_newPwd);
        ResponseEntity<String> response8 = UserClient.querySelf(user01_newPwd);

        assertTrue(JSONObject.parseObject(response3_1.getBody()).getInteger("code") == ServiceErrorCode.ERROR_OLD_PASSWORD_ERROR.code());
        assertTrue(JSONObject.parseObject(response3_2.getBody()).getInteger("code") == ServiceErrorCode.ERROR_NEW_PASSWORD_EQUAL_OLD.code());
        assertTrue(Integer.valueOf(JSONObject.parseObject(response4.getBody()).getString("code")) == 0);
        assertTrue(JSONObject.parseObject(response6.getBody()).getInteger("code") == ServiceErrorCode.ERROR_LOGIN.code());
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
