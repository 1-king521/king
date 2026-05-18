package com.example.wyk.model.request;

import lombok.Data;

@Data
public class CodeLoginRequest {
    // 手机号
    private String phone;
    // 短信验证码
    private String code;
}