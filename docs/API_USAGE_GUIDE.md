# ShopX API 使用指南

本文档提供了所有新实现功能的API使用示例。

## 目录

1. [智能搜索和筛选](#智能搜索和筛选)
2. [商品信息管理](#商品信息管理)
3. [价格透明化](#价格透明化)
4. [购物车和结算](#购物车和结算)
5. [物流追踪](#物流追踪)
6. [退货退款](#退货退款)
7. [推荐系统](#推荐系统)
8. [账户安全](#账户安全)
9. [库存管理](#库存管理)
10. [商品对比](#商品对比)
11. [愿望清单](#愿望清单)
12. [客服系统](#客服系统)
13. [评价系统](#评价系统)
14. [移动端支持](#移动端支持)

---

## 智能搜索和筛选

### 高级搜索

```http
POST /api/v1/products/search/advanced
Content-Type: application/json
Authorization: Bearer {token}

{
  "keyword": "笔记本电脑",
  "category": "电子产品",
  "minPrice": 1000,
  "maxPrice": 10000,
  "sortBy": "price",
  "order": "asc",
  "page": 1,
  "size": 20
}
```

### 保存筛选条件

```http
POST /api/v1/search/filters
Content-Type: application/json
Authorization: Bearer {token}

{
  "filterName": "我的笔记本筛选",
  "filterConditions": {
    "category": "电子产品",
    "minPrice": 5000,
    "maxPrice": 15000
  },
  "isDefault": false
}
```

### 获取搜索历史

```http
GET /api/v1/search/history?page=1&size=10
Authorization: Bearer {token}
```

---

## 商品信息管理

### 创建商品评价

```http
POST /api/v1/reviews
Content-Type: application/x-www-form-urlencoded
Authorization: Bearer {token}

productId=123&orderId=456&rating=5&content=很好用&images=["url1","url2"]
```

### 获取商品评价列表

```http
GET /api/v1/reviews/product/123?page=1&size=20&sortBy=rating&order=desc
```

### 评价有用性投票

```http
POST /api/v1/reviews/123/vote?helpful=true
Authorization: Bearer {token}
```

### 商家回复评价

```http
POST /api/v1/reviews/123/reply?reply=感谢您的评价
Authorization: Bearer {token}
```

---

## 价格透明化

### 获取价格历史

```http
GET /api/v1/price/history/123?days=30
```

### 获取总价计算

```http
POST /api/v1/price/calculate
Content-Type: application/json

{
  "productId": 123,
  "quantity": 2,
  "shippingAddressId": 1
}
```

### 申请价格保护

```http
POST /api/v1/price/protection
Content-Type: application/json
Authorization: Bearer {token}

{
  "orderId": 456
}
```

---

## 购物车和结算

### 检查购物车商品状态

```http
POST /api/v1/cart/check-status
Content-Type: application/json
Authorization: Bearer {token}

{
  "cartItemIds": [1, 2, 3]
}
```

### 创建结算订单

```http
POST /api/v1/checkout/create
Content-Type: application/json
Authorization: Bearer {token}

{
  "cartItemIds": [1, 2],
  "shippingAddressId": 1,
  "paymentMethodId": 1,
  "shippingMethod": "EXPRESS"
}
```

### 游客结算

```http
POST /api/v1/checkout/guest
Content-Type: application/json

{
  "sessionId": "guest-session-id",
  "shippingAddress": {
    "receiverName": "张三",
    "receiverPhone": "13800138000",
    "detailAddress": "北京市朝阳区..."
  },
  "paymentMethod": "ALIPAY"
}
```

---

## 物流追踪

### 获取物流信息

```http
GET /api/v1/logistics/tracking/order/456
Authorization: Bearer {token}
```

### 更新物流状态

```http
PUT /api/v1/logistics/tracking/123
Content-Type: application/json
Authorization: Bearer {token}

{
  "status": "IN_TRANSIT",
  "currentLocation": "北京分拨中心",
  "details": {
    "timestamp": "2024-01-01T10:00:00",
    "description": "已发出"
  }
}
```

### 获取配送选项

```http
GET /api/v1/logistics/shipping-options?productId=123&addressId=1
```

---

## 退货退款

### 创建退货申请

```http
POST /api/v1/return/create
Content-Type: application/json
Authorization: Bearer {token}

{
  "orderId": 456,
  "productId": 123,
  "quantity": 1,
  "reason": "质量问题",
  "description": "商品有瑕疵"
}
```

### 获取退货进度

```http
GET /api/v1/return/123
Authorization: Bearer {token}
```

### 取消退货

```http
POST /api/v1/return/123/cancel
Authorization: Bearer {token}
```

---

## 推荐系统

### 获取推荐商品（过滤已购买）

```http
GET /api/v1/recommendation/collaborative?userId=1&limit=10
Authorization: Bearer {token}
```

### 反馈推荐结果

```http
POST /api/v1/recommendation/feedback
Content-Type: application/x-www-form-urlencoded
Authorization: Bearer {token}

productId=123&algorithm=collaborative&feedbackType=LIKE&reason=很感兴趣
```

### 更新推荐偏好

```http
PUT /api/v1/recommendation/preferences
Content-Type: application/json
Authorization: Bearer {token}

{
  "filterPurchased": true,
  "filterReviewed": false,
  "algorithmWeights": {
    "collaborative": 0.5,
    "content": 0.3,
    "hybrid": 0.2
  }
}
```

---

## 账户安全

### 获取登录历史

```http
GET /api/v1/security/login-history?page=1&size=20
Authorization: Bearer {token}
```

### 启用双因素认证

```http
POST /api/v1/security/2fa/enable?authMethod=SMS&phoneOrEmail=13800138000
Authorization: Bearer {token}
```

### 验证2FA码

```http
POST /api/v1/security/2fa/verify?code=123456
Authorization: Bearer {token}
```

### 获取安全统计

```http
GET /api/v1/security/stats
Authorization: Bearer {token}
```

### 更新安全设置

```http
PUT /api/v1/security/settings
Content-Type: application/json
Authorization: Bearer {token}

{
  "loginAlertEnabled": true,
  "abnormalLoginAlertEnabled": true,
  "passwordChangeAlertEnabled": true
}
```

---

## 库存管理

### 添加缺货提醒

```http
POST /api/v1/stock/notification?productId=123
Authorization: Bearer {token}
```

### 创建商品预订

```http
POST /api/v1/stock/reservation?productId=123&quantity=1
Authorization: Bearer {token}
```

### 获取替代商品

```http
GET /api/v1/stock/alternatives/123?limit=5
```

### 获取预订列表

```http
GET /api/v1/stock/reservations
Authorization: Bearer {token}
```

---

## 商品对比

### 创建商品对比

```http
POST /api/v1/comparison?comparisonName=笔记本对比&isPublic=false
Content-Type: application/json
Authorization: Bearer {token}

[123, 456, 789]
```

### 获取对比详情

```http
GET /api/v1/comparison/1
Authorization: Bearer {token}
```

### 生成对比表格

```http
POST /api/v1/comparison/table
Content-Type: application/json

[123, 456, 789]
```

### 分享对比列表

```http
GET /api/v1/comparison/share/{shareLink}
```

---

## 愿望清单

### 添加到愿望清单

```http
POST /api/v1/wishlist?productId=123&category=电子产品&notes=想要这个
Authorization: Bearer {token}
```

### 设置价格提醒

```http
POST /api/v1/wishlist/price-alert?productId=123&targetPrice=5000
Authorization: Bearer {token}
```

### 批量操作

```http
POST /api/v1/wishlist/batch?operation=DELETE
Content-Type: application/json
Authorization: Bearer {token}

[1, 2, 3]
```

### 分享愿望清单

```http
POST /api/v1/wishlist/share?category=电子产品
Authorization: Bearer {token}
```

---

## 客服系统

### 创建客服工单

```http
POST /api/v1/customer-service/ticket?ticketType=ORDER&title=订单问题&content=我的订单没有收到&priority=HIGH
Authorization: Bearer {token}
```

### 获取工单列表

```http
GET /api/v1/customer-service/tickets?page=1&size=20
Authorization: Bearer {token}
```

### 搜索常见问题

```http
GET /api/v1/customer-service/faq/search?keyword=退货
```

### 标记FAQ有用

```http
POST /api/v1/customer-service/faq/123/helpful
Authorization: Bearer {token}
```

---

## 评价系统

### 创建评价

```http
POST /api/v1/reviews?productId=123&orderId=456&rating=5&content=很好用
Authorization: Bearer {token}
```

### 获取评价统计

```http
GET /api/v1/reviews/product/123/stats
```

### 筛选评价

```http
GET /api/v1/reviews/product/123?page=1&size=20&sortBy=helpful&order=desc
```

---

## 移动端支持

### 获取设备信息

```http
GET /api/v1/mobile/device-info
```

### 获取移动端配置

```http
GET /api/v1/mobile/config
```

### 检查PWA更新

```http
GET /api/v1/mobile/pwa/check-update?currentVersion=1.0.0
```

---

## 通用响应格式

所有API都遵循统一的响应格式：

### 成功响应

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    // 响应数据
  },
  "timestamp": "2024-01-01T10:00:00"
}
```

### 分页响应

```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "data": [...],
    "total": 100,
    "page": 1,
    "size": 20,
    "totalPages": 5
  }
}
```

### 错误响应

```json
{
  "code": 400,
  "message": "请求参数错误",
  "data": null,
  "timestamp": "2024-01-01T10:00:00"
}
```

---

## 认证说明

大部分API需要认证，请在请求头中添加：

```
Authorization: Bearer {token}
```

获取token的方式请参考认证相关文档。

---

## 错误码说明

- `200`: 成功
- `400`: 请求参数错误
- `401`: 未认证
- `403`: 无权限
- `404`: 资源不存在
- `500`: 服务器错误

---

## 注意事项

1. 所有时间格式使用ISO 8601格式：`YYYY-MM-DDTHH:mm:ss`
2. 金额使用两位小数：`100.00`
3. 分页参数：`page`从1开始，`size`默认20
4. 所有ID都是Long类型
5. JSON数组使用标准格式：`[1, 2, 3]`

---

**最后更新**: 2024年

