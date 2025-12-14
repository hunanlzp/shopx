package com.shopx.enums;

import com.shopx.constant.Constants;

/**
 * 工单优先级枚举
 */
public enum TicketPriorityEnum {
    LOW(Constants.TicketPriority.LOW, "低"),
    MEDIUM(Constants.TicketPriority.MEDIUM, "中"),
    HIGH(Constants.TicketPriority.HIGH, "高"),
    URGENT(Constants.TicketPriority.URGENT, "紧急");

    private final String code;
    private final String description;

    TicketPriorityEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static TicketPriorityEnum fromCode(String code) {
        for (TicketPriorityEnum priority : values()) {
            if (priority.code.equals(code)) {
                return priority;
            }
        }
        throw new IllegalArgumentException("Unknown ticket priority: " + code);
    }
}

