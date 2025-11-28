package com.hulahoop.bikewayback;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
// 💡 [해결책] @MapperScan을 추가하여 MyBatis 매퍼가 위치한 패키지를 지정합니다.
@MapperScan(basePackages = "com.hulahoop.bikewayback.model.dao")
@EnableScheduling // 🔹 스케줄러 활성화
public class BikewayBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(BikewayBackApplication.class, args);
    }
}