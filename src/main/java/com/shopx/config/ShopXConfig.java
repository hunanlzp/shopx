package com.shopx.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * 应用配置类
 * 统一管理所有配置属性
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "shopx")
public class ShopXConfig {
    
    /**
     * Redis配置
     */
    private Redis redis = new Redis();
    
    /**
     * 异步配置
     */
    private Async async = new Async();
    
    /**
     * 文件上传配置
     */
    private FileUpload fileUpload = new FileUpload();
    
    /**
     * 业务配置
     */
    private Business business = new Business();
    
    /**
     * 安全配置
     */
    private Security security = new Security();
    
    @Data
    public static class Redis {
        private String lockPrefix = "shopx:lock:";
        private Integer defaultTimeout = 30;
        private Integer maxRetryTimes = 3;
    }
    
    @Data
    public static class Async {
        private Integer corePoolSize = 5;
        private Integer maxPoolSize = 20;
        private Integer queueCapacity = 100;
        private Integer keepAliveSeconds = 60;
        private String threadNamePrefix = "shopx-async-";
    }
    
    @Data
    public static class FileUpload {
        private String uploadPath = "./uploads/";
        private Long maxFileSize = 10 * 1024 * 1024L; // 10MB
        private String[] allowedTypes = {"jpg", "jpeg", "png", "gif", "pdf", "doc", "docx"};
        private Boolean enableCompression = true;
    }
    
    @Data
    public static class Business {
        private Integer defaultPageSize = 20;
        private Integer maxPageSize = 100;
        private Integer sessionTimeout = 30 * 60; // 30分钟
        private Integer maxLoginAttempts = 5;
        private Integer lockoutDuration = 15 * 60; // 15分钟
        private Integer orderTimeout = 1800; // 订单超时时间（秒）
        private Integer maxCartItems = 100; // 购物车最大商品数
        private Map<String, Object> features;
    }
    
    @Data
    public static class Security {
        private String jwtSecret = "shopx-jwt-secret";
        private Integer jwtExpiration = 7 * 24 * 60 * 60; // 7天
        private Boolean enableCors = true;
        private String[] corsOrigins = {"*"};
        private Boolean enableCsrf = false;
        private Integer passwordMinLength = 6;
        private Integer passwordMaxLength = 20;
    }
}
