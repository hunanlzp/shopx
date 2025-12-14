package com.shopx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体
 */
@Data
@TableName("t_order")
public class Order {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;
    
    /**
     * 订单状态：PENDING, PAID, SHIPPED, DELIVERED, CANCELLED
     */
    private String status;
    
    /**
     * 支付状态：UNPAID, PAID, REFUNDED
     */
    private String paymentStatus;
    
    /**
     * 收货地址
     */
    private String shippingAddress;
    
    /**
     * 配送方式：STANDARD, EXPRESS, PICKUP
     */
    private String shippingMethod;
    
    /**
     * 物流单号
     */
    private String trackingNumber;
    
    /**
     * 预计送达时间
     */
    private LocalDateTime estimatedDeliveryTime;
    
    /**
     * 退货状态：NONE, PENDING, PARTIAL, COMPLETED
     */
    private String returnStatus;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

