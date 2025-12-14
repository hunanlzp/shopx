package com.shopx.service;

import com.shopx.entity.Order;
import com.shopx.util.ResponseUtil;

import java.util.List;

/**
 * 订单服务接口
 */
public interface OrderService {
    
    /**
     * 创建订单
     */
    Order createOrder(Long userId, List<Long> cartItemIds, String shippingAddress);
    
    /**
     * 获取订单列表
     */
    ResponseUtil.PageResult<Order> getOrders(Long userId, int page, int size);
    
    /**
     * 获取订单详情
     */
    Order getOrderById(Long orderId);
    
    /**
     * 取消订单
     */
    boolean cancelOrder(Long orderId, Long userId);
    
    /**
     * 更新订单状态
     */
    boolean updateOrderStatus(Long orderId, String status);
    
    /**
     * 支付订单
     */
    boolean payOrder(Long orderId, String paymentMethod);
}

