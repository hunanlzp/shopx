package com.shopx.service;

import com.shopx.entity.*;

import java.util.List;

/**
 * AI助手服务接口
 */
public interface AIAssistantService {
    
    /**
     * 处理用户消息
     * @param userId 用户ID
     * @param message 消息内容
     * @param sessionId 会话ID
     * @return AI回复
     */
    AIResponse processMessage(Long userId, String message, String sessionId);
    
    /**
     * 获取AI建议
     * @param userId 用户ID
     * @param type 建议类型
     * @return 建议列表
     */
    List<AISuggestion> getSuggestions(Long userId, String type);
    
    /**
     * 获取对话历史
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @param page 页码
     * @param size 每页大小
     * @return 对话历史
     */
    AIChatHistory getChatHistory(Long userId, String sessionId, Integer page, Integer size);
    
    /**
     * 清除对话历史
     * @param userId 用户ID
     * @param sessionId 会话ID
     */
    void clearChatHistory(Long userId, String sessionId);
    
    /**
     * 获取AI状态
     * @return AI状态信息
     */
    AIStatus getAIStatus();
    
    /**
     * 设置用户偏好
     * @param userId 用户ID
     * @param preferences 偏好设置
     */
    void setUserPreferences(Long userId, AIUserPreferences preferences);
}
