import React, { useState } from 'react'
import { Row, Col } from 'antd'
import { ComparisonTable, ComparisonList } from '../components/ComparisonComponents'

const ComparisonPage: React.FC = () => {
  const [selectedComparison, setSelectedComparison] = useState<any>(null)

  return (
    <div style={{ padding: 24, maxWidth: 1200, margin: '0 auto' }}>
      <Row gutter={[16, 16]}>
        <Col xs={24} lg={8}>
          <ComparisonList onSelect={setSelectedComparison} />
        </Col>
        <Col xs={24} lg={16}>
          {selectedComparison ? (
            <ComparisonTable
              productIds={selectedComparison.productIds}
              onRemove={(productId) => {
                // 处理移除商品
              }}
            />
          ) : (
            <div style={{ textAlign: 'center', padding: 40 }}>
              请选择一个对比列表查看详情
            </div>
          )}
        </Col>
      </Row>
    </div>
  )
}

export default ComparisonPage

