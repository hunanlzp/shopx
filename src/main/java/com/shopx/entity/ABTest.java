package com.shopx.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * A/B测试实体
 */
@Data
@TableName("t_ab_test")
public class ABTest {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String testName;
    private String description;
    private String algorithmA;
    private String algorithmB;
    private Double trafficSplit; // A:B流量分配比例
    private String status; // ACTIVE, PAUSED, COMPLETED
    
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    
    private String metricsA; // JSON格式
    private String metricsB; // JSON格式
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}

