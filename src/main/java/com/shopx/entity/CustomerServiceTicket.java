package com.shopx.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 客服工单实体
 */
@Data
@TableName("t_customer_service_ticket")
public class CustomerServiceTicket {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 工单号
     */
    private String ticketNo;
    
    /**
     * 工单类型：ORDER, PRODUCT, PAYMENT, REFUND, OTHER
     */
    private String ticketType;
    
    /**
     * 标题
     */
    private String title;
    
    /**
     * 内容
     */
    private String content;
    
    /**
     * 工单状态：PENDING, PROCESSING, RESOLVED, CLOSED
     */
    private String status;
    
    /**
     * 优先级：LOW, MEDIUM, HIGH, URGENT
     */
    private String priority;
    
    /**
     * 客服ID
     */
    private Long serviceStaffId;
    
    /**
     * 回复内容
     */
    private String reply;
    
    /**
     * 回复时间
     */
    private LocalDateTime replyTime;
    
    /**
     * 解决时间
     */
    private LocalDateTime resolvedTime;
    
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

