package com.shopx.validation;

import com.shopx.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 * 数据校验工具类
 * 提供统一的数据校验方法
 */
@Slf4j
public class ValidationUtils {

    // 常用正则表达式
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^1[3-9]\\d{9}$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_]{3,20}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{6,20}$");

    /**
     * 校验非空
     */
    public static void notNull(Object obj, String message) {
        if (obj == null) {
            throw new BusinessException(400, message);
        }
    }

    /**
     * 校验非空字符串
     */
    public static void notBlank(String str, String message) {
        if (!StringUtils.hasText(str)) {
            throw new BusinessException(400, message);
        }
    }

    /**
     * 校验邮箱格式
     */
    public static void validEmail(String email, String message) {
        if (!StringUtils.hasText(email)) {
            throw new BusinessException(400, message);
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new BusinessException(400, message);
        }
    }

    /**
     * 校验手机号格式
     */
    public static void validPhone(String phone, String message) {
        if (!StringUtils.hasText(phone)) {
            throw new BusinessException(400, message);
        }
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new BusinessException(400, message);
        }
    }

    /**
     * 校验用户名格式
     */
    public static void validUsername(String username, String message) {
        if (!StringUtils.hasText(username)) {
            throw new BusinessException(400, message);
        }
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new BusinessException(400, message);
        }
    }

    /**
     * 校验密码格式
     */
    public static void validPassword(String password, String message) {
        if (!StringUtils.hasText(password)) {
            throw new BusinessException(400, message);
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new BusinessException(400, message);
        }
    }

    /**
     * 校验长度
     */
    public static void validLength(String str, int min, int max, String message) {
        if (!StringUtils.hasText(str)) {
            throw new BusinessException(400, message);
        }
        if (str.length() < min || str.length() > max) {
            throw new BusinessException(400, message);
        }
    }

    /**
     * 校验数值范围
     */
    public static void validRange(Number value, Number min, Number max, String message) {
        if (value == null) {
            throw new BusinessException(400, message);
        }
        double val = value.doubleValue();
        double minVal = min.doubleValue();
        double maxVal = max.doubleValue();
        if (val < minVal || val > maxVal) {
            throw new BusinessException(400, message);
        }
    }

    /**
     * 校验正整数
     */
    public static void validPositiveInteger(Integer value, String message) {
        if (value == null || value <= 0) {
            throw new BusinessException(400, message);
        }
    }

    /**
     * 校验正数
     */
    public static void validPositive(Number value, String message) {
        if (value == null || value.doubleValue() <= 0) {
            throw new BusinessException(400, message);
        }
    }

    /**
     * 校验非负数
     */
    public static void validNonNegative(Number value, String message) {
        if (value == null || value.doubleValue() < 0) {
            throw new BusinessException(400, message);
        }
    }

    /**
     * 校验ID
     */
    public static void validId(Long id, String message) {
        if (id == null || id <= 0) {
            throw new BusinessException(400, message);
        }
    }

    /**
     * 校验分页参数
     */
    public static void validPageParams(Integer page, Integer size, String message) {
        if (page == null || page < 1) {
            throw new BusinessException(400, "页码必须大于0");
        }
        if (size == null || size < 1 || size > 100) {
            throw new BusinessException(400, "每页大小必须在1-100之间");
        }
    }

    /**
     * 校验状态值
     */
    public static void validStatus(String status, String[] validStatuses, String message) {
        if (!StringUtils.hasText(status)) {
            throw new BusinessException(400, message);
        }
        for (String validStatus : validStatuses) {
            if (validStatus.equals(status)) {
                return;
            }
        }
        throw new BusinessException(400, message);
    }

    /**
     * 校验JSON格式
     */
    public static void validJson(String json, String message) {
        if (!StringUtils.hasText(json)) {
            throw new BusinessException(400, message);
        }
        try {
            com.alibaba.fastjson2.JSON.parse(json);
        } catch (Exception e) {
            throw new BusinessException(400, message);
        }
    }

    /**
     * 校验URL格式
     */
    public static void validUrl(String url, String message) {
        if (!StringUtils.hasText(url)) {
            throw new BusinessException(400, message);
        }
        try {
            new java.net.URL(url);
        } catch (Exception e) {
            throw new BusinessException(400, message);
        }
    }

    /**
     * 校验文件扩展名
     */
    public static void validFileExtension(String filename, String[] allowedExtensions, String message) {
        if (!StringUtils.hasText(filename)) {
            throw new BusinessException(400, message);
        }
        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        for (String allowedExt : allowedExtensions) {
            if (allowedExt.toLowerCase().equals(extension)) {
                return;
            }
        }
        throw new BusinessException(400, message);
    }

    /**
     * 校验业务规则
     */
    public static void validBusinessRule(boolean condition, String message) {
        if (!condition) {
            throw new BusinessException(400, message);
        }
    }

    /**
     * 校验权限
     */
    public static void validPermission(boolean hasPermission, String message) {
        if (!hasPermission) {
            throw new BusinessException(403, message);
        }
    }

    /**
     * 校验资源存在
     */
    public static void validResourceExists(Object resource, String message) {
        if (resource == null) {
            throw new BusinessException(404, message);
        }
    }

    /**
     * 校验操作权限
     */
    public static void validOperationPermission(boolean canOperate, String message) {
        if (!canOperate) {
            throw new BusinessException(403, message);
        }
    }
}
