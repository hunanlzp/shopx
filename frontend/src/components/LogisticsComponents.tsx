 import React, { useState, useEffect } from 'react'
import { Card, Steps, Tag, message, Spin, Empty, Button, Form, Input, Select } from 'antd'
import { TruckOutlined, EnvironmentOutlined, PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons'
import ApiService from '../services/api'
import type { LogisticsTracking, ShippingOption, ShippingAddress } from '../types'


// 物流追踪组件
export const LogisticsTrackingComponent: React.FC<{ orderId: number }> = ({ orderId }) => {
  const [loading, setLoading] = useState(false)
  const [tracking, setTracking] = useState<LogisticsTracking | null>(null)

  useEffect(() => {
    loadTracking()
  }, [orderId])

  const loadTracking = async () => {
    try {
      setLoading(true)
      const response = await ApiService.getLogisticsTracking(orderId)
      if (response.code === 200) {
        setTracking(response.data)
      }
    } catch (error: any) {
      message.error(error.message || '加载物流信息失败')
    } finally {
      setLoading(false)
    }
  }

  if (loading) {
    return <Spin size="large" style={{ display: 'block', textAlign: 'center', padding: '40px' }} />
  }

  if (!tracking) {
    return <Empty description="暂无物流信息" />
  }

  const statusMap: Record<string, { color: string; text: string }> = {
    PENDING: { color: 'default', text: '待发货' },
    PICKED_UP: { color: 'processing', text: '已揽收' },
    IN_TRANSIT: { color: 'processing', text: '运输中' },
    OUT_FOR_DELIVERY: { color: 'warning', text: '派送中' },
    DELIVERED: { color: 'success', text: '已送达' },
    EXCEPTION: { color: 'error', text: '异常' },
  }

  const currentStatus = statusMap[tracking.status] || { color: 'default', text: tracking.status }

  const stepItems = tracking.details.map((detail, index) => ({
    title: detail.description,
    description: (
      <div>
        <div>{detail.location || '未知位置'}</div>
        <div style={{ color: '#999', fontSize: 12 }}>{new Date(detail.timestamp).toLocaleString()}</div>
      </div>
    ),
    status: index === 0 ? 'finish' : index === tracking.details.length - 1 ? 'process' : 'finish',
  }))

  return (
    <Card
      title={
        <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
          <TruckOutlined />
          <span>物流追踪</span>
        </div>
      }
      extra={
        <Tag color={currentStatus.color}>{currentStatus.text}</Tag>
      }
    >
      <div style={{ marginBottom: 16 }}>
        <div><strong>快递公司:</strong> {tracking.carrier}</div>
        <div style={{ marginTop: 8 }}><strong>运单号:</strong> {tracking.trackingNumber}</div>
        {tracking.currentLocation && (
          <div style={{ marginTop: 8 }}>
            <strong>当前位置:</strong> {tracking.currentLocation}
          </div>
        )}
        {tracking.estimatedDeliveryTime && (
          <div style={{ marginTop: 8 }}>
            <strong>预计送达:</strong> {new Date(tracking.estimatedDeliveryTime).toLocaleString()}
          </div>
        )}
      </div>
      <Steps direction="vertical" items={stepItems} />
    </Card>
  )
}

// 收货地址表单组件
export const ShippingAddressForm: React.FC<{
  address?: ShippingAddress
  onSave?: (address: ShippingAddress) => void
  onCancel?: () => void
}> = ({ address, onSave, onCancel }) => {
  const [form] = Form.useForm()
  const [saving, setSaving] = useState(false)

  useEffect(() => {
    if (address) {
      form.setFieldsValue(address)
    }
  }, [address, form])

  const handleSubmit = async (values: any) => {
    try {
      setSaving(true)
      let response
      if (address?.id) {
        response = await ApiService.updateShippingAddress(address.id, values)
      } else {
        response = await ApiService.addShippingAddress(values)
      }
      
      if (response.code === 200) {
        message.success(address?.id ? '地址更新成功' : '地址添加成功')
        onSave?.(response.data)
        form.resetFields()
      }
    } catch (error: any) {
      message.error(error.message || '保存地址失败')
    } finally {
      setSaving(false)
    }
  }

  return (
    <Form
      form={form}
      layout="vertical"
      onFinish={handleSubmit}
      initialValues={{
        isDefault: false,
      }}
    >
      <Form.Item
        name="receiverName"
        label="收货人姓名"
        rules={[{ required: true, message: '请输入收货人姓名' }]}
      >
        <Input placeholder="请输入收货人姓名" />
      </Form.Item>
      <Form.Item
        name="receiverPhone"
        label="联系电话"
        rules={[
          { required: true, message: '请输入联系电话' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码' },
        ]}
      >
        <Input placeholder="请输入手机号码" />
      </Form.Item>
      <Form.Item
        name="province"
        label="省份"
        rules={[{ required: true, message: '请选择省份' }]}
      >
        <Input placeholder="请输入省份" />
      </Form.Item>
      <Form.Item
        name="city"
        label="城市"
        rules={[{ required: true, message: '请输入城市' }]}
      >
        <Input placeholder="请输入城市" />
      </Form.Item>
      <Form.Item
        name="district"
        label="区县"
        rules={[{ required: true, message: '请输入区县' }]}
      >
        <Input placeholder="请输入区县" />
      </Form.Item>
      <Form.Item
        name="detailAddress"
        label="详细地址"
        rules={[{ required: true, message: '请输入详细地址' }]}
      >
        <Input.TextArea rows={3} placeholder="请输入详细地址" />
      </Form.Item>
      <Form.Item
        name="postalCode"
        label="邮政编码"
      >
        <Input placeholder="请输入邮政编码（可选）" />
      </Form.Item>
      <Form.Item
        name="isDefault"
        valuePropName="checked"
      >
        <input type="checkbox" /> 设为默认地址
      </Form.Item>
      <Form.Item>
        <Button type="primary" htmlType="submit" loading={saving} block>
          {address?.id ? '更新地址' : '添加地址'}
        </Button>
        {onCancel && (
          <Button onClick={onCancel} style={{ marginTop: 8 }} block>
            取消
          </Button>
        )}
      </Form.Item>
    </Form>
  )
}

// 配送选项选择器组件
export const ShippingOptionsSelector: React.FC<{
  productId: number
  addressId?: number
  value?: string
  onChange?: (option: ShippingOption) => void
}> = ({ productId, addressId, value, onChange }) => {
  const [loading, setLoading] = useState(false)
  const [options, setOptions] = useState<ShippingOption[]>([])

  useEffect(() => {
    if (productId && addressId) {
      loadOptions()
    }
  }, [productId, addressId])

  const loadOptions = async () => {
    if (!addressId) return

    try {
      setLoading(true)
      const response = await ApiService.getShippingOptions(productId, addressId)
      if (response.code === 200) {
        setOptions(response.data || [])
      }
    } catch (error: any) {
      message.error(error.message || '加载配送选项失败')
    } finally {
      setLoading(false)
    }
  }

  if (!addressId) {
    return <Empty description="请先选择收货地址" />
  }

  if (loading) {
    return <Spin />
  }

  return (
    <div>
      {options.map(option => (
        <Card
          key={option.method}
          style={{
            marginBottom: 8,
            cursor: 'pointer',
            border: value === option.method ? '2px solid #1890ff' : '1px solid #d9d9d9',
          }}
          onClick={() => onChange?.(option)}
        >
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <div style={{ fontWeight: 'bold' }}>{option.name}</div>
              <div style={{ color: '#999', fontSize: 12, marginTop: 4 }}>
                预计{option.estimatedDays}天送达
              </div>
              {option.description && (
                <div style={{ color: '#999', fontSize: 12, marginTop: 4 }}>
                  {option.description}
                </div>
              )}
            </div>
            <div style={{ fontSize: 18, fontWeight: 'bold', color: '#1890ff' }}>
              ¥{option.fee.toFixed(2)}
            </div>
          </div>
        </Card>
      ))}
    </div>
  )
}

// 收货地址管理组件
export const ShippingAddressManager: React.FC<{
  onSelect?: (address: ShippingAddress) => void
  selectedAddressId?: number
}> = ({ onSelect, selectedAddressId }) => {
  const [loading, setLoading] = useState(false)
  const [addresses, setAddresses] = useState<ShippingAddress[]>([])
  const [editingAddress, setEditingAddress] = useState<ShippingAddress | null>(null)
  const [showForm, setShowForm] = useState(false)

  useEffect(() => {
    loadAddresses()
  }, [])

  const loadAddresses = async () => {
    try {
      setLoading(true)
      const response = await ApiService.getShippingAddresses()
      if (response.code === 200) {
        setAddresses(response.data || [])
      }
    } catch (error: any) {
      message.error(error.message || '加载地址列表失败')
    } finally {
      setLoading(false)
    }
  }

  const handleDelete = async (addressId: number) => {
    try {
      await ApiService.deleteShippingAddress(addressId)
      message.success('地址删除成功')
      loadAddresses()
    } catch (error: any) {
      message.error(error.message || '删除地址失败')
    }
  }

  const handleSave = () => {
    setShowForm(false)
    setEditingAddress(null)
    loadAddresses()
  }

  return (
    <div>
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between' }}>
        <h3>收货地址</h3>
        <Button
          type="primary"
          icon={<PlusOutlined />}
          onClick={() => {
            setEditingAddress(null)
            setShowForm(true)
          }}
        >
          添加地址
        </Button>
      </div>

      {showForm && (
        <Card style={{ marginBottom: 16 }}>
          <ShippingAddressForm
            address={editingAddress || undefined}
            onSave={handleSave}
            onCancel={() => {
              setShowForm(false)
              setEditingAddress(null)
            }}
          />
        </Card>
      )}

      <Spin spinning={loading}>
        {addresses.length === 0 ? (
          <Empty description="暂无收货地址" />
        ) : (
          <div>
            {addresses.map(address => (
              <Card
                key={address.id}
                style={{
                  marginBottom: 8,
                  cursor: 'pointer',
                  border: selectedAddressId === address.id ? '2px solid #1890ff' : '1px solid #d9d9d9',
                }}
                onClick={() => onSelect?.(address)}
                actions={[
                  <Button
                    type="link"
                    icon={<EditOutlined />}
                    onClick={(e) => {
                      e.stopPropagation()
                      setEditingAddress(address)
                      setShowForm(true)
                    }}
                  >
                    编辑
                  </Button>,
                  <Button
                    type="link"
                    danger
                    icon={<DeleteOutlined />}
                    onClick={(e) => {
                      e.stopPropagation()
                      handleDelete(address.id)
                    }}
                  >
                    删除
                  </Button>,
                ]}
              >
                <div>
                  <div style={{ fontWeight: 'bold' }}>
                    {address.receiverName} {address.receiverPhone}
                    {address.isDefault && <Tag color="blue" style={{ marginLeft: 8 }}>默认</Tag>}
                  </div>
                  <div style={{ marginTop: 8, color: '#666' }}>
                    <EnvironmentOutlined /> {address.province} {address.city} {address.district} {address.detailAddress}
                  </div>
                  {address.postalCode && (
                    <div style={{ marginTop: 4, color: '#999', fontSize: 12 }}>
                      邮编: {address.postalCode}
                    </div>
                  )}
                </div>
              </Card>
            ))}
          </div>
        )}
      </Spin>
    </div>
  )
}

