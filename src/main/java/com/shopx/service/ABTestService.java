package com.shopx.service;

import com.shopx.entity.ABTest;
import com.shopx.entity.ABTestResult;

import java.util.List;
import java.util.Map;

/**
 * A/B测试服务接口
 */
public interface ABTestService {
    
    /**
     * 创建A/B测试
     */
    ABTest createABTest(ABTest abTest);
    
    /**
     * 获取活跃的A/B测试
     */
    ABTest getActiveTest(String testName);
    
    /**
     * 为用户分配算法（A或B）
     */
    String assignAlgorithm(Long userId, String testName);
    
    /**
     * 记录A/B测试结果
     */
    void recordResult(Long testId, Long userId, String algorithm, Long productId, String actionType, Double actionValue);
    
    /**
     * 获取A/B测试统计
     */
    Map<String, Object> getTestStats(Long testId);
    
    /**
     * 获取所有A/B测试
     */
    List<ABTest> getAllTests();
    
    /**
     * 更新测试状态
     */
    void updateTestStatus(Long testId, String status);
}

