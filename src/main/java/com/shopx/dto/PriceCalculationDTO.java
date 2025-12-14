package com.shopx.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 价格计算结果DTO
 */
@Data
public class PriceCalculationDTO {
    private BigDecimal productPrice;
    private Integer quantity;
    private BigDecimal subtotal;
    private BigDecimal shippingFee;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;
    private BigDecimal total;
}

