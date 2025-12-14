package com.shopx.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 物流追踪实体
 */
@Data
@TableName("t_logistics_tracking")
public class LogisticsTracking {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 物流单号
     */
    private String trackingNumber;
    
    /**
     * 物流公司
     */
    private String logisticsCompany;
    
    /**
     * 物流状态
     */
    private String status;
    
    /**
     * 当前位置
     */
    private String currentLocation;
    
    /**
     * 详细信息（JSON格式）
     */
    private String details;
    
    /**
     * 预计送达时间
     */
    private LocalDateTime estimatedDeliveryTime;
    
    /**
     * 实际送达时间
     */
    private LocalDateTime actualDeliveryTime;
    
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

