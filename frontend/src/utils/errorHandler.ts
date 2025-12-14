import { message, notification } from 'antd'
import { history } from './history'

/**
 * API错误处理工具
 */
export class ApiErrorHandler {
  
  /**
   * 处理API错误
   */
  static handleError(error: any, defaultMessage: string = '操作失败') {
    console.error('API Error:', error)
    
    if (error.response) {
      // 服务器响应错误
      const { status, data } = error.response
      
      switch (status) {
        case 400:
          this.handleBadRequest(data)
          break
        case 401:
          this.handleUnauthorized(data)
          break
        case 403:
          this.handleForbidden(data)
          break
        case 404:
          this.handleNotFound(data)
          break
        case 422:
          this.handleValidationError(data)
          break
        case 500:
          this.handleServerError(data)
          break
        default:
          this.handleDefaultError(data, defaultMessage)
      }
    } else if (error.request) {
      // 网络错误
      this.handleNetworkError()
    } else {
      // 其他错误
      this.handleDefaultError(null, error.message || defaultMessage)
    }
  }
  
  /**
   * 处理400错误（参数错误）
   */
  private static handleBadRequest(data: any) {
    const errorMessage = data?.message || '请求参数错误'
    
    if (data?.data && typeof data.data === 'object') {
      // 显示字段验证错误
      const fieldErrors = Object.entries(data.data)
        .map(([field, message]) => `${field}: ${message}`)
        .join('\n')
      
      notification.error({
        message: '参数验证失败',
        description: fieldErrors,
        duration: 5
      })
    } else {
      message.error(errorMessage)
    }
  }
  
  /**
   * 处理401错误（未授权）
   */
  private static handleUnauthorized(data: any) {
    const errorMessage = data?.message || '登录已过期，请重新登录'
    
    // 清除本地存储
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    
    notification.warning({
      message: '登录过期',
      description: errorMessage,
      duration: 3,
      onClose: () => {
        // 跳转到登录页面
        window.location.href = '/auth'
      }
    })
  }
  
  /**
   * 处理403错误（权限不足）
   */
  private static handleForbidden(data: any) {
    const errorMessage = data?.message || '权限不足'
    
    notification.error({
      message: '权限不足',
      description: errorMessage,
      duration: 3
    })
  }
  
  /**
   * 处理404错误（资源不存在）
   */
  private static handleNotFound(data: any) {
    const errorMessage = data?.message || '请求的资源不存在'
    
    message.error(errorMessage)
  }
  
  /**
   * 处理422错误（验证错误）
   */
  private static handleValidationError(data: any) {
    const errorMessage = data?.message || '数据验证失败'
    
    if (data?.data && typeof data.data === 'object') {
      const fieldErrors = Object.entries(data.data)
        .map(([field, message]) => `${field}: ${message}`)
        .join('\n')
      
      notification.error({
        message: '数据验证失败',
        description: fieldErrors,
        duration: 5
      })
    } else {
      message.error(errorMessage)
    }
  }
  
  /**
   * 处理500错误（服务器错误）
   */
  private static handleServerError(data: any) {
    const errorMessage = data?.message || '服务器内部错误，请稍后重试'
    
    notification.error({
      message: '服务器错误',
      description: errorMessage,
      duration: 5
    })
  }
  
  /**
   * 处理网络错误
   */
  private static handleNetworkError() {
    notification.error({
      message: '网络错误',
      description: '网络连接失败，请检查网络设置后重试',
      duration: 5
    })
  }
  
  /**
   * 处理默认错误
   */
  private static handleDefaultError(data: any, defaultMessage: string) {
    const errorMessage = data?.message || defaultMessage
    
    message.error(errorMessage)
  }
  
  /**
   * 处理成功响应
   */
  static handleSuccess(data: any, successMessage?: string) {
    const message = data?.message || successMessage
    
    if (message) {
      notification.success({
        message: '操作成功',
        description: message,
        duration: 3
      })
    }
  }
  
  /**
   * 处理警告响应
   */
  static handleWarning(data: any, warningMessage?: string) {
    const message = data?.message || warningMessage
    
    if (message) {
      notification.warning({
        message: '警告',
        description: message,
        duration: 3
      })
    }
  }
  
  /**
   * 处理信息响应
   */
  static handleInfo(data: any, infoMessage?: string) {
    const message = data?.message || infoMessage
    
    if (message) {
      notification.info({
        message: '提示',
        description: message,
        duration: 3
      })
    }
  }
}

/**
 * API请求拦截器
 */
export const setupApiInterceptors = (axiosInstance: any) => {
  // 请求拦截器
  axiosInstance.interceptors.request.use(
    (config: any) => {
      // 添加认证token
      const token = localStorage.getItem('token')
      if (token) {
        config.headers.Authorization = `Bearer ${token}`
      }
      
      // 添加请求时间戳
      config.metadata = { startTime: new Date() }
      
      return config
    },
    (error: any) => {
      console.error('Request interceptor error:', error)
      return Promise.reject(error)
    }
  )
  
  // 响应拦截器
  axiosInstance.interceptors.response.use(
    (response: any) => {
      // 计算请求耗时
      const endTime = new Date()
      const duration = endTime.getTime() - response.config.metadata.startTime.getTime()
      console.log(`API Request ${response.config.url} took ${duration}ms`)
      
      // 处理成功响应
      if (response.data?.code === 200) {
        return response
      } else {
        // 处理业务错误
        const error = new Error(response.data?.message || '请求失败')
        ;(error as any).response = response
        return Promise.reject(error)
      }
    },
    (error: any) => {
      // 处理HTTP错误
      ApiErrorHandler.handleError(error)
      return Promise.reject(error)
    }
  )
}

/**
 * 重试机制
 */
export class RetryHandler {
  private static readonly MAX_RETRIES = 3
  private static readonly RETRY_DELAY = 1000
  
  static async retryRequest<T>(
    requestFn: () => Promise<T>,
    maxRetries: number = this.MAX_RETRIES,
    delay: number = this.RETRY_DELAY
  ): Promise<T> {
    let lastError: any
    
    for (let i = 0; i <= maxRetries; i++) {
      try {
        return await requestFn()
      } catch (error) {
        lastError = error
        
        // 如果是最后一次重试，直接抛出错误
        if (i === maxRetries) {
          break
        }
        
        // 等待后重试
        await this.delay(delay * Math.pow(2, i))
      }
    }
    
    throw lastError
  }
  
  private static delay(ms: number): Promise<void> {
    return new Promise(resolve => setTimeout(resolve, ms))
  }
}

/**
 * 错误恢复策略
 */
export class ErrorRecovery {
  /**
   * 检查是否需要重新登录
   */
  static checkAuthError(error: any): boolean {
    return error.response?.status === 401
  }
  
  /**
   * 检查是否是网络错误
   */
  static checkNetworkError(error: any): boolean {
    return !error.response && error.request
  }
  
  /**
   * 检查是否是服务器错误
   */
  static checkServerError(error: any): boolean {
    return error.response?.status >= 500
  }
  
  /**
   * 获取错误恢复建议
   */
  static getRecoverySuggestion(error: any): string {
    if (this.checkAuthError(error)) {
      return '请重新登录'
    }
    
    if (this.checkNetworkError(error)) {
      return '请检查网络连接'
    }
    
    if (this.checkServerError(error)) {
      return '请稍后重试'
    }
    
    return '请稍后重试'
  }
}
