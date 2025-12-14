package com.shopx.service.impl;

import com.alibaba.fastjson2.JSON;
import com.shopx.config.AIConfig;
import com.shopx.constant.Constants;
import com.shopx.entity.*;
import com.shopx.enums.AIProviderEnum;
import com.shopx.service.AIAssistantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * AI助手服务实现类
 */
@Slf4j
@Service
public class AIAssistantServiceImpl implements AIAssistantService {
    
    @Autowired(required = false)
    private RestTemplate restTemplate;
    
    @Autowired
    private AIConfig aiConfig;
    
    @Autowired(required = false)
    private com.shopx.service.UserBehaviorService userBehaviorService;
    
    // 模拟AI对话数据存储
    private final Map<String, List<AIChatMessage>> chatHistories = new HashMap<>();
    private final Map<Long, AIUserPreferences> userPreferences = new HashMap<>();
    
    public AIAssistantServiceImpl() {
        if (restTemplate == null) {
            restTemplate = new RestTemplate();
        }
    }
    
    @Override
    public AIResponse processMessage(Long userId, String message, String sessionId) {
        log.info("处理AI助手消息: userId={}, message={}, sessionId={}", userId, message, sessionId);
        
        // 记录用户行为：AI对话
        if (userBehaviorService != null) {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("sessionId", sessionId);
            metadata.put("message", message);
            userBehaviorService.recordBehavior(userId, null, "AI_CHAT", metadata);
        }
        
        // 生成会话ID
        if (sessionId == null) {
            sessionId = "session_" + userId + "_" + System.currentTimeMillis();
        }
        
        // 保存用户消息
        saveMessage(sessionId, userId, message, "user");
        
        // 分析用户偏好，用于个性化回复
        Map<String, Object> userPreferences = null;
        if (userBehaviorService != null) {
            userPreferences = userBehaviorService.analyzeUserPreferences(userId);
        }
        
        // 生成AI回复（基于用户偏好）
        String aiReply = generateAIResponse(userId, message, userPreferences);
        
        // 保存AI回复
        saveMessage(sessionId, userId, aiReply, "assistant");
        
        // 构建响应
        return AIResponse.builder()
                .sessionId(sessionId)
                .reply(aiReply)
                .timestamp(LocalDateTime.now().toString())
                .suggestions(generateSuggestions(userId, message, userPreferences))
                .responseType("text")
                .confidence(0.95)
                .build();
    }
    
    @Override
    public List<AISuggestion> getSuggestions(Long userId, String type) {
        log.info("获取AI建议: userId={}, type={}", userId, type);
        
        List<AISuggestion> suggestions = new ArrayList<>();
        
        // 根据类型生成不同的建议
        switch (type != null ? type.toLowerCase() : "general") {
            case "product":
                suggestions.addAll(generateProductSuggestions(userId));
                break;
            case "lifestyle":
                suggestions.addAll(generateLifestyleSuggestions(userId));
                break;
            case "shopping":
                suggestions.addAll(generateShoppingSuggestions(userId));
                break;
            default:
                suggestions.addAll(generateGeneralSuggestions(userId));
                break;
        }
        
        return suggestions;
    }
    
    @Override
    public AIChatHistory getChatHistory(Long userId, String sessionId, Integer page, Integer size) {
        log.info("获取AI对话历史: userId={}, sessionId={}, page={}, size={}", userId, sessionId, page, size);
        
        List<AIChatMessage> allMessages = new ArrayList<>();
        
        if (sessionId != null) {
            // 获取特定会话的历史
            allMessages = chatHistories.getOrDefault(sessionId, new ArrayList<>());
        } else {
            // 获取用户所有会话的历史
            for (Map.Entry<String, List<AIChatMessage>> entry : chatHistories.entrySet()) {
                if (entry.getKey().startsWith("session_" + userId + "_")) {
                    allMessages.addAll(entry.getValue());
                }
            }
        }
        
        // 分页处理
        int start = (page - 1) * size;
        int end = Math.min(start + size, allMessages.size());
        List<AIChatMessage> pagedMessages = allMessages.subList(start, end);
        
        int totalPages = (int) Math.ceil((double) allMessages.size() / size);
        
        return AIChatHistory.builder()
                .messages(pagedMessages)
                .total((long) allMessages.size())
                .page(page)
                .size(size)
                .totalPages(totalPages)
                .build();
    }
    
    @Override
    public void clearChatHistory(Long userId, String sessionId) {
        log.info("清除AI对话历史: userId={}, sessionId={}", userId, sessionId);
        
        if (sessionId != null) {
            chatHistories.remove(sessionId);
        } else {
            // 清除用户所有会话历史
            chatHistories.entrySet().removeIf(entry -> 
                entry.getKey().startsWith("session_" + userId + "_"));
        }
    }
    
    @Override
    public AIStatus getAIStatus() {
        return AIStatus.builder()
                .status("online")
                .version("1.0.0")
                .totalSessions(chatHistories.size())
                .totalUsers(userPreferences.size())
                .lastUpdate(LocalDateTime.now().toString())
                .responseTime(150L)
                .memoryUsage(45.5)
                .cpuUsage(12.3)
                .build();
    }
    
    @Override
    public void setUserPreferences(Long userId, AIUserPreferences preferences) {
        log.info("设置用户偏好: userId={}, preferences={}", userId, preferences);
        userPreferences.put(userId, preferences);
    }
    
    /**
     * 保存消息到历史记录
     */
    private void saveMessage(String sessionId, Long userId, String content, String role) {
        AIChatMessage message = AIChatMessage.builder()
                .sessionId(sessionId)
                .userId(userId)
                .role(role)
                .content(content)
                .messageType("text")
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        
        chatHistories.computeIfAbsent(sessionId, k -> new ArrayList<>()).add(message);
    }
    
    /**
     * 生成AI回复（重载方法，兼容旧代码）
     */
    private String generateAIResponse(Long userId, String message) {
        return generateAIResponse(userId, message, null);
    }
    
    /**
     * 生成AI回复
     */
    private String generateAIResponse(Long userId, String message, Map<String, Object> userPreferences) {
        // 如果AI服务未启用，使用降级策略
        if (!aiConfig.getEnabled()) {
            return generateFallbackResponse(message);
        }
        
        try {
            // 根据配置的提供商调用不同的AI服务
            AIProviderEnum provider = AIProviderEnum.fromCode(aiConfig.getProvider());
            String response = switch (provider) {
                case OPENAI -> callOpenAI(userId, message);
                case CLAUDE -> callClaude(userId, message);
                case CUSTOM -> callCustomAI(userId, message);
            };
            
            return response != null ? response : generateFallbackResponse(message);
        } catch (Exception e) {
            log.error("AI服务调用失败，使用降级策略", e);
            if (aiConfig.getFallbackEnabled()) {
                return generateFallbackResponse(message);
            }
            throw new RuntimeException("AI服务不可用", e);
        }
    }
    
    /**
     * 调用OpenAI API
     */
    private String callOpenAI(Long userId, String message) {
        AIConfig.OpenAI config = aiConfig.getOpenai();
        
        if (config.getApiKey() == null || config.getApiKey().equals("your-api-key-here")) {
            log.warn("OpenAI API Key未配置，使用降级策略");
            return null;
        }
        
        try {
            // 构建请求
            Map<String, Object> request = new HashMap<>();
            request.put("model", config.getModel());
            request.put("max_tokens", config.getMaxTokens());
            request.put("temperature", config.getTemperature());
            
            // 构建消息历史
            List<Map<String, String>> messages = new ArrayList<>();
            
            // 系统提示词（包含用户偏好信息）
            StringBuilder systemPrompt = new StringBuilder("你是ShopX电商平台的AI购物助手。你的任务是帮助用户购物，推荐商品，解答购物问题。请用友好、专业的语气回复用户。");
            if (userPreferences != null) {
                systemPrompt.append("\n用户偏好信息：");
                if (userPreferences.containsKey("totalLikes")) {
                    systemPrompt.append("用户已喜欢").append(userPreferences.get("totalLikes")).append("个商品。");
                }
                if (userPreferences.containsKey("totalViews")) {
                    systemPrompt.append("用户已浏览").append(userPreferences.get("totalViews")).append("个商品。");
                }
            }
            
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", systemPrompt.toString());
            messages.add(systemMessage);
            
            // 获取历史对话
            String sessionId = "session_" + userId + "_" + System.currentTimeMillis();
            List<AIChatMessage> history = chatHistories.getOrDefault(sessionId, new ArrayList<>());
            for (AIChatMessage msg : history.subList(Math.max(0, history.size() - 5), history.size())) {
                Map<String, String> histMsg = new HashMap<>();
                histMsg.put("role", "user".equals(msg.getRole()) ? "user" : "assistant");
                histMsg.put("content", msg.getContent());
                messages.add(histMsg);
            }
            
            // 当前用户消息
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", message);
            messages.add(userMessage);
            
            request.put("messages", messages);
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(config.getApiKey());
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
            
            // 发送请求
            ResponseEntity<Map> response = restTemplate.exchange(
                config.getApiUrl(),
                HttpMethod.POST,
                entity,
                Map.class
            );
            
            // 解析响应
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                List<Map<String, Object>> choices = (List<Map<String, Object>>) body.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> choice = choices.get(0);
                    Map<String, Object> messageObj = (Map<String, Object>) choice.get("message");
                    return (String) messageObj.get("content");
                }
            }
            
            return null;
        } catch (Exception e) {
            log.error("调用OpenAI API失败", e);
            return null;
        }
    }
    
    /**
     * 调用Claude API
     */
    private String callClaude(Long userId, String message) {
        AIConfig.Claude config = aiConfig.getClaude();
        
        if (config.getApiKey() == null || config.getApiKey().equals("your-api-key-here")) {
            log.warn("Claude API Key未配置，使用降级策略");
            return null;
        }
        
        try {
            // 构建请求
            Map<String, Object> request = new HashMap<>();
            request.put("model", config.getModel());
            request.put("max_tokens", config.getMaxTokens());
            
            // 构建消息
            Map<String, String> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", message);
            
            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(userMsg);
            request.put("messages", messages);
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-api-key", config.getApiKey());
            headers.set("anthropic-version", "2023-06-01");
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
            
            // 发送请求
            ResponseEntity<Map> response = restTemplate.exchange(
                config.getApiUrl(),
                HttpMethod.POST,
                entity,
                Map.class
            );
            
            // 解析响应
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                List<Map<String, Object>> content = (List<Map<String, Object>>) body.get("content");
                if (content != null && !content.isEmpty()) {
                    return (String) content.get(0).get("text");
                }
            }
            
            return null;
        } catch (Exception e) {
            log.error("调用Claude API失败", e);
            return null;
        }
    }
    
    /**
     * 调用自定义AI服务
     */
    private String callCustomAI(Long userId, String message) {
        AIConfig.Custom config = aiConfig.getCustom();
        
        if (config.getApiUrl() == null || config.getApiUrl().isEmpty()) {
            log.warn("自定义AI服务URL未配置，使用降级策略");
            return null;
        }
        
        try {
            // 构建请求
            Map<String, Object> request = new HashMap<>();
            request.put("userId", userId);
            request.put("message", message);
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (config.getApiKey() != null && !config.getApiKey().isEmpty()) {
                headers.set("Authorization", "Bearer " + config.getApiKey());
            }
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
            
            // 发送请求
            ResponseEntity<Map> response = restTemplate.exchange(
                config.getApiUrl(),
                HttpMethod.POST,
                entity,
                Map.class
            );
            
            // 解析响应
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                return (String) body.get("response");
            }
            
            return null;
        } catch (Exception e) {
            log.error("调用自定义AI服务失败", e);
            return null;
        }
    }
    
    /**
     * 降级策略：使用规则引擎生成回复
     */
    private String generateFallbackResponse(String message) {
        String lowerMessage = message.toLowerCase();
        
        // 商品推荐相关
        if (lowerMessage.contains("推荐") || lowerMessage.contains("商品")) {
            return "根据您的需求，我为您推荐以下商品：\n" +
                   "1. 智能运动手环 - 适合健身爱好者\n" +
                   "2. 时尚连衣裙 - 适合日常穿搭\n" +
                   "3. 无线蓝牙耳机 - 适合音乐爱好者\n" +
                   "您想了解哪个商品的详细信息吗？";
        }
        
        // 价格相关
        if (lowerMessage.contains("价格") || lowerMessage.contains("多少钱")) {
            return "我们的商品价格都很实惠，并且经常有优惠活动。\n" +
                   "您可以在商品详情页面查看具体价格，\n" +
                   "也可以告诉我您感兴趣的价位，我来为您推荐合适的商品。";
        }
        
        // 购物车相关
        if (lowerMessage.contains("购物车") || lowerMessage.contains("加入")) {
            return "您可以将喜欢的商品加入购物车，\n" +
                   "然后一起结算，这样更方便也更省钱。\n" +
                   "需要我帮您推荐一些搭配商品吗？";
        }
        
        // AR/VR体验相关
        if (lowerMessage.contains("ar") || lowerMessage.contains("vr") || lowerMessage.contains("体验")) {
            return "我们支持AR和VR体验功能！\n" +
                   "您可以在商品详情页面点击'AR体验'或'VR体验'按钮，\n" +
                   "通过3D模型更直观地了解商品。\n" +
                   "需要我为您演示如何使用吗？";
        }
        
        // 协作购物相关
        if (lowerMessage.contains("朋友") || lowerMessage.contains("一起") || lowerMessage.contains("协作")) {
            return "协作购物功能让您可以和朋友一起购物！\n" +
                   "您可以创建协作会话，邀请朋友一起浏览商品，\n" +
                   "实时聊天讨论，共同决策购买。\n" +
                   "需要我帮您创建协作会话吗？";
        }
        
        // 环保相关
        if (lowerMessage.contains("环保") || lowerMessage.contains("回收") || lowerMessage.contains("可持续")) {
            return "我们非常重视环保和可持续发展！\n" +
                   "许多商品都支持回收利用，\n" +
                   "您可以在商品详情页面查看回收价值。\n" +
                   "让我们一起为环保事业贡献力量！";
        }
        
        // 默认回复
        return "您好！我是ShopX的AI购物助手，很高兴为您服务！\n" +
               "我可以帮您：\n" +
               "• 推荐合适的商品\n" +
               "• 解答购物问题\n" +
               "• 介绍AR/VR体验功能\n" +
               "• 协助协作购物\n" +
               "• 提供环保购物建议\n" +
               "请告诉我您需要什么帮助？";
    }
    
    /**
     * 生成建议（重载方法，兼容旧代码）
     */
    private List<AISuggestion> generateSuggestions(Long userId, String message) {
        return generateSuggestions(userId, message, null);
    }
    
    /**
     * 生成建议
     */
    private List<AISuggestion> generateSuggestions(Long userId, String message, Map<String, Object> userPreferences) {
        List<AISuggestion> suggestions = new ArrayList<>();
        
        suggestions.add(AISuggestion.builder()
                .type("product")
                .title("查看热门商品")
                .description("浏览当前最受欢迎的商品")
                .action("navigate_to_products")
                .priority(1)
                .build());
        
        suggestions.add(AISuggestion.builder()
                .type("recommendation")
                .title("获取个性化推荐")
                .description("基于您的喜好推荐商品")
                .action("navigate_to_recommendation")
                .priority(2)
                .build());
        
        suggestions.add(AISuggestion.builder()
                .type("collaboration")
                .title("开始协作购物")
                .description("邀请朋友一起购物")
                .action("navigate_to_collaboration")
                .priority(3)
                .build());
        
        return suggestions;
    }
    
    /**
     * 生成商品建议
     */
    private List<AISuggestion> generateProductSuggestions(Long userId) {
        List<AISuggestion> suggestions = new ArrayList<>();
        
        suggestions.add(AISuggestion.builder()
                .type("product")
                .title("智能运动手环")
                .description("适合健身爱好者，支持多种运动模式")
                .data(Map.of("price", "299", "image", "https://picsum.photos/200/200?random=1"))
                .priority(1)
                .build());
        
        suggestions.add(AISuggestion.builder()
                .type("product")
                .title("时尚连衣裙")
                .description("优雅时尚，适合多种场合")
                .data(Map.of("price", "199", "image", "https://picsum.photos/200/200?random=2"))
                .priority(2)
                .build());
        
        return suggestions;
    }
    
    /**
     * 生成生活方式建议
     */
    private List<AISuggestion> generateLifestyleSuggestions(Long userId) {
        List<AISuggestion> suggestions = new ArrayList<>();
        
        suggestions.add(AISuggestion.builder()
                .type("lifestyle")
                .title("健康生活")
                .description("推荐健康相关的商品和服务")
                .data(Map.of("category", "health"))
                .priority(1)
                .build());
        
        suggestions.add(AISuggestion.builder()
                .type("lifestyle")
                .title("时尚穿搭")
                .description("为您推荐时尚搭配方案")
                .data(Map.of("category", "fashion"))
                .priority(2)
                .build());
        
        return suggestions;
    }
    
    /**
     * 生成购物建议
     */
    private List<AISuggestion> generateShoppingSuggestions(Long userId) {
        List<AISuggestion> suggestions = new ArrayList<>();
        
        suggestions.add(AISuggestion.builder()
                .type("shopping")
                .title("优惠活动")
                .description("查看当前优惠活动")
                .action("view_discounts")
                .priority(1)
                .build());
        
        suggestions.add(AISuggestion.builder()
                .type("shopping")
                .title("购物车优化")
                .description("优化您的购物车商品")
                .action("optimize_cart")
                .priority(2)
                .build());
        
        return suggestions;
    }
    
    /**
     * 生成通用建议
     */
    private List<AISuggestion> generateGeneralSuggestions(Long userId) {
        List<AISuggestion> suggestions = new ArrayList<>();
        
        suggestions.add(AISuggestion.builder()
                .type("general")
                .title("新用户指南")
                .description("了解ShopX平台的功能特色")
                .action("show_guide")
                .priority(1)
                .build());
        
        suggestions.add(AISuggestion.builder()
                .type("general")
                .title("联系客服")
                .description("如有问题，请联系客服")
                .action("contact_support")
                .priority(2)
                .build());
        
        return suggestions;
    }
}
