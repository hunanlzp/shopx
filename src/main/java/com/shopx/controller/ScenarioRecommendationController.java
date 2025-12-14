package com.shopx.controller;

import com.shopx.entity.Product;
import com.shopx.service.ScenarioRecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 情境化推荐控制器
 * 提供基于场景、生活方式和预测的智能推荐服务
 */
@Slf4j
@RestController
@RequestMapping("/recommendation")
@Tag(name = "智能推荐", description = "情境化推荐API，包括场景推荐、生活方式推荐和预测推荐")
public class ScenarioRecommendationController {
    
    @Autowired
    private ScenarioRecommendationService recommendationService;
    
    /**
     * 基于场景推荐商品
     * 根据用户当前场景（如运动健身、约会、工作等）推荐相关商品
     */
    @Operation(summary = "场景推荐", description = "基于用户当前场景推荐相关商品")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "推荐成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    @GetMapping("/scenario")
    public ResponseEntity<Map<String, Object>> recommendByScenario(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "场景类型", required = true, example = "运动健身") @RequestParam String scenario) {
        
        log.info("收到场景推荐请求: userId={}, scenario={}", userId, scenario);
        
        List<Product> products = recommendationService.recommendByScenario(userId, scenario);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "推荐成功");
        response.put("data", products);
        response.put("total", products.size());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 基于生活方式推荐商品
     */
    @GetMapping("/lifestyle")
    public ResponseEntity<Map<String, Object>> recommendByLifestyle(
            @RequestParam Long userId,
            @RequestParam String lifestyle) {
        
        log.info("收到生活方式推荐请求: userId={}, lifestyle={}", userId, lifestyle);
        
        List<Product> products = recommendationService.recommendByLifestyle(userId, lifestyle);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "推荐成功");
        response.put("data", products);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 预测性推荐
     */
    @GetMapping("/predict")
    public ResponseEntity<Map<String, Object>> predictRecommendation(
            @RequestParam Long userId) {
        
        log.info("收到预测推荐请求: userId={}", userId);
        
        List<Product> products = recommendationService.predictRecommendation(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "预测推荐成功");
        response.put("data", products);
        
        return ResponseEntity.ok(response);
    }
}

