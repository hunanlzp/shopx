import { create } from 'zustand'
import { devtools, persist, subscribeWithSelector } from 'zustand/middleware'
import { immer } from 'zustand/middleware/immer'

// 用户接口
export interface User {
  id: number
  username: string
  email: string
  avatar?: string
  role: string
  enabled: boolean
  followerCount: number
  followingCount: number
  sustainabilityScore: number
  recycleCount: number
  createTime: string
  updateTime: string
}

// 商品接口
export interface Product {
  id: number
  name: string
  description: string
  price: number
  category: string
  image?: string
  stock: number
  status: string
  viewCount: number
  likeCount: number
  shareCount: number
  has3dPreview: boolean
  arModelUrl?: string
  vrExperienceUrl?: string
  createTime: string
  updateTime: string
}

// 购物车项接口
export interface CartItem {
  id: number
  product: Product
  quantity: number
  addedAt: string
}

// 应用状态接口
export interface AppState {
  // 用户状态
  user: User | null
  isAuthenticated: boolean
  token: string | null
  
  // 商品状态
  products: Product[]
  currentProduct: Product | null
  hotProducts: Product[]
  
  // 购物车状态
  cart: CartItem[]
  cartCount: number
  cartTotal: number
  
  // UI状态
  loading: boolean
  error: string | null
  theme: 'light' | 'dark'
  language: 'zh' | 'en'
  
  // AR/VR状态
  arvrExperience: {
    isActive: boolean
    productId: number | null
    experienceType: 'AR' | 'VR' | null
  }
  
  // AI助手状态
  aiAssistant: {
    isActive: boolean
    messages: Array<{
      id: string
      type: 'user' | 'assistant'
      content: string
      timestamp: string
    }>
    isTyping: boolean
  }
  
  // 协作购物状态
  collaboration: {
    isActive: boolean
    sessionId: string | null
    participants: Array<{
      id: number
      username: string
      avatar?: string
      status: 'online' | 'offline' | 'away'
    }>
  }
  
  // 推荐状态
  recommendations: {
    scenarioRecommendations: Product[]
    lifestyleRecommendations: Product[]
    aiRecommendations: Product[]
    history: Array<{
      id: string
      type: string
      products: Product[]
      timestamp: string
    }>
  }
  
  // 价值循环状态
  recycle: {
    orders: Array<{
      id: number
      productName: string
      quantity: number
      status: string
      estimatedValue: number
      actualValue?: number
      createTime: string
    }>
    activities: Array<{
      id: number
      title: string
      description: string
      type: string
      status: string
      participants: number
      points: number
    }>
    stats: {
      totalOrders: number
      totalValue: number
      sustainabilityScore: number
      ecoLevel: string
    }
  }

  // 愿望清单状态
  wishlist: Array<{
    id: number
    productId: number
    product: any
    category?: string
    priceAlert: boolean
    targetPrice?: number
    notes?: string
    createTime: string
  }>

  // 商品对比状态
  comparisons: Array<{
    id: number
    comparisonName: string
    productIds: number[]
    isPublic: boolean
    shareLink?: string
    createTime: string
  }>

  // 退货订单状态
  returnOrders: Array<{
    id: number
    orderId: number
    productId: number
    productName: string
    quantity: number
    reason: string
    status: string
    refundAmount: number
    createTime: string
  }>

  // 安全设置状态
  securitySettings: {
    loginHistory: Array<{
      id: number
      ipAddress: string
      location?: string
      device: string
      loginTime: string
      isAbnormal: boolean
    }>
    twoFactorAuth: {
      isEnabled: boolean
      authMethod?: string
    }
    accountSecurity: {
      passwordStrength: string
      enableLoginAlerts: boolean
      enableTransactionAlerts: boolean
    }
  }

  // 库存通知状态
  stockNotifications: Array<{
    id: number
    productId: number
    productName: string
    currentStock: number
    isNotified: boolean
    createTime: string
  }>

  // 登录历史状态
  loginHistory: Array<{
    id: number
    ipAddress: string
    location?: string
    device: string
    browser: string
    os: string
    loginTime: string
    isAbnormal: boolean
    status: string
  }>
}

// 应用操作接口
export interface AppActions {
  // 用户操作
  setUser: (user: User | null) => void
  setToken: (token: string | null) => void
  logout: () => void
  
  // 商品操作
  setProducts: (products: Product[]) => void
  setCurrentProduct: (product: Product | null) => void
  setHotProducts: (products: Product[]) => void
  
  // 购物车操作
  addToCart: (product: Product, quantity?: number) => void
  removeFromCart: (productId: number) => void
  updateCartQuantity: (productId: number, quantity: number) => void
  clearCart: () => void
  
  // UI操作
  setLoading: (loading: boolean) => void
  setError: (error: string | null) => void
  setTheme: (theme: 'light' | 'dark') => void
  setLanguage: (language: 'zh' | 'en') => void
  
  // AR/VR操作
  startARVRExperience: (productId: number, experienceType: 'AR' | 'VR') => void
  endARVRExperience: () => void
  
  // AI助手操作
  startAIAssistant: () => void
  endAIAssistant: () => void
  addAIMessage: (message: { type: 'user' | 'assistant'; content: string }) => void
  setAITyping: (typing: boolean) => void
  
  // 协作购物操作
  startCollaboration: (sessionId: string) => void
  endCollaboration: () => void
  addParticipant: (participant: { id: number; username: string; avatar?: string; status: 'online' | 'offline' | 'away' }) => void
  removeParticipant: (userId: number) => void
  
  // 推荐操作
  setScenarioRecommendations: (products: Product[]) => void
  setLifestyleRecommendations: (products: Product[]) => void
  setAIRecommendations: (products: Product[]) => void
  addRecommendationHistory: (recommendation: { id: string; type: string; products: Product[] }) => void
  
  // 价值循环操作
  setRecycleOrders: (orders: Array<any>) => void
  addRecycleOrder: (order: any) => void
  updateRecycleOrderStatus: (orderId: number, status: string) => void
  setEcoActivities: (activities: Array<any>) => void
  joinEcoActivity: (activityId: number) => void
  setRecycleStats: (stats: any) => void

  // 愿望清单操作
  setWishlist: (items: Array<any>) => void
  addToWishlist: (item: any) => void
  removeFromWishlist: (productId: number) => void
  updateWishlistItem: (productId: number, updates: any) => void

  // 商品对比操作
  setComparisons: (comparisons: Array<any>) => void
  addComparison: (comparison: any) => void
  removeComparison: (comparisonId: number) => void
  updateComparison: (comparisonId: number, updates: any) => void

  // 退货操作
  setReturnOrders: (orders: Array<any>) => void
  addReturnOrder: (order: any) => void
  updateReturnOrderStatus: (returnId: number, status: string) => void

  // 安全操作
  setSecuritySettings: (settings: any) => void
  setLoginHistory: (history: Array<any>) => void
  addLoginHistory: (entry: any) => void
  updateTwoFactorAuth: (enabled: boolean, method?: string) => void

  // 库存通知操作
  setStockNotifications: (notifications: Array<any>) => void
  addStockNotification: (notification: any) => void
  removeStockNotification: (notificationId: number) => void
}

// 计算属性接口
export interface ComputedState {
  // 购物车计算属性
  cartCount: number
  cartTotal: number
  
  // 用户计算属性
  isAdmin: boolean
  isSeller: boolean
  isUser: boolean
  
  // 商品计算属性
  availableProducts: Product[]
  featuredProducts: Product[]
  
  // 推荐计算属性
  allRecommendations: Product[]
  recentRecommendations: Product[]
}

// 创建store
export const useStore = create<AppState & AppActions>()(
  devtools(
    persist(
      subscribeWithSelector(
        immer((set, get) => ({
          // 初始状态
          user: null,
          isAuthenticated: false,
          token: null,
          
          products: [],
          currentProduct: null,
          hotProducts: [],
          
          cart: [],
          cartCount: 0,
          cartTotal: 0,
          
          loading: false,
          error: null,
          theme: 'light',
          language: 'zh',
          
          arvrExperience: {
            isActive: false,
            productId: null,
            experienceType: null
          },
          
          aiAssistant: {
            isActive: false,
            messages: [],
            isTyping: false
          },
          
          collaboration: {
            isActive: false,
            sessionId: null,
            participants: []
          },
          
          recommendations: {
            scenarioRecommendations: [],
            lifestyleRecommendations: [],
            aiRecommendations: [],
            history: []
          },
          
          recycle: {
            orders: [],
            activities: [],
            stats: {
              totalOrders: 0,
              totalValue: 0,
              sustainabilityScore: 0,
              ecoLevel: 'Bronze'
            }
          },

          wishlist: [],
          comparisons: [],
          returnOrders: [],
          securitySettings: {
            loginHistory: [],
            twoFactorAuth: {
              isEnabled: false
            },
            accountSecurity: {
              passwordStrength: 'MEDIUM',
              enableLoginAlerts: false,
              enableTransactionAlerts: false
            }
          },
          stockNotifications: [],
          loginHistory: [],
          
          // 用户操作
          setUser: (user) => set((state) => {
            state.user = user
            state.isAuthenticated = !!user
          }),
          
          setToken: (token) => set((state) => {
            state.token = token
            if (token) {
              localStorage.setItem('token', token)
            } else {
              localStorage.removeItem('token')
            }
          }),
          
          logout: () => set((state) => {
            state.user = null
            state.isAuthenticated = false
            state.token = null
            state.cart = []
            state.cartCount = 0
            state.cartTotal = 0
            localStorage.removeItem('token')
          }),
          
          // 商品操作
          setProducts: (products) => set((state) => {
            state.products = products
          }),
          
          setCurrentProduct: (product) => set((state) => {
            state.currentProduct = product
          }),
          
          setHotProducts: (products) => set((state) => {
            state.hotProducts = products
          }),
          
          // 购物车操作
          addToCart: (product, quantity = 1) => set((state) => {
            const existingItem = state.cart.find(item => item.product.id === product.id)
            if (existingItem) {
              existingItem.quantity += quantity
            } else {
              state.cart.push({
                id: Date.now(),
                product,
                quantity,
                addedAt: new Date().toISOString()
              })
            }
            
            // 更新计算属性
            state.cartCount = state.cart.reduce((sum, item) => sum + item.quantity, 0)
            state.cartTotal = state.cart.reduce((sum, item) => sum + (item.product.price * item.quantity), 0)
          }),
          
          removeFromCart: (productId) => set((state) => {
            state.cart = state.cart.filter(item => item.product.id !== productId)
            
            // 更新计算属性
            state.cartCount = state.cart.reduce((sum, item) => sum + item.quantity, 0)
            state.cartTotal = state.cart.reduce((sum, item) => sum + (item.product.price * item.quantity), 0)
          }),
          
          updateCartQuantity: (productId, quantity) => set((state) => {
            const item = state.cart.find(item => item.product.id === productId)
            if (item) {
              if (quantity <= 0) {
                state.cart = state.cart.filter(item => item.product.id !== productId)
              } else {
                item.quantity = quantity
              }
            }
            
            // 更新计算属性
            state.cartCount = state.cart.reduce((sum, item) => sum + item.quantity, 0)
            state.cartTotal = state.cart.reduce((sum, item) => sum + (item.product.price * item.quantity), 0)
          }),
          
          clearCart: () => set((state) => {
            state.cart = []
            state.cartCount = 0
            state.cartTotal = 0
          }),
          
          // UI操作
          setLoading: (loading) => set((state) => {
            state.loading = loading
          }),
          
          setError: (error) => set((state) => {
            state.error = error
          }),
          
          setTheme: (theme) => set((state) => {
            state.theme = theme
          }),
          
          setLanguage: (language) => set((state) => {
            state.language = language
          }),
          
          // AR/VR操作
          startARVRExperience: (productId, experienceType) => set((state) => {
            state.arvrExperience = {
              isActive: true,
              productId,
              experienceType
            }
          }),
          
          endARVRExperience: () => set((state) => {
            state.arvrExperience = {
              isActive: false,
              productId: null,
              experienceType: null
            }
          }),
          
          // AI助手操作
          startAIAssistant: () => set((state) => {
            state.aiAssistant.isActive = true
          }),
          
          endAIAssistant: () => set((state) => {
            state.aiAssistant.isActive = false
            state.aiAssistant.messages = []
            state.aiAssistant.isTyping = false
          }),
          
          addAIMessage: (message) => set((state) => {
            state.aiAssistant.messages.push({
              id: Date.now().toString(),
              ...message,
              timestamp: new Date().toISOString()
            })
          }),
          
          setAITyping: (typing) => set((state) => {
            state.aiAssistant.isTyping = typing
          }),
          
          // 协作购物操作
          startCollaboration: (sessionId) => set((state) => {
            state.collaboration.isActive = true
            state.collaboration.sessionId = sessionId
          }),
          
          endCollaboration: () => set((state) => {
            state.collaboration.isActive = false
            state.collaboration.sessionId = null
            state.collaboration.participants = []
          }),
          
          addParticipant: (participant) => set((state) => {
            const existingIndex = state.collaboration.participants.findIndex(p => p.id === participant.id)
            if (existingIndex >= 0) {
              state.collaboration.participants[existingIndex] = participant
            } else {
              state.collaboration.participants.push(participant)
            }
          }),
          
          removeParticipant: (userId) => set((state) => {
            state.collaboration.participants = state.collaboration.participants.filter(p => p.id !== userId)
          }),
          
          // 推荐操作
          setScenarioRecommendations: (products) => set((state) => {
            state.recommendations.scenarioRecommendations = products
          }),
          
          setLifestyleRecommendations: (products) => set((state) => {
            state.recommendations.lifestyleRecommendations = products
          }),
          
          setAIRecommendations: (products) => set((state) => {
            state.recommendations.aiRecommendations = products
          }),
          
          addRecommendationHistory: (recommendation) => set((state) => {
            state.recommendations.history.unshift({
              ...recommendation,
              timestamp: new Date().toISOString()
            })
            
            // 只保留最近10条记录
            if (state.recommendations.history.length > 10) {
              state.recommendations.history = state.recommendations.history.slice(0, 10)
            }
          }),
          
          // 价值循环操作
          setRecycleOrders: (orders) => set((state) => {
            state.recycle.orders = orders
          }),
          
          addRecycleOrder: (order) => set((state) => {
            state.recycle.orders.unshift(order)
          }),
          
          updateRecycleOrderStatus: (orderId, status) => set((state) => {
            const order = state.recycle.orders.find(o => o.id === orderId)
            if (order) {
              order.status = status
            }
          }),
          
          setEcoActivities: (activities) => set((state) => {
            state.recycle.activities = activities
          }),
          
          joinEcoActivity: (activityId) => set((state) => {
            const activity = state.recycle.activities.find(a => a.id === activityId)
            if (activity) {
              activity.participants += 1
            }
          }),
          
          setRecycleStats: (stats) => set((state) => {
            state.recycle.stats = stats
          }),

          // 愿望清单操作
          setWishlist: (items) => set((state) => {
            state.wishlist = items
          }),

          addToWishlist: (item) => set((state) => {
            const existingIndex = state.wishlist.findIndex(w => w.productId === item.productId)
            if (existingIndex >= 0) {
              state.wishlist[existingIndex] = item
            } else {
              state.wishlist.push(item)
            }
          }),

          removeFromWishlist: (productId) => set((state) => {
            state.wishlist = state.wishlist.filter(w => w.productId !== productId)
          }),

          updateWishlistItem: (productId, updates) => set((state) => {
            const item = state.wishlist.find(w => w.productId === productId)
            if (item) {
              Object.assign(item, updates)
            }
          }),

          // 商品对比操作
          setComparisons: (comparisons) => set((state) => {
            state.comparisons = comparisons
          }),

          addComparison: (comparison) => set((state) => {
            state.comparisons.push(comparison)
          }),

          removeComparison: (comparisonId) => set((state) => {
            state.comparisons = state.comparisons.filter(c => c.id !== comparisonId)
          }),

          updateComparison: (comparisonId, updates) => set((state) => {
            const comparison = state.comparisons.find(c => c.id === comparisonId)
            if (comparison) {
              Object.assign(comparison, updates)
            }
          }),

          // 退货操作
          setReturnOrders: (orders) => set((state) => {
            state.returnOrders = orders
          }),

          addReturnOrder: (order) => set((state) => {
            state.returnOrders.unshift(order)
          }),

          updateReturnOrderStatus: (returnId, status) => set((state) => {
            const order = state.returnOrders.find(o => o.id === returnId)
            if (order) {
              order.status = status
            }
          }),

          // 安全操作
          setSecuritySettings: (settings) => set((state) => {
            state.securitySettings = { ...state.securitySettings, ...settings }
          }),

          setLoginHistory: (history) => set((state) => {
            state.loginHistory = history
            state.securitySettings.loginHistory = history
          }),

          addLoginHistory: (entry) => set((state) => {
            state.loginHistory.unshift(entry)
            state.securitySettings.loginHistory.unshift(entry)
            // 只保留最近50条
            if (state.loginHistory.length > 50) {
              state.loginHistory = state.loginHistory.slice(0, 50)
              state.securitySettings.loginHistory = state.securitySettings.loginHistory.slice(0, 50)
            }
          }),

          updateTwoFactorAuth: (enabled, method) => set((state) => {
            state.securitySettings.twoFactorAuth = {
              isEnabled: enabled,
              authMethod: method
            }
          }),

          // 库存通知操作
          setStockNotifications: (notifications) => set((state) => {
            state.stockNotifications = notifications
          }),

          addStockNotification: (notification) => set((state) => {
            const existingIndex = state.stockNotifications.findIndex(n => n.productId === notification.productId)
            if (existingIndex >= 0) {
              state.stockNotifications[existingIndex] = notification
            } else {
              state.stockNotifications.push(notification)
            }
          }),

          removeStockNotification: (notificationId) => set((state) => {
            state.stockNotifications = state.stockNotifications.filter(n => n.id !== notificationId)
          })
        }))
      ),
      {
        name: 'shopx-store',
        partialize: (state) => ({
          user: state.user,
          token: state.token,
          theme: state.theme,
          language: state.language,
          cart: state.cart
        })
      }
    ),
    {
      name: 'shopx-store'
    }
  )
)

// 计算属性选择器
export const useComputedStore = () => {
  const store = useStore()
  
  return {
    // 购物车计算属性
    cartCount: store.cart.reduce((sum, item) => sum + item.quantity, 0),
    cartTotal: store.cart.reduce((sum, item) => sum + (item.product.price * item.quantity), 0),
    
    // 用户计算属性
    isAdmin: store.user?.role === 'ADMIN',
    isSeller: store.user?.role === 'SELLER',
    isUser: store.user?.role === 'USER',
    
    // 商品计算属性
    availableProducts: store.products.filter(p => p.status === 'ACTIVE' && p.stock > 0),
    featuredProducts: store.products.filter(p => p.viewCount > 100),
    
    // 推荐计算属性
    allRecommendations: [
      ...store.recommendations.scenarioRecommendations,
      ...store.recommendations.lifestyleRecommendations,
      ...store.recommendations.aiRecommendations
    ],
    recentRecommendations: store.recommendations.history.slice(0, 5).flatMap(h => h.products)
  }
}