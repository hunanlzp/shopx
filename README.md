# ShopX 创新电商平台

## 📖 项目简介

ShopX是一个现代化的创新电商平台，集成了AR/VR体验、AI购物助手、协作购物、智能推荐、价值循环等前沿功能。项目采用前后端分离架构，使用React 18 + Spring Boot 3.2.0技术栈，提供完整的电商解决方案。

## ✨ 核心特性

### 🎮 AR/VR体验
- **3D商品展示**：支持多种产品类型的3D模型渲染
- **交互控制**：鼠标/触摸控制，支持旋转、缩放、平移
- **环境渲染**：多种环境预设，提升视觉体验
- **动画效果**：流畅的动画和过渡效果
- **用户操作**：喜欢、分享、加购物车、截图、录制等操作

### 🤖 AI购物助手
- **智能对话**：自然语言交互，理解用户意图
- **商品推荐**：基于用户偏好的智能推荐
- **快速操作**：一键操作，提升用户体验
- **个性化设置**：可定制的AI个性和行为
- **学习能力**：基于用户行为的持续学习

### 👥 协作购物
- **实时协作**：多用户实时协作购物
- **商品共享**：商品信息共享和讨论
- **标注功能**：商品标注和协作编辑
- **多媒体支持**：音视频通话、屏幕共享
- **会话管理**：完整的会话生命周期管理

### 🎯 智能推荐
- **多算法融合**：协同过滤、内容推荐、深度学习等
- **场景化推荐**：基于使用场景的个性化推荐
- **实时推荐**：基于实时行为的动态推荐
- **推荐解释**：推荐理由和置信度展示
- **A/B测试**：推荐算法效果对比

### ♻️ 价值循环
- **回收管理**：完整的回收订单生命周期
- **环保活动**：多样化的环保活动参与
- **等级系统**：基于环保贡献的等级系统
- **环境影响**：量化的环境影响统计
- **社区互动**：环保社区和社交功能

## 🛠️ 技术栈

### 前端技术
- **框架**：React 18 + TypeScript
- **构建工具**：Vite 4.1.0
- **UI组件库**：Ant Design 5.2.0
- **状态管理**：Zustand 4.3.0
- **路由**：React Router 6.8.0
- **3D渲染**：Three.js 0.150.0 + React Three Fiber 8.8.0 + Drei 9.50.0
- **动画**：Framer Motion 10.0.0
- **HTTP客户端**：Axios 1.3.0
- **数据获取**：React Query 3.39.0
- **表单处理**：React Hook Form 7.43.0
- **WebSocket**：SockJS + STOMP.js 7.0.0
- **工具库**：Lodash-es、Day.js、Classnames
- **Hooks库**：ahooks 3.7.0、react-use 17.4.0
- **虚拟滚动**：react-virtualized 9.22.0
- **错误处理**：react-error-boundary 4.0.0
- **通知**：react-hot-toast 2.4.0
- **SEO**：react-helmet-async 1.3.0
- **测试**：Vitest 0.28.0 + Testing Library
- **代码规范**：ESLint + Prettier

### 后端技术
- **框架**：Spring Boot 3.2.0
- **数据库**：MySQL 8.0
- **缓存**：Redis + Redisson (分布式锁)
- **ORM**：MyBatis-Plus 3.5.5
- **认证授权**：Sa-Token 1.37.0 (JWT支持)
- **API文档**：SpringDoc OpenAPI 2.2.0
- **数据验证**：Spring Validation
- **事务管理**：Spring Transaction
- **异步处理**：Spring Async
- **消息队列**：RabbitMQ
- **定时任务**：Quartz
- **邮件服务**：Spring Mail
- **文件处理**：Apache POI、Thumbnailator
- **JSON处理**：Fastjson2
- **对象映射**：MapStruct
- **工具类**：Lombok

### 开发工具
- **版本控制**：Git
- **包管理**：Maven 3.6+ (后端) + npm (前端)
- **容器化**：Docker + Docker Compose
- **反向代理**：Nginx
- **监控**：Spring Boot Actuator + Prometheus
- **CI/CD**：GitHub Actions
- **代码质量**：EditorConfig

## 📁 项目结构

```
shopx/
├── frontend/                 # 前端项目
│   ├── src/
│   │   ├── components/      # 组件
│   │   │   ├── ARVRExperience.tsx
│   │   │   ├── ProductComponents.tsx
│   │   │   └── ...
│   │   ├── hooks/           # 自定义Hooks
│   │   │   └── useHooks.ts
│   │   ├── services/        # API服务
│   │   │   └── api.ts
│   │   ├── store/           # 状态管理
│   │   │   └── useStore.ts
│   │   ├── utils/           # 工具函数
│   │   │   └── utils.ts
│   │   └── ...
│   ├── package.json
│   ├── vite.config.ts
│   ├── tsconfig.json
│   └── ...
├── src/                     # 后端项目
│   ├── main/
│   │   ├── java/com/shopx/
│   │   │   ├── controller/  # 控制器层
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── ProductController.java
│   │   │   │   ├── OrderController.java
│   │   │   │   ├── CartController.java
│   │   │   │   ├── CheckoutController.java
│   │   │   │   ├── ARVRController.java
│   │   │   │   ├── AIAssistantController.java
│   │   │   │   ├── CollaborationController.java
│   │   │   │   ├── RecommendationController.java
│   │   │   │   ├── RecycleController.java
│   │   │   │   ├── FileController.java
│   │   │   │   ├── EmailController.java
│   │   │   │   ├── WebSocketController.java
│   │   │   │   └── ...
│   │   │   ├── service/     # 服务层
│   │   │   ├── mapper/      # 数据访问层
│   │   │   ├── entity/      # 实体类
│   │   │   │   ├── User.java
│   │   │   │   ├── Product.java
│   │   │   │   ├── Order.java
│   │   │   │   ├── CartItem.java
│   │   │   │   ├── RecycleOrder.java
│   │   │   │   └── ...
│   │   │   ├── config/      # 配置类
│   │   │   ├── util/        # 工具类
│   │   │   ├── exception/   # 异常处理
│   │   │   ├── cache/       # 缓存相关
│   │   │   ├── validation/  # 验证相关
│   │   │   └── annotation/  # 自定义注解
│   │   └── resources/
│   │       ├── application.yml
│   │       └── db/
│   │           ├── schema.sql
│   │           └── test_data.sql
│   └── test/
├── backend/                 # 后端Docker配置
│   └── Dockerfile
├── frontend/                 # 前端项目
│   ├── Dockerfile
│   └── ...
├── docs/                    # 项目文档
│   ├── ARCHITECTURE_DESIGN.md
│   ├── API_DOCUMENTATION.md
│   ├── DEPLOYMENT_GUIDE.md
│   ├── DEVELOPMENT_GUIDE.md
│   └── ...
├── docker-compose.yml       # Docker Compose配置
├── pom.xml                  # Maven配置
└── README.md
```

## 🚀 快速开始

### 环境要求

- **Java**：JDK 17+
- **Node.js**：16+ (推荐使用18+)
- **MySQL**：8.0+
- **Redis**：6.0+ (推荐7.0+)
- **Maven**：3.6+
- **Docker**：20.0+ (可选，用于快速启动数据库)
- **RabbitMQ**：3.8+ (可选，用于消息队列)

### 本地开发

#### 1. 克隆项目
```bash
git clone https://github.com/your-username/shopx.git
cd shopx
```

#### 2. 后端启动
```bash
# 方式一：使用Docker Compose启动MySQL和Redis
docker-compose up -d mysql redis

# 等待数据库启动后，初始化数据库（Docker会自动执行schema.sql和test_data.sql）
# 或者手动执行：
mysql -u root -p < src/main/resources/db/schema.sql
mysql -u root -p < src/main/resources/db/test_data.sql

# 方式二：如果已安装MySQL和Redis，直接配置application.yml中的连接信息

# 启动后端服务
mvn spring-boot:run

# 或者打包后运行
mvn clean package -DskipTests
java -jar target/shopx-backend-1.0.0.jar
```

**注意**：
- 确保MySQL和Redis服务已启动
- 数据库会自动创建（如果使用Docker Compose）
- 默认数据库用户名：root，密码：root（开发环境）
- 生产环境请修改密码

#### 3. 前端启动
```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 其他命令
npm run build        # 构建生产版本
npm run preview       # 预览生产构建
npm run lint         # 代码检查
npm run lint:fix     # 自动修复代码问题
npm run type-check   # TypeScript类型检查
npm test             # 运行测试
npm run test:coverage # 测试覆盖率
npm run format       # 格式化代码
```

#### 4. 访问应用
- **前端应用**：http://localhost:3000
- **后端API**：http://localhost:8080/api
- **API文档**：http://localhost:8080/api/swagger-ui.html
- **健康检查**：http://localhost:8080/api/actuator/health

### Docker部署

#### 1. 使用Docker Compose一键启动
```bash
# 启动所有服务（MySQL、Redis、后端、前端）
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose down
```

#### 2. 单独构建镜像（可选）
```bash
# 构建后端镜像
docker build -f backend/Dockerfile -t shopx-backend .

# 构建前端镜像
cd frontend
docker build -t shopx-frontend .
```

#### 3. 访问服务
- **前端应用**：http://localhost:3000
- **后端API**：http://localhost:8080/api
- **API文档**：http://localhost:8080/api/swagger-ui.html

## 📚 API文档

### 认证相关 (AuthController)
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/logout` - 用户登出
- `GET /api/auth/user-info` - 获取用户信息
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/refresh` - 刷新Token

### 商品管理 (ProductController)
- `GET /api/products` - 获取商品列表（支持分页、搜索、筛选）
- `GET /api/products/{id}` - 获取商品详情
- `POST /api/products` - 创建商品
- `PUT /api/products/{id}` - 更新商品
- `DELETE /api/products/{id}` - 删除商品
- `GET /api/products/hot` - 获取热门商品
- `GET /api/products/search` - 搜索商品
- `GET /api/products/category/{category}` - 按分类获取商品

### 购物车 (CartController)
- `GET /api/cart` - 获取购物车
- `POST /api/cart/add` - 添加商品到购物车
- `PUT /api/cart/update` - 更新购物车商品
- `DELETE /api/cart/remove/{itemId}` - 移除购物车商品
- `DELETE /api/cart/clear` - 清空购物车

### 订单管理 (OrderController, CheckoutController)
- `GET /api/orders` - 获取订单列表
- `GET /api/orders/{id}` - 获取订单详情
- `POST /api/orders` - 创建订单
- `POST /api/checkout` - 结算
- `PUT /api/orders/{id}/cancel` - 取消订单
- `PUT /api/orders/{id}/status` - 更新订单状态

### AR/VR体验 (ARVRController)
- `GET /api/ar-vr/ar/{productId}` - 获取AR体验URL
- `GET /api/ar-vr/vr/{productId}` - 获取VR体验URL
- `GET /api/ar-vr/model/{productId}` - 获取3D模型信息
- `POST /api/ar-vr/interaction` - 记录交互行为
- `GET /api/ar-vr/stats` - 获取体验统计

### AI助手 (AIAssistantController)
- `POST /api/ai-assistant/chat` - AI对话
- `GET /api/ai-assistant/suggestions` - 获取AI建议
- `GET /api/ai-assistant/history` - 获取聊天历史
- `GET /api/ai-assistant/status` - 获取AI状态
- `POST /api/ai-assistant/preferences` - 设置AI偏好

### 协作购物 (CollaborationController, CollaborativeShoppingController)
- `POST /api/collaboration/session` - 创建协作会话
- `POST /api/collaboration/session/{sessionId}/join` - 加入会话
- `GET /api/collaboration/session/{sessionId}` - 获取会话信息
- `POST /api/collaboration/session/{sessionId}/message` - 发送消息
- `POST /api/collaboration/session/{sessionId}/annotation` - 添加标注
- `POST /api/collaboration/session/{sessionId}/end` - 结束会话
- `GET /api/collaboration/sessions` - 获取用户协作会话列表

### 推荐系统 (RecommendationController, ScenarioRecommendationController)
- `GET /api/recommendation/scenario` - 场景推荐
- `GET /api/recommendation/lifestyle` - 生活方式推荐
- `GET /api/recommendation/predict` - AI预测推荐
- `GET /api/recommendation/algorithms` - 获取推荐算法
- `GET /api/recommendation/history` - 获取推荐历史
- `POST /api/recommendation/feedback` - 反馈推荐结果

### 价值循环 (RecycleController)
- `POST /api/recycle/order` - 创建回收订单
- `GET /api/recycle/orders` - 获取回收订单列表
- `GET /api/recycle/order/{id}` - 获取回收订单详情
- `PUT /api/recycle/order/{id}/status` - 更新回收订单状态
- `GET /api/recycle/activities` - 获取环保活动
- `POST /api/recycle/activity/{id}/join` - 参加环保活动
- `GET /api/recycle/stats` - 获取回收统计

### A/B测试 (ABTestController)
- `GET /api/ab-test` - 获取A/B测试列表
- `POST /api/ab-test` - 创建A/B测试
- `GET /api/ab-test/{id}/results` - 获取测试结果

### 文件管理 (FileController)
- `POST /api/file/upload` - 上传文件
- `GET /api/file/{id}` - 下载文件
- `DELETE /api/file/{id}` - 删除文件

### 邮件服务 (EmailController)
- `POST /api/email/send` - 发送邮件
- `POST /api/email/verify` - 验证邮箱

### WebSocket (WebSocketController)
- `WS /api/ws` - WebSocket连接（用于实时协作、消息推送等）

> 📖 完整的API文档请查看 [API文档](./docs/API_DOCUMENTATION.md)

## 🔧 配置说明

### 环境变量配置

项目支持通过环境变量覆盖配置，主要环境变量：

```bash
# 数据库配置
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/shopx
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=root

# Redis配置
SPRING_REDIS_HOST=localhost
SPRING_REDIS_PORT=6379
SPRING_REDIS_PASSWORD=

# RabbitMQ配置
SPRING_RABBITMQ_HOST=localhost
SPRING_RABBITMQ_PORT=5672
SPRING_RABBITMQ_USERNAME=guest
SPRING_RABBITMQ_PASSWORD=guest

# AI服务配置
OPENAI_API_KEY=your-openai-api-key
CLAUDE_API_KEY=your-claude-api-key
CUSTOM_AI_URL=http://localhost:8000/api/chat

# 邮件配置
SPRING_MAIL_USERNAME=your-email@qq.com
SPRING_MAIL_PASSWORD=your-email-password
```

### 后端配置 (application.yml)

主要配置项：

```yaml
server:
  port: 8080
  servlet:
    context-path: /api  # API路径前缀

spring:
  # 数据库配置
  datasource:
    url: jdbc:mysql://localhost:3306/shopx?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
  
  # Redis配置
  redis:
    host: localhost
    port: 6379
    database: 0
  
  # RabbitMQ配置
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
  
  # 邮件配置
  mail:
    host: smtp.qq.com
    port: 587
    username: your-email@qq.com
    password: your-email-password
  
  # 文件上传配置
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 50MB

# Sa-Token配置
sa-token:
  token-name: satoken
  timeout: 2592000  # 30天
  is-concurrent: true
  is-share: true
  token-style: uuid
  jwt-secret-key: shopx-jwt-secret-key-2024

# MyBatis-Plus配置
mybatis-plus:
  mapper-locations: classpath*:mapper/**/*.xml
  type-aliases-package: com.shopx.entity
  global-config:
    db-config:
      id-type: auto
      logic-delete-value: 1

# SpringDoc OpenAPI配置
springdoc:
  swagger-ui:
    path: /swagger-ui.html

# 监控配置
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
```

> 📖 完整配置说明请查看 [部署指南](./docs/DEPLOYMENT_GUIDE.md)

### 前端配置 (vite.config.ts)
```typescript
export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

## 🧪 测试

### 后端测试
```bash
# 运行所有测试
mvn test

# 运行特定测试
mvn test -Dtest=ProductControllerTest

# 生成测试报告
mvn surefire-report:report
```

### 前端测试
```bash
cd frontend

# 运行所有测试
npm test

# 运行测试并生成覆盖率报告
npm run test:coverage

# 运行UI测试
npm run test:ui
```

## 📊 性能监控

### 应用监控
- **健康检查**：http://localhost:8080/api/actuator/health
- **应用信息**：http://localhost:8080/api/actuator/info
- **指标监控**：http://localhost:8080/api/actuator/metrics
- **Prometheus**：http://localhost:8080/api/actuator/prometheus

### 性能指标
- **API响应时间**：< 200ms
- **数据库查询**：< 100ms
- **缓存命中率**：> 90%
- **系统可用性**：> 99.9%

## 🔒 安全特性

### 认证授权
- **JWT Token**：无状态认证
- **Sa-Token**：权限控制框架
- **RBAC**：基于角色的访问控制
- **密码加密**：BCrypt加密

### 数据安全
- **SQL注入防护**：MyBatis-Plus参数化查询
- **XSS防护**：前端输入验证和转义
- **CSRF防护**：Token验证
- **HTTPS**：生产环境强制HTTPS

## 🚀 部署指南

### 生产环境部署

#### 1. 环境准备
```bash
# 安装Java 17
sudo apt update
sudo apt install openjdk-17-jdk

# 安装MySQL 8.0
sudo apt install mysql-server-8.0

# 安装Redis
sudo apt install redis-server

# 安装Nginx
sudo apt install nginx
```

#### 2. 数据库配置
```sql
-- 创建数据库
CREATE DATABASE shopx CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建用户
CREATE USER 'shopx'@'%' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON shopx.* TO 'shopx'@'%';
FLUSH PRIVILEGES;
```

#### 3. 应用部署
```bash
# 构建后端
mvn clean package -DskipTests

# 构建前端
cd frontend
npm run build

# 部署文件
sudo cp target/shopx-1.0.0.jar /opt/shopx/
sudo cp -r frontend/dist/* /var/www/html/
```

#### 4. Nginx配置
```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    # 前端静态文件
    location / {
        root /var/www/html;
        try_files $uri $uri/ /index.html;
    }
    
    # API代理
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

#### 5. 服务启动
```bash
# 创建systemd服务
sudo tee /etc/systemd/system/shopx.service > /dev/null <<EOF
[Unit]
Description=ShopX Application
After=network.target

[Service]
Type=simple
User=shopx
ExecStart=/usr/bin/java -jar /opt/shopx/shopx-1.0.0.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

# 启动服务
sudo systemctl daemon-reload
sudo systemctl enable shopx
sudo systemctl start shopx
```

## 🤝 贡献指南

### 开发流程
1. Fork项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建Pull Request

### 代码规范
- **后端**：遵循Google Java Style Guide
- **前端**：遵循Airbnb JavaScript Style Guide
- **提交信息**：使用Conventional Commits规范
- **测试覆盖率**：> 80%

### 问题报告
- 使用GitHub Issues报告bug
- 提供详细的复现步骤
- 包含环境信息和错误日志

## 📚 更多文档

- [项目详细介绍](./docs/PROJECT_INTRODUCTION.md) - 项目全面介绍、功能详解、技术亮点
- [架构设计文档](./docs/ARCHITECTURE_DESIGN.md) - 系统架构、模块设计、数据流等
- [API完整文档](./docs/API_DOCUMENTATION.md) - 详细的API接口说明
- [部署指南](./docs/DEPLOYMENT_GUIDE.md) - 生产环境部署步骤
- [开发指南](./docs/DEVELOPMENT_GUIDE.md) - 开发环境搭建、代码规范
- [项目改进记录](./docs/IMPROVEMENTS.md) - 项目完善和优化记录

## 🔧 核心功能模块

### 用户系统
- 用户注册/登录/登出
- JWT Token认证
- 用户信息管理
- 权限控制（RBAC）

### 商品系统
- 商品CRUD操作
- 商品分类管理
- 商品搜索（支持关键词、分类筛选）
- 商品图片管理
- 商品评价系统
- 价格历史记录
- 商品审核流程

### 购物车系统
- 购物车增删改查
- 购物车商品数量管理
- 购物车持久化

### 订单系统
- 订单创建和管理
- 订单状态流转
- 订单详情查看
- 订单取消
- 物流跟踪
- 支付方式管理
- 收货地址管理

### AR/VR系统
- 3D模型加载和渲染
- AR/VR体验URL生成
- 用户交互行为记录
- 体验统计数据

### AI助手系统
- 智能对话（支持OpenAI、Claude等）
- 聊天历史管理
- AI偏好设置
- 降级策略（本地规则引擎）

### 协作购物系统
- WebSocket实时通信
- 协作会话管理
- 商品共享和讨论
- 标注功能
- 多人实时协作

### 推荐系统
- 多算法融合推荐
- 场景化推荐
- 生活方式推荐
- AI预测推荐
- A/B测试支持
- 推荐反馈机制

### 价值循环系统
- 回收订单管理
- 环保活动参与
- 环保统计和等级系统
- 社区互动

### 其他功能
- 文件上传下载
- 邮件发送和验证
- 定时任务（Quartz）
- 消息队列（RabbitMQ）
- 分布式锁（Redisson）
- 缓存管理（Redis）

## 📄 许可证

本项目采用MIT许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 📞 联系我们

- **项目主页**：https://github.com/your-username/shopx
- **问题反馈**：https://github.com/your-username/shopx/issues
- **邮箱**：contact@shopx.com

## 🙏 致谢

感谢所有为ShopX项目做出贡献的开发者和开源社区！

---

**ShopX - 创新电商，未来购物体验！** 🚀