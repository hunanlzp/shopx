package com.shopx.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 保存的筛选条件实体
 */
@Data
@TableName("t_saved_filter")
public class SavedFilter {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 筛选条件名称
     */
    private String filterName;
    
    /**
     * 筛选条件（JSON格式）
     */
    private String filterConditions;
    
    /**
     * 是否默认
     */
    private Boolean isDefault;
    
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

