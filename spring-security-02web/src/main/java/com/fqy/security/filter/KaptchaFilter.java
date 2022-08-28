package com.fqy.security.filter;

import com.fqy.security.exction.KaptchaException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义验证码的  filter
 */
public class KaptchaFilter extends UsernamePasswordAuthenticationFilter {

    private static final String FORM_KAPTCHA_kEY = "kaptcha";

    private String kaptchaParameter = FORM_KAPTCHA_kEY;

    public String getKaptchaParameter() {
        return kaptchaParameter;
    }

    public void setKaptchaParameter(String kaptchaParameter) {
        this.kaptchaParameter = kaptchaParameter;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        //1.从请求中获取验证码
        String verifCode = request.getParameter(getKaptchaParameter());
        //2.与session 中验证码进行对比
        String sessionverifCode = (String) request.getSession().getAttribute("kaptcha");
        if (!ObjectUtils.isEmpty(sessionverifCode) && !ObjectUtils.isEmpty(verifCode) &&
           verifCode.equals(sessionverifCode)){
          return super.attemptAuthentication(request,response);
        }
        throw new KaptchaException("验证码不匹配!");
    }
}
