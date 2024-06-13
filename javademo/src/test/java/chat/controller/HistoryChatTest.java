package chat.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hibob.anyim.client.client.ChatClient;
import com.hibob.anyim.client.client.NettyClient;
import com.hibob.anyim.client.client.UserClient;
import com.hibob.anyim.client.consts.Users;
import com.hibob.anyim.netty.protobuf.MsgType;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

@Slf4j
public class HistoryChatTest {

    private static UserClient user01 = Users.ACCOUNT_01_CLIENTID_01;
    private static UserClient user02 = Users.ACCOUNT_02_CLIENTID_01;

    @Before
    public void beforeTest() throws Exception {
        if (!user01.validateAccount()) {
            user01.register();
        }
        user01.login();

        if (!user02.validateAccount()) {
            user02.register();
        }
    }

    @Test
    public void test01() throws Exception {
        log.info("===>正在执行Test，Class: [{}]，Method: [{}]", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

        long startTime = new Date().getTime();
        ChatClient chatClient = new ChatClient(user01, user02);
        NettyClient nettyClient = new NettyClient(user01);
        nettyClient.start();
        String content = UUID.randomUUID().toString();
        nettyClient.send(MsgType.CHAT, user02.getAccount(), content);
        long endTime = startTime+ 60000;
        long lastMsgId = -1;

        while (true) {
            ResponseEntity<String> response = chatClient.history(startTime, endTime, lastMsgId, 10);
            JSONObject jsonObject = JSONObject.parseObject(response.getBody()).getJSONObject("data");
            long count = jsonObject.getLong("count");
            lastMsgId = jsonObject.getLong("lastMsgId");
            JSONArray msgList = jsonObject.getJSONArray("msgList");
            for (Object msg : msgList) {
                JSONObject msgJson = (JSONObject) msg;
                String s = msgJson.getString("content");
                if (content.equals(s)) {
                    assertTrue(true);
                    nettyClient.stop();
                    return;
                }
            }

            log.info("=======>count: {}, lastMsgId: {}, msgList: {}", count, lastMsgId, msgList);
            if (msgList.size() == count) {
                break;
            }
        }

        nettyClient.stop();
        assertTrue(false);
    }
}
