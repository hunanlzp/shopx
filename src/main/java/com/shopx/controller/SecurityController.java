package com.shopx.controller;

import com.shopx.annotation.ApiVersion;
import com.shopx.entity.ApiResponse;
import com.shopx.entity.AccountSecurity;
import com.shopx.entity.LoginHistory;
import com.shopx.entity.TwoFactorAuth;
import com.shopx.service.SecurityService;
import com.shopx.util.ResponseUtil;
import com.shopx.util.SaTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 账户安全控制器
 */
@Slf4j
@RestController
@RequestMapping("/security")
@ApiVersion("v1")
@Tag(name = "账户安全", description = "账户安全相关API")
public class SecurityController {
    
    @Autowired
    private SecurityService securityService;
    
    /**
     * 获取登录历史
     */
    @Operation(summary = "获取登录历史", description = "获取当前用户的登录历史记录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @GetMapping("/login-history")
    public ResponseEntity<ApiResponse<ResponseUtil.PageResult<LoginHistory>>> getLoginHistory(
            @Parameter(description = "页码", required = false) @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小", required = false) @RequestParam(defaultValue = "20") int size) {
        
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            ResponseUtil.PageResult<LoginHistory> result = securityService.getLoginHistory(userId, page, size);
            return ResponseUtil.success("查询成功", result);
        } catch (Exception e) {
            log.error("获取登录历史失败", e);
            return ResponseUtil.error("获取登录历史失败，请稍后重试");
        }
    }
    
    /**
     * 启用双因素认证
     */
    @Operation(summary = "启用双因素认证", description = "启用双因素认证（2FA）")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "启用成功")
    })
    @PostMapping("/2fa/enable")
    public ResponseEntity<ApiResponse<TwoFactorAuth>> enable2FA(
            @Parameter(description = "认证方式", required = true) @RequestParam String authMethod,
            @Parameter(description = "手机号或邮箱", required = true) @RequestParam String phoneOrEmail) {
        
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            TwoFactorAuth twoFactorAuth = securityService.enable2FA(userId, authMethod, phoneOrEmail);
            return ResponseUtil.success("启用成功", twoFactorAuth);
        } catch (Exception e) {
            log.error("启用双因素认证失败", e);
            return ResponseUtil.error("启用双因素认证失败：" + e.getMessage());
        }
    }
    
    /**
     * 禁用双因素认证
     */
    @Operation(summary = "禁用双因素认证", description = "禁用双因素认证（2FA）")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "禁用成功")
    })
    @PostMapping("/2fa/disable")
    public ResponseEntity<ApiResponse<Void>> disable2FA() {
        
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            boolean success = securityService.disable2FA(userId);
            if (success) {
                return ResponseUtil.success("禁用成功", null);
            } else {
                return ResponseUtil.error("禁用失败，未启用双因素认证");
            }
        } catch (Exception e) {
            log.error("禁用双因素认证失败", e);
            return ResponseUtil.error("禁用双因素认证失败，请稍后重试");
        }
    }
    
    /**
     * 验证双因素认证码
     */
    @Operation(summary = "验证2FA码", description = "验证双因素认证码")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "验证成功")
    })
    @PostMapping("/2fa/verify")
    public ResponseEntity<ApiResponse<Boolean>> verify2FACode(
            @Parameter(description = "验证码", required = true) @RequestParam String code) {
        
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            boolean valid = securityService.verify2FACode(userId, code);
            return ResponseUtil.success("验证完成", valid);
        } catch (Exception e) {
            log.error("验证2FA码失败", e);
            return ResponseUtil.error("验证失败，请稍后重试");
        }
    }
    
    /**
     * 生成备用验证码
     */
    @Operation(summary = "生成备用验证码", description = "生成双因素认证的备用验证码")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "生成成功")
    })
    @PostMapping("/2fa/backup-codes")
    public ResponseEntity<ApiResponse<List<String>>> generateBackupCodes() {
        
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            List<String> codes = securityService.generateBackupCodes(userId);
            return ResponseUtil.success("生成成功", codes);
        } catch (Exception e) {
            log.error("生成备用验证码失败", e);
            return ResponseUtil.error("生成备用验证码失败，请稍后重试");
        }
    }
    
    /**
     * 获取账户安全设置
     */
    @Operation(summary = "获取安全设置", description = "获取当前用户的账户安全设置")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @GetMapping("/settings")
    public ResponseEntity<ApiResponse<AccountSecurity>> getAccountSecurity() {
        
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            AccountSecurity security = securityService.getAccountSecurity(userId);
            return ResponseUtil.success("查询成功", security);
        } catch (Exception e) {
            log.error("获取安全设置失败", e);
            return ResponseUtil.error("获取安全设置失败，请稍后重试");
        }
    }
    
    /**
     * 更新账户安全设置
     */
    @Operation(summary = "更新安全设置", description = "更新当前用户的账户安全设置")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功")
    })
    @PutMapping("/settings")
    public ResponseEntity<ApiResponse<AccountSecurity>> updateAccountSecurity(
            @Parameter(description = "安全设置", required = true) @RequestBody Map<String, Object> settings) {
        
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            AccountSecurity security = securityService.updateAccountSecurity(userId, settings);
            return ResponseUtil.success("更新成功", security);
        } catch (Exception e) {
            log.error("更新安全设置失败", e);
            return ResponseUtil.error("更新安全设置失败，请稍后重试");
        }
    }
    
    /**
     * 删除账户
     */
    @Operation(summary = "删除账户", description = "删除当前用户账户")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功")
    })
    @DeleteMapping("/account")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(
            @Parameter(description = "密码", required = true) @RequestParam String password) {
        
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            boolean success = securityService.deleteAccount(userId, password);
            if (success) {
                return ResponseUtil.success("账户删除成功", null);
            } else {
                return ResponseUtil.error("账户删除失败，密码错误");
            }
        } catch (Exception e) {
            log.error("删除账户失败", e);
            return ResponseUtil.error("删除账户失败，请稍后重试");
        }
    }
    
    /**
     * 获取安全统计
     */
    @Operation(summary = "获取安全统计", description = "获取当前用户的账户安全统计信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<com.shopx.dto.SecurityStatsDTO>> getSecurityStats() {
        
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            com.shopx.dto.SecurityStatsDTO stats = securityService.getSecurityStats(userId);
            return ResponseUtil.success("查询成功", stats);
        } catch (Exception e) {
            log.error("获取安全统计失败", e);
            return ResponseUtil.error("获取安全统计失败，请稍后重试");
        }
    }
}
