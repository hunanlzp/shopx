package com.shopx.service.impl;

import com.shopx.entity.ShoppingSession;
import com.shopx.mapper.ShoppingSessionMapper;
import com.shopx.service.CollaborativeShoppingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.alibaba.fastjson2.JSON;

/**
 * 协作购物服务实现
 */
@Slf4j
@Service
public class CollaborativeShoppingServiceImpl implements CollaborativeShoppingService {
    
    @Autowired
    private ShoppingSessionMapper sessionMapper;
    
    @Override
    public String createSession(Long hostUserId, Long productId) {
        log.info("创建协作购物会话，主持人: {}, 商品: {}", hostUserId, productId);
        
        String sessionId = UUID.randomUUID().toString().replace("-", "");
        
        ShoppingSession session = new ShoppingSession();
        session.setSessionId(sessionId);
        session.setHostUserId(hostUserId);
        session.setProductId(productId);
        session.setSessionStatus("ACTIVE");
        session.setParticipantIds(JSON.toJSONString(new ArrayList<Long>()));
        session.setChatHistory("[]");
        session.setAnnotations("[]");
        session.setCreateTime(LocalDateTime.now());
        session.setUpdateTime(LocalDateTime.now());
        
        sessionMapper.insert(session);
        
        return sessionId;
    }
    
    @Override
    public boolean joinSession(String sessionId, Long userId) {
        log.info("用户 {} 加入会话 {}", userId, sessionId);
        
        ShoppingSession session = sessionMapper.selectBySessionId(sessionId);
        if (session == null || !"ACTIVE".equals(session.getSessionStatus())) {
            return false;
        }
        
        try {
            List<Long> participants = JSON.parseArray(session.getParticipantIds(), Long.class);
            if (!participants.contains(userId)) {
                participants.add(userId);
            }
            session.setParticipantIds(JSON.toJSONString(participants));
            session.setUpdateTime(LocalDateTime.now());
            sessionMapper.updateById(session);
            return true;
        } catch (Exception e) {
            log.error("加入会话失败", e);
            return false;
        }
    }
    
    @Override
    public ShoppingSession getSession(String sessionId) {
        return sessionMapper.selectBySessionId(sessionId);
    }
    
    @Override
    public void endSession(String sessionId) {
        log.info("结束会话 {}", sessionId);
        
        ShoppingSession session = sessionMapper.selectBySessionId(sessionId);
        if (session != null) {
            session.setSessionStatus("ENDED");
            session.setUpdateTime(LocalDateTime.now());
            sessionMapper.updateById(session);
        }
    }
}

