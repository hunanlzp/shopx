package com.shopx.enums;

import com.shopx.constant.Constants;

/**
 * AI提供商枚举
 */
public enum AIProviderEnum {
    OPENAI(Constants.AIProvider.OPENAI, "OpenAI"),
    CLAUDE(Constants.AIProvider.CLAUDE, "Claude"),
    CUSTOM(Constants.AIProvider.CUSTOM, "自定义");

    private final String code;
    private final String description;

    AIProviderEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static AIProviderEnum fromCode(String code) {
        if (code == null) {
            return getDefault();
        }
        String lowerCode = code.toLowerCase();
        for (AIProviderEnum provider : values()) {
            if (provider.code.equals(lowerCode)) {
                return provider;
            }
        }
        // 默认返回OpenAI
        return OPENAI;
    }

    public static AIProviderEnum getDefault() {
        return OPENAI;
    }
}

