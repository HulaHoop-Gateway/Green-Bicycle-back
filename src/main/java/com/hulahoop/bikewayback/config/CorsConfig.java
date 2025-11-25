// 예시: com.novacinema.config.CorsConfig.java
package com.hulahoop.bikewayback.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // 모든 API 경로에 적용
                        .allowedOriginPatterns("http://localhost:3000","http://localhost:5174")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        //️ 자격 증명(쿠키 등)을 허용하면 이 설정이 필수
                        .allowCredentials(true);
            }
        };
    }
}