package com.shopx.dto;

import com.shopx.entity.Product;
import lombok.Data;

/**
 * 带解释的商品DTO
 */
@Data
public class ProductWithExplanationDTO {
    private Product product;
    private RecommendationExplanationDTO explanation;
}

