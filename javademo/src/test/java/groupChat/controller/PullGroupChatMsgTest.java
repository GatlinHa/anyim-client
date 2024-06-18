package groupChat.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hibob.anyim.client.*;
import com.hibob.anyim.consts.Groups;
import com.hibob.anyim.consts.Users;
import com.hibob.anyim.entity.Group;
import com.hibob.anyim.entity.User;
import com.hibob.anyim.netty.protobuf.MsgType;
import lombok.extern.slf4j.Slf4j;
import org.junit.*;
import org.springframework.http.ResponseEntity;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertTrue;

@Slf4j
public class PullGroupChatMsgTest {

    private static User user01 = Users.ACCOUNT_01_CLIENTID_01;
    private static User user02 = Users.ACCOUNT_02_CLIENTID_01;
    private static User user03 = Users.ACCOUNT_03_CLIENTID_01;
    private static User user04 = Users.ACCOUNT_04_CLIENTID_01; // 这个用户不在群组中

    private static Group group = Groups.GROUP_1;

    @BeforeClass //只用执行一次
    public static void beforeTest() throws Exception {
        if (!UserClient.validateAccount(user01)) {
            UserClient.register(user01);
        }
        if (!UserClient.validateAccount(user02)) {
            UserClient.register(user02);
        }
        if (!UserClient.validateAccount(user03)) {
            UserClient.register(user03);
        }
        if (!UserClient.validateAccount(user04)) {
            UserClient.register(user04);
        }

        UserClient.login(user01);
        UserClient.login(user02);
        UserClient.login(user03);
        UserClient.login(user04);

        List<Map<String, Object>> members = new ArrayList<>();
        members.add(new HashMap<String, Object>(){{
            put("memberAccount", user01.getAccount());
            put("memberRole", 3);
        }});
        members.add(new HashMap<String, Object>(){{
            put("memberAccount", user02.getAccount());
            put("memberRole", 0);
        }});
        members.add(new HashMap<String, Object>(){{
            put("memberAccount", user03.getAccount());
            put("memberRole", 0);
        }});
        group.setUserLocal(user01);
        group.setMembers(members);
        GroupMngClient.createGroup(group);
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
        NettyClient.send(MsgType.GROUP_CHAT, String.valueOf(group.getGroupId()), content);
        long lastMsgId = -1;

        while (true) {
            ResponseEntity<String> response = GroupChatClient.pullMsg(user01, group, lastMsgId, lastPullTime, 10);
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
        NettyClient.send(MsgType.GROUP_CHAT, String.valueOf(group.getGroupId()), content);
        long lastMsgId = -1;

        while (true) {
            ResponseEntity<String> response = GroupChatClient.pullMsg(user01, group, lastMsgId, lastPullTime, 10);
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
        int sendCnt = 20;
        int i = 0;
        while (i < sendCnt) {
            NettyClient.send(MsgType.GROUP_CHAT, String.valueOf(group.getGroupId()), content);
            i++;
        }

        Thread.sleep(300); // 等待消息写入mongoDB

        long lastMsgId = -1;
        int cnt = 0;
        while (true) {
            ResponseEntity<String> response = GroupChatClient.pullMsg(user01, group, lastMsgId, lastPullTime, 10);
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

    /**
     * 用不在群组的user04查看消息，应该查询失败
     * @throws Exception
     */
    @Test
    public void test04() throws Exception {
        log.info("===>正在执行Test，Class: [{}]，Method: [{}]", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());
        long lastPullTime = new Date().getTime();
        long lastMsgId = -1;
        ResponseEntity<String> response = GroupChatClient.pullMsg(user04, group, lastMsgId, lastPullTime, 10);
        assertTrue(JSONObject.parseObject(response.getBody()).getInteger("code") == 502);
    }

    @AfterClass
    public static void afterTest() throws Exception {
        GroupMngClient.delGroup(user01, group.getGroupId());
        UserClient.register(user01);
        UserClient.register(user02);
        UserClient.register(user03);
        UserClient.register(user04);
    }
}
