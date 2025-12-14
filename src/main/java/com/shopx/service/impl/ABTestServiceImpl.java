package com.shopx.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shopx.constant.Constants;
import com.shopx.entity.ABTest;
import com.shopx.entity.ABTestResult;
import com.shopx.enums.AlgorithmTypeEnum;
import com.shopx.mapper.ABTestMapper;
import com.shopx.mapper.ABTestResultMapper;
import com.shopx.service.ABTestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * A/B测试服务实现类
 */
@Slf4j
@Service
public class ABTestServiceImpl extends ServiceImpl<ABTestMapper, ABTest> implements ABTestService {
    
    @Autowired
    private ABTestResultMapper resultMapper;
    
    @Override
    public ABTest createABTest(ABTest abTest) {
        abTest.setStatus(Constants.ABTestStatus.ACTIVE);
        abTest.setStartDate(LocalDateTime.now());
        save(abTest);
        log.info("创建A/B测试: {}", abTest.getTestName());
        return abTest;
    }
    
    @Override
    public ABTest getActiveTest(String testName) {
        return getOne(new LambdaQueryWrapper<ABTest>()
                .eq(ABTest::getTestName, testName)
                .eq(ABTest::getStatus, Constants.ABTestStatus.ACTIVE)
                .last("LIMIT 1"));
    }
    
    @Override
    public String assignAlgorithm(Long userId, String testName) {
        ABTest test = getActiveTest(testName);
        if (test == null) {
            return AlgorithmTypeEnum.getDefault().getCode(); // 默认算法
        }
        
        // 基于用户ID的哈希值分配算法，确保同一用户总是分配到同一算法
        long hash = (userId + test.getId()) % 100;
        double split = test.getTrafficSplit() != null ? test.getTrafficSplit() : 50.0;
        
        return hash < split ? test.getAlgorithmA() : test.getAlgorithmB();
    }
    
    @Override
    public void recordResult(Long testId, Long userId, String algorithm, Long productId, String actionType, Double actionValue) {
        ABTestResult result = new ABTestResult();
        result.setTestId(testId);
        result.setUserId(userId);
        result.setAlgorithm(algorithm);
        result.setProductId(productId);
        result.setActionType(actionType);
        if (actionValue != null) {
            result.setActionValue(BigDecimal.valueOf(actionValue));
        }
        result.setCreateTime(LocalDateTime.now());
        
        resultMapper.insert(result);
        log.debug("记录A/B测试结果: testId={}, userId={}, algorithm={}, action={}", 
                testId, userId, algorithm, actionType);
    }
    
    @Override
    public Map<String, Object> getTestStats(Long testId) {
        Map<String, Object> stats = new HashMap<>();
        
        ABTest test = getById(testId);
        if (test == null) {
            return stats;
        }
        
        // 获取算法A的结果
        List<ABTestResult> resultsA = resultMapper.selectList(
                new LambdaQueryWrapper<ABTestResult>()
                        .eq(ABTestResult::getTestId, testId)
                        .eq(ABTestResult::getAlgorithm, test.getAlgorithmA())
        );
        
        // 获取算法B的结果
        List<ABTestResult> resultsB = resultMapper.selectList(
                new LambdaQueryWrapper<ABTestResult>()
                        .eq(ABTestResult::getTestId, testId)
                        .eq(ABTestResult::getAlgorithm, test.getAlgorithmB())
        );
        
        // 计算指标
        Map<String, Object> metricsA = calculateMetrics(resultsA);
        Map<String, Object> metricsB = calculateMetrics(resultsB);
        
        stats.put("testId", testId);
        stats.put("testName", test.getTestName());
        stats.put("algorithmA", test.getAlgorithmA());
        stats.put("algorithmB", test.getAlgorithmB());
        stats.put("metricsA", metricsA);
        stats.put("metricsB", metricsB);
        
        // 比较结果
        double ctrA = (Double) metricsA.getOrDefault("ctr", 0.0);
        double ctrB = (Double) metricsB.getOrDefault("ctr", 0.0);
        double improvement = ctrB > 0 ? ((ctrA - ctrB) / ctrB) * 100 : 0;
        
        stats.put("improvement", improvement);
        stats.put("winner", ctrA > ctrB ? test.getAlgorithmA() : test.getAlgorithmB());
        
        return stats;
    }
    
    @Override
    public List<ABTest> getAllTests() {
        return list();
    }
    
    @Override
    public void updateTestStatus(Long testId, String status) {
        ABTest test = getById(testId);
        if (test != null) {
            test.setStatus(status);
            if ("COMPLETED".equals(status)) {
                test.setEndDate(LocalDateTime.now());
            }
            updateById(test);
        }
    }
    
    /**
     * 计算指标
     */
    private Map<String, Object> calculateMetrics(List<ABTestResult> results) {
        Map<String, Object> metrics = new HashMap<>();
        
        long totalActions = results.size();
        long views = results.stream().filter(r -> "VIEW".equals(r.getActionType())).count();
        long clicks = results.stream().filter(r -> "CLICK".equals(r.getActionType())).count();
        long purchases = results.stream().filter(r -> "PURCHASE".equals(r.getActionType())).count();
        
        double totalValue = results.stream()
                .filter(r -> r.getActionValue() != null)
                .mapToDouble(r -> r.getActionValue().doubleValue())
                .sum();
        
        metrics.put("totalActions", totalActions);
        metrics.put("views", views);
        metrics.put("clicks", clicks);
        metrics.put("purchases", purchases);
        metrics.put("ctr", views > 0 ? (double) clicks / views * 100 : 0.0);
        metrics.put("conversionRate", views > 0 ? (double) purchases / views * 100 : 0.0);
        metrics.put("totalValue", totalValue);
        metrics.put("avgValue", purchases > 0 ? totalValue / purchases : 0.0);
        
        return metrics;
    }
}

