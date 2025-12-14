package com.shopx.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 搜索历史实体
 */
@Data
@TableName("t_search_history")
public class SearchHistory {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID（可为空，支持游客搜索）
     */
    private Long userId;
    
    /**
     * 搜索关键词
     */
    private String keyword;
    
    /**
     * 搜索类型：BASIC, ADVANCED
     */
    private String searchType;
    
    /**
     * 筛选条件（JSON格式）
     */
    private String filterConditions;
    
    /**
     * 搜索结果数量
     */
    private Integer resultCount;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

