package com.shopx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shopx.cache.CacheManager;
import com.shopx.constant.Constants;
import com.shopx.entity.User;
import com.shopx.exception.BusinessException;
import com.shopx.mapper.UserMapper;
import com.shopx.service.UserService;
import com.shopx.util.PasswordUtil;
import com.shopx.validation.ValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private CacheManager cacheManager;
    
    @Override
    public User findByUsername(String username) {
        ValidationUtils.notBlank(username, "用户名不能为空");
        
        // 先从缓存获取
        User user = cacheManager.getUserCache(getUserIdByUsername(username));
        if (user != null) {
            return user;
        }
        
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        user = userMapper.selectOne(queryWrapper);
        
        if (user != null) {
            cacheManager.setUserCache(user.getId(), user);
        }
        
        return user;
    }
    
    @Override
    public User findByEmail(String email) {
        ValidationUtils.notBlank(email, "邮箱不能为空");
        
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        return userMapper.selectOne(queryWrapper);
    }
    
    @Override
    public User authenticate(String username, String password) {
        ValidationUtils.notBlank(username, "用户名不能为空");
        ValidationUtils.notBlank(password, "密码不能为空");
        
        User user = findByUsername(username);
        if (user != null && PasswordUtil.verifyPassword(password, user.getPassword())) {
            return user;
        }
        return null;
    }
    
    @Override
    public boolean existsByUsername(String username) {
        return findByUsername(username) != null;
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return findByEmail(email) != null;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public User createUser(User user) {
        // 参数校验
        ValidationUtils.notBlank(user.getUsername(), "用户名不能为空");
        ValidationUtils.validUsername(user.getUsername(), "用户名格式不正确");
        ValidationUtils.notBlank(user.getPassword(), "密码不能为空");
        ValidationUtils.validPassword(user.getPassword(), "密码格式不正确");
        
        // 检查用户名和邮箱是否已存在
        if (existsByUsername(user.getUsername())) {
            throw new BusinessException(400, "用户名已存在");
        }
        if (user.getEmail() != null && existsByEmail(user.getEmail())) {
            throw new BusinessException(400, "邮箱已存在");
        }
        
        // 加密密码
        user.setPassword(PasswordUtil.encryptPassword(user.getPassword()));
        
        // 设置默认值
        if (user.getRole() == null) {
            user.setRole(Constants.UserRole.USER);
        }
        if (user.getEnabled() == null) {
            user.setEnabled(true);
        }
        if (user.getFollowerCount() == null) {
            user.setFollowerCount(0);
        }
        if (user.getFollowingCount() == null) {
            user.setFollowingCount(0);
        }
        if (user.getSustainabilityScore() == null) {
            user.setSustainabilityScore(0);
        }
        if (user.getRecycleCount() == null) {
            user.setRecycleCount(0);
        }
        
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        
        userMapper.insert(user);
        
        // 设置缓存
        cacheManager.setUserCache(user.getId(), user);
        
        log.info("用户创建成功: id={}, username={}", user.getId(), user.getUsername());
        return user;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public User updateUser(User user) {
        ValidationUtils.validId(user.getId(), "用户ID不能为空");
        
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
        
        // 更新缓存
        cacheManager.setUserCache(user.getId(), user);
        
        log.info("用户更新成功: id={}", user.getId());
        return user;
    }
    
    @Override
    public User getUserById(Long id) {
        ValidationUtils.validId(id, "用户ID不能为空");
        
        // 先从缓存获取
        User user = cacheManager.getUserCache(id);
        if (user != null) {
            return user;
        }
        
        user = userMapper.selectById(id);
        if (user != null) {
            cacheManager.setUserCache(id, user);
        }
        
        return user;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        ValidationUtils.validId(userId, "用户ID不能为空");
        ValidationUtils.notBlank(oldPassword, "旧密码不能为空");
        ValidationUtils.notBlank(newPassword, "新密码不能为空");
        ValidationUtils.validPassword(newPassword, "新密码格式不正确");
        
        User user = getUserById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        
        if (!PasswordUtil.verifyPassword(oldPassword, user.getPassword())) {
            throw new BusinessException(400, "旧密码不正确");
        }
        
        user.setPassword(PasswordUtil.encryptPassword(newPassword));
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
        
        // 更新缓存
        cacheManager.setUserCache(userId, user);
        
        log.info("用户密码修改成功: id={}", userId);
        return true;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean disableUser(Long userId) {
        ValidationUtils.validId(userId, "用户ID不能为空");
        
        User user = getUserById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        
        user.setEnabled(false);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
        
        // 更新缓存
        cacheManager.setUserCache(userId, user);
        
        log.info("用户禁用成功: id={}", userId);
        return true;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean enableUser(Long userId) {
        ValidationUtils.validId(userId, "用户ID不能为空");
        
        User user = getUserById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        
        user.setEnabled(true);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
        
        // 更新缓存
        cacheManager.setUserCache(userId, user);
        
        log.info("用户启用成功: id={}", userId);
        return true;
    }
    
    @Override
    public List<String> getUserPermissions(Long userId) {
        ValidationUtils.validId(userId, "用户ID不能为空");
        
        // 先从缓存获取
        List<String> permissions = cacheManager.getPermissionCache(userId);
        if (permissions != null) {
            return permissions;
        }
        
        User user = getUserById(userId);
        if (user == null) {
            return List.of();
        }
        
        // 根据角色返回权限
        permissions = getPermissionsByRole(user.getRole());
        
        // 设置缓存
        cacheManager.setPermissionCache(userId, permissions);
        
        return permissions;
    }
    
    @Override
    public List<String> getUserRoles(Long userId) {
        ValidationUtils.validId(userId, "用户ID不能为空");
        
        User user = getUserById(userId);
        if (user == null) {
            return List.of();
        }
        
        return List.of(user.getRole());
    }
    
    /**
     * 根据用户名获取用户ID（用于缓存）
     */
    private Long getUserIdByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        queryWrapper.select("id");
        User user = userMapper.selectOne(queryWrapper);
        return user != null ? user.getId() : null;
    }
    
    /**
     * 根据角色获取权限列表
     */
    private List<String> getPermissionsByRole(String role) {
        return switch (role) {
            case Constants.UserRole.ADMIN -> List.of(
                Constants.Permission.USER_LIST, Constants.Permission.USER_ADD, 
                Constants.Permission.USER_UPDATE, Constants.Permission.USER_DELETE,
                Constants.Permission.PRODUCT_LIST, Constants.Permission.PRODUCT_ADD,
                Constants.Permission.PRODUCT_UPDATE, Constants.Permission.PRODUCT_DELETE,
                Constants.Permission.ORDER_LIST, Constants.Permission.ORDER_UPDATE,
                Constants.Permission.RECYCLE_LIST, Constants.Permission.RECYCLE_UPDATE
            );
            case Constants.UserRole.SELLER -> List.of(
                Constants.Permission.PRODUCT_LIST, Constants.Permission.PRODUCT_ADD,
                Constants.Permission.PRODUCT_UPDATE, Constants.Permission.PRODUCT_DELETE,
                Constants.Permission.ORDER_LIST, Constants.Permission.ORDER_UPDATE
            );
            case Constants.UserRole.USER -> List.of(
                Constants.Permission.PRODUCT_LIST, Constants.Permission.ORDER_ADD,
                Constants.Permission.RECYCLE_ADD
            );
            default -> List.of();
        };
    }
}
