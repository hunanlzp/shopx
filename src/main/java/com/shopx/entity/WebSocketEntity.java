package com.shopx.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * WebSocket消息实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebSocketMessage {
    
    /**
     * 消息类型
     */
    private String type;
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 消息内容
     */
    private String message;
    
    /**
     * 时间戳
     */
    private String timestamp;
    
    /**
     * 额外数据
     */
    private Map<String, Object> data;
}

/**
 * 聊天消息实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 消息内容
     */
    private String message;
    
    /**
     * 消息类型
     */
    private String messageType;
    
    /**
     * 时间戳
     */
    private LocalDateTime timestamp;
}

/**
 * 用户加入/离开消息实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserJoinLeaveMessage {
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 操作类型（join/leave）
     */
    private String action;
    
    /**
     * 时间戳
     */
    private LocalDateTime timestamp;
}

/**
 * 商品标注消息实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnotationMessage {
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 标注内容
     */
    private String annotation;
    
    /**
     * 标注位置
     */
    private Map<String, Object> position;
    
    /**
     * 时间戳
     */
    private LocalDateTime timestamp;
}

/**
 * 商品切换消息实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductChangeMessage {
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 商品ID
     */
    private Long productId;
    
    /**
     * 商品名称
     */
    private String productName;
    
    /**
     * 时间戳
     */
    private LocalDateTime timestamp;
}

/**
 * AR/VR体验消息实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExperienceMessage {
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 体验类型（AR/VR）
     */
    private String experienceType;
    
    /**
     * 体验数据
     */
    private Map<String, Object> experienceData;
    
    /**
     * 时间戳
     */
    private LocalDateTime timestamp;
}
