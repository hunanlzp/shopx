package com.shopx.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * 统一缓存管理类
 * 提供统一的缓存操作接口
 */
@Slf4j
@Component
public class CacheManager {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 缓存前缀常量
     */
    public static final String USER_PREFIX = "shopx:user:";
    public static final String PRODUCT_PREFIX = "shopx:product:";
    public static final String SESSION_PREFIX = "shopx:session:";
    public static final String PERMISSION_PREFIX = "shopx:permission:";
    public static final String RECOMMENDATION_PREFIX = "shopx:recommendation:";

    /**
     * 设置缓存
     */
    public void set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            log.debug("设置缓存成功: key={}", key);
        } catch (Exception e) {
            log.error("设置缓存失败: key={}", key, e);
        }
    }

    /**
     * 设置缓存（带过期时间）
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
            log.debug("设置缓存成功: key={}, timeout={} {}", key, timeout, unit);
        } catch (Exception e) {
            log.error("设置缓存失败: key={}", key, e);
        }
    }

    /**
     * 设置缓存（带过期时间）
     */
    public void set(String key, Object value, Duration duration) {
        try {
            redisTemplate.opsForValue().set(key, value, duration);
            log.debug("设置缓存成功: key={}, duration={}", key, duration);
        } catch (Exception e) {
            log.error("设置缓存失败: key={}", key, e);
        }
    }

    /**
     * 获取缓存
     */
    public Object get(String key) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            log.debug("获取缓存: key={}, hit={}", key, value != null);
            return value;
        } catch (Exception e) {
            log.error("获取缓存失败: key={}", key, e);
            return null;
        }
    }

    /**
     * 获取缓存（指定类型）
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        Object value = get(key);
        if (value != null && type.isInstance(value)) {
            return (T) value;
        }
        return null;
    }

    /**
     * 删除缓存
     */
    public void delete(String key) {
        try {
            redisTemplate.delete(key);
            log.debug("删除缓存成功: key={}", key);
        } catch (Exception e) {
            log.error("删除缓存失败: key={}", key, e);
        }
    }

    /**
     * 批量删除缓存
     */
    public void delete(String... keys) {
        try {
            redisTemplate.delete(java.util.Arrays.asList(keys));
            log.debug("批量删除缓存成功: keys={}", java.util.Arrays.toString(keys));
        } catch (Exception e) {
            log.error("批量删除缓存失败: keys={}", java.util.Arrays.toString(keys), e);
        }
    }

    /**
     * 检查缓存是否存在
     */
    public boolean exists(String key) {
        try {
            Boolean exists = redisTemplate.hasKey(key);
            log.debug("检查缓存存在: key={}, exists={}", key, exists);
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            log.error("检查缓存存在失败: key={}", key, e);
            return false;
        }
    }

    /**
     * 设置过期时间
     */
    public void expire(String key, long timeout, TimeUnit unit) {
        try {
            redisTemplate.expire(key, timeout, unit);
            log.debug("设置过期时间成功: key={}, timeout={} {}", key, timeout, unit);
        } catch (Exception e) {
            log.error("设置过期时间失败: key={}", key, e);
        }
    }

    /**
     * 获取过期时间
     */
    public long getExpire(String key) {
        try {
            Long expire = redisTemplate.getExpire(key);
            return expire != null ? expire : -1;
        } catch (Exception e) {
            log.error("获取过期时间失败: key={}", key, e);
            return -1;
        }
    }

    /**
     * 用户缓存操作
     */
    public void setUserCache(Long userId, Object user) {
        set(USER_PREFIX + userId, user, Duration.ofHours(1));
    }

    public Object getUserCache(Long userId) {
        return get(USER_PREFIX + userId);
    }

    public void deleteUserCache(Long userId) {
        delete(USER_PREFIX + userId);
    }

    /**
     * 商品缓存操作
     */
    public void setProductCache(Long productId, Object product) {
        set(PRODUCT_PREFIX + productId, product, Duration.ofMinutes(30));
    }

    public Object getProductCache(Long productId) {
        return get(PRODUCT_PREFIX + productId);
    }

    public void deleteProductCache(Long productId) {
        delete(PRODUCT_PREFIX + productId);
    }

    /**
     * 权限缓存操作
     */
    public void setPermissionCache(Long userId, Object permissions) {
        set(PERMISSION_PREFIX + userId, permissions, Duration.ofMinutes(15));
    }

    public Object getPermissionCache(Long userId) {
        return get(PERMISSION_PREFIX + userId);
    }

    public void deletePermissionCache(Long userId) {
        delete(PERMISSION_PREFIX + userId);
    }

    /**
     * 清除所有缓存
     */
    public void clearAllCache() {
        try {
            redisTemplate.getConnectionFactory().getConnection().flushDb();
            log.info("清除所有缓存成功");
        } catch (Exception e) {
            log.error("清除所有缓存失败", e);
        }
    }
}
