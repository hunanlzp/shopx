import React, { useState } from 'react'
import { Layout, Menu, Avatar, Dropdown, Badge, Button, message } from 'antd'
import { 
  ShoppingCartOutlined, 
  BellOutlined, 
  UserOutlined,
  LogoutOutlined,
  RobotOutlined
} from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { useStore } from '../store/useStore'
import { ApiService } from '../services/api'
import EnhancedAIAssistant from './EnhancedAIAssistant'

const { Header: AntHeader } = Layout

const Header: React.FC = () => {
  const navigate = useNavigate()
  const { user, notifications, cartItems, setUser } = useStore()
  const [aiAssistantVisible, setAiAssistantVisible] = useState(false)

  const handleLogout = async () => {
    try {
      if (user?.id) {
        await ApiService.logout(user.id)
      }
      
      // 清除本地存储
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      
      // 清除全局状态
      setUser(null)
      
      message.success('退出登录成功')
      navigate('/auth')
    } catch (error) {
      console.error('退出登录失败:', error)
      message.error('退出登录失败')
    }
  }

  const userMenuItems = [
    {
      key: 'profile',
      label: '个人资料',
      onClick: () => navigate('/profile'),
    },
    {
      key: 'settings',
      label: '设置',
    },
    {
      type: 'divider' as const,
    },
    {
      key: 'logout',
      label: '退出登录',
      icon: <LogoutOutlined />,
      onClick: handleLogout,
    },
  ]

  return (
    <AntHeader className="header">
      <div className="header-left">
        <div className="logo" onClick={() => navigate('/')}>
          <span className="logo-text">ShopX</span>
          <span className="logo-subtitle">创新电商</span>
        </div>
      </div>
      
      <div className="header-right">
        <Button 
          type="text" 
          icon={<RobotOutlined />}
          onClick={() => setAiAssistantVisible(true)}
        >
          AI助手
        </Button>
        
        <Button 
          type="text" 
          icon={<ShoppingCartOutlined />}
          onClick={() => navigate('/cart')}
        >
          <Badge count={cartItems.length} size="small">
            购物车
          </Badge>
        </Button>
        
        <Button 
          type="text" 
          icon={<BellOutlined />}
          onClick={() => navigate('/notifications')}
        >
          <Badge count={notifications.length} size="small">
            通知
          </Badge>
        </Button>
        
        <Dropdown menu={{ items: userMenuItems }} placement="bottomRight">
          <div className="user-info">
            <Avatar 
              size="small" 
              icon={<UserOutlined />} 
              src={user?.avatar}
            />
            <span className="username">{user?.username || '游客'}</span>
          </div>
        </Dropdown>
      </div>
      
      {/* AI助手组件 - 使用Modal包装 */}
      {aiAssistantVisible && user && (
        <div style={{
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          zIndex: 2000,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          background: 'rgba(0, 0, 0, 0.5)',
          backdropFilter: 'blur(4px)'
        }} onClick={() => setAiAssistantVisible(false)}>
          <div style={{
            width: '90%',
            maxWidth: '800px',
            maxHeight: '80vh',
            background: 'white',
            borderRadius: '20px',
            overflow: 'hidden',
            boxShadow: '0 20px 60px rgba(0, 0, 0, 0.3)'
          }} onClick={(e) => e.stopPropagation()}>
            <EnhancedAIAssistant 
              userId={user.id}
              onProductClick={(product) => {
                navigate(`/products/${product.id}`)
                setAiAssistantVisible(false)
              }}
              onAddToCart={(product) => {
                // 添加到购物车的逻辑
                console.log('Add to cart:', product)
              }}
            />
          </div>
        </div>
      )}
    </AntHeader>
  )
}

export default Header
