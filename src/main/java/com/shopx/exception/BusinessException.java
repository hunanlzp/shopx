package com.shopx.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 业务异常基类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException {
    
    /**
     * 错误码
     */
    private Integer code;
    
    /**
     * 错误消息
     */
    private String message;
    
    /**
     * 错误详情
     */
    private Object details;
    
    public BusinessException(String message) {
        super(message);
        this.code = 500;
        this.message = message;
    }
    
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
    
    public BusinessException(Integer code, String message, Object details) {
        super(message);
        this.code = code;
        this.message = message;
        this.details = details;
    }
}

/**
 * 用户相关异常
 */
@Data
@EqualsAndHashCode(callSuper = true)
class UserException extends BusinessException {
    
    public UserException(String message) {
        super(400, message);
    }
    
    public UserException(Integer code, String message) {
        super(code, message);
    }
}

/**
 * 商品相关异常
 */
@Data
@EqualsAndHashCode(callSuper = true)
class ProductException extends BusinessException {
    
    public ProductException(String message) {
        super(400, message);
    }
    
    public ProductException(Integer code, String message) {
        super(code, message);
    }
}

/**
 * 订单相关异常
 */
@Data
@EqualsAndHashCode(callSuper = true)
class OrderException extends BusinessException {
    
    public OrderException(String message) {
        super(400, message);
    }
    
    public OrderException(Integer code, String message) {
        super(code, message);
    }
}

/**
 * 认证相关异常
 */
@Data
@EqualsAndHashCode(callSuper = true)
class AuthException extends BusinessException {
    
    public AuthException(String message) {
        super(401, message);
    }
    
    public AuthException(Integer code, String message) {
        super(code, message);
    }
}

/**
 * 权限相关异常
 */
@Data
@EqualsAndHashCode(callSuper = true)
class PermissionException extends BusinessException {
    
    public PermissionException(String message) {
        super(403, message);
    }
    
    public PermissionException(Integer code, String message) {
        super(code, message);
    }
}

/**
 * 资源不存在异常
 */
@Data
@EqualsAndHashCode(callSuper = true)
class ResourceNotFoundException extends BusinessException {
    
    public ResourceNotFoundException(String message) {
        super(404, message);
    }
    
    public ResourceNotFoundException(Integer code, String message) {
        super(code, message);
    }
}

/**
 * 参数验证异常
 */
@Data
@EqualsAndHashCode(callSuper = true)
class ValidationException extends BusinessException {
    
    public ValidationException(String message) {
        super(400, message);
    }
    
    public ValidationException(Integer code, String message) {
        super(code, message);
    }
    
    public ValidationException(String message, Object details) {
        super(400, message, details);
    }
}

/**
 * 系统异常
 */
@Data
@EqualsAndHashCode(callSuper = true)
class SystemException extends BusinessException {
    
    public SystemException(String message) {
        super(500, message);
    }
    
    public SystemException(Integer code, String message) {
        super(code, message);
    }
    
    public SystemException(String message, Throwable cause) {
        super(500, message);
        initCause(cause);
    }
}
