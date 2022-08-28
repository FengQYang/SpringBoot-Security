package com.fqy.security.controller;

import com.google.code.kaptcha.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Controller
public class VerifyCodeController {

    private final Producer producer;

    @Autowired
    public VerifyCodeController(Producer producer) {
        this.producer = producer;
    }

    @RequestMapping("/vc.jpg")
    public void verifCode(HttpServletResponse response, HttpSession session) throws IOException {
        //1.生成验证码
        String verifCode = producer.createText();
        //2.保存到  session中
        session.setAttribute("kaptcha",verifCode);
        //3.生成图片
        BufferedImage image = producer.createImage(verifCode);
        //4.响应
        response.setContentType("image/png");
        ServletOutputStream os = response.getOutputStream();
        ImageIO.write(image,"jpg",os);
    }

}
