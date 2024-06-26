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
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

@Slf4j
public class PullChatMsgTest {

    private static User user01 = Users.ACCOUNT_01_CLIENTID_01;
    private static User user02 = Users.ACCOUNT_02_CLIENTID_01;

    @BeforeClass //只用执行一次
    public static void beforeTest() throws Exception {
        if (!UserClient.validateAccount(user01)) {
            UserClient.register(user01);
        }
        UserClient.login(user01);

        if (!UserClient.validateAccount(user02)) {
            UserClient.register(user02);
        }
        UserClient.login(user02);
    }

    /**
     * 测试消息发出去之后，再拉取消息，看是否能拉取到（从Redis拉取）
     * @throws Exception
     */
    @Test
    public void test01() throws Exception {
        log.info("===>正在执行Test，Class: [{}]，Method: [{}]", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

        long lastPullTime = new Date().getTime();
        NettyClient.setUser(user01);
        NettyClient.start();
        String content = UUID.randomUUID().toString();
        NettyClient.send(MsgType.CHAT, user02.getAccount(), content);
        long lastMsgId = -1;

        while (true) {
            ResponseEntity<String> response = ChatClient.pullMsg(user01, user02, lastMsgId, lastPullTime, 10);
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
     * 测试消息发出去之后，再拉取消息，看是否能拉取到（从mongoDB拉取）
     * @throws Exception
     */
    @Test
    public void test02() throws Exception {
        log.info("===>正在执行Test，Class: [{}]，Method: [{}]", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lastPullTime = sdf.parse("2024-06-01 00:00:00").getTime();
        NettyClient.setUser(user01);
        NettyClient.start();
        String content = UUID.randomUUID().toString();
        NettyClient.send(MsgType.CHAT, user02.getAccount(), content);
        long lastMsgId = -1;

        while (true) {
            ResponseEntity<String> response = ChatClient.pullMsg(user01, user02, lastMsgId, lastPullTime, 10);
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
    public void test03() throws Exception {
        log.info("===>正在执行Test，Class: [{}]，Method: [{}]", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

        long lastPullTime = new Date().getTime();
        NettyClient.setUser(user01);
        NettyClient.start();
        String content = UUID.randomUUID().toString();
        int sendCnt = 10;
        int i = 0;
        while (i < sendCnt) {
            NettyClient.send(MsgType.CHAT, user02.getAccount(), content);
            i++;
        }
        long lastMsgId = -1;

        int cnt = 0;
        while (true) {
            ResponseEntity<String> response = ChatClient.pullMsg(user01, user02, lastMsgId, lastPullTime, 10);
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

    @AfterClass //只用执行一次
    public static void afterTest() throws Exception {
        UserClient.deregister(user01);
        UserClient.deregister(user02);
    }

}
