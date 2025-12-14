import React, { useState } from 'react'
import { Tabs } from 'antd'
import { ReturnForm, ReturnProgress, ReturnHistory } from '../components/ReturnComponents'

const { TabPane } = Tabs

const ReturnPage: React.FC = () => {
  const [activeTab, setActiveTab] = useState('history')

  return (
    <div style={{ padding: 24, maxWidth: 1200, margin: '0 auto' }}>
      <Tabs activeKey={activeTab} onChange={setActiveTab}>
        <TabPane tab="退货历史" key="history">
          <ReturnHistory
            onViewDetail={(returnId) => {
              setActiveTab('detail')
              // 这里应该设置returnId到状态
            }}
          />
        </TabPane>
        <TabPane tab="申请退货" key="apply">
          <ReturnForm
            orderId={0} // 应该从路由或状态获取
            productId={0}
            productName="示例商品"
            quantity={1}
            onSuccess={() => {
              setActiveTab('history')
            }}
          />
        </TabPane>
        <TabPane tab="退货进度" key="detail">
          <ReturnProgress returnId={0} /> {/* 应该从状态获取 */}
        </TabPane>
      </Tabs>
    </div>
  )
}

export default ReturnPage

