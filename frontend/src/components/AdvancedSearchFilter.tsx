import React, { useState, useEffect } from 'react'
import {
  Card,
  Row,
  Col,
  Input,
  Select,
  Slider,
  Button,
  Checkbox,
  Space,
  Collapse,
  Tag,
  Popover,
  message
} from 'antd'
import {
  FilterOutlined,
  SaveOutlined,
  DeleteOutlined,
  HistoryOutlined,
  SearchOutlined
} from '@ant-design/icons'
import { ApiService } from '../services/api'

const { Search } = Input
const { Option } = Select
const { Panel } = Collapse

interface FilterConditions {
  keyword?: string
  minPrice?: number
  maxPrice?: number
  category?: string
  stockStatus?: 'inStock' | 'outOfStock' | 'all'
  has3dPreview?: boolean
  isRecyclable?: boolean
  sortBy?: 'price' | 'popularity' | 'newest'
  sortOrder?: 'asc' | 'desc'
}

interface AdvancedSearchFilterProps {
  onFilterChange: (filters: FilterConditions) => void
  onSearch: (keyword: string, filters: FilterConditions) => void
  categories?: string[]
}

const AdvancedSearchFilter: React.FC<AdvancedSearchFilterProps> = ({
  onFilterChange,
  onSearch,
  categories = []
}) => {
  const [filters, setFilters] = useState<FilterConditions>({
    stockStatus: 'all',
    sortBy: 'newest',
    sortOrder: 'desc'
  })
  const [keyword, setKeyword] = useState('')
  const [priceRange, setPriceRange] = useState<[number, number]>([0, 10000])
  const [suggestions, setSuggestions] = useState<string[]>([])
  const [searchHistory, setSearchHistory] = useState<any[]>([])
  const [savedFilters, setSavedFilters] = useState<any[]>([])
  const [showSuggestions, setShowSuggestions] = useState(false)

  useEffect(() => {
    loadSearchHistory()
    loadSavedFilters()
  }, [])

  const loadSearchHistory = async () => {
    try {
      const response = await ApiService.getSearchHistory(20)
      setSearchHistory(response.data || [])
    } catch (error) {
      console.error('加载搜索历史失败:', error)
    }
  }

  const loadSavedFilters = async () => {
    try {
      const response = await ApiService.getSavedFilters()
      setSavedFilters(response.data || [])
    } catch (error) {
      console.error('加载保存的筛选条件失败:', error)
    }
  }

  const handleKeywordChange = async (value: string) => {
    setKeyword(value)
    if (value.length >= 2) {
      try {
        const response = await ApiService.getSearchSuggestions(value, 10)
        setSuggestions(response.data || [])
        setShowSuggestions(true)
      } catch (error) {
        console.error('获取搜索建议失败:', error)
      }
    } else {
      setShowSuggestions(false)
    }
  }

  const handleFilterChange = (key: keyof FilterConditions, value: any) => {
    const newFilters = { ...filters, [key]: value }
    setFilters(newFilters)
    onFilterChange(newFilters)
  }

  const handlePriceRangeChange = (values: [number, number]) => {
    setPriceRange(values)
    handleFilterChange('minPrice', values[0])
    handleFilterChange('maxPrice', values[1])
  }

  const handleSearch = () => {
    const searchFilters = {
      ...filters,
      minPrice: priceRange[0] > 0 ? priceRange[0] : undefined,
      maxPrice: priceRange[1] < 10000 ? priceRange[1] : undefined
    }
    onSearch(keyword, searchFilters)
  }

  const handleSaveFilter = async () => {
    if (!keyword && Object.keys(filters).length === 0) {
      message.warning('请先设置筛选条件')
      return
    }

    const filterName = prompt('请输入筛选条件名称:')
    if (!filterName) return

    try {
      const filterConditions = {
        keyword,
        ...filters,
        minPrice: priceRange[0] > 0 ? priceRange[0] : undefined,
        maxPrice: priceRange[1] < 10000 ? priceRange[1] : undefined
      }
      await ApiService.saveFilter(filterName, filterConditions, false)
      message.success('保存成功')
      loadSavedFilters()
    } catch (error) {
      console.error('保存筛选条件失败:', error)
      message.error('保存失败')
    }
  }

  const handleLoadSavedFilter = (savedFilter: any) => {
    try {
      const conditions = JSON.parse(savedFilter.filterConditions)
      setKeyword(conditions.keyword || '')
      setFilters(conditions)
      if (conditions.minPrice || conditions.maxPrice) {
        setPriceRange([
          conditions.minPrice || 0,
          conditions.maxPrice || 10000
        ])
      }
      onFilterChange(conditions)
      message.success('已加载筛选条件')
    } catch (error) {
      console.error('加载筛选条件失败:', error)
      message.error('加载失败')
    }
  }

  const handleDeleteSavedFilter = async (filterId: number) => {
    try {
      await ApiService.deleteSavedFilter(filterId)
      message.success('删除成功')
      loadSavedFilters()
    } catch (error) {
      console.error('删除筛选条件失败:', error)
      message.error('删除失败')
    }
  }

  const handleHistoryClick = (history: any) => {
    setKeyword(history.keyword)
    if (history.filterConditions) {
      try {
        const conditions = JSON.parse(history.filterConditions)
        setFilters(conditions)
        onFilterChange(conditions)
      } catch (error) {
        console.error('解析筛选条件失败:', error)
      }
    }
  }

  return (
    <Card
      title={
        <Space>
          <FilterOutlined />
          <span>高级筛选</span>
        </Space>
      }
      extra={
        <Space>
          <Popover
            title="搜索历史"
            content={
              <div style={{ maxHeight: 300, overflowY: 'auto' }}>
                {searchHistory.length === 0 ? (
                  <div>暂无搜索历史</div>
                ) : (
                  searchHistory.map((item, index) => (
                    <div
                      key={index}
                      style={{
                        padding: '8px 0',
                        cursor: 'pointer',
                        borderBottom: '1px solid #f0f0f0'
                      }}
                      onClick={() => handleHistoryClick(item)}
                    >
                      <Space>
                        <SearchOutlined />
                        <span>{item.keyword}</span>
                        {item.resultCount > 0 && (
                          <Tag color="blue">{item.resultCount} 个结果</Tag>
                        )}
                      </Space>
                    </div>
                  ))
                )}
              </div>
            }
            trigger="click"
          >
            <Button icon={<HistoryOutlined />} size="small">
              历史
            </Button>
          </Popover>
          <Button
            icon={<SaveOutlined />}
            size="small"
            onClick={handleSaveFilter}
          >
            保存筛选
          </Button>
        </Space>
      }
    >
      <Row gutter={[16, 16]}>
        {/* 关键词搜索 */}
        <Col xs={24} sm={24} md={12}>
          <div style={{ position: 'relative' }}>
            <Search
              placeholder="搜索商品..."
              value={keyword}
              onChange={(e) => handleKeywordChange(e.target.value)}
              onSearch={handleSearch}
              enterButton={<SearchOutlined />}
              size="large"
            />
            {showSuggestions && suggestions.length > 0 && (
              <div
                style={{
                  position: 'absolute',
                  top: '100%',
                  left: 0,
                  right: 0,
                  zIndex: 1000,
                  background: 'white',
                  border: '1px solid #d9d9d9',
                  borderRadius: '4px',
                  boxShadow: '0 2px 8px rgba(0,0,0,0.15)',
                  maxHeight: 200,
                  overflowY: 'auto'
                }}
              >
                {suggestions.map((suggestion, index) => (
                  <div
                    key={index}
                    style={{
                      padding: '8px 12px',
                      cursor: 'pointer',
                      borderBottom: index < suggestions.length - 1 ? '1px solid #f0f0f0' : 'none'
                    }}
                    onClick={() => {
                      setKeyword(suggestion)
                      setShowSuggestions(false)
                      handleSearch()
                    }}
                    onMouseEnter={(e) => {
                      e.currentTarget.style.background = '#f5f5f5'
                    }}
                    onMouseLeave={(e) => {
                      e.currentTarget.style.background = 'white'
                    }}
                  >
                    {suggestion}
                  </div>
                ))}
              </div>
            )}
          </div>
        </Col>

        {/* 保存的筛选条件 */}
        {savedFilters.length > 0 && (
          <Col xs={24} sm={24} md={12}>
            <Select
              placeholder="加载保存的筛选条件"
              style={{ width: '100%' }}
              onSelect={(value) => {
                const filter = savedFilters.find(f => f.id === value)
                if (filter) {
                  handleLoadSavedFilter(filter)
                }
              }}
            >
              {savedFilters.map((filter) => (
                <Option key={filter.id} value={filter.id}>
                  <Space>
                    <span>{filter.filterName}</span>
                    <Button
                      type="text"
                      size="small"
                      icon={<DeleteOutlined />}
                      onClick={(e) => {
                        e.stopPropagation()
                        handleDeleteSavedFilter(filter.id)
                      }}
                    />
                  </Space>
                </Option>
              ))}
            </Select>
          </Col>
        )}
      </Row>

      <Collapse style={{ marginTop: 16 }}>
        <Panel header="价格区间" key="price">
          <Slider
            range
            min={0}
            max={10000}
            step={100}
            value={priceRange}
            onChange={handlePriceRangeChange}
            marks={{
              0: '¥0',
              5000: '¥5000',
              10000: '¥10000+'
            }}
          />
          <div style={{ marginTop: 8 }}>
            <Space>
              <span>价格范围:</span>
              <Tag>¥{priceRange[0]} - ¥{priceRange[1]}</Tag>
            </Space>
          </div>
        </Panel>

        <Panel header="分类和属性" key="category">
          <Row gutter={[16, 16]}>
            <Col xs={24} sm={12}>
              <div style={{ marginBottom: 8 }}>分类</div>
              <Select
                placeholder="选择分类"
                value={filters.category}
                onChange={(value) => handleFilterChange('category', value)}
                allowClear
                style={{ width: '100%' }}
              >
                {categories.map(cat => (
                  <Option key={cat} value={cat}>{cat}</Option>
                ))}
              </Select>
            </Col>
            <Col xs={24} sm={12}>
              <div style={{ marginBottom: 8 }}>库存状态</div>
              <Select
                value={filters.stockStatus}
                onChange={(value) => handleFilterChange('stockStatus', value)}
                style={{ width: '100%' }}
              >
                <Option value="all">全部</Option>
                <Option value="inStock">有货</Option>
                <Option value="outOfStock">缺货</Option>
              </Select>
            </Col>
          </Row>
          <Row gutter={[16, 16]} style={{ marginTop: 16 }}>
            <Col xs={24}>
              <Space>
                <Checkbox
                  checked={filters.has3dPreview}
                  onChange={(e) => handleFilterChange('has3dPreview', e.target.checked)}
                >
                  仅显示有3D预览的商品
                </Checkbox>
                <Checkbox
                  checked={filters.isRecyclable}
                  onChange={(e) => handleFilterChange('isRecyclable', e.target.checked)}
                >
                  仅显示可回收商品
                </Checkbox>
              </Space>
            </Col>
          </Row>
        </Panel>

        <Panel header="排序" key="sort">
          <Row gutter={[16, 16]}>
            <Col xs={24} sm={12}>
              <div style={{ marginBottom: 8 }}>排序方式</div>
              <Select
                value={filters.sortBy}
                onChange={(value) => handleFilterChange('sortBy', value)}
                style={{ width: '100%' }}
              >
                <Option value="newest">最新上架</Option>
                <Option value="price">价格</Option>
                <Option value="popularity">人气</Option>
              </Select>
            </Col>
            {filters.sortBy === 'price' && (
              <Col xs={24} sm={12}>
                <div style={{ marginBottom: 8 }}>排序顺序</div>
                <Select
                  value={filters.sortOrder}
                  onChange={(value) => handleFilterChange('sortOrder', value)}
                  style={{ width: '100%' }}
                >
                  <Option value="asc">从低到高</Option>
                  <Option value="desc">从高到低</Option>
                </Select>
              </Col>
            )}
          </Row>
        </Panel>
      </Collapse>

      <div style={{ marginTop: 16, textAlign: 'right' }}>
        <Button type="primary" onClick={handleSearch} size="large">
          搜索
        </Button>
      </div>
    </Card>
  )
}

export default AdvancedSearchFilter

