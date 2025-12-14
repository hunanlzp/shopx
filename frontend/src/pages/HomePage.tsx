import React, { useEffect, useState } from 'react'
import { Card, Row, Col, Button, Typography, Space, Tag, Spin, Badge } from 'antd'
import { 
  ExperimentOutlined, 
  TeamOutlined, 
  BulbOutlined, 
  RecycleOutlined,
  RobotOutlined,
  ArrowRightOutlined,
  ThunderboltOutlined,
  RocketOutlined,
  StarOutlined
} from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { useStore } from '../store/useStore'
import { ApiService } from '../services/api'
import { motion } from 'framer-motion'
import './Home.css'

const { Title, Paragraph } = Typography

const HomePage: React.FC = () => {
  const navigate = useNavigate()
  const { setProducts, addNotification } = useStore()
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    loadProducts()
  }, [])

  const loadProducts = async () => {
    try {
      setLoading(true)
      const response = await ApiService.getProducts()
      setProducts(response.data)
    } catch (error) {
      console.error('加载商品失败:', error)
      addNotification({
        title: '加载失败',
        message: '无法加载商品数据，请稍后重试',
        type: 'error'
      })
    } finally {
      setLoading(false)
    }
  }

  const features = [
    {
      icon: <BulbOutlined />,
      title: '情境化推荐',
      description: '基于用户生活场景的智能推荐系统',
      action: () => navigate('/recommendation'),
      gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
      iconColor: '#667eea',
      delay: 0.1
    },
    {
      icon: <TeamOutlined />,
      title: '协作购物',
      description: '多人实时购物体验，与朋友一起购物',
      action: () => navigate('/collaboration'),
      gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
      iconColor: '#f5576c',
      delay: 0.2
    },
    {
      icon: <ExperimentOutlined />,
      title: 'AR/VR体验',
      description: '沉浸式购物体验，虚拟试用商品',
      action: () => navigate('/ar-vr'),
      gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
      iconColor: '#4facfe',
      delay: 0.3
    },
    {
      icon: <RecycleOutlined />,
      title: '价值循环',
      description: '产品回收与再利用，可持续消费',
      action: () => navigate('/recycle'),
      gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
      iconColor: '#43e97b',
      delay: 0.4
    },
    {
      icon: <RobotOutlined />,
      title: 'AI购物助手',
      description: '个性化购物建议，智能客服',
      action: () => navigate('/ai-assistant'),
      gradient: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
      iconColor: '#fa709a',
      delay: 0.5
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
    <div className="home-page">
      {/* 现代化欢迎横幅 */}
      <motion.div
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.6 }}
      >
        <Card className="welcome-banner-modern">
          <div className="banner-background">
            <div className="banner-gradient"></div>
            <div className="banner-pattern"></div>
          </div>
          <Row align="middle" justify="space-between" className="banner-content">
            <Col xs={24} lg={14}>
              <motion.div
                initial={{ opacity: 0, x: -30 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ delay: 0.2, duration: 0.6 }}
              >
                <Badge.Ribbon text="NEW" color="pink">
                  <Title level={1} className="banner-title">
                    <RocketOutlined className="title-icon" />
                    欢迎来到 ShopX
                  </Title>
                </Badge.Ribbon>
                <Title level={3} className="banner-subtitle">
                  创新电商平台，重新定义购物体验
                </Title>
                <Paragraph className="banner-description">
                  体验情境化推荐、协作购物、AR/VR试穿等颠覆性功能，
                  让购物变得更加智能、社交和可持续。
                </Paragraph>
                <Space size="large" className="banner-actions">
                  <Button 
                    type="primary" 
                    size="large"
                    icon={<ThunderboltOutlined />}
                    className="banner-btn-primary"
                    onClick={() => navigate('/products')}
                  >
                    开始购物
                  </Button>
                  <Button 
                    size="large"
                    icon={<StarOutlined />}
                    className="banner-btn-secondary"
                    onClick={() => navigate('/recommendation')}
                  >
                    智能推荐
                  </Button>
                </Space>
              </motion.div>
            </Col>
            <Col xs={24} lg={10} className="banner-image-col">
              <motion.div
                initial={{ opacity: 0, scale: 0.8 }}
                animate={{ opacity: 1, scale: 1 }}
                transition={{ delay: 0.4, duration: 0.6 }}
                className="banner-image-wrapper"
              >
                <div className="banner-image-glow"></div>
                <div className="banner-image">
                  <div className="image-placeholder">
                    <RocketOutlined style={{ fontSize: '120px', color: 'rgba(255,255,255,0.3)' }} />
                  </div>
                </div>
              </motion.div>
            </Col>
          </Row>
        </Card>
      </motion.div>

      {/* 核心功能展示 - 现代化卡片设计 */}
      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ delay: 0.3, duration: 0.6 }}
      >
        <Card className="features-section" bordered={false}>
          <div className="section-header">
            <Title level={2} className="section-title">
              <ThunderboltOutlined className="section-icon" />
              核心功能
            </Title>
            <Paragraph className="section-description">
              探索ShopX的创新功能，体验未来购物方式
            </Paragraph>
          </div>
          <Row gutter={[24, 24]}>
            {features.map((feature, index) => (
              <Col xs={24} sm={12} lg={8} xl={6} key={index}>
                <motion.div
                  initial={{ opacity: 0, y: 30 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: feature.delay, duration: 0.5 }}
                  whileHover={{ y: -8, scale: 1.02 }}
                  whileTap={{ scale: 0.98 }}
                >
                  <Card
                    hoverable
                    className="feature-card-modern"
                    onClick={feature.action}
                    style={{ 
                      background: feature.gradient,
                      border: 'none',
                      height: '100%',
                      position: 'relative',
                      overflow: 'hidden'
                    }}
                  >
                    <div className="feature-card-shine"></div>
                    <div className="feature-content">
                      <div className="feature-icon-wrapper">
                        <div className="feature-icon-bg"></div>
                        <div className="feature-icon" style={{ color: '#fff' }}>
                          {feature.icon}
                        </div>
                      </div>
                      <Title level={4} className="feature-title">
                        {feature.title}
                      </Title>
                      <Paragraph className="feature-description">
                        {feature.description}
                      </Paragraph>
                      <Button 
                        type="text" 
                        icon={<ArrowRightOutlined />}
                        className="feature-action-btn"
                      >
                        了解更多
                      </Button>
                    </div>
                  </Card>
                </motion.div>
              </Col>
            ))}
          </Row>
        </Card>
      </motion.div>

      {/* 平台特色 - 现代化列表设计 */}
      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ delay: 0.5, duration: 0.6 }}
      >
        <Card className="highlights-section" bordered={false}>
          <div className="section-header">
            <Title level={2} className="section-title">
              <StarOutlined className="section-icon" />
              平台特色
            </Title>
          </div>
          <Row gutter={[24, 16]}>
            <Col span={24}>
              <Space direction="vertical" size="large" style={{ width: '100%' }}>
                {[
                  { emoji: '🎯', tag: '情境化推荐', color: 'blue', text: '基于用户生活场景和行为的智能推荐，让购物更精准' },
                  { emoji: '👥', tag: '协作购物', color: 'green', text: '多人实时购物体验，与朋友一起浏览和讨论商品' },
                  { emoji: '🥽', tag: 'AR/VR体验', color: 'purple', text: '沉浸式购物体验，虚拟试穿和3D预览功能' },
                  { emoji: '🔄', tag: '价值循环', color: 'orange', text: '产品回收与再利用，促进可持续消费' },
                  { emoji: '🤖', tag: 'AI助手', color: 'magenta', text: '个性化购物建议，智能客服和预测服务' }
                ].map((item, index) => (
                  <motion.div
                    key={index}
                    initial={{ opacity: 0, x: -20 }}
                    animate={{ opacity: 1, x: 0 }}
                    transition={{ delay: 0.6 + index * 0.1, duration: 0.4 }}
                    className="feature-highlight-modern"
                  >
                    <Tag color={item.color} className="highlight-tag">
                      <span className="tag-emoji">{item.emoji}</span>
                      {item.tag}
                    </Tag>
                    <span className="highlight-text">{item.text}</span>
                  </motion.div>
                ))}
              </Space>
            </Col>
          </Row>
        </Card>
      </motion.div>
    </div>
  )
}

export default HomePage
