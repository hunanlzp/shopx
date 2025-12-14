package com.shopx.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 商品对比实体
 */
@Data
@TableName("t_product_comparison")
public class ProductComparison {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 对比列表名称
     */
    private String comparisonName;
    
    /**
     * 商品ID列表（JSON数组，最多5个）
     */
    private String productIds;
    
    /**
     * 是否公开（可分享）
     */
    private Boolean isPublic;
    
    /**
     * 分享链接
     */
    private String shareLink;
    
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

