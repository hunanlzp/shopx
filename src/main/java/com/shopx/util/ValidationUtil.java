package com.shopx.util;

import com.shopx.exception.BusinessException;
import com.shopx.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 * 参数验证工具类
 */
@Slf4j
public class ValidationUtil {
    
    // 邮箱正则表达式
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    // 手机号正则表达式
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^1[3-9]\\d{9}$"
    );
    
    // 用户名正则表达式（3-20位字母数字下划线）
    private static final Pattern USERNAME_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_]{3,20}$"
    );
    
    // 密码正则表达式（6-20位，包含字母和数字）
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{6,20}$"
    );
    
    /**
     * 验证字符串不为空
     */
    public static void validateNotEmpty(String value, String fieldName) {
        if (!StringUtils.hasText(value)) {
            throw new ValidationException(fieldName + "不能为空");
        }
    }
    
    /**
     * 验证字符串长度
     */
    public static void validateLength(String value, String fieldName, int minLength, int maxLength) {
        if (value == null) {
            throw new ValidationException(fieldName + "不能为空");
        }
        if (value.length() < minLength) {
            throw new ValidationException(fieldName + "长度不能少于" + minLength + "位");
        }
        if (value.length() > maxLength) {
            throw new ValidationException(fieldName + "长度不能超过" + maxLength + "位");
        }
    }
    
    /**
     * 验证邮箱格式
     */
    public static void validateEmail(String email) {
        if (!StringUtils.hasText(email)) {
            throw new ValidationException("邮箱不能为空");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException("邮箱格式不正确");
        }
    }
    
    /**
     * 验证手机号格式
     */
    public static void validatePhone(String phone) {
        if (!StringUtils.hasText(phone)) {
            throw new ValidationException("手机号不能为空");
        }
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new ValidationException("手机号格式不正确");
        }
    }
    
    /**
     * 验证用户名格式
     */
    public static void validateUsername(String username) {
        if (!StringUtils.hasText(username)) {
            throw new ValidationException("用户名不能为空");
        }
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new ValidationException("用户名格式不正确，应为3-20位字母、数字或下划线");
        }
    }
    
    /**
     * 验证密码格式
     */
    public static void validatePassword(String password) {
        if (!StringUtils.hasText(password)) {
            throw new ValidationException("密码不能为空");
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new ValidationException("密码格式不正确，应为6-20位，包含字母和数字");
        }
    }
    
    /**
     * 验证数字范围
     */
    public static void validateNumberRange(Number value, String fieldName, Number min, Number max) {
        if (value == null) {
            throw new ValidationException(fieldName + "不能为空");
        }
        double doubleValue = value.doubleValue();
        double minValue = min.doubleValue();
        double maxValue = max.doubleValue();
        
        if (doubleValue < minValue) {
            throw new ValidationException(fieldName + "不能小于" + minValue);
        }
        if (doubleValue > maxValue) {
            throw new ValidationException(fieldName + "不能大于" + maxValue);
        }
    }
    
    /**
     * 验证正整数
     */
    public static void validatePositiveInteger(Integer value, String fieldName) {
        if (value == null) {
            throw new ValidationException(fieldName + "不能为空");
        }
        if (value <= 0) {
            throw new ValidationException(fieldName + "必须为正整数");
        }
    }
    
    /**
     * 验证正数
     */
    public static void validatePositiveNumber(Number value, String fieldName) {
        if (value == null) {
            throw new ValidationException(fieldName + "不能为空");
        }
        if (value.doubleValue() <= 0) {
            throw new ValidationException(fieldName + "必须为正数");
        }
    }
    
    /**
     * 验证对象不为空
     */
    public static void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new ValidationException(fieldName + "不能为空");
        }
    }
    
    /**
     * 验证数组不为空
     */
    public static void validateArrayNotEmpty(Object[] array, String fieldName) {
        if (array == null || array.length == 0) {
            throw new ValidationException(fieldName + "不能为空");
        }
    }
    
    /**
     * 验证集合不为空
     */
    public static void validateCollectionNotEmpty(java.util.Collection<?> collection, String fieldName) {
        if (collection == null || collection.isEmpty()) {
            throw new ValidationException(fieldName + "不能为空");
        }
    }
    
    /**
     * 验证ID有效性
     */
    public static void validateId(Long id, String fieldName) {
        if (id == null) {
            throw new ValidationException(fieldName + "不能为空");
        }
        if (id <= 0) {
            throw new ValidationException(fieldName + "必须为正整数");
        }
    }
    
    /**
     * 验证分页参数
     */
    public static void validatePagination(Integer page, Integer size) {
        if (page == null || page < 1) {
            throw new ValidationException("页码必须大于0");
        }
        if (size == null || size < 1) {
            throw new ValidationException("每页大小必须大于0");
        }
        if (size > 100) {
            throw new ValidationException("每页大小不能超过100");
        }
    }
    
    /**
     * 验证状态值
     */
    public static void validateStatus(String status, String[] validStatuses, String fieldName) {
        if (!StringUtils.hasText(status)) {
            throw new ValidationException(fieldName + "不能为空");
        }
        
        boolean isValid = false;
        for (String validStatus : validStatuses) {
            if (validStatus.equals(status)) {
                isValid = true;
                break;
            }
        }
        
        if (!isValid) {
            throw new ValidationException(fieldName + "值不正确，有效值：" + String.join(", ", validStatuses));
        }
    }
    
    /**
     * 验证URL格式
     */
    public static void validateUrl(String url, String fieldName) {
        if (!StringUtils.hasText(url)) {
            throw new ValidationException(fieldName + "不能为空");
        }
        
        try {
            new java.net.URL(url);
        } catch (Exception e) {
            throw new ValidationException(fieldName + "格式不正确");
        }
    }
    
    /**
     * 验证日期格式
     */
    public static void validateDateFormat(String dateStr, String format, String fieldName) {
        if (!StringUtils.hasText(dateStr)) {
            throw new ValidationException(fieldName + "不能为空");
        }
        
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(format);
            sdf.setLenient(false);
            sdf.parse(dateStr);
        } catch (Exception e) {
            throw new ValidationException(fieldName + "格式不正确，应为：" + format);
        }
    }
}
