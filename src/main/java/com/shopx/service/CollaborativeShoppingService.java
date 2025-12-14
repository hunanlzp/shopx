package com.shopx.service;

import com.shopx.entity.ShoppingSession;
import java.util.List;

/**
 * 协作购物服务 - 支持多人实时购物
 */
public interface CollaborativeShoppingService {
    
    /**
     * 创建协作购物会话
     * @param hostUserId 主持人用户ID
     * @param productId 商品ID
     * @return 会话ID
     */
    String createSession(Long hostUserId, Long productId);
    
    /**
     * 加入会话
     * @param sessionId 会话ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean joinSession(String sessionId, Long userId);
    
    /**
     * 获取会话信息
     * @param sessionId 会话ID
     * @return 会话信息
     */
    ShoppingSession getSession(String sessionId);
    
    /**
     * 结束会话
     * @param sessionId 会话ID
     */
    void endSession(String sessionId);
}

