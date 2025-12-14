package com.shopx.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 最近体验记录DTO
 */
@Data
public class RecentExperienceDTO {
    private Long productId;
    private String productName;
    private String experienceType;
    private LocalDateTime experienceTime;
}

