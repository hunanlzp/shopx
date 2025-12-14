package com.shopx.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 愿望清单实体
 */
@Data
@TableName("t_wishlist")
public class Wishlist {
    
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
     * 分类
     */
    private String category;
    
    /**
     * 价格下降提醒
     */
    private Boolean priceAlert;
    
    /**
     * 目标价格（价格提醒阈值）
     */
    private BigDecimal targetPrice;
    
    /**
     * 备注
     */
    private String notes;
    
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

