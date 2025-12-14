package com.shopx.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * API版本管理配置
 */
@Configuration
public class ApiVersionConfig implements WebMvcConfigurer {
    
    /**
     * 自定义请求映射处理器
     * 支持API版本管理
     */
    @Bean
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
        RequestMappingHandlerMapping mapping = new RequestMappingHandlerMapping();
        mapping.setOrder(0);
        return mapping;
    }
}
