package com.hibob.anyim;

import com.alibaba.fastjson.JSONObject;
import com.hibob.anyim.client.GroupMngClient;
import com.hibob.anyim.client.NettyClient;
import com.hibob.anyim.client.UserClient;
import com.hibob.anyim.consts.Groups;
import com.hibob.anyim.consts.Users;
import com.hibob.anyim.entity.Group;
import com.hibob.anyim.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.*;

@Slf4j
public class GroupChatClientMain {
    private static final Map<String, User> userMap = new HashMap<>();
    private static final Map<String, Group> groupMap = new HashMap<>();

    private static String token ="";
    private static String signKey ="";

    static {
        userMap.put("user1-client1", Users.ACCOUNT_01_CLIENTID_01);
        userMap.put("user1-client2", Users.ACCOUNT_01_CLIENTID_02);
        userMap.put("user2-client1", Users.ACCOUNT_02_CLIENTID_01);
        userMap.put("user2-client2", Users.ACCOUNT_02_CLIENTID_02);
        userMap.put("user3-client1", Users.ACCOUNT_03_CLIENTID_01);
        userMap.put("user3-client2", Users.ACCOUNT_03_CLIENTID_02);
        userMap.put("user4-client1", Users.ACCOUNT_04_CLIENTID_01);
        userMap.put("user4-client2", Users.ACCOUNT_04_CLIENTID_02);

        groupMap.put("group01", Groups.GROUP_1);
        groupMap.put("group02", Groups.GROUP_2);
        groupMap.put("group03", Groups.GROUP_3);
        groupMap.put("group04", Groups.GROUP_4);
    }

    public static void login(User user) throws Exception {
        ResponseEntity<String> response = UserClient.login(user);
        JSONObject login = JSONObject.parseObject(response.getBody());
        token = login.getJSONObject("data").getJSONObject("accessToken").getString("token");
        signKey = login.getJSONObject("data").getJSONObject("accessToken").getString("secret");
    }

    private static void register() throws Exception {
        if (!UserClient.validateAccount(userMap.get("user1-client1"))) {
            UserClient.register(userMap.get("user1-client1"));
        }
        if (!UserClient.validateAccount(userMap.get("user2-client1"))) {
            UserClient.register(userMap.get("user2-client1"));
        }
        if (!UserClient.validateAccount(userMap.get("user3-client1"))) {
            UserClient.register(userMap.get("user3-client1"));
        }
        if (!UserClient.validateAccount(userMap.get("user4-client1"))) {
            UserClient.register(userMap.get("user4-client1"));
        }
    }

    private static void clearUser(User user) throws Exception {
        UserClient.login(user);
        UserClient.deregister(user);
    }

    private static void addMembers(Group group, User user, int role) {
        if (group.getMembers() == null) {
            List<Map<String, Object>> members = new ArrayList<>();
            group.setMembers(members);
        }

        group.getMembers().add(new HashMap<String, Object>(){{
            put("memberAccount", user.getAccount());
            put("memberRole", role);
        }});

    }

    private static void createGroup(Group group, Properties properties, String filePath) throws Exception {
        String groupId;
        group.setUserLocal(userMap.get("user1-client1"));
        UserClient.login(userMap.get("user1-client1"));

        addMembers(group, userMap.get("user1-client1"), 3);
        addMembers(group, userMap.get("user2-client1"), 0);
        addMembers(group, userMap.get("user3-client1"), 0);
        ResponseEntity<String> response = GroupMngClient.createGroup(group);
        groupId = JSONObject.parseObject(response.getBody()).getJSONObject("data").getJSONObject("groupInfo").getString("groupId");
        group.setGroupId(Long.valueOf(groupId));
        properties.setProperty("groupId", groupId);

        File file = new File(GroupChatClientMain.class.getClassLoader().getResource(".").getPath() + File.separator + filePath);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        properties.store(fileOutputStream, "");
    }

    public static void main(String[] args) throws Exception {
        User user = null;
        Group group = null;
        String port = "";
        for (String arg : args) {
            if (arg.startsWith("-user")) {
                user = userMap.get(arg.split("=")[1]);
            }
            if (arg.startsWith("-group")) {
                group = groupMap.get(arg.split("=")[1]);
            }
            if (arg.startsWith("-port")) {
                port = arg.split("=")[1];
            }
        }
        if (user == null || group == null || port.equals("")) {
            log.error("参数错误");
        }

        register();

        String filePath = "group.properties";
        Properties properties = new Properties();
        InputStream resourceAsStream = GroupChatClientMain.class.getClassLoader().getResourceAsStream(filePath);
        properties.load(resourceAsStream);
        String groupId = properties.getProperty("groupId");
        if (StringUtils.hasLength(groupId)) {
            group.setGroupId(Long.valueOf(groupId));
            group.setUserLocal(userMap.get("user1-client1"));
            UserClient.login(userMap.get("user1-client1"));
            ResponseEntity<String> response = GroupMngClient.queryGroupInfo(group);
            if (JSONObject.parseObject(response.getBody()).getJSONObject("data") == null) {
                createGroup(group, properties, filePath);
            }
        }
        else {
            createGroup(group, properties, filePath);
        }


        UserClient.login(user);
        NettyClient nettyClient = new NettyClient(user, port);
        nettyClient.start();
        log.info("===========>本次测试的群组ID为：{}", group.getGroupId());
        nettyClient.scannerInGroupChat();
    }



}

