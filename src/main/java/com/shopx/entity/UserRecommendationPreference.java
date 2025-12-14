package com.shopx.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户推荐偏好实体
 */
@Data
@TableName("t_user_recommendation_preference")
public class UserRecommendationPreference {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 偏好设置（JSON格式）
     */
    private String preferences;
    
    /**
     * 是否过滤已购买商品
     */
    private Boolean filterPurchased;
    
    /**
     * 是否过滤已评价商品
     */
    private Boolean filterReviewed;
    
    /**
     * 推荐算法偏好权重（JSON格式）
     */
    private String algorithmWeights;
    
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

