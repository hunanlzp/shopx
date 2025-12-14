package com.shopx.enums;

import com.shopx.constant.Constants;

/**
 * 退货订单状态枚举
 */
public enum ReturnOrderStatusEnum {
    PENDING(Constants.ReturnOrderStatus.PENDING, "待审核"),
    APPROVED(Constants.ReturnOrderStatus.APPROVED, "已批准"),
    REJECTED(Constants.ReturnOrderStatus.REJECTED, "已拒绝"),
    RETURNING(Constants.ReturnOrderStatus.RETURNING, "退货中"),
    RETURNED(Constants.ReturnOrderStatus.RETURNED, "已退货"),
    REFUNDED(Constants.ReturnOrderStatus.REFUNDED, "已退款"),
    CANCELLED(Constants.ReturnOrderStatus.CANCELLED, "已取消");

    private final String code;
    private final String description;

    ReturnOrderStatusEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static ReturnOrderStatusEnum fromCode(String code) {
        for (ReturnOrderStatusEnum status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown return order status: " + code);
    }
}

