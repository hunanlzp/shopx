package com.shopx.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 游客会话实体
 */
@Data
@TableName("t_guest_session")
public class GuestSession {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 会话ID（UUID）
     */
    private String sessionId;
    
    /**
     * 购物车数据（JSON格式）
     */
    private String cartData;
    
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

