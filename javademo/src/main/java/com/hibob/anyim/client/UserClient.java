package com.hibob.anyim.client;

import com.alibaba.fastjson.JSONObject;
import com.hibob.anyim.entity.User;
import com.hibob.anyim.utils.JwtUtil;
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
public class UserClient {

    private static final RestTemplate restTemplate = new RestTemplate();

    public static ResponseEntity<String> register(User user) throws Exception {
        String url = "http://localhost:80/user/register";
        HttpHeaders headers = new HttpHeaders();
        Map<String, Object> map = new HashMap<>(baseRequestMap());
        map.put("account", user.getAccount());
        map.put("avatar", user.getAvatar());
        map.put("inviteCode", user.getInviteCode());
        map.put("nickName", user.getNickName());
        map.put("password", user.getPassword());
        map.put("phoneNum", user.getPhoneNum());
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

    public static ResponseEntity<String> login(User user) throws Exception {
        String url = "http://localhost:80/user/login";
        HttpHeaders headers = new HttpHeaders();
        Map<String, Object> map = new HashMap<>(baseRequestMap());
        map.put("account", user.getAccount());
        map.put("clientId", user.getClientId());
        map.put("password", user.getPassword());
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

        JSONObject jsonObject = JSONObject.parseObject(response.getBody());
        String accessToken = jsonObject.getJSONObject("data").getJSONObject("accessToken").getString("token");
        String accessSecret = jsonObject.getJSONObject("data").getJSONObject("accessToken").getString("secret");
        String refreshToken = jsonObject.getJSONObject("data").getJSONObject("refreshToken").getString("token");
        String refreshSecret = jsonObject.getJSONObject("data").getJSONObject("refreshToken").getString("secret");

        user.setAccessToken(accessToken);
        user.setAccessSecret(accessSecret);
        user.setRefreshToken(refreshToken);
        user.setRefreshSecret(refreshSecret);

        return response;
    }

    /**
     * true：存在这个账号，false：不存在这个账号
     * @return
     * @throws Exception
     */
    public static Boolean validateAccount(User user) throws Exception {
        String url = "http://localhost:80/user/validateAccount";
        HttpHeaders headers = new HttpHeaders();
        Map<String, Object> map = new HashMap<>(baseRequestMap());
        map.put("account", user.getAccount());
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
            throw e;
        }

        JSONObject object = JSONObject.parseObject(response.getBody());
        if (object.getString("code").equals("0")) {
            return false;
        }
        else {
            return true;
        }
    }

    public static ResponseEntity<String> logout(User user) throws Exception {
        String url = "http://localhost:80/user/logout";
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

    public static ResponseEntity<String> deregister(User user) throws Exception {
        String url = "http://localhost:80/user/deregister";
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

    public static ResponseEntity<String> modifyPwd(User user, String oldPassword, String password) throws Exception {
        String url = "http://localhost:80/user/modifyPwd";
        HttpHeaders headers = getHttpHeaders(user.getAccessToken(), user.getAccessSecret());
        Map<String, Object> map = new HashMap<>(baseRequestMap());
        map.put("oldPassword", oldPassword);
        map.put("password", password);
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

    public static ResponseEntity<String> refreshToken(User user) throws Exception {
        String url = "http://localhost:80/user/refreshToken";
        HttpHeaders headers = getHttpHeadersForRefresh(user.getRefreshToken(), user.getRefreshSecret());
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

        JSONObject jsonObject = JSONObject.parseObject(response.getBody());
        String accessToken = jsonObject.getJSONObject("data").getJSONObject("accessToken").getString("token");
        String accessSecret = jsonObject.getJSONObject("data").getJSONObject("accessToken").getString("secret");

        user.setAccessToken(accessToken);
        user.setAccessSecret(accessSecret);

        return response;
    }

    public static ResponseEntity<String> querySelf(User user) throws Exception {
        String url = "http://localhost:80/user/querySelf";
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

    public static ResponseEntity<String> modifySelf(User user, String nickName,
                                 String avatar,
                                 String avatarThumb,
                                 String sex,
                                 String level,
                                 String signature) throws Exception {
        String url = "http://localhost:80/user/modifySelf";
        HttpHeaders headers = getHttpHeaders(user.getAccessToken(), user.getAccessSecret());
        Map<String, Object> map = new HashMap<>(baseRequestMap());
        map.put("nickName", nickName);
        map.put("avatar", avatar);
        map.put("avatarThumb", avatarThumb);
        map.put("sex", sex);
        map.put("level", level);
        map.put("signature", signature);
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

    public static ResponseEntity<String> query(User user, String account) throws Exception {
        String url = "http://localhost:80/user/query";
        HttpHeaders headers = getHttpHeaders(user.getAccessToken(), user.getAccessSecret());
        Map<String, Object> map = new HashMap<>(baseRequestMap());
        map.put("account", account);
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

    public static ResponseEntity<String> findByNick(User user, String nickNameKeyWords) throws Exception {
        String url = "http://localhost:80/user/findByNick";
        HttpHeaders headers = getHttpHeaders(user.getAccessToken(), user.getAccessSecret());
        Map<String, Object> map = new HashMap<>(baseRequestMap());
        map.put("nickNameKeyWords", nickNameKeyWords);
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

    private static HttpHeaders getHttpHeadersForRefresh(String token, String signKey) {
        HttpHeaders headers = new HttpHeaders();
        String traceId = UUID.randomUUID().toString();
        String timestamp = String.valueOf(Instant.now().getEpochSecond());
        String sign = JwtUtil.generateSign(signKey, traceId + timestamp);
        headers.add("traceId", traceId);
        headers.add("timestamp", timestamp);
        headers.add("sign", sign);
        headers.add("refreshToken", token);
        return headers;
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
