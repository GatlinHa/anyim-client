package com.hibob.anyim.entity;

import lombok.Data;

import java.util.Date;

@Data
public class User {

    private String account;
    private String clientId;
    private String avatar;
    private String inviteCode;
    private String nickName;
    private String password;
    private String phoneNum;
    private String accessToken;
    private String accessSecret;
    private String refreshToken;
    private String refreshSecret;
    private String avatarThumb;
    private String sex;
    private String level;
    private String signature;
    private String email;
    private Date birthday;

    public User(
            String account,
            String clientId,
            String avatar,
            String inviteCode,
            String nickName,
            String password,
            String phoneNum,
            String email,
            Date birthday
    ) {
        this.account = account;
        this.clientId = clientId;
        this.avatar = avatar;
        this.inviteCode = inviteCode;
        this.nickName = nickName;
        this.password = password;
        this.phoneNum = phoneNum;
        this.email = email;
        this.birthday = birthday;
    }

    public User(User user) {
        this.account = user.account;
        this.clientId = user.clientId;
        this.avatar = user.avatar;
        this.inviteCode = user.inviteCode;
        this.nickName = user.nickName;
        this.password = user.password;
        this.phoneNum = user.phoneNum;
        this.accessToken = user.accessToken;
        this.accessSecret = user.accessSecret;
        this.refreshToken = user.refreshToken;
        this.refreshSecret = user.refreshSecret;
        this.avatarThumb = user.avatarThumb;
        this.sex = user.sex;
        this.level = user.level;
        this.signature = user.signature;
        this.email = user.email;
        this.birthday = user.birthday;
    }
}
