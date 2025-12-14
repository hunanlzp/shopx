package com.shopx.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 双因素认证实体
 */
@Data
@TableName("t_two_factor_auth")
public class TwoFactorAuth {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 是否启用
     */
    private Boolean enabled;
    
    /**
     * 认证方式：SMS, EMAIL, APP
     */
    private String authMethod;
    
    /**
     * 密钥（用于APP认证）
     */
    private String secretKey;
    
    /**
     * 手机号（用于SMS认证）
     */
    private String phoneNumber;
    
    /**
     * 邮箱（用于EMAIL认证）
     */
    private String email;
    
    /**
     * 备用验证码
     */
    private String backupCodes;
    
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

