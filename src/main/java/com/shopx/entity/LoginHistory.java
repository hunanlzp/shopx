package com.shopx.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 登录历史实体
 */
@Data
@TableName("t_login_history")
public class LoginHistory {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 登录IP
     */
    private String ipAddress;
    
    /**
     * 登录设备
     */
    private String device;
    
    /**
     * 浏览器信息
     */
    private String browser;
    
    /**
     * 登录地点
     */
    private String location;
    
    /**
     * 登录状态：SUCCESS, FAILED
     */
    private String status;
    
    /**
     * 失败原因
     */
    private String failureReason;
    
    /**
     * 是否异常登录
     */
    private Boolean isAbnormal;
    
    /**
     * 异常原因
     */
    private String abnormalReason;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

