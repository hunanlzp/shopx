package com.shopx.service;

import com.shopx.entity.Product;
import java.util.List;

/**
 * 情境化推荐服务 - 基于场景的智能推荐
 */
public interface ScenarioRecommendationService {
    
    /**
     * 基于用户当前场景推荐商品
     * @param userId 用户ID
     * @param scenario 场景类型
     * @return 推荐商品列表
     */
    List<Product> recommendByScenario(Long userId, String scenario);
    
    /**
     * 根据生活方式推荐商品
     * @param userId 用户ID
     * @param lifestyle 生活方式
     * @return 推荐商品列表
     */
    List<Product> recommendByLifestyle(Long userId, String lifestyle);
    
    /**
     * 季节性和场合预测推荐
     * @param userId 用户ID
     * @return 预测性推荐商品
     */
    List<Product> predictRecommendation(Long userId);
}

