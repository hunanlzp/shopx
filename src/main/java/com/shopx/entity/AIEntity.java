package com.shopx.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AI助手聊天消息实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIChatMessage {
    
    /**
     * 消息ID
     */
    private Long id;
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 消息角色（user/assistant）
     */
    private String role;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 消息类型
     */
    private String messageType;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

/**
 * AI助手回复实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIResponse {
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * AI回复内容
     */
    private String reply;
    
    /**
     * 回复时间
     */
    private String timestamp;
    
    /**
     * 建议列表
     */
    private List<AISuggestion> suggestions;
    
    /**
     * 回复类型
     */
    private String responseType;
    
    /**
     * 置信度
     */
    private Double confidence;
}

/**
 * AI建议实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AISuggestion {
    
    /**
     * 建议类型
     */
    private String type;
    
    /**
     * 建议标题
     */
    private String title;
    
    /**
     * 建议描述
     */
    private String description;
    
    /**
     * 建议动作
     */
    private String action;
    
    /**
     * 建议数据
     */
    private Object data;
    
    /**
     * 建议优先级
     */
    private Integer priority;
}

/**
 * AI对话历史实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIChatHistory {
    
    /**
     * 消息列表
     */
    private List<AIChatMessage> messages;
    
    /**
     * 总数量
     */
    private Long total;
    
    /**
     * 当前页码
     */
    private Integer page;
    
    /**
     * 每页大小
     */
    private Integer size;
    
    /**
     * 总页数
     */
    private Integer totalPages;
}

/**
 * AI助手状态实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIStatus {
    
    /**
     * 状态（online/offline）
     */
    private String status;
    
    /**
     * 版本号
     */
    private String version;
    
    /**
     * 总会话数
     */
    private Integer totalSessions;
    
    /**
     * 总用户数
     */
    private Integer totalUsers;
    
    /**
     * 最后更新时间
     */
    private String lastUpdate;
    
    /**
     * 响应时间（毫秒）
     */
    private Long responseTime;
    
    /**
     * 内存使用率
     */
    private Double memoryUsage;
    
    /**
     * CPU使用率
     */
    private Double cpuUsage;
}

/**
 * AI用户偏好实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIUserPreferences {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 偏好设置
     */
    private String preferences;
    
    /**
     * 语言偏好
     */
    private String language;
    
    /**
     * 推荐类型偏好
     */
    private String recommendationType;
    
    /**
     * 通知偏好
     */
    private String notificationPreference;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
