package chat.controller;

import com.alibaba.fastjson.JSONObject;
import com.hibob.anyim.client.client.ChatClient;
import com.hibob.anyim.client.client.UserClient;
import com.hibob.anyim.client.consts.Users;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;

import static org.junit.Assert.assertTrue;

@Slf4j
public class PullChatMsgTest {

    private static ChatClient chatClient = new ChatClient(Users.ACCOUNT_01_CLIENTID_01, Users.ACCOUNT_02_CLIENTID_01);

    @Test
    public void test01() throws Exception {
        log.info("===>正在执行Test，Class: [{}]，Method: [{}]", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());
//        ResponseEntity<String> response1 = chatClient.pullMsg(-1, -1);
        ResponseEntity<String> response2 = chatClient.pullMsg(-1, new Date().getTime() / 1000);

//        log.info(response1.getBody().toString());
        log.info(response2.getBody().toString());
    }


}
