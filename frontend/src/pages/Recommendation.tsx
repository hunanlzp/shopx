import React, { useState, useEffect } from 'react'
import { 
  Card, 
  Row, 
  Col, 
  Button, 
  Select, 
  Space, 
  Spin, 
  Empty,
  Tag,
  Typography,
  Tabs,
  Input,
  message
} from 'antd'
import { 
  BulbOutlined, 
  ExperimentOutlined, 
  TeamOutlined, 
  ShoppingCartOutlined,
  HeartOutlined,
  ShareAltOutlined,
  EyeOutlined,
  RobotOutlined
} from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { useStore } from '../store/useStore'
import ApiService from '../services/api'
import { Product } from '../store/useStore'

const { Title, Paragraph } = Typography
const { TabPane } = Tabs
const { Option } = Select

const Recommendation: React.FC = () => {
  const navigate = useNavigate()
  const { addToCart, addNotification, addRecommendation } = useStore()
  const [loading, setLoading] = useState(false)
  const [recommendations, setRecommendations] = useState<Product[]>([])
  const [selectedUserId, setSelectedUserId] = useState(1)
  const [selectedScenario, setSelectedScenario] = useState('运动健身')
  const [selectedLifestyle, setSelectedLifestyle] = useState('运动健身')
  const [aiQuery, setAiQuery] = useState('')

  const scenarios = [
    '运动健身', '时尚潮流', '居家办公', '旅行摄影', 
    '美食烹饪', '艺术设计', '商务办公', '母婴育儿', 
    '环保生活', '科技数码'
  ]

  const lifestyles = [
    '运动健身', '时尚潮流', '美食烹饪', '艺术设计',
    '商务办公', '母婴育儿', '环保生活', '科技数码',
    '旅行摄影', '音乐电影', '宠物养护', '汽车改装'
  ]

  const users = [
    { id: 1, name: 'Alice Wang', lifestyle: '运动健身,环保生活,科技数码' },
    { id: 2, name: 'Bob Li', lifestyle: '时尚潮流,美食烹饪,旅行摄影' },
    { id: 3, name: 'Carol Zhang', lifestyle: '艺术设计,音乐电影,宠物养护' },
    { id: 4, name: 'David Chen', lifestyle: '商务办公,投资理财,汽车改装' },
    { id: 5, name: 'Eve Liu', lifestyle: '母婴育儿,家居装饰,健康养生' }
  ]

  const loadScenarioRecommendations = async () => {
    try {
      setLoading(true)
      const response = await ApiService.getScenarioRecommendation(selectedUserId, selectedScenario)
      setRecommendations(response.data)
      
      // 保存推荐记录
      addRecommendation({
        id: Date.now().toString(),
        userId: selectedUserId,
        scenario: selectedScenario,
        products: response.data,
        timestamp: new Date().toISOString()
      })
      
      addNotification({
        title: '推荐成功',
        message: `为您推荐了 ${response.data.length} 个商品`,
        type: 'success'
      })
    } catch (error) {
      console.error('获取推荐失败:', error)
      message.error('获取推荐失败')
    } finally {
      setLoading(false)
    }
  }

  const loadLifestyleRecommendations = async () => {
    try {
      setLoading(true)
      const response = await ApiService.getLifestyleRecommendation(selectedUserId, selectedLifestyle)
      setRecommendations(response.data)
      
      addNotification({
        title: '推荐成功',
        message: `基于生活方式推荐了 ${response.data.length} 个商品`,
        type: 'success'
      })
    } catch (error) {
      console.error('获取推荐失败:', error)
      message.error('获取推荐失败')
    } finally {
      setLoading(false)
    }
  }

  const loadPredictRecommendations = async () => {
    try {
      setLoading(true)
      const response = await ApiService.getPredictRecommendation(selectedUserId)
      setRecommendations(response.data)
      
      addNotification({
        title: '预测推荐',
        message: `AI预测您可能需要 ${response.data.length} 个商品`,
        type: 'info'
      })
    } catch (error) {
      console.error('获取预测推荐失败:', error)
      message.error('获取预测推荐失败')
    } finally {
      setLoading(false)
    }
  }

  const handleAddToCart = (product: Product) => {
    addToCart(product)
    addNotification({
      title: '添加成功',
      message: `${product.name} 已添加到购物车`,
      type: 'success'
    })
  }

  const handleProductClick = (product: Product) => {
    navigate(`/products/${product.id}`)
  }

  const handleAIChat = async () => {
    if (!aiQuery.trim()) {
      message.warning('请输入您的问题')
      return
    }

    try {
      setLoading(true)
      const response = await ApiService.chatWithAI(selectedUserId, aiQuery)
      
      if (response.suggestedProducts && response.suggestedProducts.length > 0) {
        setRecommendations(response.suggestedProducts)
        addNotification({
          title: 'AI推荐',
          message: response.response,
          type: 'info'
        })
      } else {
        addNotification({
          title: 'AI回复',
          message: response.response,
          type: 'info'
        })
      }
    } catch (error) {
      console.error('AI对话失败:', error)
      message.error('AI对话失败')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="recommendation-page">
      <Card>
        <Title level={2}>
          <BulbOutlined /> 智能推荐系统
        </Title>
        <Paragraph>
          基于AI算法和用户行为分析，为您提供个性化的商品推荐服务
        </Paragraph>
      </Card>

      <Card style={{ marginTop: 16 }}>
        <Tabs defaultActiveKey="scenario">
          <TabPane tab="场景推荐" key="scenario">
            <div className="recommendation-controls">
              <Row gutter={[16, 16]} align="middle">
                <Col xs={24} sm={8}>
                  <Space>
                    <span>用户:</span>
                    <Select
                      value={selectedUserId}
                      onChange={setSelectedUserId}
                      style={{ width: 150 }}
                    >
                      {users.map(user => (
                        <Option key={user.id} value={user.id}>
                          {user.name}
                        </Option>
                      ))}
                    </Select>
                  </Space>
                </Col>
                <Col xs={24} sm={8}>
                  <Space>
                    <span>场景:</span>
                    <Select
                      value={selectedScenario}
                      onChange={setSelectedScenario}
                      style={{ width: 150 }}
                    >
                      {scenarios.map(scenario => (
                        <Option key={scenario} value={scenario}>
                          {scenario}
                        </Option>
                      ))}
                    </Select>
                  </Space>
                </Col>
                <Col xs={24} sm={8}>
                  <Button 
                    type="primary" 
                    onClick={loadScenarioRecommendations}
                    loading={loading}
                    icon={<BulbOutlined />}
                  >
                    获取推荐
                  </Button>
                </Col>
              </Row>
            </div>
          </TabPane>

          <TabPane tab="生活方式推荐" key="lifestyle">
            <div className="recommendation-controls">
              <Row gutter={[16, 16]} align="middle">
                <Col xs={24} sm={8}>
                  <Space>
                    <span>用户:</span>
                    <Select
                      value={selectedUserId}
                      onChange={setSelectedUserId}
                      style={{ width: 150 }}
                    >
                      {users.map(user => (
                        <Option key={user.id} value={user.id}>
                          {user.name}
                        </Option>
                      ))}
                    </Select>
                  </Space>
                </Col>
                <Col xs={24} sm={8}>
                  <Space>
                    <span>生活方式:</span>
                    <Select
                      value={selectedLifestyle}
                      onChange={setSelectedLifestyle}
                      style={{ width: 150 }}
                    >
                      {lifestyles.map(lifestyle => (
                        <Option key={lifestyle} value={lifestyle}>
                          {lifestyle}
                        </Option>
                      ))}
                    </Select>
                  </Space>
                </Col>
                <Col xs={24} sm={8}>
                  <Button 
                    type="primary" 
                    onClick={loadLifestyleRecommendations}
                    loading={loading}
                    icon={<BulbOutlined />}
                  >
                    获取推荐
                  </Button>
                </Col>
              </Row>
            </div>
          </TabPane>

          <TabPane tab="预测推荐" key="predict">
            <div className="recommendation-controls">
              <Row gutter={[16, 16]} align="middle">
                <Col xs={24} sm={8}>
                  <Space>
                    <span>用户:</span>
                    <Select
                      value={selectedUserId}
                      onChange={setSelectedUserId}
                      style={{ width: 150 }}
                    >
                      {users.map(user => (
                        <Option key={user.id} value={user.id}>
                          {user.name}
                        </Option>
                      ))}
                    </Select>
                  </Space>
                </Col>
                <Col xs={24} sm={8}>
                  <span>基于用户历史行为和季节预测</span>
                </Col>
                <Col xs={24} sm={8}>
                  <Button 
                    type="primary" 
                    onClick={loadPredictRecommendations}
                    loading={loading}
                    icon={<RobotOutlined />}
                  >
                    预测推荐
                  </Button>
                </Col>
              </Row>
            </div>
          </TabPane>

          <TabPane tab="AI助手" key="ai">
            <div className="ai-chat-controls">
              <Row gutter={[16, 16]} align="middle">
                <Col xs={24} sm={8}>
                  <Space>
                    <span>用户:</span>
                    <Select
                      value={selectedUserId}
                      onChange={setSelectedUserId}
                      style={{ width: 150 }}
                    >
                      {users.map(user => (
                        <Option key={user.id} value={user.id}>
                          {user.name}
                        </Option>
                      ))}
                    </Select>
                  </Space>
                </Col>
                <Col xs={24} sm={12}>
                  <Input
                    placeholder="例如：我想买一个适合运动的耳机"
                    value={aiQuery}
                    onChange={(e) => setAiQuery(e.target.value)}
                    onPressEnter={handleAIChat}
                  />
                </Col>
                <Col xs={24} sm={4}>
                  <Button 
                    type="primary" 
                    onClick={handleAIChat}
                    loading={loading}
                    icon={<RobotOutlined />}
                    block
                  >
                    询问AI
                  </Button>
                </Col>
              </Row>
            </div>
          </TabPane>
        </Tabs>
      </Card>

      {/* 推荐结果 */}
      {loading ? (
        <div className="loading-spinner">
          <Spin size="large" />
        </div>
      ) : recommendations.length > 0 ? (
        <Card title="推荐结果" style={{ marginTop: 16 }}>
          <Row gutter={[24, 24]}>
            {recommendations.map(product => (
              <Col xs={24} sm={12} lg={8} xl={6} key={product.id}>
                <Card
                  hoverable
                  className="product-card"
                  cover={
                    <img
                      alt={product.name}
                      src={`https://picsum.photos/300/200?random=${product.id}`}
                      style={{ height: 200, objectFit: 'cover' }}
                    />
                  }
                  actions={[
                    <EyeOutlined onClick={() => handleProductClick(product)} />,
                    <ShoppingCartOutlined onClick={() => handleAddToCart(product)} />,
                    <HeartOutlined />,
                    <ShareAltOutlined />,
                  ]}
                  onClick={() => handleProductClick(product)}
                >
                  <div className="product-info">
                    <div className="product-title">{product.name}</div>
                    <div className="product-price">¥{product.price}</div>
                    <div className="product-tags">
                      <Space wrap>
                        {product.suitableScenarios?.slice(0, 2).map(scenario => (
                          <Tag key={scenario} color="blue">{scenario}</Tag>
                        ))}
                        {product.isRecyclable && (
                          <Tag color="green">可回收</Tag>
                        )}
                      </Space>
                    </div>
                  </div>
                </Card>
              </Col>
            ))}
          </Row>
        </Card>
      ) : (
        <Card style={{ marginTop: 16 }}>
          <Empty
            description="暂无推荐结果"
            image={Empty.PRESENTED_IMAGE_SIMPLE}
          >
            <Button type="primary" onClick={loadScenarioRecommendations}>
              获取推荐
            </Button>
          </Empty>
        </Card>
      )}
    </div>
  )
}

export default Recommendation

