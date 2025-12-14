import React, { useState, useEffect } from 'react'
import { 
  Card, 
  Row, 
  Col, 
  Button, 
  Space, 
  Typography, 
  List, 
  Tag,
  Progress,
  Statistic,
  Table,
  Modal,
  Form,
  Input,
  InputNumber,
  Select,
  message,
  Empty,
  Spin,
  Avatar,
  Badge
} from 'antd'
import { 
  RecycleOutlined, 
  TrophyOutlined,
  GiftOutlined,
  EnvironmentOutlined,
  PlusOutlined,
  EyeOutlined,
  CheckCircleOutlined,
  ClockCircleOutlined,
  CloseCircleOutlined
} from '@ant-design/icons'
import { useStore } from '../store/useStore'
import { ApiService } from '../services/api'

const { Title, Paragraph, Text } = Typography
const { Option } = Select

interface RecycleOrder {
  id: number
  userId: number
  productId: number
  productName: string
  productImage: string
  quantity: number
  recyclePrice: number
  status: string
  reason: string
  address: string
  contact: string
  remark: string
  reviewTime: string
  completeTime: string
  createTime: string
  updateTime: string
}

interface RecycleStats {
  userId: number
  totalRecycles: number
  totalValue: number
  monthlyRecycles: number
  monthlyValue: number
  ecoPoints: number
  ecoLevel: string
  lastUpdate: string
}

interface EcoActivity {
  id: number
  name: string
  description: string
  type: string
  status: string
  startTime: string
  endTime: string
  participantCount: number
  rewardPoints: number
  imageUrl: string
  createTime: string
  updateTime: string
}

interface UserEcoRanking {
  userId: number
  username: string
  avatar: string
  ecoPoints: number
  ranking: number
  ecoLevel: string
}

const RecyclePage: React.FC = () => {
  const { user, addNotification } = useStore()
  const [loading, setLoading] = useState(true)
  const [stats, setStats] = useState<RecycleStats | null>(null)
  const [orders, setOrders] = useState<RecycleOrder[]>([])
  const [activities, setActivities] = useState<EcoActivity[]>([])
  const [ranking, setRanking] = useState<UserEcoRanking[]>([])
  const [createOrderModalVisible, setCreateOrderModalVisible] = useState(false)
  const [form] = Form.useForm()

  const currentUserId = user?.id || 1

  useEffect(() => {
    loadPageData()
  }, [])

  const loadPageData = async () => {
    try {
      setLoading(true)
      const response = await ApiService.getRecyclePageData(currentUserId)
      
      if (response.code === 200) {
        const data = response.data
        setStats(data.userStats)
        setOrders(data.recycleOrders)
        setActivities(data.ecoActivities)
        setRanking(data.ecoRanking)
      }
    } catch (error) {
      console.error('加载价值循环页面数据失败:', error)
      message.error('加载数据失败')
    } finally {
      setLoading(false)
    }
  }

  const handleCreateOrder = async (values: any) => {
    try {
      const orderData = {
        userId: currentUserId,
        productId: values.productId,
        productName: values.productName,
        productImage: values.productImage,
        quantity: values.quantity,
        recyclePrice: values.recyclePrice,
        reason: values.reason,
        address: values.address,
        contact: values.contact,
        remark: values.remark
      }

      const response = await ApiService.createRecycleOrder(orderData)
      
      if (response.code === 200) {
        message.success('回收订单创建成功')
        setCreateOrderModalVisible(false)
        form.resetFields()
        loadPageData()
      } else {
        message.error(response.message)
      }
    } catch (error) {
      console.error('创建回收订单失败:', error)
      message.error('创建回收订单失败')
    }
  }

  const handleJoinActivity = async (activityId: number) => {
    try {
      const response = await ApiService.joinEcoActivity(activityId, currentUserId)
      
      if (response.code === 200) {
        message.success('参与活动成功')
        loadPageData()
      } else {
        message.error(response.message)
      }
    } catch (error) {
      console.error('参与活动失败:', error)
      message.error('参与活动失败')
    }
  }

  const getStatusTag = (status: string) => {
    const statusMap = {
      'PENDING': { color: 'orange', text: '待审核', icon: <ClockCircleOutlined /> },
      'APPROVED': { color: 'blue', text: '已审核', icon: <CheckCircleOutlined /> },
      'REJECTED': { color: 'red', text: '已拒绝', icon: <CloseCircleOutlined /> },
      'COMPLETED': { color: 'green', text: '已完成', icon: <CheckCircleOutlined /> }
    }
    
    const statusInfo = statusMap[status as keyof typeof statusMap] || statusMap.PENDING
    
    return (
      <Tag color={statusInfo.color} icon={statusInfo.icon}>
        {statusInfo.text}
      </Tag>
    )
  }

  const getEcoLevelColor = (level: string) => {
    const levelMap = {
      '环保大师': 'purple',
      '环保达人': 'blue',
      '环保新手': 'green',
      '环保初学者': 'default'
    }
    
    return levelMap[level as keyof typeof levelMap] || 'default'
  }

  const orderColumns = [
    {
      title: '商品信息',
      dataIndex: 'productName',
      key: 'productName',
      render: (text: string, record: RecycleOrder) => (
        <Space>
          <img 
            src={record.productImage || `https://picsum.photos/60/60?random=${record.id}`}
            alt={text}
            style={{ width: 60, height: 60, borderRadius: 8, objectFit: 'cover' }}
          />
          <div>
            <div style={{ fontWeight: 'bold' }}>{text}</div>
            <div style={{ color: '#666', fontSize: '12px' }}>
              数量: {record.quantity} | 回收价: ¥{record.recyclePrice}
            </div>
          </div>
        </Space>
      )
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status: string) => getStatusTag(status)
    },
    {
      title: '申请时间',
      dataIndex: 'createTime',
      key: 'createTime',
      render: (time: string) => new Date(time).toLocaleDateString()
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record: RecycleOrder) => (
        <Button 
          type="link" 
          icon={<EyeOutlined />}
          onClick={() => message.info('查看订单详情功能开发中...')}
        >
          查看详情
        </Button>
      )
    }
  ]

  if (loading) {
    return (
      <div className="loading-spinner">
        <Spin size="large" />
      </div>
    )
  }

  return (
    <div className="recycle-page">
      <Card>
        <Title level={2}>
          <RecycleOutlined /> 价值循环
        </Title>
        <Paragraph>
          通过回收利用，让商品获得第二次生命，为环保事业贡献力量
        </Paragraph>
      </Card>

      {/* 用户统计信息 */}
      {stats && (
        <Card title="我的环保统计" style={{ marginTop: 16 }}>
          <Row gutter={[24, 24]}>
            <Col xs={24} sm={12} md={6}>
              <Statistic
                title="总回收次数"
                value={stats.totalRecycles}
                prefix={<RecycleOutlined />}
              />
            </Col>
            <Col xs={24} sm={12} md={6}>
              <Statistic
                title="总回收价值"
                value={stats.totalValue}
                prefix="¥"
                precision={2}
              />
            </Col>
            <Col xs={24} sm={12} md={6}>
              <Statistic
                title="本月回收"
                value={stats.monthlyRecycles}
                prefix={<EnvironmentOutlined />}
              />
            </Col>
            <Col xs={24} sm={12} md={6}>
              <Statistic
                title="环保积分"
                value={stats.ecoPoints}
                prefix={<TrophyOutlined />}
                suffix={
                  <Tag color={getEcoLevelColor(stats.ecoLevel)}>
                    {stats.ecoLevel}
                  </Tag>
                }
              />
            </Col>
          </Row>
          
          <div style={{ marginTop: 24 }}>
            <Text type="secondary">环保等级进度</Text>
            <Progress 
              percent={(stats.ecoPoints / 1000) * 100} 
              strokeColor={{
                '0%': '#108ee9',
                '100%': '#87d068',
              }}
              format={() => `${stats.ecoPoints}/1000`}
            />
          </div>
        </Card>
      )}

      <Row gutter={[16, 16]} style={{ marginTop: 16 }}>
        {/* 回收订单 */}
        <Col xs={24} lg={16}>
          <Card 
            title="我的回收订单"
            extra={
              <Button 
                type="primary" 
                icon={<PlusOutlined />}
                onClick={() => setCreateOrderModalVisible(true)}
              >
                申请回收
              </Button>
            }
          >
            {orders.length > 0 ? (
              <Table
                columns={orderColumns}
                dataSource={orders}
                rowKey="id"
                pagination={{ pageSize: 5 }}
                size="small"
              />
            ) : (
              <Empty description="暂无回收订单" />
            )}
          </Card>
        </Col>

        {/* 环保活动 */}
        <Col xs={24} lg={8}>
          <Card title="环保活动">
            {activities.length > 0 ? (
              <List
                dataSource={activities}
                renderItem={(activity) => (
                  <List.Item>
                    <List.Item.Meta
                      avatar={
                        <img 
                          src={activity.imageUrl}
                          alt={activity.name}
                          style={{ width: 60, height: 40, borderRadius: 4, objectFit: 'cover' }}
                        />
                      }
                      title={activity.name}
                      description={
                        <div>
                          <div style={{ fontSize: '12px', color: '#666', marginBottom: 4 }}>
                            {activity.description}
                          </div>
                          <Space>
                            <Tag color="blue">{activity.type}</Tag>
                            <Text type="secondary" style={{ fontSize: '12px' }}>
                              {activity.participantCount} 人参与
                            </Text>
                          </Space>
                        </div>
                      }
                    />
                    <Button 
                      type="primary" 
                      size="small"
                      onClick={() => handleJoinActivity(activity.id)}
                    >
                      参与
                    </Button>
                  </List.Item>
                )}
              />
            ) : (
              <Empty description="暂无活动" />
            )}
          </Card>
        </Col>
      </Row>

      {/* 环保排行榜 */}
      <Card title="环保积分排行榜" style={{ marginTop: 16 }}>
        {ranking.length > 0 ? (
          <List
            dataSource={ranking}
            renderItem={(item, index) => (
              <List.Item>
                <List.Item.Meta
                  avatar={
                    <Badge count={item.ranking} color="gold">
                      <Avatar 
                        src={item.avatar}
                        size={40}
                        icon={<TrophyOutlined />}
                      />
                    </Badge>
                  }
                  title={item.username}
                  description={
                    <Space>
                      <Text type="secondary">积分: {item.ecoPoints}</Text>
                      <Tag color={getEcoLevelColor(item.ecoLevel)}>
                        {item.ecoLevel}
                      </Tag>
                    </Space>
                  }
                />
                {index < 3 && (
                  <TrophyOutlined 
                    style={{ 
                      fontSize: '24px', 
                      color: index === 0 ? '#FFD700' : index === 1 ? '#C0C0C0' : '#CD7F32' 
                    }} 
                  />
                )}
              </List.Item>
            )}
          />
        ) : (
          <Empty description="暂无排行榜数据" />
        )}
      </Card>

      {/* 创建回收订单模态框 */}
      <Modal
        title="申请商品回收"
        open={createOrderModalVisible}
        onCancel={() => setCreateOrderModalVisible(false)}
        footer={null}
        width={600}
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={handleCreateOrder}
        >
          <Form.Item
            name="productName"
            label="商品名称"
            rules={[{ required: true, message: '请输入商品名称' }]}
          >
            <Input placeholder="请输入商品名称" />
          </Form.Item>

          <Form.Item
            name="quantity"
            label="回收数量"
            rules={[{ required: true, message: '请输入回收数量' }]}
          >
            <InputNumber min={1} placeholder="请输入回收数量" style={{ width: '100%' }} />
          </Form.Item>

          <Form.Item
            name="recyclePrice"
            label="期望回收价格"
            rules={[{ required: true, message: '请输入期望回收价格' }]}
          >
            <InputNumber 
              min={0} 
              step={0.01}
              placeholder="请输入期望回收价格" 
              style={{ width: '100%' }} 
            />
          </Form.Item>

          <Form.Item
            name="reason"
            label="回收原因"
            rules={[{ required: true, message: '请输入回收原因' }]}
          >
            <Input.TextArea 
              rows={3} 
              placeholder="请说明回收原因，如：商品损坏、不再需要等" 
            />
          </Form.Item>

          <Form.Item
            name="address"
            label="回收地址"
            rules={[{ required: true, message: '请输入回收地址' }]}
          >
            <Input.TextArea 
              rows={2} 
              placeholder="请输入详细的回收地址" 
            />
          </Form.Item>

          <Form.Item
            name="contact"
            label="联系方式"
            rules={[{ required: true, message: '请输入联系方式' }]}
          >
            <Input placeholder="请输入手机号码或微信号" />
          </Form.Item>

          <Form.Item
            name="remark"
            label="备注"
          >
            <Input.TextArea 
              rows={2} 
              placeholder="其他需要说明的信息" 
            />
          </Form.Item>

          <Form.Item>
            <Space>
              <Button type="primary" htmlType="submit">
                提交申请
              </Button>
              <Button onClick={() => setCreateOrderModalVisible(false)}>
                取消
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default RecyclePage
