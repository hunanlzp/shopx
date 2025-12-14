package com.shopx.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 配送选项DTO
 */
@Data
public class ShippingOptionDTO {
    private String method;
    private String name;
    private Integer estimatedDays;
    private BigDecimal fee;
    private String description;
}

