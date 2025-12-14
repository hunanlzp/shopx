package com.shopx.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * AI服务配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "shopx.ai")
public class AIConfig {
    
    /**
     * AI服务提供商: openai, claude, custom
     */
    private String provider = "openai";
    
    /**
     * 是否启用AI服务
     */
    private Boolean enabled = true;
    
    /**
     * OpenAI配置
     */
    private OpenAI openai = new OpenAI();
    
    /**
     * Claude配置
     */
    private Claude claude = new Claude();
    
    /**
     * 自定义AI服务配置
     */
    private Custom custom = new Custom();
    
    /**
     * 是否启用降级策略
     */
    private Boolean fallbackEnabled = true;
    
    @Data
    public static class OpenAI {
        private String apiKey;
        private String apiUrl = "https://api.openai.com/v1/chat/completions";
        private String model = "gpt-3.5-turbo";
        private Integer maxTokens = 1000;
        private Double temperature = 0.7;
        private Integer timeout = 30000;
    }
    
    @Data
    public static class Claude {
        private String apiKey;
        private String apiUrl = "https://api.anthropic.com/v1/messages";
        private String model = "claude-3-sonnet-20240229";
        private Integer maxTokens = 1000;
    }
    
    @Data
    public static class Custom {
        private String apiUrl;
        private String apiKey;
        private Integer timeout = 30000;
    }
}

