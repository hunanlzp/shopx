package com.shopx.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 极简高可用性配置
 */
@Configuration
public class HighAvailabilityConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}