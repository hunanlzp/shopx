package com.shopx.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

/**
 * 增强健康检查配置
 * 提供更详细的系统健康状态监控
 */
@Component
public class EnhancedHealthIndicator implements HealthIndicator {

    private final DataSource dataSource;
    private final RedisTemplate<String, Object> redisTemplate;

    public EnhancedHealthIndicator(DataSource dataSource, RedisTemplate<String, Object> redisTemplate) {
        this.dataSource = dataSource;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Health health() {
        Health.Builder builder = new Health.Builder();
        
        try {
            // 检查数据库连接
            checkDatabaseHealth(builder);
            
            // 检查Redis连接
            checkRedisHealth(builder);
            
            // 检查系统资源
            checkSystemResources(builder);
            
            return builder.build();
        } catch (Exception e) {
            return builder.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }

    /**
     * 检查数据库健康状态
     */
    private void checkDatabaseHealth(Health.Builder builder) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            boolean isValid = connection.isValid(5); // 5秒超时
            if (isValid) {
                builder.up()
                        .withDetail("database", "UP")
                        .withDetail("database.url", connection.getMetaData().getURL())
                        .withDetail("database.driver", connection.getMetaData().getDriverName());
            } else {
                builder.down().withDetail("database", "DOWN");
            }
        }
    }

    /**
     * 检查Redis健康状态
     */
    private void checkRedisHealth(Health.Builder builder) {
        try {
            long startTime = System.currentTimeMillis();
            String pong = redisTemplate.getConnectionFactory()
                    .getConnection()
                    .ping();
            long responseTime = System.currentTimeMillis() - startTime;
            
            if ("PONG".equals(pong)) {
                builder.up()
                        .withDetail("redis", "UP")
                        .withDetail("redis.responseTime", responseTime + "ms");
            } else {
                builder.down().withDetail("redis", "DOWN");
            }
        } catch (Exception e) {
            builder.down()
                    .withDetail("redis", "DOWN")
                    .withDetail("redis.error", e.getMessage());
        }
    }

    /**
     * 检查系统资源
     */
    private void checkSystemResources(Health.Builder builder) {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory();
        
        double memoryUsagePercent = (double) usedMemory / maxMemory * 100;
        
        builder.withDetail("memory.total", formatBytes(totalMemory))
                .withDetail("memory.used", formatBytes(usedMemory))
                .withDetail("memory.free", formatBytes(freeMemory))
                .withDetail("memory.usagePercent", String.format("%.2f%%", memoryUsagePercent))
                .withDetail("processors", runtime.availableProcessors());
        
        // 内存使用率超过90%时标记为DOWN
        if (memoryUsagePercent > 90) {
            builder.down().withDetail("memory.status", "CRITICAL");
        } else if (memoryUsagePercent > 80) {
            builder.status(Status.UP).withDetail("memory.status", "WARNING");
        } else {
            builder.status(Status.UP).withDetail("memory.status", "HEALTHY");
        }
    }

    /**
     * 格式化字节数
     */
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.2f MB", bytes / (1024.0 * 1024));
        return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
    }
}
