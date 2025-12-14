package com.shopx;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * ShopX应用启动类
 * 采用Spring Boot 3.2.0作为基础框架
 */
@SpringBootApplication
@MapperScan("com.shopx.mapper")
@EnableCaching
@EnableAsync
public class ShopXApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopXApplication.class, args);
    }

}