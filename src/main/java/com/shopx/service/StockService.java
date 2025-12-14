package com.shopx.service;

import com.shopx.entity.Product;
import com.shopx.entity.ProductReservation;
import com.shopx.entity.StockNotification;

import java.util.List;

/**
 * 库存服务接口
 */
public interface StockService {
    
    /**
     * 检查库存并实时同步
     */
    boolean checkAndSyncStock(Long productId, Integer quantity);
    
    /**
     * 添加缺货提醒
     */
    StockNotification addStockNotification(Long userId, Long productId);
    
    /**
     * 获取用户的缺货提醒列表
     */
    List<StockNotification> getUserStockNotifications(Long userId);
    
    /**
     * 取消缺货提醒
     */
    boolean cancelStockNotification(Long notificationId, Long userId);
    
    /**
     * 处理到货通知（当商品补货时）
     */
    void notifyStockAvailable(Long productId);
    
    /**
     * 创建商品预订
     */
    ProductReservation createReservation(Long userId, Long productId, Integer quantity);
    
    /**
     * 获取用户预订列表
     */
    List<ProductReservation> getUserReservations(Long userId);
    
    /**
     * 取消预订
     */
    boolean cancelReservation(Long reservationId, Long userId);
    
    /**
     * 处理预订（当商品到货时）
     */
    void fulfillReservation(Long productId);
    
    /**
     * 获取替代商品推荐
     */
    List<Product> getAlternativeProducts(Long productId, int limit);
}

