import React, { useState, useEffect } from 'react'
import { Row, Col, Tabs } from 'antd'
import { WishlistCard, WishlistCategoryManager, PriceAlertSetting } from '../components/WishlistComponents'
import ApiService from '../services/api'
import { WishlistItem } from '../types'

const { TabPane } = Tabs

const WishlistPage: React.FC = () => {
  const [loading, setLoading] = useState(false)
  const [wishlist, setWishlist] = useState<WishlistItem[]>([])
  const [selectedCategory, setSelectedCategory] = useState<string>('')

  useEffect(() => {
    loadWishlist()
  }, [selectedCategory])

  const loadWishlist = async () => {
    try {
      setLoading(true)
      const response = await ApiService.getWishlist(selectedCategory || undefined)
      if (response.code === 200) {
        setWishlist(response.data?.list || response.data || [])
      }
    } catch (error: any) {
      // message.error(error.message || '加载愿望清单失败')
    } finally {
      setLoading(false)
    }
  }

  const handleRemove = async (productId: number) => {
    try {
      await ApiService.removeFromWishlist(productId)
      await loadWishlist()
    } catch (error: any) {
      // message.error(error.message || '移除失败')
    }
  }

  const handleUpdate = async (productId: number, updates: Partial<WishlistItem>) => {
    try {
      // 这里应该调用更新API
      await loadWishlist()
    } catch (error: any) {
      // message.error(error.message || '更新失败')
    }
  }

  const categories = Array.from(new Set(wishlist.map(item => item.category).filter(Boolean)))

  return (
    <div style={{ padding: 24, maxWidth: 1200, margin: '0 auto' }}>
      <WishlistCategoryManager
        items={wishlist}
        onCategoryChange={setSelectedCategory}
      />
      <Row gutter={[16, 16]}>
        {wishlist.map(item => (
          <Col xs={24} sm={12} md={8} lg={6} key={item.id}>
            <WishlistCard
              item={item}
              onRemove={() => handleRemove(item.productId)}
              onUpdate={(updates) => handleUpdate(item.productId, updates)}
            />
          </Col>
        ))}
      </Row>
      {wishlist.length === 0 && (
        <div style={{ textAlign: 'center', padding: 40 }}>
          愿望清单为空
        </div>
      )}
    </div>
  )
}

export default WishlistPage

