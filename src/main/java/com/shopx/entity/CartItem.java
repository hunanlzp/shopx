package com.shopx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购物车项实体
 */
@Data
@TableName("cart_item")
public class CartItem {
    
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
     * 商品名称（冗余字段，提高查询效率）
     */
    private String productName;
    
    /**
     * 商品图片（冗余字段）
     */
    private String productImage;
    
    /**
     * 商品单价
     */
    private BigDecimal price;
    
    /**
     * 数量
     */
    private Integer quantity;
    
    /**
     * 小计
     */
    private BigDecimal subtotal;
    
    /**
     * 状态：VALID, OUT_OF_STOCK, PRICE_CHANGED, DISABLED
     */
    private String status;
    
    /**
     * 最后检查时间
     */
    private LocalDateTime lastCheckedTime;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

