package com.shopx.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 回收订单实体 - 价值循环系统
 */
@Data
@TableName("t_recycle_order")
public class RecycleOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    private Long productId;
    private String productName;      // 商品名称
    private Integer quantity;
    private BigDecimal estimatedValue;
    private BigDecimal finalValue;
    private BigDecimal actualValue;  // 实际价值
    private BigDecimal recyclePrice; // 回收价格
    
    private String status;           // 状态: PENDING, APPROVED, COMPLETED
    private String recycleType;      // 回收类型: RETURN, RESELL, DONATE
    
    private String inspectionResult; // 检测结果(JSON)
    private String environmentalImpact; // 环境影响数据
    private LocalDateTime pickupDate;    // 取件日期
    private LocalDateTime completionDate; // 完成日期
    private String notes;            // 备注
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}

