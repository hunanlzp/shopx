import React, { useState } from 'react'
import { Tabs, Row, Col } from 'antd'
import {
  TicketForm,
  TicketList,
  FAQSearch,
  FAQList,
} from '../components/CustomerServiceComponents'

const { TabPane } = Tabs

const CustomerServicePage: React.FC = () => {
  const [activeTab, setActiveTab] = useState('tickets')

  return (
    <div style={{ padding: 24, maxWidth: 1200, margin: '0 auto' }}>
      <Tabs activeKey={activeTab} onChange={setActiveTab}>
        <TabPane tab="我的工单" key="tickets">
          <div style={{ marginBottom: 24 }}>
            <TicketForm
              onSuccess={() => {
                setActiveTab('tickets')
                // 刷新工单列表
              }}
            />
          </div>
          <TicketList />
        </TabPane>
        <TabPane tab="常见问题" key="faq">
          <Row gutter={[16, 16]}>
            <Col xs={24} lg={8}>
              <FAQSearch />
            </Col>
            <Col xs={24} lg={16}>
              <FAQList />
            </Col>
          </Row>
        </TabPane>
      </Tabs>
    </div>
  )
}

export default CustomerServicePage

