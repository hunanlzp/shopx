package com.shopx.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 退货订单实体
 */
@Data
@TableName("t_return_order")
public class ReturnOrder {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 原订单ID
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
     * 退货数量
     */
    private Integer quantity;
    
    /**
     * 退货原因
     */
    private String reason;
    
    /**
     * 退货说明
     */
    private String description;
    
    /**
     * 退货金额
     */
    private BigDecimal refundAmount;
    
    /**
     * 退货状态：PENDING, APPROVED, REJECTED, PROCESSING, COMPLETED, CANCELLED
     */
    private String status;
    
    /**
     * 退货标签（打印或电子标签）
     */
    private String returnLabel;
    
    /**
     * 物流单号（退货物流）
     */
    private String returnTrackingNumber;
    
    /**
     * 审核意见
     */
    private String auditComment;
    
    /**
     * 审核时间
     */
    private LocalDateTime auditTime;
    
    /**
     * 退款时间
     */
    private LocalDateTime refundTime;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}

