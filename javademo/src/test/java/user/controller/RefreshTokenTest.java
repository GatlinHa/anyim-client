package user.controller;

import com.alibaba.fastjson.JSONObject;
import com.hibob.anyim.client.client.UserClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertTrue;

@Slf4j
public class RefreshTokenTest {

    private static UserClient user01 = new UserClient(
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
        if (user01.validateAccount()) {
            user01.login();
            user01.deregister();
        }
    }

    /**
     * 注册 -> 登录 -> 刷新token失败（RefreshToken错误） -> 刷新token -> 用老token查询 -> 用新token查询 -> 再次刷新token -> 用新新token查询
     * @throws Exception
     */
    @Test
    public void test01() throws Exception, Exception {
        log.info("===>正在执行Test，Class: [{}]，Method: [{}]", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

        // 注册
        ResponseEntity<String> response1 = user01.register();
        // 登录
        ResponseEntity<String> response2 = user01.login();
        // 刷新token失败（用错误的RefreshToken）
        UserClient user01_errorRefresh = new UserClient(user01);
        user01_errorRefresh.setRefreshToken("refreshTokenError");
        ResponseEntity<String> response3 = user01_errorRefresh.refreshToken();
        // 刷新token
        UserClient user01_correctRefresh1 = new UserClient(user01);
        ResponseEntity<String> response4 = user01_correctRefresh1.refreshToken();
        // 用老token查询
        ResponseEntity<String> response5 = user01.querySelf();
        // 用新token查询
        ResponseEntity<String> response6 = user01_correctRefresh1.querySelf();
        // 再次刷新token
        UserClient user01_correctRefresh2 = new UserClient(user01_correctRefresh1);
        ResponseEntity<String> response7 = user01_correctRefresh2.refreshToken();
        // 用新新token查询
        ResponseEntity<String> response8 = user01_correctRefresh2.querySelf();

        assertTrue(response3.getStatusCode() == HttpStatus.UNAUTHORIZED);
        assertTrue(Integer.valueOf(JSONObject.parseObject(response4.getBody()).getString("code")) == 0);
        assertTrue(response5.getStatusCode() == HttpStatus.UNAUTHORIZED);
        assertTrue(Integer.valueOf(JSONObject.parseObject(response6.getBody()).getString("code")) == 0);
        assertTrue(Integer.valueOf(JSONObject.parseObject(response7.getBody()).getString("code")) == 0);
        assertTrue(Integer.valueOf(JSONObject.parseObject(response8.getBody()).getString("code")) == 0);

    }

    @After
    public void afterTest() throws Exception {
        if (user01.validateAccount()) {
            user01.login();
            user01.deregister();
        }
    }

}
