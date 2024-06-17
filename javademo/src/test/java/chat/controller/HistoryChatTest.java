package chat.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hibob.anyim.client.ChatClient;
import com.hibob.anyim.client.NettyClient;
import com.hibob.anyim.client.UserClient;
import com.hibob.anyim.consts.Users;
import com.hibob.anyim.entity.User;
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

    private static User user01 = Users.ACCOUNT_01_CLIENTID_01;
    private static User user02 = Users.ACCOUNT_02_CLIENTID_01;

    @Before
    public void beforeTest() throws Exception {
        if (!UserClient.validateAccount(user01)) {
            UserClient.register(user01);
        }
        UserClient.login(user01);

        if (!UserClient.validateAccount(user02)) {
            UserClient.register(user02);
        }
    }

    @Test
    public void test01() throws Exception {
        log.info("===>正在执行Test，Class: [{}]，Method: [{}]", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

        long startTime = new Date().getTime();
        ChatClient chatClient = new ChatClient(user01, user02);
        NettyClient.setUser(user01);
        NettyClient.start();
        String content = UUID.randomUUID().toString();
        NettyClient.send(MsgType.CHAT, user02.getAccount(), content);
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
                    NettyClient.stop();
                    return;
                }
            }

            log.info("=======>count: {}, lastMsgId: {}, msgList: {}", count, lastMsgId, msgList);
            if (msgList.size() == count) {
                break;
            }
        }

        NettyClient.stop();
        assertTrue(false);
    }

    /**
     * 测试消息发出去多条特定消息，再拉取消息，看是否能全部拉取到
     * @throws Exception
     */
    @Test
    public void test02() throws Exception {
        log.info("===>正在执行Test，Class: [{}]，Method: [{}]", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

        long startTime = new Date().getTime();
        ChatClient chatClient = new ChatClient(user01, user02);
        NettyClient.setUser(user01);
        NettyClient.start();
        String content = UUID.randomUUID().toString();
        int sendCnt = 20;
        int i = 0;
        while (i < sendCnt) {
            NettyClient.send(MsgType.CHAT, user02.getAccount(), content);
            i++;
        }
        long endTime = startTime+ 60000;
        long lastMsgId = -1;

        int cnt = 0;
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
                    cnt++;
                }
            }

            log.info("=======>count: {}, lastMsgId: {}, msgList: {}", count, lastMsgId, msgList);
            if (msgList.size() == count) {
                break;
            }
        }

        NettyClient.stop();
        assertTrue(cnt == sendCnt);
    }
}
