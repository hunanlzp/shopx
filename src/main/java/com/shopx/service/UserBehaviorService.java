package com.shopx.service;

import com.shopx.entity.UserBehavior;

import java.util.List;
import java.util.Map;

/**
 * 用户行为分析服务接口
 */
public interface UserBehaviorService {
    
    /**
     * 记录用户行为
     */
    void recordBehavior(Long userId, Long productId, String behaviorType, Map<String, Object> metadata);
    
    /**
     * 获取用户行为统计
     */
    Map<String, Object> getUserBehaviorStats(Long userId);
    
    /**
     * 分析用户偏好
     */
    Map<String, Object> analyzeUserPreferences(Long userId);
    
    /**
     * 获取用户最近行为
     */
    List<UserBehavior> getRecentBehaviors(Long userId, int limit);
    
    /**
     * 获取用户喜欢的商品类别
     */
    List<String> getFavoriteCategories(Long userId);
    
    /**
     * 获取用户偏好的价格区间
     */
    Map<String, Double> getPricePreference(Long userId);
}

