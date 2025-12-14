import SockJS from 'sockjs-client'
import { Stomp } from '@stomp/stompjs'

export interface WebSocketMessage {
  type: string
  sessionId: string
  userId: number
  username: string
  message?: string
  annotation?: any
  productId?: number
  experienceType?: string
  experienceData?: any
  timestamp: string
}

export interface ChatMessage {
  userId: number
  username: string
  message: string
  timestamp: string
}

export interface AnnotationMessage {
  userId: number
  username: string
  annotation: {
    type: string
    content: string
    position: { x: number; y: number }
  }
  timestamp: string
}

export interface ProductChangeMessage {
  userId: number
  username: string
  productId: number
  timestamp: string
}

export interface ExperienceMessage {
  userId: number
  username: string
  experienceType: string
  experienceData: any
  timestamp: string
}

class WebSocketService {
  private stompClient: any = null
  private isConnected = false
  private sessionId: string | null = null
  private userId: number | null = null
  private username: string | null = null

  // 消息回调函数
  private onChatMessage: ((message: ChatMessage) => void) | null = null
  private onUserJoin: ((message: any) => void) | null = null
  private onUserLeave: ((message: any) => void) | null = null
  private onAnnotation: ((message: AnnotationMessage) => void) | null = null
  private onProductChange: ((message: ProductChangeMessage) => void) | null = null
  private onExperience: ((message: ExperienceMessage) => void) | null = null

  /**
   * 连接到WebSocket服务器
   */
  connect(sessionId: string, userId: number, username: string): Promise<void> {
    return new Promise((resolve, reject) => {
      try {
        this.sessionId = sessionId
        this.userId = userId
        this.username = username

        const socket = new SockJS('/api/ws')
        this.stompClient = Stomp.over(socket)

        this.stompClient.connect(
          {},
          (frame: any) => {
            console.log('WebSocket连接成功:', frame)
            this.isConnected = true
            
            // 订阅各种消息
            this.subscribeToMessages()
            
            // 发送加入会话消息
            this.sendJoinMessage()
            
            resolve()
          },
          (error: any) => {
            console.error('WebSocket连接失败:', error)
            this.isConnected = false
            reject(error)
          }
        )
      } catch (error) {
        console.error('WebSocket连接异常:', error)
        reject(error)
      }
    })
  }

  /**
   * 断开WebSocket连接
   */
  disconnect(): void {
    if (this.stompClient && this.isConnected) {
      // 发送离开会话消息
      this.sendLeaveMessage()
      
      this.stompClient.disconnect()
      this.isConnected = false
      this.sessionId = null
      this.userId = null
      this.username = null
    }
  }

  /**
   * 订阅各种消息
   */
  private subscribeToMessages(): void {
    if (!this.stompClient || !this.sessionId) return

    // 订阅聊天消息
    this.stompClient.subscribe(`/topic/chat/${this.sessionId}`, (message: any) => {
      const data = JSON.parse(message.body)
      if (data.type === 'chat' && this.onChatMessage) {
        this.onChatMessage({
          userId: data.userId,
          username: data.username,
          message: data.message,
          timestamp: data.timestamp
        })
      }
    })

    // 订阅用户加入消息
    this.stompClient.subscribe(`/topic/join/${this.sessionId}`, (message: any) => {
      const data = JSON.parse(message.body)
      if (data.type === 'join' && this.onUserJoin) {
        this.onUserJoin(data)
      }
    })

    // 订阅用户离开消息
    this.stompClient.subscribe(`/topic/leave/${this.sessionId}`, (message: any) => {
      const data = JSON.parse(message.body)
      if (data.type === 'leave' && this.onUserLeave) {
        this.onUserLeave(data)
      }
    })

    // 订阅标注消息
    this.stompClient.subscribe(`/topic/annotation/${this.sessionId}`, (message: any) => {
      const data = JSON.parse(message.body)
      if (data.type === 'annotation' && this.onAnnotation) {
        this.onAnnotation({
          userId: data.userId,
          username: data.username,
          annotation: data.annotation,
          timestamp: data.timestamp
        })
      }
    })

    // 订阅商品切换消息
    this.stompClient.subscribe(`/topic/product-change/${this.sessionId}`, (message: any) => {
      const data = JSON.parse(message.body)
      if (data.type === 'product-change' && this.onProductChange) {
        this.onProductChange({
          userId: data.userId,
          username: data.username,
          productId: data.productId,
          timestamp: data.timestamp
        })
      }
    })

    // 订阅体验消息
    this.stompClient.subscribe(`/topic/experience/${this.sessionId}`, (message: any) => {
      const data = JSON.parse(message.body)
      if (data.type === 'experience' && this.onExperience) {
        this.onExperience({
          userId: data.userId,
          username: data.username,
          experienceType: data.experienceType,
          experienceData: data.experienceData,
          timestamp: data.timestamp
        })
      }
    })
  }

  /**
   * 发送聊天消息
   */
  sendChatMessage(message: string): void {
    if (this.stompClient && this.isConnected && this.sessionId && this.userId && this.username) {
      this.stompClient.send(`/app/chat/${this.sessionId}`, {}, JSON.stringify({
        userId: this.userId,
        username: this.username,
        message: message
      }))
    }
  }

  /**
   * 发送加入会话消息
   */
  private sendJoinMessage(): void {
    if (this.stompClient && this.isConnected && this.sessionId && this.userId && this.username) {
      this.stompClient.send(`/app/join/${this.sessionId}`, {}, JSON.stringify({
        userId: this.userId,
        username: this.username
      }))
    }
  }

  /**
   * 发送离开会话消息
   */
  private sendLeaveMessage(): void {
    if (this.stompClient && this.isConnected && this.sessionId && this.userId && this.username) {
      this.stompClient.send(`/app/leave/${this.sessionId}`, {}, JSON.stringify({
        userId: this.userId,
        username: this.username
      }))
    }
  }

  /**
   * 发送标注消息
   */
  sendAnnotation(annotation: any): void {
    if (this.stompClient && this.isConnected && this.sessionId && this.userId && this.username) {
      this.stompClient.send(`/app/annotation/${this.sessionId}`, {}, JSON.stringify({
        userId: this.userId,
        username: this.username,
        annotation: annotation
      }))
    }
  }

  /**
   * 发送商品切换消息
   */
  sendProductChange(productId: number): void {
    if (this.stompClient && this.isConnected && this.sessionId && this.userId && this.username) {
      this.stompClient.send(`/app/product-change/${this.sessionId}`, {}, JSON.stringify({
        userId: this.userId,
        username: this.username,
        productId: productId
      }))
    }
  }

  /**
   * 发送体验消息
   */
  sendExperience(experienceType: string, experienceData: any): void {
    if (this.stompClient && this.isConnected && this.sessionId && this.userId && this.username) {
      this.stompClient.send(`/app/experience/${this.sessionId}`, {}, JSON.stringify({
        userId: this.userId,
        username: this.username,
        experienceType: experienceType,
        experienceData: experienceData
      }))
    }
  }

  // 设置回调函数
  setOnChatMessage(callback: (message: ChatMessage) => void): void {
    this.onChatMessage = callback
  }

  setOnUserJoin(callback: (message: any) => void): void {
    this.onUserJoin = callback
  }

  setOnUserLeave(callback: (message: any) => void): void {
    this.onUserLeave = callback
  }

  setOnAnnotation(callback: (message: AnnotationMessage) => void): void {
    this.onAnnotation = callback
  }

  setOnProductChange(callback: (message: ProductChangeMessage) => void): void {
    this.onProductChange = callback
  }

  setOnExperience(callback: (message: ExperienceMessage) => void): void {
    this.onExperience = callback
  }

  // 获取连接状态
  getConnectionStatus(): boolean {
    return this.isConnected
  }

  getSessionId(): string | null {
    return this.sessionId
  }

  getUserId(): number | null {
    return this.userId
  }

  getUsername(): string | null {
    return this.username
  }
}

// 创建单例实例
export const webSocketService = new WebSocketService()
export default webSocketService
