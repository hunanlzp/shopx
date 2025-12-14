import React, { useState, useEffect } from 'react'
import { Card, Table, Tag, Button, Switch, Modal, Form, Input, message, Spin, Alert, QRCode, Space } from 'antd'
import { SafetyOutlined, MobileOutlined, MailOutlined, LockOutlined, WarningOutlined, CheckCircleOutlined } from '@ant-design/icons'
import ApiService from '../services/api'
import { LoginHistory, TwoFactorAuth, AccountSecurity, SecurityStats } from '../types'

// 登录历史列表组件
export const LoginHistoryList: React.FC<{ page?: number; size?: number }> = ({ page = 1, size = 20 }) => {
  const [loading, setLoading] = useState(false)
  const [history, setHistory] = useState<LoginHistory[]>([])
  const [total, setTotal] = useState(0)

  useEffect(() => {
    loadHistory()
  }, [page, size])

  const loadHistory = async () => {
    try {
      setLoading(true)
      const response = await ApiService.getLoginHistory(page, size)
      if (response.code === 200) {
        setHistory(response.data?.list || [])
        setTotal(response.data?.total || 0)
      }
    } catch (error: any) {
      message.error(error.message || '加载登录历史失败')
    } finally {
      setLoading(false)
    }
  }

  const columns = [
    {
      title: '登录时间',
      dataIndex: 'loginTime',
      key: 'loginTime',
      render: (time: string) => new Date(time).toLocaleString(),
    },
    {
      title: 'IP地址',
      dataIndex: 'ipAddress',
      key: 'ipAddress',
    },
    {
      title: '位置',
      dataIndex: 'location',
      key: 'location',
      render: (location: string) => location || '未知',
    },
    {
      title: '设备',
      dataIndex: 'device',
      key: 'device',
      render: (_: any, record: LoginHistory) => (
        <div>
          <div>{record.device}</div>
          <div style={{ fontSize: 12, color: '#999' }}>{record.browser} / {record.os}</div>
        </div>
      ),
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status: string, record: LoginHistory) => (
        <Space>
          <Tag color={status === 'SUCCESS' ? 'success' : 'error'}>
            {status === 'SUCCESS' ? '成功' : '失败'}
          </Tag>
          {record.isAbnormal && (
            <Tag color="warning" icon={<WarningOutlined />}>异常</Tag>
          )}
        </Space>
      ),
    },
  ]

  return (
    <Card title={<><SafetyOutlined /> 登录历史</>}>
      <Table
        columns={columns}
        dataSource={history}
        loading={loading}
        rowKey="id"
        pagination={{
          current: page,
          pageSize: size,
          total,
          showTotal: (total) => `共 ${total} 条记录`,
        }}
      />
    </Card>
  )
}

// 2FA设置组件
export const TwoFactorAuthSetup: React.FC<{}> = () => {
  const [loading, setLoading] = useState(false)
  const [twoFA, setTwoFA] = useState<TwoFactorAuth | null>(null)
  const [showSetupModal, setShowSetupModal] = useState(false)
  const [setupMethod, setSetupMethod] = useState<'SMS' | 'EMAIL' | 'TOTP'>('SMS')
  const [phoneOrEmail, setPhoneOrEmail] = useState('')
  const [verificationCode, setVerificationCode] = useState('')
  const [backupCodes, setBackupCodes] = useState<string[]>([])

  useEffect(() => {
    load2FA()
  }, [])

  const load2FA = async () => {
    try {
      setLoading(true)
      const response = await ApiService.getAccountSecurity()
      if (response.code === 200 && response.data?.twoFactorAuth) {
        setTwoFA(response.data.twoFactorAuth)
      }
    } catch (error: any) {
      // 忽略错误
    } finally {
      setLoading(false)
    }
  }

  const handleEnable = async () => {
    if (!phoneOrEmail) {
      message.error('请输入手机号或邮箱')
      return
    }

    try {
      setLoading(true)
      const response = await ApiService.enable2FA(setupMethod, phoneOrEmail)
      if (response.code === 200) {
        message.success('双因素认证已启用')
        await load2FA()
        if (setupMethod === 'TOTP' && response.data?.secret) {
          // 显示二维码
          setShowSetupModal(true)
        }
      }
    } catch (error: any) {
      message.error(error.message || '启用双因素认证失败')
    } finally {
      setLoading(false)
    }
  }

  const handleDisable = async () => {
    try {
      setLoading(true)
      const response = await ApiService.disable2FA()
      if (response.code === 200) {
        message.success('双因素认证已禁用')
        await load2FA()
      }
    } catch (error: any) {
      message.error(error.message || '禁用双因素认证失败')
    } finally {
      setLoading(false)
    }
  }

  const handleGenerateBackupCodes = async () => {
    try {
      const response = await ApiService.generateBackupCodes()
      if (response.code === 200) {
        setBackupCodes(response.data || [])
        Modal.info({
          title: '备用验证码',
          content: (
            <div>
              <p>请妥善保管这些备用验证码，每个验证码只能使用一次：</p>
              <div style={{ marginTop: 16 }}>
                {response.data.map((code: string, index: number) => (
                  <div key={index} style={{ fontFamily: 'monospace', marginBottom: 4 }}>
                    {code}
                  </div>
                ))}
              </div>
            </div>
          ),
        })
      }
    } catch (error: any) {
      message.error(error.message || '生成备用验证码失败')
    }
  }

  return (
    <Card
      title={<><LockOutlined /> 双因素认证</>}
      extra={
        twoFA?.isEnabled ? (
          <Button danger onClick={handleDisable} loading={loading}>
            禁用
          </Button>
        ) : (
          <Button type="primary" onClick={() => setShowSetupModal(true)}>
            启用
          </Button>
        )
      }
    >
      {twoFA?.isEnabled ? (
        <div>
          <Alert
            message="双因素认证已启用"
            description={`认证方式: ${twoFA.authMethod === 'SMS' ? '短信' : twoFA.authMethod === 'EMAIL' ? '邮箱' : 'TOTP'}`}
            type="success"
            showIcon
            style={{ marginBottom: 16 }}
          />
          <Button onClick={handleGenerateBackupCodes} style={{ marginTop: 8 }}>
            生成备用验证码
          </Button>
        </div>
      ) : (
        <div>
          <p>双因素认证可以增强账户安全性，建议启用。</p>
        </div>
      )}

      <Modal
        title="设置双因素认证"
        open={showSetupModal}
        onOk={handleEnable}
        onCancel={() => {
          setShowSetupModal(false)
          setPhoneOrEmail('')
          setVerificationCode('')
        }}
        okText="启用"
        cancelText="取消"
      >
        <Form layout="vertical">
          <Form.Item label="认证方式">
            <Space>
              <Button
                type={setupMethod === 'SMS' ? 'primary' : 'default'}
                icon={<MobileOutlined />}
                onClick={() => setSetupMethod('SMS')}
              >
                短信
              </Button>
              <Button
                type={setupMethod === 'EMAIL' ? 'primary' : 'default'}
                icon={<MailOutlined />}
                onClick={() => setSetupMethod('EMAIL')}
              >
                邮箱
              </Button>
              <Button
                type={setupMethod === 'TOTP' ? 'primary' : 'default'}
                onClick={() => setSetupMethod('TOTP')}
              >
                验证器应用
              </Button>
            </Space>
          </Form.Item>
          <Form.Item label={setupMethod === 'SMS' ? '手机号' : setupMethod === 'EMAIL' ? '邮箱' : '密钥'}>
            <Input
              value={phoneOrEmail}
              onChange={(e) => setPhoneOrEmail(e.target.value)}
              placeholder={setupMethod === 'SMS' ? '请输入手机号' : setupMethod === 'EMAIL' ? '请输入邮箱' : '将显示二维码'}
            />
          </Form.Item>
        </Form>
      </Modal>
    </Card>
  )
}

// 安全设置组件
export const SecuritySettings: React.FC<{}> = () => {
  const [loading, setLoading] = useState(false)
  const [settings, setSettings] = useState<AccountSecurity | null>(null)

  useEffect(() => {
    loadSettings()
  }, [])

  const loadSettings = async () => {
    try {
      setLoading(true)
      const response = await ApiService.getAccountSecurity()
      if (response.code === 200) {
        setSettings(response.data)
      }
    } catch (error: any) {
      message.error(error.message || '加载安全设置失败')
    } finally {
      setLoading(false)
    }
  }

  const handleUpdate = async (field: string, value: boolean) => {
    try {
      const updates = { [field]: value }
      const response = await ApiService.updateAccountSecurity(updates)
      if (response.code === 200) {
        message.success('设置已更新')
        await loadSettings()
      }
    } catch (error: any) {
      message.error(error.message || '更新设置失败')
    }
  }

  if (loading) {
    return <Spin />
  }

  if (!settings) {
    return null
  }

  return (
    <Card title={<><SafetyOutlined /> 安全设置</>}>
      <div style={{ marginBottom: 16 }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
          <div>
            <div style={{ fontWeight: 'bold' }}>登录提醒</div>
            <div style={{ color: '#999', fontSize: 12 }}>当账户在新设备登录时发送提醒</div>
          </div>
          <Switch
            checked={settings.enableLoginAlerts}
            onChange={(checked) => handleUpdate('enableLoginAlerts', checked)}
          />
        </div>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div>
            <div style={{ fontWeight: 'bold' }}>交易提醒</div>
            <div style={{ color: '#999', fontSize: 12 }}>当账户发生重要交易时发送提醒</div>
          </div>
          <Switch
            checked={settings.enableTransactionAlerts}
            onChange={(checked) => handleUpdate('enableTransactionAlerts', checked)}
          />
        </div>
      </div>
      <div style={{ marginTop: 16, paddingTop: 16, borderTop: '1px solid #f0f0f0' }}>
        <div><strong>密码强度:</strong> 
          <Tag color={settings.passwordStrength === 'STRONG' ? 'success' : settings.passwordStrength === 'MEDIUM' ? 'warning' : 'error'} style={{ marginLeft: 8 }}>
            {settings.passwordStrength === 'STRONG' ? '强' : settings.passwordStrength === 'MEDIUM' ? '中' : '弱'}
          </Tag>
        </div>
        <div style={{ marginTop: 8, color: '#999', fontSize: 12 }}>
          上次修改: {new Date(settings.lastPasswordChange).toLocaleString()}
        </div>
      </div>
    </Card>
  )
}

// 异常登录提醒组件
export const AbnormalLoginAlert: React.FC<{ loginHistory: LoginHistory[] }> = ({ loginHistory }) => {
  const abnormalLogins = loginHistory.filter(h => h.isAbnormal)

  if (abnormalLogins.length === 0) {
    return null
  }

  return (
    <Alert
      message="检测到异常登录"
      description={
        <div>
          {abnormalLogins.slice(0, 3).map(login => (
            <div key={login.id} style={{ marginTop: 8 }}>
              <strong>{new Date(login.loginTime).toLocaleString()}</strong> - 
              {login.location || '未知位置'} ({login.ipAddress})
            </div>
          ))}
          {abnormalLogins.length > 3 && (
            <div style={{ marginTop: 8, color: '#999' }}>
              还有 {abnormalLogins.length - 3} 条异常登录记录
            </div>
          )}
        </div>
      }
      type="warning"
      showIcon
      icon={<WarningOutlined />}
      style={{ marginBottom: 16 }}
    />
  )
}

