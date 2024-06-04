package com.hibob.anyim.client.client;

import com.alibaba.fastjson.JSONObject;
import com.hibob.anyim.client.utils.JwtUtil;
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

    private String account;
    private String clientId;
    private String headImage;
    private String inviteCode;
    private String nickName;
    private String password;
    private String phoneNum;
    private String accessToken;
    private String accessSecret;
    private String refreshToken;
    private String refreshSecret;
    private String headImageThumb;
    private String sex;
    private String level;
    private String signature;

    public UserClient(
            String account,
            String clientId,
            String headImage,
            String inviteCode,
            String nickName,
            String password,
            String phoneNum
    ) {
        this.account = account;
        this.clientId = clientId;
        this.headImage = headImage;
        this.inviteCode = inviteCode;
        this.nickName = nickName;
        this.password = password;
        this.phoneNum = phoneNum;
    }

    public UserClient(UserClient userClient) {
        this.account = userClient.account;
        this.clientId = userClient.clientId;
        this.headImage = userClient.headImage;
        this.inviteCode = userClient.inviteCode;
        this.nickName = userClient.nickName;
        this.password = userClient.password;
        this.phoneNum = userClient.phoneNum;
        this.accessToken = userClient.accessToken;
        this.accessSecret = userClient.accessSecret;
        this.refreshToken = userClient.refreshToken;
        this.refreshSecret = userClient.refreshSecret;
        this.headImageThumb = userClient.headImageThumb;
        this.sex = userClient.sex;
        this.level = userClient.level;
        this.signature = userClient.signature;
    }

    private final RestTemplate restTemplate = new RestTemplate();

    public ResponseEntity<String> register() throws Exception {
        String url = "http://localhost:80/user/register";
        HttpHeaders headers = new HttpHeaders();
        Map<String, String> map = new HashMap<>();
        map.put("account", account);
        map.put("headImage", headImage);
        map.put("inviteCode", inviteCode);
        map.put("nickName", nickName);
        map.put("password", password);
        map.put("phoneNum", phoneNum);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(map, headers);
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

    public ResponseEntity<String> login() throws Exception {
        String url = "http://localhost:80/user/login";
        HttpHeaders headers = new HttpHeaders();
        Map<String, String> map = new HashMap<>();
        map.put("account", account);
        map.put("clientId", clientId);
        map.put("password", password);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(map, headers);
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
        accessToken = jsonObject.getJSONObject("data").getJSONObject("accessToken").getString("token");
        accessSecret = jsonObject.getJSONObject("data").getJSONObject("accessToken").getString("secret");
        refreshToken = jsonObject.getJSONObject("data").getJSONObject("refreshToken").getString("token");
        refreshSecret = jsonObject.getJSONObject("data").getJSONObject("refreshToken").getString("secret");
        return response;
    }

    /**
     * true：存在这个账号，false：不存在这个账号
     * @return
     * @throws Exception
     */
    public Boolean validateAccount() throws Exception {
        String url = "http://localhost:80/user/validateAccount";
        HttpHeaders headers = new HttpHeaders();
        Map<String, String> map = new HashMap<>();
        map.put("account", account);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(map, headers);
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

    public ResponseEntity<String> logout() throws Exception {
        String url = "http://localhost:80/user/logout";
        HttpHeaders headers = getHttpHeaders(accessToken, accessSecret);
        Map<String, String> map = new HashMap<>();
        HttpEntity<Map<String, String>> request = new HttpEntity<>(map, headers);
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

    public ResponseEntity<String> deregister() throws Exception {
        String url = "http://localhost:80/user/deregister";
        HttpHeaders headers = getHttpHeaders(accessToken, accessSecret);
        Map<String, String> map = new HashMap<>();
        HttpEntity<Map<String, String>> request = new HttpEntity<>(map, headers);
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

    public ResponseEntity<String> modifyPwd(String oldPassword, String password) throws Exception {
        String url = "http://localhost:80/user/modifyPwd";
        HttpHeaders headers = getHttpHeaders(accessToken, accessSecret);
        Map<String, String> map = new HashMap<>();
        map.put("oldPassword", oldPassword);
        map.put("password", password);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(map, headers);
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

    public ResponseEntity<String> refreshToken() throws Exception {
        String url = "http://localhost:80/user/refreshToken";
        HttpHeaders headers = getHttpHeadersForRefresh(refreshToken, refreshSecret);
        Map<String, String> map = new HashMap<>();
        HttpEntity<Map<String, String>> request = new HttpEntity<>(map, headers);
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
        accessToken = jsonObject.getJSONObject("data").getJSONObject("accessToken").getString("token");
        accessSecret = jsonObject.getJSONObject("data").getJSONObject("accessToken").getString("secret");

        return response;
    }

    public ResponseEntity<String> querySelf() throws Exception {
        String url = "http://localhost:80/user/querySelf";
        HttpHeaders headers = getHttpHeaders(accessToken, accessSecret);
        Map<String, String> map = new HashMap<>();
        HttpEntity<Map<String, String>> request = new HttpEntity<>(map, headers);
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

    public ResponseEntity<String> modifySelf(String nickName,
                                 String headImage,
                                 String headImageThumb,
                                 String sex,
                                 String level,
                                 String signature) throws Exception {
        String url = "http://localhost:80/user/modifySelf";
        HttpHeaders headers = getHttpHeaders(accessToken, accessSecret);
        Map<String, String> map = new HashMap<>();
        map.put("nickName", nickName);
        map.put("headImage", headImage);
        map.put("headImageThumb", headImageThumb);
        map.put("sex", sex);
        map.put("level", level);
        map.put("signature", signature);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(map, headers);
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

    public ResponseEntity<String> query(String account) throws Exception {
        String url = "http://localhost:80/user/query";
        HttpHeaders headers = getHttpHeaders(accessToken, accessSecret);
        Map<String, String> map = new HashMap<>();
        map.put("account", account);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(map, headers);
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

    public ResponseEntity<String> findByNick(String nickNameKeyWords) throws Exception {
        String url = "http://localhost:80/user/findByNick";
        HttpHeaders headers = getHttpHeaders(accessToken, accessSecret);
        Map<String, String> map = new HashMap<>();
        map.put("nickNameKeyWords", nickNameKeyWords);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(map, headers);
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

    private HttpHeaders getHttpHeadersForRefresh(String token, String signKey) {
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
