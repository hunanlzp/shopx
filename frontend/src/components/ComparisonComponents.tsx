import React, { useState, useEffect } from 'react'
import { Card, Table, Button, Input, Tag, message, Spin, Empty, Modal, Space, ShareAltOutlined } from 'antd'
import { CompareArrowsOutlined, PlusOutlined, DeleteOutlined, ShareAltOutlined as ShareIcon } from '@ant-design/icons'
import ApiService from '../services/api'
import { ProductComparison, ComparisonTable } from '../types'

// 对比表格组件
export const ComparisonTable: React.FC<{
  productIds: number[]
  onRemove?: (productId: number) => void
}> = ({ productIds, onRemove }) => {
  const [loading, setLoading] = useState(false)
  const [tableData, setTableData] = useState<ComparisonTable | null>(null)

  useEffect(() => {
    if (productIds.length > 0) {
      loadTable()
    }
  }, [productIds.join(',')])

  const loadTable = async () => {
    try {
      setLoading(true)
      const response = await ApiService.generateComparisonTable(productIds)
      if (response.code === 200) {
        setTableData(response.data)
      }
    } catch (error: any) {
      message.error(error.message || '加载对比表格失败')
    } finally {
      setLoading(false)
    }
  }

  if (loading) {
    return <Spin size="large" style={{ display: 'block', textAlign: 'center', padding: '40px' }} />
  }

  if (!tableData || tableData.products.length === 0) {
    return <Empty description="暂无对比数据" />
  }

  const columns = [
    {
      title: '属性',
      dataIndex: 'name',
      key: 'name',
      width: 150,
      fixed: 'left' as const,
    },
    ...tableData.products.map((product, index) => ({
      title: (
        <div>
          <div style={{ fontWeight: 'bold' }}>{product.name}</div>
          <div style={{ color: '#1890ff', fontSize: 16, marginTop: 4 }}>
            ¥{product.price.toFixed(2)}
          </div>
          {onRemove && (
            <Button
              type="link"
              danger
              size="small"
              icon={<DeleteOutlined />}
              onClick={() => onRemove(product.id)}
              style={{ marginTop: 4 }}
            >
              移除
            </Button>
          )}
        </div>
      ),
      dataIndex: `value${index}`,
      key: `product${index}`,
      width: 200,
    })),
  ]

  const dataSource = tableData.attributes.map((attr, attrIndex) => ({
    key: attrIndex,
    name: attr.name,
    ...tableData.products.reduce((acc, product, productIndex) => {
      acc[`value${productIndex}`] = attr.values[productIndex] || '-'
      return acc
    }, {} as any),
  }))

  return (
    <Card title={<><CompareArrowsOutlined /> 商品对比</>}>
      <Table
        columns={columns}
        dataSource={dataSource}
        scroll={{ x: 'max-content' }}
        pagination={false}
      />
    </Card>
  )
}

// 对比列表管理组件
export const ComparisonList: React.FC<{
  onSelect?: (comparison: ProductComparison) => void
}> = ({ onSelect }) => {
  const [loading, setLoading] = useState(false)
  const [comparisons, setComparisons] = useState<ProductComparison[]>([])
  const [showCreateModal, setShowCreateModal] = useState(false)
  const [comparisonName, setComparisonName] = useState('')
  const [selectedProductIds, setSelectedProductIds] = useState<number[]>([])

  useEffect(() => {
    loadComparisons()
  }, [])

  const loadComparisons = async () => {
    try {
      setLoading(true)
      const response = await ApiService.getComparisons()
      if (response.code === 200) {
        setComparisons(response.data || [])
      }
    } catch (error: any) {
      message.error(error.message || '加载对比列表失败')
    } finally {
      setLoading(false)
    }
  }

  const handleCreate = async () => {
    if (!comparisonName.trim()) {
      message.error('请输入对比名称')
      return
    }
    if (selectedProductIds.length < 2) {
      message.error('至少需要选择2个商品进行对比')
      return
    }

    try {
      const response = await ApiService.createComparison({
        comparisonName,
        productIds: selectedProductIds,
        isPublic: false,
      })
      if (response.code === 200) {
        message.success('对比列表创建成功')
        setShowCreateModal(false)
        setComparisonName('')
        setSelectedProductIds([])
        await loadComparisons()
      }
    } catch (error: any) {
      message.error(error.message || '创建对比列表失败')
    }
  }

  const handleDelete = async (comparisonId: number) => {
    try {
      await ApiService.deleteComparison(comparisonId)
      message.success('对比列表已删除')
      await loadComparisons()
    } catch (error: any) {
      message.error(error.message || '删除对比列表失败')
    }
  }

  const handleShare = (shareLink: string) => {
    const fullUrl = `${window.location.origin}/comparison/share/${shareLink}`
    navigator.clipboard.writeText(fullUrl).then(() => {
      message.success('分享链接已复制到剪贴板')
    }).catch(() => {
      message.error('复制链接失败')
    })
  }

  return (
    <div>
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between' }}>
        <h3>我的对比列表</h3>
        <Button
          type="primary"
          icon={<PlusOutlined />}
          onClick={() => setShowCreateModal(true)}
        >
          创建对比
        </Button>
      </div>

      {loading ? (
        <Spin />
      ) : comparisons.length === 0 ? (
        <Empty description="暂无对比列表" />
      ) : (
        <div>
          {comparisons.map(comparison => (
            <Card
              key={comparison.id}
              style={{ marginBottom: 16 }}
              actions={[
                <Button type="link" onClick={() => onSelect?.(comparison)}>
                  查看
                </Button>,
                comparison.shareLink && (
                  <Button
                    type="link"
                    icon={<ShareIcon />}
                    onClick={() => handleShare(comparison.shareLink!)}
                  >
                    分享
                  </Button>
                ),
                <Button
                  type="link"
                  danger
                  onClick={() => handleDelete(comparison.id)}
                >
                  删除
                </Button>,
              ].filter(Boolean)}
            >
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div>
                  <div style={{ fontWeight: 'bold', fontSize: 16 }}>{comparison.comparisonName}</div>
                  <div style={{ marginTop: 8, color: '#666' }}>
                    商品数量: {comparison.productIds.length}
                  </div>
                  <div style={{ marginTop: 4, color: '#999', fontSize: 12 }}>
                    创建时间: {new Date(comparison.createTime).toLocaleString()}
                  </div>
                </div>
                <div>
                  {comparison.isPublic && <Tag color="blue">公开</Tag>}
                </div>
              </div>
            </Card>
          ))}
        </div>
      )}

      <Modal
        title="创建对比列表"
        open={showCreateModal}
        onOk={handleCreate}
        onCancel={() => {
          setShowCreateModal(false)
          setComparisonName('')
          setSelectedProductIds([])
        }}
        okText="创建"
        cancelText="取消"
      >
        <div style={{ marginBottom: 16 }}>
          <label>对比名称:</label>
          <Input
            value={comparisonName}
            onChange={(e) => setComparisonName(e.target.value)}
            placeholder="请输入对比名称"
            style={{ marginTop: 8 }}
          />
        </div>
        <div>
          <label>选择商品 (至少2个):</label>
          <div style={{ marginTop: 8, color: '#999', fontSize: 12 }}>
            已选择: {selectedProductIds.length} 个商品
          </div>
          <div style={{ marginTop: 8, color: '#999', fontSize: 12 }}>
            (这里应该有一个商品选择器，暂时用占位符)
          </div>
        </div>
      </Modal>
    </div>
  )
}

// 对比分享组件
export const ComparisonShare: React.FC<{
  comparisonId: number
  shareLink?: string
}> = ({ comparisonId, shareLink }) => {
  const handleShare = () => {
    if (!shareLink) {
      message.warning('该对比列表未生成分享链接')
      return
    }

    const fullUrl = `${window.location.origin}/comparison/share/${shareLink}`
    
    if (navigator.share) {
      navigator.share({
        title: '商品对比',
        text: '查看我的商品对比',
        url: fullUrl,
      }).catch(() => {
        // 用户取消分享
      })
    } else {
      navigator.clipboard.writeText(fullUrl).then(() => {
        message.success('分享链接已复制到剪贴板')
      }).catch(() => {
        message.error('复制链接失败')
      })
    }
  }

  return (
    <Button
      type="primary"
      icon={<ShareIcon />}
      onClick={handleShare}
    >
      分享对比
    </Button>
  )
}

