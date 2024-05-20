package com.sbl.demo.sblproject.domain;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private long usiNum;
    private String usiId;
    private String usiPwd;
    private String usiName;
    private String usiPhoneNum;
    private String usiEmail;
    private String usiAuth;
    private String active;

}
