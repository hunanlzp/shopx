# ShopX 全景文档（2025-12）

面向研发、测试、运维与产品的综合说明，覆盖项目定位、架构、功能、接口、环境、部署、运维与安全等信息。阅读完本篇即可完成本地启动、理解核心设计并知晓上线与运维要点。

## 1. 项目概览
- **定位**：创新电商平台，融合 AR/VR 商品体验、AI 购物助手、协作购物、智能推荐与价值循环（回收/环保）。
- **架构形态**：前后端分离，后端 Spring Boot 3.2 单体按领域分包，预留微服务拆分边界；前端 React 18 + Vite。
- **主要特性**：
  - AR/VR：3D 展示、交互控制、模型/体验 URL、交互数据上报。
  - AI 助手：对话、推荐、快捷操作、个性化偏好、状态查询。
  - 协作购物：会话创建/加入、实时消息/标注（WebSocket）、多媒体入口预留。
  - 推荐系统：场景/生活方式/预测推荐，算法列表与反馈。
  - 价值循环：回收订单、环保活动、环保积分与统计。

## 2. 技术栈与基础设施
- **前端**：React 18 + TypeScript、Vite、Ant Design 5、Zustand/Immer、React Router 6、Axios、Three.js + R3F、Framer Motion、Vitest/RTL。
- **后端**：Spring Boot 3.2、MyBatis-Plus、MySQL 8、Redis + Redisson、Sa-Token（JWT/RBAC）、Spring Validation/Transaction/Async、Swagger/OpenAPI。
- **中间件/扩展**：RabbitMQ（事件）、MinIO（对象存储）、Elasticsearch（搜索，预留）、Prometheus + Grafana（监控）、ELK（日 志）、Nginx（反代/静态）、Docker(+Compose)；Kubernetes 与分库分表为规划项。
- **CI/CD**：GitHub Actions（前后端测试/构建、Docker 镜像、Trivy 安全扫描，见 `.github/workflows/ci.yml`）。

## 3. 目录速览
```
shopx/
├── frontend/                # React 前端
├── src/main/java/com/shopx/ # 后端业务代码
│   ├── controller/ service/ mapper/ entity/ config/ exception/ util/ validation/ cache/ ...
│   └── ShopXApplication.java
├── src/main/resources/      # application.yml, db/schema.sql, db/test_data.sql
├── docs/                    # 文档（API/架构/部署/开发/改进摘要等）
├── backend/Dockerfile       # 后端镜像
├── docker-compose.yml       # MySQL/Redis 等本地编排
└── pom.xml                  # Maven 配置
```
> 详细文档索引见 `docs/README.md`，架构详解见 `docs/ARCHITECTURE_DESIGN.md`，API 细节见 `docs/API_DOCUMENTATION.md`。

## 4. 核心模块与职责
- **用户/认证**：登录/登出、用户信息、角色/权限（Sa-Token + JWT），密码加密（BCrypt），数据校验（Spring Validation）。
- **商品**：CRUD、分类、搜索/热门、统计字段（浏览/喜欢/分享）、3D 预览与 AR/VR URL。
- **AR/VR**：体验 URL 下发、3D 模型信息、交互行为记录、体验统计。
- **AI 助手**：对话、建议、状态查询、偏好设置、历史管理，接口对接外部大模型留口。
- **协作购物**：会话生命周期、加入/结束、消息与标注；后端 WebSocket 处理器广播会话内消息。
- **推荐系统**：协同过滤/内容/混合推荐融合，场景/生活方式/预测接口，算法列表与反馈、推荐历史与统计。
- **价值循环**：回收订单、状态流转、环保活动、积分/等级/碳减排等统计。
- **通用能力**：缓存（Redis + @Cacheable/@CacheEvict）、统一响应/异常、日志、健康检查与指标（Actuator/Prometheus）。

## 5. 关键数据模型（示例）
- `t_user`：username/email/password/role/avatar/enabled，统计字段（followerCount/followingCount/sustainabilityScore/recycleCount），时间戳。
- `t_product`：name/description/price/category/image/stock/status，统计（view/like/share），AR/VR 字段（has3dPreview/arModelUrl/vrExperienceUrl），时间戳。
- `t_recycle_order`：userId/productName/quantity/status(枚举)/estimatedValue/actualValue/pickupDate/notes，时间戳。
- `t_eco_activity`：title/description/type/status/start/end/participants/max/points/difficulty/category，时间戳。

## 6. 主要接口分组（更多示例见 `docs/API_DOCUMENTATION.md`）
- 认证：`POST /api/auth/login`，`POST /api/auth/logout`，`GET /api/auth/user-info`
- 商品：`GET/POST/PUT/DELETE /api/products`，`/hot`，`/search`，`/category/{category}`
- AR/VR：`GET /api/ar-vr/ar|vr|model/{productId}`，`POST /api/ar-vr/interaction`
- AI 助手：`POST /api/ai-assistant/chat`，`GET /suggestions|history|status`，`DELETE /history`，`POST /preferences`
- 协作：`POST /api/collaboration/session`，`/join`，`/message`，`/annotation`，`GET /session/{id}`，`GET /sessions`
- 推荐：`GET /api/recommendation/scenario|lifestyle|predict|algorithms|history|stats`，`POST /feedback`
- 价值循环：`POST /api/recycle/order`，`GET /orders|order/{id}`，`PUT /order/{id}/status`，`GET /activities|stats`，`POST /activity/{id}/join`
- 响应格式：`{ code, message, data, timestamp }`；分页：`data = { list, total, page, size }`；错误码：200/400/401/403/404/500。

## 7. 安全与权限
- **认证**：JWT（自定义密钥，30 天有效），请求头 `Authorization: Bearer <token>`。
- **授权**：Sa-Token RBAC，典型权限如 `product:view/create/update/delete`。
- **数据校验**：Bean Validation（长度/正则/邮箱/密码强度）。
- **密码**：BCrypt 存储与比对。
- **输入/防护**：参数化 SQL（MyBatis-Plus）、XSS 转义、CSRF Token、生产强制 HTTPS。

## 8. 性能与可扩展性
- **缓存**：Redis + 注解缓存 + 手动缓存管理，典型 key：`user:{id}`、`product:{id}`，TTL 1h/30min。
- **DB 优化**：索引（用户/商品/订单复合索引），热点查询示例在 `ProductMapper`。
- **前端优化**：路由/组件懒加载、虚拟滚动（react-window）、图片懒加载/骨架屏。
- **扩展规划**：服务按领域拆分（用户/商品/订单/推荐/协作/ARVR/AI/价值循环），异步事件（RabbitMQ），读写分离/分库分表（示例配置在架构文档），Redis Cluster、CDN。

## 9. 监控与日志
- **Actuator**：`/actuator/health|info|metrics|prometheus`。
- **指标**：请求耗时分布、接口维度标签（method/uri）。
- **日志**：结构化日志，控制台/文件 pattern 与大小/保留配置；关键操作日志（user action/product view/error）。
- **可视化**：Prometheus + Grafana，ELK 结构化日志，Nginx/应用双层日志。

## 10. 开发环境与本地运行
1) 环境：JDK 17+、Node 16+、MySQL 8、Redis 6、Maven 3.6+；可选 Docker 20+。  
2) 启动依赖：`docker-compose up -d mysql redis`（或自建 MySQL/Redis）。  
3) 初始化数据：`mysql -u root -p < src/main/resources/db/schema.sql && test_data.sql`。  
4) 后端：`mvn spring-boot:run`（端口 8080，Swagger `http://localhost:8080/swagger-ui.html`）。  
5) 前端：`cd frontend && npm install && npm run dev`（端口 3000，代理 `/api -> 8080`，配置见 `vite.config.ts`）。  
6) 访问：前端 `http://localhost:3000`，API `http://localhost:8080`。

## 11. 配置要点
- 后端 `application.yml`：数据源、Redis、Sa-Token（token-name/timeout/token-style/jwt-secret-key）、日志级别。
- 前端 `vite.config.ts`：开发代理指向后端。
- 环境变量样例：`.env.example`（数据库/Redis/JWT/对象存储/消息队列/监控等）。

## 12. 测试与质量
- **后端**：`mvn test` 或 `mvn test -Dtest=ProductControllerTest`；Surefire 报告 `mvn surefire-report:report`。
- **前端**：`npm test`、`npm run test:coverage`、`npm run test:ui`；示例测试在 `frontend/src/components/__tests__/`、`frontend/src/utils/__tests__/`。
- **覆盖率要求**：>80%（参考规范），CI 自动执行测试与格式校验。

## 13. 部署指南（摘要）
- **Docker 本地/集成**：`docker build -t shopx-backend .`，`cd frontend && docker build -t shopx-frontend .`，`docker-compose up -d`。
- **生产**：
  - 安装 Java/MySQL/Redis/Nginx（示例脚本见根 README 部署章节）。
  - 构建：`mvn clean package -DskipTests`；前端 `npm run build`，静态文件发布到 `/var/www/html/`。
  - Nginx 反代 `/api` 到 8080，静态资源回源到前端 dist。
  - Systemd 服务样例 `shopx.service`（见 README）。
- **K8s（规划）**：可将当前 Docker 镜像接入 Helm/Ingress/Gateway，配合 Prometheus Operator。

## 14. 国际化与前端体验
- 国际化：`frontend/src/i18n` 提供中/英文，自动检测浏览器语言，`LanguageSwitcher` 组件切换，hook `useI18n()`。
- 3D/交互：AR/VR 组件基于 Three.js + R3F，支持旋转/缩放/平移、自动旋转开关、交互计数与事件回调。

## 15. CI/CD 流程（GitHub Actions）
- 触发：push/PR。步骤：依赖缓存 -> 后端测试 + 构建（含 MySQL/Redis 服务）-> 前端 lint/typecheck/test/build -> Docker build -> Trivy 安全扫描 -> 测试报告上传。
- 产物：可选镜像 `shopx-backend`、`shopx-frontend`，测试/覆盖率报告。

## 16. 常见排查
- **启动连不上 DB/Redis**：检查 `application.yml`/环境变量与 docker-compose 状态；确认端口与白名单。
- **登录失败/401**：确认 Authorization 头 `Bearer <token>`，JWT 密钥一致，Sa-Token 配置未变更。
- **前端跨域**：确保 Vite 代理 `/api` 指向 `http://localhost:8080`，生产由 Nginx 反代。
- **AR/VR 模型加载失败**：校验 `arModelUrl/vrExperienceUrl` 可访问，前端资源域名在浏览器可打开，网络/跨域已放行。
- **缓存未命中/数据不一致**：检查 @CacheEvict 是否在写路径调用；必要时清理相关 key。

## 17. 后续规划（参考架构文档）
- 微服务拆分与服务网格（K8s + Istio），多活/灰度。
- 分库分表与读写分离、Redis Cluster。
- 更丰富的 AR/VR 资产与边缘加速，AI 能力扩展与模型升级。
- 国际化/多时区/多货币，移动端（React Native），IoT 与供应链溯源扩展。

---

如需更深入的设计与接口细节，请对照：
- 架构设计：`docs/ARCHITECTURE_DESIGN.md`
- 接口文档：`docs/API_DOCUMENTATION.md`
- 部署指南：`docs/DEPLOYMENT_GUIDE.md`
- 开发指南：`docs/DEVELOPMENT_GUIDE.md`
- 改进摘要：`docs/IMPROVEMENTS.md`

本文件旨在作为快速全景索引与落地手册，后续若有更新请同步上述专题文档。 

