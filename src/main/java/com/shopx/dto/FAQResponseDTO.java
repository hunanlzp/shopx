package com.shopx.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * FAQ响应DTO
 */
@Data
public class FAQResponseDTO {
    private Long id;
    private String category;
    private String question;
    private String answer;
    private Integer helpfulCount;
    private Integer viewCount;
    private Boolean isPublished;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

