package com.shopx.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 商品审核配置类
 * 用于管理商品完整度评分的权重配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "shopx.product.audit")
public class ProductAuditConfig {
    
    private ScoreWeights scoreWeights = new ScoreWeights();
    
    @Data
    public static class ScoreWeights {
        // 基本信息（40分）
        private int basicInfo = 40;
        private int name = 10;
        private int description = 10;
        private int price = 10;
        private int category = 10;
        
        // 图片（20分）
        private int images = 20;
        private int minImageCount = 3;
        private int imageScorePerItem = 7;
        
        // 详细属性（20分）
        private int details = 20;
        private int scenarios = 5;
        private int lifestyleTags = 5;
        private int stock = 5;
        private int has3dPreview = 5;
        
        // AR/VR体验（10分）
        private int arvr = 10;
        
        // 价值循环信息（10分）
        private int recycle = 10;
        
        // 最大分数
        private int maxScore = 100;
    }
}

