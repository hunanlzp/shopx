package com.shopx.util;

import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

/**
 * 极简高可用性工具类
 */
@Slf4j
@Component
public class HighAvailabilityUtil {

    public <T> T executeWithRetry(Supplier<T> supplier, int maxAttempts) {
        Exception lastException = null;
        
        for (int i = 0; i < maxAttempts; i++) {
            try {
                return supplier.get();
            } catch (Exception e) {
                lastException = e;
                log.warn("执行失败，重试第{}次: {}", i + 1, e.getMessage());
                if (i < maxAttempts - 1) {
                    try {
                        Thread.sleep(1000 * (i + 1)); // 递增延迟
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
        
        throw new RuntimeException("重试失败", lastException);
    }
}