import React, { useEffect, useState } from 'react'
import { 
  Card, 
  Row, 
  Col, 
  Button, 
  Input, 
  Select, 
  Tag, 
  Space, 
  Spin, 
  Empty,
  Pagination,
  Tooltip,
  Badge
} from 'antd'
import { 
  SearchOutlined, 
  ExperimentOutlined, 
  TeamOutlined, 
  HeartOutlined,
  ShareAltOutlined,
  EyeOutlined,
  ShoppingCartOutlined,
  FilterOutlined
} from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { useStore } from '../store/useStore'
import ApiService from '../services/api'
import { Product } from '../store/useStore'
import AdvancedSearchFilter from '../components/AdvancedSearchFilter'

const { Search } = Input
const { Option } = Select

const ProductList: React.FC = () => {
  const navigate = useNavigate()
  const { products, setProducts, addToCart, addNotification } = useStore()
  const [loading, setLoading] = useState(true)
  const [searchText, setSearchText] = useState('')
  const [selectedCategory, setSelectedCategory] = useState('')
  const [selectedScenario, setSelectedScenario] = useState('')
  const [currentPage, setCurrentPage] = useState(1)
  const [pageSize, setPageSize] = useState(12)

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

  const handleProductClick = (product: Product) => {
    navigate(`/products/${product.id}`)
  }

  const handleAddToCart = (product: Product, e: React.MouseEvent) => {
    e.stopPropagation()
    addToCart(product)
    addNotification({
      title: '添加成功',
      message: `${product.name} 已添加到购物车`,
      type: 'success'
    })
  }

  const handleARExperience = (product: Product, e: React.MouseEvent) => {
    e.stopPropagation()
    if (product.arModelUrl) {
      // 这里可以集成AR体验功能
      addNotification({
        title: 'AR体验',
        message: '正在启动AR体验功能...',
        type: 'info'
      })
    }
  }

  const handleCollaboration = (product: Product, e: React.MouseEvent) => {
    e.stopPropagation()
    navigate(`/collaboration?productId=${product.id}`)
  }

  const filteredProducts = products.filter(product => {
    const matchesSearch = product.name.toLowerCase().includes(searchText.toLowerCase()) ||
                         product.description.toLowerCase().includes(searchText.toLowerCase())
    const matchesCategory = !selectedCategory || product.category === selectedCategory
    const matchesScenario = !selectedScenario || 
                           (product.suitableScenarios && product.suitableScenarios.includes(selectedScenario))
    
    return matchesSearch && matchesCategory && matchesScenario
  })

  const paginatedProducts = filteredProducts.slice(
    (currentPage - 1) * pageSize,
    currentPage * pageSize
  )

  const categories = [...new Set(products.map(p => p.category))]
  const scenarios = [
    '运动健身', '时尚潮流', '居家办公', '旅行摄影', 
    '美食烹饪', '艺术设计', '商务办公', '母婴育儿', 
    '环保生活', '科技数码'
  ]

  if (loading) {
    return (
      <div className="loading-spinner">
        <Spin size="large" />
      </div>
    )
  }

  return (
    <div className="product-list-page">
      {/* 搜索和筛选区域 */}
      <Card style={{ marginBottom: 24 }}>
        <Row gutter={[16, 16]} align="middle">
          <Col xs={24} sm={12} md={8}>
            <Search
              placeholder="搜索商品..."
              value={searchText}
              onChange={(e) => setSearchText(e.target.value)}
              onSearch={setSearchText}
              enterButton={<SearchOutlined />}
              size="large"
            />
          </Col>
          <Col xs={12} sm={6} md={4}>
            <Select
              placeholder="选择分类"
              value={selectedCategory}
              onChange={setSelectedCategory}
              allowClear
              style={{ width: '100%' }}
            >
              {categories.map(category => (
                <Option key={category} value={category}>{category}</Option>
              ))}
            </Select>
          </Col>
          <Col xs={12} sm={6} md={4}>
            <Select
              placeholder="选择场景"
              value={selectedScenario}
              onChange={setSelectedScenario}
              allowClear
              style={{ width: '100%' }}
            >
              {scenarios.map(scenario => (
                <Option key={scenario} value={scenario}>{scenario}</Option>
              ))}
            </Select>
          </Col>
          <Col xs={24} sm={12} md={8}>
            <Space>
              <Button 
                icon={<FilterOutlined />}
                onClick={() => {
                  setSearchText('')
                  setSelectedCategory('')
                  setSelectedScenario('')
                }}
              >
                清除筛选
              </Button>
              <Button 
                type="primary"
                onClick={() => navigate('/recommendation')}
              >
                智能推荐
              </Button>
            </Space>
          </Col>
        </Row>
      </Card>

      {/* 商品网格 */}
      {paginatedProducts.length > 0 ? (
        <>
          <Row gutter={[24, 24]}>
            {paginatedProducts.map(product => (
              <Col xs={24} sm={12} lg={8} xl={6} key={product.id}>
                <Card
                  hoverable
                  className="product-card"
                  cover={
                    <div className="product-image-container">
                      <img
                        alt={product.name}
                        src={`https://picsum.photos/300/200?random=${product.id}`}
                        className="product-image"
                      />
                      <div className="product-overlay">
                        <Space>
                          {product.has3dPreview && (
                            <Tooltip title="AR体验">
                              <Button
                                type="primary"
                                shape="circle"
                                icon={<ExperimentOutlined />}
                                className="ar-vr-button"
                                onClick={(e) => handleARExperience(product, e)}
                              />
                            </Tooltip>
                          )}
                          <Tooltip title="协作购物">
                            <Button
                              type="primary"
                              shape="circle"
                              icon={<TeamOutlined />}
                              onClick={(e) => handleCollaboration(product, e)}
                            />
                          </Tooltip>
                        </Space>
                      </div>
                    </div>
                  }
                  actions={[
                    <Tooltip title="查看详情">
                      <EyeOutlined onClick={() => handleProductClick(product)} />
                    </Tooltip>,
                    <Tooltip title="添加到购物车">
                      <ShoppingCartOutlined onClick={(e) => handleAddToCart(product, e)} />
                    </Tooltip>,
                    <Tooltip title="喜欢">
                      <HeartOutlined />
                    </Tooltip>,
                    <Tooltip title="分享">
                      <ShareAltOutlined />
                    </Tooltip>,
                  ]}
                  onClick={() => handleProductClick(product)}
                >
                  <div className="product-info">
                    <div className="product-title">{product.name}</div>
                    <div className="product-description">{product.description}</div>
                    <div className="product-price">¥{product.price}</div>
                    
                    <div className="product-tags">
                      <Space wrap>
                        {product.suitableScenarios?.slice(0, 2).map(scenario => (
                          <Tag key={scenario} color="blue">{scenario}</Tag>
                        ))}
                        {product.isRecyclable && (
                          <Tag color="green">可回收</Tag>
                        )}
                        {product.isRentable && (
                          <Tag color="orange">可租赁</Tag>
                        )}
                      </Space>
                    </div>

                    <div className="product-stats">
                      <Space>
                        <Badge count={product.likeCount || 0} showZero>
                          <HeartOutlined />
                        </Badge>
                        <Badge count={product.shareCount || 0} showZero>
                          <ShareAltOutlined />
                        </Badge>
                        <Badge count={product.viewCount || 0} showZero>
                          <EyeOutlined />
                        </Badge>
                      </Space>
                    </div>
                  </div>
                </Card>
              </Col>
            ))}
          </Row>

          {/* 分页 */}
          <div style={{ textAlign: 'center', marginTop: 24 }}>
            <Pagination
              current={currentPage}
              total={filteredProducts.length}
              pageSize={pageSize}
              onChange={(page, size) => {
                setCurrentPage(page)
                setPageSize(size || 12)
              }}
              showSizeChanger
              showQuickJumper
              showTotal={(total, range) => 
                `第 ${range[0]}-${range[1]} 条，共 ${total} 条商品`
              }
            />
          </div>
        </>
      ) : (
        <Empty
          description="没有找到符合条件的商品"
          image={Empty.PRESENTED_IMAGE_SIMPLE}
        >
          <Button type="primary" onClick={() => {
            setSearchText('')
            setSelectedCategory('')
            setSelectedScenario('')
          }}>
            查看所有商品
          </Button>
        </Empty>
      )}
    </div>
  )
}

export default ProductList

