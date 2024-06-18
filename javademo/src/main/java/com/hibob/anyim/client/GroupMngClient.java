package com.hibob.anyim.client;

import com.alibaba.fastjson.JSONObject;
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
import java.util.List;
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
        Map<String, Object> map = new HashMap<>(baseRequestMap());
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
        if (Integer.valueOf(JSONObject.parseObject(response.getBody()).getString("code")) == 0) {
            Long groupId = JSONObject.parseObject(response.getBody()).getJSONObject("data").getJSONObject("groupInfo").getLong("groupId");
            group.setGroupId(groupId);
        }
        return response;
    }

    public static ResponseEntity<String> queryGroupInfo(Group group) throws Exception {
        String url = "http://localhost:80/groupmng/queryGroupInfo";
        HttpHeaders headers = getHttpHeaders(group.getUserLocal().getAccessToken(), group.getUserLocal().getAccessSecret());
        Map<String, Object> map = new HashMap<>(baseRequestMap());
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
        Map<String, Object> map = new HashMap<>(baseRequestMap());
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
        Map<String, Object> map = new HashMap<>(baseRequestMap());
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

    public static ResponseEntity<String> modifyGroup(Group group, String groupName, String announcement, String avatar) throws Exception {
        String url = "http://localhost:80/groupmng/modifyGroup";
        HttpHeaders headers = getHttpHeaders(group.getUserLocal().getAccessToken(), group.getUserLocal().getAccessSecret());
        Map<String, Object> map = new HashMap<>(baseRequestMap());
        map.put("groupId", group.getGroupId());
        map.put("groupName", groupName);
        map.put("announcement", announcement);
        map.put("avatar", avatar);
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

    public static ResponseEntity<String> changeMembers(Group group, List<Map<String, Object>> addMembers, List<Map<String, Object>> delMembers) throws Exception {
        String url = "http://localhost:80/groupmng/changeMembers";
        HttpHeaders headers = getHttpHeaders(group.getUserLocal().getAccessToken(), group.getUserLocal().getAccessSecret());
        Map<String, Object> map = new HashMap<>(baseRequestMap());
        map.put("groupId", group.getGroupId());
        map.put("addMembers", addMembers);
        map.put("delMembers", delMembers);
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

    public static ResponseEntity<String> changeRole(Group group, String memberAccount, int memberRole) throws Exception {
        String url = "http://localhost:80/groupmng/changeRole";
        HttpHeaders headers = getHttpHeaders(group.getUserLocal().getAccessToken(), group.getUserLocal().getAccessSecret());
        Map<String, Object> map = new HashMap<>(baseRequestMap());
        map.put("groupId", group.getGroupId());
        map.put("memberAccount", memberAccount);
        map.put("memberRole", memberRole);
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

    public static ResponseEntity<String> ownerTransfer(Group group, User owner, User newOwner) throws Exception {
        String url = "http://localhost:80/groupmng/ownerTransfer";
        HttpHeaders headers = getHttpHeaders(owner.getAccessToken(), owner.getAccessSecret());
        Map<String, Object> map = new HashMap<>(baseRequestMap());
        map.put("groupId", group.getGroupId());
        map.put("account", newOwner.getAccount());
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

    private static Map<String, Object> baseRequestMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("clientType", 2);
        map.put("clientName", "chrome浏览器");
        map.put("clientVersion", "1.0.0.beta");
        return map;
    }

}
