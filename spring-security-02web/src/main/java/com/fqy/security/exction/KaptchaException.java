package com.fqy.security.exction;


import org.springframework.security.core.AuthenticationException;

//自定义验证码认证异常类
public class KaptchaException extends AuthenticationException {

    public KaptchaException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public KaptchaException(String msg) {
        super(msg);
    }
}
