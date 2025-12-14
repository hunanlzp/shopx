package com.shopx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shopx.entity.User;

import java.util.List;

/**
 * 用户服务接口
 * 继承MyBatis-Plus的IService，提供基础服务方法
 */
public interface UserService extends IService<User> {

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户对象
     */
    User findByUsername(String username);

    /**
     * 根据邮箱查询用户
     * @param email 邮箱
     * @return 用户对象
     */
    User findByEmail(String email);

    /**
     * 用户认证
     * @param username 用户名
     * @param password 密码
     * @return 认证成功的用户对象
     */
    User authenticate(String username, String password);

    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 创建用户
     * @param user 用户对象
     * @return 创建的用户对象
     */
    User createUser(User user);

    /**
     * 更新用户信息
     * @param user 用户对象
     * @return 更新的用户对象
     */
    User updateUser(User user);

    /**
     * 根据ID获取用户
     * @param id 用户ID
     * @return 用户对象
     */
    User getUserById(Long id);

    /**
     * 修改密码
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 修改结果
     */
    boolean changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 禁用用户
     * @param userId 用户ID
     * @return 操作结果
     */
    boolean disableUser(Long userId);

    /**
     * 启用用户
     * @param userId 用户ID
     * @return 操作结果
     */
    boolean enableUser(Long userId);
    
    /**
     * 获取用户权限列表
     * @param userId 用户ID
     * @return 权限列表
     */
    List<String> getUserPermissions(Long userId);
    
    /**
     * 获取用户角色列表
     * @param userId 用户ID
     * @return 角色列表
     */
    List<String> getUserRoles(Long userId);

}