import React from 'react'
import { useParams } from 'react-router-dom'
import { Row, Col } from 'antd'
import { PriceHistoryChart, PriceProtectionCard, TotalPriceCalculator } from '../components/PriceComponents'

const PriceHistoryPage: React.FC = () => {
  const { productId } = useParams<{ productId: string }>()
  const productIdNum = productId ? parseInt(productId) : 0

  return (
    <div style={{ padding: 24 }}>
      <Row gutter={[16, 16]}>
        <Col xs={24} lg={16}>
          <PriceHistoryChart productId={productIdNum} days={30} />
        </Col>
        <Col xs={24} lg={8}>
          <TotalPriceCalculator productId={productIdNum} quantity={1} addressId={1} />
        </Col>
      </Row>
    </div>
  )
}

export default PriceHistoryPage

