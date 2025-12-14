 # ShopX 开发指南

## 📋 概述

本指南将帮助开发者快速上手ShopX项目的开发，包括环境搭建、代码规范、开发流程、测试指南等。

## 🛠️ 开发环境搭建

### 1. 环境要求

#### 必需软件
- **Java**: JDK 17+
- **Node.js**: 16+
- **MySQL**: 8.0+
- **Redis**: 6.0+
- **Maven**: 3.6+
- **Git**: 2.0+

#### 推荐IDE
- **后端**: IntelliJ IDEA / Eclipse
- **前端**: Visual Studio Code / WebStorm
- **数据库**: DBeaver / MySQL Workbench

### 2. 项目初始化

#### 克隆项目
```bash
git clone https://github.com/your-username/shopx.git
cd shopx
```

#### 后端环境配置
```bash
# 安装依赖
mvn clean install

# 配置数据库
mysql -u root -p
CREATE DATABASE shopx CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'shopx'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON shopx.* TO 'shopx'@'localhost';
FLUSH PRIVILEGES;

# 导入数据
mysql -u shopx -p shopx < src/main/resources/db/schema.sql
mysql -u shopx -p shopx < src/main/resources/db/test_data.sql
```

#### 前端环境配置
```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

### 3. IDE配置

#### IntelliJ IDEA配置
1. **导入项目**: File -> Open -> 选择项目根目录
2. **Maven配置**: File -> Settings -> Build -> Build Tools -> Maven
3. **代码格式化**: File -> Settings -> Editor -> Code Style -> Java
4. **插件安装**:
   - Lombok Plugin
   - MyBatis Plugin
   - Spring Boot Plugin

#### VS Code配置
1. **安装扩展**:
   - ES7+ React/Redux/React-Native snippets
   - TypeScript Importer
   - Prettier - Code formatter
   - ESLint
   - Auto Rename Tag

2. **配置文件** (.vscode/settings.json):
```json
{
  "editor.formatOnSave": true,
  "editor.codeActionsOnSave": {
    "source.fixAll.eslint": true
  },
  "typescript.preferences.importModuleSpecifier": "relative"
}
```

## 📝 代码规范

### 1. 后端代码规范

#### Java代码规范
```java
/**
 * 商品服务实现类
 * 
 * @author ShopX Team
 * @version 1.0
 * @since 2024-01-01
 */
@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class ProductServiceImpl implements ProductService {
    
    private final ProductMapper productMapper;
    private final CacheManager cacheManager;
    
    public ProductServiceImpl(ProductMapper productMapper, CacheManager cacheManager) {
        this.productMapper = productMapper;
        this.cacheManager = cacheManager;
    }
    
    @Override
    @Cacheable(value = "products", key = "#id")
    public Product getProductById(Long id) {
        ValidationUtils.validId(id, "商品ID不能为空");
        
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        
        return product;
    }
}
```

#### 命名规范
- **类名**: 使用PascalCase，如`ProductController`
- **方法名**: 使用camelCase，如`getProductById`
- **常量**: 使用UPPER_SNAKE_CASE，如`MAX_RETRY_COUNT`
- **包名**: 使用小写，如`com.shopx.service`

#### 注释规范
```java
/**
 * 根据ID获取商品信息
 * 
 * @param id 商品ID
 * @return 商品信息
 * @throws BusinessException 当商品不存在时抛出
 */
public Product getProductById(Long id) {
    // 实现逻辑
}
```

### 2. 前端代码规范

#### TypeScript代码规范
```typescript
/**
 * 商品接口定义
 */
interface Product {
  id: number
  name: string
  price: number
  description: string
  category: string
  image?: string
  stock: number
  status: 'ACTIVE' | 'INACTIVE' | 'DISCONTINUED'
  viewCount: number
  likeCount: number
  shareCount: number
  has3dPreview: boolean
  arModelUrl?: string
  vrExperienceUrl?: string
  createTime: string
  updateTime: string
}

/**
 * 商品卡片组件
 * 
 * @param product 商品信息
 * @param onAddToCart 添加到购物车回调
 * @param onLike 喜欢商品回调
 * @param onShare 分享商品回调
 */
interface ProductCardProps {
  product: Product
  onAddToCart?: (product: Product) => void
  onLike?: (product: Product) => void
  onShare?: (product: Product) => void
}

const ProductCard: React.FC<ProductCardProps> = ({
  product,
  onAddToCart,
  onLike,
  onShare
}) => {
  const [loading, setLoading] = useState(false)
  
  const handleAddToCart = useCallback(async () => {
    setLoading(true)
    try {
      await onAddToCart?.(product)
    } finally {
      setLoading(false)
    }
  }, [product, onAddToCart])
  
  return (
    <Card
      hoverable
      cover={<img alt={product.name} src={product.image} />}
      actions={[
        <Button key="cart" onClick={handleAddToCart} loading={loading}>
          加入购物车
        </Button>,
        <Button key="like" onClick={() => onLike?.(product)}>
          喜欢
        </Button>,
        <Button key="share" onClick={() => onShare?.(product)}>
          分享
        </Button>
      ]}
    >
      <Card.Meta
        title={product.name}
        description={product.description}
      />
    </Card>
  )
}
```

#### 组件规范
- **组件名**: 使用PascalCase，如`ProductCard`
- **文件名**: 使用PascalCase，如`ProductCard.tsx`
- **Hook名**: 使用camelCase，如`useProduct`
- **常量**: 使用UPPER_SNAKE_CASE，如`MAX_PRODUCTS_PER_PAGE`

#### 样式规范
```typescript
// 使用CSS-in-JS
const StyledCard = styled(Card)`
  .ant-card-cover {
    height: 200px;
    overflow: hidden;
  }
  
  .ant-card-meta-title {
    font-size: 16px;
    font-weight: 600;
  }
  
  .ant-card-meta-description {
    color: #666;
    font-size: 14px;
  }
`

// 使用CSS Modules
import styles from './ProductCard.module.css'

const ProductCard = () => (
  <div className={styles.card}>
    <img className={styles.image} src={product.image} alt={product.name} />
    <h3 className={styles.title}>{product.name}</h3>
    <p className={styles.description}>{product.description}</p>
  </div>
)
```

## 🔄 开发流程

### 1. Git工作流

#### 分支策略
- **main**: 主分支，用于生产环境
- **develop**: 开发分支，用于集成开发
- **feature/**: 功能分支，如`feature/user-auth`
- **hotfix/**: 热修复分支，如`hotfix/fix-login-bug`
- **release/**: 发布分支，如`release/v1.0.0`

#### 提交规范
使用Conventional Commits规范：
```
<type>[optional scope]: <description>

[optional body]

[optional footer(s)]
```

类型说明：
- `feat`: 新功能
- `fix`: 修复bug
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 代码重构
- `test`: 测试相关
- `chore`: 构建过程或辅助工具的变动

示例：
```bash
git commit -m "feat(auth): add JWT token authentication"
git commit -m "fix(product): resolve product image upload issue"
git commit -m "docs(api): update API documentation"
```

### 2. 开发步骤

#### 1. 创建功能分支
```bash
git checkout develop
git pull origin develop
git checkout -b feature/new-feature
```

#### 2. 开发功能
```bash
# 编写代码
# 运行测试
mvn test
npm test

# 提交代码
git add .
git commit -m "feat: implement new feature"
```

#### 3. 创建Pull Request
```bash
git push origin feature/new-feature
# 在GitHub上创建PR
```

#### 4. 代码审查
- 检查代码质量
- 运行测试
- 检查代码规范
- 确认功能完整性

#### 5. 合并代码
```bash
git checkout develop
git pull origin develop
git merge feature/new-feature
git push origin develop
```

### 3. 发布流程

#### 1. 创建发布分支
```bash
git checkout develop
git checkout -b release/v1.0.0
```

#### 2. 版本更新
```bash
# 更新版本号
mvn versions:set -DnewVersion=1.0.0
npm version 1.0.0

# 更新CHANGELOG
# 更新文档
```

#### 3. 测试和修复
```bash
# 运行完整测试
mvn test
npm test

# 修复发现的问题
git commit -m "fix: resolve release issues"
```

#### 4. 发布
```bash
# 合并到main分支
git checkout main
git merge release/v1.0.0
git tag v1.0.0
git push origin main --tags

# 合并回develop分支
git checkout develop
git merge release/v1.0.0
git push origin develop
```

## 🧪 测试指南

### 1. 后端测试

#### 单元测试
```java
@SpringBootTest
@Transactional
@Rollback
class ProductServiceTest {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Test
    void testGetProductById() {
        // Given
        Product product = new Product();
        product.setName("测试商品");
        product.setPrice(99.99);
        productMapper.insert(product);
        
        // When
        Product result = productService.getProductById(product.getId());
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("测试商品");
        assertThat(result.getPrice()).isEqualTo(99.99);
    }
    
    @Test
    void testGetProductByIdNotFound() {
        // When & Then
        assertThatThrownBy(() -> productService.getProductById(999L))
            .isInstanceOf(BusinessException.class)
            .hasMessage("商品不存在");
    }
}
```

#### 集成测试
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductControllerIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Test
    void testGetProducts() {
        // Given
        Product product = new Product();
        product.setName("测试商品");
        product.setPrice(99.99);
        productMapper.insert(product);
        
        // When
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity(
            "/api/products", ApiResponse.class);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCode()).isEqualTo(200);
    }
}
```

### 2. 前端测试

#### 组件测试
```typescript
import { render, screen, fireEvent } from '@testing-library/react'
import { ProductCard } from '../ProductCard'

const mockProduct = {
  id: 1,
  name: '测试商品',
  price: 99.99,
  description: '测试描述',
  category: '测试分类',
  image: 'https://example.com/image.jpg',
  stock: 100,
  status: 'ACTIVE' as const,
  viewCount: 0,
  likeCount: 0,
  shareCount: 0,
  has3dPreview: false,
  createTime: '2024-01-01T00:00:00Z',
  updateTime: '2024-01-01T00:00:00Z'
}

describe('ProductCard', () => {
  it('renders product information correctly', () => {
    render(<ProductCard product={mockProduct} />)
    
    expect(screen.getByText('测试商品')).toBeInTheDocument()
    expect(screen.getByText('测试描述')).toBeInTheDocument()
    expect(screen.getByText('¥99.99')).toBeInTheDocument()
  })
  
  it('calls onAddToCart when add to cart button is clicked', () => {
    const mockOnAddToCart = jest.fn()
    render(<ProductCard product={mockProduct} onAddToCart={mockOnAddToCart} />)
    
    fireEvent.click(screen.getByText('加入购物车'))
    
    expect(mockOnAddToCart).toHaveBeenCalledWith(mockProduct)
  })
})
```

#### Hook测试
```typescript
import { renderHook, act } from '@testing-library/react'
import { useProducts } from '../useProducts'

describe('useProducts', () => {
  it('should fetch products successfully', async () => {
    const { result } = renderHook(() => useProducts())
    
    await act(async () => {
      await result.current.fetchProducts()
    })
    
    expect(result.current.products).toHaveLength(10)
    expect(result.current.loading).toBe(false)
    expect(result.current.error).toBeNull()
  })
})
```

### 3. E2E测试

#### Playwright测试
```typescript
import { test, expect } from '@playwright/test'

test('user can browse products', async ({ page }) => {
  await page.goto('/')
  
  // 等待商品列表加载
  await page.waitForSelector('[data-testid="product-list"]')
  
  // 检查商品卡片
  const productCards = await page.locator('[data-testid="product-card"]')
  await expect(productCards).toHaveCount(10)
  
  // 点击第一个商品
  await productCards.first().click()
  
  // 检查商品详情页
  await expect(page).toHaveURL(/\/products\/\d+/)
  await expect(page.locator('[data-testid="product-name"]')).toBeVisible()
})
```

## 🔧 调试技巧

### 1. 后端调试

#### 日志配置
```yaml
logging:
  level:
    com.shopx: DEBUG
    org.springframework: INFO
    org.mybatis: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

#### 断点调试
1. 在IDE中设置断点
2. 以Debug模式启动应用
3. 触发断点条件
4. 查看变量值和调用栈

#### 性能分析
```java
@Component
@Slf4j
public class PerformanceMonitor {
    
    @EventListener
    public void handleRequest(RequestEvent event) {
        long startTime = System.currentTimeMillis();
        
        // 处理请求
        
        long endTime = System.currentTimeMillis();
        log.info("Request processed in {}ms", endTime - startTime);
    }
}
```

### 2. 前端调试

#### React DevTools
1. 安装React DevTools浏览器扩展
2. 在组件中查看props和state
3. 使用Profiler分析性能

#### Redux DevTools
```typescript
// 配置Redux DevTools
const store = createStore(
  rootReducer,
  window.__REDUX_DEVTOOLS_EXTENSION__ && window.__REDUX_DEVTOOLS_EXTENSION__()
)
```

#### 网络调试
```typescript
// 使用浏览器开发者工具
// Network标签页查看API请求
// Console标签页查看错误信息
// Sources标签页设置断点
```

## 📚 学习资源

### 1. 技术文档
- **Spring Boot**: https://spring.io/projects/spring-boot
- **React**: https://reactjs.org/docs
- **TypeScript**: https://www.typescriptlang.org/docs
- **Ant Design**: https://ant.design/docs/react/introduce-cn

### 2. 最佳实践
- **Java**: https://google.github.io/styleguide/javaguide.html
- **TypeScript**: https://typescript-eslint.io/rules/
- **React**: https://react.dev/learn

### 3. 工具推荐
- **Postman**: API测试工具
- **Insomnia**: API客户端
- **DBeaver**: 数据库管理工具
- **Redis Desktop Manager**: Redis管理工具

## 🤝 贡献指南

### 1. 如何贡献
1. Fork项目
2. 创建功能分支
3. 提交代码
4. 创建Pull Request

### 2. 代码审查标准
- 代码质量
- 测试覆盖率
- 文档完整性
- 性能影响

### 3. 问题报告
- 使用GitHub Issues
- 提供详细复现步骤
- 包含环境信息

## 📞 技术支持

如有开发问题，请联系：
- **邮箱**: dev-support@shopx.com
- **Slack**: #shopx-dev
- **GitHub**: https://github.com/shopx/issues
