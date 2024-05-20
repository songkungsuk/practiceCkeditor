package com.sbl.demo.sblproject.domain.web.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserDto {

    private long usiNum;
    private String usiId;
    private String usiPwd;
    private String usiName;
    private String usiPhoneNum;
    private String usiEmail;
    private String usiAuth;
    private long usiLastLgin;
    private String credat;
    private String cretim;
    private String lmodat;
    private String lmotim;
    // 유저 수정시의 패스워드
    private String password;
}
