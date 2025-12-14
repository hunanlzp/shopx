import React from 'react'
import { Spin, Card, Typography, Space } from 'antd'
import { LoadingOutlined } from '@ant-design/icons'

const { Text } = Typography

interface LoadingProps {
  tip?: string
  size?: 'small' | 'default' | 'large'
  spinning?: boolean
  children?: React.ReactNode
  style?: React.CSSProperties
  className?: string
}

/**
 * 通用加载组件
 */
export const Loading: React.FC<LoadingProps> = ({
  tip = '加载中...',
  size = 'default',
  spinning = true,
  children,
  style,
  className
}) => {
  const antIcon = <LoadingOutlined style={{ fontSize: 24 }} spin />

  if (children) {
    return (
      <Spin spinning={spinning} tip={tip} indicator={antIcon} size={size}>
        {children}
      </Spin>
    )
  }

  return (
    <div 
      className={`loading-container ${className || ''}`}
      style={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        padding: '40px',
        ...style
      }}
    >
      <Spin indicator={antIcon} size={size} />
      <Text type="secondary" style={{ marginTop: 16 }}>
        {tip}
      </Text>
    </div>
  )
}

/**
 * 页面加载组件
 */
export const PageLoading: React.FC<{ tip?: string }> = ({ tip = '页面加载中...' }) => {
  return (
    <div style={{ 
      height: '100vh', 
      display: 'flex', 
      alignItems: 'center', 
      justifyContent: 'center' 
    }}>
      <Card style={{ textAlign: 'center', padding: '40px' }}>
        <Loading tip={tip} size="large" />
      </Card>
    </div>
  )
}

/**
 * 数据加载组件
 */
export const DataLoading: React.FC<{ tip?: string }> = ({ tip = '数据加载中...' }) => {
  return (
    <Card style={{ textAlign: 'center', padding: '40px' }}>
      <Loading tip={tip} />
    </Card>
  )
}

/**
 * 按钮加载组件
 */
export const ButtonLoading: React.FC<{ loading?: boolean; children: React.ReactNode }> = ({ 
  loading = false, 
  children 
}) => {
  return (
    <Spin spinning={loading} size="small">
      {children}
    </Spin>
  )
}

/**
 * 表格加载组件
 */
export const TableLoading: React.FC<{ loading?: boolean; children: React.ReactNode }> = ({ 
  loading = false, 
  children 
}) => {
  return (
    <Spin spinning={loading} tip="数据加载中...">
      {children}
    </Spin>
  )
}

/**
 * 骨架屏加载组件
 */
export const SkeletonLoading: React.FC<{ 
  rows?: number
  avatar?: boolean
  title?: boolean
  paragraph?: boolean
}> = ({ 
  rows = 3, 
  avatar = false, 
  title = true, 
  paragraph = true 
}) => {
  return (
    <Card>
      <Space direction="vertical" style={{ width: '100%' }}>
        {avatar && (
          <div style={{ display: 'flex', alignItems: 'center' }}>
            <div 
              style={{ 
                width: 40, 
                height: 40, 
                borderRadius: '50%', 
                backgroundColor: '#f0f0f0',
                marginRight: 16
              }} 
            />
            <div style={{ flex: 1 }}>
              <div 
                style={{ 
                  height: 16, 
                  backgroundColor: '#f0f0f0', 
                  borderRadius: 4,
                  marginBottom: 8
                }} 
              />
              <div 
                style={{ 
                  height: 12, 
                  backgroundColor: '#f0f0f0', 
                  borderRadius: 4,
                  width: '60%'
                }} 
              />
            </div>
          </div>
        )}
        
        {title && (
          <div 
            style={{ 
              height: 20, 
              backgroundColor: '#f0f0f0', 
              borderRadius: 4,
              width: '40%'
            }} 
          />
        )}
        
        {paragraph && Array.from({ length: rows }).map((_, index) => (
          <div 
            key={index}
            style={{ 
              height: 14, 
              backgroundColor: '#f0f0f0', 
              borderRadius: 4,
              width: index === rows - 1 ? '60%' : '100%'
            }} 
          />
        ))}
      </Space>
    </Card>
  )
}

/**
 * 卡片加载组件
 */
export const CardLoading: React.FC<{ count?: number }> = ({ count = 3 }) => {
  return (
    <div style={{ display: 'flex', gap: 16, flexWrap: 'wrap' }}>
      {Array.from({ length: count }).map((_, index) => (
        <Card key={index} style={{ width: 300, height: 200 }}>
          <SkeletonLoading rows={2} avatar={true} />
        </Card>
      ))}
    </div>
  )
}

/**
 * 列表加载组件
 */
export const ListLoading: React.FC<{ count?: number }> = ({ count = 5 }) => {
  return (
    <Card>
      <Space direction="vertical" style={{ width: '100%' }}>
        {Array.from({ length: count }).map((_, index) => (
          <div key={index} style={{ display: 'flex', alignItems: 'center', padding: '8px 0' }}>
            <div 
              style={{ 
                width: 40, 
                height: 40, 
                borderRadius: '50%', 
                backgroundColor: '#f0f0f0',
                marginRight: 16
              }} 
            />
            <div style={{ flex: 1 }}>
              <div 
                style={{ 
                  height: 16, 
                  backgroundColor: '#f0f0f0', 
                  borderRadius: 4,
                  marginBottom: 8,
                  width: '70%'
                }} 
              />
              <div 
                style={{ 
                  height: 12, 
                  backgroundColor: '#f0f0f0', 
                  borderRadius: 4,
                  width: '50%'
                }} 
              />
            </div>
          </div>
        ))}
      </Space>
    </Card>
  )
}

export default Loading
