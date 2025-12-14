package com.shopx.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 商品预订实体
 */
@Data
@TableName("t_product_reservation")
public class ProductReservation {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 商品ID
     */
    private Long productId;
    
    /**
     * 预订数量
     */
    private Integer quantity;
    
    /**
     * 预订状态：PENDING, FULFILLED, CANCELLED, EXPIRED
     */
    private String status;
    
    /**
     * 预计到货时间
     */
    private LocalDateTime expectedArrivalTime;
    
    /**
     * 实际到货时间
     */
    private LocalDateTime actualArrivalTime;
    
    /**
     * 过期时间
     */
    private LocalDateTime expireTime;
    
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

