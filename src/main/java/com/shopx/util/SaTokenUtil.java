package com.shopx.util;

import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Sa-Token工具类
 */
@Slf4j
public class SaTokenUtil {
    
    /**
     * 获取当前登录用户ID
     */
    public static Long getCurrentUserId() {
        try {
            return StpUtil.getLoginIdAsLong();
        } catch (Exception e) {
            log.warn("获取当前用户ID失败", e);
            return null;
        }
    }
    
    /**
     * 获取当前登录用户ID（字符串）
     */
    public static String getCurrentUserIdStr() {
        try {
            return StpUtil.getLoginIdAsString();
        } catch (Exception e) {
            log.warn("获取当前用户ID失败", e);
            return null;
        }
    }
    
    /**
     * 检查用户是否已登录
     */
    public static boolean isLogin() {
        return StpUtil.isLogin();
    }
    
    /**
     * 检查用户是否具有指定权限
     */
    public static boolean hasPermission(String permission) {
        try {
            return StpUtil.hasPermission(permission);
        } catch (Exception e) {
            log.warn("检查权限失败: {}", permission, e);
            return false;
        }
    }
    
    /**
     * 检查用户是否具有指定角色
     */
    public static boolean hasRole(String role) {
        try {
            return StpUtil.hasRole(role);
        } catch (Exception e) {
            log.warn("检查角色失败: {}", role, e);
            return false;
        }
    }
    
    /**
     * 检查用户是否具有任意一个权限
     */
    public static boolean hasAnyPermission(String... permissions) {
        try {
            return StpUtil.hasPermissionOr(permissions);
        } catch (Exception e) {
            log.warn("检查权限失败", e);
            return false;
        }
    }
    
    /**
     * 检查用户是否具有所有权限
     */
    public static boolean hasAllPermissions(String... permissions) {
        try {
            return StpUtil.hasPermissionAnd(permissions);
        } catch (Exception e) {
            log.warn("检查权限失败", e);
            return false;
        }
    }
    
    /**
     * 检查用户是否具有任意一个角色
     */
    public static boolean hasAnyRole(String... roles) {
        try {
            return StpUtil.hasRoleOr(roles);
        } catch (Exception e) {
            log.warn("检查角色失败", e);
            return false;
        }
    }
    
    /**
     * 检查用户是否具有所有角色
     */
    public static boolean hasAllRoles(String... roles) {
        try {
            return StpUtil.hasRoleAnd(roles);
        } catch (Exception e) {
            log.warn("检查角色失败", e);
            return false;
        }
    }
    
    /**
     * 获取用户权限列表
     */
    public static List<String> getPermissionList() {
        try {
            return StpUtil.getPermissionList();
        } catch (Exception e) {
            log.warn("获取权限列表失败", e);
            return List.of();
        }
    }
    
    /**
     * 获取用户角色列表
     */
    public static List<String> getRoleList() {
        try {
            return StpUtil.getRoleList();
        } catch (Exception e) {
            log.warn("获取角色列表失败", e);
            return List.of();
        }
    }
    
    /**
     * 获取当前Token值
     */
    public static String getTokenValue() {
        try {
            return StpUtil.getTokenValue();
        } catch (Exception e) {
            log.warn("获取Token值失败", e);
            return null;
        }
    }
    
    /**
     * 获取Token剩余有效期
     */
    public static long getTokenTimeout() {
        try {
            return StpUtil.getTokenTimeout();
        } catch (Exception e) {
            log.warn("获取Token有效期失败", e);
            return -1;
        }
    }
    
    /**
     * 刷新Token有效期
     */
    public static void refreshTimeout() {
        try {
            StpUtil.refreshTimeout();
        } catch (Exception e) {
            log.warn("刷新Token有效期失败", e);
        }
    }
    
    /**
     * 踢出指定用户
     */
    public static void kickout(Object loginId) {
        try {
            StpUtil.kickout(loginId);
        } catch (Exception e) {
            log.warn("踢出用户失败: {}", loginId, e);
        }
    }
    
    /**
     * 踢出指定用户（指定设备）
     */
    public static void kickout(Object loginId, String device) {
        try {
            StpUtil.kickout(loginId, device);
        } catch (Exception e) {
            log.warn("踢出用户失败: {}, device: {}", loginId, device, e);
        }
    }
    
    /**
     * 检查是否为管理员
     */
    public static boolean isAdmin() {
        return hasRole("ADMIN");
    }
    
    /**
     * 检查是否为商家
     */
    public static boolean isSeller() {
        return hasRole("SELLER");
    }
    
    /**
     * 检查是否为普通用户
     */
    public static boolean isUser() {
        return hasRole("USER");
    }
    
    /**
     * 检查是否有商品管理权限
     */
    public static boolean canManageProducts() {
        return hasAnyPermission("product:add", "product:update", "product:delete");
    }
    
    /**
     * 检查是否有用户管理权限
     */
    public static boolean canManageUsers() {
        return hasAnyPermission("user:add", "user:update", "user:delete");
    }
    
    /**
     * 检查是否有订单管理权限
     */
    public static boolean canManageOrders() {
        return hasAnyPermission("order:update", "order:delete");
    }
    
    /**
     * 检查是否有回收管理权限
     */
    public static boolean canManageRecycle() {
        return hasAnyPermission("recycle:update", "recycle:delete");
    }
}
