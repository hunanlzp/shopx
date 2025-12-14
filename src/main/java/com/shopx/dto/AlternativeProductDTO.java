package com.shopx.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 替代商品DTO
 */
@Data
public class AlternativeProductDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private String image;
    private Integer stock;
    private String reason;
}

