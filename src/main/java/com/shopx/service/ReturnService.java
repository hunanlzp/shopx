package com.shopx.service;

import com.shopx.entity.ReturnOrder;
import com.shopx.util.ResponseUtil;

import java.util.List;

/**
 * 退货服务接口
 */
public interface ReturnService {
    
    /**
     * 一键退货申请
     */
    ReturnOrder createReturnOrder(Long orderId, Long productId, Integer quantity, String reason, String description);
    
    /**
     * 审核退货申请
     */
    ReturnOrder auditReturnOrder(Long returnOrderId, String status, String comment);
    
    /**
     * 生成退货标签
     */
    String generateReturnLabel(Long returnOrderId);
    
    /**
     * 处理退款（24-48小时到账）
     */
    boolean processRefund(Long returnOrderId);
    
    /**
     * 获取用户退货列表
     */
    ResponseUtil.PageResult<ReturnOrder> getUserReturnOrders(Long userId, int page, int size);
    
    /**
     * 获取退货详情
     */
    ReturnOrder getReturnOrderById(Long returnOrderId);
    
    /**
     * 取消退货申请
     */
    boolean cancelReturnOrder(Long returnOrderId, Long userId);
}

