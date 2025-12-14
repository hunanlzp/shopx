package com.shopx.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 价格保护响应DTO
 */
@Data
public class PriceProtectionResponseDTO {
    private Long id;
    private Long orderId;
    private Long userId;
    private BigDecimal originalPrice;
    private BigDecimal currentPrice;
    private BigDecimal refundAmount;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

