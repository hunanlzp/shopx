package com.shopx.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 商品评价实体
 */
@Data
@TableName("t_product_review")
public class ProductReview {
    
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
     * 订单ID（用于验证是否购买）
     */
    private Long orderId;
    
    /**
     * 评分（1-5）
     */
    private Integer rating;
    
    /**
     * 评价内容
     */
    private String content;
    
    /**
     * 评价图片（JSON数组）
     */
    private String images;
    
    /**
     * 评价视频（JSON数组）
     */
    private String videos;
    
    /**
     * 有用数
     */
    private Integer helpfulCount;
    
    /**
     * 是否已购买验证
     */
    private Boolean isVerified;
    
    /**
     * 商家回复
     */
    private String merchantReply;
    
    /**
     * 商家回复时间
     */
    private LocalDateTime replyTime;
    
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

