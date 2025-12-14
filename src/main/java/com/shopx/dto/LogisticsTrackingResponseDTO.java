package com.shopx.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 物流追踪响应DTO
 */
@Data
public class LogisticsTrackingResponseDTO {
    private Long id;
    private Long orderId;
    private String trackingNumber;
    private String carrier;
    private String status;
    private String currentLocation;
    private LocalDateTime estimatedDeliveryTime;
    private LocalDateTime actualDeliveryTime;
    private List<TrackingDetailDTO> details;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @Data
    public static class TrackingDetailDTO {
        private LocalDateTime timestamp;
        private String location;
        private String description;
        private String status;
    }
}

