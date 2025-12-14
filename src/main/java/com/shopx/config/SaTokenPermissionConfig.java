package com.shopx.config;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.dao.SaTokenDaoRedisJackson;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import com.shopx.entity.User;
import com.shopx.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Sa-Token权限配置
 */
@Slf4j
@Configuration
public class SaTokenPermissionConfig {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 配置Redis存储
     */
    @Bean
    public SaTokenDao getSaTokenDao() {
        return new SaTokenDaoRedisJackson();
    }

    /**
     * 权限认证接口实现类
     */
    @Bean
    public StpInterface stpInterface() {
        return new StpInterface() {
            
            /**
             * 返回一个账号所拥有的权限码集合
             */
            @Override
            public List<String> getPermissionList(Object loginId, String loginType) {
                log.info("获取用户权限列表: loginId={}, loginType={}", loginId, loginType);
                
                List<String> permissionList = new ArrayList<>();
                
                try {
                    Long userId = Long.valueOf(loginId.toString());
                    User user = userService.getUserById(userId);
                    
                    if (user != null) {
                        // 根据用户角色分配权限
                        String role = user.getRole();
                        if ("ADMIN".equals(role)) {
                            // 管理员权限
                            permissionList.add("user:list");
                            permissionList.add("user:add");
                            permissionList.add("user:update");
                            permissionList.add("user:delete");
                            permissionList.add("product:list");
                            permissionList.add("product:add");
                            permissionList.add("product:update");
                            permissionList.add("product:delete");
                            permissionList.add("order:list");
                            permissionList.add("order:update");
                            permissionList.add("order:delete");
                            permissionList.add("recycle:list");
                            permissionList.add("recycle:update");
                            permissionList.add("recycle:delete");
                        } else if ("SELLER".equals(role)) {
                            // 商家权限
                            permissionList.add("product:list");
                            permissionList.add("product:add");
                            permissionList.add("product:update");
                            permissionList.add("product:delete");
                            permissionList.add("order:list");
                            permissionList.add("order:update");
                        } else {
                            // 普通用户权限
                            permissionList.add("product:list");
                            permissionList.add("order:list");
                            permissionList.add("order:add");
                            permissionList.add("recycle:list");
                            permissionList.add("recycle:add");
                        }
                    }
                } catch (Exception e) {
                    log.error("获取用户权限失败", e);
                }
                
                return permissionList;
            }

            /**
             * 返回一个账号所拥有的角色标识集合
             */
            @Override
            public List<String> getRoleList(Object loginId, String loginType) {
                log.info("获取用户角色列表: loginId={}, loginType={}", loginId, loginType);
                
                List<String> roleList = new ArrayList<>();
                
                try {
                    Long userId = Long.valueOf(loginId.toString());
                    User user = userService.getUserById(userId);
                    
                    if (user != null && user.getRole() != null) {
                        roleList.add(user.getRole());
                    }
                } catch (Exception e) {
                    log.error("获取用户角色失败", e);
                }
                
                return roleList;
            }
        };
    }

    /**
     * 配置JWT模式
     */
    @Bean
    public StpLogic getStpLogicJwt() {
        return new StpLogicJwtForSimple();
    }
}
