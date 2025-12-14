package com.shopx.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 商品审核实体
 */
@Data
@TableName("t_product_audit")
public class ProductAudit {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 商品ID
     */
    private Long productId;
    
    /**
     * 审核状态：PENDING, APPROVED, REJECTED
     */
    private String status;
    
    /**
     * 审核人ID
     */
    private Long auditorId;
    
    /**
     * 审核意见
     */
    private String comment;
    
    /**
     * 完整度评分（0-100）
     */
    private Integer completenessScore;
    
    /**
     * 审核时间
     */
    private LocalDateTime auditTime;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

