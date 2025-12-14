package com.shopx.dto;

import lombok.Data;

import java.util.List;

/**
 * AR/VR统计数据DTO
 */
@Data
public class ARVRStatsDTO {
    private Long totalARViews;
    private Long totalVRViews;
    private Long totalInteractions;
    private List<FavoriteProductDTO> favoriteProducts;
    private List<RecentExperienceDTO> recentExperiences;
}

