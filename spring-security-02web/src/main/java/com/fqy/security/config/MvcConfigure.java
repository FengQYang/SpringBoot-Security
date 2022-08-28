package com.fqy.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 对  SpringMVC 进行自定义配置
 */
@Configuration
public class MvcConfigure implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

        registry.addViewController("/index.html").setViewName("index");
        registry.addViewController("/login.html").setViewName("login");
    }
}
