package com.shopx.controller;

import com.alibaba.fastjson2.JSON;
import com.shopx.entity.*;
import com.shopx.service.CollaborativeShoppingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WebSocket消息控制器
 * 处理实时协作购物消息
 */
@Slf4j
@Controller
public class WebSocketController {
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private CollaborativeShoppingService collaborativeService;
    
    /**
     * 处理聊天消息
     */
    @MessageMapping("/chat/{sessionId}")
    @SendTo("/topic/chat/{sessionId}")
    public ChatMessage handleChatMessage(
            @DestinationVariable String sessionId,
            @Payload Map<String, Object> message) {
        
        log.info("收到聊天消息: sessionId={}, message={}", sessionId, message);
        
        ChatMessage chatMessage = ChatMessage.builder()
                .sessionId(sessionId)
                .userId(Long.valueOf(message.get("userId").toString()))
                .username(message.get("username").toString())
                .message(message.get("message").toString())
                .messageType("text")
                .timestamp(LocalDateTime.now())
                .build();
        
        // 更新会话聊天记录
        updateSessionChatHistory(sessionId, chatMessage);
        
        return chatMessage;
    }
    
    /**
     * 处理用户加入会话
     */
    @MessageMapping("/join/{sessionId}")
    @SendTo("/topic/join/{sessionId}")
    public UserJoinLeaveMessage handleUserJoin(
            @DestinationVariable String sessionId,
            @Payload Map<String, Object> message) {
        
        log.info("用户加入会话: sessionId={}, userId={}", sessionId, message.get("userId"));
        
        UserJoinLeaveMessage joinMessage = UserJoinLeaveMessage.builder()
                .sessionId(sessionId)
                .userId(Long.valueOf(message.get("userId").toString()))
                .username(message.get("username").toString())
                .action("join")
                .timestamp(LocalDateTime.now())
                .build();
        
        // 更新会话参与者
        updateSessionParticipants(sessionId, joinMessage.getUserId());
        
        return joinMessage;
    }
    
    /**
     * 处理用户离开会话
     */
    @MessageMapping("/leave/{sessionId}")
    @SendTo("/topic/leave/{sessionId}")
    public UserJoinLeaveMessage handleUserLeave(
            @DestinationVariable String sessionId,
            @Payload Map<String, Object> message) {
        
        log.info("用户离开会话: sessionId={}, userId={}", sessionId, message.get("userId"));
        
        UserJoinLeaveMessage leaveMessage = UserJoinLeaveMessage.builder()
                .sessionId(sessionId)
                .userId(Long.valueOf(message.get("userId").toString()))
                .username(message.get("username").toString())
                .action("leave")
                .timestamp(LocalDateTime.now())
                .build();
        
        return leaveMessage;
    }
    
    /**
     * 处理商品标注
     */
    @MessageMapping("/annotation/{sessionId}")
    @SendTo("/topic/annotation/{sessionId}")
    public AnnotationMessage handleAnnotation(
            @DestinationVariable String sessionId,
            @Payload Map<String, Object> message) {
        
        log.info("收到商品标注: sessionId={}, userId={}", sessionId, message.get("userId"));
        
        AnnotationMessage annotationMessage = AnnotationMessage.builder()
                .sessionId(sessionId)
                .userId(Long.valueOf(message.get("userId").toString()))
                .username(message.get("username").toString())
                .annotation(message.get("annotation").toString())
                .position((Map<String, Object>) message.get("position"))
                .timestamp(LocalDateTime.now())
                .build();
        
        // 更新会话标注记录
        updateSessionAnnotations(sessionId, annotationMessage);
        
        return annotationMessage;
    }
    
    /**
     * 处理商品切换
     */
    @MessageMapping("/product-change/{sessionId}")
    @SendTo("/topic/product-change/{sessionId}")
    public ProductChangeMessage handleProductChange(
            @DestinationVariable String sessionId,
            @Payload Map<String, Object> message) {
        
        log.info("商品切换: sessionId={}, productId={}", sessionId, message.get("productId"));
        
        ProductChangeMessage productChangeMessage = ProductChangeMessage.builder()
                .sessionId(sessionId)
                .userId(Long.valueOf(message.get("userId").toString()))
                .username(message.get("username").toString())
                .productId(Long.valueOf(message.get("productId").toString()))
                .productName(message.get("productName") != null ? message.get("productName").toString() : null)
                .timestamp(LocalDateTime.now())
                .build();
        
        return productChangeMessage;
    }
    
    /**
     * 处理AR/VR体验
     */
    @MessageMapping("/experience/{sessionId}")
    @SendTo("/topic/experience/{sessionId}")
    public ExperienceMessage handleExperience(
            @DestinationVariable String sessionId,
            @Payload Map<String, Object> message) {
        
        log.info("AR/VR体验: sessionId={}, experienceType={}", sessionId, message.get("experienceType"));
        
        ExperienceMessage experienceMessage = ExperienceMessage.builder()
                .sessionId(sessionId)
                .userId(Long.valueOf(message.get("userId").toString()))
                .username(message.get("username").toString())
                .experienceType(message.get("experienceType").toString())
                .experienceData((Map<String, Object>) message.get("experienceData"))
                .timestamp(LocalDateTime.now())
                .build();
        
        return experienceMessage;
    }
    
    /**
     * 更新会话聊天记录
     */
    private void updateSessionChatHistory(String sessionId, ChatMessage message) {
        try {
            ShoppingSession session = collaborativeService.getSession(sessionId);
            if (session != null) {
                List<Map<String, Object>> chatHistory = JSON.parseArray(session.getChatHistory(), Map.class);
                
                Map<String, Object> messageMap = new HashMap<>();
                messageMap.put("userId", message.getUserId());
                messageMap.put("username", message.getUsername());
                messageMap.put("message", message.getMessage());
                messageMap.put("messageType", message.getMessageType());
                messageMap.put("timestamp", message.getTimestamp().toString());
                
                chatHistory.add(messageMap);
                session.setChatHistory(JSON.toJSONString(chatHistory));
                // 这里应该调用更新方法，但为了简化，暂时不实现
            }
        } catch (Exception e) {
            log.error("更新聊天记录失败", e);
        }
    }
    
    /**
     * 更新会话参与者
     */
    private void updateSessionParticipants(String sessionId, Long userId) {
        try {
            collaborativeService.joinSession(sessionId, userId);
        } catch (Exception e) {
            log.error("更新参与者失败", e);
        }
    }
    
    /**
     * 更新会话标注记录
     */
    private void updateSessionAnnotations(String sessionId, AnnotationMessage annotation) {
        try {
            ShoppingSession session = collaborativeService.getSession(sessionId);
            if (session != null) {
                List<Map<String, Object>> annotations = JSON.parseArray(session.getAnnotations(), Map.class);
                
                Map<String, Object> annotationMap = new HashMap<>();
                annotationMap.put("userId", annotation.getUserId());
                annotationMap.put("username", annotation.getUsername());
                annotationMap.put("annotation", annotation.getAnnotation());
                annotationMap.put("position", annotation.getPosition());
                annotationMap.put("timestamp", annotation.getTimestamp().toString());
                
                annotations.add(annotationMap);
                session.setAnnotations(JSON.toJSONString(annotations));
                // 这里应该调用更新方法，但为了简化，暂时不实现
            }
        } catch (Exception e) {
            log.error("更新标注记录失败", e);
        }
    }
}
