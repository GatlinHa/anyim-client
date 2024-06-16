package com.hibob.anyim.client;

import com.hibob.anyim.entity.Group;
import com.hibob.anyim.entity.User;
import com.hibob.anyim.utils.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@Slf4j
@AllArgsConstructor
public class GroupMngClient {

    private static final RestTemplate restTemplate = new RestTemplate();

    public static ResponseEntity<String> createGroup(Group group) throws Exception {
        String url = "http://localhost:80/groupmng/createGroup";
        HttpHeaders headers = getHttpHeaders(group.getUserLocal().getAccessToken(), group.getUserLocal().getAccessSecret());
        Map<String, Object> map = new HashMap<>();
        map.put("groupType", group.getGroupType());
        map.put("groupName", group.getGroupName());
        map.put("announcement", group.getAnnouncement());
        map.put("avatar", group.getAvatar());
        map.put("members", group.getMembers());
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(map, headers);
        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(
                    new URI(url),
                    HttpMethod.POST,
                    request,
                    String.class);
        }
        catch (HttpClientErrorException.Unauthorized e) {
            response = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            return response;
        }
        return response;
    }

    public static ResponseEntity<String> queryGroupInfo(Group group) throws Exception {
        String url = "http://localhost:80/groupmng/queryGroupInfo";
        HttpHeaders headers = getHttpHeaders(group.getUserLocal().getAccessToken(), group.getUserLocal().getAccessSecret());
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", group.getGroupId());
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(map, headers);
        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(
                    new URI(url),
                    HttpMethod.POST,
                    request,
                    String.class);
        }
        catch (HttpClientErrorException.Unauthorized e) {
            response = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            return response;
        }
        return response;
    }

    public static ResponseEntity<String> queryGroupList(User user) throws Exception {
        String url = "http://localhost:80/groupmng/queryGroupList";
        HttpHeaders headers = getHttpHeaders(user.getAccessToken(), user.getAccessSecret());
        Map<String, Object> map = new HashMap<>();
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(map, headers);
        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(
                    new URI(url),
                    HttpMethod.POST,
                    request,
                    String.class);
        }
        catch (HttpClientErrorException.Unauthorized e) {
            response = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            return response;
        }
        return response;
    }

    public static ResponseEntity<String> delGroup(User user, long groupId) throws Exception {
        String url = "http://localhost:80/groupmng/delGroup";
        HttpHeaders headers = getHttpHeaders(user.getAccessToken(), user.getAccessSecret());
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", groupId);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(map, headers);
        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(
                    new URI(url),
                    HttpMethod.POST,
                    request,
                    String.class);
        }
        catch (HttpClientErrorException.Unauthorized e) {
            response = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            return response;
        }
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

}
