package com.shopx.utils;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 分布式锁工具类
 * 封装Redisson分布式锁的常用操作，提供更便捷的API
 */
@Component
public class DistributedLockUtil {

    private static final Logger logger = LoggerFactory.getLogger(DistributedLockUtil.class);

    @Autowired
    private RedissonClient redissonClient;

    @Value("${shopx.redis.lock-prefix}")
    private String lockPrefix;

    /**
     * 获取分布式锁
     * @param lockName 锁名称
     * @param waitTime 等待时间
     * @param leaseTime 持有时间
     * @param unit 时间单位
     * @return 锁对象
     */
    public RLock getLock(String lockName, long waitTime, long leaseTime, TimeUnit unit) {
        String lockKey = lockPrefix + lockName;
        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean locked = lock.tryLock(waitTime, leaseTime, unit);
            if (!locked) {
                logger.warn("Failed to acquire lock: {}", lockKey);
                return null;
            }
            logger.debug("Acquired lock: {}", lockKey);
            return lock;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Interrupted while acquiring lock: {}", lockKey, e);
            return null;
        }
    }

    /**
     * 获取分布式锁（默认参数）
     * @param lockName 锁名称
     * @return 锁对象
     */
    public RLock getLock(String lockName) {
        return getLock(lockName, 10, 30, TimeUnit.SECONDS);
    }

    /**
     * 释放锁
     * @param lock 锁对象
     */
    public void releaseLock(RLock lock) {
        if (lock != null && lock.isHeldByCurrentThread()) {
            lock.unlock();
            logger.debug("Released lock: {}", lock.getName());
        }
    }

    /**
     * 在分布式锁保护下执行操作
     * @param lockName 锁名称
     * @param supplier 操作函数
     * @param <T> 返回值类型
     * @return 操作结果
     */
    public <T> T executeWithLock(String lockName, Supplier<T> supplier) {
        RLock lock = getLock(lockName);
        try {
            if (lock == null) {
                throw new RuntimeException("Failed to acquire lock: " + lockName);
            }
            return supplier.get();
        } finally {
            releaseLock(lock);
        }
    }

    /**
     * 在分布式锁保护下执行无返回值操作
     * @param lockName 锁名称
     * @param action 操作函数
     */
    public void executeWithLock(String lockName, Runnable action) {
        RLock lock = getLock(lockName);
        try {
            if (lock == null) {
                throw new RuntimeException("Failed to acquire lock: " + lockName);
            }
            action.run();
        } finally {
            releaseLock(lock);
        }
    }

}