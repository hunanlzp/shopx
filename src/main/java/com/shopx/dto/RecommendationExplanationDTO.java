package com.shopx.dto;

import lombok.Data;

/**
 * 推荐解释DTO
 */
@Data
public class RecommendationExplanationDTO {
    private String reason;
    private Double confidence;
    private String algorithm;
    private String detail;
}

