package com.shopx.config;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT拦截器
 * 用于验证用户身份和权限
 */
@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求路径
        String requestPath = request.getRequestURI();
        log.debug("JWT拦截器处理请求: {}", requestPath);
        
        // 获取Authorization头
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // 对于需要认证的接口，返回401错误
            if (isProtectedPath(requestPath)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                
                Map<String, Object> result = new HashMap<>();
                result.put("code", 401);
                result.put("message", "未授权访问，请先登录");
                
                response.getWriter().write(JSON.toJSONString(result));
                return false;
            }
            return true;
        }
        
        // 提取token
        String token = authHeader.substring(7);
        
        // 验证token
        if (!isValidToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            
            Map<String, Object> result = new HashMap<>();
            result.put("code", 401);
            result.put("message", "Token无效或已过期");
            
            response.getWriter().write(JSON.toJSONString(result));
            return false;
        }
        
        // 将用户信息存储到请求属性中
        Long userId = extractUserIdFromToken(token);
        request.setAttribute("currentUserId", userId);
        
        return true;
    }
    
    /**
     * 判断是否为需要保护的路径
     */
    private boolean isProtectedPath(String path) {
        // 定义需要保护的路径模式
        String[] protectedPatterns = {
                "/products",
                "/recommendation",
                "/collaboration",
                "/recycle",
                "/ai-chat",
                "/user-behavior"
        };
        
        for (String pattern : protectedPatterns) {
            if (path.startsWith(pattern)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 验证token是否有效
     */
    private boolean isValidToken(String token) {
        // 简化的token验证逻辑
        // 实际项目中应该使用JWT库进行验证
        try {
            if (token.startsWith("token_")) {
                String[] parts = token.split("_");
                if (parts.length >= 3) {
                    Long userId = Long.parseLong(parts[1]);
                    Long timestamp = Long.parseLong(parts[2]);
                    
                    // 检查token是否过期（24小时）
                    long currentTime = System.currentTimeMillis();
                    long tokenTime = timestamp;
                    long expireTime = 24 * 60 * 60 * 1000; // 24小时
                    
                    return (currentTime - tokenTime) < expireTime;
                }
            }
        } catch (Exception e) {
            log.error("Token验证失败", e);
        }
        
        return false;
    }
    
    /**
     * 从token中提取用户ID
     */
    private Long extractUserIdFromToken(String token) {
        try {
            if (token.startsWith("token_")) {
                String[] parts = token.split("_");
                if (parts.length >= 2) {
                    return Long.parseLong(parts[1]);
                }
            }
        } catch (Exception e) {
            log.error("提取用户ID失败", e);
        }
        
        return null;
    }
}
