package com.shopx.service;

import com.shopx.dto.PriceCalculationDTO;
import com.shopx.entity.PriceHistory;
import com.shopx.entity.PriceProtection;
import com.shopx.entity.Product;

import java.math.BigDecimal;
import java.util.List;

/**
 * 价格服务接口
 */
public interface PriceService {
    
    /**
     * 记录价格变动
     */
    void recordPriceChange(Product product, String reason);
    
    /**
     * 获取价格历史
     */
    List<PriceHistory> getPriceHistory(Long productId, int days);
    
    /**
     * 计算商品总价（含运费、税费）
     */
    PriceCalculationDTO calculateTotalPrice(Long productId, Integer quantity, Long shippingAddressId);
    
    /**
     * 创建价格保护
     */
    PriceProtection createPriceProtection(Long orderId, Long userId, Long productId, BigDecimal purchasePrice);
    
    /**
     * 检查价格保护并退款
     */
    List<PriceProtection> checkAndRefundPriceProtection();
    
    /**
     * 获取用户的价格保护列表
     */
    List<PriceProtection> getUserPriceProtections(Long userId);
}

