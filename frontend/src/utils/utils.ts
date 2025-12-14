// 格式化工具函数
export const formatNumber = {
  // 格式化价格
  price: (price: number): string => {
    return `¥${price.toFixed(2)}`
  },
  
  // 格式化数量
  quantity: (quantity: number): string => {
    if (quantity >= 1000000) {
      return `${(quantity / 1000000).toFixed(1)}M`
    } else if (quantity >= 1000) {
      return `${(quantity / 1000).toFixed(1)}K`
    }
    return quantity.toString()
  },
  
  // 格式化百分比
  percentage: (value: number, decimals: number = 1): string => {
    return `${(value * 100).toFixed(decimals)}%`
  },
  
  // 格式化评分
  rating: (rating: number): string => {
    return rating.toFixed(1)
  }
}

// 时间格式化工具
export const formatTime = {
  // 相对时间
  fromNow: (date: string | Date): string => {
    const now = new Date()
    const target = new Date(date)
    const diff = now.getTime() - target.getTime()
    
    const seconds = Math.floor(diff / 1000)
    const minutes = Math.floor(seconds / 60)
    const hours = Math.floor(minutes / 60)
    const days = Math.floor(hours / 24)
    
    if (days > 0) {
      return `${days}天前`
    } else if (hours > 0) {
      return `${hours}小时前`
    } else if (minutes > 0) {
      return `${minutes}分钟前`
    } else {
      return '刚刚'
    }
  },
  
  // 格式化日期
  date: (date: string | Date, format: string = 'YYYY-MM-DD'): string => {
    const d = new Date(date)
    const year = d.getFullYear()
    const month = String(d.getMonth() + 1).padStart(2, '0')
    const day = String(d.getDate()).padStart(2, '0')
    const hours = String(d.getHours()).padStart(2, '0')
    const minutes = String(d.getMinutes()).padStart(2, '0')
    const seconds = String(d.getSeconds()).padStart(2, '0')
    
    return format
      .replace('YYYY', year.toString())
      .replace('MM', month)
      .replace('DD', day)
      .replace('HH', hours)
      .replace('mm', minutes)
      .replace('ss', seconds)
  }
}

// 字符串工具
export const stringUtils = {
  // 截断字符串
  truncate: (str: string, length: number, suffix: string = '...'): string => {
    if (str.length <= length) return str
    return str.substring(0, length) + suffix
  },
  
  // 首字母大写
  capitalize: (str: string): string => {
    return str.charAt(0).toUpperCase() + str.slice(1)
  },
  
  // 驼峰转换
  camelCase: (str: string): string => {
    return str.replace(/-([a-z])/g, (g) => g[1].toUpperCase())
  },
  
  // 短横线转换
  kebabCase: (str: string): string => {
    return str.replace(/([A-Z])/g, '-$1').toLowerCase()
  }
}

// 数组工具
export const arrayUtils = {
  // 去重
  unique: <T>(arr: T[]): T[] => {
    return [...new Set(arr)]
  },
  
  // 分组
  groupBy: <T>(arr: T[], key: keyof T): Record<string, T[]> => {
    return arr.reduce((groups, item) => {
      const group = String(item[key])
      groups[group] = groups[group] || []
      groups[group].push(item)
      return groups
    }, {} as Record<string, T[]>)
  },
  
  // 排序
  sortBy: <T>(arr: T[], key: keyof T, order: 'asc' | 'desc' = 'asc'): T[] => {
    return [...arr].sort((a, b) => {
      const aVal = a[key]
      const bVal = b[key]
      
      if (aVal < bVal) return order === 'asc' ? -1 : 1
      if (aVal > bVal) return order === 'asc' ? 1 : -1
      return 0
    })
  }
}

// 对象工具
export const objectUtils = {
  // 深拷贝
  deepClone: <T>(obj: T): T => {
    if (obj === null || typeof obj !== 'object') return obj
    if (obj instanceof Date) return new Date(obj.getTime()) as unknown as T
    if (obj instanceof Array) return obj.map(item => objectUtils.deepClone(item)) as unknown as T
    if (typeof obj === 'object') {
      const clonedObj = {} as T
      for (const key in obj) {
        if (obj.hasOwnProperty(key)) {
          clonedObj[key] = objectUtils.deepClone(obj[key])
        }
      }
      return clonedObj
    }
    return obj
  },
  
  // 合并对象
  merge: <T extends Record<string, any>>(target: T, ...sources: Partial<T>[]): T => {
    return sources.reduce((acc, source) => {
      return { ...acc, ...source }
    }, target)
  },
  
  // 过滤空值
  filterEmpty: <T extends Record<string, any>>(obj: T): Partial<T> => {
    const filtered = {} as Partial<T>
    for (const key in obj) {
      if (obj[key] !== null && obj[key] !== undefined && obj[key] !== '') {
        filtered[key] = obj[key]
      }
    }
    return filtered
  }
}

// 设备检测
export const deviceUtils = {
  // 是否为移动设备
  isMobile: (): boolean => {
    return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)
  },
  
  // 是否为平板设备
  isTablet: (): boolean => {
    return /iPad|Android(?=.*\bMobile\b)/i.test(navigator.userAgent)
  },
  
  // 是否为桌面设备
  isDesktop: (): boolean => {
    return !deviceUtils.isMobile() && !deviceUtils.isTablet()
  },
  
  // 获取设备类型
  getDeviceType: (): 'mobile' | 'tablet' | 'desktop' => {
    if (deviceUtils.isMobile()) return 'mobile'
    if (deviceUtils.isTablet()) return 'tablet'
    return 'desktop'
  }
}

// 性能工具
export const performanceUtils = {
  // 防抖
  debounce: <T extends (...args: any[]) => any>(
    func: T,
    wait: number
  ): ((...args: Parameters<T>) => void) => {
    let timeout: NodeJS.Timeout
    return (...args: Parameters<T>) => {
      clearTimeout(timeout)
      timeout = setTimeout(() => func(...args), wait)
    }
  },
  
  // 节流
  throttle: <T extends (...args: any[]) => any>(
    func: T,
    wait: number
  ): ((...args: Parameters<T>) => void) => {
    let lastTime = 0
    return (...args: Parameters<T>) => {
      const now = Date.now()
      if (now - lastTime >= wait) {
        lastTime = now
        func(...args)
      }
    }
  },
  
  // 性能测量
  measure: (name: string, fn: () => void): void => {
    const start = performance.now()
    fn()
    const end = performance.now()
    console.log(`${name}: ${end - start}ms`)
  }
}

// 颜色工具
export const colorUtils = {
  // 十六进制转RGB
  hexToRgb: (hex: string): { r: number; g: number; b: number } | null => {
    const result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex)
    return result ? {
      r: parseInt(result[1], 16),
      g: parseInt(result[2], 16),
      b: parseInt(result[3], 16)
    } : null
  },
  
  // RGB转十六进制
  rgbToHex: (r: number, g: number, b: number): string => {
    return `#${((1 << 24) + (r << 16) + (g << 8) + b).toString(16).slice(1)}`
  },
  
  // 生成随机颜色
  randomColor: (): string => {
    return colorUtils.rgbToHex(
      Math.floor(Math.random() * 256),
      Math.floor(Math.random() * 256),
      Math.floor(Math.random() * 256)
    )
  }
}