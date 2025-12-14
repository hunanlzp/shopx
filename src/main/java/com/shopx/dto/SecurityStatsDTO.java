package com.shopx.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 安全统计DTO
 */
@Data
public class SecurityStatsDTO {
    private Integer totalLogins;
    private Integer successfulLogins;
    private Integer failedLogins;
    private Integer abnormalLogins;
    private LocalDateTime lastLoginTime;
    private Boolean twoFactorEnabled;
    private LocalDateTime passwordLastChanged;
}

