package com.shopx.service;

import com.shopx.entity.LogisticsTracking;
import com.shopx.entity.ShippingAddress;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 物流服务接口
 */
public interface LogisticsService {
    
    /**
     * 创建物流追踪记录
     */
    LogisticsTracking createTracking(Long orderId, String trackingNumber, String logisticsCompany, String shippingMethod);
    
    /**
     * 同步物流信息（从第三方API）
     */
    LogisticsTracking syncLogisticsInfo(String trackingNumber, String logisticsCompany);
    
    /**
     * 获取物流追踪信息
     */
    LogisticsTracking getTrackingInfo(Long orderId);
    
    /**
     * 计算配送费用
     */
    BigDecimal calculateShippingFee(String shippingMethod, String shippingAddress, BigDecimal totalAmount);
    
    /**
     * 预估配送时间
     */
    LocalDateTime estimateDeliveryTime(String shippingMethod, String shippingAddress);
    
    /**
     * 获取配送选项
     */
    List<com.shopx.dto.ShippingOptionDTO> getShippingOptions(String shippingAddress);
    
    /**
     * 添加收货地址
     */
    ShippingAddress addShippingAddress(Long userId, ShippingAddress address);
    
    /**
     * 获取用户收货地址列表
     */
    List<ShippingAddress> getUserShippingAddresses(Long userId);
    
    /**
     * 设置默认地址
     */
    boolean setDefaultAddress(Long addressId, Long userId);
    
    /**
     * 删除收货地址
     */
    boolean deleteShippingAddress(Long addressId, Long userId);
}

