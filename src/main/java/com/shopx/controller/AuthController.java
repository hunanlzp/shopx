package com.shopx.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.shopx.entity.ApiResponse;
import com.shopx.entity.User;
import com.shopx.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@Tag(name = "用户认证", description = "用户登录、注册、权限管理相关API")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 用户登录
     */
    @Operation(summary = "用户登录", description = "通过用户名和密码进行登录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "登录成功"),
            @ApiResponse(responseCode = "401", description = "用户名或密码错误")
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Parameter(description = "用户名", required = true) @RequestParam String username,
            @Parameter(description = "密码", required = true) @RequestParam String password) {
        
        log.info("用户登录请求: username={}", username);
        
        try {
            User user = userService.authenticate(username, password);
            if (user != null) {
                // 使用Sa-Token进行登录
                StpUtil.login(user.getId());
                
                // 获取token
                String token = StpUtil.getTokenValue();
                
                LoginResponse response = LoginResponse.builder()
                        .user(user)
                        .token(token)
                        .loginTime(LocalDateTime.now())
                        .build();
                
                return ResponseEntity.ok(ApiResponse.success("登录成功", response));
            } else {
                return ResponseEntity.ok(ApiResponse.unauthorized("用户名或密码错误"));
            }
        } catch (Exception e) {
            log.error("用户登录失败", e);
            return ResponseEntity.ok(ApiResponse.error("登录失败，请稍后重试"));
        }
    }
    
    /**
     * 用户注册
     */
    @Operation(summary = "用户注册", description = "创建新用户账号")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "注册成功"),
            @ApiResponse(responseCode = "400", description = "注册失败")
    })
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> register(@RequestBody User user) {
        
        log.info("用户注册请求: username={}", user.getUsername());
        
        try {
            // 检查用户名是否已存在
            if (userService.existsByUsername(user.getUsername())) {
                return ResponseEntity.ok(ApiResponse.badRequest("用户名已存在"));
            }
            
            // 检查邮箱是否已存在
            if (user.getEmail() != null && userService.existsByEmail(user.getEmail())) {
                return ResponseEntity.ok(ApiResponse.badRequest("邮箱已存在"));
            }
            
            // 设置默认角色
            if (user.getRole() == null) {
                user.setRole("USER");
            }
            
            User createdUser = userService.createUser(user);
            return ResponseEntity.ok(ApiResponse.success("注册成功", createdUser));
        } catch (Exception e) {
            log.error("用户注册失败", e);
            return ResponseEntity.ok(ApiResponse.error("注册失败，请稍后重试"));
        }
    }
    
    /**
     * 用户登出
     */
    @Operation(summary = "用户登出", description = "退出当前登录状态")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "登出成功")
    })
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        
        try {
            StpUtil.logout();
            return ResponseEntity.ok(ApiResponse.success("登出成功", null));
        } catch (Exception e) {
            log.error("用户登出失败", e);
            return ResponseEntity.ok(ApiResponse.error("登出失败，请稍后重试"));
        }
    }
    
    /**
     * 修改密码
     */
    @Operation(summary = "修改密码", description = "修改当前用户密码")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "修改成功"),
            @ApiResponse(responseCode = "400", description = "原密码错误")
    })
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Parameter(description = "原密码", required = true) @RequestParam String oldPassword,
            @Parameter(description = "新密码", required = true) @RequestParam String newPassword) {
        
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            boolean success = userService.changePassword(userId, oldPassword, newPassword);
            
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("密码修改成功", null));
            } else {
                return ResponseEntity.ok(ApiResponse.badRequest("原密码错误"));
            }
        } catch (Exception e) {
            log.error("修改密码失败", e);
            return ResponseEntity.ok(ApiResponse.error("修改密码失败，请稍后重试"));
        }
    }
    
    /**
     * 获取当前用户信息
     */
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @GetMapping("/user")
    public ResponseEntity<ApiResponse<User>> getCurrentUser() {
        
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            User user = userService.getUserById(userId);
            
            if (user != null) {
                return ResponseEntity.ok(ApiResponse.success("获取用户信息成功", user));
            } else {
                return ResponseEntity.ok(ApiResponse.notFound("用户不存在"));
            }
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            return ResponseEntity.ok(ApiResponse.error("获取用户信息失败，请稍后重试"));
        }
    }
    
    /**
     * 更新用户信息
     */
    @Operation(summary = "更新用户信息", description = "更新当前用户的基本信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功")
    })
    @PutMapping("/user")
    public ResponseEntity<ApiResponse<User>> updateUser(@RequestBody User user) {
        
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            user.setId(userId);
            
            User updatedUser = userService.updateUser(user);
            return ResponseEntity.ok(ApiResponse.success("更新用户信息成功", updatedUser));
        } catch (Exception e) {
            log.error("更新用户信息失败", e);
            return ResponseEntity.ok(ApiResponse.error("更新用户信息失败，请稍后重试"));
        }
    }
    
    /**
     * 获取用户权限列表
     */
    @Operation(summary = "获取用户权限列表", description = "获取当前用户的权限列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @GetMapping("/permissions")
    public ResponseEntity<ApiResponse<List<String>>> getPermissions() {
        
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            List<String> permissions = StpUtil.getPermissionList(userId);
            
            return ResponseEntity.ok(ApiResponse.success("获取权限列表成功", permissions));
        } catch (Exception e) {
            log.error("获取权限列表失败", e);
            return ResponseEntity.ok(ApiResponse.error("获取权限列表失败，请稍后重试"));
        }
    }
    
    /**
     * 获取用户角色列表
     */
    @Operation(summary = "获取用户角色列表", description = "获取当前用户的角色列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @GetMapping("/roles")
    public ResponseEntity<ApiResponse<List<String>>> getRoles() {
        
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            List<String> roles = StpUtil.getRoleList(userId);
            
            return ResponseEntity.ok(ApiResponse.success("获取角色列表成功", roles));
        } catch (Exception e) {
            log.error("获取角色列表失败", e);
            return ResponseEntity.ok(ApiResponse.error("获取角色列表失败，请稍后重试"));
        }
    }
    
    /**
     * 检查用户是否登录
     */
    @Operation(summary = "检查登录状态", description = "检查当前用户是否已登录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "检查成功")
    })
    @GetMapping("/check")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkLogin() {
        
        try {
            boolean isLogin = StpUtil.isLogin();
            Map<String, Object> result = new HashMap<>();
            result.put("isLogin", isLogin);
            
            if (isLogin) {
                result.put("userId", StpUtil.getLoginId());
                result.put("tokenValue", StpUtil.getTokenValue());
                result.put("loginTime", StpUtil.getLoginTime());
            }
            
            return ResponseEntity.ok(ApiResponse.success("检查登录状态成功", result));
        } catch (Exception e) {
            log.error("检查登录状态失败", e);
            return ResponseEntity.ok(ApiResponse.error("检查登录状态失败，请稍后重试"));
        }
    }
    
    /**
     * 登录响应实体
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class LoginResponse {
        private User user;
        private String token;
        private LocalDateTime loginTime;
    }
}