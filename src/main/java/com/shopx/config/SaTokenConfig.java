package com.shopx.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token配置类
 */
@Slf4j
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    /**
     * 注册Sa-Token拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册Sa-Token拦截器，校验规则为StpUtil.checkLogin()登录校验
        registry.addInterceptor(new SaInterceptor(handle -> {
            // 指定一些接口不需要登录校验
            SaRouter.match("/**")
                    .notMatch("/auth/**")           // 排除登录相关接口
                    .notMatch("/products")           // 排除商品列表接口
                    .notMatch("/products/*")         // 排除商品详情接口
                    .notMatch("/swagger-ui/**")      // 排除Swagger UI
                    .notMatch("/v3/api-docs/**")     // 排除API文档
                    .notMatch("/favicon.ico")         // 排除favicon
                    .notMatch("/error")              // 排除错误页面
                    .notMatch("/actuator/**")         // 排除监控端点
                    .check(r -> StpUtil.checkLogin()); // 其他接口需要登录校验
        })).addPathPatterns("/**");
    }

    /**
     * 配置JWT模式
     */
    @Bean
    public StpLogic getStpLogicJwt() {
        return new StpLogicJwtForSimple();
    }
}
