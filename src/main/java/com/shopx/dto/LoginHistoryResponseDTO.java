package com.shopx.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录历史响应DTO
 */
@Data
public class LoginHistoryResponseDTO {
    private Long id;
    private Long userId;
    private String ipAddress;
    private String location;
    private String device;
    private String browser;
    private String os;
    private LocalDateTime loginTime;
    private Boolean isAbnormal;
    private String status;
}

