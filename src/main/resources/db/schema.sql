-- ShopX创新电商平台数据库表结构

CREATE DATABASE IF NOT EXISTS shopx DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE shopx;

-- 用户表
CREATE TABLE t_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    
    -- 用户角色和权限
    role VARCHAR(20) DEFAULT 'USER' COMMENT '用户角色: USER, SELLER, ADMIN',
    avatar VARCHAR(500) COMMENT '头像URL',
    enabled BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    
    preferences TEXT COMMENT '用户偏好(JSON)',
    lifestyle VARCHAR(500) COMMENT '生活方式标签',
    scenarios VARCHAR(500) COMMENT '常用场景',
    
    follower_count INT DEFAULT 0,
    following_count INT DEFAULT 0,
    social_profile TEXT COMMENT '社交资料(JSON)',
    
    sustainability_score INT DEFAULT 0,
    recycle_count INT DEFAULT 0,
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version INT DEFAULT 0,
    
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_role (role),
    INDEX idx_lifestyle (lifestyle(255))
);

-- 商品表
CREATE TABLE t_product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    stock INT DEFAULT 0,
    category VARCHAR(100),
    
    suitable_scenarios TEXT COMMENT '适用场景(JSON)',
    seasonality VARCHAR(100),
    lifestyle_tags VARCHAR(500),
    
    ar_model_url VARCHAR(500),
    vr_experience_url VARCHAR(500),
    has_3d_preview BOOLEAN DEFAULT FALSE,
    
    is_recyclable BOOLEAN DEFAULT FALSE,
    is_rentable BOOLEAN DEFAULT FALSE,
    recycle_value DECIMAL(10,2) DEFAULT 0,
    
    like_count INT DEFAULT 0,
    share_count INT DEFAULT 0,
    view_count INT DEFAULT 0,
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_category (category),
    INDEX idx_price (price),
    INDEX idx_scenarios (suitable_scenarios(255))
);

-- 购物会话表
CREATE TABLE t_shopping_session (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_id VARCHAR(50) UNIQUE NOT NULL,
    host_user_id BIGINT NOT NULL,
    participant_ids TEXT COMMENT '参与者ID列表(JSON)',
    product_id BIGINT,
    session_status VARCHAR(20) DEFAULT 'ACTIVE',
    
    chat_history TEXT COMMENT '聊天记录(JSON)',
    annotations TEXT COMMENT '标注信息(JSON)',
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_session_id (session_id),
    INDEX idx_host_user (host_user_id),
    INDEX idx_status (session_status)
);

-- 推荐记录表
CREATE TABLE t_recommendation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    scenario VARCHAR(100) COMMENT '推荐场景',
    reason TEXT COMMENT '推荐理由',
    score DECIMAL(5,2) COMMENT '推荐分数',
    
    model_version VARCHAR(50),
    features TEXT COMMENT '特征向量(JSON)',
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_user_scenario (user_id, scenario(50)),
    INDEX idx_product (product_id)
);

-- 回收订单表
CREATE TABLE t_recycle_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT DEFAULT 1,
    estimated_value DECIMAL(10,2),
    final_value DECIMAL(10,2),
    
    status VARCHAR(20) DEFAULT 'PENDING',
    recycle_type VARCHAR(50),
    
    inspection_result TEXT COMMENT '检测结果(JSON)',
    environmental_impact TEXT COMMENT '环境影响数据(JSON)',
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_user (user_id),
    INDEX idx_status (status)
);

-- 订单表
CREATE TABLE t_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    order_no VARCHAR(50) UNIQUE NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    payment_status VARCHAR(20),
    
    shipping_address TEXT,
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_user (user_id),
    INDEX idx_order_no (order_no),
    INDEX idx_status (status)
);

-- 订单详情表
CREATE TABLE t_order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    
    INDEX idx_order (order_id),
    INDEX idx_product (product_id)
);

-- 购物车表
CREATE TABLE cart_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(200),
    product_image VARCHAR(500),
    price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    subtotal DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'VALID' COMMENT '状态: VALID, OUT_OF_STOCK, PRICE_CHANGED, DISABLED',
    last_checked_time DATETIME COMMENT '最后检查时间',
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_user (user_id),
    INDEX idx_product (product_id),
    UNIQUE KEY uk_user_product (user_id, product_id)
);

-- 游客会话表
CREATE TABLE t_guest_session (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_id VARCHAR(50) UNIQUE NOT NULL COMMENT '会话ID（UUID）',
    cart_data TEXT COMMENT '购物车数据(JSON格式)',
    expire_time DATETIME NOT NULL COMMENT '过期时间',
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_session_id (session_id),
    INDEX idx_expire_time (expire_time)
);

-- 支付方式表
CREATE TABLE t_payment_method (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    payment_type VARCHAR(20) NOT NULL COMMENT '支付方式类型: ALIPAY, WECHAT, CREDIT_CARD',
    payment_name VARCHAR(100) COMMENT '支付方式名称',
    account_number VARCHAR(200) COMMENT '支付账号（加密存储）',
    is_default BOOLEAN DEFAULT FALSE COMMENT '是否默认',
    enabled BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_user (user_id)
);

-- 用户行为日志表
CREATE TABLE t_user_behavior (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    product_id BIGINT,
    behavior_type VARCHAR(50) COMMENT '行为类型: VIEW, LIKE, SHARE, ADD_CART, PURCHASE',
    
    session_info TEXT COMMENT '会话信息(JSON)',
    device_info VARCHAR(500),
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_user_behavior (user_id, behavior_type),
    INDEX idx_product (product_id)
);

-- AR/VR体验记录表
CREATE TABLE t_experience_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    experience_type VARCHAR(20) COMMENT 'AR/VR',
    duration_seconds INT,
    interaction_data TEXT COMMENT '交互数据(JSON)',
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_user_product (user_id, product_id)
);

-- 社交互动表
CREATE TABLE t_social_interaction (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    target_type VARCHAR(20) COMMENT 'USER/PRODUCT',
    target_id BIGINT NOT NULL,
    interaction_type VARCHAR(20) COMMENT 'FOLLOW, LIKE, COMMENT',
    content TEXT,
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_user_target (user_id, target_type, target_id)
);

-- AI助手对话记录表
CREATE TABLE t_ai_conversation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    query TEXT NOT NULL,
    response TEXT,
    intent VARCHAR(100),
    confidence DECIMAL(3,2),
    suggested_products TEXT COMMENT '推荐商品(JSON)',
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_user (user_id)
);

-- 情境标签表
CREATE TABLE t_scenario (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    tag_type VARCHAR(50) COMMENT '场景类型',
    priority INT DEFAULT 0,
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_type_priority (tag_type, priority)
);

-- A/B测试配置表
CREATE TABLE t_ab_test (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    test_name VARCHAR(100) NOT NULL,
    description TEXT,
    algorithm_a VARCHAR(50) NOT NULL COMMENT '算法A',
    algorithm_b VARCHAR(50) NOT NULL COMMENT '算法B',
    traffic_split DECIMAL(5,2) DEFAULT 50.00 COMMENT '流量分配比例(A:B)',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT 'ACTIVE, PAUSED, COMPLETED',
    start_date DATETIME,
    end_date DATETIME,
    
    metrics_a TEXT COMMENT '算法A指标(JSON)',
    metrics_b TEXT COMMENT '算法B指标(JSON)',
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_status (status),
    INDEX idx_test_name (test_name)
);

-- A/B测试结果表
CREATE TABLE t_ab_test_result (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    test_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    algorithm VARCHAR(50) NOT NULL COMMENT '使用的算法',
    product_id BIGINT,
    action_type VARCHAR(50) COMMENT 'VIEW, CLICK, PURCHASE等',
    action_value DECIMAL(10,2) COMMENT '行为价值',
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_test_user (test_id, user_id),
    INDEX idx_algorithm (algorithm),
    INDEX idx_action (action_type)
);

-- 搜索历史表
CREATE TABLE t_search_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT COMMENT '用户ID（可为空，支持游客搜索）',
    keyword VARCHAR(200) NOT NULL COMMENT '搜索关键词',
    search_type VARCHAR(20) DEFAULT 'BASIC' COMMENT '搜索类型: BASIC, ADVANCED',
    filter_conditions TEXT COMMENT '筛选条件(JSON)',
    result_count INT DEFAULT 0 COMMENT '搜索结果数量',
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_user (user_id),
    INDEX idx_keyword (keyword),
    INDEX idx_create_time (create_time)
);

-- 保存的筛选条件表
CREATE TABLE t_saved_filter (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    filter_name VARCHAR(100) NOT NULL COMMENT '筛选条件名称',
    filter_conditions TEXT NOT NULL COMMENT '筛选条件(JSON)',
    is_default BOOLEAN DEFAULT FALSE COMMENT '是否默认',
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_user (user_id)
);

-- 商品图片表
CREATE TABLE t_product_image (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    image_url VARCHAR(500) NOT NULL COMMENT '图片URL',
    image_type VARCHAR(20) DEFAULT 'MAIN' COMMENT '图片类型: MAIN, DETAIL, SCENE',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_product (product_id)
);

-- 商品评价表
CREATE TABLE t_product_review (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    order_id BIGINT COMMENT '订单ID（用于验证是否购买）',
    rating INT NOT NULL COMMENT '评分（1-5）',
    content TEXT COMMENT '评价内容',
    images TEXT COMMENT '评价图片(JSON数组)',
    videos TEXT COMMENT '评价视频(JSON数组)',
    helpful_count INT DEFAULT 0 COMMENT '有用数',
    is_verified BOOLEAN DEFAULT FALSE COMMENT '是否已购买验证',
    merchant_reply TEXT COMMENT '商家回复',
    reply_time DATETIME COMMENT '商家回复时间',
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_product (product_id),
    INDEX idx_user (user_id),
    INDEX idx_order (order_id),
    INDEX idx_rating (rating)
);

-- 商品审核表
CREATE TABLE t_product_audit (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '审核状态: PENDING, APPROVED, REJECTED',
    auditor_id BIGINT COMMENT '审核人ID',
    comment TEXT COMMENT '审核意见',
    completeness_score INT COMMENT '完整度评分（0-100）',
    audit_time DATETIME COMMENT '审核时间',
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_product (product_id),
    INDEX idx_status (status)
);

-- 商品信息修改历史表
CREATE TABLE t_product_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    modified_by BIGINT NOT NULL COMMENT '修改人ID',
    changes TEXT COMMENT '修改字段(JSON格式，记录修改前后的值)',
    reason VARCHAR(500) COMMENT '修改原因',
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_product (product_id),
    INDEX idx_modified_by (modified_by)
);

-- 扩展商品表，添加完整度评分字段
ALTER TABLE t_product ADD COLUMN completeness_score INT DEFAULT 0 COMMENT '完整度评分（0-100）';
ALTER TABLE t_product ADD COLUMN shipping_fee DECIMAL(10,2) DEFAULT 0 COMMENT '运费';
ALTER TABLE t_product ADD COLUMN tax_rate DECIMAL(5,2) DEFAULT 0 COMMENT '税率（百分比）';

-- 价格历史表
CREATE TABLE t_price_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    shipping_fee DECIMAL(10,2) DEFAULT 0 COMMENT '运费',
    tax DECIMAL(10,2) DEFAULT 0 COMMENT '税费',
    total_price DECIMAL(10,2) NOT NULL COMMENT '总价',
    reason VARCHAR(500) COMMENT '价格变动原因',
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_product (product_id),
    INDEX idx_create_time (create_time)
);

-- 价格保护表
CREATE TABLE t_price_protection (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    purchase_price DECIMAL(10,2) NOT NULL COMMENT '购买价格',
    current_price DECIMAL(10,2) COMMENT '当前价格',
    price_difference DECIMAL(10,2) COMMENT '差价',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '保护状态: ACTIVE, EXPIRED, REFUNDED',
    start_time DATETIME NOT NULL COMMENT '保护开始时间',
    end_time DATETIME NOT NULL COMMENT '保护结束时间（7天后）',
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_order (order_id),
    INDEX idx_user (user_id),
    INDEX idx_product (product_id),
    INDEX idx_status (status),
    INDEX idx_end_time (end_time)
);

-- 扩展订单表，添加配送相关字段
ALTER TABLE t_order ADD COLUMN shipping_method VARCHAR(20) COMMENT '配送方式: STANDARD, EXPRESS, PICKUP';
ALTER TABLE t_order ADD COLUMN tracking_number VARCHAR(50) COMMENT '物流单号';
ALTER TABLE t_order ADD COLUMN estimated_delivery_time DATETIME COMMENT '预计送达时间';

-- 物流追踪表
CREATE TABLE t_logistics_tracking (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    tracking_number VARCHAR(50) NOT NULL COMMENT '物流单号',
    logistics_company VARCHAR(50) COMMENT '物流公司',
    status VARCHAR(20) COMMENT '物流状态',
    current_location VARCHAR(200) COMMENT '当前位置',
    details TEXT COMMENT '详细信息(JSON格式)',
    estimated_delivery_time DATETIME COMMENT '预计送达时间',
    actual_delivery_time DATETIME COMMENT '实际送达时间',
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_order (order_id),
    INDEX idx_tracking_number (tracking_number),
    INDEX idx_status (status)
);

-- 收货地址表
CREATE TABLE t_shipping_address (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    receiver_name VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    receiver_phone VARCHAR(20) NOT NULL COMMENT '收货人电话',
    province VARCHAR(50) COMMENT '省份',
    city VARCHAR(50) COMMENT '城市',
    district VARCHAR(50) COMMENT '区县',
    detail_address VARCHAR(200) NOT NULL COMMENT '详细地址',
    postal_code VARCHAR(10) COMMENT '邮政编码',
    is_default BOOLEAN DEFAULT FALSE COMMENT '是否默认地址',
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_user (user_id)
);

-- 退货订单表
CREATE TABLE t_return_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL COMMENT '原订单ID',
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL COMMENT '退货数量',
    reason VARCHAR(100) COMMENT '退货原因',
    description TEXT COMMENT '退货说明',
    refund_amount DECIMAL(10,2) COMMENT '退货金额',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '退货状态: PENDING, APPROVED, REJECTED, PROCESSING, COMPLETED, CANCELLED',
    return_label VARCHAR(500) COMMENT '退货标签（打印或电子标签）',
    return_tracking_number VARCHAR(50) COMMENT '物流单号（退货物流）',
    audit_comment TEXT COMMENT '审核意见',
    audit_time DATETIME COMMENT '审核时间',
    refund_time DATETIME COMMENT '退款时间',
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_order (order_id),
    INDEX idx_user (user_id),
    INDEX idx_status (status)
);

-- 扩展订单表，添加退货状态字段
ALTER TABLE t_order ADD COLUMN return_status VARCHAR(20) COMMENT '退货状态: NONE, PENDING, PARTIAL, COMPLETED';

-- 推荐反馈表
CREATE TABLE t_recommendation_feedback (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    algorithm VARCHAR(50) COMMENT '推荐算法',
    feedback_type VARCHAR(20) NOT NULL COMMENT '反馈类型: LIKE, DISLIKE, IGNORE',
    reason VARCHAR(500) COMMENT '反馈原因',
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_user (user_id),
    INDEX idx_product (product_id),
    INDEX idx_feedback_type (feedback_type)
);

-- 用户推荐偏好表
CREATE TABLE t_user_recommendation_preference (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    preferences TEXT COMMENT '偏好设置(JSON格式)',
    filter_purchased BOOLEAN DEFAULT TRUE COMMENT '是否过滤已购买商品',
    filter_reviewed BOOLEAN DEFAULT FALSE COMMENT '是否过滤已评价商品',
    algorithm_weights TEXT COMMENT '推荐算法偏好权重(JSON格式)',
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_user (user_id)
);

-- 库存提醒表
CREATE TABLE t_stock_notification (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '通知状态: PENDING, SENT, CANCELLED',
    notify_time DATETIME COMMENT '通知时间',
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_user (user_id),
    INDEX idx_product (product_id),
    INDEX idx_status (status)
);

-- 商品预订表
CREATE TABLE t_product_reservation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL COMMENT '预订数量',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '预订状态: PENDING, FULFILLED, CANCELLED, EXPIRED',
    expected_arrival_time DATETIME COMMENT '预计到货时间',
    actual_arrival_time DATETIME COMMENT '实际到货时间',
    expire_time DATETIME COMMENT '过期时间',
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_user (user_id),
    INDEX idx_product (product_id),
    INDEX idx_status (status)
);

-- 愿望清单表
CREATE TABLE t_wishlist (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    category VARCHAR(50) COMMENT '分类',
    price_alert BOOLEAN DEFAULT FALSE COMMENT '价格下降提醒',
    target_price DECIMAL(10,2) COMMENT '目标价格（价格提醒阈值）',
    notes TEXT COMMENT '备注',
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_user (user_id),
    INDEX idx_product (product_id),
    INDEX idx_category (category),
    UNIQUE KEY uk_user_product (user_id, product_id)
);

-- 商品对比表
CREATE TABLE t_product_comparison (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    comparison_name VARCHAR(100) COMMENT '对比列表名称',
    product_ids TEXT NOT NULL COMMENT '商品ID列表(JSON数组，最多5个)',
    is_public BOOLEAN DEFAULT FALSE COMMENT '是否公开（可分享）',
    share_link VARCHAR(100) COMMENT '分享链接',
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_user (user_id),
    INDEX idx_share_link (share_link)
);

-- 客服工单表
CREATE TABLE t_customer_service_ticket (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    ticket_no VARCHAR(50) UNIQUE NOT NULL COMMENT '工单号',
    ticket_type VARCHAR(20) NOT NULL COMMENT '工单类型: ORDER, PRODUCT, PAYMENT, REFUND, OTHER',
    title VARCHAR(200) NOT NULL COMMENT '标题',
    content TEXT NOT NULL COMMENT '内容',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '工单状态: PENDING, PROCESSING, RESOLVED, CLOSED',
    priority VARCHAR(20) DEFAULT 'MEDIUM' COMMENT '优先级: LOW, MEDIUM, HIGH, URGENT',
    service_staff_id BIGINT COMMENT '客服ID',
    reply TEXT COMMENT '回复内容',
    reply_time DATETIME COMMENT '回复时间',
    resolved_time DATETIME COMMENT '解决时间',
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_user (user_id),
    INDEX idx_ticket_no (ticket_no),
    INDEX idx_status (status),
    INDEX idx_service_staff (service_staff_id)
);

-- 常见问题表
CREATE TABLE t_faq (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category VARCHAR(50) COMMENT '问题分类',
    question VARCHAR(500) NOT NULL COMMENT '问题',
    answer TEXT NOT NULL COMMENT '答案',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    helpful_count INT DEFAULT 0 COMMENT '有用数',
    enabled BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_category (category),
    INDEX idx_enabled (enabled)
);

-- 评价投票表
CREATE TABLE t_review_vote (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    review_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    vote_type VARCHAR(20) NOT NULL COMMENT '投票类型: HELPFUL, NOT_HELPFUL',
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_review (review_id),
    INDEX idx_user (user_id),
    UNIQUE KEY uk_review_user (review_id, user_id)
);

-- 登录历史表
CREATE TABLE t_login_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    ip_address VARCHAR(50) COMMENT '登录IP',
    device VARCHAR(200) COMMENT '登录设备',
    browser VARCHAR(200) COMMENT '浏览器信息',
    location VARCHAR(200) COMMENT '登录地点',
    status VARCHAR(20) NOT NULL COMMENT '登录状态: SUCCESS, FAILED',
    failure_reason VARCHAR(500) COMMENT '失败原因',
    is_abnormal BOOLEAN DEFAULT FALSE COMMENT '是否异常登录',
    abnormal_reason VARCHAR(500) COMMENT '异常原因',
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_user (user_id),
    INDEX idx_status (status),
    INDEX idx_is_abnormal (is_abnormal),
    INDEX idx_create_time (create_time)
);

-- 双因素认证表
CREATE TABLE t_two_factor_auth (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    enabled BOOLEAN DEFAULT FALSE COMMENT '是否启用',
    auth_method VARCHAR(20) COMMENT '认证方式: SMS, EMAIL, APP',
    secret_key VARCHAR(200) COMMENT '密钥（用于APP认证）',
    phone_number VARCHAR(20) COMMENT '手机号（用于SMS认证）',
    email VARCHAR(100) COMMENT '邮箱（用于EMAIL认证）',
    backup_codes TEXT COMMENT '备用验证码',
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_user (user_id),
    INDEX idx_enabled (enabled)
);

-- 账户安全设置表
CREATE TABLE t_account_security (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    login_alert_enabled BOOLEAN DEFAULT TRUE COMMENT '是否启用登录提醒',
    abnormal_login_alert_enabled BOOLEAN DEFAULT TRUE COMMENT '是否启用异常登录提醒',
    password_change_alert_enabled BOOLEAN DEFAULT TRUE COMMENT '是否启用密码修改提醒',
    account_deletion_alert_enabled BOOLEAN DEFAULT TRUE COMMENT '是否启用账户删除提醒',
    privacy_settings TEXT COMMENT '隐私设置(JSON格式)',
    last_login_time DATETIME COMMENT '最后登录时间',
    last_login_ip VARCHAR(50) COMMENT '最后登录IP',
    account_status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '账户状态: ACTIVE, SUSPENDED, DELETED',
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_user (user_id),
    INDEX idx_account_status (account_status)
);

