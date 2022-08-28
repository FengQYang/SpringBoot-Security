package com.fqy.security.config;

import com.fqy.security.filter.KaptchaFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 自定义 security 相关配置
 */
@Configuration
public class SecurityConfigure extends WebSecurityConfigurerAdapter {

     @Bean
    public UserDetailsService userDetailsService(){
        InMemoryUserDetailsManager detailsManager = new InMemoryUserDetailsManager();
        detailsManager.createUser(User.withUsername("fqy").password("{noop}123").roles("admin").build());
        return detailsManager;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService());
    }

    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public KaptchaFilter kaptchaFilter() throws Exception {
        KaptchaFilter kaptchaFilter = new KaptchaFilter();
        kaptchaFilter.setFilterProcessesUrl("/doLogin");
        kaptchaFilter.setUsernameParameter("uname");
        kaptchaFilter.setPasswordParameter("passwd");
        kaptchaFilter.setKaptchaParameter("kaptcha");
        //指定认证管理器
        kaptchaFilter.setAuthenticationManager(authenticationManagerBean());
        //成功处理
        kaptchaFilter.setAuthenticationSuccessHandler((req,resp,auth)->{
            resp.sendRedirect("/index.html");
        });
        //失败处理
        kaptchaFilter.setAuthenticationFailureHandler((req,resp,auth)->{
            resp.sendRedirect("/login.html");
        });
        return kaptchaFilter;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/login.html","/vc.jpg").permitAll()
                .anyRequest().authenticated().and()
                .formLogin()
                .loginPage("/login.html")
                .loginProcessingUrl("/doLogin")
                .usernameParameter("uname")
                .passwordParameter("passwd")
                .defaultSuccessUrl("/index.html",true)
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/logout.html")
                .and()
                .addFilterAt(kaptchaFilter(), UsernamePasswordAuthenticationFilter.class)
                .csrf().disable();
    }
 }
