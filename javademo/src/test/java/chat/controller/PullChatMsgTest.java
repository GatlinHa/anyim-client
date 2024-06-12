package chat.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hibob.anyim.client.client.ChatClient;
import com.hibob.anyim.client.consts.Users;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.Date;

@Slf4j
public class PullChatMsgTest {

    private static ChatClient chatClient = new ChatClient(Users.ACCOUNT_01_CLIENTID_01, Users.ACCOUNT_02_CLIENTID_01);

    @Test
    public void test01() throws Exception {
        log.info("===>正在执行Test，Class: [{}]，Method: [{}]", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());
        long lastMsgId = -1;
        while (true) {
            ResponseEntity<String> response = chatClient.pullMsg(lastMsgId, new Date().getTime() / 1000, 2);
            JSONObject jsonObject = JSONObject.parseObject(response.getBody()).getJSONObject("data");
            long count = jsonObject.getLong("count");
            lastMsgId = jsonObject.getLong("lastMsgId");
            JSONArray msgList = jsonObject.getJSONArray("msgList");
            log.info("=======>count: {}, lastMsgId: {}, msgList: {}", count, lastMsgId, msgList);
            if (msgList.size() == count) {
                break;
            }
        }
    }
}
