# ShopX API 文档

## 📋 概述

ShopX API提供完整的电商平台功能，包括用户管理、商品管理、AR/VR体验、AI助手、协作购物、智能推荐、价值循环等模块。

## 🔐 认证

### 获取Token
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "user@example.com",
  "password": "password123"
}
```

**响应：**
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "username": "user@example.com",
      "email": "user@example.com",
      "role": "USER",
      "avatar": "https://example.com/avatar.jpg"
    }
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### 使用Token
在请求头中添加Authorization字段：
```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## 👤 用户管理

### 用户登录
```http
POST /api/auth/login
```

**请求参数：**
- `username` (string): 用户名或邮箱
- `password` (string): 密码

### 用户登出
```http
POST /api/auth/logout
```

### 获取用户信息
```http
GET /api/auth/user-info
```

**响应：**
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": 1,
    "username": "user@example.com",
    "email": "user@example.com",
    "role": "USER",
    "avatar": "https://example.com/avatar.jpg",
    "followerCount": 100,
    "followingCount": 50,
    "sustainabilityScore": 850,
    "recycleCount": 25
  }
}
```

## 🛍️ 商品管理

### 获取商品列表
```http
GET /api/products?page=1&size=20&keyword=手机&category=电子产品
```

**查询参数：**
- `page` (int): 页码，默认1
- `size` (int): 每页大小，默认20
- `keyword` (string): 搜索关键词
- `category` (string): 商品分类

**响应：**
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "list": [
      {
        "id": 1,
        "name": "iPhone 15 Pro",
        "description": "最新款iPhone",
        "price": 7999.00,
        "category": "电子产品",
        "image": "https://example.com/iphone.jpg",
        "stock": 100,
        "status": "ACTIVE",
        "viewCount": 1500,
        "likeCount": 200,
        "shareCount": 50,
        "has3dPreview": true,
        "arModelUrl": "https://example.com/ar/iphone",
        "vrExperienceUrl": "https://example.com/vr/iphone"
      }
    ],
    "total": 100,
    "page": 1,
    "size": 20
  }
}
```

### 获取商品详情
```http
GET /api/products/{id}
```

**路径参数：**
- `id` (long): 商品ID

### 创建商品
```http
POST /api/products
```

**请求体：**
```json
{
  "name": "商品名称",
  "description": "商品描述",
  "price": 99.99,
  "category": "分类",
  "image": "https://example.com/image.jpg",
  "stock": 100,
  "has3dPreview": true,
  "arModelUrl": "https://example.com/ar/model",
  "vrExperienceUrl": "https://example.com/vr/experience"
}
```

### 更新商品
```http
PUT /api/products/{id}
```

### 删除商品
```http
DELETE /api/products/{id}
```

### 获取热门商品
```http
GET /api/products/hot?limit=10
```

### 搜索商品
```http
GET /api/products/search?keyword=手机&page=1&size=20
```

### 按分类获取商品
```http
GET /api/products/category/{category}?page=1&size=20
```

## 🎮 AR/VR体验

### 获取AR体验URL
```http
GET /api/ar-vr/ar/{productId}
```

**响应：**
```json
{
  "code": 200,
  "message": "获取AR体验URL成功",
  "data": {
    "arUrl": "/ar-experience/1",
    "productId": "1",
    "productName": "iPhone 15 Pro",
    "modelUrl": "https://example.com/ar/iphone"
  }
}
```

### 获取VR体验URL
```http
GET /api/ar-vr/vr/{productId}
```

### 获取3D模型信息
```http
GET /api/ar-vr/model/{productId}
```

**响应：**
```json
{
  "code": 200,
  "message": "获取3D模型信息成功",
  "data": {
    "productId": 1,
    "productName": "iPhone 15 Pro",
    "arModelUrl": "https://example.com/ar/iphone",
    "vrExperienceUrl": "https://example.com/vr/iphone",
    "has3dPreview": true,
    "modelConfig": {
      "geometry": "box",
      "size": [2.0, 0.3, 1.0],
      "color": "#1890ff",
      "material": "metal"
    }
  }
}
```

### 记录交互行为
```http
POST /api/ar-vr/interaction?productId=1&interactionType=click
```

**请求体：**
```json
{
  "position": {"x": 100, "y": 200},
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### 获取体验统计
```http
GET /api/ar-vr/stats
```

## 🤖 AI助手

### AI对话
```http
POST /api/ai-assistant/chat?message=推荐一些手机&sessionId=session_123
```

**响应：**
```json
{
  "code": 200,
  "message": "AI回复成功",
  "data": {
    "response": "我为您推荐一些优质手机，请查看下方的推荐列表。",
    "suggestedProducts": [
      {
        "id": 1,
        "name": "iPhone 15 Pro",
        "price": 7999.00,
        "image": "https://example.com/iphone.jpg"
      }
    ],
    "sessionId": "session_123",
    "timestamp": "2024-01-01T00:00:00Z",
    "confidence": 0.85
  }
}
```

### 获取AI建议
```http
GET /api/ai-assistant/suggestions?type=general
```

**查询参数：**
- `type` (string): 建议类型 (general, recommendation, comparison)

### 获取聊天历史
```http
GET /api/ai-assistant/history?sessionId=session_123&page=1&size=20
```

### 清空聊天历史
```http
DELETE /api/ai-assistant/history?sessionId=session_123
```

### 获取AI状态
```http
GET /api/ai-assistant/status
```

**响应：**
```json
{
  "code": 200,
  "message": "获取AI状态成功",
  "data": {
    "status": "online",
    "model": "GPT-4",
    "version": "1.0.0",
    "responseTime": "< 2s",
    "uptime": "99.9%",
    "lastUpdate": "2024-01-01T00:00:00Z"
  }
}
```

### 设置AI偏好
```http
POST /api/ai-assistant/preferences
```

**请求体：**
```json
{
  "personality": "friendly",
  "responseSpeed": "normal",
  "detailLevel": "medium",
  "language": "zh"
}
```

## 👥 协作购物

### 创建协作会话
```http
POST /api/collaboration/session?hostUserId=1&productId=1
```

**响应：**
```json
{
  "code": 200,
  "message": "协作会话创建成功",
  "data": {
    "sessionId": "collab_1234567890",
    "session": {
      "id": "collab_1234567890",
      "hostUserId": 1,
      "productId": 1,
      "productName": "iPhone 15 Pro",
      "productPrice": 7999.00,
      "participants": [1],
      "status": "ACTIVE",
      "createTime": "2024-01-01T00:00:00Z"
    }
  }
}
```

### 加入协作会话
```http
POST /api/collaboration/session/{sessionId}/join?userId=2
```

### 获取协作会话
```http
GET /api/collaboration/session/{sessionId}
```

### 结束协作会话
```http
POST /api/collaboration/session/{sessionId}/end
```

### 发送协作消息
```http
POST /api/collaboration/session/{sessionId}/message?userId=1&message=这个商品不错&messageType=text
```

### 添加商品标注
```http
POST /api/collaboration/session/{sessionId}/annotation?userId=1&content=这里有问题&x=100&y=200
```

### 获取用户协作会话列表
```http
GET /api/collaboration/sessions?userId=1
```

## 🎯 推荐系统

### 场景推荐
```http
GET /api/recommendation/scenario?userId=1&scenario=工作
```

**响应：**
```json
{
  "code": 200,
  "message": "场景推荐生成成功",
  "data": {
    "scenario": "工作",
    "recommendedProducts": [
      {
        "id": 1,
        "name": "商务笔记本电脑",
        "price": 5999.00,
        "image": "https://example.com/laptop.jpg"
      }
    ],
    "confidence": 0.85,
    "algorithm": "scenario_based",
    "timestamp": "2024-01-01T00:00:00Z"
  }
}
```

### 生活方式推荐
```http
GET /api/recommendation/lifestyle?userId=1&lifestyle=简约
```

### AI预测推荐
```http
GET /api/recommendation/predict?userId=1
```

### 获取推荐算法列表
```http
GET /api/recommendation/algorithms
```

**响应：**
```json
{
  "code": 200,
  "message": "获取推荐算法列表成功",
  "data": [
    {
      "key": "collaborative",
      "name": "协同过滤",
      "description": "基于用户行为相似性推荐",
      "accuracy": 85,
      "speed": 120,
      "type": "collaborative"
    }
  ]
}
```

### 获取用户推荐历史
```http
GET /api/recommendation/history?userId=1&page=1&size=20
```

### 获取推荐统计
```http
GET /api/recommendation/stats?userId=1
```

### 反馈推荐结果
```http
POST /api/recommendation/feedback?userId=1&recommendationId=rec_1&feedbackType=like&rating=5
```

## ♻️ 价值循环

### 创建回收订单
```http
POST /api/recycle/order
```

**请求体：**
```json
{
  "productName": "旧手机",
  "quantity": 1,
  "estimatedValue": 500.00,
  "pickupDate": "2024-01-15T10:00:00Z",
  "notes": "手机功能正常"
}
```

**响应：**
```json
{
  "code": 200,
  "message": "回收订单创建成功",
  "data": {
    "id": 1,
    "userId": 1,
    "productName": "旧手机",
    "quantity": 1,
    "estimatedValue": 500.00,
    "status": "PENDING",
    "createTime": "2024-01-01T00:00:00Z"
  }
}
```

### 获取用户回收订单
```http
GET /api/recycle/orders?userId=1
```

### 更新回收订单状态
```http
PUT /api/recycle/order/{orderId}/status?status=COMPLETED
```

### 获取环保活动列表
```http
GET /api/recycle/activities
```

**响应：**
```json
{
  "code": 200,
  "message": "获取环保活动成功",
  "data": [
    {
      "id": 1,
      "title": "30天无塑料挑战",
      "description": "挑战30天不使用一次性塑料制品",
      "type": "CHALLENGE",
      "status": "ONGOING",
      "startDate": "2024-01-01T00:00:00Z",
      "endDate": "2024-01-31T23:59:59Z",
      "participants": 50,
      "maxParticipants": 100,
      "points": 100,
      "difficulty": "MEDIUM",
      "category": "环保挑战"
    }
  ]
}
```

### 参加环保活动
```http
POST /api/recycle/activity/{activityId}/join?userId=1
```

### 获取用户回收统计
```http
GET /api/recycle/stats?userId=1
```

**响应：**
```json
{
  "code": 200,
  "message": "获取回收统计成功",
  "data": {
    "totalOrders": 25,
    "totalValue": 12500.00,
    "sustainabilityScore": 850,
    "ecoLevel": "Gold",
    "carbonSaved": 62.5,
    "treesPlanted": 2.5,
    "waterSaved": 1250.0,
    "energySaved": 125.0,
    "monthlyTrend": [
      {
        "month": "January",
        "orders": 5,
        "value": 2500.00,
        "score": 50
      }
    ]
  }
}
```

### 获取回收订单详情
```http
GET /api/recycle/order/{orderId}
```

## 📊 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 🔄 响应格式

所有API响应都遵循统一格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 📝 分页格式

分页查询的响应格式：

```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "list": [],
    "total": 100,
    "page": 1,
    "size": 20
  }
}
```

## 🚀 使用示例

### JavaScript示例
```javascript
// 获取商品列表
const response = await fetch('/api/products?page=1&size=20', {
  headers: {
    'Authorization': 'Bearer ' + token,
    'Content-Type': 'application/json'
  }
});
const data = await response.json();
console.log(data.data.list);
```

### Java示例
```java
// 使用RestTemplate调用API
RestTemplate restTemplate = new RestTemplate();
HttpHeaders headers = new HttpHeaders();
headers.setBearerAuth(token);
HttpEntity<String> entity = new HttpEntity<>(headers);

ResponseEntity<ApiResponse> response = restTemplate.exchange(
    "/api/products?page=1&size=20",
    HttpMethod.GET,
    entity,
    ApiResponse.class
);
```

## 📞 技术支持

如有API使用问题，请联系：
- **邮箱**：api-support@shopx.com
- **文档**：https://docs.shopx.com
- **GitHub**：https://github.com/shopx/api-docs
