package com.hibob.anyim.client;

import com.hibob.anyim.utils.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
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

@Data
@Slf4j
@AllArgsConstructor
public class ChatClient {

    private UserClient userClientLocal;
    private UserClient userClientPeer;

    private final RestTemplate restTemplate = new RestTemplate();

    public ResponseEntity<String> pullMsg(long lastMsgId, long lastPullTime, int pageSize) throws Exception {
        userClientLocal.login();
        String url = "http://localhost:80/chat/pullMsg";
        HttpHeaders headers = getHttpHeaders(userClientLocal.getAccessToken(), userClientLocal.getAccessSecret());
        Map<String, Object> map = new HashMap<>();
        map.put("toAccount", userClientPeer.getAccount());
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

    public ResponseEntity<String> history(long startTime, long endTime, long lastMsgId, int pageSize) throws Exception {
        userClientLocal.login();
        String url = "http://localhost:80/chat/history";
        HttpHeaders headers = getHttpHeaders(userClientLocal.getAccessToken(), userClientLocal.getAccessSecret());
        Map<String, Object> map = new HashMap<>();
        map.put("toAccount", userClientPeer.getAccount());
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


    private HttpHeaders getHttpHeaders(String token, String signKey) {
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

}
