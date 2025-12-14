import React, { useState, useEffect } from 'react'
import { 
  Card, 
  Row, 
  Col, 
  Button, 
  Avatar, 
  Space, 
  Tag,
  Typography,
  Tabs,
  List,
  Statistic,
  Progress,
  Empty,
  Badge
} from 'antd'
import { 
  UserOutlined, 
  ShoppingCartOutlined,
  HeartOutlined,
  ShareAltOutlined,
  EyeOutlined,
  RecycleOutlined,
  TeamOutlined,
  BulbOutlined,
  SettingOutlined,
  EditOutlined
} from '@ant-design/icons'
import { useStore } from '../store/useStore'
import ApiService from '../services/api'
import { User, Product, CartItem, Notification } from '../store/useStore'
import { useNavigate } from 'react-router-dom'

const { Title, Paragraph } = Typography
const { TabPane } = Tabs

const Profile: React.FC = () => {
  const { user, cartItems, notifications, recommendations, collaborationSessions } = useStore()
  const [userInfo, setUserInfo] = useState<User | null>(null)
  const [loading, setLoading] = useState(true)
  const navigate = useNavigate()

  useEffect(() => {
    loadUserInfo()
  }, [])

  const loadUserInfo = async () => {
    try {
      setLoading(true)
      // 模拟加载用户信息
      const mockUser: User = {
        id: 1,
        username: 'alice_wang',
        email: 'alice@example.com',
        preferences: { theme: 'dark', language: 'zh-CN' },
        lifestyle: ['运动健身', '环保生活', '科技数码'],
        scenarios: ['健身房', '户外运动', '居家办公']
      }
      setUserInfo(mockUser)
    } catch (error) {
      console.error('加载用户信息失败:', error)
    } finally {
      setLoading(false)
    }
  }

  const sustainabilityScore = 85
  const recycleCount = 12
  const followerCount = 150
  const followingCount = 89

  const recentActivities = [
    { type: 'purchase', product: '智能运动手环', time: '2小时前' },
    { type: 'recommendation', product: '瑜伽垫套装', time: '1天前' },
    { type: 'collaboration', product: '时尚连衣裙', time: '2天前' },
    { type: 'recycle', product: '旧手机', time: '3天前' },
  ]

  const achievements = [
    { name: '环保达人', description: '完成10次产品回收', progress: 100 },
    { name: '社交购物家', description: '参与5次协作购物', progress: 80 },
    { name: '推荐专家', description: '获得50个推荐', progress: 60 },
    { name: 'AR体验师', description: '体验10个AR商品', progress: 40 },
  ]

  if (loading) {
    return <div>Loading...</div>
  }

  return (
    <div className="profile-page">
      {/* 用户基本信息 */}
      <Card>
        <Row gutter={[24, 24]} align="middle">
          <Col xs={24} sm={8}>
            <div className="user-avatar-section">
              <Avatar size={120} icon={<UserOutlined />} />
              <div className="user-basic-info">
                <Title level={3}>{userInfo?.username || '用户'}</Title>
                <Paragraph type="secondary">{userInfo?.email}</Paragraph>
                <Space>
                  <Tag color="blue">运动健身</Tag>
                  <Tag color="green">环保生活</Tag>
                  <Tag color="purple">科技数码</Tag>
                </Space>
              </div>
            </div>
          </Col>
          
          <Col xs={24} sm={16}>
            <Row gutter={[16, 16]}>
              <Col span={6}>
                <Statistic title="关注者" value={followerCount} />
              </Col>
              <Col span={6}>
                <Statistic title="关注中" value={followingCount} />
              </Col>
              <Col span={6}>
                <Statistic title="回收次数" value={recycleCount} />
              </Col>
              <Col span={6}>
                <Statistic title="可持续评分" value={sustainabilityScore} suffix="/100" />
              </Col>
            </Row>
            
            <div style={{ marginTop: 16 }}>
              <Space>
                <Button type="primary" icon={<EditOutlined />}>
                  编辑资料
                </Button>
                <Button icon={<SettingOutlined />}>
                  设置
                </Button>
              </Space>
            </div>
          </Col>
        </Row>
      </Card>

      {/* 详细信息和活动 */}
      <Card style={{ marginTop: 16 }}>
        <Tabs defaultActiveKey="overview">
          <TabPane tab="概览" key="overview">
            <Row gutter={[24, 24]}>
              <Col xs={24} lg={12}>
                <Card title="购物车" size="small">
                  <div className="cart-summary">
                    <Space>
                      <ShoppingCartOutlined />
                      <span>{cartItems.length} 件商品</span>
                    </Space>
                    <List
                      size="small"
                      dataSource={cartItems.slice(0, 3)}
                      renderItem={(item: CartItem) => (
                        <List.Item>
                          <List.Item.Meta
                            title={item.product.name}
                            description={`¥${item.product.price} x ${item.quantity}`}
                          />
                        </List.Item>
                      )}
                    />
                    {cartItems.length > 3 && (
                      <div style={{ textAlign: 'center', marginTop: 8 }}>
                        <Button type="link">查看全部</Button>
                      </div>
                    )}
                  </div>
                </Card>
              </Col>
              
              <Col xs={24} lg={12}>
                <Card title="最近活动" size="small">
                  <List
                    size="small"
                    dataSource={recentActivities}
                    renderItem={(activity) => (
                      <List.Item>
                        <List.Item.Meta
                          title={activity.product}
                          description={activity.time}
                        />
                        <Tag color="blue">{activity.type}</Tag>
                      </List.Item>
                    )}
                  />
                </Card>
              </Col>
            </Row>
          </TabPane>

          <TabPane tab="推荐记录" key="recommendations">
            <div className="recommendations-section">
              <Title level={4}>我的推荐记录</Title>
              {recommendations.length > 0 ? (
                <List
                  dataSource={recommendations}
                  renderItem={(recommendation) => (
                    <List.Item>
                      <Card size="small" style={{ width: '100%' }}>
                        <Row gutter={[16, 16]}>
                          <Col span={6}>
                            <Badge count={recommendation.products.length}>
                              <BulbOutlined style={{ fontSize: '24px' }} />
                            </Badge>
                          </Col>
                          <Col span={12}>
                            <div>
                              <Title level={5}>
                                {recommendation.scenario || 'AI推荐'}
                              </Title>
                              <Paragraph type="secondary">
                                {new Date(recommendation.timestamp).toLocaleString()}
                              </Paragraph>
                            </div>
                          </Col>
                          <Col span={6}>
                            <Button type="link">查看详情</Button>
                          </Col>
                        </Row>
                      </Card>
                    </List.Item>
                  )}
                />
              ) : (
                <Empty description="暂无推荐记录" />
              )}
            </div>
          </TabPane>

          <TabPane tab="协作购物" key="collaboration">
            <div className="collaboration-section">
              <Title level={4}>协作购物记录</Title>
              {collaborationSessions.length > 0 ? (
                <List
                  dataSource={collaborationSessions}
                  renderItem={(session) => (
                    <List.Item>
                      <Card size="small" style={{ width: '100%' }}>
                        <Row gutter={[16, 16]}>
                          <Col span={6}>
                            <TeamOutlined style={{ fontSize: '24px' }} />
                          </Col>
                          <Col span={12}>
                            <div>
                              <Title level={5}>会话 {session.id}</Title>
                              <Paragraph type="secondary">
                                参与者: {session.participantIds.length} 人
                              </Paragraph>
                              <Paragraph type="secondary">
                                {new Date(session.createTime).toLocaleString()}
                              </Paragraph>
                            </div>
                          </Col>
                          <Col span={6}>
                            <Tag color={session.status === 'ACTIVE' ? 'green' : 'red'}>
                              {session.status}
                            </Tag>
                          </Col>
                        </Row>
                      </Card>
                    </List.Item>
                  )}
                />
              ) : (
                <Empty description="暂无协作购物记录" />
              )}
            </div>
          </TabPane>

          <TabPane tab="价值循环" key="recycle">
            <div className="recycle-section">
              <Title level={4}>可持续消费记录</Title>
              <Row gutter={[16, 16]}>
                <Col span={24}>
                  <Card>
                    <Row gutter={[16, 16]}>
                      <Col span={12}>
                        <Statistic 
                          title="回收次数" 
                          value={recycleCount} 
                          suffix="次"
                        />
                      </Col>
                      <Col span={12}>
                        <Statistic 
                          title="环保贡献" 
                          value={recycleCount * 2.5} 
                          suffix="kg CO₂"
                        />
                      </Col>
                    </Row>
                  </Card>
                </Col>
                
                <Col span={24}>
                  <Card title="环保成就">
                    <List
                      dataSource={achievements}
                      renderItem={(achievement) => (
                        <List.Item>
                          <div style={{ width: '100%' }}>
                            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 8 }}>
                              <span>{achievement.name}</span>
                              <span>{achievement.progress}%</span>
                            </div>
                            <Progress 
                              percent={achievement.progress} 
                              size="small"
                            />
                            <div style={{ fontSize: '12px', color: '#666', marginTop: 4 }}>
                              {achievement.description}
                            </div>
                          </div>
                        </List.Item>
                      )}
                    />
                  </Card>
                </Col>
              </Row>
            </div>
          </TabPane>

          <TabPane tab="通知" key="notifications">
          
          <TabPane tab="账户安全" key="security">
            <Button type="primary" onClick={() => navigate('/security')}>
              前往安全设置
            </Button>
          </TabPane>
          
          <TabPane tab="愿望清单" key="wishlist">
            <Button type="primary" onClick={() => navigate('/wishlist')}>
              查看愿望清单
            </Button>
          </TabPane>
          
          <TabPane tab="商品对比" key="comparison">
            <Button type="primary" onClick={() => navigate('/comparison')}>
              查看商品对比
            </Button>
          </TabPane>
          
          <TabPane tab="客服中心" key="customer-service">
            <Button type="primary" onClick={() => navigate('/customer-service')}>
              前往客服中心
            </Button>
          </TabPane>
            <div className="notifications-section">
              <Title level={4}>消息通知</Title>
              {notifications.length > 0 ? (
                <List
                  dataSource={notifications}
                  renderItem={(notification: Notification) => (
                    <List.Item>
                      <Card size="small" style={{ width: '100%' }}>
                        <Row gutter={[16, 16]}>
                          <Col span={20}>
                            <div>
                              <Title level={5}>{notification.title}</Title>
                              <Paragraph>{notification.message}</Paragraph>
                              <Paragraph type="secondary">
                                {new Date(notification.timestamp).toLocaleString()}
                              </Paragraph>
                            </div>
                          </Col>
                          <Col span={4}>
                            <Tag color={notification.read ? 'default' : 'blue'}>
                              {notification.read ? '已读' : '未读'}
                            </Tag>
                          </Col>
                        </Row>
                      </Card>
                    </List.Item>
                  )}
                />
              ) : (
                <Empty description="暂无通知" />
              )}
            </div>
          </TabPane>
        </Tabs>
      </Card>
    </div>
  )
}

export default Profile

