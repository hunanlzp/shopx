package com.shopx.util;

import com.shopx.entity.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * 响应工具类
 * 简化Controller层返回ApiResponse的ResponseEntity构建
 */
@Slf4j
public class ResponseUtil {

    private ResponseUtil() {
        // 私有构造函数，防止实例化
    }

    /**
     * 构建成功响应
     * @param data 业务数据
     * @param <T> 数据类型
     * @return ResponseEntity
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(T data) {
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    /**
     * 构建成功响应
     * @param message 消息
     * @param data 业务数据
     * @param <T> 数据类型
     * @return ResponseEntity
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(String message, T data) {
        return ResponseEntity.ok(ApiResponse.success(message, data));
    }

    /**
     * 构建成功响应（无数据）
     * @param message 消息
     * @param <T> 数据类型
     * @return ResponseEntity
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(String message) {
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    /**
     * 构建分页成功响应
     * @param data 业务数据
     * @param total 总数
     * @param page 页码
     * @param size 每页大小
     * @param <T> 数据类型
     * @return ResponseEntity
     */
    public static <T> ResponseEntity<ApiResponse<PageResult<T>>> successPage(List<T> data, long total, int page, int size) {
        PageResult<T> pageResult = PageResult.<T>builder()
                .data(data)
                .total(total)
                .page(page)
                .size(size)
                .totalPages((int) Math.ceil((double) total / size))
                .build();
        return ResponseEntity.ok(ApiResponse.success("查询成功", pageResult));
    }

    /**
     * 构建错误响应
     * @param code 错误码
     * @param message 错误消息
     * @param <T> 数据类型
     * @return ResponseEntity
     */
    public static <T> ResponseEntity<ApiResponse<T>> error(int code, String message) {
        return ResponseEntity
                .status(HttpStatus.OK) // 业务错误也可能返回200，具体看业务约定
                .body(ApiResponse.error(code, message));
    }

    /**
     * 构建错误响应（默认500）
     * @param message 错误消息
     * @param <T> 数据类型
     * @return ResponseEntity
     */
    public static <T> ResponseEntity<ApiResponse<T>> error(String message) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(message));
    }

    /**
     * 构建客户端请求错误响应（400）
     * @param message 错误消息
     * @param <T> 数据类型
     * @return ResponseEntity
     */
    public static <T> ResponseEntity<ApiResponse<T>> badRequest(String message) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.badRequest(message));
    }

    /**
     * 构建未授权响应（401）
     * @param message 错误消息
     * @param <T> 数据类型
     * @return ResponseEntity
     */
    public static <T> ResponseEntity<ApiResponse<T>> unauthorized(String message) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.unauthorized(message));
    }

    /**
     * 构建禁止访问响应（403）
     * @param message 错误消息
     * @param <T> 数据类型
     * @return ResponseEntity
     */
    public static <T> ResponseEntity<ApiResponse<T>> forbidden(String message) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.forbidden(message));
    }

    /**
     * 构建未找到资源响应（404）
     * @param message 错误消息
     * @param <T> 数据类型
     * @return ResponseEntity
     */
    public static <T> ResponseEntity<ApiResponse<T>> notFound(String message) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.notFound(message));
    }

    /**
     * 构建方法不允许响应（405）
     * @param message 错误消息
     * @param <T> 数据类型
     * @return ResponseEntity
     */
    public static <T> ResponseEntity<ApiResponse<T>> methodNotAllowed(String message) {
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.error(HttpStatus.METHOD_NOT_ALLOWED.value(), message));
    }

    /**
     * 构建冲突响应（409）
     * @param message 错误消息
     * @param <T> 数据类型
     * @return ResponseEntity
     */
    public static <T> ResponseEntity<ApiResponse<T>> conflict(String message) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(HttpStatus.CONFLICT.value(), message));
    }

    /**
     * 构建服务器错误响应（500）
     * @param message 错误消息
     * @param <T> 数据类型
     * @return ResponseEntity
     */
    public static <T> ResponseEntity<ApiResponse<T>> serverError(String message) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), message));
    }

    /**
     * 构建服务不可用响应（503）
     * @param message 错误消息
     * @param <T> 数据类型
     * @return ResponseEntity
     */
    public static <T> ResponseEntity<ApiResponse<T>> serviceUnavailable(String message) {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.error(HttpStatus.SERVICE_UNAVAILABLE.value(), message));
    }

    /**
     * 分页结果实体
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class PageResult<T> {
        private List<T> data;
        private long total;
        private int page;
        private int size;
        private int totalPages;
        private boolean hasNext;
        private boolean hasPrevious;

        public boolean isHasNext() {
            return page < totalPages;
        }

        public boolean isHasPrevious() {
            return page > 1;
        }
    }
}