import React, { useState, useEffect } from 'react'
import { Card, Button, Tag, message, Spin, Alert, Popconfirm } from 'antd'
import { CheckCircleOutlined, CloseCircleOutlined, WarningOutlined, ReloadOutlined } from '@ant-design/icons'
import ApiService from '../services/api'
import { CartItemStatus } from '../types'

// 购物车状态检查器组件
export const CartStatusChecker: React.FC<{
  cartItemIds: number[]
  onStatusChange?: (statuses: CartItemStatus[]) => void
  autoCheck?: boolean
  checkInterval?: number
}> = ({ cartItemIds, onStatusChange, autoCheck = false, checkInterval = 30000 }) => {
  const [loading, setLoading] = useState(false)
  const [statuses, setStatuses] = useState<CartItemStatus[]>([])

  useEffect(() => {
    if (cartItemIds.length > 0) {
      checkStatus()
      if (autoCheck) {
        const interval = setInterval(checkStatus, checkInterval)
        return () => clearInterval(interval)
      }
    }
  }, [cartItemIds.join(','), autoCheck, checkInterval])

  const checkStatus = async () => {
    try {
      setLoading(true)
      const response = await ApiService.checkCartItemStatus(cartItemIds)
      if (response.code === 200) {
        setStatuses(response.data || [])
        onStatusChange?.(response.data || [])
      }
    } catch (error: any) {
      message.error(error.message || '检查购物车状态失败')
    } finally {
      setLoading(false)
    }
  }

  if (statuses.length === 0) {
    return null
  }

  const hasIssues = statuses.some(s => s.status !== 'AVAILABLE')

  return (
    <div>
      {hasIssues && (
        <Alert
          message="购物车商品状态异常"
          description={
            <div>
              {statuses
                .filter(s => s.status !== 'AVAILABLE')
                .map(status => (
                  <div key={status.cartItemId} style={{ marginTop: 8 }}>
                    <strong>{status.productName}:</strong> {status.message}
                    {status.status === 'PRICE_CHANGED' && status.currentPrice && (
                      <span> 当前价格: ¥{status.currentPrice.toFixed(2)}</span>
                    )}
                    {status.status === 'OUT_OF_STOCK' && (
                      <span> 当前库存: {status.stock}</span>
                    )}
                  </div>
                ))}
            </div>
          }
          type="warning"
          showIcon
          action={
            <Button size="small" icon={<ReloadOutlined />} onClick={checkStatus} loading={loading}>
              刷新
            </Button>
          }
          style={{ marginBottom: 16 }}
        />
      )}
    </div>
  )
}

// 购物车商品卡片组件（增强版）
export const CartItemCard: React.FC<{
  item: {
    id: number
    product: any
    quantity: number
    status?: CartItemStatus
  }
  onQuantityChange?: (productId: number, quantity: number) => void
  onRemove?: (productId: number) => void
}> = ({ item, onQuantityChange, onRemove }) => {
  const [quantity, setQuantity] = useState(item.quantity)
  const [removing, setRemoving] = useState(false)

  const handleQuantityChange = (newQuantity: number) => {
    if (newQuantity < 1) return
    setQuantity(newQuantity)
    onQuantityChange?.(item.product.id, newQuantity)
  }

  const handleRemove = async () => {
    try {
      setRemoving(true)
      await ApiService.removeFromCart(item.product.id)
      onRemove?.(item.product.id)
      message.success('已从购物车移除')
    } catch (error: any) {
      message.error(error.message || '移除失败')
    } finally {
      setRemoving(false)
    }
  }

  const getStatusTag = () => {
    if (!item.status) return null

    const statusMap: Record<string, { color: string; icon: React.ReactNode; text: string }> = {
      AVAILABLE: { color: 'success', icon: <CheckCircleOutlined />, text: '可用' },
      OUT_OF_STOCK: { color: 'error', icon: <CloseCircleOutlined />, text: '缺货' },
      PRICE_CHANGED: { color: 'warning', icon: <WarningOutlined />, text: '价格已变动' },
      UNAVAILABLE: { color: 'error', icon: <CloseCircleOutlined />, text: '不可用' },
    }

    const statusInfo = statusMap[item.status.status] || statusMap.AVAILABLE
    return (
      <Tag color={statusInfo.color} icon={statusInfo.icon}>
        {statusInfo.text}
      </Tag>
    )
  }

  return (
    <Card
      style={{ marginBottom: 16 }}
      actions={[
        <Popconfirm
          title="确定要移除这个商品吗？"
          onConfirm={handleRemove}
          okText="确定"
          cancelText="取消"
        >
          <Button type="link" danger loading={removing}>
            移除
          </Button>
        </Popconfirm>,
      ]}
    >
      <div style={{ display: 'flex', gap: 16 }}>
        <img
          src={item.product.image || '/placeholder.png'}
          alt={item.product.name}
          style={{ width: 100, height: 100, objectFit: 'cover', borderRadius: 4 }}
        />
        <div style={{ flex: 1 }}>
          <h3>{item.product.name}</h3>
          <div style={{ marginTop: 8 }}>
            {getStatusTag()}
          </div>
          <div style={{ marginTop: 16, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <span style={{ fontSize: 18, fontWeight: 'bold', color: '#1890ff' }}>
                ¥{item.product.price.toFixed(2)}
              </span>
              {item.status?.status === 'PRICE_CHANGED' && item.status.currentPrice && (
                <span style={{ marginLeft: 8, textDecoration: 'line-through', color: '#999' }}>
                  ¥{item.status.originalPrice?.toFixed(2)}
                </span>
              )}
            </div>
            <div>
              <Button.Group>
                <Button onClick={() => handleQuantityChange(quantity - 1)} disabled={quantity <= 1}>-</Button>
                <span style={{ padding: '0 16px', minWidth: 50, textAlign: 'center', display: 'inline-block' }}>
                  {quantity}
                </span>
                <Button onClick={() => handleQuantityChange(quantity + 1)}>+</Button>
              </Button.Group>
            </div>
          </div>
          {item.status?.message && (
            <Alert
              message={item.status.message}
              type="warning"
              showIcon
              style={{ marginTop: 8 }}
            />
          )}
        </div>
      </div>
    </Card>
  )
}

// 游客购物车管理组件
export const GuestCartManager: React.FC<{
  sessionId: string
  onCheckout?: () => void
}> = ({ sessionId, onCheckout }) => {
  const [cart, setCart] = useState<any[]>([])

  useEffect(() => {
    // 从localStorage加载游客购物车
    const savedCart = localStorage.getItem(`guest_cart_${sessionId}`)
    if (savedCart) {
      try {
        setCart(JSON.parse(savedCart))
      } catch (e) {
        console.error('Failed to parse guest cart', e)
      }
    }
  }, [sessionId])

  const saveCart = (newCart: any[]) => {
    setCart(newCart)
    localStorage.setItem(`guest_cart_${sessionId}`, JSON.stringify(newCart))
  }

  const addToCart = (product: any, quantity: number = 1) => {
    const existingIndex = cart.findIndex(item => item.product.id === product.id)
    const newCart = [...cart]
    
    if (existingIndex >= 0) {
      newCart[existingIndex].quantity += quantity
    } else {
      newCart.push({ product, quantity, addedAt: new Date().toISOString() })
    }
    
    saveCart(newCart)
    message.success('已添加到购物车')
  }

  const removeFromCart = (productId: number) => {
    const newCart = cart.filter(item => item.product.id !== productId)
    saveCart(newCart)
    message.success('已从购物车移除')
  }

  const updateQuantity = (productId: number, quantity: number) => {
    if (quantity <= 0) {
      removeFromCart(productId)
      return
    }
    
    const newCart = cart.map(item =>
      item.product.id === productId ? { ...item, quantity } : item
    )
    saveCart(newCart)
  }

  return {
    cart,
    addToCart,
    removeFromCart,
    updateQuantity,
    clearCart: () => saveCart([]),
    getCartCount: () => cart.reduce((sum, item) => sum + item.quantity, 0),
    getCartTotal: () => cart.reduce((sum, item) => sum + item.product.price * item.quantity, 0),
  }
}

