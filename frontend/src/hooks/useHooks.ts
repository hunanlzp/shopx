// 简化的自定义Hooks
import { useState, useEffect, useCallback } from 'react'

// API相关Hooks
export const useProducts = (page = 1, size = 20) => {
  const [products, setProducts] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  const fetchProducts = useCallback(async () => {
    setLoading(true)
    try {
      // 模拟API调用
      const response = await fetch(`/api/products?page=${page}&size=${size}`)
      const data = await response.json()
      setProducts(data.data.list || [])
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }, [page, size])

  useEffect(() => {
    fetchProducts()
  }, [fetchProducts])

  return { products, loading, error, refetch: fetchProducts }
}

export const useAuth = () => {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(false)

  const login = useCallback(async (username: string, password: string) => {
    setLoading(true)
    try {
      const response = await fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password })
      })
      const data = await response.json()
      if (data.code === 200) {
        setUser(data.data.user)
        localStorage.setItem('token', data.data.token)
      }
      return data
    } finally {
      setLoading(false)
    }
  }, [])

  const logout = useCallback(() => {
    setUser(null)
    localStorage.removeItem('token')
  }, [])

  return { user, loading, login, logout }
}

// 业务逻辑Hooks
export const useCart = () => {
  const [cart, setCart] = useState([])
  const [cartCount, setCartCount] = useState(0)

  const addToCart = useCallback((product: any, quantity = 1) => {
    setCart(prev => {
      const existingItem = prev.find(item => item.product.id === product.id)
      if (existingItem) {
        existingItem.quantity += quantity
        return [...prev]
      } else {
        return [...prev, { product, quantity, addedAt: new Date().toISOString() }]
      }
    })
  }, [])

  const removeFromCart = useCallback((productId: number) => {
    setCart(prev => prev.filter(item => item.product.id !== productId))
  }, [])

  const updateQuantity = useCallback((productId: number, quantity: number) => {
    setCart(prev => prev.map(item => 
      item.product.id === productId ? { ...item, quantity } : item
    ))
  }, [])

  const clearCart = useCallback(() => {
    setCart([])
  }, [])

  useEffect(() => {
    setCartCount(cart.reduce((sum, item) => sum + item.quantity, 0))
  }, [cart])

  return { cart, cartCount, addToCart, removeFromCart, updateQuantity, clearCart }
}

export const useSearch = () => {
  const [keyword, setKeyword] = useState('')
  const [results, setResults] = useState([])
  const [loading, setLoading] = useState(false)

  const search = useCallback(async (query: string) => {
    if (!query.trim()) return
    
    setLoading(true)
    try {
      const response = await fetch(`/api/products/search?keyword=${encodeURIComponent(query)}`)
      const data = await response.json()
      setResults(data.data.list || [])
    } catch (error) {
      console.error('Search error:', error)
    } finally {
      setLoading(false)
    }
  }, [])

  return { keyword, setKeyword, results, loading, search }
}

export const usePagination = (total: number, pageSize = 20) => {
  const [current, setCurrent] = useState(1)
  const [pageSize, setPageSize] = useState(pageSize)

  const totalPages = Math.ceil(total / pageSize)

  const goToPage = useCallback((page: number) => {
    if (page >= 1 && page <= totalPages) {
      setCurrent(page)
    }
  }, [totalPages])

  const nextPage = useCallback(() => {
    goToPage(current + 1)
  }, [current, goToPage])

  const prevPage = useCallback(() => {
    goToPage(current - 1)
  }, [current, goToPage])

  return {
    current,
    pageSize,
    totalPages,
    goToPage,
    nextPage,
    prevPage,
    setPageSize
  }
}

// 工具Hooks
export const useDebounce = (value: any, delay: number) => {
  const [debouncedValue, setDebouncedValue] = useState(value)

  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedValue(value)
    }, delay)

    return () => {
      clearTimeout(handler)
    }
  }, [value, delay])

  return debouncedValue
}

export const useThrottle = (value: any, delay: number) => {
  const [throttledValue, setThrottledValue] = useState(value)
  const [lastExecuted, setLastExecuted] = useState(Date.now())

  useEffect(() => {
    if (Date.now() >= lastExecuted + delay) {
      setThrottledValue(value)
      setLastExecuted(Date.now())
    } else {
      const timer = setTimeout(() => {
        setThrottledValue(value)
        setLastExecuted(Date.now())
      }, delay - (Date.now() - lastExecuted))

      return () => clearTimeout(timer)
    }
  }, [value, delay, lastExecuted])

  return throttledValue
}

export const useLocalStorage = (key: string, initialValue: any) => {
  const [storedValue, setStoredValue] = useState(() => {
    try {
      const item = window.localStorage.getItem(key)
      return item ? JSON.parse(item) : initialValue
    } catch (error) {
      return initialValue
    }
  })

  const setValue = useCallback((value: any) => {
    try {
      setStoredValue(value)
      window.localStorage.setItem(key, JSON.stringify(value))
    } catch (error) {
      console.error('Error saving to localStorage:', error)
    }
  }, [key])

  return [storedValue, setValue]
}

export const useSessionStorage = (key: string, initialValue: any) => {
  const [storedValue, setStoredValue] = useState(() => {
    try {
      const item = window.sessionStorage.getItem(key)
      return item ? JSON.parse(item) : initialValue
    } catch (error) {
      return initialValue
    }
  })

  const setValue = useCallback((value: any) => {
    try {
      setStoredValue(value)
      window.sessionStorage.setItem(key, JSON.stringify(value))
    } catch (error) {
      console.error('Error saving to sessionStorage:', error)
    }
  }, [key])

  return [storedValue, setValue]
}

// 设备检测Hooks
export const useDevice = () => {
  const [device, setDevice] = useState({
    isMobile: false,
    isTablet: false,
    isDesktop: false,
    userAgent: ''
  })

  useEffect(() => {
    const userAgent = navigator.userAgent
    const isMobile = /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(userAgent)
    const isTablet = /iPad|Android(?=.*\bMobile\b)/i.test(userAgent)
    const isDesktop = !isMobile && !isTablet

    setDevice({
      isMobile,
      isTablet,
      isDesktop,
      userAgent
    })
  }, [])

  return device
}

// 网络状态Hooks
export const useNetworkStatus = () => {
  const [isOnline, setIsOnline] = useState(navigator.onLine)

  useEffect(() => {
    const handleOnline = () => setIsOnline(true)
    const handleOffline = () => setIsOnline(false)

    window.addEventListener('online', handleOnline)
    window.addEventListener('offline', handleOffline)

    return () => {
      window.removeEventListener('online', handleOnline)
      window.removeEventListener('offline', handleOffline)
    }
  }, [])

  return isOnline
}

// 窗口尺寸Hooks
export const useWindowSize = () => {
  const [windowSize, setWindowSize] = useState({
    width: window.innerWidth,
    height: window.innerHeight
  })

  useEffect(() => {
    const handleResize = () => {
      setWindowSize({
        width: window.innerWidth,
        height: window.innerHeight
      })
    }

    window.addEventListener('resize', handleResize)
    return () => window.removeEventListener('resize', handleResize)
  }, [])

  return windowSize
}

// 滚动位置Hooks
export const useScrollPosition = () => {
  const [scrollPosition, setScrollPosition] = useState({
    x: window.pageXOffset,
    y: window.pageYOffset
  })

  useEffect(() => {
    const handleScroll = () => {
      setScrollPosition({
        x: window.pageXOffset,
        y: window.pageYOffset
      })
    }

    window.addEventListener('scroll', handleScroll)
    return () => window.removeEventListener('scroll', handleScroll)
  }, [])

  return scrollPosition
}

// 点击外部Hooks
export const useClickOutside = (ref: any, callback: () => void) => {
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (ref.current && !ref.current.contains(event.target)) {
        callback()
      }
    }

    document.addEventListener('mousedown', handleClickOutside)
    return () => document.removeEventListener('mousedown', handleClickOutside)
  }, [ref, callback])
}

// 键盘事件Hooks
export const useKeyPress = (targetKey: string, callback: () => void) => {
  useEffect(() => {
    const handleKeyPress = (event: KeyboardEvent) => {
      if (event.key === targetKey) {
        callback()
      }
    }

    window.addEventListener('keydown', handleKeyPress)
    return () => window.removeEventListener('keydown', handleKeyPress)
  }, [targetKey, callback])
}

// 复制到剪贴板Hooks
export const useClipboard = () => {
  const [copied, setCopied] = useState(false)

  const copyToClipboard = useCallback(async (text: string) => {
    try {
      await navigator.clipboard.writeText(text)
      setCopied(true)
      setTimeout(() => setCopied(false), 2000)
    } catch (error) {
      console.error('Failed to copy to clipboard:', error)
    }
  }, [])

  return { copied, copyToClipboard }
}