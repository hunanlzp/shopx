package com.shopx.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步任务配置类
 * 配置线程池参数，优化异步操作性能
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    @Value("${shopx.async.core-pool-size}")
    private int corePoolSize;

    @Value("${shopx.async.max-pool-size}")
    private int maxPoolSize;

    @Value("${shopx.async.queue-capacity}")
    private int queueCapacity;

    @Value("${shopx.async.keep-alive-seconds}")
    private int keepAliveSeconds;

    /**
     * 配置异步任务执行器
     * 线程池参数可通过配置文件灵活调整
     */
    @Bean(name = "asyncExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数
        executor.setCorePoolSize(corePoolSize);
        // 设置最大线程数
        executor.setMaxPoolSize(maxPoolSize);
        // 设置队列容量
        executor.setQueueCapacity(queueCapacity);
        // 设置线程活跃时间（秒）
        executor.setKeepAliveSeconds(keepAliveSeconds);
        // 设置线程名称前缀
        executor.setThreadNamePrefix("ShopX-Async-");
        // 设置拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 初始化
        executor.initialize();
        return executor;
    }

}