package com.shopx.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 推荐反馈实体
 */
@Data
@TableName("t_recommendation_feedback")
public class RecommendationFeedback {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 推荐ID或商品ID
     */
    private Long productId;
    
    /**
     * 推荐算法
     */
    private String algorithm;
    
    /**
     * 反馈类型：LIKE, DISLIKE, IGNORE
     */
    private String feedbackType;
    
    /**
     * 反馈原因
     */
    private String reason;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

