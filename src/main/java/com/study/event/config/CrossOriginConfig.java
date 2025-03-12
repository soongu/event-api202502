package com.study.event.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// 전역 크로스오리진 설정: 허용할 클라이언트를 설정
@Configuration
public class CrossOriginConfig implements WebMvcConfigurer {

    private String[] urls = {
            "http://localhost:5173",
            "http://localhost:5174",
            "http://localhost:5175",
            "http://gondue.co.kr.s3-website.ap-northeast-2.amazonaws.com",
            "http://gondue.co.kr"
    };

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry
                .addMapping("/api/**") // 클라이언트의 어떤 요청을 허용할지
                .allowedOrigins(urls) // 어떤 클라이언트의 주소를 허용할지
                .allowedMethods("*") // 어떤 요청방식을 허용할지
                .allowedHeaders("*") // 어떤 헤더를 포함시킬지
                .allowCredentials(true) // 쿠키 전송을 허용할지
        ;
    }
}
