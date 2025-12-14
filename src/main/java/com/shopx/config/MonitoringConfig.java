package com.shopx.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * 极简监控配置
 */
@Configuration
public class MonitoringConfig {

    @Bean
    public HealthIndicator customHealthIndicator(DataSource dataSource) {
        return () -> {
            try (Connection connection = dataSource.getConnection()) {
                return Health.up().withDetail("database", "UP").build();
            } catch (Exception e) {
                return Health.down().withDetail("database", "DOWN").build();
            }
        };
    }
}