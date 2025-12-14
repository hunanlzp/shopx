package com.shopx.enums;

import com.shopx.constant.Constants;

/**
 * 算法类型枚举
 */
public enum AlgorithmTypeEnum {
    HYBRID(Constants.AlgorithmType.HYBRID, "混合推荐"),
    COLLABORATIVE(Constants.AlgorithmType.COLLABORATIVE, "协同过滤"),
    CONTENT_BASED(Constants.AlgorithmType.CONTENT_BASED, "内容推荐");

    private final String code;
    private final String description;

    AlgorithmTypeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static AlgorithmTypeEnum fromCode(String code) {
        for (AlgorithmTypeEnum algorithm : values()) {
            if (algorithm.code.equals(code)) {
                return algorithm;
            }
        }
        // 默认返回混合推荐
        return HYBRID;
    }

    public static AlgorithmTypeEnum getDefault() {
        return HYBRID;
    }
}

