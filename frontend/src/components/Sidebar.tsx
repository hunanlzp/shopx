import React from 'react'
import { Layout, Menu } from 'antd'
import { 
  HomeOutlined,
  ShoppingOutlined,
  BulbOutlined,
  TeamOutlined,
  UserOutlined,
  ExperimentOutlined,
  RecycleOutlined,
  RobotOutlined
} from '@ant-design/icons'
import { useNavigate, useLocation } from 'react-router-dom'

const { Sider } = Layout

const Sidebar: React.FC = () => {
  const navigate = useNavigate()
  const location = useLocation()

  const menuItems = [
    {
      key: '/',
      icon: <HomeOutlined />,
      label: '首页',
    },
    {
      key: '/products',
      icon: <ShoppingOutlined />,
      label: '商品浏览',
    },
    {
      key: '/recommendation',
      icon: <BulbOutlined />,
      label: '智能推荐',
      children: [
        {
          key: '/recommendation/scenario',
          label: '场景推荐',
        },
        {
          key: '/recommendation/lifestyle',
          label: '生活方式推荐',
        },
        {
          key: '/recommendation/predict',
          label: '预测推荐',
        },
      ],
    },
    {
      key: '/collaboration',
      icon: <TeamOutlined />,
      label: '协作购物',
    },
    {
      key: '/ar-vr',
      icon: <ExperimentOutlined />,
      label: 'AR/VR体验',
    },
    {
      key: '/recycle',
      icon: <RecycleOutlined />,
      label: '价值循环',
    },
    {
      key: '/ai-assistant',
      icon: <RobotOutlined />,
      label: 'AI助手',
    },
    {
      key: '/profile',
      icon: <UserOutlined />,
      label: '个人中心',
    },
  ]

  const handleMenuClick = ({ key }: { key: string }) => {
    navigate(key)
  }

  return (
    <Sider 
      width={200} 
      className="sidebar"
      theme="light"
    >
      <div className="sidebar-header">
        <h3>功能导航</h3>
      </div>
      <Menu
        mode="inline"
        selectedKeys={[location.pathname]}
        items={menuItems}
        onClick={handleMenuClick}
        className="sidebar-menu"
      />
    </Sider>
  )
}

export default Sidebar
