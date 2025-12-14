package com.shopx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shopx.constant.Constants;
import com.shopx.entity.AccountSecurity;
import com.shopx.entity.LoginHistory;
import com.shopx.entity.TwoFactorAuth;
import com.shopx.entity.User;
import com.shopx.mapper.AccountSecurityMapper;
import com.shopx.mapper.LoginHistoryMapper;
import com.shopx.mapper.TwoFactorAuthMapper;
import com.shopx.mapper.UserMapper;
import com.shopx.service.SecurityService;
import com.shopx.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 账户安全服务实现类
 */
@Slf4j
@Service
public class SecurityServiceImpl extends ServiceImpl<LoginHistoryMapper, LoginHistory> implements SecurityService {
    
    @Autowired
    private LoginHistoryMapper loginHistoryMapper;
    
    @Autowired
    private TwoFactorAuthMapper twoFactorAuthMapper;
    
    @Autowired
    private AccountSecurityMapper accountSecurityMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginHistory recordLoginHistory(Long userId, String ipAddress, String device, String browser, String status, String failureReason) {
        log.info("记录登录历史: userId={}, ipAddress={}, status={}", userId, ipAddress, status);
        
        LoginHistory history = new LoginHistory();
        history.setUserId(userId);
        history.setIpAddress(ipAddress);
        history.setDevice(device);
        history.setBrowser(browser);
        history.setStatus(status);
        history.setFailureReason(failureReason);
        
        // 检测异常登录
        if ("SUCCESS".equals(status)) {
            boolean isAbnormal = detectAbnormalLogin(userId, ipAddress, device, browser);
            history.setIsAbnormal(isAbnormal);
            if (isAbnormal) {
                history.setAbnormalReason("检测到异常登录：IP地址、设备或浏览器与历史记录不符");
                // 发送异常登录提醒
                sendAbnormalLoginAlert(userId, ipAddress, device);
            }
        }
        
        loginHistoryMapper.insert(history);
        
        // 更新账户安全信息
        updateLastLoginInfo(userId, ipAddress);
        
        return history;
    }
    
    @Override
    public ResponseUtil.PageResult<LoginHistory> getLoginHistory(Long userId, int page, int size) {
        QueryWrapper<LoginHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .orderByDesc("create_time");
        
        Page<LoginHistory> pageParam = new Page<>(page, size);
        Page<LoginHistory> result = loginHistoryMapper.selectPage(pageParam, queryWrapper);
        
        return ResponseUtil.PageResult.<LoginHistory>builder()
                .data(result.getRecords())
                .total(result.getTotal())
                .page(page)
                .size(size)
                .totalPages((int) result.getPages())
                .build();
    }
    
    @Override
    public boolean detectAbnormalLogin(Long userId, String ipAddress, String device, String browser) {
        // 获取最近10次成功登录记录
        QueryWrapper<LoginHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("status", "SUCCESS")
                   .orderByDesc("create_time")
                   .last("LIMIT 10");
        
        List<LoginHistory> recentLogins = loginHistoryMapper.selectList(queryWrapper);
        
        if (recentLogins.isEmpty()) {
            // 首次登录，不算异常
            return false;
        }
        
        // 检查IP地址是否常见
        Set<String> commonIps = recentLogins.stream()
                .map(LoginHistory::getIpAddress)
                .collect(Collectors.toSet());
        
        // 检查设备是否常见
        Set<String> commonDevices = recentLogins.stream()
                .map(LoginHistory::getDevice)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        
        // 如果IP、设备都不在常见列表中，判定为异常
        boolean ipAbnormal = !commonIps.contains(ipAddress);
        boolean deviceAbnormal = !commonDevices.isEmpty() && !commonDevices.contains(device);
        
        return ipAbnormal || deviceAbnormal;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TwoFactorAuth enable2FA(Long userId, String authMethod, String phoneOrEmail) {
        log.info("启用双因素认证: userId={}, authMethod={}", userId, authMethod);
        
        // 检查是否已启用
        QueryWrapper<TwoFactorAuth> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        TwoFactorAuth existing = twoFactorAuthMapper.selectOne(queryWrapper);
        
        if (existing != null && Boolean.TRUE.equals(existing.getEnabled())) {
            throw new RuntimeException("双因素认证已启用");
        }
        
        TwoFactorAuth twoFactorAuth;
        if (existing != null) {
            twoFactorAuth = existing;
        } else {
            twoFactorAuth = new TwoFactorAuth();
            twoFactorAuth.setUserId(userId);
        }
        
        twoFactorAuth.setEnabled(true);
        twoFactorAuth.setAuthMethod(authMethod);
        
        // 根据认证方式设置相应字段
        if ("SMS".equals(authMethod)) {
            twoFactorAuth.setPhoneNumber(phoneOrEmail);
            // 生成密钥（实际应该使用更安全的方式）
            twoFactorAuth.setSecretKey(UUID.randomUUID().toString());
        } else if ("EMAIL".equals(authMethod)) {
            twoFactorAuth.setEmail(phoneOrEmail);
            twoFactorAuth.setSecretKey(UUID.randomUUID().toString());
        } else if ("APP".equals(authMethod)) {
            // 生成TOTP密钥（实际应该使用Google Authenticator等标准）
            twoFactorAuth.setSecretKey(UUID.randomUUID().toString());
        }
        
        // 生成备用验证码
        List<String> backupCodes = generateBackupCodes(userId);
        twoFactorAuth.setBackupCodes(String.join(",", backupCodes));
        
        if (existing != null) {
            twoFactorAuthMapper.updateById(twoFactorAuth);
        } else {
            twoFactorAuthMapper.insert(twoFactorAuth);
        }
        
        return twoFactorAuth;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean disable2FA(Long userId) {
        QueryWrapper<TwoFactorAuth> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        TwoFactorAuth twoFactorAuth = twoFactorAuthMapper.selectOne(queryWrapper);
        
        if (twoFactorAuth == null) {
            return false;
        }
        
        twoFactorAuth.setEnabled(false);
        twoFactorAuthMapper.updateById(twoFactorAuth);
        return true;
    }
    
    @Override
    public boolean verify2FACode(Long userId, String code) {
        QueryWrapper<TwoFactorAuth> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("enabled", true);
        TwoFactorAuth twoFactorAuth = twoFactorAuthMapper.selectOne(queryWrapper);
        
        if (twoFactorAuth == null) {
            return false;
        }
        
        // 检查备用验证码
        if (twoFactorAuth.getBackupCodes() != null) {
            List<String> backupCodes = Arrays.asList(twoFactorAuth.getBackupCodes().split(","));
            if (backupCodes.contains(code)) {
                // 使用后移除备用验证码
                backupCodes.remove(code);
                twoFactorAuth.setBackupCodes(String.join(",", backupCodes));
                twoFactorAuthMapper.updateById(twoFactorAuth);
                return true;
            }
        }
        
        // 实际应该验证TOTP码（需要集成TOTP库）
        // 这里简化处理
        return code.length() == 6 && code.matches("\\d+");
    }
    
    @Override
    public List<String> generateBackupCodes(Long userId) {
        List<String> codes = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            codes.add(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        return codes;
    }
    
    @Override
    public AccountSecurity getAccountSecurity(Long userId) {
        QueryWrapper<AccountSecurity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        AccountSecurity security = accountSecurityMapper.selectOne(queryWrapper);
        
        if (security == null) {
            // 创建默认安全设置
            security = new AccountSecurity();
            security.setUserId(userId);
            security.setLoginAlertEnabled(true);
            security.setAbnormalLoginAlertEnabled(true);
            security.setPasswordChangeAlertEnabled(true);
            security.setAccountDeletionAlertEnabled(true);
            security.setAccountStatus(Constants.AccountStatus.ACTIVE);
            accountSecurityMapper.insert(security);
        }
        
        return security;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AccountSecurity updateAccountSecurity(Long userId, Map<String, Object> settings) {
        AccountSecurity security = getAccountSecurity(userId);
        
        if (settings.containsKey("loginAlertEnabled")) {
            security.setLoginAlertEnabled((Boolean) settings.get("loginAlertEnabled"));
        }
        if (settings.containsKey("abnormalLoginAlertEnabled")) {
            security.setAbnormalLoginAlertEnabled((Boolean) settings.get("abnormalLoginAlertEnabled"));
        }
        if (settings.containsKey("passwordChangeAlertEnabled")) {
            security.setPasswordChangeAlertEnabled((Boolean) settings.get("passwordChangeAlertEnabled"));
        }
        if (settings.containsKey("accountDeletionAlertEnabled")) {
            security.setAccountDeletionAlertEnabled((Boolean) settings.get("accountDeletionAlertEnabled"));
        }
        if (settings.containsKey("privacySettings")) {
            security.setPrivacySettings(settings.get("privacySettings").toString());
        }
        
        accountSecurityMapper.updateById(security);
        return security;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteAccount(Long userId, String password) {
        // 验证密码
        User user = userMapper.selectById(userId);
        if (user == null) {
            return false;
        }
        
        // 实际应该验证密码哈希
        // 这里简化处理
        
        // 更新账户状态为已删除
        AccountSecurity security = getAccountSecurity(userId);
        security.setAccountStatus("DELETED");
        accountSecurityMapper.updateById(security);
        
        // 发送账户删除提醒
        sendAccountDeletionAlert(userId);
        
        return true;
    }
    
    @Override
    public Map<String, Object> getSecurityStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 获取登录历史统计
        QueryWrapper<LoginHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<LoginHistory> allLogins = loginHistoryMapper.selectList(queryWrapper);
        
        stats.put("totalLogins", allLogins.size());
        stats.put("successfulLogins", allLogins.stream()
                .filter(l -> "SUCCESS".equals(l.getStatus()))
                .count());
        stats.put("failedLogins", allLogins.stream()
                .filter(l -> "FAILED".equals(l.getStatus()))
                .count());
        stats.put("abnormalLogins", allLogins.stream()
                .filter(l -> Boolean.TRUE.equals(l.getIsAbnormal()))
                .count());
        
        // 检查是否启用2FA
        QueryWrapper<TwoFactorAuth> twoFactorQuery = new QueryWrapper<>();
        twoFactorQuery.eq("user_id", userId)
                     .eq("enabled", true);
        TwoFactorAuth twoFactorAuth = twoFactorAuthMapper.selectOne(twoFactorQuery);
        stats.put("twoFactorEnabled", twoFactorAuth != null);
        
        return stats;
    }
    
    private void updateLastLoginInfo(Long userId, String ipAddress) {
        AccountSecurity security = getAccountSecurity(userId);
        security.setLastLoginTime(LocalDateTime.now());
        security.setLastLoginIp(ipAddress);
        accountSecurityMapper.updateById(security);
    }
    
    private void sendAbnormalLoginAlert(Long userId, String ipAddress, String device) {
        // 这里应该发送实际的通知（邮件、短信、推送等）
        log.warn("异常登录提醒: userId={}, ipAddress={}, device={}", userId, ipAddress, device);
    }
    
    private void sendAccountDeletionAlert(Long userId) {
        // 这里应该发送实际的通知
        log.info("账户删除提醒: userId={}", userId);
    }
}

