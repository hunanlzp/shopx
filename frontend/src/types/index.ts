// ============================================
// ShopX Frontend Type Definitions
// ============================================

// ========== 价格相关类型 ==========
export interface PriceHistory {
  id: number
  productId: number
  price: number
  recordDate: string
}

export interface PriceProtection {
  id: number
  orderId: number
  userId: number
  originalPrice: number
  currentPrice: number
  refundAmount: number
  status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'COMPLETED'
  createTime: string
  updateTime: string
}

export interface TotalPriceCalculation {
  productPrice: number
  quantity: number
  subtotal: number
  shippingFee: number
  taxRate: number
  taxAmount: number
  total: number
}

// ========== 购物车和结算相关类型 ==========
export interface CartItemStatus {
  cartItemId: number
  productId: number
  productName: string
  status: 'AVAILABLE' | 'OUT_OF_STOCK' | 'PRICE_CHANGED' | 'UNAVAILABLE'
  currentPrice?: number
  originalPrice?: number
  stock?: number
  message?: string
}

export interface CheckoutOrder {
  cartItemIds: number[]
  shippingAddressId: number
  paymentMethodId?: number
  shippingMethod: 'STANDARD' | 'EXPRESS' | 'OVERNIGHT'
  notes?: string
}

export interface GuestCheckout {
  sessionId: string
  shippingAddress: {
    receiverName: string
    receiverPhone: string
    province: string
    city: string
    district: string
    detailAddress: string
    postalCode?: string
  }
  paymentMethod: 'ALIPAY' | 'WECHAT' | 'CREDIT_CARD' | 'CASH_ON_DELIVERY'
  items: Array<{
    productId: number
    quantity: number
  }>
}

export interface PaymentMethod {
  id: number
  userId: number
  type: 'ALIPAY' | 'WECHAT' | 'CREDIT_CARD' | 'BANK_CARD'
  account: string
  accountName: string
  isDefault: boolean
  createTime: string
}

// ========== 物流相关类型 ==========
export interface LogisticsTracking {
  id: number
  orderId: number
  trackingNumber: string
  carrier: string
  status: 'PENDING' | 'PICKED_UP' | 'IN_TRANSIT' | 'OUT_FOR_DELIVERY' | 'DELIVERED' | 'EXCEPTION'
  currentLocation?: string
  estimatedDeliveryTime?: string
  actualDeliveryTime?: string
  details: Array<{
    timestamp: string
    location?: string
    description: string
    status: string
  }>
  createTime: string
  updateTime: string
}

export interface ShippingOption {
  method: 'STANDARD' | 'EXPRESS' | 'OVERNIGHT'
  name: string
  estimatedDays: number
  fee: number
  description?: string
}

export interface ShippingAddress {
  id: number
  userId: number
  receiverName: string
  receiverPhone: string
  province: string
  city: string
  district: string
  detailAddress: string
  postalCode?: string
  isDefault: boolean
  createTime: string
  updateTime: string
}

// ========== 退货退款相关类型 ==========
export interface ReturnOrder {
  id: number
  orderId: number
  userId: number
  productId: number
  productName: string
  quantity: number
  reason: string
  description?: string
  images?: string[]
  status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'RETURNING' | 'RETURNED' | 'REFUNDED' | 'CANCELLED'
  refundAmount: number
  refundMethod?: string
  trackingNumber?: string
  createTime: string
  updateTime: string
}

// ========== 推荐系统相关类型 ==========
export interface RecommendationFeedback {
  id: number
  userId: number
  productId: number
  algorithm: string
  feedbackType: 'LIKE' | 'DISLIKE' | 'PURCHASE' | 'IGNORE'
  reason?: string
  createTime: string
}

export interface UserRecommendationPreference {
  id: number
  userId: number
  filterPurchased: boolean
  filterReviewed: boolean
  algorithmWeights: {
    collaborative: number
    content: number
    hybrid: number
  }
  preferredCategories?: string[]
  excludedCategories?: string[]
  updateTime: string
}

// ========== 账户安全相关类型 ==========
export interface LoginHistory {
  id: number
  userId: number
  ipAddress: string
  location?: string
  device: string
  browser: string
  os: string
  loginTime: string
  isAbnormal: boolean
  status: 'SUCCESS' | 'FAILED'
}

export interface TwoFactorAuth {
  id: number
  userId: number
  authMethod: 'SMS' | 'EMAIL' | 'TOTP'
  phoneOrEmail: string
  isEnabled: boolean
  backupCodes?: string[]
  createTime: string
  updateTime: string
}

export interface AccountSecurity {
  id: number
  userId: number
  passwordStrength: 'WEAK' | 'MEDIUM' | 'STRONG'
  lastPasswordChange: string
  enableLoginAlerts: boolean
  enableTransactionAlerts: boolean
  trustedDevices: Array<{
    deviceId: string
    deviceName: string
    lastUsed: string
  }>
  updateTime: string
}

export interface SecurityStats {
  totalLogins: number
  successfulLogins: number
  failedLogins: number
  abnormalLogins: number
  lastLoginTime: string
  twoFactorEnabled: boolean
  passwordLastChanged: string
}

// ========== 库存管理相关类型 ==========
export interface StockNotification {
  id: number
  userId: number
  productId: number
  productName: string
  currentStock: number
  isNotified: boolean
  notifyTime?: string
  createTime: string
}

export interface ProductReservation {
  id: number
  userId: number
  productId: number
  productName: string
  quantity: number
  status: 'PENDING' | 'CONFIRMED' | 'CANCELLED' | 'EXPIRED'
  expireTime: string
  createTime: string
}

// ========== 商品对比相关类型 ==========
export interface ProductComparison {
  id: number
  userId: number
  comparisonName: string
  productIds: number[]
  isPublic: boolean
  shareLink?: string
  createTime: string
  updateTime: string
}

export interface ComparisonTable {
  products: Array<{
    id: number
    name: string
    price: number
    category: string
    [key: string]: any
  }>
  attributes: Array<{
    name: string
    values: any[]
  }>
}

// ========== 愿望清单相关类型 ==========
export interface WishlistItem {
  id: number
  userId: number
  productId: number
  product: {
    id: number
    name: string
    price: number
    image?: string
    stock: number
  }
  category?: string
  priceAlert: boolean
  targetPrice?: number
  notes?: string
  createTime: string
  updateTime: string
}

// ========== 客服系统相关类型 ==========
export interface CustomerServiceTicket {
  id: number
  userId: number
  ticketType: 'ORDER' | 'PRODUCT' | 'PAYMENT' | 'LOGISTICS' | 'RETURN' | 'OTHER'
  title: string
  content: string
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT'
  status: 'OPEN' | 'IN_PROGRESS' | 'RESOLVED' | 'CLOSED'
  attachments?: string[]
  replies: Array<{
    id: number
    userId: number
    content: string
    isStaff: boolean
    createTime: string
  }>
  createTime: string
  updateTime: string
}

export interface FAQ {
  id: number
  category: string
  question: string
  answer: string
  helpfulCount: number
  viewCount: number
  isPublished: boolean
  createTime: string
  updateTime: string
}

// ========== 评价系统相关类型 ==========
export interface ProductReview {
  id: number
  productId: number
  userId: number
  orderId?: number
  username?: string
  avatar?: string
  rating: number
  content: string
  images?: string[]
  videos?: string[]
  helpfulCount: number
  merchantReply?: string
  merchantReplyTime?: string
  isVerifiedPurchase: boolean
  createTime: string
  updateTime: string
}

export interface ReviewVote {
  id: number
  reviewId: number
  userId: number
  helpful: boolean
  createTime: string
}

export interface ReviewStats {
  totalReviews: number
  averageRating: number
  ratingDistribution: {
    '5': number
    '4': number
    '3': number
    '2': number
    '1': number
  }
  verifiedPurchaseCount: number
  withImagesCount: number
  withVideosCount: number
}

// ========== 移动端相关类型 ==========
export interface DeviceInfo {
  isMobile: boolean
  isTablet: boolean
  isDesktop: boolean
  os: string
  browser: string
  screenWidth: number
  screenHeight: number
  userAgent: string
}

export interface MobileConfig {
  enablePWA: boolean
  enableOfflineMode: boolean
  enablePushNotifications: boolean
  cacheStrategy: 'CACHE_FIRST' | 'NETWORK_FIRST' | 'STALE_WHILE_REVALIDATE'
}

// ========== 通用类型 ==========
export interface PaginatedResponse<T> {
  list: T[]
  total: number
  page: number
  size: number
  totalPages: number
}

export interface ApiError {
  code: number
  message: string
  timestamp: string
}

