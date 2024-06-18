package user.controller;

import com.hibob.anyim.client.UserClient;
import com.hibob.anyim.consts.Users;
import com.hibob.anyim.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

@Slf4j
public class ValidateAccountTest {

    private static User user01 = Users.ACCOUNT_01_CLIENTID_01;

    @Before
    public void beforeTest() throws Exception {
        if (UserClient.validateAccount(user01)) {
            UserClient.login(user01);
            UserClient.deregister(user01);
        }
    }

    /**
     * 校验不存在的账号 -> 注册 -> 校验已存在的账号
     * @throws Exception
     */
    @Test
    public void test01() throws Exception {
        log.info("===>正在执行Test，Class: [{}]，Method: [{}]", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());
        Boolean result1 = UserClient.validateAccount(user01);
        UserClient.register(user01);
        Boolean result2 = UserClient.validateAccount(user01);

        assertTrue(result1 == false);
        assertTrue(result2 == true);
    }

    @After
    public void afterTest() throws Exception {
        if (UserClient.validateAccount(user01)) {
            UserClient.login(user01);
            UserClient.deregister(user01);
        }
    }
}
