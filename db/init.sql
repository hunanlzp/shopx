-- 创建数据库
CREATE DATABASE IF NOT EXISTS shopx DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE shopx;

-- 创建用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    email VARCHAR(100) UNIQUE COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号码',
    status TINYINT DEFAULT 1 COMMENT '用户状态：0-禁用，1-启用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除状态：0-未删除，1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建商品表
CREATE TABLE IF NOT EXISTS product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '商品名称',
    description TEXT COMMENT '商品描述',
    price DECIMAL(10,2) NOT NULL COMMENT '商品价格',
    stock INT NOT NULL DEFAULT 0 COMMENT '库存数量',
    sales INT NOT NULL DEFAULT 0 COMMENT '销量',
    category_id BIGINT COMMENT '分类ID',
    image_url VARCHAR(255) COMMENT '商品图片',
    status TINYINT DEFAULT 0 COMMENT '商品状态：0-下架，1-上架',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除状态：0-未删除，1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建商品分类表
CREATE TABLE IF NOT EXISTS category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID',
    level TINYINT DEFAULT 1 COMMENT '分类级别',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建索引
CREATE INDEX idx_user_username ON sys_user(username);
CREATE INDEX idx_user_email ON sys_user(email);
CREATE INDEX idx_product_name ON product(name);
CREATE INDEX idx_product_category_id ON product(category_id);
CREATE INDEX idx_product_status ON product(status);
CREATE INDEX idx_category_parent_id ON category(parent_id);

-- 插入测试数据
INSERT INTO sys_user (username, password, email, phone, status) VALUES
('admin', '123456', 'admin@shopx.com', '13800138000', 1),
('user1', '123456', 'user1@shopx.com', '13800138001', 1);

INSERT INTO category (name, parent_id, level, sort, status) VALUES
('电子产品', 0, 1, 1, 1),
('手机', 1, 2, 1, 1),
('电脑', 1, 2, 2, 1),
('服装鞋帽', 0, 1, 2, 1),
('男装', 4, 2, 1, 1),
('女装', 4, 2, 2, 1);

INSERT INTO product (name, description, price, stock, sales, category_id, image_url, status) VALUES
('iPhone 15 Pro', '苹果最新款手机，搭载A17 Pro芯片', 7999.00, 100, 0, 2, 'https://example.com/iphone15pro.jpg', 1),
('MacBook Pro', '苹果笔记本电脑，M3芯片', 12999.00, 50, 0, 3, 'https://example.com/macbookpro.jpg', 1),
('华为P60', '华为旗舰手机，昆仑玻璃', 5999.00, 80, 0, 2, 'https://example.com/huaweip60.jpg', 1);