import React, { useState, useEffect } from 'react'
import { Card, Form, Input, Select, Button, Tag, message, Spin, Empty, List, Tabs } from 'antd'
import { CustomerServiceOutlined, QuestionCircleOutlined, PlusOutlined, CheckCircleOutlined } from '@ant-design/icons'
import ApiService from '../services/api'
import { CustomerServiceTicket, FAQ } from '../types'

const { Option } = Select
const { TextArea } = Input
const { TabPane } = Tabs

// 工单表单组件
export const TicketForm: React.FC<{
  onSuccess?: () => void
}> = ({ onSuccess }) => {
  const [form] = Form.useForm()
  const [submitting, setSubmitting] = useState(false)

  const ticketTypes = [
    { value: 'ORDER', label: '订单问题' },
    { value: 'PRODUCT', label: '商品问题' },
    { value: 'PAYMENT', label: '支付问题' },
    { value: 'LOGISTICS', label: '物流问题' },
    { value: 'RETURN', label: '退货问题' },
    { value: 'OTHER', label: '其他' },
  ]

  const priorities = [
    { value: 'LOW', label: '低' },
    { value: 'MEDIUM', label: '中' },
    { value: 'HIGH', label: '高' },
    { value: 'URGENT', label: '紧急' },
  ]

  const handleSubmit = async (values: any) => {
    try {
      setSubmitting(true)
      const response = await ApiService.createTicket({
        ticketType: values.ticketType,
        title: values.title,
        content: values.content,
        priority: values.priority || 'MEDIUM',
      })
      
      if (response.code === 200) {
        message.success('工单创建成功')
        form.resetFields()
        onSuccess?.()
      }
    } catch (error: any) {
      message.error(error.message || '创建工单失败')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <Card title={<><CustomerServiceOutlined /> 创建工单</>}>
      <Form form={form} layout="vertical" onFinish={handleSubmit}>
        <Form.Item
          name="ticketType"
          label="问题类型"
          rules={[{ required: true, message: '请选择问题类型' }]}
        >
          <Select placeholder="请选择问题类型">
            {ticketTypes.map(type => (
              <Option key={type.value} value={type.value}>{type.label}</Option>
            ))}
          </Select>
        </Form.Item>
        <Form.Item
          name="title"
          label="标题"
          rules={[{ required: true, message: '请输入标题' }]}
        >
          <Input placeholder="请简要描述问题" />
        </Form.Item>
        <Form.Item
          name="content"
          label="详细描述"
          rules={[{ required: true, message: '请输入详细描述' }]}
        >
          <TextArea rows={6} placeholder="请详细描述您遇到的问题" />
        </Form.Item>
        <Form.Item
          name="priority"
          label="优先级"
        >
          <Select placeholder="请选择优先级" defaultValue="MEDIUM">
            {priorities.map(priority => (
              <Option key={priority.value} value={priority.value}>{priority.label}</Option>
            ))}
          </Select>
        </Form.Item>
        <Form.Item>
          <Button type="primary" htmlType="submit" loading={submitting} block>
            提交工单
          </Button>
        </Form.Item>
      </Form>
    </Card>
  )
}

// 工单列表组件
export const TicketList: React.FC<{}> = () => {
  const [loading, setLoading] = useState(false)
  const [tickets, setTickets] = useState<CustomerServiceTicket[]>([])
  const [page, setPage] = useState(1)
  const [total, setTotal] = useState(0)

  useEffect(() => {
    loadTickets()
  }, [page])

  const loadTickets = async () => {
    try {
      setLoading(true)
      const response = await ApiService.getTickets(page, 10)
      if (response.code === 200) {
        setTickets(response.data?.list || [])
        setTotal(response.data?.total || 0)
      }
    } catch (error: any) {
      message.error(error.message || '加载工单列表失败')
    } finally {
      setLoading(false)
    }
  }

  const statusMap: Record<string, { color: string; text: string }> = {
    OPEN: { color: 'blue', text: '待处理' },
    IN_PROGRESS: { color: 'processing', text: '处理中' },
    RESOLVED: { color: 'success', text: '已解决' },
    CLOSED: { color: 'default', text: '已关闭' },
  }

  const priorityMap: Record<string, { color: string; text: string }> = {
    LOW: { color: 'default', text: '低' },
    MEDIUM: { color: 'blue', text: '中' },
    HIGH: { color: 'orange', text: '高' },
    URGENT: { color: 'red', text: '紧急' },
  }

  return (
    <div>
      {tickets.length === 0 ? (
        <Empty description="暂无工单" />
      ) : (
        <List
          loading={loading}
          dataSource={tickets}
          renderItem={(ticket) => (
            <List.Item
              actions={[
                <Button type="link" onClick={() => {
                  // 查看详情
                }}>
                  查看详情
                </Button>,
              ]}
            >
              <List.Item.Meta
                title={
                  <div>
                    <span style={{ fontWeight: 'bold' }}>{ticket.title}</span>
                    <Tag color={statusMap[ticket.status]?.color || 'default'} style={{ marginLeft: 8 }}>
                      {statusMap[ticket.status]?.text || ticket.status}
                    </Tag>
                    <Tag color={priorityMap[ticket.priority]?.color || 'default'} style={{ marginLeft: 8 }}>
                      {priorityMap[ticket.priority]?.text || ticket.priority}
                    </Tag>
                  </div>
                }
                description={
                  <div>
                    <div style={{ marginTop: 4 }}>{ticket.content}</div>
                    <div style={{ marginTop: 8, color: '#999', fontSize: 12 }}>
                      {new Date(ticket.createTime).toLocaleString()}
                    </div>
                  </div>
                }
              />
            </List.Item>
          )}
        />
      )}
    </div>
  )
}

// FAQ搜索组件
export const FAQSearch: React.FC<{
  onSelect?: (faq: FAQ) => void
}> = ({ onSelect }) => {
  const [loading, setLoading] = useState(false)
  const [keyword, setKeyword] = useState('')
  const [results, setResults] = useState<FAQ[]>([])

  const handleSearch = async () => {
    if (!keyword.trim()) {
      message.warning('请输入搜索关键词')
      return
    }

    try {
      setLoading(true)
      const response = await ApiService.searchFAQs(keyword)
      if (response.code === 200) {
        setResults(response.data || [])
      }
    } catch (error: any) {
      message.error(error.message || '搜索失败')
    } finally {
      setLoading(false)
    }
  }

  return (
    <Card title={<><QuestionCircleOutlined /> 搜索常见问题</>}>
      <Input.Search
        placeholder="输入关键词搜索"
        value={keyword}
        onChange={(e) => setKeyword(e.target.value)}
        onSearch={handleSearch}
        enterButton
        loading={loading}
      />
      {results.length > 0 && (
        <List
          dataSource={results}
          renderItem={(faq) => (
            <List.Item
              style={{ cursor: 'pointer' }}
              onClick={() => onSelect?.(faq)}
            >
              <List.Item.Meta
                title={faq.question}
                description={faq.answer.substring(0, 100) + '...'}
              />
            </List.Item>
          )}
          style={{ marginTop: 16 }}
        />
      )}
    </Card>
  )
}

// FAQ列表组件
export const FAQList: React.FC<{
  category?: string
}> = ({ category }) => {
  const [loading, setLoading] = useState(false)
  const [faqs, setFaqs] = useState<FAQ[]>([])
  const [page, setPage] = useState(1)

  useEffect(() => {
    loadFAQs()
  }, [category, page])

  const loadFAQs = async () => {
    try {
      setLoading(true)
      const response = await ApiService.getFAQs(category, page, 10)
      if (response.code === 200) {
        setFaqs(response.data?.list || [])
      }
    } catch (error: any) {
      message.error(error.message || '加载FAQ失败')
    } finally {
      setLoading(false)
    }
  }

  const handleMarkHelpful = async (faqId: number) => {
    try {
      await ApiService.markFAQHelpful(faqId)
      message.success('感谢您的反馈')
      await loadFAQs()
    } catch (error: any) {
      message.error(error.message || '操作失败')
    }
  }

  return (
    <div>
      {faqs.length === 0 ? (
        <Empty description="暂无常见问题" />
      ) : (
        <List
          loading={loading}
          dataSource={faqs}
          renderItem={(faq) => (
            <List.Item
              actions={[
                <Button
                  type="link"
                  icon={<CheckCircleOutlined />}
                  onClick={() => handleMarkHelpful(faq.id)}
                >
                  有用 ({faq.helpfulCount})
                </Button>,
              ]}
            >
              <List.Item.Meta
                title={
                  <div>
                    <span style={{ fontWeight: 'bold' }}>{faq.question}</span>
                    {faq.category && (
                      <Tag style={{ marginLeft: 8 }}>{faq.category}</Tag>
                    )}
                  </div>
                }
                description={
                  <div>
                    <div style={{ marginTop: 8 }}>{faq.answer}</div>
                    <div style={{ marginTop: 8, color: '#999', fontSize: 12 }}>
                      查看次数: {faq.viewCount}
                    </div>
                  </div>
                }
              />
            </List.Item>
          )}
        />
      )}
    </div>
  )
}

