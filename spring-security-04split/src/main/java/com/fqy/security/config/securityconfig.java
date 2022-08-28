package com.fqy.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fqy.security.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class securityconfig extends WebSecurityConfigurerAdapter{

    private final MyUserDetailsService myUserDetailsService;

    @Autowired
    public securityconfig(MyUserDetailsService myUserDetailsService) {
        this.myUserDetailsService = myUserDetailsService;
    }


    /*本地的AuthenticationManagerBuilder*/
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myUserDetailsService);
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    //自定义  filter 交给工厂管理
    @Bean
    public LoginFilter loginFilter() throws Exception {
        LoginFilter loginFilter = new LoginFilter();
        loginFilter.setFilterProcessesUrl("/doLogin"); //指定认证url
        loginFilter.setUsernameParameter("uname"); //指定接受json 用户名 key
        loginFilter.setPasswordParameter("passwd"); //指定接受json 密码 key
        loginFilter.setAuthenticationManager(authenticationManagerBean());
        //认证成功处理
        loginFilter.setAuthenticationSuccessHandler(((request, response, authentication) -> {
            Map<String,Object> result = new HashMap<>();
            result.put("msg","登录成功");
            result.put("用户信息",authentication.getPrincipal());
            response.setStatus(HttpStatus.OK.value());
            response.setContentType("application/json;charset=UTF-8");
            String s = new ObjectMapper().writeValueAsString(result);
            response.getWriter().println(s);
        }));
        //认证失败处理
        loginFilter.setAuthenticationFailureHandler((request, response, exception) -> {
            Map<String,Object> result = new HashMap<>();
            result.put("msg","登录失败");
            result.put("用户信息",exception.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setContentType("application/json;charset=UTF-8");
            String s = new ObjectMapper().writeValueAsString(result);
            response.getWriter().println(s);
        });
        return loginFilter;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest().authenticated().and()
                .formLogin()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint((req,resp,auth)->{
                    resp.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    resp.setStatus(HttpStatus.UNAUTHORIZED.value());
                    resp.getWriter().println("请认证之后再去处理");
                })
                .and()
                .logout()
                .logoutSuccessHandler((req,resp,auth)->{
                    Map<String,Object> result = new HashMap<>();
                    result.put("msg","注销成功");
                    result.put("用户信息",auth.getPrincipal());
                    resp.setStatus(HttpStatus.OK.value());
                    resp.setContentType("application/json;charset=UTF-8");
                    String s = new ObjectMapper().writeValueAsString(result);
                    resp.getWriter().println(s);
                })
                .and()
                .csrf().disable()
                //at:用来某个 filter 替换过滤器链中哪个filter
                // before:放在过滤器链中哪个filter之前
                // after:放在过滤器链中哪个filter之后
                .addFilterAt(loginFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
