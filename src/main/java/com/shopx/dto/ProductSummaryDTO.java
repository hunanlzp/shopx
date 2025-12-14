package com.shopx.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品摘要DTO（用于列表展示）
 */
@Data
public class ProductSummaryDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private String image;
    private Integer stock;
    private String status;
    private Integer viewCount;
    private Integer likeCount;
    private Integer shareCount;
    private Boolean has3dPreview;
    private LocalDateTime createTime;
}

