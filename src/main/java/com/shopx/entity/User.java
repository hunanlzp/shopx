package com.shopx.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户实体 - 支持个性化推荐和社交互动
 */
@Data
@TableName("t_user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String username;
    private String password;
    private String email;
    private String phone;
    
    // 用户角色和权限
    private String role; // USER, SELLER, ADMIN
    private String avatar; // 头像URL
    private Boolean enabled; // 是否启用
    
    // 个性化偏好
    private String preferences; // JSON格式存储用户偏好
    private String lifestyle;   // 生活方式标签
    private String scenarios;   // 常用场景
    
    // 社交属性
    private Integer followerCount;
    private Integer followingCount;
    private String socialProfile; // 社交资料
    
    // 价值循环相关
    private Integer sustainabilityScore; // 可持续消费评分
    private Integer recycleCount;        // 回收次数
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @Version
    private Integer version;
}
