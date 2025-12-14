import React, { useState } from 'react'
import { 
  Card, 
  Form, 
  Input, 
  Button, 
  Typography, 
  Space, 
  message,
  Divider,
  Row,
  Col
} from 'antd'
import { 
  UserOutlined, 
  LockOutlined, 
  MailOutlined,
  PhoneOutlined,
  LoginOutlined,
  UserAddOutlined
} from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { useStore } from '../store/useStore'
import { ApiService } from '../services/api'

const { Title, Text } = Typography

interface LoginForm {
  username: string
  password: string
}

interface RegisterForm {
  username: string
  password: string
  confirmPassword: string
  email: string
  phone: string
}

const AuthPage: React.FC = () => {
  const navigate = useNavigate()
  const { setUser, addNotification } = useStore()
  const [isLogin, setIsLogin] = useState(true)
  const [loading, setLoading] = useState(false)

  const handleLogin = async (values: LoginForm) => {
    try {
      setLoading(true)
      const response = await ApiService.login(values.username, values.password)
      
      if (response.code === 200) {
        // 保存token到localStorage
        localStorage.setItem('token', response.token)
        localStorage.setItem('user', JSON.stringify(response.data))
        
        // 更新全局状态
        setUser(response.data)
        
        addNotification({
          title: '登录成功',
          message: `欢迎回来，${response.data.username}！`,
          type: 'success'
        })
        
        // 跳转到首页
        navigate('/')
      } else {
        message.error(response.message)
      }
    } catch (error) {
      console.error('登录失败:', error)
      message.error('登录失败，请稍后重试')
    } finally {
      setLoading(false)
    }
  }

  const handleRegister = async (values: RegisterForm) => {
    if (values.password !== values.confirmPassword) {
      message.error('两次输入的密码不一致')
      return
    }

    try {
      setLoading(true)
      const userData = {
        username: values.username,
        password: values.password,
        email: values.email,
        phone: values.phone
      }
      
      const response = await ApiService.register(userData)
      
      if (response.code === 200) {
        message.success('注册成功，请登录')
        setIsLogin(true)
      } else {
        message.error(response.message)
      }
    } catch (error) {
      console.error('注册失败:', error)
      message.error('注册失败，请稍后重试')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth-page">
      <Row justify="center" align="middle" style={{ minHeight: '100vh', background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' }}>
        <Col xs={22} sm={18} md={12} lg={8} xl={6}>
          <Card className="auth-card">
            <div className="auth-header">
              <Title level={2} style={{ textAlign: 'center', marginBottom: 8 }}>
                ShopX
              </Title>
              <Text type="secondary" style={{ textAlign: 'center', display: 'block', marginBottom: 32 }}>
                创新电商平台
              </Text>
            </div>

            {isLogin ? (
              <Form
                name="login"
                onFinish={handleLogin}
                autoComplete="off"
                size="large"
              >
                <Form.Item
                  name="username"
                  rules={[
                    { required: true, message: '请输入用户名' },
                    { min: 3, message: '用户名至少3个字符' }
                  ]}
                >
                  <Input
                    prefix={<UserOutlined />}
                    placeholder="用户名"
                  />
                </Form.Item>

                <Form.Item
                  name="password"
                  rules={[
                    { required: true, message: '请输入密码' },
                    { min: 6, message: '密码至少6个字符' }
                  ]}
                >
                  <Input.Password
                    prefix={<LockOutlined />}
                    placeholder="密码"
                  />
                </Form.Item>

                <Form.Item>
                  <Button
                    type="primary"
                    htmlType="submit"
                    loading={loading}
                    icon={<LoginOutlined />}
                    block
                  >
                    登录
                  </Button>
                </Form.Item>
              </Form>
            ) : (
              <Form
                name="register"
                onFinish={handleRegister}
                autoComplete="off"
                size="large"
              >
                <Form.Item
                  name="username"
                  rules={[
                    { required: true, message: '请输入用户名' },
                    { min: 3, message: '用户名至少3个字符' },
                    { max: 20, message: '用户名最多20个字符' }
                  ]}
                >
                  <Input
                    prefix={<UserOutlined />}
                    placeholder="用户名"
                  />
                </Form.Item>

                <Form.Item
                  name="email"
                  rules={[
                    { required: true, message: '请输入邮箱' },
                    { type: 'email', message: '请输入有效的邮箱地址' }
                  ]}
                >
                  <Input
                    prefix={<MailOutlined />}
                    placeholder="邮箱"
                  />
                </Form.Item>

                <Form.Item
                  name="phone"
                  rules={[
                    { required: true, message: '请输入手机号' },
                    { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号' }
                  ]}
                >
                  <Input
                    prefix={<PhoneOutlined />}
                    placeholder="手机号"
                  />
                </Form.Item>

                <Form.Item
                  name="password"
                  rules={[
                    { required: true, message: '请输入密码' },
                    { min: 6, message: '密码至少6个字符' },
                    { max: 20, message: '密码最多20个字符' }
                  ]}
                >
                  <Input.Password
                    prefix={<LockOutlined />}
                    placeholder="密码"
                  />
                </Form.Item>

                <Form.Item
                  name="confirmPassword"
                  rules={[
                    { required: true, message: '请确认密码' }
                  ]}
                >
                  <Input.Password
                    prefix={<LockOutlined />}
                    placeholder="确认密码"
                  />
                </Form.Item>

                <Form.Item>
                  <Button
                    type="primary"
                    htmlType="submit"
                    loading={loading}
                    icon={<UserAddOutlined />}
                    block
                  >
                    注册
                  </Button>
                </Form.Item>
              </Form>
            )}

            <Divider />

            <div className="auth-footer">
              <Space direction="vertical" style={{ width: '100%', textAlign: 'center' }}>
                <Text type="secondary">
                  {isLogin ? '还没有账户？' : '已有账户？'}
                </Text>
                <Button
                  type="link"
                  onClick={() => setIsLogin(!isLogin)}
                  style={{ padding: 0 }}
                >
                  {isLogin ? '立即注册' : '立即登录'}
                </Button>
              </Space>
            </div>

            <div className="auth-features" style={{ marginTop: 24 }}>
              <Title level={5} style={{ textAlign: 'center', marginBottom: 16 }}>
                平台特色
              </Title>
              <Row gutter={[8, 8]}>
                <Col span={12}>
                  <Text type="secondary" style={{ fontSize: '12px' }}>
                    🎯 情境化推荐
                  </Text>
                </Col>
                <Col span={12}>
                  <Text type="secondary" style={{ fontSize: '12px' }}>
                    👥 协作购物
                  </Text>
                </Col>
                <Col span={12}>
                  <Text type="secondary" style={{ fontSize: '12px' }}>
                    🥽 AR/VR体验
                  </Text>
                </Col>
                <Col span={12}>
                  <Text type="secondary" style={{ fontSize: '12px' }}>
                    🔄 价值循环
                  </Text>
                </Col>
              </Row>
            </div>
          </Card>
        </Col>
      </Row>
    </div>
  )
}

export default AuthPage
