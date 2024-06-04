package user.controller;

import com.alibaba.fastjson.JSONObject;
import com.hibob.anyim.client.client.UserClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertTrue;

@Slf4j
public class ModifyPwdTest {

    private static UserClient user01 = new UserClient(
            "account_test01",
            "clientId_test01",
            "headImage_test01",
            "inviteCode_test01",
            "nickName_test01",
            "password_test01",
            "phoneNum_test01"
    );
    private static UserClient user01_errorPwd = new UserClient(user01);;
    private static UserClient user01_newPwd = new UserClient(user01);;

    @Before
    public void beforeTest() throws Exception {
        user01_errorPwd.setPassword("error_password");
        user01_newPwd.setPassword("new_password");
        if (user01.validateAccount()) {
            user01.login();
            user01.deregister();
        }
    }

    /**
     * 注册 -> 登录 -> 修改密码失败 -> 修改密码成功 -> 无法使用查询 -> 使用旧密码登录 -> 使用新密码登录 -> 可以使用查询
     * @throws Exception
     */
    @Test
    public void test01() throws Exception {
        log.info("===>正在执行Test，Class: [{}]，Method: [{}]", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

        ResponseEntity<String> response1 = user01.register();
        ResponseEntity<String> response2 = user01.login();
        ResponseEntity<String> response3 = user01.modifyPwd(user01_errorPwd.getPassword(), user01_newPwd.getPassword());
        ResponseEntity<String> response4 = user01.modifyPwd(user01.getPassword(), user01_newPwd.getPassword());
        ResponseEntity<String> response5 = user01.querySelf();
        ResponseEntity<String> response6 = user01.login();
        ResponseEntity<String> response7 = user01_newPwd.login();
        ResponseEntity<String> response8 = user01_newPwd.querySelf();

        assertTrue(response3.getStatusCode() == HttpStatus.UNAUTHORIZED);
        assertTrue(Integer.valueOf(JSONObject.parseObject(response4.getBody()).getString("code")) == 0);
        assertTrue(response5.getStatusCode() == HttpStatus.UNAUTHORIZED);
        assertTrue(response6.getStatusCode() == HttpStatus.UNAUTHORIZED);
        assertTrue(Integer.valueOf(JSONObject.parseObject(response7.getBody()).getString("code")) == 0);
        assertTrue(JSONObject.parseObject(response8.getBody()).getJSONObject("data").getString("nickName").equals(user01.getNickName()));
    }

    @After
    public void afterTest() throws Exception {
        if (user01.validateAccount()) {
            user01_newPwd.login();
            user01_newPwd.deregister();
        }
    }

}
