package com.shopx.enums;

import com.shopx.constant.Constants;

/**
 * 工单状态枚举
 */
public enum TicketStatusEnum {
    OPEN(Constants.TicketStatus.OPEN, "待处理"),
    IN_PROGRESS(Constants.TicketStatus.IN_PROGRESS, "处理中"),
    RESOLVED(Constants.TicketStatus.RESOLVED, "已解决"),
    CLOSED(Constants.TicketStatus.CLOSED, "已关闭");

    private final String code;
    private final String description;

    TicketStatusEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static TicketStatusEnum fromCode(String code) {
        for (TicketStatusEnum status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown ticket status: " + code);
    }
}

