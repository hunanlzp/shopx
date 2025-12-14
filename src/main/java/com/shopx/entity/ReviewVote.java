package com.shopx.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 评价投票实体
 */
@Data
@TableName("t_review_vote")
public class ReviewVote {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 评价ID
     */
    private Long reviewId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 投票类型：HELPFUL, NOT_HELPFUL
     */
    private String voteType;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

