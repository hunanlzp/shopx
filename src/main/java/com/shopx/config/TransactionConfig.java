package com.shopx.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 事务管理配置
 */
@Slf4j
@Configuration
@EnableTransactionManagement
public class TransactionConfig {
    
    /**
     * 事务模板
     */
    @Bean
    public TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        
        // 设置事务超时时间（秒）
        template.setTimeout(30);
        
        // 设置只读事务
        template.setReadOnly(false);
        
        log.info("事务模板配置完成");
        return template;
    }
}
