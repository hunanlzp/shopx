import React, { useState, useEffect } from 'react'
import { Card, Button, Tag, Input, Select, message, Spin, Empty, Modal, InputNumber, Checkbox } from 'antd'
import { HeartOutlined, DeleteOutlined, ShareAltOutlined, BellOutlined, EditOutlined } from '@ant-design/icons'
import ApiService from '../services/api'
import { WishlistItem } from '../types'

const { Option } = Select

// 愿望清单卡片组件
export const WishlistCard: React.FC<{
  item: WishlistItem
  onRemove?: () => void
  onUpdate?: (updates: Partial<WishlistItem>) => void
}> = ({ item, onRemove, onUpdate }) => {
  const [editing, setEditing] = useState(false)
  const [notes, setNotes] = useState(item.notes || '')
  const [category, setCategory] = useState(item.category || '')

  const handleSave = () => {
    onUpdate?.({ notes, category })
    setEditing(false)
  }

  const handleSetPriceAlert = async (targetPrice: number) => {
    try {
      await ApiService.setPriceAlert(item.productId, targetPrice)
      message.success('价格提醒已设置')
      onUpdate?.({ priceAlert: true, targetPrice })
    } catch (error: any) {
      message.error(error.message || '设置价格提醒失败')
    }
  }

  return (
    <Card
      style={{ marginBottom: 16 }}
      cover={
        <img
          alt={item.product.name}
          src={item.product.image || '/placeholder.png'}
          style={{ height: 200, objectFit: 'cover' }}
        />
      }
      actions={[
        <Button type="link" icon={<EditOutlined />} onClick={() => setEditing(!editing)}>
          {editing ? '取消' : '编辑'}
        </Button>,
        <Button type="link" danger icon={<DeleteOutlined />} onClick={onRemove}>
          删除
        </Button>,
      ]}
    >
      <div>
        <h3>{item.product.name}</h3>
        <div style={{ marginTop: 8 }}>
          <span style={{ fontSize: 20, fontWeight: 'bold', color: '#1890ff' }}>
            ¥{item.product.price.toFixed(2)}
          </span>
          {item.priceAlert && item.targetPrice && (
            <Tag color="orange" style={{ marginLeft: 8 }}>
              目标价: ¥{item.targetPrice.toFixed(2)}
            </Tag>
          )}
        </div>
        <div style={{ marginTop: 8 }}>
          {item.category && <Tag>{item.category}</Tag>}
          {item.product.stock > 0 ? (
            <Tag color="success">有货</Tag>
          ) : (
            <Tag color="error">缺货</Tag>
          )}
        </div>
        {editing ? (
          <div style={{ marginTop: 16 }}>
            <Input
              placeholder="分类"
              value={category}
              onChange={(e) => setCategory(e.target.value)}
              style={{ marginBottom: 8 }}
            />
            <Input.TextArea
              placeholder="备注"
              value={notes}
              onChange={(e) => setNotes(e.target.value)}
              rows={3}
              style={{ marginBottom: 8 }}
            />
            <Button type="primary" onClick={handleSave} block>
              保存
            </Button>
          </div>
        ) : (
          item.notes && (
            <div style={{ marginTop: 8, color: '#666', fontSize: 12 }}>
              {item.notes}
            </div>
          )
        )}
        {!item.priceAlert && (
          <Button
            type="link"
            icon={<BellOutlined />}
            onClick={() => {
              Modal.confirm({
                title: '设置价格提醒',
                content: (
                  <InputNumber
                    placeholder="目标价格"
                    min={0}
                    precision={2}
                    style={{ width: '100%', marginTop: 8 }}
                    onChange={(value) => {
                      if (value) {
                        handleSetPriceAlert(value)
                      }
                    }}
                  />
                ),
                onOk: () => {},
              })
            }}
            style={{ marginTop: 8 }}
          >
            设置价格提醒
          </Button>
        )}
      </div>
    </Card>
  )
}

// 价格提醒设置组件
export const PriceAlertSetting: React.FC<{
  productId: number
  currentPrice: number
  onSet?: (targetPrice: number) => void
}> = ({ productId, currentPrice, onSet }) => {
  const [targetPrice, setTargetPrice] = useState<number>(currentPrice * 0.9)

  const handleSet = async () => {
    if (targetPrice >= currentPrice) {
      message.warning('目标价格应低于当前价格')
      return
    }

    try {
      await ApiService.setPriceAlert(productId, targetPrice)
      message.success('价格提醒已设置')
      onSet?.(targetPrice)
    } catch (error: any) {
      message.error(error.message || '设置价格提醒失败')
    }
  }

  return (
    <Card title={<><BellOutlined /> 设置价格提醒</>}>
      <div style={{ marginBottom: 16 }}>
        <div>当前价格: <strong style={{ color: '#1890ff' }}>¥{currentPrice.toFixed(2)}</strong></div>
      </div>
      <InputNumber
        value={targetPrice}
        onChange={(value) => setTargetPrice(value || 0)}
        min={0}
        precision={2}
        prefix="¥"
        style={{ width: '100%', marginBottom: 16 }}
      />
      <Button type="primary" onClick={handleSet} block>
        设置提醒
      </Button>
      <div style={{ marginTop: 8, color: '#999', fontSize: 12 }}>
        当商品价格降至目标价格或以下时，我们将通知您
      </div>
    </Card>
  )
}

// 分类管理组件
export const WishlistCategoryManager: React.FC<{
  items: WishlistItem[]
  onCategoryChange?: (category: string) => void
}> = ({ items, onCategoryChange }) => {
  const categories = Array.from(new Set(items.map(item => item.category).filter(Boolean)))

  return (
    <div style={{ marginBottom: 16 }}>
      <div style={{ marginBottom: 8 }}>分类筛选:</div>
      <Space wrap>
        <Button
          onClick={() => onCategoryChange?.('')}
        >
          全部
        </Button>
        {categories.map(category => (
          <Button
            key={category}
            onClick={() => onCategoryChange?.(category || '')}
          >
            {category}
          </Button>
        ))}
      </Space>
    </div>
  )
}

