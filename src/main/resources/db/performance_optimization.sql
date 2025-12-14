-- ShopX 数据库性能优化脚本
-- 创建时间: 2024-01-01
-- 描述: 优化数据库性能，包括索引、分区、查询优化等

-- =============================================
-- 1. 索引优化
-- =============================================

-- 用户表索引优化
CREATE INDEX IF NOT EXISTS idx_user_username ON t_user(username);
CREATE INDEX IF NOT EXISTS idx_user_email ON t_user(email);
CREATE INDEX IF NOT EXISTS idx_user_role ON t_user(role);
CREATE INDEX IF NOT EXISTS idx_user_enabled ON t_user(enabled);
CREATE INDEX IF NOT EXISTS idx_user_create_time ON t_user(create_time);
CREATE INDEX IF NOT EXISTS idx_user_sustainability_score ON t_user(sustainability_score);

-- 商品表索引优化
CREATE INDEX IF NOT EXISTS idx_product_name ON t_product(name);
CREATE INDEX IF NOT EXISTS idx_product_category ON t_product(category);
CREATE INDEX IF NOT EXISTS idx_product_status ON t_product(status);
CREATE INDEX IF NOT EXISTS idx_product_price ON t_product(price);
CREATE INDEX IF NOT EXISTS idx_product_stock ON t_product(stock);
CREATE INDEX IF NOT EXISTS idx_product_create_time ON t_product(create_time);
CREATE INDEX IF NOT EXISTS idx_product_view_count ON t_product(view_count);
CREATE INDEX IF NOT EXISTS idx_product_like_count ON t_product(like_count);
CREATE INDEX IF NOT EXISTS idx_product_has3d_preview ON t_product(has3d_preview);

-- 订单表索引优化
CREATE INDEX IF NOT EXISTS idx_order_user_id ON t_order(user_id);
CREATE INDEX IF NOT EXISTS idx_order_status ON t_order(status);
CREATE INDEX IF NOT EXISTS idx_order_create_time ON t_order(create_time);
CREATE INDEX IF NOT EXISTS idx_order_total_amount ON t_order(total_amount);
CREATE INDEX IF NOT EXISTS idx_order_payment_status ON t_order(payment_status);

-- 订单项表索引优化
CREATE INDEX IF NOT EXISTS idx_order_item_order_id ON t_order_item(order_id);
CREATE INDEX IF NOT EXISTS idx_order_item_product_id ON t_order_item(product_id);
CREATE INDEX IF NOT EXISTS idx_order_item_quantity ON t_order_item(quantity);

-- 回收订单表索引优化
CREATE INDEX IF NOT EXISTS idx_recycle_order_user_id ON t_recycle_order(user_id);
CREATE INDEX IF NOT EXISTS idx_recycle_order_status ON t_recycle_order(status);
CREATE INDEX IF NOT EXISTS idx_recycle_order_create_time ON t_recycle_order(create_time);
CREATE INDEX IF NOT EXISTS idx_recycle_order_estimated_value ON t_recycle_order(estimated_value);

-- 环保活动表索引优化
CREATE INDEX IF NOT EXISTS idx_eco_activity_type ON t_eco_activity(type);
CREATE INDEX IF NOT EXISTS idx_eco_activity_status ON t_eco_activity(status);
CREATE INDEX IF NOT EXISTS idx_eco_activity_start_date ON t_eco_activity(start_date);
CREATE INDEX IF NOT EXISTS idx_eco_activity_end_date ON t_eco_activity(end_date);
CREATE INDEX IF NOT EXISTS idx_eco_activity_category ON t_eco_activity(category);

-- 用户行为表索引优化
CREATE INDEX IF NOT EXISTS idx_user_behavior_user_id ON t_user_behavior(user_id);
CREATE INDEX IF NOT EXISTS idx_user_behavior_product_id ON t_user_behavior(product_id);
CREATE INDEX IF NOT EXISTS idx_user_behavior_behavior_type ON t_user_behavior(behavior_type);
CREATE INDEX IF NOT EXISTS idx_user_behavior_create_time ON t_user_behavior(create_time);

-- =============================================
-- 2. 复合索引优化
-- =============================================

-- 商品查询复合索引
CREATE INDEX IF NOT EXISTS idx_product_category_status ON t_product(category, status);
CREATE INDEX IF NOT EXISTS idx_product_status_price ON t_product(status, price);
CREATE INDEX IF NOT EXISTS idx_product_category_price ON t_product(category, price);
CREATE INDEX IF NOT EXISTS idx_product_status_create_time ON t_product(status, create_time);

-- 订单查询复合索引
CREATE INDEX IF NOT EXISTS idx_order_user_status ON t_order(user_id, status);
CREATE INDEX IF NOT EXISTS idx_order_status_create_time ON t_order(status, create_time);
CREATE INDEX IF NOT EXISTS idx_order_user_create_time ON t_order(user_id, create_time);

-- 用户行为分析复合索引
CREATE INDEX IF NOT EXISTS idx_user_behavior_user_type ON t_user_behavior(user_id, behavior_type);
CREATE INDEX IF NOT EXISTS idx_user_behavior_product_type ON t_user_behavior(product_id, behavior_type);
CREATE INDEX IF NOT EXISTS idx_user_behavior_type_time ON t_user_behavior(behavior_type, create_time);

-- 回收订单复合索引
CREATE INDEX IF NOT EXISTS idx_recycle_order_user_status ON t_recycle_order(user_id, status);
CREATE INDEX IF NOT EXISTS idx_recycle_order_status_time ON t_recycle_order(status, create_time);

-- =============================================
-- 3. 全文搜索索引
-- =============================================

-- 商品名称和描述全文搜索
ALTER TABLE t_product ADD FULLTEXT(name, description);

-- 用户行为备注全文搜索
ALTER TABLE t_user_behavior ADD FULLTEXT(notes);

-- =============================================
-- 4. 分区表优化
-- =============================================

-- 用户行为表按月分区
ALTER TABLE t_user_behavior 
PARTITION BY RANGE (YEAR(create_time) * 100 + MONTH(create_time)) (
    PARTITION p202401 VALUES LESS THAN (202402),
    PARTITION p202402 VALUES LESS THAN (202403),
    PARTITION p202403 VALUES LESS THAN (202404),
    PARTITION p202404 VALUES LESS THAN (202405),
    PARTITION p202405 VALUES LESS THAN (202406),
    PARTITION p202406 VALUES LESS THAN (202407),
    PARTITION p202407 VALUES LESS THAN (202408),
    PARTITION p202408 VALUES LESS THAN (202409),
    PARTITION p202409 VALUES LESS THAN (202410),
    PARTITION p202410 VALUES LESS THAN (202411),
    PARTITION p202411 VALUES LESS THAN (202412),
    PARTITION p202412 VALUES LESS THAN (202501),
    PARTITION p_future VALUES LESS THAN MAXVALUE
);

-- 订单表按月分区
ALTER TABLE t_order 
PARTITION BY RANGE (YEAR(create_time) * 100 + MONTH(create_time)) (
    PARTITION p202401 VALUES LESS THAN (202402),
    PARTITION p202402 VALUES LESS THAN (202403),
    PARTITION p202403 VALUES LESS THAN (202404),
    PARTITION p202404 VALUES LESS THAN (202405),
    PARTITION p202405 VALUES LESS THAN (202406),
    PARTITION p202406 VALUES LESS THAN (202407),
    PARTITION p202407 VALUES LESS THAN (202408),
    PARTITION p202408 VALUES LESS THAN (202409),
    PARTITION p202409 VALUES LESS THAN (202410),
    PARTITION p202410 VALUES LESS THAN (202411),
    PARTITION p202411 VALUES LESS THAN (202412),
    PARTITION p202412 VALUES LESS THAN (202501),
    PARTITION p_future VALUES LESS THAN MAXVALUE
);

-- =============================================
-- 5. 视图优化
-- =============================================

-- 商品统计视图
CREATE OR REPLACE VIEW v_product_stats AS
SELECT 
    p.id,
    p.name,
    p.category,
    p.price,
    p.stock,
    p.status,
    p.view_count,
    p.like_count,
    p.share_count,
    COUNT(DISTINCT oi.order_id) as order_count,
    SUM(oi.quantity) as total_sold,
    AVG(oi.quantity) as avg_quantity_per_order,
    COUNT(DISTINCT ub.user_id) as unique_viewers,
    p.create_time,
    p.update_time
FROM t_product p
LEFT JOIN t_order_item oi ON p.id = oi.product_id
LEFT JOIN t_order o ON oi.order_id = o.id AND o.status = 'COMPLETED'
LEFT JOIN t_user_behavior ub ON p.id = ub.product_id AND ub.behavior_type = 'VIEW'
GROUP BY p.id, p.name, p.category, p.price, p.stock, p.status, p.view_count, p.like_count, p.share_count, p.create_time, p.update_time;

-- 用户统计视图
CREATE OR REPLACE VIEW v_user_stats AS
SELECT 
    u.id,
    u.username,
    u.email,
    u.role,
    u.sustainability_score,
    u.recycle_count,
    COUNT(DISTINCT o.id) as total_orders,
    SUM(o.total_amount) as total_spent,
    COUNT(DISTINCT ro.id) as total_recycle_orders,
    SUM(ro.estimated_value) as total_recycle_value,
    COUNT(DISTINCT ub.product_id) as products_viewed,
    u.create_time,
    u.update_time
FROM t_user u
LEFT JOIN t_order o ON u.id = o.user_id AND o.status = 'COMPLETED'
LEFT JOIN t_recycle_order ro ON u.id = ro.user_id AND ro.status = 'COMPLETED'
LEFT JOIN t_user_behavior ub ON u.id = ub.user_id AND ub.behavior_type = 'VIEW'
GROUP BY u.id, u.username, u.email, u.role, u.sustainability_score, u.recycle_count, u.create_time, u.update_time;

-- 热门商品视图
CREATE OR REPLACE VIEW v_hot_products AS
SELECT 
    p.id,
    p.name,
    p.category,
    p.price,
    p.image,
    p.view_count,
    p.like_count,
    p.share_count,
    COUNT(DISTINCT oi.order_id) as order_count,
    SUM(oi.quantity) as total_sold,
    (p.view_count * 0.3 + p.like_count * 0.4 + p.share_count * 0.2 + COUNT(DISTINCT oi.order_id) * 0.1) as hot_score
FROM t_product p
LEFT JOIN t_order_item oi ON p.id = oi.product_id
LEFT JOIN t_order o ON oi.order_id = o.id AND o.status = 'COMPLETED'
WHERE p.status = 'ACTIVE'
GROUP BY p.id, p.name, p.category, p.price, p.image, p.view_count, p.like_count, p.share_count
ORDER BY hot_score DESC;

-- =============================================
-- 6. 存储过程优化
-- =============================================

-- 更新商品统计信息存储过程
DELIMITER //
CREATE PROCEDURE UpdateProductStats(IN product_id BIGINT)
BEGIN
    DECLARE view_count INT DEFAULT 0;
    DECLARE like_count INT DEFAULT 0;
    DECLARE share_count INT DEFAULT 0;
    DECLARE order_count INT DEFAULT 0;
    DECLARE total_sold INT DEFAULT 0;
    
    -- 计算浏览次数
    SELECT COUNT(*) INTO view_count 
    FROM t_user_behavior 
    WHERE product_id = product_id AND behavior_type = 'VIEW';
    
    -- 计算喜欢次数
    SELECT COUNT(*) INTO like_count 
    FROM t_user_behavior 
    WHERE product_id = product_id AND behavior_type = 'LIKE';
    
    -- 计算分享次数
    SELECT COUNT(*) INTO share_count 
    FROM t_user_behavior 
    WHERE product_id = product_id AND behavior_type = 'SHARE';
    
    -- 计算订单数量和销量
    SELECT COUNT(DISTINCT oi.order_id), SUM(oi.quantity) 
    INTO order_count, total_sold
    FROM t_order_item oi
    JOIN t_order o ON oi.order_id = o.id
    WHERE oi.product_id = product_id AND o.status = 'COMPLETED';
    
    -- 更新商品统计信息
    UPDATE t_product 
    SET 
        view_count = view_count,
        like_count = like_count,
        share_count = share_count,
        update_time = NOW()
    WHERE id = product_id;
    
END //
DELIMITER ;

-- 批量更新商品统计信息存储过程
DELIMITER //
CREATE PROCEDURE BatchUpdateProductStats()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE product_id BIGINT;
    DECLARE product_cursor CURSOR FOR SELECT id FROM t_product WHERE status = 'ACTIVE';
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    OPEN product_cursor;
    
    read_loop: LOOP
        FETCH product_cursor INTO product_id;
        IF done THEN
            LEAVE read_loop;
        END IF;
        
        CALL UpdateProductStats(product_id);
    END LOOP;
    
    CLOSE product_cursor;
END //
DELIMITER ;

-- =============================================
-- 7. 触发器优化
-- =============================================

-- 用户行为统计触发器
DELIMITER //
CREATE TRIGGER tr_user_behavior_stats 
AFTER INSERT ON t_user_behavior
FOR EACH ROW
BEGIN
    IF NEW.behavior_type = 'VIEW' THEN
        UPDATE t_product 
        SET view_count = view_count + 1, update_time = NOW()
        WHERE id = NEW.product_id;
    ELSEIF NEW.behavior_type = 'LIKE' THEN
        UPDATE t_product 
        SET like_count = like_count + 1, update_time = NOW()
        WHERE id = NEW.product_id;
    ELSEIF NEW.behavior_type = 'SHARE' THEN
        UPDATE t_product 
        SET share_count = share_count + 1, update_time = NOW()
        WHERE id = NEW.product_id;
    END IF;
END //
DELIMITER ;

-- 订单状态更新触发器
DELIMITER //
CREATE TRIGGER tr_order_status_update 
AFTER UPDATE ON t_order
FOR EACH ROW
BEGIN
    IF NEW.status = 'COMPLETED' AND OLD.status != 'COMPLETED' THEN
        -- 更新商品销量统计
        UPDATE t_product p
        JOIN t_order_item oi ON p.id = oi.product_id
        SET p.update_time = NOW()
        WHERE oi.order_id = NEW.id;
    END IF;
END //
DELIMITER ;

-- =============================================
-- 8. 查询优化建议
-- =============================================

-- 商品搜索优化查询
-- 使用全文搜索 + 索引优化
-- SELECT * FROM t_product 
-- WHERE MATCH(name, description) AGAINST('搜索关键词' IN NATURAL LANGUAGE MODE)
-- AND status = 'ACTIVE' 
-- AND category = '电子产品'
-- ORDER BY view_count DESC, like_count DESC;

-- 用户行为分析优化查询
-- SELECT 
--     ub.behavior_type,
--     COUNT(*) as count,
--     COUNT(DISTINCT ub.user_id) as unique_users,
--     COUNT(DISTINCT ub.product_id) as unique_products
-- FROM t_user_behavior ub
-- WHERE ub.create_time >= DATE_SUB(NOW(), INTERVAL 30 DAY)
-- GROUP BY ub.behavior_type
-- ORDER BY count DESC;

-- 热门商品推荐优化查询
-- SELECT p.*, vps.hot_score
-- FROM t_product p
-- JOIN v_hot_products vps ON p.id = vps.id
-- WHERE p.status = 'ACTIVE'
-- ORDER BY vps.hot_score DESC
-- LIMIT 20;

-- =============================================
-- 9. 数据库配置优化
-- =============================================

-- 设置MySQL配置参数（需要在my.cnf中配置）
-- [mysqld]
-- # 内存配置
-- innodb_buffer_pool_size = 2G
-- innodb_log_file_size = 256M
-- innodb_log_buffer_size = 16M
-- innodb_flush_log_at_trx_commit = 2
-- 
-- # 连接配置
-- max_connections = 500
-- max_connect_errors = 1000
-- 
-- # 查询缓存
-- query_cache_size = 128M
-- query_cache_type = 1
-- 
-- # 临时表配置
-- tmp_table_size = 64M
-- max_heap_table_size = 64M
-- 
-- # 慢查询日志
-- slow_query_log = 1
-- slow_query_log_file = /var/log/mysql/slow.log
-- long_query_time = 2

-- =============================================
-- 10. 定期维护任务
-- =============================================

-- 创建定期维护事件
DELIMITER //
CREATE EVENT ev_daily_maintenance
ON SCHEDULE EVERY 1 DAY
STARTS '2024-01-01 02:00:00'
DO
BEGIN
    -- 更新商品统计信息
    CALL BatchUpdateProductStats();
    
    -- 清理过期数据
    DELETE FROM t_user_behavior 
    WHERE create_time < DATE_SUB(NOW(), INTERVAL 1 YEAR);
    
    -- 优化表
    OPTIMIZE TABLE t_product, t_user, t_order, t_order_item, t_user_behavior;
    
    -- 分析表统计信息
    ANALYZE TABLE t_product, t_user, t_order, t_order_item, t_user_behavior;
END //
DELIMITER ;

-- 启用事件调度器
SET GLOBAL event_scheduler = ON;

-- =============================================
-- 11. 性能监控查询
-- =============================================

-- 查看表大小
-- SELECT 
--     table_name,
--     ROUND(((data_length + index_length) / 1024 / 1024), 2) AS 'Size in MB'
-- FROM information_schema.tables
-- WHERE table_schema = 'shopx'
-- ORDER BY (data_length + index_length) DESC;

-- 查看索引使用情况
-- SELECT 
--     object_schema,
--     object_name,
--     index_name,
--     count_read,
--     count_write,
--     count_read / (count_read + count_write) as read_ratio
-- FROM performance_schema.table_io_waits_summary_by_index_usage
-- WHERE object_schema = 'shopx'
-- ORDER BY count_read DESC;

-- 查看慢查询
-- SELECT 
--     query_time,
--     lock_time,
--     rows_sent,
--     rows_examined,
--     sql_text
-- FROM mysql.slow_log
-- WHERE start_time >= DATE_SUB(NOW(), INTERVAL 1 DAY)
-- ORDER BY query_time DESC
-- LIMIT 10;

-- =============================================
-- 12. 备份和恢复策略
-- =============================================

-- 创建备份存储过程
DELIMITER //
CREATE PROCEDURE CreateBackup()
BEGIN
    DECLARE backup_name VARCHAR(100);
    SET backup_name = CONCAT('shopx_backup_', DATE_FORMAT(NOW(), '%Y%m%d_%H%i%s'));
    
    -- 这里可以调用mysqldump命令
    -- 实际实现需要系统权限
    SELECT CONCAT('mysqldump -u root -p shopx > /backup/', backup_name, '.sql') as backup_command;
END //
DELIMITER ;

-- 创建恢复存储过程
DELIMITER //
CREATE PROCEDURE RestoreBackup(IN backup_file VARCHAR(255))
BEGIN
    -- 这里可以调用mysql命令
    -- 实际实现需要系统权限
    SELECT CONCAT('mysql -u root -p shopx < /backup/', backup_file) as restore_command;
END //
DELIMITER ;

-- =============================================
-- 完成数据库优化脚本
-- =============================================

-- 显示优化完成信息
SELECT 'Database optimization completed successfully!' as message;
SELECT NOW() as completion_time;
