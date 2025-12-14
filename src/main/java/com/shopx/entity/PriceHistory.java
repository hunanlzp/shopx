package com.shopx.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 价格历史实体
 */
@Data
@TableName("t_price_history")
public class PriceHistory {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 商品ID
     */
    private Long productId;
    
    /**
     * 价格
     */
    private BigDecimal price;
    
    /**
     * 运费
     */
    private BigDecimal shippingFee;
    
    /**
     * 税费
     */
    private BigDecimal tax;
    
    /**
     * 总价
     */
    private BigDecimal totalPrice;
    
    /**
     * 价格变动原因
     */
    private String reason;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

