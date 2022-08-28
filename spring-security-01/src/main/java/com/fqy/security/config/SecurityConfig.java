package com.fqy.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

@Configuration
public class SecurityConfig{

    private final MyUserDetailService myUserDetailService;

    @Autowired
    public SecurityConfig(MyUserDetailService myUserDetailService) {
        this.myUserDetailService = myUserDetailService;
    }

    /* @Bean
    public UserDetailsService userDetailsService(){
        InMemoryUserDetailsManager detailsManager = new InMemoryUserDetailsManager();
        detailsManager.createUser(User.withUsername("fqy").password("{noop}123").roles("admin").build());
        return detailsManager;
    }*/

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                //只对“/index”,"/login.html"路径放行
                .authorizeRequests().antMatchers("/index","/login.html").permitAll().anyRequest().authenticated().and()
                .formLogin()
                .loginPage("/login.html")  //用来指定默认登录页面 注意： 一旦自定义登陆页面需指定登录的url
                .loginProcessingUrl("/doLogin")  //指定处理登录请求的url
                .usernameParameter("uname")
                .passwordParameter("passwd")
                //.successForwardUrl("/hello") //认证成功 forward 跳转路径
                //.defaultSuccessUrl("/hello",true) //认证成功 redirect 跳转路径  根据上一次保存的请求跳转 第二个参数是总是跳转到该路径
                .successHandler(new MySuccessHandle())//认证成功的处理 前后端分离方案
                //.failureForwardUrl("/login.html") //认证失败  forward 跳转 错误信息存在request
                //.failureUrl("/login.html") //认证失败  redirect 跳转 错误信息存在session中
                .failureHandler(new MyFailedHandle()) //自定义认证失败后的 逻辑  前后段分离方案
                .and()
                .logout()
                .logoutRequestMatcher(new OrRequestMatcher(
                        new AntPathRequestMatcher("/aa" ,"GET"),
                        new AntPathRequestMatcher("/bb" ,"POST")
                ))//改变注销登录的 url
                .logoutSuccessHandler(new MyLogoutSuccessHandle()) //注销成功后的处理  前后段分离的  方案
                .and()
                .csrf().disable()
                .build();
    }

}
