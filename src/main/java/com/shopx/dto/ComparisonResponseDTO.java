package com.shopx.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 对比响应DTO
 */
@Data
public class ComparisonResponseDTO {
    private Long id;
    private Long userId;
    private String comparisonName;
    private List<Long> productIds;
    private Boolean isPublic;
    private String shareLink;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

