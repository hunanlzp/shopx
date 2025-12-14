import React, { useState, useEffect } from 'react'
import { Card, Button, Tag, message, Spin, Empty, List, Modal, InputNumber } from 'antd'
import { BellOutlined, ShoppingCartOutlined, ReloadOutlined, CheckCircleOutlined } from '@ant-design/icons'
import ApiService from '../services/api'
import { StockNotification, ProductReservation } from '../types'

// 缺货提醒卡片组件
export const StockNotificationCard: React.FC<{
  notification: StockNotification
  onCancel?: () => void
}> = ({ notification, onCancel }) => {
  const handleCancel = async () => {
    try {
      await ApiService.cancelStockNotification(notification.id)
      message.success('已取消缺货提醒')
      onCancel?.()
    } catch (error: any) {
      message.error(error.message || '取消提醒失败')
    }
  }

  return (
    <Card
      style={{ marginBottom: 8 }}
      actions={[
        <Button type="link" danger onClick={handleCancel}>
          取消提醒
        </Button>,
      ]}
    >
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
        <div>
          <div style={{ fontWeight: 'bold' }}>{notification.productName}</div>
          <div style={{ marginTop: 8, color: '#666' }}>
            当前库存: <Tag color={notification.currentStock > 0 ? 'success' : 'error'}>
              {notification.currentStock}
            </Tag>
          </div>
          {notification.isNotified && (
            <div style={{ marginTop: 8, color: '#999', fontSize: 12 }}>
              已通知: {notification.notifyTime ? new Date(notification.notifyTime).toLocaleString() : '未知'}
            </div>
          )}
        </div>
        <BellOutlined style={{ fontSize: 24, color: '#1890ff' }} />
      </div>
    </Card>
  )
}

// 预订卡片组件
export const ReservationCard: React.FC<{
  reservation: ProductReservation
  onCancel?: () => void
}> = ({ reservation, onCancel }) => {
  const handleCancel = async () => {
    try {
      await ApiService.cancelReservation(reservation.id)
      message.success('预订已取消')
      onCancel?.()
    } catch (error: any) {
      message.error(error.message || '取消预订失败')
    }
  }

  const statusMap: Record<string, { color: string; text: string }> = {
    PENDING: { color: 'orange', text: '待确认' },
    CONFIRMED: { color: 'blue', text: '已确认' },
    CANCELLED: { color: 'default', text: '已取消' },
    EXPIRED: { color: 'red', text: '已过期' },
  }

  return (
    <Card
      style={{ marginBottom: 8 }}
      actions={[
        reservation.status === 'PENDING' || reservation.status === 'CONFIRMED' ? (
          <Button type="link" danger onClick={handleCancel}>
            取消预订
          </Button>
        ) : null,
      ].filter(Boolean)}
    >
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
        <div>
          <div style={{ fontWeight: 'bold' }}>{reservation.productName}</div>
          <div style={{ marginTop: 8, color: '#666' }}>
            数量: {reservation.quantity}
          </div>
          <div style={{ marginTop: 8, color: '#999', fontSize: 12 }}>
            到期时间: {new Date(reservation.expireTime).toLocaleString()}
          </div>
        </div>
        <Tag color={statusMap[reservation.status]?.color || 'default'}>
          {statusMap[reservation.status]?.text || reservation.status}
        </Tag>
      </div>
    </Card>
  )
}

// 替代商品推荐组件
export const AlternativeProducts: React.FC<{
  productId: number
  limit?: number
  onSelect?: (productId: number) => void
}> = ({ productId, limit = 5, onSelect }) => {
  const [loading, setLoading] = useState(false)
  const [products, setProducts] = useState<any[]>([])

  useEffect(() => {
    loadAlternatives()
  }, [productId])

  const loadAlternatives = async () => {
    try {
      setLoading(true)
      const response = await ApiService.getAlternativeProducts(productId, limit)
      if (response.code === 200) {
        setProducts(response.data || [])
      }
    } catch (error: any) {
      message.error(error.message || '加载替代商品失败')
    } finally {
      setLoading(false)
    }
  }

  if (loading) {
    return <Spin />
  }

  if (products.length === 0) {
    return <Empty description="暂无替代商品推荐" />
  }

  return (
    <Card title="替代商品推荐">
      <List
        dataSource={products}
        renderItem={(product) => (
          <List.Item
            actions={[
              <Button type="primary" onClick={() => onSelect?.(product.id)}>
                查看详情
              </Button>,
            ]}
          >
            <List.Item.Meta
              avatar={
                <img
                  src={product.image || '/placeholder.png'}
                  alt={product.name}
                  style={{ width: 60, height: 60, objectFit: 'cover', borderRadius: 4 }}
                />
              }
              title={product.name}
              description={
                <div>
                  <div style={{ color: '#1890ff', fontSize: 16, fontWeight: 'bold' }}>
                    ¥{product.price.toFixed(2)}
                  </div>
                  <div style={{ marginTop: 4, color: '#999', fontSize: 12 }}>
                    库存: {product.stock}
                  </div>
                </div>
              }
            />
          </List.Item>
        )}
      />
    </Card>
  )
}

// 缺货提醒管理组件
export const StockNotificationManager: React.FC<{}> = () => {
  const [loading, setLoading] = useState(false)
  const [notifications, setNotifications] = useState<StockNotification[]>([])

  useEffect(() => {
    loadNotifications()
  }, [])

  const loadNotifications = async () => {
    try {
      setLoading(true)
      const response = await ApiService.getStockNotifications()
      if (response.code === 200) {
        setNotifications(response.data || [])
      }
    } catch (error: any) {
      message.error(error.message || '加载缺货提醒失败')
    } finally {
      setLoading(false)
    }
  }

  const handleAddNotification = async (productId: number) => {
    try {
      const response = await ApiService.addStockNotification(productId)
      if (response.code === 200) {
        message.success('缺货提醒已设置')
        await loadNotifications()
      }
    } catch (error: any) {
      message.error(error.message || '设置缺货提醒失败')
    }
  }

  return {
    notifications,
    loading,
    loadNotifications,
    addNotification: handleAddNotification,
  }
}

// 商品预订管理组件
export const ReservationManager: React.FC<{}> = () => {
  const [loading, setLoading] = useState(false)
  const [reservations, setReservations] = useState<ProductReservation[]>([])

  useEffect(() => {
    loadReservations()
  }, [])

  const loadReservations = async () => {
    try {
      setLoading(true)
      const response = await ApiService.getReservations()
      if (response.code === 200) {
        setReservations(response.data || [])
      }
    } catch (error: any) {
      message.error(error.message || '加载预订列表失败')
    } finally {
      setLoading(false)
    }
  }

  const handleCreateReservation = async (productId: number, quantity: number) => {
    try {
      const response = await ApiService.createReservation(productId, quantity)
      if (response.code === 200) {
        message.success('商品预订成功')
        await loadReservations()
      }
    } catch (error: any) {
      message.error(error.message || '创建预订失败')
    }
  }

  return {
    reservations,
    loading,
    loadReservations,
    createReservation: handleCreateReservation,
  }
}

