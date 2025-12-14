package com.shopx.service;

import com.shopx.entity.Product;

import java.util.List;
import java.util.Map;

/**
 * 推荐服务接口
 */
public interface RecommendationService {
    
    /**
     * 协同过滤推荐
     */
    List<Product> collaborativeFilterRecommend(Long userId, int limit);
    
    /**
     * 内容推荐
     */
    List<Product> contentBasedRecommend(Long userId, int limit);
    
    /**
     * 混合推荐
     */
    List<Product> hybridRecommend(Long userId, int limit);
    
    /**
     * 计算用户相似度
     */
    Map<Long, Double> calculateUserSimilarity(Long userId);
    
    /**
     * 计算商品相似度
     */
    Map<Long, Double> calculateProductSimilarity(Long productId);
    
    /**
     * 获取推荐解释
     */
    Map<String, Object> getRecommendationExplanation(Long userId, Long productId, String algorithm);
    
    /**
     * 过滤已购买商品
     */
    List<Product> filterPurchasedProducts(Long userId, List<Product> products);
    
    /**
     * 记录推荐反馈
     */
    void recordFeedback(Long userId, Long productId, String algorithm, String feedbackType, String reason);
    
    /**
     * 获取用户推荐偏好
     */
    Map<String, Object> getUserRecommendationPreferences(Long userId);
    
    /**
     * 更新用户推荐偏好
     */
    void updateUserRecommendationPreferences(Long userId, Map<String, Object> preferences);
}

