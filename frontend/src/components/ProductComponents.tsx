// 简化的商品组件
import { useState, useCallback } from 'react'

// 商品接口
interface Product {
  id: number
  name: string
  description: string
  price: number
  category: string
  image?: string
  stock: number
  status: string
  viewCount: number
  likeCount: number
  shareCount: number
  has3dPreview: boolean
  arModelUrl?: string
  vrExperienceUrl?: string
  createTime: string
  updateTime: string
}

// 商品卡片组件
interface ProductCardProps {
  product: Product
  onAddToCart?: (product: Product) => void
  onLike?: (product: Product) => void
  onShare?: (product: Product) => void
  onViewAR?: (product: Product) => void
  onViewVR?: (product: Product) => void
}

const ProductCard = ({
  product,
  onAddToCart,
  onLike,
  onShare,
  onViewAR,
  onViewVR
}: ProductCardProps) => {
  const [loading, setLoading] = useState(false)
  const [liked, setLiked] = useState(false)

  const handleAddToCart = useCallback(async () => {
    setLoading(true)
    try {
      await onAddToCart?.(product)
    } finally {
      setLoading(false)
    }
  }, [product, onAddToCart])

  const handleLike = useCallback(() => {
    setLiked(!liked)
    onLike?.(product)
  }, [liked, product, onLike])

  const handleShare = useCallback(() => {
    onShare?.(product)
  }, [product, onShare])

  const handleViewAR = useCallback(() => {
    onViewAR?.(product)
  }, [product, onViewAR])

  const handleViewVR = useCallback(() => {
    onViewVR?.(product)
  }, [product, onViewVR])

  return (
    <div style={{
      border: '1px solid #d9d9d9',
      borderRadius: '8px',
      padding: '16px',
      background: '#fff',
      boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
      transition: 'all 0.3s ease',
      cursor: 'pointer'
    }}>
      {/* 商品图片 */}
      <div style={{
        height: '200px',
        background: '#f5f5f5',
        borderRadius: '4px',
        marginBottom: '12px',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        overflow: 'hidden'
      }}>
        {product.image ? (
          <img 
            src={product.image} 
            alt={product.name}
            style={{ width: '100%', height: '100%', objectFit: 'cover' }}
          />
        ) : (
          <div style={{ color: '#999', fontSize: '14px' }}>暂无图片</div>
        )}
      </div>

      {/* 商品信息 */}
      <div style={{ marginBottom: '12px' }}>
        <h3 style={{ 
          margin: '0 0 8px 0', 
          fontSize: '16px', 
          fontWeight: 'bold',
          color: '#333'
        }}>
          {product.name}
        </h3>
        <p style={{ 
          margin: '0 0 8px 0', 
          fontSize: '14px', 
          color: '#666',
          lineHeight: '1.4'
        }}>
          {product.description}
        </p>
        <div style={{ 
          fontSize: '18px', 
          fontWeight: 'bold', 
          color: '#52c41a',
          marginBottom: '8px'
        }}>
          ¥{product.price.toFixed(2)}
        </div>
        <div style={{ fontSize: '12px', color: '#999' }}>
          库存: {product.stock} | 分类: {product.category}
        </div>
      </div>

      {/* 操作按钮 */}
      <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
        <button
          onClick={handleAddToCart}
          disabled={loading || product.stock === 0}
          style={{
            flex: 1,
            padding: '8px 16px',
            border: '1px solid #1890ff',
            borderRadius: '4px',
            background: '#1890ff',
            color: 'white',
            cursor: loading || product.stock === 0 ? 'not-allowed' : 'pointer',
            opacity: loading || product.stock === 0 ? 0.6 : 1,
            fontSize: '14px'
          }}
        >
          {loading ? '添加中...' : '加入购物车'}
        </button>
        
        <button
          onClick={handleLike}
          style={{
            padding: '8px 12px',
            border: '1px solid #d9d9d9',
            borderRadius: '4px',
            background: liked ? '#ff4d4f' : '#fff',
            color: liked ? 'white' : '#666',
            cursor: 'pointer',
            fontSize: '14px'
          }}
        >
          {liked ? '❤️' : '🤍'} {product.likeCount}
        </button>
        
        <button
          onClick={handleShare}
          style={{
            padding: '8px 12px',
            border: '1px solid #d9d9d9',
            borderRadius: '4px',
            background: '#fff',
            cursor: 'pointer',
            fontSize: '14px'
          }}
        >
          📤 {product.shareCount}
        </button>
      </div>

      {/* AR/VR体验按钮 */}
      {product.has3dPreview && (
        <div style={{ marginTop: '12px', display: 'flex', gap: '8px' }}>
          {product.arModelUrl && (
            <button
              onClick={handleViewAR}
              style={{
                flex: 1,
                padding: '6px 12px',
                border: '1px solid #52c41a',
                borderRadius: '4px',
                background: '#52c41a',
                color: 'white',
                cursor: 'pointer',
                fontSize: '12px'
              }}
            >
              🥽 AR体验
            </button>
          )}
          {product.vrExperienceUrl && (
            <button
              onClick={handleViewVR}
              style={{
                flex: 1,
                padding: '6px 12px',
                border: '1px solid #722ed1',
                borderRadius: '4px',
                background: '#722ed1',
                color: 'white',
                cursor: 'pointer',
                fontSize: '12px'
              }}
            >
              🥽 VR体验
            </button>
          )}
        </div>
      )}
    </div>
  )
}

// 商品列表组件
interface ProductListProps {
  products: Product[]
  loading?: boolean
  onAddToCart?: (product: Product) => void
  onLike?: (product: Product) => void
  onShare?: (product: Product) => void
  onViewAR?: (product: Product) => void
  onViewVR?: (product: Product) => void
}

const ProductList = ({
  products,
  loading = false,
  onAddToCart,
  onLike,
  onShare,
  onViewAR,
  onViewVR
}: ProductListProps) => {
  if (loading) {
    return (
      <div style={{ 
        display: 'grid', 
        gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', 
        gap: '20px',
        padding: '20px'
      }}>
        {Array.from({ length: 6 }).map((_, index) => (
          <div key={index} style={{
            border: '1px solid #d9d9d9',
            borderRadius: '8px',
            padding: '16px',
            background: '#f5f5f5',
            height: '400px'
          }}>
            <div style={{
              height: '200px',
              background: '#e0e0e0',
              borderRadius: '4px',
              marginBottom: '12px',
              animation: 'pulse 1.5s ease-in-out infinite'
            }} />
            <div style={{
              height: '20px',
              background: '#e0e0e0',
              borderRadius: '4px',
              marginBottom: '8px',
              animation: 'pulse 1.5s ease-in-out infinite'
            }} />
            <div style={{
              height: '16px',
              background: '#e0e0e0',
              borderRadius: '4px',
              marginBottom: '8px',
              animation: 'pulse 1.5s ease-in-out infinite'
            }} />
          </div>
        ))}
        <style>{`
          @keyframes pulse {
            0%, 100% { opacity: 1; }
            50% { opacity: 0.5; }
          }
        `}</style>
      </div>
    )
  }

  if (products.length === 0) {
    return (
      <div style={{
        textAlign: 'center',
        padding: '60px 20px',
        color: '#999'
      }}>
        <div style={{ fontSize: '48px', marginBottom: '16px' }}>📦</div>
        <div style={{ fontSize: '16px' }}>暂无商品</div>
      </div>
    )
  }

  return (
    <div style={{ 
      display: 'grid', 
      gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', 
      gap: '20px',
      padding: '20px'
    }}>
      {products.map(product => (
        <ProductCard
          key={product.id}
          product={product}
          onAddToCart={onAddToCart}
          onLike={onLike}
          onShare={onShare}
          onViewAR={onViewAR}
          onViewVR={onViewVR}
        />
      ))}
    </div>
  )
}

// 搜索过滤器组件
interface SearchFilterProps {
  keyword: string
  onKeywordChange: (keyword: string) => void
  category: string
  onCategoryChange: (category: string) => void
  categories: string[]
  onSearch: () => void
  onReset: () => void
}

const SearchFilter = ({
  keyword,
  onKeywordChange,
  category,
  onCategoryChange,
  categories,
  onSearch,
  onReset
}: SearchFilterProps) => {
  return (
    <div style={{
      padding: '20px',
      background: '#f5f5f5',
      borderRadius: '8px',
      marginBottom: '20px'
    }}>
      <div style={{ display: 'flex', gap: '16px', alignItems: 'center', flexWrap: 'wrap' }}>
        {/* 关键词搜索 */}
        <div style={{ flex: '1', minWidth: '200px' }}>
          <input
            type="text"
            placeholder="搜索商品..."
            value={keyword}
            onChange={(e) => onKeywordChange(e.target.value)}
            style={{
              width: '100%',
              padding: '8px 12px',
              border: '1px solid #d9d9d9',
              borderRadius: '4px',
              fontSize: '14px'
            }}
          />
        </div>

        {/* 分类筛选 */}
        <div style={{ minWidth: '150px' }}>
          <select
            value={category}
            onChange={(e) => onCategoryChange(e.target.value)}
            style={{
              width: '100%',
              padding: '8px 12px',
              border: '1px solid #d9d9d9',
              borderRadius: '4px',
              fontSize: '14px'
            }}
          >
            <option value="">全部分类</option>
            {categories.map(cat => (
              <option key={cat} value={cat}>{cat}</option>
            ))}
          </select>
        </div>

        {/* 操作按钮 */}
        <div style={{ display: 'flex', gap: '8px' }}>
          <button
            onClick={onSearch}
            style={{
              padding: '8px 16px',
              border: '1px solid #1890ff',
              borderRadius: '4px',
              background: '#1890ff',
              color: 'white',
              cursor: 'pointer',
              fontSize: '14px'
            }}
          >
            搜索
          </button>
          <button
            onClick={onReset}
            style={{
              padding: '8px 16px',
              border: '1px solid #d9d9d9',
              borderRadius: '4px',
              background: '#fff',
              cursor: 'pointer',
              fontSize: '14px'
            }}
          >
            重置
          </button>
        </div>
      </div>
    </div>
  )
}

// 分页组件
interface PaginationProps {
  current: number
  total: number
  pageSize: number
  onPageChange: (page: number) => void
  onPageSizeChange: (size: number) => void
}

const Pagination = ({
  current,
  total,
  pageSize,
  onPageChange,
  onPageSizeChange
}: PaginationProps) => {
  const totalPages = Math.ceil(total / pageSize)

  if (totalPages <= 1) return null

  const pages = []
  const startPage = Math.max(1, current - 2)
  const endPage = Math.min(totalPages, current + 2)

  for (let i = startPage; i <= endPage; i++) {
    pages.push(i)
  }

  return (
    <div style={{
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
      gap: '8px',
      padding: '20px'
    }}>
      <button
        onClick={() => onPageChange(current - 1)}
        disabled={current === 1}
        style={{
          padding: '8px 12px',
          border: '1px solid #d9d9d9',
          borderRadius: '4px',
          background: current === 1 ? '#f5f5f5' : '#fff',
          cursor: current === 1 ? 'not-allowed' : 'pointer',
          fontSize: '14px'
        }}
      >
        上一页
      </button>

      {pages.map(page => (
        <button
          key={page}
          onClick={() => onPageChange(page)}
          style={{
            padding: '8px 12px',
            border: '1px solid #d9d9d9',
            borderRadius: '4px',
            background: page === current ? '#1890ff' : '#fff',
            color: page === current ? 'white' : '#333',
            cursor: 'pointer',
            fontSize: '14px'
          }}
        >
          {page}
        </button>
      ))}

      <button
        onClick={() => onPageChange(current + 1)}
        disabled={current === totalPages}
        style={{
          padding: '8px 12px',
          border: '1px solid #d9d9d9',
          borderRadius: '4px',
          background: current === totalPages ? '#f5f5f5' : '#fff',
          cursor: current === totalPages ? 'not-allowed' : 'pointer',
          fontSize: '14px'
        }}
      >
        下一页
      </button>

      <div style={{ marginLeft: '16px', fontSize: '14px', color: '#666' }}>
        共 {total} 条，每页
        <select
          value={pageSize}
          onChange={(e) => onPageSizeChange(Number(e.target.value))}
          style={{
            margin: '0 8px',
            padding: '4px 8px',
            border: '1px solid #d9d9d9',
            borderRadius: '4px',
            fontSize: '14px'
          }}
        >
          <option value={10}>10</option>
          <option value={20}>20</option>
          <option value={50}>50</option>
        </select>
        条
      </div>
    </div>
  )
}

export { ProductCard, ProductList, SearchFilter, Pagination }
export type { Product, ProductCardProps, ProductListProps, SearchFilterProps, PaginationProps }