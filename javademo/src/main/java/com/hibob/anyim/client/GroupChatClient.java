package com.hibob.anyim.client;

import com.hibob.anyim.entity.Group;
import com.hibob.anyim.entity.User;
import com.hibob.anyim.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class GroupChatClient {

    private static final RestTemplate restTemplate = new RestTemplate();

    public static ResponseEntity<String> pullMsg(User userLocal, Group group, long lastMsgId, long lastPullTime, int pageSize) throws Exception {
        UserClient.login(userLocal);
        String url = "http://localhost:80/groupChat/pullMsg";
        HttpHeaders headers = getHttpHeaders(userLocal.getAccessToken(), userLocal.getAccessSecret());
        Map<String, Object> map = new HashMap<>(baseRequestMap());
        map.put("groupId", group.getGroupId());
        map.put("groupId", group.getGroupId());
        map.put("lastMsgId", lastMsgId);
        map.put("lastPullTime", lastPullTime);
        map.put("pageSize", pageSize);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(map, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                new URI(url),
                HttpMethod.POST,
                request,
                String.class);

        return response;
    }

    public static ResponseEntity<String> history(User userLocal, Group group, long startTime, long endTime, long lastMsgId, int pageSize) throws Exception {
        UserClient.login(userLocal);
        String url = "http://localhost:80/groupChat/history";
        HttpHeaders headers = getHttpHeaders(userLocal.getAccessToken(), userLocal.getAccessSecret());
        Map<String, Object> map = new HashMap<>(baseRequestMap());
        map.put("groupId", group.getGroupId());
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        map.put("lastMsgId", lastMsgId);
        map.put("pageSize", pageSize);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(map, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                new URI(url),
                HttpMethod.POST,
                request,
                String.class);

        return response;
    }

    private static HttpHeaders getHttpHeaders(String token, String signKey) {
        HttpHeaders headers = new HttpHeaders();
        String traceId = UUID.randomUUID().toString();
        String timestamp = String.valueOf(Instant.now().getEpochSecond());
        String sign = JwtUtil.generateSign(signKey, traceId + timestamp);
        headers.add("traceId", traceId);
        headers.add("timestamp", timestamp);
        headers.add("sign", sign);
        headers.add("accessToken", token);
        return headers;
    }

    private static Map<String, Object> baseRequestMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("clientType", 2);
        map.put("clientName", "chrome浏览器");
        map.put("clientVersion", "1.0.0.beta");
        return map;
    }

}
