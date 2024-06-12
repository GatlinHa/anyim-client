package chat.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hibob.anyim.client.client.ChatClient;
import com.hibob.anyim.client.consts.Users;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.text.SimpleDateFormat;

@Slf4j
public class HistoryChatTest {

    private static ChatClient chatClient = new ChatClient(Users.ACCOUNT_01_CLIENTID_01, Users.ACCOUNT_02_CLIENTID_01);

    @Test
    public void test01() throws Exception {
        log.info("===>正在执行Test，Class: [{}]，Method: [{}]", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());
        long lastMsgId = -1;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long startTime = sdf.parse("2024-06-12 00:00:00").getTime()/1000;
        long endTime = sdf.parse("2024-06-13 00:00:00").getTime()/1000;
        while (true) {
            ResponseEntity<String> response = chatClient.history(startTime, endTime, lastMsgId, 2);
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
