import React from 'react'
import { useParams } from 'react-router-dom'
import { LogisticsTrackingComponent, ShippingAddressManager } from '../components/LogisticsComponents'

const LogisticsPage: React.FC = () => {
  const { orderId } = useParams<{ orderId: string }>()
  const orderIdNum = orderId ? parseInt(orderId) : 0

  return (
    <div style={{ padding: 24, maxWidth: 1200, margin: '0 auto' }}>
      <LogisticsTrackingComponent orderId={orderIdNum} />
      <div style={{ marginTop: 24 }}>
        <ShippingAddressManager />
      </div>
    </div>
  )
}

export default LogisticsPage

