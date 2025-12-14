package com.shopx.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shopx.entity.UserBehavior;
import com.shopx.mapper.UserBehaviorMapper;
import com.shopx.service.UserBehaviorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户行为分析服务实现类
 */
@Slf4j
@Service
public class UserBehaviorServiceImpl extends ServiceImpl<UserBehaviorMapper, UserBehavior> implements UserBehaviorService {
    
    @Override
    public void recordBehavior(Long userId, Long productId, String behaviorType, Map<String, Object> metadata) {
        UserBehavior behavior = new UserBehavior();
        behavior.setUserId(userId);
        behavior.setProductId(productId);
        behavior.setBehaviorType(behaviorType);
        
        if (metadata != null && !metadata.isEmpty()) {
            behavior.setSessionInfo(JSON.toJSONString(metadata));
        }
        
        behavior.setCreateTime(LocalDateTime.now());
        
        save(behavior);
        log.info("记录用户行为: userId={}, productId={}, behaviorType={}", userId, productId, behaviorType);
    }
    
    @Override
    public Map<String, Object> getUserBehaviorStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 查询用户所有行为
        List<UserBehavior> behaviors = list(new LambdaQueryWrapper<UserBehavior>()
                .eq(UserBehavior::getUserId, userId));
        
        // 统计各类型行为数量
        Map<String, Long> behaviorCounts = behaviors.stream()
                .collect(Collectors.groupingBy(
                        UserBehavior::getBehaviorType,
                        Collectors.counting()
                ));
        
        stats.put("totalBehaviors", behaviors.size());
        stats.put("behaviorCounts", behaviorCounts);
        stats.put("viewCount", behaviorCounts.getOrDefault("VIEW", 0L));
        stats.put("likeCount", behaviorCounts.getOrDefault("LIKE", 0L));
        stats.put("shareCount", behaviorCounts.getOrDefault("SHARE", 0L));
        stats.put("addCartCount", behaviorCounts.getOrDefault("ADD_CART", 0L));
        stats.put("purchaseCount", behaviorCounts.getOrDefault("PURCHASE", 0L));
        
        // 最近30天的行为
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        long recentBehaviors = behaviors.stream()
                .filter(b -> b.getCreateTime().isAfter(thirtyDaysAgo))
                .count();
        stats.put("recentBehaviors", recentBehaviors);
        
        return stats;
    }
    
    @Override
    public Map<String, Object> analyzeUserPreferences(Long userId) {
        Map<String, Object> preferences = new HashMap<>();
        
        // 获取用户行为
        List<UserBehavior> behaviors = list(new LambdaQueryWrapper<UserBehavior>()
                .eq(UserBehavior::getUserId, userId)
                .orderByDesc(UserBehavior::getCreateTime)
                .last("LIMIT 1000"));
        
        if (behaviors.isEmpty()) {
            return preferences;
        }
        
        // 分析喜欢的商品类别（基于LIKE和PURCHASE行为）
        List<Long> likedProductIds = behaviors.stream()
                .filter(b -> "LIKE".equals(b.getBehaviorType()) || "PURCHASE".equals(b.getBehaviorType()))
                .map(UserBehavior::getProductId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        
        // 分析浏览的商品类别（基于VIEW行为）
        List<Long> viewedProductIds = behaviors.stream()
                .filter(b -> "VIEW".equals(b.getBehaviorType()))
                .map(UserBehavior::getProductId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        
        preferences.put("likedProductIds", likedProductIds);
        preferences.put("viewedProductIds", viewedProductIds);
        preferences.put("totalLikes", likedProductIds.size());
        preferences.put("totalViews", viewedProductIds.size());
        
        // 分析活跃时间段
        Map<Integer, Long> hourDistribution = behaviors.stream()
                .collect(Collectors.groupingBy(
                        b -> b.getCreateTime().getHour(),
                        Collectors.counting()
                ));
        preferences.put("activeHours", hourDistribution);
        
        // 分析行为频率
        long daysWithActivity = behaviors.stream()
                .map(b -> b.getCreateTime().toLocalDate())
                .distinct()
                .count();
        preferences.put("activeDays", daysWithActivity);
        preferences.put("avgBehaviorsPerDay", behaviors.size() / Math.max(1, daysWithActivity));
        
        return preferences;
    }
    
    @Override
    public List<UserBehavior> getRecentBehaviors(Long userId, int limit) {
        return list(new LambdaQueryWrapper<UserBehavior>()
                .eq(UserBehavior::getUserId, userId)
                .orderByDesc(UserBehavior::getCreateTime)
                .last("LIMIT " + limit));
    }
    
    @Override
    public List<String> getFavoriteCategories(Long userId) {
        // 这里需要关联商品表获取类别信息
        // 简化实现：返回空列表，实际应该查询商品类别
        return new ArrayList<>();
    }
    
    @Override
    public Map<String, Double> getPricePreference(Long userId) {
        // 这里需要关联商品表获取价格信息
        // 简化实现：返回默认值
        Map<String, Double> pricePref = new HashMap<>();
        pricePref.put("min", 0.0);
        pricePref.put("max", 10000.0);
        pricePref.put("avg", 500.0);
        return pricePref;
    }
}

