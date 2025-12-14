package com.shopx.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.shopx.annotation.ApiVersion;
import com.shopx.constant.Constants;
import com.shopx.entity.ApiResponse;
import com.shopx.entity.Product;
import com.shopx.service.ProductService;
import com.shopx.util.ResponseUtil;
import com.shopx.util.SaTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 推荐系统控制器
 * 提供智能推荐相关的API接口
 */
@Slf4j
@RestController
@RequestMapping("/recommendation")
@ApiVersion("v1")
@Tag(name = "推荐系统", description = "智能推荐相关API")
public class RecommendationController {

    @Autowired
    private ProductService productService;
    
    @Autowired(required = false)
    private com.shopx.service.RecommendationService recommendationService;
    
    @Autowired(required = false)
    private com.shopx.service.ABTestService abTestService;

    // 模拟推荐算法配置
    private final Map<String, Map<String, Object>> algorithmConfigs = new HashMap<>();

    public RecommendationController() {
        // 初始化推荐算法配置
        initAlgorithmConfigs();
    }

    /**
     * 获取场景推荐
     */
    @Operation(summary = "获取场景推荐", description = "根据使用场景推荐商品")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "推荐成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    @SaCheckLogin
    @GetMapping("/scenario")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getScenarioRecommendation(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "场景类型", required = true) @RequestParam String scenario) {

        try {
            List<Product> recommendedProducts;
            String algorithm = "scenario_based";
            
            // A/B测试：为用户分配算法
            if (abTestService != null) {
                String assignedAlgorithm = abTestService.assignAlgorithm(userId, "recommendation_test");
                algorithm = assignedAlgorithm;
                
                // 根据分配的算法获取推荐
                if (recommendationService != null) {
                    switch (assignedAlgorithm) {
                        case "collaborative":
                            recommendedProducts = recommendationService.collaborativeFilterRecommend(userId, 10);
                            break;
                        case "content":
                            recommendedProducts = recommendationService.contentBasedRecommend(userId, 10);
                            break;
                        case "hybrid":
                            recommendedProducts = recommendationService.hybridRecommend(userId, 10);
                            break;
                        default:
                            recommendedProducts = getProductsByScenario(scenario);
                            break;
                    }
                } else {
                    recommendedProducts = getProductsByScenario(scenario);
                }
                
                // 记录A/B测试结果
                ABTest activeTest = abTestService.getActiveTest("recommendation_test");
                if (activeTest != null && !recommendedProducts.isEmpty()) {
                    abTestService.recordResult(activeTest.getId(), userId, assignedAlgorithm, 
                            recommendedProducts.get(0).getId(), "VIEW", null);
                }
            } else {
                // 如果没有A/B测试，使用默认逻辑
                recommendedProducts = getProductsByScenario(scenario);
                
                if (recommendationService != null) {
                    List<Product> hybridProducts = recommendationService.hybridRecommend(userId, 10);
                    if (!hybridProducts.isEmpty()) {
                        recommendedProducts = hybridProducts;
                        algorithm = "hybrid";
                    }
                }
            }
            
            // 为每个商品生成推荐解释
            List<com.shopx.dto.ProductWithExplanationDTO> productsWithExplanation = new ArrayList<>();
            for (Product product : recommendedProducts) {
                com.shopx.dto.ProductWithExplanationDTO productData = new com.shopx.dto.ProductWithExplanationDTO();
                productData.setProduct(product);
                
                if (recommendationService != null) {
                    Map<String, Object> explanationMap = recommendationService.getRecommendationExplanation(
                        userId, product.getId(), "hybrid"
                    );
                    com.shopx.dto.RecommendationExplanationDTO explanation = new com.shopx.dto.RecommendationExplanationDTO();
                    explanation.setReason((String) explanationMap.get("reason"));
                    explanation.setConfidence((Double) explanationMap.get("confidence"));
                    explanation.setAlgorithm("hybrid");
                    productData.setExplanation(explanation);
                } else {
                    com.shopx.dto.RecommendationExplanationDTO explanation = new com.shopx.dto.RecommendationExplanationDTO();
                    explanation.setReason("基于场景推荐");
                    explanation.setConfidence(0.7);
                    explanation.setAlgorithm("hybrid");
                    productData.setExplanation(explanation);
                }
                
                productsWithExplanation.add(productData);
            }
            
            // 计算推荐置信度
            double confidence = calculateConfidence(scenario, userId);
            
            com.shopx.dto.RecommendationResponseDTO result = new com.shopx.dto.RecommendationResponseDTO();
            result.setScenario(scenario);
            result.setRecommendedProducts(recommendedProducts);
            result.setProductsWithExplanation(productsWithExplanation);
            result.setConfidence(confidence);
            result.setAlgorithm(algorithm);
            result.setTimestamp(LocalDateTime.now());

            // 记录推荐行为
            log.info("为用户 {} 生成场景推荐: {}", userId, scenario);

            return ResponseUtil.success("场景推荐生成成功", result);
        } catch (Exception e) {
            log.error("生成场景推荐失败", e);
            return ResponseUtil.error("生成场景推荐失败，请稍后重试");
        }
    }

    /**
     * 获取生活方式推荐
     */
    @Operation(summary = "获取生活方式推荐", description = "根据生活方式推荐商品")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "推荐成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    @SaCheckLogin
    @GetMapping("/lifestyle")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getLifestyleRecommendation(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "生活方式", required = true) @RequestParam String lifestyle) {

        try {
            // 根据生活方式获取推荐商品
            List<Product> recommendedProducts = getProductsByLifestyle(lifestyle);
            
            // 如果推荐服务可用，使用真实算法
            if (recommendationService != null) {
                List<Product> contentProducts = recommendationService.contentBasedRecommend(userId, 10);
                if (!contentProducts.isEmpty()) {
                    recommendedProducts = contentProducts;
                }
            }
            
            // 为每个商品生成推荐解释
            List<com.shopx.dto.ProductWithExplanationDTO> productsWithExplanation = new ArrayList<>();
            for (Product product : recommendedProducts) {
                com.shopx.dto.ProductWithExplanationDTO productData = new com.shopx.dto.ProductWithExplanationDTO();
                productData.setProduct(product);
                
                if (recommendationService != null) {
                    Map<String, Object> explanationMap = recommendationService.getRecommendationExplanation(
                        userId, product.getId(), "content"
                    );
                    com.shopx.dto.RecommendationExplanationDTO explanation = new com.shopx.dto.RecommendationExplanationDTO();
                    explanation.setReason((String) explanationMap.get("reason"));
                    explanation.setConfidence((Double) explanationMap.get("confidence"));
                    explanation.setAlgorithm("content");
                    productData.setExplanation(explanation);
                } else {
                    com.shopx.dto.RecommendationExplanationDTO explanation = new com.shopx.dto.RecommendationExplanationDTO();
                    explanation.setReason("基于生活方式推荐");
                    explanation.setConfidence(0.7);
                    explanation.setAlgorithm("content");
                    productData.setExplanation(explanation);
                }
                
                productsWithExplanation.add(productData);
            }
            
            // 计算推荐置信度
            double confidence = calculateConfidence(lifestyle, userId);
            
            com.shopx.dto.RecommendationResponseDTO result = new com.shopx.dto.RecommendationResponseDTO();
            result.setLifestyle(lifestyle);
            result.setRecommendedProducts(recommendedProducts);
            result.setProductsWithExplanation(productsWithExplanation);
            result.setConfidence(confidence);
            result.setAlgorithm(recommendationService != null ? "content" : "lifestyle_based");
            result.setTimestamp(LocalDateTime.now());

            // 记录推荐行为
            log.info("为用户 {} 生成生活方式推荐: {}", userId, lifestyle);

            return ResponseUtil.success("生活方式推荐生成成功", result);
        } catch (Exception e) {
            log.error("生成生活方式推荐失败", e);
            return ResponseUtil.error("生成生活方式推荐失败，请稍后重试");
        }
    }

    /**
     * 获取AI预测推荐
     */
    @Operation(summary = "获取AI预测推荐", description = "基于AI算法预测用户可能感兴趣的商品")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "推荐成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    @SaCheckLogin
    @GetMapping("/predict")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPredictRecommendation(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId) {

        try {
            // 基于用户历史行为进行AI预测
            List<Product> predictedProducts = getPredictedProducts(userId);
            
            // 如果推荐服务可用，使用协同过滤算法
            if (recommendationService != null) {
                List<Product> collaborativeProducts = recommendationService.collaborativeFilterRecommend(userId, 10);
                if (!collaborativeProducts.isEmpty()) {
                    predictedProducts = collaborativeProducts;
                }
            }
            
            // 为每个商品生成推荐解释
            List<com.shopx.dto.ProductWithExplanationDTO> productsWithExplanation = new ArrayList<>();
            for (Product product : predictedProducts) {
                com.shopx.dto.ProductWithExplanationDTO productData = new com.shopx.dto.ProductWithExplanationDTO();
                productData.setProduct(product);
                
                if (recommendationService != null) {
                    Map<String, Object> explanationMap = recommendationService.getRecommendationExplanation(
                        userId, product.getId(), "collaborative"
                    );
                    com.shopx.dto.RecommendationExplanationDTO explanation = new com.shopx.dto.RecommendationExplanationDTO();
                    explanation.setReason((String) explanationMap.get("reason"));
                    explanation.setConfidence((Double) explanationMap.get("confidence"));
                    explanation.setAlgorithm("collaborative");
                    productData.setExplanation(explanation);
                } else {
                    com.shopx.dto.RecommendationExplanationDTO explanation = new com.shopx.dto.RecommendationExplanationDTO();
                    explanation.setReason("基于AI预测推荐");
                    explanation.setConfidence(0.8);
                    explanation.setAlgorithm("collaborative");
                    productData.setExplanation(explanation);
                }
                
                productsWithExplanation.add(productData);
            }
            
            // 计算预测置信度
            double confidence = calculatePredictionConfidence(userId);
            
            com.shopx.dto.RecommendationResponseDTO result = new com.shopx.dto.RecommendationResponseDTO();
            result.setRecommendedProducts(predictedProducts);
            result.setProductsWithExplanation(productsWithExplanation);
            result.setConfidence(confidence);
            result.setAlgorithm(recommendationService != null ? "collaborative" : "ai_prediction");
            result.setAlgorithm(recommendationService != null ? "collaborative" : "deep_learning_v2");
            result.setTimestamp(LocalDateTime.now());

            // 记录预测行为
            log.info("为用户 {} 生成AI预测推荐", userId);

            return ResponseUtil.success("AI预测推荐生成成功", result);
        } catch (Exception e) {
            log.error("生成AI预测推荐失败", e);
            return ResponseUtil.error("生成AI预测推荐失败，请稍后重试");
        }
    }

    /**
     * 获取推荐算法列表
     */
    @Operation(summary = "获取推荐算法列表", description = "获取可用的推荐算法及其配置")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @GetMapping("/algorithms")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getRecommendationAlgorithms() {

        try {
            List<Map<String, Object>> algorithms = new ArrayList<>();
            
            for (Map.Entry<String, Map<String, Object>> entry : algorithmConfigs.entrySet()) {
                Map<String, Object> algorithm = new HashMap<>(entry.getValue());
                algorithm.put("key", entry.getKey());
                algorithms.add(algorithm);
            }

            return ResponseUtil.success("获取推荐算法列表成功", algorithms);
        } catch (Exception e) {
            log.error("获取推荐算法列表失败", e);
            return ResponseUtil.error("获取推荐算法列表失败，请稍后重试");
        }
    }

    /**
     * 获取用户推荐历史
     */
    @Operation(summary = "获取用户推荐历史", description = "获取用户的推荐历史记录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @SaCheckLogin
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getUserRecommendationHistory(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "页码", required = false) @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小", required = false) @RequestParam(defaultValue = "20") int size) {

        try {
            // 模拟推荐历史数据
            List<Map<String, Object>> history = generateRecommendationHistory(userId, page, size);

            return ResponseUtil.success("获取推荐历史成功", history);
        } catch (Exception e) {
            log.error("获取推荐历史失败", e);
            return ResponseUtil.error("获取推荐历史失败，请稍后重试");
        }
    }

    /**
     * 获取推荐统计
     */
    @Operation(summary = "获取推荐统计", description = "获取用户的推荐效果统计")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @SaCheckLogin
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRecommendationStats(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId) {

        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalRecommendations", 156);
            stats.put("clickThroughRate", 23.5);
            stats.put("conversionRate", 8.2);
            stats.put("averageRating", 4.3);
            stats.put("favoriteCategories", Arrays.asList("电子产品", "服装", "家居用品"));
            stats.put("monthlyTrend", generateMonthlyTrend());

            return ResponseUtil.success("获取推荐统计成功", stats);
        } catch (Exception e) {
            log.error("获取推荐统计失败", e);
            return ResponseUtil.error("获取推荐统计失败，请稍后重试");
        }
    }

    /**
     * 反馈推荐结果
     */
    @Operation(summary = "反馈推荐结果", description = "用户对推荐结果进行反馈")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "反馈成功")
    })
    @SaCheckLogin
    @PostMapping("/feedback")
    public ResponseEntity<ApiResponse<Void>> feedbackRecommendation(
            @Parameter(description = "商品ID", required = true) @RequestParam Long productId,
            @Parameter(description = "推荐算法", required = true) @RequestParam String algorithm,
            @Parameter(description = "反馈类型", required = true) @RequestParam String feedbackType,
            @Parameter(description = "反馈原因", required = false) @RequestParam(required = false) String reason) {

        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            
            // 记录用户反馈
            if (recommendationService != null) {
                recommendationService.recordFeedback(userId, productId, algorithm, feedbackType, reason);
            }
            
            log.info("用户 {} 对推荐商品 {} 进行反馈: {}", userId, productId, feedbackType);

            return ResponseUtil.success("反馈提交成功", null);
        } catch (Exception e) {
            log.error("提交反馈失败", e);
            return ResponseUtil.error("提交反馈失败");
        }
    }
    
    /**
     * 获取用户推荐偏好
     */
    @Operation(summary = "获取推荐偏好", description = "获取当前用户的推荐偏好设置")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @SaCheckLogin
    @GetMapping("/preferences")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserRecommendationPreferences() {
        
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            Map<String, Object> preferences = recommendationService != null ? 
                recommendationService.getUserRecommendationPreferences(userId) : new HashMap<>();
            return ResponseUtil.success("查询成功", preferences);
        } catch (Exception e) {
            log.error("获取推荐偏好失败", e);
            return ResponseUtil.error("获取推荐偏好失败，请稍后重试");
        }
    }
    
    /**
     * 更新用户推荐偏好
     */
    @Operation(summary = "更新推荐偏好", description = "更新当前用户的推荐偏好设置")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功")
    })
    @SaCheckLogin
    @PutMapping("/preferences")
    public ResponseEntity<ApiResponse<Void>> updateUserRecommendationPreferences(
            @Parameter(description = "偏好设置", required = true) @RequestBody Map<String, Object> preferences) {
        
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            if (recommendationService != null) {
                recommendationService.updateUserRecommendationPreferences(userId, preferences);
            }
            return ResponseUtil.success("更新成功", null);
        } catch (Exception e) {
            log.error("更新推荐偏好失败", e);
            return ResponseUtil.error("更新推荐偏好失败，请稍后重试");
        }
    }

    /**
     * 根据场景获取商品
     */
    private List<Product> getProductsByScenario(String scenario) {
        try {
            switch (scenario.toLowerCase()) {
                case "工作":
                    return productService.getProductsByCategory("办公用品", 1, 10).getData();
                case "运动":
                    return productService.getProductsByCategory("运动健身", 1, 10).getData();
                case "休闲":
                    return productService.getProductsByCategory("休闲娱乐", 1, 10).getData();
                case "学习":
                    return productService.getProductsByCategory("学习用品", 1, 10).getData();
                case "旅行":
                    return productService.getProductsByCategory("旅行用品", 1, 10).getData();
                case "居家":
                    return productService.getProductsByCategory("家居用品", 1, 10).getData();
                default:
                    return productService.getHotProducts(10);
            }
        } catch (Exception e) {
            log.error("根据场景获取商品失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据生活方式获取商品
     */
    private List<Product> getProductsByLifestyle(String lifestyle) {
        try {
            switch (lifestyle.toLowerCase()) {
                case "简约":
                    return productService.searchProducts("简约", 1, 10).getData();
                case "奢华":
                    return productService.searchProducts("奢华", 1, 10).getData();
                case "环保":
                    return productService.searchProducts("环保", 1, 10).getData();
                case "时尚":
                    return productService.searchProducts("时尚", 1, 10).getData();
                case "实用":
                    return productService.searchProducts("实用", 1, 10).getData();
                default:
                    return productService.getHotProducts(10);
            }
        } catch (Exception e) {
            log.error("根据生活方式获取商品失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取预测商品
     */
    private List<Product> getPredictedProducts(Long userId) {
        try {
            // 模拟AI预测逻辑
            // 这里可以调用机器学习模型进行预测
            return productService.getHotProducts(10);
        } catch (Exception e) {
            log.error("获取预测商品失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 计算推荐置信度
     */
    private double calculateConfidence(String context, Long userId) {
        // 模拟置信度计算
        return 0.7 + Math.random() * 0.3;
    }

    /**
     * 计算预测置信度
     */
    private double calculatePredictionConfidence(Long userId) {
        // 模拟AI预测置信度
        return 0.8 + Math.random() * 0.2;
    }

    /**
     * 生成推荐历史
     */
    private List<Map<String, Object>> generateRecommendationHistory(Long userId, int page, int size) {
        List<Map<String, Object>> history = new ArrayList<>();
        
        // 模拟历史数据
        Map<String, Object> record1 = new HashMap<>();
        record1.put("id", "rec_1");
        record1.put("type", "scenario");
        record1.put("context", "工作");
        record1.put("products", Arrays.asList("笔记本电脑", "办公椅", "显示器"));
        record1.put("confidence", 0.85);
        record1.put("timestamp", LocalDateTime.now().minusDays(1));
        history.add(record1);
        
        Map<String, Object> record2 = new HashMap<>();
        record2.put("id", "rec_2");
        record2.put("type", "lifestyle");
        record2.put("context", "简约");
        record2.put("products", Arrays.asList("简约台灯", "收纳盒", "简约书架"));
        record2.put("confidence", 0.78);
        record2.put("timestamp", LocalDateTime.now().minusDays(2));
        history.add(record2);
        
        return history;
    }

    /**
     * 生成月度趋势
     */
    private List<Map<String, Object>> generateMonthlyTrend() {
        List<Map<String, Object>> trend = new ArrayList<>();
        
        for (int i = 11; i >= 0; i--) {
            Map<String, Object> month = new HashMap<>();
            month.put("month", LocalDateTime.now().minusMonths(i).getMonth().toString());
            month.put("recommendations", 10 + (int)(Math.random() * 20));
            month.put("clicks", 2 + (int)(Math.random() * 8));
            month.put("conversions", (int)(Math.random() * 3));
            trend.add(month);
        }
        
        return trend;
    }

    /**
     * 初始化推荐算法配置
     */
    private void initAlgorithmConfigs() {
        // 协同过滤算法
        Map<String, Object> collaborative = new HashMap<>();
        collaborative.put("name", "协同过滤");
        collaborative.put("description", "基于用户行为相似性推荐");
        collaborative.put("accuracy", 85);
        collaborative.put("speed", 120);
        collaborative.put("type", "collaborative");
        algorithmConfigs.put("collaborative", collaborative);

        // 内容推荐算法
        Map<String, Object> content = new HashMap<>();
        content.put("name", "内容推荐");
        content.put("description", "基于商品特征相似性推荐");
        content.put("accuracy", 78);
        content.put("speed", 80);
        content.put("type", "content");
        algorithmConfigs.put("content", content);

        // 混合推荐算法
        Map<String, Object> hybrid = new HashMap<>();
        hybrid.put("name", "混合推荐");
        hybrid.put("description", "结合多种算法的智能推荐");
        hybrid.put("accuracy", 92);
        hybrid.put("speed", 200);
        hybrid.put("type", "hybrid");
        algorithmConfigs.put("hybrid", hybrid);

        // 深度学习算法
        Map<String, Object> deep = new HashMap<>();
        deep.put("name", "深度学习");
        deep.put("description", "基于神经网络的深度推荐");
        deep.put("accuracy", 88);
        deep.put("speed", 300);
        deep.put("type", "deep_learning");
        algorithmConfigs.put("deep", deep);
    }
}
