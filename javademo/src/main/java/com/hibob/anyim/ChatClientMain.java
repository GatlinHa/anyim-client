package com.hibob.anyim;

import com.alibaba.fastjson.JSONObject;
import com.hibob.anyim.client.NettyClient;
import com.hibob.anyim.client.UserClient;
import com.hibob.anyim.consts.Users;
import com.hibob.anyim.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ChatClientMain {
    private static final Map<String, User> startupCmdMap = new HashMap<>();

    private static String token ="";
    private static String signKey ="";

    static {
        startupCmdMap.put("11", Users.ACCOUNT_01_CLIENTID_01);
        startupCmdMap.put("12", Users.ACCOUNT_01_CLIENTID_02);
        startupCmdMap.put("21", Users.ACCOUNT_02_CLIENTID_01);
        startupCmdMap.put("22", Users.ACCOUNT_02_CLIENTID_02);
    }

    public static void register(User user) throws Exception {
        UserClient.register(user);
    }

    public static void login(User user) throws Exception {
        ResponseEntity<String> response = UserClient.login(user);
        JSONObject login = JSONObject.parseObject(response.getBody());
        token = login.getJSONObject("data").getJSONObject("accessToken").getString("token");
        signKey = login.getJSONObject("data").getJSONObject("accessToken").getString("secret");
    }

    private static void clearUser(User user) throws Exception {
        UserClient.login(user);
        UserClient.deregister(user);
    }

    /**
     * args第1个参数表示用哪个user数据，第2个参数表示连接netty的哪个端口
     * @param args
     * @throws URISyntaxException
     */
    public static void main(String[] args) throws Exception {
        User user = startupCmdMap.get(args[0]);
        if (!UserClient.validateAccount(user)) {
            UserClient.register(user);
        }
        UserClient.login(user);
        NettyClient.setUser(user);
        NettyClient.setNettyPort(args[1]);
        NettyClient.start();
        NettyClient.scannerInChat();
    }

}

