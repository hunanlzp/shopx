import React, { useState, useRef, useEffect, useCallback, useMemo } from 'react'
import { 
  Card, 
  Button, 
  Space, 
  Typography, 
  Tag, 
  Tooltip, 
  Spin, 
  Empty,
  Badge,
  Dropdown,
  Menu,
  Modal,
  Form,
  Select,
  Switch,
  Slider,
  message,
  Tabs,
  Divider,
  Progress,
  Alert,
  Row,
  Col,
  Statistic,
  Timeline,
  List,
  Avatar,
  Rate,
  Steps,
  Descriptions,
  Table,
  InputNumber,
  DatePicker,
  Upload,
  Image
} from 'antd'
import {
  RecycleOutlined,
  EnvironmentOutlined,
  TrophyOutlined,
  GiftOutlined,
  StarOutlined,
  HeartOutlined,
  ShoppingCartOutlined,
  EyeOutlined,
  ShareAltOutlined,
  SettingOutlined,
  ReloadOutlined,
  FilterOutlined,
  SortAscendingOutlined,
  SortDescendingOutlined,
  CrownOutlined,
  FireOutlined,
  BulbOutlined,
  ThunderboltOutlined,
  RocketOutlined,
  TargetOutlined,
  BarChartOutlined,
  LineChartOutlined,
  PieChartOutlined,
  InfoCircleOutlined,
  QuestionCircleOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  ExclamationCircleOutlined,
  PlusOutlined,
  MinusOutlined,
  CalendarOutlined,
  ClockCircleOutlined,
  DollarOutlined,
  PercentageOutlined,
  TeamOutlined,
  GlobalOutlined,
  SafetyOutlined,
  ExperimentOutlined,
  FileTextOutlined,
  PictureOutlined,
  UploadOutlined,
  DownloadOutlined,
  EditOutlined,
  DeleteOutlined,
  CheckOutlined,
  CloseOutlined
} from '@ant-design/icons'
import { motion, AnimatePresence } from 'framer-motion'
import { useStore } from '../store/useStore'
import { ApiService } from '../services/api'
import { formatTime, formatNumber } from '../utils/utils'
import CommunityTab from './CommunityTab'

const { Title, Paragraph, Text } = Typography
const { TabPane } = Tabs
const { Step } = Steps
const { RangePicker } = DatePicker

interface RecycleOrder {
  id: number
  userId: number
  productId: number
  productName: string
  productImage?: string
  quantity: number
  status: 'PENDING' | 'SCHEDULED' | 'COMPLETED' | 'CANCELLED'
  estimatedValue: number
  actualValue?: number
  pickupDate?: string
  completionDate?: string
  createTime: string
  updateTime: string
  notes?: string
  images?: string[]
}

interface EcoActivity {
  id: number
  title: string
  description: string
  type: 'CHALLENGE' | 'EVENT' | 'EDUCATION'
  status: 'UPCOMING' | 'ONGOING' | 'COMPLETED'
  startDate: string
  endDate: string
  participants: number
  maxParticipants?: number
  rewards?: string[]
  requirements?: string[]
  image?: string
  category: string
  difficulty: 'EASY' | 'MEDIUM' | 'HARD'
  points: number
}

interface RecycleStats {
  totalOrders: number
  totalValue: number
  sustainabilityScore: number
  ecoLevel: string
  carbonSaved: number
  treesPlanted: number
  waterSaved: number
  energySaved: number
  monthlyTrend: Array<{
    month: string
    orders: number
    value: number
    score: number
  }>
}

interface RecycleProps {
  userId: number
  onProductClick?: (product: any) => void
  onAddToCart?: (product: any) => void
  onLike?: (product: any) => void
  onShare?: (product: any) => void
  className?: string
}

// 回收订单卡片组件
const RecycleOrderCard: React.FC<{
  order: RecycleOrder
  onStatusUpdate?: (orderId: number, status: string) => void
  onCancel?: (orderId: number) => void
  onView?: (orderId: number) => void
}> = ({ order, onStatusUpdate, onCancel, onView }) => {
  const getStatusColor = (status: string) => {
    switch (status) {
      case 'PENDING': return 'orange'
      case 'SCHEDULED': return 'blue'
      case 'COMPLETED': return 'green'
      case 'CANCELLED': return 'red'
      default: return 'gray'
    }
  }

  const getStatusText = (status: string) => {
    switch (status) {
      case 'PENDING': return '待处理'
      case 'SCHEDULED': return '已安排'
      case 'COMPLETED': return '已完成'
      case 'CANCELLED': return '已取消'
      default: return '未知'
    }
  }

  return (
    <motion.div
      whileHover={{ scale: 1.02 }}
      whileTap={{ scale: 0.98 }}
    >
      <Card
        hoverable
        style={{ marginBottom: '16px' }}
        actions={[
          <Tooltip title="查看详情">
            <Button 
              size="small" 
              icon={<EyeOutlined />}
              onClick={() => onView?.(order.id)}
            />
          </Tooltip>,
          <Tooltip title="更新状态">
            <Button 
              size="small" 
              icon={<EditOutlined />}
              onClick={() => onStatusUpdate?.(order.id, 'COMPLETED')}
            />
          </Tooltip>,
          <Tooltip title="取消订单">
            <Button 
              size="small" 
              icon={<CloseOutlined />}
              danger
              onClick={() => onCancel?.(order.id)}
            />
          </Tooltip>
        ]}
      >
        <div style={{ display: 'flex', gap: '12px' }}>
          <div style={{ flexShrink: 0 }}>
            {order.productImage ? (
              <Image
                width={80}
                height={80}
                src={order.productImage}
                alt={order.productName}
                style={{ borderRadius: '8px' }}
              />
            ) : (
              <div style={{
                width: 80,
                height: 80,
                background: '#f0f0f0',
                borderRadius: '8px',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center'
              }}>
                <RecycleOutlined style={{ fontSize: '24px', color: '#999' }} />
              </div>
            )}
          </div>
          
          <div style={{ flex: 1 }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '8px' }}>
              <Title level={5} style={{ margin: 0 }}>
                {order.productName}
              </Title>
              <Tag color={getStatusColor(order.status)}>
                {getStatusText(order.status)}
              </Tag>
            </div>
            
            <div style={{ marginBottom: '8px' }}>
              <Text type="secondary">数量: {order.quantity}</Text>
              <Divider type="vertical" />
              <Text type="secondary">预估价值: {formatNumber.price(order.estimatedValue)}</Text>
              {order.actualValue && (
                <>
                  <Divider type="vertical" />
                  <Text type="secondary">实际价值: {formatNumber.price(order.actualValue)}</Text>
                </>
              )}
            </div>
            
            <div style={{ fontSize: '12px', color: '#999' }}>
              <div>创建时间: {formatTime.fromNow(order.createTime)}</div>
              {order.pickupDate && (
                <div>取件时间: {formatTime.fromNow(order.pickupDate)}</div>
              )}
              {order.completionDate && (
                <div>完成时间: {formatTime.fromNow(order.completionDate)}</div>
              )}
            </div>
          </div>
        </div>
      </Card>
    </motion.div>
  )
}

// 环保活动卡片组件
const EcoActivityCard: React.FC<{
  activity: EcoActivity
  onJoin?: (activityId: number) => void
  onView?: (activityId: number) => void
}> = ({ activity, onJoin, onView }) => {
  const getTypeColor = (type: string) => {
    switch (type) {
      case 'CHALLENGE': return 'red'
      case 'EVENT': return 'blue'
      case 'EDUCATION': return 'green'
      default: return 'gray'
    }
  }

  const getTypeText = (type: string) => {
    switch (type) {
      case 'CHALLENGE': return '挑战'
      case 'EVENT': return '活动'
      case 'EDUCATION': return '教育'
      default: return '未知'
    }
  }

  const getDifficultyColor = (difficulty: string) => {
    switch (difficulty) {
      case 'EASY': return 'green'
      case 'MEDIUM': return 'orange'
      case 'HARD': return 'red'
      default: return 'gray'
    }
  }

  const getDifficultyText = (difficulty: string) => {
    switch (difficulty) {
      case 'EASY': return '简单'
      case 'MEDIUM': return '中等'
      case 'HARD': return '困难'
      default: return '未知'
    }
  }

  const isJoinable = activity.status === 'UPCOMING' || activity.status === 'ONGOING'
  const isFull = activity.maxParticipants && activity.participants >= activity.maxParticipants

  return (
    <motion.div
      whileHover={{ scale: 1.02 }}
      whileTap={{ scale: 0.98 }}
    >
      <Card
        hoverable
        cover={
          activity.image ? (
            <Image
              height={200}
              src={activity.image}
              alt={activity.title}
              style={{ objectFit: 'cover' }}
            />
          ) : (
            <div style={{
              height: 200,
              background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: 'white'
            }}>
              <EnvironmentOutlined style={{ fontSize: '48px' }} />
            </div>
          )
        }
        actions={[
          <Tooltip title="查看详情">
            <Button 
              size="small" 
              icon={<EyeOutlined />}
              onClick={() => onView?.(activity.id)}
            />
          </Tooltip>,
          <Tooltip title={isJoinable && !isFull ? '参加活动' : '无法参加'}>
            <Button 
              size="small" 
              icon={<PlusOutlined />}
              type="primary"
              disabled={!isJoinable || isFull}
              onClick={() => onJoin?.(activity.id)}
            >
              {isFull ? '已满' : '参加'}
            </Button>
          </Tooltip>
        ]}
      >
        <Card.Meta
          title={
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <span>{activity.title}</span>
              <Space>
                <Tag color={getTypeColor(activity.type)}>
                  {getTypeText(activity.type)}
                </Tag>
                <Tag color={getDifficultyColor(activity.difficulty)}>
                  {getDifficultyText(activity.difficulty)}
                </Tag>
              </Space>
            </div>
          }
          description={
            <div>
              <Paragraph 
                ellipsis={{ rows: 2 }} 
                style={{ margin: '8px 0' }}
              >
                {activity.description}
              </Paragraph>
              
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '8px' }}>
                <div>
                  <Text type="secondary">
                    <TeamOutlined /> {activity.participants}
                    {activity.maxParticipants && `/${activity.maxParticipants}`}
                  </Text>
                  <Divider type="vertical" />
                  <Text type="secondary">
                    <StarOutlined /> {activity.points} 积分
                  </Text>
                </div>
                
                <div style={{ fontSize: '12px', color: '#999' }}>
                  {formatTime.fromNow(activity.startDate)}
                </div>
              </div>
            </div>
          }
        />
      </Card>
    </motion.div>
  )
}

// 回收统计组件
const RecycleStatsComponent: React.FC<{
  stats: RecycleStats
}> = ({ stats }) => {
  const getEcoLevelColor = (level: string) => {
    switch (level) {
      case 'Bronze': return '#cd7f32'
      case 'Silver': return '#c0c0c0'
      case 'Gold': return '#ffd700'
      case 'Platinum': return '#e5e4e2'
      case 'Diamond': return '#b9f2ff'
      default: return '#999'
    }
  }

  return (
    <div>
      <Row gutter={16} style={{ marginBottom: '24px' }}>
        <Col span={6}>
          <Card>
            <Statistic
              title="总订单数"
              value={stats.totalOrders}
              valueStyle={{ color: '#1890ff' }}
              prefix={<RecycleOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="总价值"
              value={stats.totalValue}
              prefix="¥"
              valueStyle={{ color: '#52c41a' }}
              prefix={<DollarOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="环保分数"
              value={stats.sustainabilityScore}
              valueStyle={{ color: '#faad14' }}
              prefix={<TrophyOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="环保等级"
              value={stats.ecoLevel}
              valueStyle={{ color: getEcoLevelColor(stats.ecoLevel) }}
              prefix={<CrownOutlined />}
            />
          </Card>
        </Col>
      </Row>

      <Row gutter={16} style={{ marginBottom: '24px' }}>
        <Col span={6}>
          <Card>
            <Statistic
              title="减少碳排放"
              value={stats.carbonSaved}
              suffix="kg"
              valueStyle={{ color: '#13c2c2' }}
              prefix={<EnvironmentOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="种植树木"
              value={stats.treesPlanted}
              suffix="棵"
              valueStyle={{ color: '#52c41a' }}
              prefix={<EnvironmentOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="节约用水"
              value={stats.waterSaved}
              suffix="L"
              valueStyle={{ color: '#1890ff' }}
              prefix={<EnvironmentOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="节约能源"
              value={stats.energySaved}
              suffix="kWh"
              valueStyle={{ color: '#fa8c16' }}
              prefix={<ThunderboltOutlined />}
            />
          </Card>
        </Col>
      </Row>

      <Card title="月度趋势" style={{ marginBottom: '24px' }}>
        <Table
          size="small"
          dataSource={stats.monthlyTrend}
          columns={[
            {
              title: '月份',
              dataIndex: 'month',
              key: 'month',
            },
            {
              title: '订单数',
              dataIndex: 'orders',
              key: 'orders',
            },
            {
              title: '价值',
              dataIndex: 'value',
              key: 'value',
              render: (value) => formatNumber.price(value),
            },
            {
              title: '分数',
              dataIndex: 'score',
              key: 'score',
            },
          ]}
          pagination={false}
        />
      </Card>
    </div>
  )
}

// 创建回收订单表单组件
const CreateRecycleOrderForm: React.FC<{
  visible: boolean
  onClose: () => void
  onSubmit: (orderData: any) => void
}> = ({ visible, onClose, onSubmit }) => {
  const [form] = Form.useForm()

  const handleSubmit = () => {
    form.validateFields().then(values => {
      onSubmit(values)
      form.resetFields()
      onClose()
    })
  }

  return (
    <Modal
      title="创建回收订单"
      open={visible}
      onCancel={onClose}
      onOk={handleSubmit}
      width={600}
    >
      <Form form={form} layout="vertical">
        <Form.Item
          label="商品名称"
          name="productName"
          rules={[{ required: true, message: '请输入商品名称' }]}
        >
          <Input placeholder="请输入要回收的商品名称" />
        </Form.Item>
        
        <Form.Item
          label="数量"
          name="quantity"
          rules={[{ required: true, message: '请输入数量' }]}
        >
          <InputNumber min={1} max={100} style={{ width: '100%' }} />
        </Form.Item>
        
        <Form.Item
          label="预估价值"
          name="estimatedValue"
          rules={[{ required: true, message: '请输入预估价值' }]}
        >
          <InputNumber min={0} max={10000} style={{ width: '100%' }} />
        </Form.Item>
        
        <Form.Item
          label="取件日期"
          name="pickupDate"
        >
          <DatePicker style={{ width: '100%' }} />
        </Form.Item>
        
        <Form.Item
          label="备注"
          name="notes"
        >
          <TextArea rows={3} placeholder="请输入备注信息" />
        </Form.Item>
        
        <Form.Item
          label="商品图片"
          name="images"
        >
          <Upload
            listType="picture-card"
            multiple
            beforeUpload={() => false}
          >
            <div>
              <PlusOutlined />
              <div style={{ marginTop: 8 }}>上传图片</div>
            </div>
          </Upload>
        </Form.Item>
      </Form>
    </Modal>
  )
}

// 增强的价值循环主组件
const EnhancedRecycle: React.FC<RecycleProps> = ({
  userId,
  onProductClick,
  onAddToCart,
  onLike,
  onShare,
  className
}) => {
  const { user } = useStore()
  const [activeTab, setActiveTab] = useState('orders')
  const [orders, setOrders] = useState<RecycleOrder[]>([])
  const [activities, setActivities] = useState<EcoActivity[]>([])
  const [stats, setStats] = useState<RecycleStats>({
    totalOrders: 0,
    totalValue: 0,
    sustainabilityScore: 0,
    ecoLevel: 'Bronze',
    carbonSaved: 0,
    treesPlanted: 0,
    waterSaved: 0,
    energySaved: 0,
    monthlyTrend: []
  })
  const [loading, setLoading] = useState(false)
  const [showCreateForm, setShowCreateForm] = useState(false)
  const [showSettings, setShowSettings] = useState(false)

  // 加载回收订单
  const loadOrders = useCallback(async () => {
    setLoading(true)
    try {
      const response = await ApiService.getUserRecycleOrders(userId)
      setOrders(response.data || [])
    } catch (error) {
      console.error('Failed to load orders:', error)
    } finally {
      setLoading(false)
    }
  }, [userId])

  // 加载环保活动
  const loadActivities = useCallback(async () => {
    try {
      const response = await ApiService.getEcoActivities()
      setActivities(response.data || [])
    } catch (error) {
      console.error('Failed to load activities:', error)
    }
  }, [])

  // 加载统计数据
  const loadStats = useCallback(async () => {
    try {
      const response = await ApiService.getUserRecycleStats(userId)
      setStats(response.data || stats)
    } catch (error) {
      console.error('Failed to load stats:', error)
    }
  }, [userId, stats])

  useEffect(() => {
    loadOrders()
    loadActivities()
    loadStats()
  }, [loadOrders, loadActivities, loadStats])

  // 创建回收订单
  const handleCreateOrder = useCallback(async (orderData: any) => {
    try {
      await ApiService.createRecycleOrder({
        userId,
        productName: orderData.productName,
        quantity: orderData.quantity,
        estimatedValue: orderData.estimatedValue,
        pickupDate: orderData.pickupDate,
        notes: orderData.notes
      })
      message.success('回收订单创建成功')
      loadOrders()
    } catch (error) {
      console.error('Failed to create order:', error)
      message.error('创建失败')
    }
  }, [userId, loadOrders])

  // 更新订单状态
  const handleStatusUpdate = useCallback(async (orderId: number, status: string) => {
    try {
      await ApiService.updateRecycleOrderStatus(orderId, status)
      message.success('状态更新成功')
      loadOrders()
    } catch (error) {
      console.error('Failed to update status:', error)
      message.error('更新失败')
    }
  }, [loadOrders])

  // 取消订单
  const handleCancelOrder = useCallback(async (orderId: number) => {
    try {
      await ApiService.updateRecycleOrderStatus(orderId, 'CANCELLED')
      message.success('订单已取消')
      loadOrders()
    } catch (error) {
      console.error('Failed to cancel order:', error)
      message.error('取消失败')
    }
  }, [loadOrders])

  // 参加环保活动
  const handleJoinActivity = useCallback(async (activityId: number) => {
    try {
      await ApiService.joinEcoActivity(activityId, userId)
      message.success('参加活动成功')
      loadActivities()
    } catch (error) {
      console.error('Failed to join activity:', error)
      message.error('参加失败')
    }
  }, [userId, loadActivities])

  return (
    <div className={className}>
      <Card
        title={
          <Space>
            <RecycleOutlined style={{ color: '#52c41a' }} />
            <span>价值循环</span>
            <Badge count={orders.length} showZero color="#52c41a" />
          </Space>
        }
        extra={
          <Space>
            <Tooltip title="创建回收订单">
              <Button 
                type="primary"
                icon={<PlusOutlined />}
                onClick={() => setShowCreateForm(true)}
              >
                创建订单
              </Button>
            </Tooltip>
            <Tooltip title="设置">
              <Button 
                icon={<SettingOutlined />} 
                size="small"
                onClick={() => setShowSettings(true)}
              />
            </Tooltip>
            <Tooltip title="刷新">
              <Button 
                icon={<ReloadOutlined />} 
                size="small"
                onClick={() => {
                  loadOrders()
                  loadActivities()
                  loadStats()
                }}
              />
            </Tooltip>
          </Space>
        }
        style={{ height: '600px' }}
        bodyStyle={{ 
          height: '500px', 
          display: 'flex', 
          flexDirection: 'column',
          padding: '16px'
        }}
      >
        <Tabs 
          activeKey={activeTab} 
          onChange={setActiveTab}
          style={{ flex: 1, display: 'flex', flexDirection: 'column' }}
        >
          <TabPane tab="我的订单" key="orders">
            <div style={{ flex: 1, overflowY: 'auto' }}>
              {loading ? (
                <div style={{ textAlign: 'center', padding: '40px 0' }}>
                  <Spin size="large" />
                </div>
              ) : orders.length === 0 ? (
                <Empty
                  description="暂无回收订单"
                  image={Empty.PRESENTED_IMAGE_SIMPLE}
                >
                  <Button 
                    type="primary" 
                    icon={<PlusOutlined />}
                    onClick={() => setShowCreateForm(true)}
                  >
                    创建第一个订单
                  </Button>
                </Empty>
              ) : (
                <div>
                  {orders.map((order) => (
                    <RecycleOrderCard
                      key={order.id}
                      order={order}
                      onStatusUpdate={handleStatusUpdate}
                      onCancel={handleCancelOrder}
                    />
                  ))}
                </div>
              )}
            </div>
          </TabPane>

          <TabPane tab="环保活动" key="activities">
            <div style={{ flex: 1, overflowY: 'auto' }}>
              {activities.length === 0 ? (
                <Empty
                  description="暂无环保活动"
                  image={Empty.PRESENTED_IMAGE_SIMPLE}
                />
              ) : (
                <Row gutter={[16, 16]}>
                  {activities.map((activity) => (
                    <Col span={12} key={activity.id}>
                      <EcoActivityCard
                        activity={activity}
                        onJoin={handleJoinActivity}
                      />
                    </Col>
                  ))}
                </Row>
              )}
            </div>
          </TabPane>

          <TabPane tab="统计信息" key="stats">
            <div style={{ flex: 1, overflowY: 'auto' }}>
              <RecycleStatsComponent stats={stats} />
            </div>
          </TabPane>

          <TabPane tab="环保等级" key="level">
            <div style={{ flex: 1, overflowY: 'auto', textAlign: 'center', padding: '40px 0' }}>
              <Card style={{ maxWidth: '400px', margin: '0 auto' }}>
                <div style={{ marginBottom: '24px' }}>
                  <Avatar 
                    size={80} 
                    icon={<CrownOutlined />}
                    style={{ backgroundColor: '#faad14' }}
                  />
                </div>
                
                <Title level={2} style={{ color: '#faad14' }}>
                  {stats.ecoLevel} 等级
                </Title>
                
                <Progress
                  type="circle"
                  percent={stats.sustainabilityScore}
                  format={() => `${stats.sustainabilityScore}分`}
                  size={120}
                />
                
                <div style={{ marginTop: '24px' }}>
                  <Text type="secondary">
                    继续参与环保活动，提升您的环保等级！
                  </Text>
                </div>
                
                <div style={{ marginTop: '16px' }}>
                  <Button type="primary" size="large">
                    查看升级条件
                  </Button>
                </div>
              </Card>
            </div>
          </TabPane>

          <TabPane tab="社区" key="community">
            <div style={{ flex: 1, overflowY: 'auto', padding: '16px' }}>
              <CommunityTab userId={userId} />
            </div>
          </TabPane>
        </Tabs>
      </Card>

      {/* 创建订单表单 */}
      <CreateRecycleOrderForm
        visible={showCreateForm}
        onClose={() => setShowCreateForm(false)}
        onSubmit={handleCreateOrder}
      />

      {/* 设置模态框 */}
      <Modal
        title="回收设置"
        open={showSettings}
        onCancel={() => setShowSettings(false)}
        footer={null}
        width={500}
      >
        <Form layout="vertical">
          <Form.Item label="通知设置">
            <Space direction="vertical">
              <div>
                <Switch defaultChecked /> 订单状态通知
              </div>
              <div>
                <Switch defaultChecked /> 活动通知
              </div>
              <div>
                <Switch /> 环保等级通知
              </div>
            </Space>
          </Form.Item>
          
          <Form.Item label="隐私设置">
            <Space direction="vertical">
              <div>
                <Switch defaultChecked /> 公开环保分数
              </div>
              <div>
                <Switch defaultChecked /> 公开参与活动
              </div>
              <div>
                <Switch /> 公开回收记录
              </div>
            </Space>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default EnhancedRecycle
