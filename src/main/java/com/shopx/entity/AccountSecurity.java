package com.shopx.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 账户安全设置实体
 */
@Data
@TableName("t_account_security")
public class AccountSecurity {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 是否启用登录提醒
     */
    private Boolean loginAlertEnabled;
    
    /**
     * 是否启用异常登录提醒
     */
    private Boolean abnormalLoginAlertEnabled;
    
    /**
     * 是否启用密码修改提醒
     */
    private Boolean passwordChangeAlertEnabled;
    
    /**
     * 是否启用账户删除提醒
     */
    private Boolean accountDeletionAlertEnabled;
    
    /**
     * 隐私设置（JSON格式）
     */
    private String privacySettings;
    
    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;
    
    /**
     * 最后登录IP
     */
    private String lastLoginIp;
    
    /**
     * 账户状态：ACTIVE, SUSPENDED, DELETED
     */
    private String accountStatus;
    
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

