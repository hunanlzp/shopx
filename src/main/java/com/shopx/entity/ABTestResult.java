package com.shopx.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * A/B测试结果实体
 */
@Data
@TableName("t_ab_test_result")
public class ABTestResult {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long testId;
    private Long userId;
    private String algorithm; // 使用的算法
    private Long productId;
    private String actionType; // VIEW, CLICK, PURCHASE等
    private BigDecimal actionValue; // 行为价值
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

