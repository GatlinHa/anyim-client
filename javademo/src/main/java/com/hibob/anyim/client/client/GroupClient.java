package com.hibob.anyim.client.client;

import com.hibob.anyim.client.utils.JwtUtil;
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
public class GroupClient {

    private UserClient userLocal;

    private int groupType;
    private String groupName;
    private String announcement;
    private String avatar;
    private List<Map<String, Object>> members;

    public GroupClient(UserClient userLocal,
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

    public GroupClient(GroupClient groupClient) {
        this.userLocal = groupClient.getUserLocal();
        this.groupType = groupClient.getGroupType();
        this.groupName = groupClient.groupName;
        this.announcement = groupClient.getAnnouncement();
        this.avatar = groupClient.getAvatar();
        this.members = groupClient.getMembers();
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
