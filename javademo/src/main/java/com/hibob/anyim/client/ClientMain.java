package com.hibob.anyim.client;

import com.alibaba.fastjson.JSONObject;
import com.hibob.anyim.client.client.NettyClient;
import com.hibob.anyim.client.client.UserClient;
import com.hibob.anyim.client.consts.Users;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ClientMain {
    private static final Map<String, UserClient> startupCmdMap = new HashMap<>();

    private static String token ="";
    private static String signKey ="";

    static {
        startupCmdMap.put("11", Users.ACCOUNT_01_CLIENTID_01);
        startupCmdMap.put("12", Users.ACCOUNT_01_CLIENTID_02);
        startupCmdMap.put("21", Users.ACCOUNT_02_CLIENTID_01);
        startupCmdMap.put("22", Users.ACCOUNT_02_CLIENTID_02);
    }

    public static void register(UserClient userClient) throws Exception {
        userClient.register();
    }

    public static void login(UserClient userClient) throws Exception {
        ResponseEntity<String> response = userClient.login();
        JSONObject login = JSONObject.parseObject(response.getBody());
        token = login.getJSONObject("data").getJSONObject("accessToken").getString("token");
        signKey = login.getJSONObject("data").getJSONObject("accessToken").getString("secret");
    }

    private static void clearUser(UserClient userClient) throws Exception {
        userClient.login();
        userClient.deregister();
    }

    /**
     * args第1个参数表示用哪个user数据，第2个参数表示连接netty的哪个端口
     * @param args
     * @throws URISyntaxException
     */
    public static void main(String[] args) throws Exception {
        UserClient userClient = startupCmdMap.get(args[0]);
        if (!userClient.validateAccount()) {
            register(userClient);
        }
        login(userClient);
        NettyClient.start(userClient, token, args[1]);

    }

}

