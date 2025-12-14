package com.shopx.service.impl;

import com.shopx.entity.Product;
import com.shopx.entity.User;
import com.shopx.entity.Recommendation;
import com.shopx.mapper.UserMapper;
import com.shopx.mapper.ProductMapper;
import com.shopx.mapper.RecommendationMapper;
import com.shopx.service.ScenarioRecommendationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 情境化推荐服务实现
 */
@Slf4j
@Service
public class ScenarioRecommendationServiceImpl implements ScenarioRecommendationService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private RecommendationMapper recommendationMapper;
    
    @Override
    public List<Product> recommendByScenario(Long userId, String scenario) {
        log.info("为用户 {} 推荐场景: {}", userId, scenario);
        
        User user = userMapper.selectById(userId);
        if (user == null) {
            return new ArrayList<>();
        }
        
        // 根据场景筛选商品
        List<Product> products = productMapper.selectList(null).stream()
            .filter(product -> product.getSuitableScenarios() != null)
            .filter(product -> product.getSuitableScenarios().contains(scenario))
            .sorted((p1, p2) -> Integer.compare(p2.getLikeCount(), p1.getLikeCount()))
            .limit(20)
            .collect(Collectors.toList());
        
        // 保存推荐记录
        for (Product product : products) {
            Recommendation recommendation = new Recommendation();
            recommendation.setUserId(userId);
            recommendation.setProductId(product.getId());
            recommendation.setScenario(scenario);
            recommendation.setReason("场景匹配：" + scenario);
            recommendation.setScore(BigDecimal.valueOf(0.85));
            recommendation.setModelVersion("v1.0");
            recommendation.setCreateTime(LocalDateTime.now());
            recommendationMapper.insert(recommendation);
        }
        
        return products;
    }
    
    @Override
    public List<Product> recommendByLifestyle(Long userId, String lifestyle) {
        log.info("为用户 {} 推荐生活方式: {}", userId, lifestyle);
        
        User user = userMapper.selectById(userId);
        if (user == null) {
            return new ArrayList<>();
        }
        
        List<Product> products = productMapper.selectList(null).stream()
            .filter(product -> product.getLifestyleTags() != null)
            .filter(product -> product.getLifestyleTags().contains(lifestyle))
            .sorted((p1, p2) -> Integer.compare(p2.getViewCount(), p1.getViewCount()))
            .limit(20)
            .collect(Collectors.toList());
        
        return products;
    }
    
    @Override
    public List<Product> predictRecommendation(Long userId) {
        log.info("为用户 {} 进行预测性推荐", userId);
        
        User user = userMapper.selectById(userId);
        if (user == null) {
            return new ArrayList<>();
        }
        
        // 根据季节和用户历史行为预测
        String currentSeason = getCurrentSeason();
        List<Product> products = productMapper.selectList(null).stream()
            .filter(product -> product.getSeasonality() != null)
            .filter(product -> product.getSeasonality().equals(currentSeason))
            .sorted((p1, p2) -> BigDecimal.valueOf(p2.getPrice()).compareTo(p1.getPrice()))
            .limit(10)
            .collect(Collectors.toList());
        
        return products;
    }
    
    private String getCurrentSeason() {
        int month = LocalDateTime.now().getMonthValue();
        if (month >= 3 && month <= 5) return "SPRING";
        if (month >= 6 && month <= 8) return "SUMMER";
        if (month >= 9 && month <= 11) return "AUTUMN";
        return "WINTER";
    }
}

