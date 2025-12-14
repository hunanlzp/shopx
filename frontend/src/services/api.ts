import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios'

// API响应接口
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
  timestamp: string
}

// API服务类
class ApiService {
  private instance: AxiosInstance
  private baseURL: string

  constructor() {
    this.baseURL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080/api'
    
    this.instance = axios.create({
      baseURL: this.baseURL,
      timeout: 10000,
      headers: {
        'Content-Type': 'application/json',
      },
    })

    this.setupInterceptors()
  }

  // 设置拦截器
  private setupInterceptors(): void {
    // 请求拦截器
    this.instance.interceptors.request.use(
      (config) => {
        // 添加认证token
        const token = localStorage.getItem('token')
        if (token) {
          config.headers.Authorization = `Bearer ${token}`
        }
        
        // 添加请求时间戳
        config.metadata = { startTime: Date.now() }
        
        return config
      },
      (error) => {
        return Promise.reject(error)
      }
    )

    // 响应拦截器
    this.instance.interceptors.response.use(
      (response: AxiosResponse) => {
        // 计算请求耗时
        const duration = Date.now() - response.config.metadata?.startTime
        console.log(`API请求耗时: ${duration}ms`)
        
        return response
      },
      (error) => {
        // 统一错误处理
        if (error.response?.status === 401) {
          // 未授权，清除token并跳转到登录页
          localStorage.removeItem('token')
          window.location.href = '/login'
        }
        
        return Promise.reject(this.handleError(error))
      }
    )
  }

  // 错误处理
  private handleError(error: any): any {
    if (error.response) {
      // 服务器响应错误
      const { status, data } = error.response
      return {
        code: status,
        message: data?.message || '服务器错误',
        data: null
      }
    } else if (error.request) {
      // 网络错误
      return {
        code: 0,
        message: '网络连接失败',
        data: null
      }
    } else {
      // 其他错误
      return {
        code: -1,
        message: error.message || '未知错误',
        data: null
      }
    }
  }

  // 通用请求方法
  private async request<T = any>(config: AxiosRequestConfig): Promise<ApiResponse<T>> {
    try {
      const response = await this.instance.request<ApiResponse<T>>(config)
      return response.data
    } catch (error) {
      throw error
    }
  }

  // GET请求
  async get<T = any>(url: string, params?: any): Promise<ApiResponse<T>> {
    return this.request<T>({
      method: 'GET',
      url,
      params
    })
  }

  // POST请求
  async post<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
    return this.request<T>({
      method: 'POST',
      url,
      data,
      ...config
    })
  }

  // PUT请求
  async put<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
    return this.request<T>({
      method: 'PUT',
      url,
      data,
      ...config
    })
  }

  // DELETE请求
  async delete<T = any>(url: string, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
    return this.request<T>({
      method: 'DELETE',
      url,
      ...config
    })
  }

  // 用户相关API
  async login(username: string, password: string): Promise<ApiResponse<{ token: string; user: any }>> {
    return this.post('/auth/login', { username, password })
  }

  async logout(): Promise<ApiResponse<void>> {
    return this.post('/auth/logout')
  }

  async getUserInfo(): Promise<ApiResponse<any>> {
    return this.get('/auth/user-info')
  }

  // 商品相关API
  async getProducts(page: number = 1, size: number = 20, keyword?: string, category?: string): Promise<ApiResponse<any>> {
    return this.get('/products', { page, size, keyword, category })
  }

  async getProductById(id: number): Promise<ApiResponse<any>> {
    return this.get(`/products/${id}`)
  }

  async createProduct(product: any): Promise<ApiResponse<any>> {
    return this.post('/products', product)
  }

  async updateProduct(id: number, product: any): Promise<ApiResponse<any>> {
    return this.put(`/products/${id}`, product)
  }

  async deleteProduct(id: number): Promise<ApiResponse<void>> {
    return this.delete(`/products/${id}`)
  }

  async getHotProducts(limit: number = 10): Promise<ApiResponse<any[]>> {
    return this.get('/products/hot', { limit })
  }

  async searchProducts(keyword: string, page: number = 1, size: number = 20): Promise<ApiResponse<any>> {
    return this.get('/products/search', { keyword, page, size })
  }

  async advancedSearch(keyword: string, filters: any, page: number = 1, size: number = 20): Promise<ApiResponse<any>> {
    return this.post('/products/search/advanced', filters, {
      params: { keyword, page, size }
    })
  }

  async getSearchSuggestions(keyword: string, limit: number = 10): Promise<ApiResponse<string[]>> {
    return this.get('/products/search/suggestions', { keyword, limit })
  }

  async getSearchHistory(limit: number = 20): Promise<ApiResponse<any[]>> {
    return this.get('/products/search/history', { limit })
  }

  async saveFilter(filterName: string, filterConditions: any, isDefault: boolean = false): Promise<ApiResponse<any>> {
    return this.post('/products/search/filters', filterConditions, {
      params: { filterName, isDefault }
    })
  }

  async getSavedFilters(): Promise<ApiResponse<any[]>> {
    return this.get('/products/search/filters')
  }

  async deleteSavedFilter(filterId: number): Promise<ApiResponse<void>> {
    return this.delete(`/products/search/filters/${filterId}`)
  }

  async getProductsByCategory(category: string, page: number = 1, size: number = 20): Promise<ApiResponse<any>> {
    return this.get(`/products/category/${category}`, { page, size })
  }

  // AR/VR相关API
  async getARExperience(productId: number): Promise<ApiResponse<any>> {
    return this.get(`/ar-vr/ar/${productId}`)
  }

  async getVRExperience(productId: number): Promise<ApiResponse<any>> {
    return this.get(`/ar-vr/vr/${productId}`)
  }

  async getModelInfo(productId: number): Promise<ApiResponse<any>> {
    return this.get(`/ar-vr/model/${productId}`)
  }

  async recordInteraction(productId: number, interactionType: string, interactionData?: any): Promise<ApiResponse<void>> {
    return this.post('/ar-vr/interaction', interactionData, {
      params: { productId, interactionType }
    })
  }

  async getExperienceStats(): Promise<ApiResponse<any>> {
    return this.get('/ar-vr/stats')
  }

  // AI助手相关API
  async chatWithAI(message: string, sessionId?: string): Promise<ApiResponse<any>> {
    return this.post('/ai-assistant/chat', null, {
      params: { message, sessionId }
    })
  }

  // 便捷方法：发送消息并直接返回数据
  async sendAIMessage(userId: number, message: string, sessionId?: string) {
    const res = await this.chatWithAI(message, sessionId)
    return res.data
  }

  async getAISuggestions(type?: string) {
    const res = await this.get<ApiResponse<any[]>>('/ai-assistant/suggestions', { type })
    return res.data
  }

  async getChatHistory(sessionId?: string, page: number = 1, size: number = 20) {
    const res = await this.get<ApiResponse<any>>('/ai-assistant/history', { sessionId, page, size })
    return res.data?.history || res.data
  }
  // 兼容调用别名
  async getAIChatHistory(sessionId?: string, page: number = 1, size: number = 20) {
    return this.getChatHistory(sessionId, page, size)
  }

  async clearChatHistory(sessionId?: string): Promise<ApiResponse<void>> {
    return this.delete('/ai-assistant/history', {
      params: { sessionId }
    })
  }

  async getAIStatus() {
    const res = await this.get<ApiResponse<any>>('/ai-assistant/status')
    return res.data
  }

  async setAIPreferences(preferences: any) {
    const res = await this.post<ApiResponse<void>>('/ai-assistant/preferences', preferences)
    return res.data
  }

  // 协作购物相关API
  async createCollaborationSession(hostUserId: number, productId: number): Promise<ApiResponse<any>> {
    return this.post('/collaboration/session', null, {
      params: { hostUserId, productId }
    })
  }

  async joinCollaborationSession(sessionId: string, userId: number): Promise<ApiResponse<void>> {
    return this.post(`/collaboration/session/${sessionId}/join`, null, {
      params: { userId }
    })
  }

  async getCollaborationSession(sessionId: string): Promise<ApiResponse<any>> {
    return this.get(`/collaboration/session/${sessionId}`)
  }

  async endCollaborationSession(sessionId: string): Promise<ApiResponse<void>> {
    return this.post(`/collaboration/session/${sessionId}/end`)
  }

  async sendCollaborationMessage(sessionId: string, userId: number, message: string, messageType: string = 'text'): Promise<ApiResponse<void>> {
    return this.post(`/collaboration/session/${sessionId}/message`, null, {
      params: { userId, message, messageType }
    })
  }

  async addAnnotation(sessionId: string, userId: number, content: string, x: number, y: number): Promise<ApiResponse<void>> {
    return this.post(`/collaboration/session/${sessionId}/annotation`, null, {
      params: { userId, content, x, y }
    })
  }

  async getUserCollaborationSessions(userId: number): Promise<ApiResponse<any[]>> {
    return this.get('/collaboration/sessions', { userId })
  }

  // 推荐系统相关API
  async getScenarioRecommendation(userId: number, scenario: string): Promise<ApiResponse<any>> {
    return this.get('/recommendation/scenario', { userId, scenario })
  }

  async getLifestyleRecommendation(userId: number, lifestyle: string): Promise<ApiResponse<any>> {
    return this.get('/recommendation/lifestyle', { userId, lifestyle })
  }

  async getPredictRecommendation(userId: number): Promise<ApiResponse<any>> {
    return this.get('/recommendation/predict', { userId })
  }

  async getRecommendationAlgorithms(): Promise<ApiResponse<any[]>> {
    return this.get('/recommendation/algorithms')
  }

  async getUserRecommendationHistory(userId: number, page: number = 1, size: number = 20): Promise<ApiResponse<any[]>> {
    return this.get('/recommendation/history', { userId, page, size })
  }

  async getRecommendationStats(userId: number): Promise<ApiResponse<any>> {
    return this.get('/recommendation/stats', { userId })
  }

  async feedbackRecommendation(userId: number, recommendationId: string, feedbackType: string, rating?: number): Promise<ApiResponse<void>> {
    return this.post('/recommendation/feedback', null, {
      params: { userId, recommendationId, feedbackType, rating }
    })
  }

  // 价值循环相关API
  async createRecycleOrder(orderData: any): Promise<ApiResponse<any>> {
    return this.post('/recycle/order', orderData)
  }

  async getUserRecycleOrders(userId: number): Promise<ApiResponse<any[]>> {
    return this.get('/recycle/orders', { userId })
  }

  async updateRecycleOrderStatus(orderId: number, status: string): Promise<ApiResponse<void>> {
    return this.put(`/recycle/order/${orderId}/status`, null, {
      params: { status }
    })
  }

  async getEcoActivities(): Promise<ApiResponse<any[]>> {
    return this.get('/recycle/activities')
  }

  async joinEcoActivity(activityId: number, userId: number): Promise<ApiResponse<void>> {
    return this.post(`/recycle/activity/${activityId}/join`, null, {
      params: { userId }
    })
  }

  async getUserRecycleStats(userId: number): Promise<ApiResponse<any>> {
    return this.get('/recycle/stats', { userId })
  }

  // 社区相关API
  async createCommunityPost(postData: any): Promise<ApiResponse<any>> {
    return this.post('/recycle/community/post', postData)
  }

  async getCommunityPosts(type?: string, category?: string, page: number = 1, size: number = 20): Promise<ApiResponse<any[]>> {
    return this.get('/recycle/community/posts', { type, category, page, size })
  }

  async getPostComments(postId: number, page: number = 1, size: number = 20): Promise<ApiResponse<any[]>> {
    return this.get(`/recycle/community/post/${postId}/comments`, { page, size })
  }

  async addCommunityComment(commentData: any): Promise<ApiResponse<any>> {
    return this.post('/recycle/community/comment', commentData)
  }

  async likeCommunityPost(postId: number): Promise<ApiResponse<void>> {
    return this.post(`/recycle/community/post/${postId}/like`)
  }

  async getUserRanking(type: string = 'sustainability', limit: number = 10): Promise<ApiResponse<any[]>> {
    return this.get('/recycle/community/ranking', { type, limit })
  }

  async getRecycleOrder(orderId: number): Promise<ApiResponse<any>> {
    return this.get(`/recycle/order/${orderId}`)
  }

  // 用户行为记录
  async recordUserBehavior(userId: number, productId: number, action: string): Promise<ApiResponse<void>> {
    return this.post('/user-behavior', {
      userId,
      productId,
      action,
      timestamp: new Date().toISOString()
    })
  }

  // 购物车相关API
  async getCart(): Promise<ApiResponse<any[]>> {
    return this.get('/cart')
  }

  async addToCart(productId: number, quantity: number = 1): Promise<ApiResponse<any>> {
    return this.post('/cart/add', null, {
      params: { productId, quantity }
    })
  }

  async updateCartItem(productId: number, quantity: number): Promise<ApiResponse<any>> {
    return this.put('/cart/update', null, {
      params: { productId, quantity }
    })
  }

  async removeFromCart(productId: number): Promise<ApiResponse<void>> {
    return this.delete('/cart/remove', {
      params: { productId }
    })
  }

  async clearCart(): Promise<ApiResponse<void>> {
    return this.delete('/cart/clear')
  }

  async getCartCount(): Promise<ApiResponse<number>> {
    return this.get('/cart/count')
  }

  // 订单相关API
  async createOrder(cartItemIds?: number[], shippingAddress?: string): Promise<ApiResponse<any>> {
    return this.post('/orders', null, {
      params: { cartItemIds, shippingAddress }
    })
  }

  async getOrders(page: number = 1, size: number = 20): Promise<ApiResponse<any>> {
    return this.get('/orders', { page, size })
  }

  async getOrderById(orderId: number): Promise<ApiResponse<any>> {
    return this.get(`/orders/${orderId}`)
  }

  async cancelOrder(orderId: number): Promise<ApiResponse<void>> {
    return this.post(`/orders/${orderId}/cancel`)
  }

  async payOrder(orderId: number, paymentMethod: string = 'ALIPAY'): Promise<ApiResponse<void>> {
    return this.post(`/orders/${orderId}/pay`, null, {
      params: { paymentMethod }
    })
  }

  // ========== 价格透明化API ==========
  async getPriceHistory(productId: number, days: number = 30): Promise<ApiResponse<any[]>> {
    return this.get(`/price/history/${productId}`, { days })
  }

  async calculateTotalPrice(productId: number, quantity: number, addressId: number): Promise<ApiResponse<any>> {
    return this.post('/price/calculate', {
      productId,
      quantity,
      shippingAddressId: addressId
    })
  }

  async createPriceProtection(orderId: number): Promise<ApiResponse<any>> {
    return this.post('/price/protection', { orderId })
  }

  async getPriceProtection(orderId: number): Promise<ApiResponse<any>> {
    return this.get(`/price/protection/${orderId}`)
  }

  // ========== 购物车和结算API ==========
  async checkCartItemStatus(cartItemIds: number[]): Promise<ApiResponse<any[]>> {
    return this.post('/cart/check-status', { cartItemIds })
  }

  async createCheckoutOrder(data: {
    cartItemIds: number[]
    shippingAddressId: number
    paymentMethodId?: number
    shippingMethod: string
    notes?: string
  }): Promise<ApiResponse<any>> {
    return this.post('/checkout/create', data)
  }

  async createGuestCheckout(data: {
    sessionId: string
    shippingAddress: any
    paymentMethod: string
    items: Array<{ productId: number; quantity: number }>
  }): Promise<ApiResponse<any>> {
    return this.post('/checkout/guest', data)
  }

  async getPaymentMethods(): Promise<ApiResponse<any[]>> {
    return this.get('/payment/methods')
  }

  async addPaymentMethod(data: {
    type: string
    account: string
    accountName: string
    isDefault?: boolean
  }): Promise<ApiResponse<any>> {
    return this.post('/payment/methods', data)
  }

  async deletePaymentMethod(methodId: number): Promise<ApiResponse<void>> {
    return this.delete(`/payment/methods/${methodId}`)
  }

  // ========== 物流追踪API ==========
  async getLogisticsTracking(orderId: number): Promise<ApiResponse<any>> {
    return this.get(`/logistics/tracking/order/${orderId}`)
  }

  async getShippingOptions(productId: number, addressId: number): Promise<ApiResponse<any[]>> {
    return this.get('/logistics/shipping-options', { productId, addressId })
  }

  async getShippingAddresses(): Promise<ApiResponse<any[]>> {
    return this.get('/logistics/addresses')
  }

  async addShippingAddress(data: {
    receiverName: string
    receiverPhone: string
    province: string
    city: string
    district: string
    detailAddress: string
    postalCode?: string
    isDefault?: boolean
  }): Promise<ApiResponse<any>> {
    return this.post('/logistics/addresses', data)
  }

  async updateShippingAddress(addressId: number, data: any): Promise<ApiResponse<void>> {
    return this.put(`/logistics/addresses/${addressId}`, data)
  }

  async deleteShippingAddress(addressId: number): Promise<ApiResponse<void>> {
    return this.delete(`/logistics/addresses/${addressId}`)
  }

  // ========== 退货退款API ==========
  async createReturnOrder(data: {
    orderId: number
    productId: number
    quantity: number
    reason: string
    description?: string
    images?: string[]
  }): Promise<ApiResponse<any>> {
    return this.post('/return/create', data)
  }

  async getReturnOrder(returnId: number): Promise<ApiResponse<any>> {
    return this.get(`/return/${returnId}`)
  }

  async cancelReturnOrder(returnId: number): Promise<ApiResponse<void>> {
    return this.post(`/return/${returnId}/cancel`)
  }

  async getReturnOrders(page: number = 1, size: number = 20): Promise<ApiResponse<any>> {
    return this.get('/return/list', { page, size })
  }

  // ========== 推荐系统API ==========
  async getRecommendationWithFilter(userId: number, limit: number = 10): Promise<ApiResponse<any[]>> {
    return this.get('/recommendation/collaborative', { userId, limit })
  }

  async recordRecommendationFeedback(data: {
    productId: number
    algorithm: string
    feedbackType: string
    reason?: string
  }): Promise<ApiResponse<void>> {
    return this.post('/recommendation/feedback', null, {
      params: data
    })
  }

  async getRecommendationPreferences(): Promise<ApiResponse<any>> {
    return this.get('/recommendation/preferences')
  }

  async updateRecommendationPreferences(data: {
    filterPurchased?: boolean
    filterReviewed?: boolean
    algorithmWeights?: {
      collaborative: number
      content: number
      hybrid: number
    }
    preferredCategories?: string[]
    excludedCategories?: string[]
  }): Promise<ApiResponse<void>> {
    return this.put('/recommendation/preferences', data)
  }

  // ========== 账户安全API ==========
  async getLoginHistory(page: number = 1, size: number = 20): Promise<ApiResponse<any>> {
    return this.get('/security/login-history', { page, size })
  }

  async enable2FA(authMethod: 'SMS' | 'EMAIL' | 'TOTP', phoneOrEmail: string): Promise<ApiResponse<any>> {
    return this.post('/security/2fa/enable', null, {
      params: { authMethod, phoneOrEmail }
    })
  }

  async disable2FA(): Promise<ApiResponse<void>> {
    return this.post('/security/2fa/disable')
  }

  async verify2FACode(code: string): Promise<ApiResponse<{ valid: boolean }>> {
    return this.post('/security/2fa/verify', null, {
      params: { code }
    })
  }

  async generateBackupCodes(): Promise<ApiResponse<string[]>> {
    return this.post('/security/2fa/backup-codes')
  }

  async getAccountSecurity(): Promise<ApiResponse<any>> {
    return this.get('/security/settings')
  }

  async updateAccountSecurity(settings: {
    enableLoginAlerts?: boolean
    enableTransactionAlerts?: boolean
  }): Promise<ApiResponse<void>> {
    return this.put('/security/settings', settings)
  }

  async deleteAccount(password: string): Promise<ApiResponse<void>> {
    return this.post('/security/account/delete', null, {
      params: { password }
    })
  }

  async getSecurityStats(): Promise<ApiResponse<any>> {
    return this.get('/security/stats')
  }

  // ========== 库存管理API ==========
  async checkStock(productId: number, quantity: number): Promise<ApiResponse<{ available: boolean; stock: number }>> {
    return this.get('/stock/check', { productId, quantity })
  }

  async addStockNotification(productId: number): Promise<ApiResponse<any>> {
    return this.post('/stock/notification', null, {
      params: { productId }
    })
  }

  async getStockNotifications(): Promise<ApiResponse<any[]>> {
    return this.get('/stock/notifications')
  }

  async cancelStockNotification(notificationId: number): Promise<ApiResponse<void>> {
    return this.delete(`/stock/notifications/${notificationId}`)
  }

  async createReservation(productId: number, quantity: number): Promise<ApiResponse<any>> {
    return this.post('/stock/reservation', null, {
      params: { productId, quantity }
    })
  }

  async getReservations(): Promise<ApiResponse<any[]>> {
    return this.get('/stock/reservations')
  }

  async cancelReservation(reservationId: number): Promise<ApiResponse<void>> {
    return this.delete(`/stock/reservations/${reservationId}`)
  }

  async getAlternativeProducts(productId: number, limit: number = 5): Promise<ApiResponse<any[]>> {
    return this.get(`/stock/alternatives/${productId}`, { limit })
  }

  // ========== 商品对比API ==========
  async createComparison(data: {
    comparisonName: string
    productIds: number[]
    isPublic?: boolean
  }): Promise<ApiResponse<any>> {
    return this.post('/comparison', data.productIds, {
      params: {
        comparisonName: data.comparisonName,
        isPublic: data.isPublic || false
      }
    })
  }

  async getComparison(comparisonId: number): Promise<ApiResponse<any>> {
    return this.get(`/comparison/${comparisonId}`)
  }

  async getComparisons(): Promise<ApiResponse<any[]>> {
    return this.get('/comparison/list')
  }

  async updateComparison(comparisonId: number, data: {
    comparisonName?: string
    productIds?: number[]
    isPublic?: boolean
  }): Promise<ApiResponse<void>> {
    return this.put(`/comparison/${comparisonId}`, data)
  }

  async deleteComparison(comparisonId: number): Promise<ApiResponse<void>> {
    return this.delete(`/comparison/${comparisonId}`)
  }

  async generateComparisonTable(productIds: number[]): Promise<ApiResponse<any>> {
    return this.post('/comparison/table', productIds)
  }

  async getComparisonByShareLink(shareLink: string): Promise<ApiResponse<any>> {
    return this.get(`/comparison/share/${shareLink}`)
  }

  // ========== 愿望清单API ==========
  async addToWishlist(productId: number, category?: string, notes?: string): Promise<ApiResponse<any>> {
    return this.post('/wishlist', null, {
      params: { productId, category, notes }
    })
  }

  async removeFromWishlist(productId: number): Promise<ApiResponse<void>> {
    return this.delete(`/wishlist/${productId}`)
  }

  async getWishlist(category?: string, page: number = 1, size: number = 20): Promise<ApiResponse<any>> {
    return this.get('/wishlist', { category, page, size })
  }

  async setPriceAlert(productId: number, targetPrice: number): Promise<ApiResponse<void>> {
    return this.post('/wishlist/price-alert', null, {
      params: { productId, targetPrice }
    })
  }

  async batchWishlistOperation(ids: number[], operation: 'DELETE' | 'MOVE', category?: string): Promise<ApiResponse<void>> {
    return this.post('/wishlist/batch', ids, {
      params: { operation, category }
    })
  }

  async shareWishlist(category?: string): Promise<ApiResponse<{ shareLink: string }>> {
    return this.post('/wishlist/share', null, {
      params: { category }
    })
  }

  async getWishlistByShareLink(shareLink: string): Promise<ApiResponse<any>> {
    return this.get(`/wishlist/share/${shareLink}`)
  }

  // ========== 客服系统API ==========
  async createTicket(data: {
    ticketType: string
    title: string
    content: string
    priority?: string
    attachments?: string[]
  }): Promise<ApiResponse<any>> {
    return this.post('/customer-service/ticket', null, {
      params: {
        ticketType: data.ticketType,
        title: data.title,
        content: data.content,
        priority: data.priority || 'MEDIUM'
      }
    })
  }

  async getTickets(page: number = 1, size: number = 20): Promise<ApiResponse<any>> {
    return this.get('/customer-service/tickets', { page, size })
  }

  async getTicket(ticketId: number): Promise<ApiResponse<any>> {
    return this.get(`/customer-service/ticket/${ticketId}`)
  }

  async getFAQs(category?: string, page: number = 1, size: number = 20): Promise<ApiResponse<any>> {
    return this.get('/customer-service/faq', { category, page, size })
  }

  async searchFAQs(keyword: string): Promise<ApiResponse<any[]>> {
    return this.get('/customer-service/faq/search', { keyword })
  }

  async markFAQHelpful(faqId: number): Promise<ApiResponse<void>> {
    return this.post(`/customer-service/faq/${faqId}/helpful`)
  }

  // ========== 评价系统API ==========
  async createReview(data: {
    productId: number
    orderId?: number
    rating: number
    content: string
    images?: string[]
    videos?: string[]
  }): Promise<ApiResponse<any>> {
    const formData = new FormData()
    formData.append('productId', data.productId.toString())
    if (data.orderId) formData.append('orderId', data.orderId.toString())
    formData.append('rating', data.rating.toString())
    formData.append('content', data.content)
    if (data.images) formData.append('images', JSON.stringify(data.images))
    if (data.videos) formData.append('videos', JSON.stringify(data.videos))

    return this.post('/reviews', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  }

  async getProductReviews(
    productId: number,
    page: number = 1,
    size: number = 20,
    sortBy: string = 'createTime',
    order: 'asc' | 'desc' = 'desc'
  ): Promise<ApiResponse<any>> {
    return this.get(`/reviews/product/${productId}`, { page, size, sortBy, order })
  }

  async voteReview(reviewId: number, helpful: boolean): Promise<ApiResponse<void>> {
    return this.post(`/reviews/${reviewId}/vote`, null, {
      params: { helpful }
    })
  }

  async getReviewStats(productId: number): Promise<ApiResponse<any>> {
    return this.get(`/reviews/stats/${productId}`)
  }

  async merchantReply(reviewId: number, reply: string): Promise<ApiResponse<void>> {
    return this.post(`/reviews/${reviewId}/reply`, null, {
      params: { reply }
    })
  }

  // ========== 移动端API ==========
  async getDeviceInfo(): Promise<ApiResponse<any>> {
    return this.get('/mobile/device-info')
  }

  async getMobileConfig(): Promise<ApiResponse<any>> {
    return this.get('/mobile/config')
  }

  async checkPWAUpdate(currentVersion: string): Promise<ApiResponse<{ hasUpdate: boolean; newVersion?: string }>> {
    return this.get('/mobile/pwa/check-update', { currentVersion })
  }
}

// 导出单例实例
export default new ApiService()