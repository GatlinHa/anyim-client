package com.hibob.anyim.client;

import com.hibob.anyim.utils.JwtUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Slf4j
public class GroupMngClient {

    private UserClient userLocal;

    private long groupId;
    private int groupType;
    private String groupName;
    private String announcement;
    private String avatar;
    private List<Map<String, Object>> members;

    public GroupMngClient(UserClient userLocal,
                          int groupType,
                          String groupName,
                          String announcement,
                          String avatar,
                          List<Map<String, Object>> members) {
        this.userLocal = userLocal;
        this.groupType = groupType;
        this.groupName = groupName;
        this.announcement = announcement;
        this.avatar = avatar;
        this.members = members;
    }

    public GroupMngClient(GroupMngClient groupMngClient) {
        this.groupId = groupMngClient.getGroupId();
        this.userLocal = groupMngClient.getUserLocal();
        this.groupType = groupMngClient.getGroupType();
        this.groupName = groupMngClient.groupName;
        this.announcement = groupMngClient.getAnnouncement();
        this.avatar = groupMngClient.getAvatar();
        this.members = groupMngClient.getMembers();
    }

    private final RestTemplate restTemplate = new RestTemplate();

    public ResponseEntity<String> createGroup() throws Exception {
        String url = "http://localhost:80/groupmng/createGroup";
        HttpHeaders headers = getHttpHeaders(userLocal.getAccessToken(), userLocal.getAccessSecret());
        Map<String, Object> map = new HashMap<>();
        map.put("groupType", groupType);
        map.put("groupName", groupName);
        map.put("announcement", announcement);
        map.put("avatar", avatar);
        map.put("members", members);
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

    public ResponseEntity<String> queryGroupInfo() throws Exception {
        String url = "http://localhost:80/groupmng/queryGroupInfo";
        HttpHeaders headers = getHttpHeaders(userLocal.getAccessToken(), userLocal.getAccessSecret());
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

    public ResponseEntity<String> queryGroupList() throws Exception {
        String url = "http://localhost:80/groupmng/queryGroupList";
        HttpHeaders headers = getHttpHeaders(userLocal.getAccessToken(), userLocal.getAccessSecret());
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
