package com.shopx.dto;

import com.shopx.entity.Product;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 推荐结果响应DTO
 */
@Data
public class RecommendationResponseDTO {
    private String scenario;
    private String lifestyle;
    private List<Product> recommendedProducts;
    private List<Product> predictedProducts;
    private List<ProductWithExplanationDTO> productsWithExplanation;
    private Double confidence;
    private String algorithm;
    private LocalDateTime timestamp;
}

