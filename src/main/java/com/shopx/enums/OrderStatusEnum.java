package com.shopx.enums;

import com.shopx.constant.Constants;

/**
 * 订单状态枚举
 */
public enum OrderStatusEnum {
    PENDING(Constants.OrderStatus.PENDING, "待处理"),
    CONFIRMED(Constants.OrderStatus.CONFIRMED, "已确认"),
    SHIPPED(Constants.OrderStatus.SHIPPED, "已发货"),
    DELIVERED(Constants.OrderStatus.DELIVERED, "已送达"),
    CANCELLED(Constants.OrderStatus.CANCELLED, "已取消"),
    REFUNDED(Constants.OrderStatus.REFUNDED, "已退款");

    private final String code;
    private final String description;

    OrderStatusEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static OrderStatusEnum fromCode(String code) {
        for (OrderStatusEnum status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown order status: " + code);
    }
}

