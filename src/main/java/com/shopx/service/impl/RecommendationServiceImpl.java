package com.shopx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shopx.constant.Constants;
import com.shopx.entity.Product;
import com.shopx.entity.UserBehavior;
import com.shopx.mapper.ProductMapper;
import com.shopx.mapper.UserBehaviorMapper;
import com.shopx.service.RecommendationService;
import com.shopx.service.UserBehaviorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 推荐服务实现类
 */
@Slf4j
@Service
public class RecommendationServiceImpl implements RecommendationService {
    
    @Autowired
    private UserBehaviorMapper userBehaviorMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired(required = false)
    private UserBehaviorService userBehaviorService;
    
    @Autowired(required = false)
    private com.shopx.mapper.RecommendationFeedbackMapper recommendationFeedbackMapper;
    
    @Autowired(required = false)
    private com.shopx.mapper.UserRecommendationPreferenceMapper preferenceMapper;
    
    @Override
    public List<Product> collaborativeFilterRecommend(Long userId, int limit) {
        log.info("协同过滤推荐: userId={}, limit={}", userId, limit);
        
        try {
            // 1. 获取用户行为数据
            List<UserBehavior> userBehaviors = userBehaviorMapper.selectList(
                new LambdaQueryWrapper<UserBehavior>()
                    .eq(UserBehavior::getUserId, userId)
            );
            
            if (userBehaviors.isEmpty()) {
                // 新用户，返回热门商品
                return getHotProducts(limit);
            }
            
            // 2. 获取用户喜欢的商品ID列表
            Set<Long> likedProductIds = userBehaviors.stream()
                .filter(b -> "LIKE".equals(b.getBehaviorType()) || "PURCHASE".equals(b.getBehaviorType()))
                .map(UserBehavior::getProductId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
            
            // 3. 找到相似用户（喜欢相同商品的用户）
            Map<Long, Double> similarUsers = calculateUserSimilarity(userId);
            
            // 4. 从相似用户喜欢的商品中推荐
            Set<Long> recommendedProductIds = new HashSet<>();
            for (Map.Entry<Long, Double> entry : similarUsers.entrySet()) {
                if (entry.getValue() > 0.3) { // 相似度阈值
                    List<UserBehavior> similarUserBehaviors = userBehaviorMapper.selectList(
                        new LambdaQueryWrapper<UserBehavior>()
                            .eq(UserBehavior::getUserId, entry.getKey())
                            .in(UserBehavior::getBehaviorType, Arrays.asList("LIKE", "PURCHASE"))
                    );
                    
                    similarUserBehaviors.stream()
                        .map(UserBehavior::getProductId)
                        .filter(Objects::nonNull)
                        .filter(pid -> !likedProductIds.contains(pid))
                        .forEach(recommendedProductIds::add);
                }
            }
            
            // 5. 获取推荐商品
            if (recommendedProductIds.isEmpty()) {
                return getHotProducts(limit);
            }
            
            List<Product> products = productMapper.selectBatchIds(new ArrayList<>(recommendedProductIds));
            
            // 过滤已购买商品
            products = filterPurchasedProducts(userId, products);
            
            // 按相似度排序并限制数量
            return products.stream()
                .limit(limit)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            log.error("协同过滤推荐失败", e);
            return getHotProducts(limit);
        }
    }
    
    @Override
    public List<Product> contentBasedRecommend(Long userId, int limit) {
        log.info("内容推荐: userId={}, limit={}", userId, limit);
        
        try {
            // 1. 获取用户喜欢的商品
            List<UserBehavior> userBehaviors = userBehaviorMapper.selectList(
                new LambdaQueryWrapper<UserBehavior>()
                    .eq(UserBehavior::getUserId, userId)
                    .in(UserBehavior::getBehaviorType, Arrays.asList("LIKE", "PURCHASE"))
            );
            
            if (userBehaviors.isEmpty()) {
                return getHotProducts(limit);
            }
            
            // 2. 获取用户喜欢的商品
            Set<Long> likedProductIds = userBehaviors.stream()
                .map(UserBehavior::getProductId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
            
            List<Product> likedProducts = productMapper.selectBatchIds(new ArrayList<>(likedProductIds));
            
            // 3. 基于商品特征（类别、价格区间等）推荐相似商品
            Set<String> preferredCategories = likedProducts.stream()
                .map(Product::getCategory)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
            
            // 计算平均价格
            double avgPrice = likedProducts.stream()
                .mapToDouble(p -> p.getPrice().doubleValue())
                .average()
                .orElse(0.0);
            
            // 4. 查找相似商品
            List<Product> allProducts = productMapper.selectList(null);
            List<Product> recommended = allProducts.stream()
                .filter(p -> !likedProductIds.contains(p.getId()))
                .filter(p -> {
                    // 类别匹配
                    if (preferredCategories.contains(p.getCategory())) {
                        return true;
                    }
                    // 价格区间匹配（±30%）
                    double price = p.getPrice().doubleValue();
                    return price >= avgPrice * 0.7 && price <= avgPrice * 1.3;
                })
                .sorted((p1, p2) -> {
                    // 按相似度排序：类别匹配优先，然后按价格接近度
                    boolean cat1 = preferredCategories.contains(p1.getCategory());
                    boolean cat2 = preferredCategories.contains(p2.getCategory());
                    if (cat1 != cat2) {
                        return cat1 ? -1 : 1;
                    }
                    double diff1 = Math.abs(p1.getPrice().doubleValue() - avgPrice);
                    double diff2 = Math.abs(p2.getPrice().doubleValue() - avgPrice);
                    return Double.compare(diff1, diff2);
                })
                .limit(limit)
                .collect(Collectors.toList());
            
            // 过滤已购买商品
            recommended = filterPurchasedProducts(userId, recommended);
            
            return recommended.isEmpty() ? getHotProducts(limit) : recommended;
            
        } catch (Exception e) {
            log.error("内容推荐失败", e);
            return getHotProducts(limit);
        }
    }
    
    @Override
    public List<Product> hybridRecommend(Long userId, int limit) {
        log.info("混合推荐: userId={}, limit={}", userId, limit);
        
        try {
            // 结合协同过滤和内容推荐
            List<Product> collaborativeProducts = collaborativeFilterRecommend(userId, limit / 2);
            List<Product> contentProducts = contentBasedRecommend(userId, limit / 2);
            
            // 合并并去重
            Map<Long, Product> productMap = new LinkedHashMap<>();
            
            // 协同过滤结果权重更高
            for (Product p : collaborativeProducts) {
                productMap.put(p.getId(), p);
            }
            
            // 添加内容推荐结果
            for (Product p : contentProducts) {
                productMap.putIfAbsent(p.getId(), p);
            }
            
            List<Product> result = new ArrayList<>(productMap.values());
            
            // 过滤已购买商品
            result = filterPurchasedProducts(userId, result);
            
            return result.stream()
                .limit(limit)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            log.error("混合推荐失败", e);
            return getHotProducts(limit);
        }
    }
    
    @Override
    public Map<Long, Double> calculateUserSimilarity(Long userId) {
        log.info("计算用户相似度: userId={}", userId);
        
        Map<Long, Double> similarities = new HashMap<>();
        
        try {
            // 获取当前用户的行为
            List<UserBehavior> userBehaviors = userBehaviorMapper.selectList(
                new LambdaQueryWrapper<UserBehavior>()
                    .eq(UserBehavior::getUserId, userId)
                    .in(UserBehavior::getBehaviorType, Arrays.asList("LIKE", "PURCHASE"))
            );
            
            Set<Long> userProductIds = userBehaviors.stream()
                .map(UserBehavior::getProductId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
            
            if (userProductIds.isEmpty()) {
                return similarities;
            }
            
            // 获取所有其他用户
            List<UserBehavior> allBehaviors = userBehaviorMapper.selectList(
                new LambdaQueryWrapper<UserBehavior>()
                    .ne(UserBehavior::getUserId, userId)
                    .in(UserBehavior::getBehaviorType, Arrays.asList("LIKE", "PURCHASE"))
            );
            
            // 按用户分组
            Map<Long, List<UserBehavior>> userBehaviorMap = allBehaviors.stream()
                .collect(Collectors.groupingBy(UserBehavior::getUserId));
            
            // 计算每个用户的相似度（Jaccard相似度）
            for (Map.Entry<Long, List<UserBehavior>> entry : userBehaviorMap.entrySet()) {
                Long otherUserId = entry.getKey();
                Set<Long> otherProductIds = entry.getValue().stream()
                    .map(UserBehavior::getProductId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
                
                // Jaccard相似度 = 交集 / 并集
                Set<Long> intersection = new HashSet<>(userProductIds);
                intersection.retainAll(otherProductIds);
                
                Set<Long> union = new HashSet<>(userProductIds);
                union.addAll(otherProductIds);
                
                if (!union.isEmpty()) {
                    double similarity = (double) intersection.size() / union.size();
                    similarities.put(otherUserId, similarity);
                }
            }
            
        } catch (Exception e) {
            log.error("计算用户相似度失败", e);
        }
        
        return similarities;
    }
    
    @Override
    public Map<Long, Double> calculateProductSimilarity(Long productId) {
        log.info("计算商品相似度: productId={}", productId);
        
        Map<Long, Double> similarities = new HashMap<>();
        
        try {
            Product product = productMapper.selectById(productId);
            if (product == null) {
                return similarities;
            }
            
            // 获取喜欢该商品的用户
            List<UserBehavior> behaviors = userBehaviorMapper.selectList(
                new LambdaQueryWrapper<UserBehavior>()
                    .eq(UserBehavior::getProductId, productId)
                    .in(UserBehavior::getBehaviorType, Arrays.asList("LIKE", "PURCHASE"))
            );
            
            Set<Long> userIds = behaviors.stream()
                .map(UserBehavior::getUserId)
                .collect(Collectors.toSet());
            
            if (userIds.isEmpty()) {
                return similarities;
            }
            
            // 获取这些用户喜欢的其他商品
            List<UserBehavior> otherBehaviors = userBehaviorMapper.selectList(
                new LambdaQueryWrapper<UserBehavior>()
                    .in(UserBehavior::getUserId, userIds)
                    .ne(UserBehavior::getProductId, productId)
                    .in(UserBehavior::getBehaviorType, Arrays.asList("LIKE", "PURCHASE"))
            );
            
            // 计算商品共现相似度
            Map<Long, Long> cooccurrence = otherBehaviors.stream()
                .collect(Collectors.groupingBy(
                    UserBehavior::getProductId,
                    Collectors.counting()
                ));
            
            long maxCount = cooccurrence.values().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(1L);
            
            // 归一化相似度
            for (Map.Entry<Long, Long> entry : cooccurrence.entrySet()) {
                double similarity = (double) entry.getValue() / maxCount;
                similarities.put(entry.getKey(), similarity);
            }
            
        } catch (Exception e) {
            log.error("计算商品相似度失败", e);
        }
        
        return similarities;
    }
    
    @Override
    public Map<String, Object> getRecommendationExplanation(Long userId, Long productId, String algorithm) {
        log.info("获取推荐解释: userId={}, productId={}, algorithm={}", userId, productId, algorithm);
        
        Map<String, Object> explanation = new HashMap<>();
        
        try {
            Product product = productMapper.selectById(productId);
            if (product == null) {
                return explanation;
            }
            
            explanation.put("productId", productId);
            explanation.put("productName", product.getName());
            explanation.put("algorithm", algorithm);
            
            switch (algorithm.toLowerCase()) {
                case "collaborative":
                    // 协同过滤解释
                    Map<Long, Double> similarUsers = calculateUserSimilarity(userId);
                    long similarUserCount = similarUsers.values().stream()
                        .filter(sim -> sim > 0.3)
                        .count();
                    
                    explanation.put("reason", String.format(
                        "因为与您有相似喜好的%d位用户也喜欢这个商品",
                        similarUserCount
                    ));
                    explanation.put("confidence", Math.min(0.9, 0.5 + similarUserCount * 0.1));
                    break;
                    
                case "content":
                    // 内容推荐解释
                    List<UserBehavior> userBehaviors = userBehaviorMapper.selectList(
                        new LambdaQueryWrapper<UserBehavior>()
                            .eq(UserBehavior::getUserId, userId)
                            .in(UserBehavior::getBehaviorType, Arrays.asList("LIKE", "PURCHASE"))
                    );
                    
                    if (!userBehaviors.isEmpty()) {
                        List<Product> likedProducts = productMapper.selectBatchIds(
                            userBehaviors.stream()
                                .map(UserBehavior::getProductId)
                                .filter(Objects::nonNull)
                                .distinct()
                                .collect(Collectors.toList())
                        );
                        
                        boolean categoryMatch = likedProducts.stream()
                            .anyMatch(p -> Objects.equals(p.getCategory(), product.getCategory()));
                        
                        if (categoryMatch) {
                            explanation.put("reason", String.format(
                                "这个商品与您喜欢的%s类别商品相似",
                                product.getCategory()
                            ));
                            explanation.put("confidence", 0.75);
                        } else {
                            explanation.put("reason", "这个商品的价格区间符合您的偏好");
                            explanation.put("confidence", 0.65);
                        }
                    } else {
                        explanation.put("reason", "这是热门商品，推荐给您");
                        explanation.put("confidence", 0.5);
                    }
                    break;
                    
                case "hybrid":
                    explanation.put("reason", "基于您的行为数据和商品特征的综合推荐");
                    explanation.put("confidence", 0.85);
                    break;
                    
                default:
                    explanation.put("reason", "基于您的偏好推荐");
                    explanation.put("confidence", 0.7);
                    break;
            }
            
        } catch (Exception e) {
            log.error("获取推荐解释失败", e);
            explanation.put("reason", "推荐理由生成失败");
            explanation.put("confidence", 0.5);
        }
        
        return explanation;
    }
    
    /**
     * 获取热门商品
     */
    private List<Product> getHotProducts(int limit) {
        try {
            return productMapper.selectList(
                new LambdaQueryWrapper<Product>()
                    .orderByDesc(Product::getViewCount)
                    .last("LIMIT " + limit)
            );
        } catch (Exception e) {
            log.error("获取热门商品失败", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Product> filterPurchasedProducts(Long userId, List<Product> products) {
        if (userId == null || products == null || products.isEmpty()) {
            return products;
        }
        
        try {
            // 获取用户已购买的商品ID列表
            List<UserBehavior> purchaseBehaviors = userBehaviorMapper.selectList(
                new LambdaQueryWrapper<UserBehavior>()
                    .eq(UserBehavior::getUserId, userId)
                    .eq(UserBehavior::getBehaviorType, "PURCHASE")
            );
            
            Set<Long> purchasedProductIds = purchaseBehaviors.stream()
                .map(UserBehavior::getProductId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
            
            // 过滤已购买的商品
            return products.stream()
                .filter(p -> !purchasedProductIds.contains(p.getId()))
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("过滤已购买商品失败", e);
            return products;
        }
    }
    
    @Override
    public void recordFeedback(Long userId, Long productId, String algorithm, String feedbackType, String reason) {
        try {
            com.shopx.entity.RecommendationFeedback feedback = new com.shopx.entity.RecommendationFeedback();
            feedback.setUserId(userId);
            feedback.setProductId(productId);
            feedback.setAlgorithm(algorithm);
            feedback.setFeedbackType(feedbackType);
            feedback.setReason(reason);
            
            if (recommendationFeedbackMapper != null) {
                recommendationFeedbackMapper.insert(feedback);
            }
            
            log.info("记录推荐反馈: userId={}, productId={}, feedbackType={}", userId, productId, feedbackType);
        } catch (Exception e) {
            log.error("记录推荐反馈失败", e);
        }
    }
    
    @Override
    public Map<String, Object> getUserRecommendationPreferences(Long userId) {
        Map<String, Object> preferences = new HashMap<>();
        preferences.put("filterPurchased", true);
        preferences.put("filterReviewed", false);
        preferences.put("algorithmWeights", Map.of(
            Constants.AlgorithmType.COLLABORATIVE, 0.5,
            Constants.AlgorithmType.CONTENT_BASED, 0.3,
            Constants.AlgorithmType.HYBRID, 0.2
        ));
        
        // 从数据库读取用户偏好
        if (preferenceMapper != null) {
            com.shopx.entity.UserRecommendationPreference pref = preferenceMapper.selectOne(
                new LambdaQueryWrapper<com.shopx.entity.UserRecommendationPreference>()
                    .eq(com.shopx.entity.UserRecommendationPreference::getUserId, userId)
            );
            
            if (pref != null) {
                preferences.put("filterPurchased", pref.getFilterPurchased());
                preferences.put("filterReviewed", pref.getFilterReviewed());
                // 解析算法权重JSON
                if (pref.getAlgorithmWeights() != null) {
                    // 简化实现，实际应该解析JSON
                    preferences.put("algorithmWeights", pref.getAlgorithmWeights());
                }
            }
        }
        
        return preferences;
    }
    
    @Override
    public void updateUserRecommendationPreferences(Long userId, Map<String, Object> preferences) {
        try {
            if (preferenceMapper == null) {
                return;
            }
            
            // 查找或创建用户偏好记录
            com.shopx.entity.UserRecommendationPreference pref = preferenceMapper.selectOne(
                new LambdaQueryWrapper<com.shopx.entity.UserRecommendationPreference>()
                    .eq(com.shopx.entity.UserRecommendationPreference::getUserId, userId)
            );
            
            if (pref == null) {
                pref = new com.shopx.entity.UserRecommendationPreference();
                pref.setUserId(userId);
            }
            
            // 更新偏好设置
            if (preferences.containsKey("filterPurchased")) {
                pref.setFilterPurchased((Boolean) preferences.get("filterPurchased"));
            }
            if (preferences.containsKey("filterReviewed")) {
                pref.setFilterReviewed((Boolean) preferences.get("filterReviewed"));
            }
            if (preferences.containsKey("algorithmWeights")) {
                // 简化实现，实际应该转换为JSON字符串
                pref.setAlgorithmWeights(preferences.get("algorithmWeights").toString());
            }
            
            if (pref.getId() == null) {
                preferenceMapper.insert(pref);
            } else {
                preferenceMapper.updateById(pref);
            }
            
            log.info("更新用户推荐偏好: userId={}, preferences={}", userId, preferences);
        } catch (Exception e) {
            log.error("更新用户推荐偏好失败", e);
        }
    }
}

