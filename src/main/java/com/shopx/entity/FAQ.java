package com.shopx.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 常见问题实体
 */
@Data
@TableName("t_faq")
public class FAQ {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 问题分类
     */
    private String category;
    
    /**
     * 问题
     */
    private String question;
    
    /**
     * 答案
     */
    private String answer;
    
    /**
     * 排序顺序
     */
    private Integer sortOrder;
    
    /**
     * 浏览次数
     */
    private Integer viewCount;
    
    /**
     * 有用数
     */
    private Integer helpfulCount;
    
    /**
     * 是否启用
     */
    private Boolean enabled;
    
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

