package com.shopx.util;

import jakarta.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

/**
 * 设备检测工具类
 * 用于检测用户设备类型（移动端、桌面端等）
 */
public class DeviceUtil {
    
    private static final Pattern MOBILE_PATTERN = Pattern.compile(
        "Mobile|Android|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini",
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern TABLET_PATTERN = Pattern.compile(
        "iPad|Android(?!.*Mobile)|Tablet",
        Pattern.CASE_INSENSITIVE
    );
    
    /**
     * 检测是否为移动设备
     */
    public static boolean isMobileDevice(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) {
            return false;
        }
        return MOBILE_PATTERN.matcher(userAgent).find();
    }
    
    /**
     * 检测是否为平板设备
     */
    public static boolean isTabletDevice(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) {
            return false;
        }
        return TABLET_PATTERN.matcher(userAgent).find();
    }
    
    /**
     * 检测是否为桌面设备
     */
    public static boolean isDesktopDevice(String userAgent) {
        return !isMobileDevice(userAgent) && !isTabletDevice(userAgent);
    }
    
    /**
     * 从HttpServletRequest获取设备类型
     */
    public static String getDeviceType(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (isTabletDevice(userAgent)) {
            return "TABLET";
        } else if (isMobileDevice(userAgent)) {
            return "MOBILE";
        } else {
            return "DESKTOP";
        }
    }
    
    /**
     * 获取浏览器信息
     */
    public static String getBrowser(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) {
            return "Unknown";
        }
        
        if (userAgent.contains("Chrome")) {
            return "Chrome";
        } else if (userAgent.contains("Firefox")) {
            return "Firefox";
        } else if (userAgent.contains("Safari") && !userAgent.contains("Chrome")) {
            return "Safari";
        } else if (userAgent.contains("Edge")) {
            return "Edge";
        } else if (userAgent.contains("Opera")) {
            return "Opera";
        } else {
            return "Unknown";
        }
    }
    
    /**
     * 获取操作系统信息
     */
    public static String getOS(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) {
            return "Unknown";
        }
        
        if (userAgent.contains("Windows")) {
            return "Windows";
        } else if (userAgent.contains("Mac")) {
            return "macOS";
        } else if (userAgent.contains("Linux")) {
            return "Linux";
        } else if (userAgent.contains("Android")) {
            return "Android";
        } else if (userAgent.contains("iPhone") || userAgent.contains("iPad")) {
            return "iOS";
        } else {
            return "Unknown";
        }
    }
    
    /**
     * 获取客户端IP地址
     */
    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        // 处理多个IP的情况，取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip;
    }
}

