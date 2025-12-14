package com.shopx.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 价格保护实体
 */
@Data
@TableName("t_price_protection")
public class PriceProtection {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 商品ID
     */
    private Long productId;
    
    /**
     * 购买价格
     */
    private BigDecimal purchasePrice;
    
    /**
     * 当前价格
     */
    private BigDecimal currentPrice;
    
    /**
     * 差价
     */
    private BigDecimal priceDifference;
    
    /**
     * 保护状态：ACTIVE, EXPIRED, REFUNDED
     */
    private String status;
    
    /**
     * 保护开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 保护结束时间（7天后）
     */
    private LocalDateTime endTime;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

