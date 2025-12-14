package com.shopx.service;

import com.shopx.entity.AccountSecurity;
import com.shopx.entity.LoginHistory;
import com.shopx.entity.TwoFactorAuth;
import com.shopx.util.ResponseUtil;

import java.util.List;
import java.util.Map;

/**
 * 账户安全服务接口
 */
public interface SecurityService {
    
    /**
     * 记录登录历史
     */
    LoginHistory recordLoginHistory(Long userId, String ipAddress, String device, String browser, String status, String failureReason);
    
    /**
     * 获取用户登录历史
     */
    ResponseUtil.PageResult<LoginHistory> getLoginHistory(Long userId, int page, int size);
    
    /**
     * 检测异常登录
     */
    boolean detectAbnormalLogin(Long userId, String ipAddress, String device, String browser);
    
    /**
     * 启用双因素认证
     */
    TwoFactorAuth enable2FA(Long userId, String authMethod, String phoneOrEmail);
    
    /**
     * 禁用双因素认证
     */
    boolean disable2FA(Long userId);
    
    /**
     * 验证双因素认证码
     */
    boolean verify2FACode(Long userId, String code);
    
    /**
     * 生成备用验证码
     */
    List<String> generateBackupCodes(Long userId);
    
    /**
     * 获取账户安全设置
     */
    AccountSecurity getAccountSecurity(Long userId);
    
    /**
     * 更新账户安全设置
     */
    AccountSecurity updateAccountSecurity(Long userId, Map<String, Object> settings);
    
    /**
     * 删除账户
     */
    boolean deleteAccount(Long userId, String password);
    
    /**
     * 获取安全统计
     */
    com.shopx.dto.SecurityStatsDTO getSecurityStats(Long userId);
}

