package com.shopx.controller;

import com.shopx.annotation.ApiVersion;
import com.shopx.entity.ApiResponse;
import com.shopx.util.DeviceUtil;
import com.shopx.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 移动端支持控制器
 * 提供移动端特定的API和配置
 */
@Slf4j
@RestController
@RequestMapping("/mobile")
@ApiVersion("v1")
@Tag(name = "移动端支持", description = "移动端体验优化相关API")
public class MobileController {
    
    /**
     * 获取设备信息
     */
    @Operation(summary = "获取设备信息", description = "检测并返回当前设备的类型和浏览器信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @GetMapping("/device-info")
    public ResponseEntity<ApiResponse<Map<String, String>>> getDeviceInfo(HttpServletRequest request) {
        
        try {
            Map<String, String> deviceInfo = new HashMap<>();
            deviceInfo.put("deviceType", DeviceUtil.getDeviceType(request));
            deviceInfo.put("browser", DeviceUtil.getBrowser(request));
            deviceInfo.put("os", DeviceUtil.getOS(request));
            deviceInfo.put("isMobile", String.valueOf(DeviceUtil.isMobileDevice(request.getHeader("User-Agent"))));
            deviceInfo.put("isTablet", String.valueOf(DeviceUtil.isTabletDevice(request.getHeader("User-Agent"))));
            
            return ResponseUtil.success("查询成功", deviceInfo);
        } catch (Exception e) {
            log.error("获取设备信息失败", e);
            return ResponseUtil.error("获取设备信息失败，请稍后重试");
        }
    }
    
    /**
     * 获取移动端配置
     */
    @Operation(summary = "获取移动端配置", description = "获取移动端特定的配置信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @GetMapping("/config")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMobileConfig() {
        
        try {
            Map<String, Object> config = new HashMap<>();
            
            // 图片配置
            Map<String, Object> imageConfig = new HashMap<>();
            imageConfig.put("lazyLoad", true);
            imageConfig.put("compression", true);
            imageConfig.put("maxWidth", 800);
            imageConfig.put("maxHeight", 800);
            imageConfig.put("quality", 0.8);
            config.put("image", imageConfig);
            
            // 性能配置
            Map<String, Object> performanceConfig = new HashMap<>();
            performanceConfig.put("enableCache", true);
            performanceConfig.put("enablePrefetch", true);
            performanceConfig.put("enableLazyLoad", true);
            config.put("performance", performanceConfig);
            
            // PWA配置
            Map<String, Object> pwaConfig = new HashMap<>();
            pwaConfig.put("enabled", true);
            pwaConfig.put("offlineSupport", true);
            pwaConfig.put("pushNotification", true);
            config.put("pwa", pwaConfig);
            
            // UI配置
            Map<String, Object> uiConfig = new HashMap<>();
            uiConfig.put("touchFriendly", true);
            uiConfig.put("responsive", true);
            uiConfig.put("minTouchTarget", 44); // 最小触摸目标尺寸（像素）
            config.put("ui", uiConfig);
            
            return ResponseUtil.success("查询成功", config);
        } catch (Exception e) {
            log.error("获取移动端配置失败", e);
            return ResponseUtil.error("获取移动端配置失败，请稍后重试");
        }
    }
    
    /**
     * 检查PWA更新
     */
    @Operation(summary = "检查PWA更新", description = "检查PWA应用是否有更新")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @GetMapping("/pwa/check-update")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkPWAUpdate(
            @RequestParam(required = false) String currentVersion) {
        
        try {
            Map<String, Object> updateInfo = new HashMap<>();
            
            // 这里应该检查实际的应用版本
            String latestVersion = "1.0.0"; // 从配置或数据库获取
            boolean hasUpdate = !latestVersion.equals(currentVersion);
            
            updateInfo.put("hasUpdate", hasUpdate);
            updateInfo.put("currentVersion", currentVersion);
            updateInfo.put("latestVersion", latestVersion);
            updateInfo.put("updateUrl", hasUpdate ? "/pwa/update" : null);
            
            return ResponseUtil.success("查询成功", updateInfo);
        } catch (Exception e) {
            log.error("检查PWA更新失败", e);
            return ResponseUtil.error("检查更新失败，请稍后重试");
        }
    }
}

