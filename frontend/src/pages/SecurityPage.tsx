import React from 'react'
import { Row, Col } from 'antd'
import {
  LoginHistoryList,
  TwoFactorAuthSetup,
  SecuritySettings,
  AbnormalLoginAlert,
} from '../components/SecurityComponents'
import { useStore } from '../store/useStore'

const SecurityPage: React.FC = () => {
  const { loginHistory } = useStore()

  return (
    <div style={{ padding: 24, maxWidth: 1200, margin: '0 auto' }}>
      <AbnormalLoginAlert loginHistory={loginHistory} />
      <Row gutter={[16, 16]}>
        <Col xs={24} lg={12}>
          <TwoFactorAuthSetup />
        </Col>
        <Col xs={24} lg={12}>
          <SecuritySettings />
        </Col>
        <Col xs={24}>
          <LoginHistoryList />
        </Col>
      </Row>
    </div>
  )
}

export default SecurityPage

