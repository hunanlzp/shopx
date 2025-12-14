package com.shopx.enums;

import com.shopx.constant.Constants;

/**
 * 物流状态枚举
 */
public enum LogisticsStatusEnum {
    PENDING(Constants.LogisticsStatus.PENDING, "待发货"),
    PICKED_UP(Constants.LogisticsStatus.PICKED_UP, "已揽收"),
    IN_TRANSIT(Constants.LogisticsStatus.IN_TRANSIT, "运输中"),
    OUT_FOR_DELIVERY(Constants.LogisticsStatus.OUT_FOR_DELIVERY, "派送中"),
    DELIVERED(Constants.LogisticsStatus.DELIVERED, "已送达"),
    EXCEPTION(Constants.LogisticsStatus.EXCEPTION, "异常");

    private final String code;
    private final String description;

    LogisticsStatusEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static LogisticsStatusEnum fromCode(String code) {
        for (LogisticsStatusEnum status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown logistics status: " + code);
    }
}

