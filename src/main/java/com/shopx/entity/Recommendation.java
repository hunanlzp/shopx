package com.shopx.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AI推荐记录实体
 */
@Data
@TableName("t_recommendation")
public class Recommendation {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    private Long productId;
    private String scenario;         // 推荐场景
    private String reason;           // 推荐理由
    private BigDecimal score;        // 推荐分数
    
    // AI模型信息
    private String modelVersion;
    private String features;         // 特征向量(JSON)
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

