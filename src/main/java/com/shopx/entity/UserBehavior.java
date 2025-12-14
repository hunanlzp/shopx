package com.shopx.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户行为实体
 */
@Data
@TableName("t_user_behavior")
public class UserBehavior {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    private Long productId;
    private String behaviorType; // VIEW, LIKE, SHARE, ADD_CART, PURCHASE
    
    private String sessionInfo; // JSON格式会话信息
    private String deviceInfo;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

