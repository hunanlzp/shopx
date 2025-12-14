package com.shopx.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 支付方式实体
 */
@Data
@TableName("t_payment_method")
public class PaymentMethod {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 支付方式类型：ALIPAY, WECHAT, CREDIT_CARD
     */
    private String paymentType;
    
    /**
     * 支付方式名称
     */
    private String paymentName;
    
    /**
     * 支付账号（加密存储）
     */
    private String accountNumber;
    
    /**
     * 是否默认
     */
    private Boolean isDefault;
    
    /**
     * 是否启用
     */
    private Boolean enabled;
    
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

