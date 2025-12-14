import React, { useState, useEffect } from 'react'
import { Card, Button, Statistic, Table, Tag, message, Spin, Empty } from 'antd'
import { Line } from '@ant-design/charts'
import { DollarOutlined, HistoryOutlined, ProtectionOutlined } from '@ant-design/icons'
import ApiService from '../services/api'
import { PriceHistory, PriceProtection, TotalPriceCalculation } from '../types'

// 价格历史图表组件
export const PriceHistoryChart: React.FC<{ productId: number; days?: number }> = ({ productId, days = 30 }) => {
  const [loading, setLoading] = useState(false)
  const [data, setData] = useState<PriceHistory[]>([])

  useEffect(() => {
    loadPriceHistory()
  }, [productId, days])

  const loadPriceHistory = async () => {
    try {
      setLoading(true)
      const response = await ApiService.getPriceHistory(productId, days)
      if (response.code === 200) {
        setData(response.data || [])
      }
    } catch (error: any) {
      message.error(error.message || '加载价格历史失败')
    } finally {
      setLoading(false)
    }
  }

  if (loading) {
    return <Spin size="large" style={{ display: 'block', textAlign: 'center', padding: '40px' }} />
  }

  if (data.length === 0) {
    return <Empty description="暂无价格历史数据" />
  }

  const chartData = data.map(item => ({
    date: item.recordDate,
    price: item.price
  }))

  const config = {
    data: chartData,
    xField: 'date',
    yField: 'price',
    point: {
      size: 4,
      shape: 'circle',
    },
    label: {
      style: {
        fill: '#aaa',
      },
    },
    smooth: true,
    color: '#1890ff',
  }

  return (
    <Card title={<><HistoryOutlined /> 价格历史 ({days}天)</>}>
      <Line {...config} />
      <div style={{ marginTop: 16 }}>
        <Statistic
          title="最低价格"
          value={Math.min(...data.map(d => d.price))}
          prefix={<DollarOutlined />}
          precision={2}
        />
        <Statistic
          title="最高价格"
          value={Math.max(...data.map(d => d.price))}
          prefix={<DollarOutlined />}
          precision={2}
          style={{ marginTop: 16 }}
        />
      </div>
    </Card>
  )
}

// 价格保护卡片组件
export const PriceProtectionCard: React.FC<{ orderId: number }> = ({ orderId }) => {
  const [loading, setLoading] = useState(false)
  const [protection, setProtection] = useState<PriceProtection | null>(null)
  const [applying, setApplying] = useState(false)

  useEffect(() => {
    loadProtection()
  }, [orderId])

  const loadProtection = async () => {
    try {
      setLoading(true)
      const response = await ApiService.getPriceProtection(orderId)
      if (response.code === 200) {
        setProtection(response.data)
      }
    } catch (error: any) {
      // 如果不存在，不显示错误
      if (error.code !== 404) {
        message.error(error.message || '加载价格保护信息失败')
      }
    } finally {
      setLoading(false)
    }
  }

  const handleApply = async () => {
    try {
      setApplying(true)
      const response = await ApiService.createPriceProtection(orderId)
      if (response.code === 200) {
        message.success('价格保护申请已提交')
        await loadProtection()
      }
    } catch (error: any) {
      message.error(error.message || '申请价格保护失败')
    } finally {
      setApplying(false)
    }
  }

  if (loading) {
    return <Spin />
  }

  if (!protection) {
    return (
      <Card title={<><ProtectionOutlined /> 价格保护</>}>
        <p>该订单尚未申请价格保护</p>
        <Button type="primary" onClick={handleApply} loading={applying}>
          申请价格保护
        </Button>
      </Card>
    )
  }

  const statusMap: Record<string, { color: string; text: string }> = {
    PENDING: { color: 'orange', text: '审核中' },
    APPROVED: { color: 'green', text: '已批准' },
    REJECTED: { color: 'red', text: '已拒绝' },
    COMPLETED: { color: 'blue', text: '已完成' },
  }

  return (
    <Card title={<><ProtectionOutlined /> 价格保护</>}>
      <div style={{ marginBottom: 16 }}>
        <Tag color={statusMap[protection.status]?.color || 'default'}>
          {statusMap[protection.status]?.text || protection.status}
        </Tag>
      </div>
      <Statistic
        title="原价"
        value={protection.originalPrice}
        prefix={<DollarOutlined />}
        precision={2}
      />
      <Statistic
        title="现价"
        value={protection.currentPrice}
        prefix={<DollarOutlined />}
        precision={2}
        style={{ marginTop: 16 }}
      />
      {protection.refundAmount > 0 && (
        <Statistic
          title="退款金额"
          value={protection.refundAmount}
          prefix={<DollarOutlined />}
          precision={2}
          valueStyle={{ color: '#3f8600' }}
          style={{ marginTop: 16 }}
        />
      )}
    </Card>
  )
}

// 总价计算器组件
export const TotalPriceCalculator: React.FC<{
  productId: number
  quantity: number
  addressId?: number
  onCalculated?: (calculation: TotalPriceCalculation) => void
}> = ({ productId, quantity, addressId, onCalculated }) => {
  const [loading, setLoading] = useState(false)
  const [calculation, setCalculation] = useState<TotalPriceCalculation | null>(null)

  useEffect(() => {
    if (productId && quantity > 0 && addressId) {
      calculatePrice()
    }
  }, [productId, quantity, addressId])

  const calculatePrice = async () => {
    if (!addressId) return

    try {
      setLoading(true)
      const response = await ApiService.calculateTotalPrice(productId, quantity, addressId)
      if (response.code === 200) {
        setCalculation(response.data)
        onCalculated?.(response.data)
      }
    } catch (error: any) {
      message.error(error.message || '计算总价失败')
    } finally {
      setLoading(false)
    }
  }

  if (!addressId) {
    return (
      <Card title="价格明细">
        <Empty description="请先选择收货地址" />
      </Card>
    )
  }

  if (loading) {
    return <Spin />
  }

  if (!calculation) {
    return null
  }

  return (
    <Card title="价格明细">
      <Table
        dataSource={[
          { key: '1', label: '商品单价', value: `¥${calculation.productPrice.toFixed(2)}` },
          { key: '2', label: '数量', value: quantity },
          { key: '3', label: '小计', value: `¥${calculation.subtotal.toFixed(2)}` },
          { key: '4', label: '运费', value: `¥${calculation.shippingFee.toFixed(2)}` },
          { key: '5', label: `税费 (${(calculation.taxRate * 100).toFixed(1)}%)`, value: `¥${calculation.taxAmount.toFixed(2)}` },
        ]}
        columns={[
          { dataIndex: 'label', key: 'label' },
          { dataIndex: 'value', key: 'value', align: 'right' },
        ]}
        pagination={false}
        showHeader={false}
        size="small"
      />
      <div style={{ marginTop: 16, paddingTop: 16, borderTop: '1px solid #f0f0f0' }}>
        <Statistic
          title="总计"
          value={calculation.total}
          prefix="¥"
          precision={2}
          valueStyle={{ fontSize: 24, fontWeight: 'bold', color: '#1890ff' }}
        />
      </div>
    </Card>
  )
}

