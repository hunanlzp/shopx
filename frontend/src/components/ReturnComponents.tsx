import React, { useState, useEffect } from 'react'
import { Card, Form, Input, Select, Button, Steps, Tag, message, Spin, Empty, Upload, Image } from 'antd'
import { UndoOutlined, CheckCircleOutlined, CloseCircleOutlined, ClockCircleOutlined } from '@ant-design/icons'
import ApiService from '../services/api'
import { ReturnOrder } from '../types'

const { Option } = Select
const { TextArea } = Input

// 退货表单组件
export const ReturnForm: React.FC<{
  orderId: number
  productId: number
  productName: string
  quantity: number
  onSuccess?: () => void
}> = ({ orderId, productId, productName, quantity, onSuccess }) => {
  const [form] = Form.useForm()
  const [submitting, setSubmitting] = useState(false)
  const [imageList, setImageList] = useState<string[]>([])

  const reasons = [
    '质量问题',
    '与描述不符',
    '尺寸不合适',
    '颜色不符',
    '收到错误商品',
    '不需要了',
    '其他',
  ]

  const handleSubmit = async (values: any) => {
    try {
      setSubmitting(true)
      const response = await ApiService.createReturnOrder({
        orderId,
        productId,
        quantity,
        reason: values.reason,
        description: values.description,
        images: imageList,
      })
      
      if (response.code === 200) {
        message.success('退货申请已提交')
        form.resetFields()
        setImageList([])
        onSuccess?.()
      }
    } catch (error: any) {
      message.error(error.message || '提交退货申请失败')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <Card title={<><UndoOutlined /> 申请退货</>}>
      <div style={{ marginBottom: 16 }}>
        <strong>商品:</strong> {productName}
        <br />
        <strong>数量:</strong> {quantity}
      </div>
      <Form form={form} layout="vertical" onFinish={handleSubmit}>
        <Form.Item
          name="reason"
          label="退货原因"
          rules={[{ required: true, message: '请选择退货原因' }]}
        >
          <Select placeholder="请选择退货原因">
            {reasons.map(reason => (
              <Option key={reason} value={reason}>{reason}</Option>
            ))}
          </Select>
        </Form.Item>
        <Form.Item
          name="description"
          label="详细说明"
          rules={[{ required: true, message: '请输入详细说明' }]}
        >
          <TextArea rows={4} placeholder="请详细描述退货原因" />
        </Form.Item>
        <Form.Item label="上传图片（可选）">
          <Upload
            listType="picture-card"
            fileList={imageList.map((url, index) => ({
              uid: index.toString(),
              url,
              name: `image-${index}`,
            }))}
            onChange={(info) => {
              // 这里应该上传到服务器并获取URL
              // 暂时使用本地预览
              const newList = info.fileList.map(file => file.url || '').filter(Boolean)
              setImageList(newList)
            }}
            beforeUpload={() => false}
          >
            {imageList.length < 5 && '+ 上传'}
          </Upload>
        </Form.Item>
        <Form.Item>
          <Button type="primary" htmlType="submit" loading={submitting} block>
            提交退货申请
          </Button>
        </Form.Item>
      </Form>
    </Card>
  )
}

// 退货进度展示组件
export const ReturnProgress: React.FC<{ returnId: number }> = ({ returnId }) => {
  const [loading, setLoading] = useState(false)
  const [returnOrder, setReturnOrder] = useState<ReturnOrder | null>(null)

  useEffect(() => {
    loadReturnOrder()
  }, [returnId])

  const loadReturnOrder = async () => {
    try {
      setLoading(true)
      const response = await ApiService.getReturnOrder(returnId)
      if (response.code === 200) {
        setReturnOrder(response.data)
      }
    } catch (error: any) {
      message.error(error.message || '加载退货信息失败')
    } finally {
      setLoading(false)
    }
  }

  if (loading) {
    return <Spin />
  }

  if (!returnOrder) {
    return <Empty description="未找到退货信息" />
  }

  const statusMap: Record<string, { color: string; text: string; icon: React.ReactNode }> = {
    PENDING: { color: 'orange', text: '待审核', icon: <ClockCircleOutlined /> },
    APPROVED: { color: 'blue', text: '已批准', icon: <CheckCircleOutlined /> },
    REJECTED: { color: 'red', text: '已拒绝', icon: <CloseCircleOutlined /> },
    RETURNING: { color: 'processing', text: '退货中', icon: <UndoOutlined /> },
    RETURNED: { color: 'cyan', text: '已退货', icon: <CheckCircleOutlined /> },
    REFUNDED: { color: 'green', text: '已退款', icon: <CheckCircleOutlined /> },
    CANCELLED: { color: 'default', text: '已取消', icon: <CloseCircleOutlined /> },
  }

  const statusInfo = statusMap[returnOrder.status] || statusMap.PENDING

  const getSteps = () => {
    const steps = [
      { title: '提交申请', status: 'finish' },
    ]

    if (returnOrder.status !== 'PENDING' && returnOrder.status !== 'CANCELLED') {
      steps.push({ title: '审核', status: returnOrder.status === 'REJECTED' ? 'error' : 'finish' })
    }

    if (['APPROVED', 'RETURNING', 'RETURNED', 'REFUNDED'].includes(returnOrder.status)) {
      steps.push({ title: '退货中', status: returnOrder.status === 'RETURNING' ? 'process' : 'finish' })
    }

    if (['RETURNED', 'REFUNDED'].includes(returnOrder.status)) {
      steps.push({ title: '已退货', status: 'finish' })
    }

    if (returnOrder.status === 'REFUNDED') {
      steps.push({ title: '已退款', status: 'finish' })
    }

    return steps
  }

  return (
    <Card
      title={<><UndoOutlined /> 退货进度</>}
      extra={<Tag color={statusInfo.color} icon={statusInfo.icon}>{statusInfo.text}</Tag>}
    >
      <div style={{ marginBottom: 16 }}>
        <div><strong>商品:</strong> {returnOrder.productName}</div>
        <div style={{ marginTop: 8 }}><strong>数量:</strong> {returnOrder.quantity}</div>
        <div style={{ marginTop: 8 }}><strong>原因:</strong> {returnOrder.reason}</div>
        {returnOrder.description && (
          <div style={{ marginTop: 8 }}><strong>说明:</strong> {returnOrder.description}</div>
        )}
        {returnOrder.refundAmount > 0 && (
          <div style={{ marginTop: 8 }}>
            <strong>退款金额:</strong> <span style={{ color: '#3f8600', fontSize: 18 }}>¥{returnOrder.refundAmount.toFixed(2)}</span>
          </div>
        )}
        {returnOrder.trackingNumber && (
          <div style={{ marginTop: 8 }}><strong>物流单号:</strong> {returnOrder.trackingNumber}</div>
        )}
      </div>
      <Steps items={getSteps()} />
      {returnOrder.images && returnOrder.images.length > 0 && (
        <div style={{ marginTop: 16 }}>
          <div style={{ marginBottom: 8 }}><strong>退货图片:</strong></div>
          <Image.PreviewGroup>
            {returnOrder.images.map((url, index) => (
              <Image key={index} src={url} width={100} height={100} style={{ marginRight: 8, marginBottom: 8 }} />
            ))}
          </Image.PreviewGroup>
        </div>
      )}
    </Card>
  )
}

// 退货历史列表组件
export const ReturnHistory: React.FC<{
  onViewDetail?: (returnId: number) => void
}> = ({ onViewDetail }) => {
  const [loading, setLoading] = useState(false)
  const [returns, setReturns] = useState<ReturnOrder[]>([])
  const [page, setPage] = useState(1)
  const [total, setTotal] = useState(0)

  useEffect(() => {
    loadReturns()
  }, [page])

  const loadReturns = async () => {
    try {
      setLoading(true)
      const response = await ApiService.getReturnOrders(page, 10)
      if (response.code === 200) {
        setReturns(response.data?.list || [])
        setTotal(response.data?.total || 0)
      }
    } catch (error: any) {
      message.error(error.message || '加载退货列表失败')
    } finally {
      setLoading(false)
    }
  }

  const statusMap: Record<string, { color: string; text: string }> = {
    PENDING: { color: 'orange', text: '待审核' },
    APPROVED: { color: 'blue', text: '已批准' },
    REJECTED: { color: 'red', text: '已拒绝' },
    RETURNING: { color: 'processing', text: '退货中' },
    RETURNED: { color: 'cyan', text: '已退货' },
    REFUNDED: { color: 'green', text: '已退款' },
    CANCELLED: { color: 'default', text: '已取消' },
  }

  return (
    <div>
      {returns.length === 0 ? (
        <Empty description="暂无退货记录" />
      ) : (
        <div>
          {returns.map(returnOrder => (
            <Card
              key={returnOrder.id}
              style={{ marginBottom: 16 }}
              actions={[
                <Button type="link" onClick={() => onViewDetail?.(returnOrder.id)}>
                  查看详情
                </Button>,
              ]}
            >
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                <div>
                  <div style={{ fontWeight: 'bold', fontSize: 16 }}>{returnOrder.productName}</div>
                  <div style={{ marginTop: 8, color: '#666' }}>
                    数量: {returnOrder.quantity} | 原因: {returnOrder.reason}
                  </div>
                  <div style={{ marginTop: 8, color: '#999', fontSize: 12 }}>
                    {new Date(returnOrder.createTime).toLocaleString()}
                  </div>
                </div>
                <div style={{ textAlign: 'right' }}>
                  <Tag color={statusMap[returnOrder.status]?.color || 'default'}>
                    {statusMap[returnOrder.status]?.text || returnOrder.status}
                  </Tag>
                  {returnOrder.refundAmount > 0 && (
                    <div style={{ marginTop: 8, color: '#3f8600', fontWeight: 'bold' }}>
                      ¥{returnOrder.refundAmount.toFixed(2)}
                    </div>
                  )}
                </div>
              </div>
            </Card>
          ))}
        </div>
      )}
    </div>
  )
}

