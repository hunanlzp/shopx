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
  Rate
} from 'antd'
import {
  BulbOutlined,
  ThunderboltOutlined,
  FireOutlined,
  GiftOutlined,
  ExperimentOutlined,
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
  TrophyOutlined,
  RocketOutlined,
  TargetOutlined,
  BarChartOutlined,
  LineChartOutlined,
  PieChartOutlined,
  InfoCircleOutlined,
  QuestionCircleOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  ExclamationCircleOutlined
} from '@ant-design/icons'
import { motion, AnimatePresence } from 'framer-motion'
import { useStore } from '../store/useStore'
import { ApiService } from '../services/api'
import { useProducts, useHotProducts, useSearchProducts } from '../hooks/useHooks'
import { ProductCard, ProductList } from './ProductComponents'
import { formatTime, formatNumber } from '../utils/utils'

const { Title, Paragraph, Text } = Typography
const { TabPane } = Tabs

interface RecommendationSession {
  id: string
  userId: number
  scenario?: string
  lifestyle?: string
  products: any[]
  timestamp: string
  score?: number
  confidence?: number
  algorithm?: string
}

interface RecommendationProps {
  userId: number
  onProductClick?: (product: any) => void
  onAddToCart?: (product: any) => void
  onLike?: (product: any) => void
  onShare?: (product: any) => void
  className?: string
}

// 推荐算法卡片组件
const AlgorithmCard: React.FC<{
  algorithm: string
  description: string
  accuracy: number
  speed: number
  isActive: boolean
  onClick: () => void
}> = ({ algorithm, description, accuracy, speed, isActive, onClick }) => {
  return (
    <motion.div
      whileHover={{ scale: 1.02 }}
      whileTap={{ scale: 0.98 }}
    >
      <Card
        hoverable
        onClick={onClick}
        style={{
          border: isActive ? '2px solid #1890ff' : '1px solid #d9d9d9',
          background: isActive ? '#f0f9ff' : 'white'
        }}
      >
        <div style={{ textAlign: 'center' }}>
          <Title level={4} style={{ margin: '0 0 8px 0' }}>
            {algorithm}
          </Title>
          <Paragraph type="secondary" style={{ margin: '0 0 16px 0' }}>
            {description}
          </Paragraph>
          
          <Row gutter={16}>
            <Col span={12}>
              <Statistic
                title="准确率"
                value={accuracy}
                suffix="%"
                valueStyle={{ color: '#52c41a', fontSize: '16px' }}
              />
            </Col>
            <Col span={12}>
              <Statistic
                title="响应速度"
                value={speed}
                suffix="ms"
                valueStyle={{ color: '#1890ff', fontSize: '16px' }}
              />
            </Col>
          </Row>
          
          {isActive && (
            <Badge 
              status="processing" 
              text="当前使用" 
              style={{ marginTop: '8px' }}
            />
          )}
        </div>
      </Card>
    </motion.div>
  )
}

// 推荐场景选择组件
const ScenarioSelector: React.FC<{
  scenarios: string[]
  selectedScenario: string
  onScenarioChange: (scenario: string) => void
}> = ({ scenarios, selectedScenario, onScenarioChange }) => {
  const scenarioIcons = {
    '工作': <TargetOutlined />,
    '运动': <ThunderboltOutlined />,
    '休闲': <GiftOutlined />,
    '学习': <BulbOutlined />,
    '旅行': <RocketOutlined />,
    '居家': <CrownOutlined />,
    '约会': <HeartOutlined />,
    '聚会': <StarOutlined />
  }

  return (
    <div style={{ marginBottom: '16px' }}>
      <Title level={5} style={{ marginBottom: '12px' }}>选择场景</Title>
      <div style={{ display: 'flex', flexWrap: 'wrap', gap: '8px' }}>
        {scenarios.map((scenario) => (
          <Button
            key={scenario}
            type={selectedScenario === scenario ? 'primary' : 'default'}
            icon={scenarioIcons[scenario as keyof typeof scenarioIcons]}
            onClick={() => onScenarioChange(scenario)}
            style={{ minWidth: '80px' }}
          >
            {scenario}
          </Button>
        ))}
      </div>
    </div>
  )
}

// 推荐结果组件
const RecommendationResults: React.FC<{
  recommendations: any[]
  loading: boolean
  algorithm: string
  confidence: number
  onProductClick?: (product: any) => void
  onAddToCart?: (product: any) => void
  onLike?: (product: any) => void
  onShare?: (product: any) => void
}> = ({ recommendations, loading, algorithm, confidence, onProductClick, onAddToCart, onLike, onShare }) => {
  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '40px 0' }}>
        <Spin size="large" />
        <div style={{ marginTop: '16px' }}>
          <Text type="secondary">AI正在分析您的偏好...</Text>
        </div>
      </div>
    )
  }

  if (recommendations.length === 0) {
    return (
      <Empty
        description="暂无推荐商品"
        image={Empty.PRESENTED_IMAGE_SIMPLE}
      >
        <Button type="primary">
          重新推荐
        </Button>
      </Empty>
    )
  }

  return (
    <div>
      <div style={{ 
        display: 'flex', 
        justifyContent: 'space-between', 
        alignItems: 'center',
        marginBottom: '16px',
        padding: '12px',
        background: '#f0f9ff',
        borderRadius: '8px',
        border: '1px solid #bae7ff'
      }}>
        <div>
          <Text strong>推荐结果</Text>
          <div style={{ fontSize: '12px', color: '#666' }}>
            使用算法: {algorithm} • 置信度: {Math.round(confidence * 100)}%
          </div>
        </div>
        <Badge count={recommendations.length} showZero color="#52c41a" />
      </div>

      <ProductList
        products={recommendations}
        onProductView={onProductClick}
        onProductAddToCart={onAddToCart}
        onProductLike={onLike}
        onProductShare={onShare}
        showActions={true}
      />
    </div>
  )
}

// 推荐历史组件
const RecommendationHistory: React.FC<{
  history: RecommendationSession[]
  onSessionClick: (session: RecommendationSession) => void
}> = ({ history, onSessionClick }) => {
  return (
    <div>
      <Title level={5} style={{ marginBottom: '16px' }}>推荐历史</Title>
      
      {history.length === 0 ? (
        <Empty
          description="暂无推荐历史"
          image={Empty.PRESENTED_IMAGE_SIMPLE}
        />
      ) : (
        <Timeline>
          {history.map((session, index) => (
            <Timeline.Item
              key={session.id}
              dot={<BulbOutlined style={{ color: '#1890ff' }} />}
            >
              <Card
                size="small"
                hoverable
                onClick={() => onSessionClick(session)}
                style={{ marginBottom: '8px' }}
              >
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <div>
                    <Text strong>
                      {session.scenario || '通用推荐'}
                    </Text>
                    <div style={{ fontSize: '12px', color: '#666' }}>
                      {session.products.length} 个商品 • {formatTime.fromNow(session.timestamp)}
                    </div>
                  </div>
                  <div style={{ textAlign: 'right' }}>
                    {session.confidence && (
                      <Progress
                        type="circle"
                        size={40}
                        percent={Math.round(session.confidence * 100)}
                        format={() => `${Math.round(session.confidence! * 100)}%`}
                      />
                    )}
                  </div>
                </div>
              </Card>
            </Timeline.Item>
          ))}
        </Timeline>
      )}
    </div>
  )
}

// 推荐统计组件
const RecommendationStats: React.FC<{
  userId: number
}> = ({ userId }) => {
  const [stats, setStats] = useState({
    totalRecommendations: 0,
    clickThroughRate: 0,
    conversionRate: 0,
    averageRating: 0,
    favoriteCategories: [],
    recentActivity: []
  })

  useEffect(() => {
    // 模拟加载统计数据
    setStats({
      totalRecommendations: 156,
      clickThroughRate: 23.5,
      conversionRate: 8.2,
      averageRating: 4.3,
      favoriteCategories: ['电子产品', '服装', '家居用品'],
      recentActivity: [
        { action: '查看推荐', time: '2分钟前' },
        { action: '加入购物车', time: '1小时前' },
        { action: '购买商品', time: '3小时前' }
      ]
    })
  }, [userId])

  return (
    <div>
      <Title level={5} style={{ marginBottom: '16px' }}>推荐统计</Title>
      
      <Row gutter={16} style={{ marginBottom: '16px' }}>
        <Col span={8}>
          <Card size="small">
            <Statistic
              title="总推荐数"
              value={stats.totalRecommendations}
              valueStyle={{ color: '#1890ff' }}
            />
          </Card>
        </Col>
        <Col span={8}>
          <Card size="small">
            <Statistic
              title="点击率"
              value={stats.clickThroughRate}
              suffix="%"
              valueStyle={{ color: '#52c41a' }}
            />
          </Card>
        </Col>
        <Col span={8}>
          <Card size="small">
            <Statistic
              title="转化率"
              value={stats.conversionRate}
              suffix="%"
              valueStyle={{ color: '#faad14' }}
            />
          </Card>
        </Col>
      </Row>

      <Card size="small" title="偏好分类" style={{ marginBottom: '16px' }}>
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '8px' }}>
          {stats.favoriteCategories.map((category, index) => (
            <Tag key={index} color="blue">
              {category}
            </Tag>
          ))}
        </div>
      </Card>

      <Card size="small" title="最近活动">
        <List
          size="small"
          dataSource={stats.recentActivity}
          renderItem={(item: any) => (
            <List.Item>
              <List.Item.Meta
                avatar={<Avatar icon={<CheckCircleOutlined />} size="small" />}
                title={item.action}
                description={item.time}
              />
            </List.Item>
          )}
        />
      </Card>
    </div>
  )
}

// 增强的推荐系统主组件
const EnhancedRecommendation: React.FC<RecommendationProps> = ({
  userId,
  onProductClick,
  onAddToCart,
  onLike,
  onShare,
  className
}) => {
  const { user } = useStore()
  const [activeTab, setActiveTab] = useState('scenario')
  const [selectedScenario, setSelectedScenario] = useState('工作')
  const [selectedLifestyle, setSelectedLifestyle] = useState('')
  const [selectedAlgorithm, setSelectedAlgorithm] = useState('collaborative')
  const [recommendations, setRecommendations] = useState<any[]>([])
  const [loading, setLoading] = useState(false)
  const [confidence, setConfidence] = useState(0.85)
  const [history, setHistory] = useState<RecommendationSession[]>([])
  const [showSettings, setShowSettings] = useState(false)

  const scenarios = ['工作', '运动', '休闲', '学习', '旅行', '居家', '约会', '聚会']
  const lifestyles = ['简约', '奢华', '环保', '时尚', '实用', '创新', '传统', '现代']
  
  const algorithms = [
    {
      key: 'collaborative',
      name: '协同过滤',
      description: '基于用户行为相似性推荐',
      accuracy: 85,
      speed: 120
    },
    {
      key: 'content',
      name: '内容推荐',
      description: '基于商品特征相似性推荐',
      accuracy: 78,
      speed: 80
    },
    {
      key: 'hybrid',
      name: '混合推荐',
      description: '结合多种算法的智能推荐',
      accuracy: 92,
      speed: 200
    },
    {
      key: 'deep',
      name: '深度学习',
      description: '基于神经网络的深度推荐',
      accuracy: 88,
      speed: 300
    }
  ]

  // 获取推荐
  const getRecommendations = useCallback(async (type: 'scenario' | 'lifestyle' | 'predict') => {
    setLoading(true)
    
    try {
      let response
      switch (type) {
        case 'scenario':
          response = await ApiService.getScenarioRecommendation(userId, selectedScenario)
          break
        case 'lifestyle':
          response = await ApiService.getLifestyleRecommendation(userId, selectedLifestyle)
          break
        case 'predict':
          response = await ApiService.getPredictRecommendation(userId)
          break
        default:
          response = await ApiService.getScenarioRecommendation(userId, selectedScenario)
      }
      
      setRecommendations(response.data || [])
      setConfidence(response.confidence || 0.85)
      
      // 保存推荐会话
      const session: RecommendationSession = {
        id: Date.now().toString(),
        userId,
        scenario: type === 'scenario' ? selectedScenario : undefined,
        lifestyle: type === 'lifestyle' ? selectedLifestyle : undefined,
        products: response.data || [],
        timestamp: new Date().toISOString(),
        confidence: response.confidence || 0.85,
        algorithm: selectedAlgorithm
      }
      
      setHistory(prev => [session, ...prev.slice(0, 9)]) // 保留最近10条
      
    } catch (error) {
      console.error('Failed to get recommendations:', error)
      message.error('获取推荐失败')
    } finally {
      setLoading(false)
    }
  }, [userId, selectedScenario, selectedLifestyle, selectedAlgorithm])

  // 处理场景推荐
  const handleScenarioRecommendation = useCallback(() => {
    getRecommendations('scenario')
  }, [getRecommendations])

  // 处理生活方式推荐
  const handleLifestyleRecommendation = useCallback(() => {
    getRecommendations('lifestyle')
  }, [getRecommendations])

  // 处理预测推荐
  const handlePredictRecommendation = useCallback(() => {
    getRecommendations('predict')
  }, [getRecommendations])

  // 处理历史会话点击
  const handleSessionClick = useCallback((session: RecommendationSession) => {
    setRecommendations(session.products)
    setConfidence(session.confidence || 0.85)
    setActiveTab('results')
  }, [])

  return (
    <div className={className}>
      <Card
        title={
          <Space>
            <BulbOutlined style={{ color: '#faad14' }} />
            <span>智能推荐</span>
            <Badge count={recommendations.length} showZero color="#52c41a" />
          </Space>
        }
        extra={
          <Space>
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
                onClick={() => getRecommendations('scenario')}
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
          <TabPane tab="场景推荐" key="scenario">
            <div style={{ flex: 1, overflowY: 'auto' }}>
              <ScenarioSelector
                scenarios={scenarios}
                selectedScenario={selectedScenario}
                onScenarioChange={setSelectedScenario}
              />
              
              <div style={{ marginBottom: '16px' }}>
                <Title level={5} style={{ marginBottom: '12px' }}>选择算法</Title>
                <Row gutter={16}>
                  {algorithms.map((algorithm) => (
                    <Col span={12} key={algorithm.key} style={{ marginBottom: '16px' }}>
                      <AlgorithmCard
                        algorithm={algorithm.name}
                        description={algorithm.description}
                        accuracy={algorithm.accuracy}
                        speed={algorithm.speed}
                        isActive={selectedAlgorithm === algorithm.key}
                        onClick={() => setSelectedAlgorithm(algorithm.key)}
                      />
                    </Col>
                  ))}
                </Row>
              </div>
              
              <Button 
                type="primary" 
                size="large" 
                block
                icon={<BulbOutlined />}
                onClick={handleScenarioRecommendation}
                loading={loading}
              >
                获取场景推荐
              </Button>
            </div>
          </TabPane>

          <TabPane tab="生活方式" key="lifestyle">
            <div style={{ flex: 1, overflowY: 'auto' }}>
              <div style={{ marginBottom: '16px' }}>
                <Title level={5} style={{ marginBottom: '12px' }}>选择生活方式</Title>
                <div style={{ display: 'flex', flexWrap: 'wrap', gap: '8px' }}>
                  {lifestyles.map((lifestyle) => (
                    <Button
                      key={lifestyle}
                      type={selectedLifestyle === lifestyle ? 'primary' : 'default'}
                      onClick={() => setSelectedLifestyle(lifestyle)}
                      style={{ minWidth: '80px' }}
                    >
                      {lifestyle}
                    </Button>
                  ))}
                </div>
              </div>
              
              <Button 
                type="primary" 
                size="large" 
                block
                icon={<HeartOutlined />}
                onClick={handleLifestyleRecommendation}
                loading={loading}
              >
                获取生活方式推荐
              </Button>
            </div>
          </TabPane>

          <TabPane tab="预测推荐" key="predict">
            <div style={{ flex: 1, overflowY: 'auto' }}>
              <Alert
                message="AI预测推荐"
                description="基于您的历史行为和偏好，AI将预测您可能感兴趣的商品"
                type="info"
                showIcon
                style={{ marginBottom: '16px' }}
              />
              
              <Button 
                type="primary" 
                size="large" 
                block
                icon={<RocketOutlined />}
                onClick={handlePredictRecommendation}
                loading={loading}
              >
                获取AI预测推荐
              </Button>
            </div>
          </TabPane>

          <TabPane tab="推荐结果" key="results">
            <div style={{ flex: 1, overflowY: 'auto' }}>
              <RecommendationResults
                recommendations={recommendations}
                loading={loading}
                algorithm={algorithms.find(a => a.key === selectedAlgorithm)?.name || '未知'}
                confidence={confidence}
                onProductClick={onProductClick}
                onAddToCart={onAddToCart}
                onLike={onLike}
                onShare={onShare}
              />
            </div>
          </TabPane>

          <TabPane tab="推荐历史" key="history">
            <div style={{ flex: 1, overflowY: 'auto' }}>
              <RecommendationHistory
                history={history}
                onSessionClick={handleSessionClick}
              />
            </div>
          </TabPane>

          <TabPane tab="统计信息" key="stats">
            <div style={{ flex: 1, overflowY: 'auto' }}>
              <RecommendationStats userId={userId} />
            </div>
          </TabPane>
        </Tabs>
      </Card>

      {/* 设置模态框 */}
      <Modal
        title="推荐设置"
        open={showSettings}
        onCancel={() => setShowSettings(false)}
        footer={null}
        width={500}
      >
        <Form layout="vertical">
          <Form.Item label="默认算法">
            <Select value={selectedAlgorithm} onChange={setSelectedAlgorithm}>
              {algorithms.map(algorithm => (
                <Select.Option key={algorithm.key} value={algorithm.key}>
                  {algorithm.name}
                </Select.Option>
              ))}
            </Select>
          </Form.Item>
          
          <Form.Item label="推荐数量">
            <Slider
              min={5}
              max={50}
              defaultValue={20}
              marks={{
                5: '5',
                20: '20',
                50: '50'
              }}
            />
          </Form.Item>
          
          <Form.Item label="功能开关">
            <Space direction="vertical">
              <div>
                <Switch defaultChecked /> 场景推荐
              </div>
              <div>
                <Switch defaultChecked /> 生活方式推荐
              </div>
              <div>
                <Switch defaultChecked /> AI预测推荐
              </div>
              <div>
                <Switch /> 个性化学习
              </div>
            </Space>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default EnhancedRecommendation
