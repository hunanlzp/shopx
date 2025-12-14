package com.shopx.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import com.shopx.entity.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Sa-Token异常处理器
 */
@Slf4j
@ControllerAdvice
public class SaTokenExceptionHandler {

    /**
     * 处理未登录异常
     */
    @ExceptionHandler(NotLoginException.class)
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> handleNotLoginException(NotLoginException ex) {
        log.warn("用户未登录: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.unauthorized("请先登录"));
    }

    /**
     * 处理权限不足异常
     */
    @ExceptionHandler(NotPermissionException.class)
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> handleNotPermissionException(NotPermissionException ex) {
        log.warn("权限不足: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.forbidden("权限不足，无法访问"));
    }

    /**
     * 处理角色不足异常
     */
    @ExceptionHandler(NotRoleException.class)
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> handleNotRoleException(NotRoleException ex) {
        log.warn("角色不足: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.forbidden("角色权限不足，无法访问"));
    }
}
