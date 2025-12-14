package com.shopx.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.shopx.annotation.ApiVersion;
import com.shopx.constant.Constants;
import com.shopx.entity.ApiResponse;
import com.shopx.entity.Product;
import com.shopx.entity.AIChatHistory;
import com.shopx.entity.AIResponse;
import com.shopx.entity.AIStatus;
import com.shopx.entity.AISuggestion;
import com.shopx.entity.AIUserPreferences;
import com.shopx.service.AIAssistantService;
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
 * AI助手控制器
 * 提供AI购物助手相关的API接口
 */
@Slf4j
@RestController
@RequestMapping("/ai-assistant")
@ApiVersion("v1")
@Tag(name = "AI助手", description = "AI购物助手相关API")
public class AIAssistantController {

    @Autowired
    private ProductService productService;
    
    @Autowired
    private AIAssistantService aiAssistantService;

    /**
     * AI聊天对话
     */
    @Operation(summary = "AI聊天对话", description = "与AI助手进行对话交流")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "对话成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    @SaCheckLogin
    @PostMapping("/chat")
    public ResponseEntity<ApiResponse<Map<String, Object>>> chatWithAI(
            @Parameter(description = "用户消息", required = true) @RequestParam String message,
            @Parameter(description = "会话ID", required = false) @RequestParam(required = false) String sessionId) {

        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            
            AIResponse aiResponse = aiAssistantService.processMessage(userId, message, sessionId);
            return ResponseUtil.success("AI回复成功", aiResponse);
        } catch (Exception e) {
            log.error("AI对话失败", e);
            return ResponseUtil.error("AI对话失败，请稍后重试");
        }
    }

    /**
     * 获取AI建议
     */
    @Operation(summary = "获取AI建议", description = "获取AI助手的商品建议")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @SaCheckLogin
    @GetMapping("/suggestions")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAISuggestions(
            @Parameter(description = "建议类型", required = false) @RequestParam(required = false) String type) {

        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            
            List<AISuggestion> suggestions = aiAssistantService.getSuggestions(userId, type);
            return ResponseUtil.success("获取AI建议成功", suggestions);
        } catch (Exception e) {
            log.error("获取AI建议失败", e);
            return ResponseUtil.error("获取AI建议失败，请稍后重试");
        }
    }

    /**
     * 获取聊天历史
     */
    @Operation(summary = "获取聊天历史", description = "获取与AI助手的聊天历史记录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @SaCheckLogin
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getChatHistory(
            @Parameter(description = "会话ID", required = false) @RequestParam(required = false) String sessionId,
            @Parameter(description = "页码", required = false) @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小", required = false) @RequestParam(defaultValue = "20") int size) {

        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            
            AIChatHistory history = aiAssistantService.getChatHistory(userId, sessionId, page, size);
            return ResponseUtil.success("获取聊天历史成功", history);
        } catch (Exception e) {
            log.error("获取聊天历史失败", e);
            return ResponseUtil.error("获取聊天历史失败，请稍后重试");
        }
    }

    /**
     * 清空聊天历史
     */
    @Operation(summary = "清空聊天历史", description = "清空与AI助手的聊天历史记录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "清空成功")
    })
    @SaCheckLogin
    @DeleteMapping("/history")
    public ResponseEntity<ApiResponse<Void>> clearChatHistory(
            @Parameter(description = "会话ID", required = false) @RequestParam(required = false) String sessionId) {

        try {
            Long userId = SaTokenUtil.getCurrentUserId();

            aiAssistantService.clearChatHistory(userId, sessionId);
            return ResponseUtil.success("聊天历史清空成功", null);
        } catch (Exception e) {
            log.error("清空聊天历史失败", e);
            return ResponseUtil.error("清空聊天历史失败");
        }
    }

    /**
     * 获取AI状态
     */
    @Operation(summary = "获取AI状态", description = "获取AI助手的运行状态信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<AIStatus>> getAIStatus() {
        try {
            AIStatus status = aiAssistantService.getAIStatus();
            return ResponseUtil.success("获取AI状态成功", status);
        } catch (Exception e) {
            log.error("获取AI状态失败", e);
            return ResponseUtil.error("获取AI状态失败，请稍后重试");
        }
    }

    /**
     * 设置AI偏好
     */
    @Operation(summary = "设置AI偏好", description = "设置AI助手的个性化偏好")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "设置成功")
    })
    @SaCheckLogin
    @PostMapping("/preferences")
    public ResponseEntity<ApiResponse<Void>> setAIPreferences(
            @Parameter(description = "偏好设置", required = true) @RequestBody Map<String, Object> preferences) {

        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            AIUserPreferences userPreferences = AIUserPreferences.builder()
                    .userId(userId)
                    .preferences(com.alibaba.fastjson2.JSON.toJSONString(preferences))
                    .language(String.valueOf(preferences.getOrDefault("language", "zh")))
                    .recommendationType(String.valueOf(preferences.getOrDefault("recommendationType", "general")))
                    .notificationPreference(String.valueOf(preferences.getOrDefault("notificationPreference", "none")))
                    .build();
            
            aiAssistantService.setUserPreferences(userId, userPreferences);
            return ResponseUtil.success("AI偏好设置成功", null);
        } catch (Exception e) {
            log.error("设置AI偏好失败", e);
            return ResponseUtil.error("设置AI偏好失败");
        }
    }

    /**
     * 生成AI响应
     */
    private String generateAIResponse(String message, Long userId) {
        // 简单的关键词匹配响应
        String lowerMessage = message.toLowerCase();
        
        if (lowerMessage.contains("推荐") || lowerMessage.contains("商品")) {
            return "我为您推荐一些优质商品，请查看下方的推荐列表。";
        } else if (lowerMessage.contains("价格") || lowerMessage.contains("便宜")) {
            return "我帮您查找性价比高的商品，正在为您筛选...";
        } else if (lowerMessage.contains("对比") || lowerMessage.contains("比较")) {
            return "我来帮您对比这些商品的特点和价格。";
        } else if (lowerMessage.contains("帮助") || lowerMessage.contains("怎么")) {
            return "我是您的AI购物助手，可以帮您推荐商品、对比价格、解答问题。有什么需要帮助的吗？";
        } else {
            return "感谢您的咨询！我理解您的需求，正在为您寻找合适的商品。";
        }
    }

    /**
     * 获取推荐商品
     */
    private List<Product> getSuggestedProducts(String message, Long userId) {
        try {
            // 根据消息内容获取相关商品
            String lowerMessage = message.toLowerCase();
            
            if (lowerMessage.contains("运动") || lowerMessage.contains("健身")) {
                return productService.getProductsByCategory("运动健身", 1, 5).getData();
            } else if (lowerMessage.contains("时尚") || lowerMessage.contains("服装")) {
                return productService.getProductsByCategory("服装", 1, 5).getData();
            } else if (lowerMessage.contains("电子") || lowerMessage.contains("数码")) {
                return productService.getProductsByCategory("电子产品", 1, 5).getData();
            } else {
                return productService.getHotProducts(5);
            }
        } catch (Exception e) {
            log.error("获取推荐商品失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取通用建议
     */
    private List<Map<String, Object>> getGeneralSuggestions(Long userId) {
        List<Map<String, Object>> suggestions = new ArrayList<>();
        
        Map<String, Object> suggestion1 = new HashMap<>();
        suggestion1.put("type", "recommendation");
        suggestion1.put("title", "为您推荐");
        suggestion1.put("description", "基于您的偏好推荐商品");
        suggestion1.put("action", "get_recommendations");
        suggestions.add(suggestion1);
        
        Map<String, Object> suggestion2 = new HashMap<>();
        suggestion2.put("type", "comparison");
        suggestion2.put("title", "商品对比");
        suggestion2.put("description", "对比不同商品的特点");
        suggestion2.put("action", "compare_products");
        suggestions.add(suggestion2);
        
        return suggestions;
    }

    /**
     * 获取推荐建议
     */
    private List<Map<String, Object>> getRecommendationSuggestions(Long userId) {
        List<Map<String, Object>> suggestions = new ArrayList<>();
        
        Map<String, Object> suggestion1 = new HashMap<>();
        suggestion1.put("type", "trending");
        suggestion1.put("title", "热门商品");
        suggestion1.put("description", "查看当前热门商品");
        suggestion1.put("action", "get_trending");
        suggestions.add(suggestion1);
        
        Map<String, Object> suggestion2 = new HashMap<>();
        suggestion2.put("type", "personalized");
        suggestion2.put("title", "个性化推荐");
        suggestion2.put("description", "基于您的历史行为推荐");
        suggestion2.put("action", "get_personalized");
        suggestions.add(suggestion2);
        
        return suggestions;
    }

    /**
     * 获取对比建议
     */
    private List<Map<String, Object>> getComparisonSuggestions(Long userId) {
        List<Map<String, Object>> suggestions = new ArrayList<>();
        
        Map<String, Object> suggestion1 = new HashMap<>();
        suggestion1.put("type", "price");
        suggestion1.put("title", "价格对比");
        suggestion1.put("description", "对比商品价格");
        suggestion1.put("action", "compare_prices");
        suggestions.add(suggestion1);
        
        Map<String, Object> suggestion2 = new HashMap<>();
        suggestion2.put("type", "features");
        suggestion2.put("title", "功能对比");
        suggestion2.put("description", "对比商品功能特点");
        suggestion2.put("action", "compare_features");
        suggestions.add(suggestion2);
        
        return suggestions;
    }

    /**
     * 生成聊天历史
     */
    private List<Map<String, Object>> generateChatHistory(Long userId, String sessionId, int page, int size) {
        List<Map<String, Object>> history = new ArrayList<>();
        
        // 模拟聊天历史数据
        Map<String, Object> message1 = new HashMap<>();
        message1.put("id", "1");
        message1.put("type", "user");
        message1.put("content", "你好，能推荐一些商品吗？");
        message1.put("timestamp", LocalDateTime.now().minusHours(2));
        history.add(message1);
        
        Map<String, Object> message2 = new HashMap<>();
        message2.put("id", "2");
        message2.put("type", "assistant");
        message2.put("content", "您好！我很乐意为您推荐商品。请告诉我您感兴趣的商品类型或用途。");
        message2.put("timestamp", LocalDateTime.now().minusHours(2));
        history.add(message2);
        
        return history;
    }

    /**
     * 生成会话ID
     */
    private String generateSessionId() {
        return "session_" + System.currentTimeMillis();
    }
}