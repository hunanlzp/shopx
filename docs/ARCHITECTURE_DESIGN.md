# ShopX 架构设计文档

## 📋 概述

ShopX是一个现代化的创新电商平台，采用微服务架构设计，集成了AR/VR体验、AI购物助手、协作购物、智能推荐、价值循环等前沿功能。本文档详细描述了系统的整体架构设计。

## 🏗️ 系统架构

### 1. 整体架构图

```
┌─────────────────────────────────────────────────────────────────┐
│                        ShopX 系统架构                            │
├─────────────────────────────────────────────────────────────────┤
│  前端层 (Frontend Layer)                                        │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ │
│  │   Web App   │ │  Mobile App │ │  Admin App  │ │   AR/VR     │ │
│  │   (React)   │ │   (React)   │ │   (React)   │ │   (Three.js)│ │
│  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘ │
├─────────────────────────────────────────────────────────────────┤
│  网关层 (Gateway Layer)                                         │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │                    Nginx / API Gateway                     │ │
│  │             负载均衡 / 路由 / 限流 / 认证                    │ │
│  └─────────────────────────────────────────────────────────────┘ │
├─────────────────────────────────────────────────────────────────┤
│  应用层 (Application Layer)                                     │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ │
│  │  用户服务   │ │  商品服务   │ │  订单服务   │ │  支付服务   │ │
│  │ User Service│ │Product Svc  │ │ Order Svc   │ │Payment Svc  │ │
│  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘ │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ │
│  │  AR/VR服务  │ │  AI助手服务 │ │  协作服务   │ │  推荐服务   │ │
│  │ AR/VR Svc   │ │ AI Assistant│ │Collaboration │ │Recommendation│ │
│  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘ │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ │
│  │  价值循环   │ │  通知服务   │ │  文件服务   │ │  搜索服务   │ │
│  │ Recycle Svc │ │Notification │ │  File Svc    │ │ Search Svc  │ │
│  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘ │
├─────────────────────────────────────────────────────────────────┤
│  数据层 (Data Layer)                                            │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ │
│  │   MySQL     │ │    Redis    │ │  MongoDB    │ │  Elasticsearch│ │
│  │  主数据库   │ │   缓存      │ │  文档存储   │ │  搜索引擎   │ │
│  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘ │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ │
│  │   MinIO     │ │   RabbitMQ  │ │   Kafka     │ │   InfluxDB  │ │
│  │  对象存储   │ │  消息队列   │ │  事件流     │ │  时序数据   │ │
│  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘ │
├─────────────────────────────────────────────────────────────────┤
│  基础设施层 (Infrastructure Layer)                              │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ │
│  │   Docker    │ │ Kubernetes  │ │   Prometheus│ │   Grafana   │ │
│  │  容器化     │ │  编排       │ │  监控       │ │  可视化     │ │
│  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

### 2. 技术栈选择

#### 前端技术栈
- **框架**: React 18 + TypeScript
- **构建工具**: Vite
- **UI组件库**: Ant Design 5
- **状态管理**: Zustand + Immer
- **路由**: React Router 6
- **HTTP客户端**: Axios
- **3D渲染**: Three.js + React Three Fiber
- **动画**: Framer Motion
- **测试**: Vitest + Testing Library

#### 后端技术栈
- **框架**: Spring Boot 3.2.0
- **数据库**: MySQL 8.0
- **缓存**: Redis + Redisson
- **ORM**: MyBatis-Plus
- **认证授权**: Sa-Token
- **API文档**: Swagger/OpenAPI
- **消息队列**: RabbitMQ
- **文件存储**: MinIO
- **搜索引擎**: Elasticsearch

#### 基础设施
- **容器化**: Docker + Docker Compose
- **编排**: Kubernetes
- **监控**: Prometheus + Grafana
- **日志**: ELK Stack
- **CI/CD**: GitHub Actions

## 🔧 核心模块设计

### 1. 用户管理模块

#### 功能特性
- 用户注册/登录
- 角色权限管理
- 用户信息管理
- 社交功能（关注/粉丝）

#### 技术实现
```java
@Entity
@Table(name = "t_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Enumerated(EnumType.STRING)
    private UserRole role;
    
    private String avatar;
    private Boolean enabled;
    
    // 社交相关字段
    private Integer followerCount;
    private Integer followingCount;
    
    // 环保相关字段
    private Integer sustainabilityScore;
    private Integer recycleCount;
    
    @CreationTimestamp
    private LocalDateTime createTime;
    
    @UpdateTimestamp
    private LocalDateTime updateTime;
}
```

#### 权限设计
```java
public enum UserRole {
    ADMIN("管理员", Arrays.asList(
        Permission.USER_MANAGE,
        Permission.PRODUCT_MANAGE,
        Permission.ORDER_MANAGE,
        Permission.SYSTEM_MANAGE
    )),
    SELLER("商家", Arrays.asList(
        Permission.PRODUCT_MANAGE,
        Permission.ORDER_MANAGE
    )),
    USER("用户", Arrays.asList(
        Permission.PRODUCT_VIEW,
        Permission.ORDER_CREATE
    ));
}
```

### 2. 商品管理模块

#### 功能特性
- 商品CRUD操作
- 商品分类管理
- 商品搜索
- 商品推荐
- 3D模型管理

#### 数据模型
```java
@Entity
@Table(name = "t_product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private BigDecimal price;
    
    @Column(nullable = false)
    private String category;
    
    private String image;
    private Integer stock;
    
    @Enumerated(EnumType.STRING)
    private ProductStatus status;
    
    // 统计字段
    private Integer viewCount;
    private Integer likeCount;
    private Integer shareCount;
    
    // AR/VR相关字段
    private Boolean has3dPreview;
    private String arModelUrl;
    private String vrExperienceUrl;
    
    @CreationTimestamp
    private LocalDateTime createTime;
    
    @UpdateTimestamp
    private LocalDateTime updateTime;
}
```

### 3. AR/VR体验模块

#### 功能特性
- 3D模型渲染
- 交互控制
- 环境设置
- 用户行为记录
- 性能优化

#### 前端实现
```typescript
interface ARVRExperienceProps {
  productId: number
  productName: string
  productPrice: number
  experienceType: 'AR' | 'VR'
  onClose: () => void
  onAddToCart?: (productId: number) => void
  onLike?: (productId: number) => void
  onShare?: (productId: number) => void
}

const ARVRExperience: React.FC<ARVRExperienceProps> = ({
  productId,
  productName,
  productPrice,
  experienceType,
  onClose,
  onAddToCart,
  onLike,
  onShare
}) => {
  const [isLoading, setIsLoading] = useState(true)
  const [autoRotate, setAutoRotate] = useState(true)
  const [interactionCount, setInteractionCount] = useState(0)
  
  // 3D场景渲染
  return (
    <div className="arvr-experience">
      <Canvas>
        <ambientLight intensity={0.5} />
        <directionalLight position={[10, 10, 5]} />
        <ProductModel 
          productId={productId}
          autoRotate={autoRotate}
          onInteraction={handleInteraction}
        />
        <OrbitControls />
      </Canvas>
    </div>
  )
}
```

#### 后端API
```java
@RestController
@RequestMapping("/api/ar-vr")
@Api(tags = "AR/VR体验")
public class ARVRController {
    
    @GetMapping("/ar/{productId}")
    public ResponseEntity<ApiResponse<Map<String, String>>> getARExperience(
            @PathVariable Long productId) {
        // 生成AR体验URL
        String arUrl = generateARUrl(productId);
        
        Map<String, String> data = new HashMap<>();
        data.put("arUrl", arUrl);
        data.put("productId", productId.toString());
        
        return ResponseUtil.success(data);
    }
    
    @PostMapping("/interaction")
    public ResponseEntity<ApiResponse<Void>> recordInteraction(
            @RequestParam Long productId,
            @RequestParam String interactionType,
            @RequestBody Map<String, Object> interactionData) {
        // 记录用户交互行为
        interactionService.recordInteraction(productId, interactionType, interactionData);
        return ResponseUtil.success();
    }
}
```

### 4. AI助手模块

#### 功能特性
- 智能对话
- 商品推荐
- 快速操作
- 个性化设置
- 学习能力

#### 对话流程
```typescript
interface AIMessage {
  id: string
  type: 'user' | 'assistant'
  content: string
  timestamp: string
  suggestedProducts?: Product[]
  quickActions?: QuickAction[]
}

const AIAssistant: React.FC = () => {
  const [messages, setMessages] = useState<AIMessage[]>([])
  const [isTyping, setIsTyping] = useState(false)
  
  const sendMessage = async (content: string) => {
    // 添加用户消息
    const userMessage: AIMessage = {
      id: generateId(),
      type: 'user',
      content,
      timestamp: new Date().toISOString()
    }
    setMessages(prev => [...prev, userMessage])
    
    // 发送到AI服务
    setIsTyping(true)
    try {
      const response = await ApiService.chatWithAI(content)
      
      // 添加AI回复
      const aiMessage: AIMessage = {
        id: generateId(),
        type: 'assistant',
        content: response.response,
        suggestedProducts: response.suggestedProducts,
        quickActions: response.quickActions,
        timestamp: new Date().toISOString()
      }
      setMessages(prev => [...prev, aiMessage])
    } finally {
      setIsTyping(false)
    }
  }
  
  return (
    <div className="ai-assistant">
      <MessageList messages={messages} />
      <MessageInput onSend={sendMessage} />
      {isTyping && <TypingIndicator />}
    </div>
  )
}
```

### 5. 协作购物模块

#### 功能特性
- 实时协作
- 商品共享
- 标注功能
- 多媒体支持
- 会话管理

#### WebSocket实现
```java
@Component
@Slf4j
public class CollaborationWebSocketHandler extends TextWebSocketHandler {
    
    private final Map<String, Set<WebSocketSession>> sessions = new ConcurrentHashMap<>();
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String sessionId = getSessionId(session);
        sessions.computeIfAbsent(sessionId, k -> ConcurrentHashMap.newKeySet())
                .add(session);
        log.info("User connected to collaboration session: {}", sessionId);
    }
    
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String sessionId = getSessionId(session);
        CollaborationMessage msg = JSON.parseObject(message.getPayload(), CollaborationMessage.class);
        
        // 广播消息给会话中的所有用户
        broadcastToSession(sessionId, msg, session);
    }
    
    private void broadcastToSession(String sessionId, CollaborationMessage message, WebSocketSession sender) {
        Set<WebSocketSession> sessionUsers = sessions.get(sessionId);
        if (sessionUsers != null) {
            sessionUsers.forEach(session -> {
                if (session.isOpen() && !session.equals(sender)) {
                    try {
                        session.sendMessage(new TextMessage(JSON.toJSONString(message)));
                    } catch (IOException e) {
                        log.error("Failed to send message", e);
                    }
                }
            });
        }
    }
}
```

### 6. 推荐系统模块

#### 功能特性
- 多算法融合
- 场景化推荐
- 实时推荐
- 推荐解释
- A/B测试

#### 推荐算法
```java
@Service
public class RecommendationService {
    
    @Autowired
    private CollaborativeFilteringService collaborativeFiltering;
    
    @Autowired
    private ContentBasedService contentBased;
    
    @Autowired
    private HybridRecommendationService hybridService;
    
    public List<Product> getRecommendations(Long userId, String scenario) {
        // 获取用户偏好
        UserPreferences preferences = getUserPreferences(userId);
        
        // 多算法融合
        List<Product> collaborativeResults = collaborativeFiltering.recommend(userId, 10);
        List<Product> contentResults = contentBased.recommend(preferences, 10);
        List<Product> hybridResults = hybridService.recommend(userId, scenario, 10);
        
        // 结果融合和排序
        return mergeAndRankRecommendations(collaborativeResults, contentResults, hybridResults);
    }
    
    private List<Product> mergeAndRankRecommendations(
            List<Product> collaborative,
            List<Product> content,
            List<Product> hybrid) {
        // 使用加权融合算法
        Map<Long, Double> scores = new HashMap<>();
        
        // 协同过滤权重: 0.4
        collaborative.forEach(product -> 
            scores.merge(product.getId(), 0.4, Double::sum));
        
        // 内容推荐权重: 0.3
        content.forEach(product -> 
            scores.merge(product.getId(), 0.3, Double::sum));
        
        // 混合推荐权重: 0.3
        hybrid.forEach(product -> 
            scores.merge(product.getId(), 0.3, Double::sum));
        
        // 按分数排序
        return scores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .map(entry -> getProductById(entry.getKey()))
                .collect(Collectors.toList());
    }
}
```

### 7. 价值循环模块

#### 功能特性
- 回收管理
- 环保活动
- 等级系统
- 环境影响统计
- 社区互动

#### 数据模型
```java
@Entity
@Table(name = "t_recycle_order")
public class RecycleOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private String productName;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Enumerated(EnumType.STRING)
    private RecycleOrderStatus status;
    
    @Column(nullable = false)
    private BigDecimal estimatedValue;
    
    private BigDecimal actualValue;
    
    private LocalDateTime pickupDate;
    private String notes;
    
    @CreationTimestamp
    private LocalDateTime createTime;
    
    @UpdateTimestamp
    private LocalDateTime updateTime;
}

@Entity
@Table(name = "t_eco_activity")
public class EcoActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    private ActivityType type;
    
    @Enumerated(EnumType.STRING)
    private ActivityStatus status;
    
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    
    private Integer participants;
    private Integer maxParticipants;
    private Integer points;
    
    @Enumerated(EnumType.STRING)
    private DifficultyLevel difficulty;
    
    private String category;
    
    @CreationTimestamp
    private LocalDateTime createTime;
    
    @UpdateTimestamp
    private LocalDateTime updateTime;
}
```

## 🔄 数据流设计

### 1. 用户请求流程

```
用户请求 → Nginx → API Gateway → 微服务 → 数据库
    ↓
响应数据 ← Nginx ← API Gateway ← 微服务 ← 数据库
```

### 2. 实时通信流程

```
用户A → WebSocket → 协作服务 → 消息队列 → 用户B
    ↓
用户A ← WebSocket ← 协作服务 ← 消息队列 ← 用户B
```

### 3. 推荐系统流程

```
用户行为 → 数据收集 → 特征提取 → 算法计算 → 推荐结果
    ↓
推荐结果 → 缓存 → 用户界面 → 用户反馈 → 模型优化
```

## 🛡️ 安全设计

### 1. 认证授权

#### JWT Token设计
```java
@Component
public class JwtTokenProvider {
    
    private final String secretKey = "shopx-jwt-secret-key";
    private final long validityInMilliseconds = 2592000000L; // 30天
    
    public String createToken(String username, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("roles", roles);
        
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);
        
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }
    
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
```

#### 权限控制
```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @GetMapping
    @SaCheckPermission("product:view")
    public ResponseEntity<ApiResponse<PageResult<Product>>> getProducts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        // 实现逻辑
    }
    
    @PostMapping
    @SaCheckPermission("product:create")
    public ResponseEntity<ApiResponse<Product>> createProduct(@RequestBody Product product) {
        // 实现逻辑
    }
    
    @PutMapping("/{id}")
    @SaCheckPermission("product:update")
    public ResponseEntity<ApiResponse<Product>> updateProduct(
            @PathVariable Long id, @RequestBody Product product) {
        // 实现逻辑
    }
    
    @DeleteMapping("/{id}")
    @SaCheckPermission("product:delete")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        // 实现逻辑
    }
}
```

### 2. 数据安全

#### 密码加密
```java
@Component
public class PasswordUtil {
    
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    
    public String encryptPassword(String rawPassword) {
        return encoder.encode(rawPassword);
    }
    
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
    
    public String generateRandomPassword() {
        return RandomStringUtils.randomAlphanumeric(12);
    }
}
```

#### 数据验证
```java
@Entity
@Table(name = "t_user")
public class User {
    
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;
    
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 20, message = "密码长度必须在8-20个字符之间")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$", 
             message = "密码必须包含大小写字母、数字和特殊字符")
    private String password;
}
```

## 📊 性能优化

### 1. 缓存策略

#### Redis缓存设计
```java
@Service
public class CacheManager {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    public void setUserCache(Long userId, User user) {
        String key = "user:" + userId;
        redisTemplate.opsForValue().set(key, user, Duration.ofHours(1));
    }
    
    public User getUserCache(Long userId) {
        String key = "user:" + userId;
        return (User) redisTemplate.opsForValue().get(key);
    }
    
    public void setProductCache(Long productId, Product product) {
        String key = "product:" + productId;
        redisTemplate.opsForValue().set(key, product, Duration.ofMinutes(30));
    }
    
    public Product getProductCache(Long productId) {
        String key = "product:" + productId;
        return (Product) redisTemplate.opsForValue().get(key);
    }
}
```

#### 应用层缓存
```java
@Service
public class ProductService {
    
    @Cacheable(value = "products", key = "#id")
    public Product getProductById(Long id) {
        return productMapper.selectById(id);
    }
    
    @CacheEvict(value = "products", key = "#product.id")
    public Product updateProduct(Product product) {
        productMapper.updateById(product);
        return product;
    }
    
    @CacheEvict(value = "products", allEntries = true)
    public void clearProductCache() {
        // 清空所有商品缓存
    }
}
```

### 2. 数据库优化

#### 索引设计
```sql
-- 用户表索引
CREATE INDEX idx_user_username ON t_user(username);
CREATE INDEX idx_user_email ON t_user(email);
CREATE INDEX idx_user_role ON t_user(role);

-- 商品表索引
CREATE INDEX idx_product_category ON t_product(category);
CREATE INDEX idx_product_status ON t_product(status);
CREATE INDEX idx_product_price ON t_product(price);
CREATE INDEX idx_product_create_time ON t_product(create_time);

-- 订单表索引
CREATE INDEX idx_order_user_id ON t_order(user_id);
CREATE INDEX idx_order_status ON t_order(status);
CREATE INDEX idx_order_create_time ON t_order(create_time);

-- 复合索引
CREATE INDEX idx_product_category_status ON t_product(category, status);
CREATE INDEX idx_order_user_status ON t_order(user_id, status);
```

#### 查询优化
```java
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    
    @Select("SELECT * FROM t_product WHERE category = #{category} AND status = 'ACTIVE' ORDER BY create_time DESC LIMIT #{limit}")
    List<Product> selectHotProducts(@Param("category") String category, @Param("limit") Integer limit);
    
    @Select("SELECT * FROM t_product WHERE name LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%')")
    List<Product> searchProducts(@Param("keyword") String keyword);
    
    @Select("SELECT p.*, COUNT(o.id) as order_count FROM t_product p LEFT JOIN t_order_item oi ON p.id = oi.product_id LEFT JOIN t_order o ON oi.order_id = o.id WHERE o.status = 'COMPLETED' GROUP BY p.id ORDER BY order_count DESC LIMIT #{limit}")
    List<Product> selectPopularProducts(@Param("limit") Integer limit);
}
```

### 3. 前端性能优化

#### 代码分割
```typescript
// 路由级别的代码分割
const ProductList = lazy(() => import('../pages/ProductList'))
const ProductDetail = lazy(() => import('../pages/ProductDetail'))
const ARVRExperience = lazy(() => import('../pages/ARVRExperience'))

// 组件级别的代码分割
const ProductCard = lazy(() => import('../components/ProductCard'))
const AIAssistant = lazy(() => import('../components/AIAssistant'))
```

#### 虚拟滚动
```typescript
import { FixedSizeList as List } from 'react-window'

const ProductList: React.FC = () => {
  const products = useProducts()
  
  const Row = ({ index, style }: { index: number; style: React.CSSProperties }) => (
    <div style={style}>
      <ProductCard product={products[index]} />
    </div>
  )
  
  return (
    <List
      height={600}
      itemCount={products.length}
      itemSize={200}
      width="100%"
    >
      {Row}
    </List>
  )
}
```

#### 图片优化
```typescript
const OptimizedImage: React.FC<{ src: string; alt: string }> = ({ src, alt }) => {
  const [loaded, setLoaded] = useState(false)
  const [error, setError] = useState(false)
  
  return (
    <div className="image-container">
      {!loaded && !error && <Skeleton.Image />}
      <img
        src={src}
        alt={alt}
        loading="lazy"
        onLoad={() => setLoaded(true)}
        onError={() => setError(true)}
        style={{ display: loaded ? 'block' : 'none' }}
      />
      {error && <div className="image-error">图片加载失败</div>}
    </div>
  )
}
```

## 🔍 监控设计

### 1. 应用监控

#### 健康检查
```java
@Component
public class HealthIndicator implements HealthIndicator {
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Override
    public Health health() {
        try {
            // 检查数据库连接
            dataSource.getConnection().close();
            
            // 检查Redis连接
            redisTemplate.opsForValue().get("health_check");
            
            return Health.up()
                    .withDetail("database", "UP")
                    .withDetail("redis", "UP")
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
```

#### 性能指标
```java
@Component
public class PerformanceMetrics {
    
    private final MeterRegistry meterRegistry;
    
    public PerformanceMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }
    
    @EventListener
    public void handleRequest(RequestEvent event) {
        Timer.Sample sample = Timer.start(meterRegistry);
        
        // 处理请求
        
        sample.stop(Timer.builder("http.request.duration")
                .tag("method", event.getMethod())
                .tag("uri", event.getUri())
                .register(meterRegistry));
    }
}
```

### 2. 日志设计

#### 日志配置
```yaml
logging:
  level:
    com.shopx: INFO
    org.springframework: WARN
    org.mybatis: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: /var/log/shopx/application.log
    max-size: 100MB
    max-history: 30
```

#### 结构化日志
```java
@Component
@Slf4j
public class StructuredLogger {
    
    public void logUserAction(Long userId, String action, Map<String, Object> context) {
        log.info("User action: userId={}, action={}, context={}", 
                userId, action, JSON.toJSONString(context));
    }
    
    public void logProductView(Long productId, Long userId, String source) {
        log.info("Product view: productId={}, userId={}, source={}", 
                productId, userId, source);
    }
    
    public void logError(String operation, Exception e, Map<String, Object> context) {
        log.error("Operation failed: operation={}, error={}, context={}", 
                operation, e.getMessage(), JSON.toJSONString(context), e);
    }
}
```

## 🚀 扩展性设计

### 1. 微服务拆分

#### 服务边界
- **用户服务**: 用户管理、认证授权
- **商品服务**: 商品管理、分类管理
- **订单服务**: 订单管理、支付处理
- **推荐服务**: 推荐算法、个性化
- **协作服务**: 实时协作、消息推送
- **AR/VR服务**: 3D模型、交互体验
- **AI服务**: 智能对话、自然语言处理
- **价值循环服务**: 回收管理、环保活动

#### 服务通信
```java
// 同步通信 - REST API
@FeignClient(name = "user-service")
public interface UserServiceClient {
    
    @GetMapping("/api/users/{id}")
    User getUserById(@PathVariable Long id);
    
    @PostMapping("/api/users")
    User createUser(@RequestBody User user);
}

// 异步通信 - 消息队列
@Component
public class OrderEventHandler {
    
    @RabbitListener(queues = "order.created")
    public void handleOrderCreated(OrderCreatedEvent event) {
        // 处理订单创建事件
        notificationService.sendOrderConfirmation(event.getUserId(), event.getOrderId());
        inventoryService.updateStock(event.getOrderItems());
    }
}
```

### 2. 数据分片

#### 水平分片
```java
@Configuration
public class ShardingConfig {
    
    @Bean
    public DataSource dataSource() {
        // 配置分片规则
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        
        // 用户表分片
        TableRuleConfiguration userTableRule = new TableRuleConfiguration("t_user", "ds${user_id % 2}.t_user_${user_id % 4}");
        userTableRule.setDatabaseShardingStrategyConfig(new InlineShardingStrategyConfiguration("user_id", "ds${user_id % 2}"));
        userTableRule.setTableShardingStrategyConfig(new InlineShardingStrategyConfiguration("user_id", "t_user_${user_id % 4}"));
        
        shardingRuleConfig.getTableRuleConfigs().add(userTableRule);
        
        return ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfig, new Properties());
    }
}
```

#### 读写分离
```java
@Configuration
public class ReadWriteSplitConfig {
    
    @Bean
    @Primary
    public DataSource masterDataSource() {
        // 主库配置
        return DataSourceBuilder.create()
                .url("jdbc:mysql://master:3306/shopx")
                .username("shopx")
                .password("password")
                .build();
    }
    
    @Bean
    public DataSource slaveDataSource() {
        // 从库配置
        return DataSourceBuilder.create()
                .url("jdbc:mysql://slave:3306/shopx")
                .username("shopx")
                .password("password")
                .build();
    }
}
```

## 📈 未来规划

### 1. 技术演进
- **云原生**: Kubernetes + Istio
- **边缘计算**: CDN + Edge Computing
- **AI/ML**: 机器学习平台集成
- **区块链**: 供应链溯源

### 2. 功能扩展
- **国际化**: 多语言支持
- **移动端**: React Native应用
- **IoT集成**: 智能设备连接
- **AR/VR增强**: 更丰富的3D体验

### 3. 性能优化
- **分布式缓存**: Redis Cluster
- **数据库优化**: 读写分离 + 分库分表
- **CDN加速**: 静态资源分发
- **负载均衡**: 多级负载均衡

---

**ShopX架构设计 - 构建现代化电商平台的技术基石！** 🏗️
