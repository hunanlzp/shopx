package com.shopx.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 商品信息修改历史实体
 */
@Data
@TableName("t_product_history")
public class ProductHistory {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 商品ID
     */
    private Long productId;
    
    /**
     * 修改人ID
     */
    private Long modifiedBy;
    
    /**
     * 修改字段（JSON格式，记录修改前后的值）
     */
    private String changes;
    
    /**
     * 修改原因
     */
    private String reason;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

