package com.shopx.dto;

import lombok.Data;

/**
 * 最喜欢的商品DTO
 */
@Data
public class FavoriteProductDTO {
    private Long productId;
    private String productName;
    private Long experienceCount;
}

