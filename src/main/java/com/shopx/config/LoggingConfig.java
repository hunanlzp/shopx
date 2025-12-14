package com.shopx.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * 日志配置类
 * 统一管理应用日志格式、输出和轮转策略
 */
@Slf4j
@Configuration
public class LoggingConfig {

    @PostConstruct
    public void initLogging() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        
        // 配置根日志级别
        Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.INFO);
        
        // 配置应用日志级别
        Logger appLogger = loggerContext.getLogger("com.shopx");
        appLogger.setLevel(Level.DEBUG);
        
        // 配置SQL日志级别
        Logger sqlLogger = loggerContext.getLogger("com.shopx.mapper");
        sqlLogger.setLevel(Level.DEBUG);
        
        log.info("日志配置初始化完成");
    }
}
