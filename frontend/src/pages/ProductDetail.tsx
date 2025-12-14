import React, { useEffect, useState } from 'react'
import { 
  Card, 
  Row, 
  Col, 
  Button, 
  Tag, 
  Space, 
  Spin, 
  Image, 
  Divider,
  Tabs,
  Badge,
  Tooltip,
  Carousel,
  Rate,
  InputNumber,
  message
} from 'antd'
import { 
  ExperimentOutlined, 
  TeamOutlined, 
  HeartOutlined,
  ShareAltOutlined,
  ShoppingCartOutlined,
  LeftOutlined,
  RightOutlined,
  RecycleOutlined,
  EyeOutlined
} from '@ant-design/icons'
import { useParams, useNavigate } from 'react-router-dom'
import { useStore } from '../store/useStore'
import ApiService from '../services/api'
import { Product } from '../store/useStore'
import ARVRExperience from '../components/ARVRExperience'
import { PriceHistoryChart } from '../components/PriceComponents'
import { ReviewList, ReviewStats, ReviewForm } from '../components/ReviewComponents'
import { AlternativeProducts } from '../components/StockComponents'
import { ComparisonTable } from '../components/ComparisonComponents'
import { WishlistCard } from '../components/WishlistComponents'

const { TabPane } = Tabs

const ProductDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const { addToCart, addNotification } = useStore()
  const [product, setProduct] = useState<Product | null>(null)
  const [loading, setLoading] = useState(true)
  const [quantity, setQuantity] = useState(1)
  const [showARVR, setShowARVR] = useState(false)
  const [arVrType, setArVrType] = useState<'AR' | 'VR'>('AR')
  const [selectedImage, setSelectedImage] = useState(0)

  useEffect(() => {
    if (id) {
      loadProduct(parseInt(id))
    }
  }, [id])

  const loadProduct = async (productId: number) => {
    try {
      setLoading(true)
      const response = await ApiService.getProductById(productId)
      setProduct(response.data)
      
      // 记录用户浏览行为
      await ApiService.recordUserBehavior(1, productId, 'VIEW')
    } catch (error) {
      console.error('加载商品详情失败:', error)
      message.error('加载商品详情失败')
    } finally {
      setLoading(false)
    }
  }

  const handleARVRExperience = (type: 'AR' | 'VR') => {
    if (!product) return
    
    if (type === 'AR' && !product.has3dPreview) {
      message.warning('该商品暂不支持AR体验')
      return
    }
    
    if (type === 'VR' && !product.vrExperienceUrl) {
      message.warning('该商品暂不支持VR体验')
      return
    }
    
    setArVrType(type)
    setShowARVR(true)
    
    // 记录AR/VR体验行为
    ApiService.recordUserBehavior(1, product.id, 'AR_VR_EXPERIENCE')
  }

  const handleAddToCart = () => {
    if (product) {
      addToCart(product, quantity)
      addNotification({
        title: '添加成功',
        message: `${product.name} x${quantity} 已添加到购物车`,
        type: 'success'
      })
    }
  }

  const handleARExperience = () => {
    if (product?.arModelUrl) {
      addNotification({
        title: 'AR体验',
        message: '正在启动AR体验功能...',
        type: 'info'
      })
      // 这里可以集成AR体验功能
    }
  }

  const handleVRExperience = () => {
    if (product?.vrExperienceUrl) {
      addNotification({
        title: 'VR体验',
        message: '正在启动VR体验功能...',
        type: 'info'
      })
      // 这里可以集成VR体验功能
    }
  }

  const handleCollaboration = () => {
    if (product) {
      navigate(`/collaboration?productId=${product.id}`)
    }
  }

  const handleRecycle = () => {
    if (product) {
      navigate(`/recycle?productId=${product.id}`)
    }
  }

  const images = [
    `https://picsum.photos/600/400?random=${id}`,
    `https://picsum.photos/600/400?random=${id}1`,
    `https://picsum.photos/600/400?random=${id}2`,
    `https://picsum.photos/600/400?random=${id}3`,
  ]

  if (loading) {
    return (
      <div className="loading-spinner">
        <Spin size="large" />
      </div>
    )
  }

  if (!product) {
    return (
      <div className="empty-state">
        <h3>商品不存在</h3>
        <Button type="primary" onClick={() => navigate('/products')}>
          返回商品列表
        </Button>
      </div>
    )
  }

  return (
    <div className="product-detail-page">
      <Button 
        icon={<LeftOutlined />} 
        onClick={() => navigate('/products')}
        style={{ marginBottom: 16 }}
      >
        返回商品列表
      </Button>

      <Row gutter={[24, 24]}>
        {/* 商品图片区域 */}
        <Col xs={24} lg={12}>
          <Card>
            <div className="product-image-gallery">
              <div className="main-image">
                <Image
                  src={images[selectedImage]}
                  alt={product.name}
                  style={{ width: '100%', borderRadius: '8px' }}
                />
              </div>
              <div className="thumbnail-list">
                {images.map((img, index) => (
                  <div
                    key={index}
                    className={`thumbnail ${selectedImage === index ? 'active' : ''}`}
                    onClick={() => setSelectedImage(index)}
                  >
                    <Image
                      src={img}
                      alt={`${product.name} ${index + 1}`}
                      style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                    />
                  </div>
                ))}
              </div>
            </div>
          </Card>
        </Col>

        {/* 商品信息区域 */}
        <Col xs={24} lg={12}>
          <Card>
            <div className="product-info">
              <h1 className="product-title">{product.name}</h1>
              
              <div className="product-price-section">
                <span className="current-price">¥{product.price}</span>
                {product.recycleValue && (
                  <span className="recycle-value">
                    回收价值: ¥{product.recycleValue}
                  </span>
                )}
              </div>

              <div className="product-rating">
                <Rate disabled defaultValue={4.5} />
                <span className="rating-text">(128 评价)</span>
              </div>

              <div className="product-description">
                <p>{product.description}</p>
              </div>

              <div className="product-tags">
                <Space wrap>
                  {product.suitableScenarios?.map(scenario => (
                    <Tag key={scenario} color="blue">{scenario}</Tag>
                  ))}
                  {product.lifestyleTags?.split(',').map(tag => (
                    <Tag key={tag} color="green">{tag}</Tag>
                  ))}
                  {product.isRecyclable && (
                    <Tag color="green" icon={<RecycleOutlined />}>可回收</Tag>
                  )}
                  {product.isRentable && (
                    <Tag color="orange">可租赁</Tag>
                  )}
                  {product.has3dPreview && (
                    <Tag color="purple">3D预览</Tag>
                  )}
                </Space>
              </div>

              <Divider />

              <div className="product-actions">
                <Row gutter={[16, 16]}>
                  <Col span={24}>
                    <Space size="large">
                      <span>数量:</span>
                      <InputNumber
                        min={1}
                        max={product.stock}
                        value={quantity}
                        onChange={(value) => setQuantity(value || 1)}
                      />
                      <span>库存: {product.stock}</span>
                    </Space>
                  </Col>
                  
                  <Col span={24}>
                    <Space wrap>
                      <Button 
                        type="primary" 
                        size="large"
                        icon={<ShoppingCartOutlined />}
                        onClick={handleAddToCart}
                      >
                        加入购物车
                      </Button>
                      
                      {product.has3dPreview && (
                        <Button 
                          size="large"
                          icon={<ExperimentOutlined />}
                          className="ar-vr-button"
                          onClick={() => handleARVRExperience('AR')}
                        >
                          AR体验
                        </Button>
                      )}
                      
                      {product.vrExperienceUrl && (
                        <Button 
                          size="large"
                          icon={<ExperimentOutlined />}
                          className="ar-vr-button"
                          onClick={() => handleARVRExperience('VR')}
                        >
                          VR体验
                        </Button>
                      )}
                      
                      <Button 
                        size="large"
                        icon={<TeamOutlined />}
                        onClick={handleCollaboration}
                      >
                        协作购物
                      </Button>
                      
                      {product.isRecyclable && (
                        <Button 
                          size="large"
                          icon={<RecycleOutlined />}
                          onClick={handleRecycle}
                        >
                          回收
                        </Button>
                      )}
                      
                      <Button 
                        size="large"
                        icon={<HeartOutlined />}
                        onClick={async () => {
                          try {
                            await ApiService.addToWishlist(product.id)
                            message.success('已添加到愿望清单')
                          } catch (error: any) {
                            message.error(error.message || '添加失败')
                          }
                        }}
                      >
                        加入愿望清单
                      </Button>
                    </Space>
                  </Col>
                </Row>
              </div>

              <Divider />

              <div className="product-stats">
                <Row gutter={[16, 16]}>
                  <Col span={8}>
                    <div className="stat-item">
                      <HeartOutlined />
                      <span>{product.likeCount || 0}</span>
                      <span>喜欢</span>
                    </div>
                  </Col>
                  <Col span={8}>
                    <div className="stat-item">
                      <ShareAltOutlined />
                      <span>{product.shareCount || 0}</span>
                      <span>分享</span>
                    </div>
                  </Col>
                  <Col span={8}>
                    <div className="stat-item">
                      <EyeOutlined />
                      <span>{product.viewCount || 0}</span>
                      <span>浏览</span>
                    </div>
                  </Col>
                </Row>
              </div>
            </div>
          </Card>
        </Col>
      </Row>

      {/* 商品详情标签页 */}
      <Card style={{ marginTop: 24 }}>
        <Tabs defaultActiveKey="details">
          <TabPane tab="商品详情" key="details">
            <div className="product-details-content">
              <h3>商品介绍</h3>
              <p>{product.description}</p>
              
              <h3>适用场景</h3>
              <ul>
                {product.suitableScenarios?.map(scenario => (
                  <li key={scenario}>{scenario}</li>
                ))}
              </ul>
              
              <h3>生活方式标签</h3>
              <p>{product.lifestyleTags}</p>
              
              {product.seasonality && (
                <>
                  <h3>季节性</h3>
                  <p>{product.seasonality}</p>
                </>
              )}
            </div>
          </TabPane>
          
          <TabPane tab="AR/VR体验" key="experience">
            <div className="ar-vr-content">
              <h3>沉浸式体验</h3>
              <p>通过AR/VR技术，您可以：</p>
              <ul>
                <li>虚拟试用商品</li>
                <li>3D查看商品细节</li>
                <li>在虚拟环境中体验商品</li>
                <li>与朋友一起体验</li>
              </ul>
              
              <Space>
                {product.arModelUrl && (
                  <Button 
                    type="primary"
                    icon={<ExperimentOutlined />}
                    onClick={handleARExperience}
                  >
                    启动AR体验
                  </Button>
                )}
                {product.vrExperienceUrl && (
                  <Button 
                    icon={<ExperimentOutlined />}
                    onClick={handleVRExperience}
                  >
                    启动VR体验
                  </Button>
                )}
              </Space>
            </div>
          </TabPane>
          
          <TabPane tab="价值循环" key="recycle">
            <div className="recycle-content">
              <h3>可持续消费</h3>
              <p>该商品支持价值循环：</p>
              <ul>
                <li>可回收利用</li>
                <li>回收价值: ¥{product.recycleValue || 0}</li>
                <li>环保包装</li>
                <li>可持续材料</li>
              </ul>
              
              <Button 
                type="primary"
                icon={<RecycleOutlined />}
                onClick={handleRecycle}
              >
                申请回收
              </Button>
            </div>
          </TabPane>
          
          <TabPane tab="用户评价" key="reviews">
            <div className="reviews-content">
              <Row gutter={[16, 16]}>
                <Col xs={24} lg={8}>
                  <ReviewStats productId={product.id} />
                </Col>
                <Col xs={24} lg={16}>
                  <ReviewForm
                    productId={product.id}
                    onSuccess={() => {
                      // 刷新评价列表
                    }}
                  />
                  <div style={{ marginTop: 24 }}>
                    <ReviewList productId={product.id} />
                  </div>
                </Col>
              </Row>
            </div>
          </TabPane>
          
          <TabPane tab="价格历史" key="price">
            <PriceHistoryChart productId={product.id} days={30} />
          </TabPane>
          
          <TabPane tab="库存管理" key="stock">
            <AlternativeProducts productId={product.id} />
          </TabPane>
          
          <TabPane tab="商品对比" key="comparison">
            <ComparisonTable productIds={[product.id]} />
          </TabPane>
        </Tabs>
      </Card>
      
      {/* AR/VR体验组件 */}
      {showARVR && product && (
        <ARVRExperience
          productId={product.id}
          productName={product.name}
          experienceType={arVrType}
          onClose={() => setShowARVR(false)}
        />
      )}
    </div>
  )
}

export default ProductDetail

