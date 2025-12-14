import React, { useState, useEffect } from 'react'
import { 
  Card, 
  Row, 
  Col, 
  Button, 
  Space, 
  Typography, 
  Select, 
  Tag,
  List,
  Avatar,
  message,
  Empty,
  Spin
} from 'antd'
import { 
  ExperimentOutlined, 
  EyeOutlined,
  ShoppingCartOutlined,
  TeamOutlined,
  BulbOutlined,
  ArrowLeftOutlined
} from '@ant-design/icons'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { useStore } from '../store/useStore'
import { ApiService } from '../services/api'
import { Product } from '../store/useStore'
import ARVRExperience from '../components/ARVRExperience'

const { Title, Paragraph } = Typography
const { Option } = Select

const ARVRPage: React.FC = () => {
  const navigate = useNavigate()
  const [searchParams] = useSearchParams()
  const { addToCart, addNotification } = useStore()
  const [products, setProducts] = useState<Product[]>([])
  const [selectedProduct, setSelectedProduct] = useState<Product | null>(null)
  const [selectedExperience, setSelectedExperience] = useState<'AR' | 'VR'>('AR')
  const [loading, setLoading] = useState(true)
  const [showExperience, setShowExperience] = useState(false)

  const productId = searchParams.get('productId')
  const experienceType = searchParams.get('type') as 'AR' | 'VR' || 'AR'

  useEffect(() => {
    loadProducts()
    if (productId) {
      loadProduct(parseInt(productId))
    }
  }, [productId])

  useEffect(() => {
    if (experienceType) {
      setSelectedExperience(experienceType)
    }
  }, [experienceType])

  const loadProducts = async () => {
    try {
      setLoading(true)
      const response = await ApiService.getProducts()
      setProducts(response.data)
    } catch (error) {
      console.error('加载商品失败:', error)
      message.error('加载商品失败')
    } finally {
      setLoading(false)
    }
  }

  const loadProduct = async (id: number) => {
    try {
      const response = await ApiService.getProductById(id)
      setSelectedProduct(response.data)
    } catch (error) {
      console.error('加载商品详情失败:', error)
      message.error('加载商品详情失败')
    }
  }

  const handleProductSelect = (productId: number) => {
    const product = products.find(p => p.id === productId)
    if (product) {
      setSelectedProduct(product)
    }
  }

  const handleStartExperience = () => {
    if (!selectedProduct) {
      message.warning('请先选择商品')
      return
    }

    if (!selectedProduct.has3dPreview && selectedExperience === 'AR') {
      message.warning('该商品暂不支持AR体验')
      return
    }

    if (!selectedProduct.vrExperienceUrl && selectedExperience === 'VR') {
      message.warning('该商品暂不支持VR体验')
      return
    }

    setShowExperience(true)
    
    // 记录AR/VR体验行为
    ApiService.recordUserBehavior(1, selectedProduct.id, 'AR_VR_EXPERIENCE')
    
    addNotification({
      title: `${selectedExperience}体验启动`,
      message: `正在启动${selectedProduct.name}的${selectedExperience}体验`,
      type: 'info'
    })
  }

  const handleAddToCart = () => {
    if (selectedProduct) {
      addToCart(selectedProduct)
      addNotification({
        title: '添加成功',
        message: `${selectedProduct.name} 已添加到购物车`,
        type: 'success'
      })
    }
  }

  const handleCollaboration = () => {
    if (selectedProduct) {
      navigate(`/collaboration?productId=${selectedProduct.id}`)
    }
  }

  const arVrProducts = products.filter(product => 
    product.has3dPreview || product.vrExperienceUrl
  )

  if (showExperience && selectedProduct) {
    return (
      <ARVRExperience
        productId={selectedProduct.id}
        productName={selectedProduct.name}
        experienceType={selectedExperience}
        onClose={() => setShowExperience(false)}
      />
    )
  }

  if (loading) {
    return (
      <div className="loading-spinner">
        <Spin size="large" />
      </div>
    )
  }

  return (
    <div className="ar-vr-page">
      <Card>
        <Space style={{ marginBottom: 16 }}>
          <Button 
            icon={<ArrowLeftOutlined />} 
            onClick={() => navigate('/products')}
          >
            返回商品列表
          </Button>
        </Space>
        
        <Title level={2}>
          <ExperimentOutlined /> AR/VR 体验中心
        </Title>
        <Paragraph>
          通过增强现实(AR)和虚拟现实(VR)技术，为您提供沉浸式的商品体验
        </Paragraph>
      </Card>

      <Row gutter={[24, 24]} style={{ marginTop: 16 }}>
        {/* 商品选择区域 */}
        <Col xs={24} lg={12}>
          <Card title="选择商品" size="small">
            <Space direction="vertical" style={{ width: '100%' }}>
              <div>
                <label>体验类型:</label>
                <Select
                  value={selectedExperience}
                  onChange={setSelectedExperience}
                  style={{ width: '100%', marginTop: 8 }}
                >
                  <Option value="AR">AR (增强现实)</Option>
                  <Option value="VR">VR (虚拟现实)</Option>
                </Select>
              </div>
              
              <div>
                <label>选择商品:</label>
                <Select
                  placeholder="选择要体验的商品"
                  value={selectedProduct?.id}
                  onChange={handleProductSelect}
                  style={{ width: '100%', marginTop: 8 }}
                >
                  {arVrProducts.map(product => (
                    <Option key={product.id} value={product.id}>
                      {product.name}
                      {product.has3dPreview && <Tag color="blue" style={{ marginLeft: 8 }}>AR</Tag>}
                      {product.vrExperienceUrl && <Tag color="purple" style={{ marginLeft: 8 }}>VR</Tag>}
                    </Option>
                  ))}
                </Select>
              </div>

              <Button 
                type="primary" 
                icon={<ExperimentOutlined />}
                onClick={handleStartExperience}
                disabled={!selectedProduct}
                block
                size="large"
              >
                开始{selectedExperience}体验
              </Button>
            </Space>
          </Card>
        </Col>

        {/* 商品信息区域 */}
        <Col xs={24} lg={12}>
          <Card title="商品信息" size="small">
            {selectedProduct ? (
              <div className="product-info">
                <img
                  src={`https://picsum.photos/400/300?random=${selectedProduct.id}`}
                  alt={selectedProduct.name}
                  style={{ width: '100%', borderRadius: '8px', marginBottom: 16 }}
                />
                <Title level={4}>{selectedProduct.name}</Title>
                <Paragraph>{selectedProduct.description}</Paragraph>
                
                <div style={{ marginBottom: 16 }}>
                  <Space wrap>
                    <span style={{ fontSize: '18px', fontWeight: 'bold', color: '#1890ff' }}>
                      ¥{selectedProduct.price}
                    </span>
                    {selectedProduct.has3dPreview && (
                      <Tag color="blue" icon={<ExperimentOutlined />}>支持AR</Tag>
                    )}
                    {selectedProduct.vrExperienceUrl && (
                      <Tag color="purple" icon={<ExperimentOutlined />}>支持VR</Tag>
                    )}
                    {selectedProduct.isRecyclable && (
                      <Tag color="green">可回收</Tag>
                    )}
                  </Space>
                </div>

                <Space wrap>
                  <Button 
                    type="primary" 
                    icon={<ShoppingCartOutlined />}
                    onClick={handleAddToCart}
                  >
                    加入购物车
                  </Button>
                  <Button 
                    icon={<TeamOutlined />}
                    onClick={handleCollaboration}
                  >
                    协作购物
                  </Button>
                </Space>
              </div>
            ) : (
              <Empty
                description="请选择商品"
                image={Empty.PRESENTED_IMAGE_SIMPLE}
              />
            )}
          </Card>
        </Col>
      </Row>

      {/* AR/VR体验商品列表 */}
      <Card title="支持AR/VR体验的商品" style={{ marginTop: 16 }}>
        {arVrProducts.length > 0 ? (
          <List
            grid={{ gutter: 16, xs: 1, sm: 2, md: 3, lg: 4, xl: 5 }}
            dataSource={arVrProducts}
            renderItem={(product) => (
              <List.Item>
                <Card
                  hoverable
                  className="product-card"
                  cover={
                    <img
                      alt={product.name}
                      src={`https://picsum.photos/300/200?random=${product.id}`}
                      style={{ height: 150, objectFit: 'cover' }}
                    />
                  }
                  actions={[
                    <Button 
                      type="link" 
                      icon={<ExperimentOutlined />}
                      onClick={() => {
                        setSelectedProduct(product)
                        setSelectedExperience('AR')
                      }}
                    >
                      AR体验
                    </Button>,
                    <Button 
                      type="link" 
                      icon={<ExperimentOutlined />}
                      onClick={() => {
                        setSelectedProduct(product)
                        setSelectedExperience('VR')
                      }}
                    >
                      VR体验
                    </Button>,
                    <Button 
                      type="link" 
                      icon={<EyeOutlined />}
                      onClick={() => navigate(`/products/${product.id}`)}
                    >
                      查看详情
                    </Button>
                  ]}
                >
                  <div className="product-info">
                    <div className="product-title">{product.name}</div>
                    <div className="product-price">¥{product.price}</div>
                    <div className="product-tags">
                      <Space wrap>
                        {product.has3dPreview && (
                          <Tag color="blue" size="small">AR</Tag>
                        )}
                        {product.vrExperienceUrl && (
                          <Tag color="purple" size="small">VR</Tag>
                        )}
                        {product.suitableScenarios?.slice(0, 2).map(scenario => (
                          <Tag key={scenario} color="green" size="small">{scenario}</Tag>
                        ))}
                      </Space>
                    </div>
                  </div>
                </Card>
              </List.Item>
            )}
          />
        ) : (
          <Empty description="暂无支持AR/VR体验的商品" />
        )}
      </Card>

      {/* 体验说明 */}
      <Card title="体验说明" style={{ marginTop: 16 }}>
        <Row gutter={[24, 16]}>
          <Col xs={24} md={12}>
            <Card size="small" title="AR体验">
              <ul>
                <li>使用手机或平板电脑的相机功能</li>
                <li>将设备对准商品进行AR预览</li>
                <li>支持360度旋转查看</li>
                <li>可以调整商品大小和位置</li>
                <li>支持拍照和分享功能</li>
              </ul>
            </Card>
          </Col>
          <Col xs={24} md={12}>
            <Card size="small" title="VR体验">
              <ul>
                <li>使用鼠标拖拽旋转视角</li>
                <li>滚轮缩放查看细节</li>
                <li>右键拖拽平移视角</li>
                <li>支持全屏沉浸式体验</li>
                <li>可以与其他用户协作体验</li>
              </ul>
            </Card>
          </Col>
        </Row>
      </Card>
    </div>
  )
}

export default ARVRPage
