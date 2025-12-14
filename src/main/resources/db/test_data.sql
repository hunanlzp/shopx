-- ShopX测试数据初始化脚本

USE shopx;

-- 插入测试用户
INSERT INTO t_user (username, password, email, phone, role, avatar, enabled, preferences, lifestyle, scenarios, follower_count, following_count, sustainability_score, recycle_count) VALUES
('alice_wang', 'password123', 'alice@example.com', '13800138001', 'USER', 'https://picsum.photos/100/100?random=1', TRUE, '{"theme":"dark","language":"zh-CN"}', '运动健身,环保生活,科技数码', '健身房,户外运动,居家办公', 150, 89, 85, 12),
('bob_li', 'password123', 'bob@example.com', '13800138002', 'SELLER', 'https://picsum.photos/100/100?random=2', TRUE, '{"theme":"light","language":"zh-CN"}', '时尚潮流,美食烹饪,旅行摄影', '约会,聚会,旅行', 89, 156, 72, 8),
('carol_zhang', 'password123', 'carol@example.com', '13800138003', 'USER', 'https://picsum.photos/100/100?random=3', TRUE, '{"theme":"auto","language":"en-US"}', '艺术设计,音乐电影,宠物养护', '工作室,咖啡厅,宠物店', 234, 67, 91, 15),
('david_chen', 'password123', 'david@example.com', '13800138004', 'ADMIN', 'https://picsum.photos/100/100?random=4', TRUE, '{"theme":"dark","language":"zh-CN"}', '商务办公,投资理财,汽车改装', '办公室,会议室,车库', 45, 123, 68, 5),
('eve_liu', 'password123', 'eve@example.com', '13800138005', 'USER', 'https://picsum.photos/100/100?random=5', TRUE, '{"theme":"light","language":"zh-CN"}', '母婴育儿,家居装饰,健康养生', '家庭,幼儿园,医院', 178, 45, 88, 20);

-- 插入情境标签
INSERT INTO t_scenario (name, description, tag_type, priority) VALUES
('运动健身', '健身房、户外运动、体育用品相关场景', 'LIFESTYLE', 10),
('时尚潮流', '约会、聚会、时尚购物相关场景', 'SOCIAL', 9),
('居家办公', '家庭办公、远程工作相关场景', 'WORK', 8),
('旅行摄影', '旅行、摄影、户外探险相关场景', 'TRAVEL', 7),
('美食烹饪', '厨房、餐厅、烹饪相关场景', 'FOOD', 6),
('艺术设计', '工作室、画廊、创意空间相关场景', 'CREATIVE', 5),
('商务办公', '办公室、会议室、商务活动相关场景', 'BUSINESS', 4),
('母婴育儿', '家庭、幼儿园、儿童相关场景', 'FAMILY', 3),
('环保生活', '可持续消费、回收利用相关场景', 'SUSTAINABLE', 2),
('科技数码', '电子产品、智能设备相关场景', 'TECH', 1);

-- 插入测试商品
INSERT INTO t_product (name, description, price, stock, category, suitable_scenarios, seasonality, lifestyle_tags, ar_model_url, vr_experience_url, has_3d_preview, is_recyclable, is_rentable, recycle_value, like_count, share_count, view_count) VALUES
('智能运动手环', '全天候健康监测，支持50+运动模式，7天续航', 299.00, 150, '智能穿戴', '["运动健身","健康监测","户外运动"]', 'ALL_SEASON', '运动健身,科技数码,健康生活', 'https://ar.shopx.com/models/smartband.ar', 'https://vr.shopx.com/experience/smartband', TRUE, TRUE, FALSE, 50.00, 234, 89, 1567),
('时尚连衣裙', '经典小黑裙，适合多种场合，优质面料', 199.00, 80, '女装', '["时尚潮流","约会","聚会"]', 'SPRING_SUMMER', '时尚潮流,优雅生活,社交聚会', 'https://ar.shopx.com/models/dress.ar', 'https://vr.shopx.com/experience/dress', TRUE, FALSE, TRUE, 30.00, 156, 67, 1234),
('无线蓝牙耳机', '主动降噪，30小时续航，Hi-Res音质', 899.00, 200, '数码配件', '["居家办公","音乐电影","科技数码"]', 'ALL_SEASON', '科技数码,音乐电影,居家办公', 'https://ar.shopx.com/models/earphones.ar', 'https://vr.shopx.com/experience/earphones', TRUE, TRUE, FALSE, 120.00, 345, 123, 2345),
('有机护肤套装', '天然成分，敏感肌适用，环保包装', 399.00, 120, '美妆护肤', '["环保生活","健康养生","居家护理"]', 'ALL_SEASON', '环保生活,健康养生,美容护肤', 'https://ar.shopx.com/models/skincare.ar', 'https://vr.shopx.com/experience/skincare', TRUE, TRUE, FALSE, 80.00, 189, 78, 1456),
('智能空气净化器', 'HEPA过滤，APP控制，静音运行', 1299.00, 50, '家电', '["居家办公","健康养生","环保生活"]', 'ALL_SEASON', '健康养生,环保生活,智能家居', 'https://ar.shopx.com/models/airpurifier.ar', 'https://vr.shopx.com/experience/airpurifier', TRUE, TRUE, TRUE, 200.00, 267, 98, 1890),
('便携式咖啡机', '一键制作，多种口味，旅行便携', 599.00, 90, '小家电', '["美食烹饪","旅行摄影","居家办公"]', 'ALL_SEASON', '美食烹饪,旅行摄影,居家办公', 'https://ar.shopx.com/models/coffeemaker.ar', 'https://vr.shopx.com/experience/coffeemaker', TRUE, TRUE, FALSE, 100.00, 178, 56, 1123),
('儿童益智玩具', '安全材质，寓教于乐，促进智力发展', 159.00, 200, '玩具', '["母婴育儿","家庭娱乐","教育学习"]', 'ALL_SEASON', '母婴育儿,教育学习,家庭娱乐', 'https://ar.shopx.com/models/toys.ar', 'https://vr.shopx.com/experience/toys', TRUE, TRUE, FALSE, 25.00, 123, 45, 987),
('商务笔记本电脑', '高性能处理器，轻薄便携，长续航', 5999.00, 30, '电脑', '["商务办公","居家办公","科技数码"]', 'ALL_SEASON', '商务办公,科技数码,高效工作', 'https://ar.shopx.com/models/laptop.ar', 'https://vr.shopx.com/experience/laptop', TRUE, TRUE, TRUE, 800.00, 89, 34, 567),
('瑜伽垫套装', '防滑设计，环保材质，多种厚度可选', 89.00, 300, '运动用品', '["运动健身","健康养生","居家健身"]', 'ALL_SEASON', '运动健身,健康养生,居家健身', 'https://ar.shopx.com/models/yogamat.ar', 'https://vr.shopx.com/experience/yogamat', TRUE, TRUE, FALSE, 15.00, 145, 67, 1456),
('智能门锁', '指纹识别，APP控制，安全可靠', 1299.00, 40, '智能家居', '["居家办公","科技数码","安全防护"]', 'ALL_SEASON', '科技数码,智能家居,安全防护', 'https://ar.shopx.com/models/smartlock.ar', 'https://vr.shopx.com/experience/smartlock', TRUE, TRUE, FALSE, 150.00, 98, 23, 789);

-- 插入推荐记录示例
INSERT INTO t_recommendation (user_id, product_id, scenario, reason, score, model_version) VALUES
(1, 1, '运动健身', '用户偏好运动健身，商品匹配度高', 0.92, 'v1.0'),
(1, 9, '运动健身', '瑜伽垫符合用户运动需求', 0.88, 'v1.0'),
(2, 2, '时尚潮流', '连衣裙适合用户时尚需求', 0.95, 'v1.0'),
(2, 4, '美食烹饪', '咖啡机符合用户生活方式', 0.85, 'v1.0'),
(3, 3, '艺术设计', '耳机适合创意工作环境', 0.90, 'v1.0'),
(3, 5, '环保生活', '空气净化器符合环保理念', 0.87, 'v1.0'),
(4, 8, '商务办公', '笔记本电脑适合商务需求', 0.93, 'v1.0'),
(4, 10, '科技数码', '智能门锁符合科技偏好', 0.89, 'v1.0'),
(5, 7, '母婴育儿', '益智玩具适合家庭需求', 0.91, 'v1.0'),
(5, 4, '健康养生', '护肤套装符合健康理念', 0.86, 'v1.0');

-- 插入用户行为日志示例
INSERT INTO t_user_behavior (user_id, product_id, behavior_type, session_info, device_info) VALUES
(1, 1, 'VIEW', '{"sessionId":"sess_001","duration":120}', 'iPhone 14 Pro'),
(1, 1, 'LIKE', '{"sessionId":"sess_001","duration":120}', 'iPhone 14 Pro'),
(1, 9, 'VIEW', '{"sessionId":"sess_002","duration":89}', 'iPhone 14 Pro'),
(2, 2, 'VIEW', '{"sessionId":"sess_003","duration":156}', 'Samsung Galaxy S23'),
(2, 2, 'ADD_CART', '{"sessionId":"sess_003","duration":156}', 'Samsung Galaxy S23'),
(3, 3, 'VIEW', '{"sessionId":"sess_004","duration":234}', 'iPad Pro'),
(3, 3, 'SHARE', '{"sessionId":"sess_004","duration":234}', 'iPad Pro'),
(4, 8, 'VIEW', '{"sessionId":"sess_005","duration":178}', 'MacBook Pro'),
(4, 8, 'PURCHASE', '{"sessionId":"sess_005","duration":178}', 'MacBook Pro'),
(5, 7, 'VIEW', '{"sessionId":"sess_006","duration":145}', 'iPhone 13');

-- 插入AR/VR体验记录示例
INSERT INTO t_experience_record (user_id, product_id, experience_type, duration_seconds, interaction_data) VALUES
(1, 1, 'AR', 45, '{"interactions":["rotate","zoom","color_change"],"satisfaction":4.5}'),
(2, 2, 'VR', 120, '{"interactions":["try_on","walk_around","lighting_change"],"satisfaction":4.8}'),
(3, 3, 'AR', 67, '{"interactions":["sound_test","fit_check","style_match"],"satisfaction":4.2}'),
(4, 8, 'AR', 89, '{"interactions":["keyboard_test","screen_view","port_check"],"satisfaction":4.6}'),
(5, 7, 'VR', 156, '{"interactions":["play_demo","safety_check","age_appropriate"],"satisfaction":4.7}');

-- 插入社交互动示例
INSERT INTO t_social_interaction (user_id, target_type, target_id, interaction_type, content) VALUES
(1, 'USER', 2, 'FOLLOW', NULL),
(2, 'USER', 1, 'FOLLOW', NULL),
(3, 'PRODUCT', 1, 'LIKE', NULL),
(3, 'PRODUCT', 2, 'COMMENT', '这件连衣裙真的很漂亮，质量也很好！'),
(4, 'PRODUCT', 8, 'COMMENT', '笔记本电脑性能很强，推荐给需要高效办公的朋友'),
(5, 'USER', 3, 'FOLLOW', NULL);

-- 插入AI对话记录示例
INSERT INTO t_ai_conversation (user_id, query, response, intent, confidence, suggested_products) VALUES
(1, '我想买一个运动手环，有什么推荐吗？', '根据您的运动健身偏好，我推荐智能运动手环，它支持50+运动模式，7天续航，还有AR试戴功能。', 'PRODUCT_RECOMMENDATION', 0.92, '[1,9]'),
(2, '我需要一件适合约会的衣服', '为您推荐时尚连衣裙，经典小黑裙设计，适合多种场合，还有VR试衣功能让您提前体验效果。', 'FASHION_ADVICE', 0.89, '[2]'),
(3, '我想买一个耳机用于工作', '推荐无线蓝牙耳机，主动降噪功能适合办公环境，30小时续航，还有AR试听体验。', 'WORK_EQUIPMENT', 0.87, '[3]'),
(4, '我需要一台商务笔记本电脑', '推荐高性能商务笔记本，轻薄便携，长续航，适合商务办公需求，支持AR体验。', 'BUSINESS_EQUIPMENT', 0.94, '[8]'),
(5, '我想给孩子买一个益智玩具', '推荐儿童益智玩具，安全材质，寓教于乐，促进智力发展，还有VR体验功能。', 'CHILDREN_PRODUCTS', 0.91, '[7]');

-- 插入回收订单示例
INSERT INTO t_recycle_order (user_id, product_id, quantity, estimated_value, final_value, status, recycle_type, inspection_result, environmental_impact) VALUES
(1, 1, 1, 50.00, 45.00, 'COMPLETED', 'ELECTRONICS', '{"condition":"good","battery_health":"85%","functionality":"normal"}', '{"co2_saved":"2.5kg","materials_recovered":"plastic,metal"}'),
(2, 2, 1, 30.00, 25.00, 'PENDING', 'TEXTILES', '{"condition":"fair","stains":"minor","wear":"light"}', '{"co2_saved":"1.2kg","materials_recovered":"fabric"}'),
(3, 3, 1, 120.00, 110.00, 'COMPLETED', 'ELECTRONICS', '{"condition":"excellent","battery_health":"95%","functionality":"perfect"}', '{"co2_saved":"3.8kg","materials_recovered":"plastic,metal,battery"}'),
(4, 8, 1, 800.00, 750.00, 'INSPECTION', 'ELECTRONICS', '{"condition":"good","screen":"no_damage","performance":"normal"}', '{"co2_saved":"15.2kg","materials_recovered":"metal,plastic,glass"}'),
(5, 7, 2, 25.00, 20.00, 'COMPLETED', 'TOYS', '{"condition":"good","safety":"passed","educational_value":"high"}', '{"co2_saved":"0.8kg","materials_recovered":"plastic,wood"}');

-- 插入订单示例
INSERT INTO t_order (user_id, order_no, total_amount, status, payment_status, shipping_address) VALUES
(1, 'ORD20231201001', 299.00, 'COMPLETED', 'PAID', '{"name":"Alice Wang","phone":"13800138001","address":"北京市朝阳区xxx街道xxx号","zipcode":"100000"}'),
(2, 'ORD20231201002', 199.00, 'SHIPPED', 'PAID', '{"name":"Bob Li","phone":"13800138002","address":"上海市浦东新区xxx路xxx号","zipcode":"200000"}'),
(3, 'ORD20231201003', 899.00, 'PENDING', 'UNPAID', '{"name":"Carol Zhang","phone":"13800138003","address":"广州市天河区xxx大道xxx号","zipcode":"510000"}'),
(4, 'ORD20231201004', 5999.00, 'COMPLETED', 'PAID', '{"name":"David Chen","phone":"13800138004","address":"深圳市南山区xxx科技园xxx栋","zipcode":"518000"}'),
(5, 'ORD20231201005', 159.00, 'SHIPPED', 'PAID', '{"name":"Eve Liu","phone":"13800138005","address":"杭州市西湖区xxx路xxx号","zipcode":"310000"}');

-- 插入订单详情
INSERT INTO t_order_item (order_id, product_id, quantity, price, subtotal) VALUES
(1, 1, 1, 299.00, 299.00),
(2, 2, 1, 199.00, 199.00),
(3, 3, 1, 899.00, 899.00),
(4, 8, 1, 5999.00, 5999.00),
(5, 7, 1, 159.00, 159.00);

-- 插入协作购物会话示例
INSERT INTO t_shopping_session (session_id, host_user_id, participant_ids, product_id, session_status, chat_history, annotations) VALUES
('sess_collab_001', 1, '[2,3]', 1, 'ACTIVE', '[{"user":1,"message":"这个手环看起来不错","time":"2023-12-01T10:00:00"},{"user":2,"message":"续航时间怎么样？","time":"2023-12-01T10:01:00"},{"user":1,"message":"7天续航，很给力","time":"2023-12-01T10:02:00"}]', '[{"user":2,"type":"highlight","content":"续航","position":{"x":100,"y":200}}]'),
('sess_collab_002', 2, '[1,4]', 2, 'ENDED', '[{"user":2,"message":"这件连衣裙怎么样？","time":"2023-12-01T11:00:00"},{"user":1,"message":"很漂亮，适合约会","time":"2023-12-01T11:01:00"},{"user":4,"message":"质量看起来不错","time":"2023-12-01T11:02:00"}]', '[{"user":1,"type":"note","content":"适合约会","position":{"x":150,"y":300}}]'),
('sess_collab_003', 3, '[5]', 3, 'ACTIVE', '[{"user":3,"message":"这个耳机降噪效果如何？","time":"2023-12-01T14:00:00"},{"user":5,"message":"我朋友用过，说效果很好","time":"2023-12-01T14:01:00"}]', '[]');
