import React, { Component, ReactNode } from 'react'
import { Result, Button, Card, Typography, Space } from 'antd'
import { 
  ReloadOutlined, 
  HomeOutlined, 
  BugOutlined,
  ExclamationCircleOutlined,
  CloseCircleOutlined,
  WarningOutlined
} from '@ant-design/icons'

const { Title, Paragraph, Text } = Typography

interface ErrorBoundaryState {
  hasError: boolean
  error: Error | null
  errorInfo: React.ErrorInfo | null
}

interface ErrorBoundaryProps {
  children: ReactNode
  fallback?: ReactNode
  onError?: (error: Error, errorInfo: React.ErrorInfo) => void
}

/**
 * React错误边界组件
 */
class ErrorBoundary extends Component<ErrorBoundaryProps, ErrorBoundaryState> {
  constructor(props: ErrorBoundaryProps) {
    super(props)
    this.state = {
      hasError: false,
      error: null,
      errorInfo: null
    }
  }

  static getDerivedStateFromError(error: Error): ErrorBoundaryState {
    return {
      hasError: true,
      error,
      errorInfo: null
    }
  }

  componentDidCatch(error: Error, errorInfo: React.ErrorInfo) {
    this.setState({
      error,
      errorInfo
    })

    // 调用错误处理回调
    if (this.props.onError) {
      this.props.onError(error, errorInfo)
    }

    // 记录错误到控制台
    console.error('ErrorBoundary caught an error:', error, errorInfo)
  }

  handleReload = () => {
    window.location.reload()
  }

  handleGoHome = () => {
    window.location.href = '/'
  }

  render() {
    if (this.state.hasError) {
      if (this.props.fallback) {
        return this.props.fallback
      }

      return (
        <div style={{ padding: '24px', minHeight: '400px' }}>
          <Card>
            <Result
              status="error"
              title="页面出现错误"
              subTitle="抱歉，页面遇到了一个错误，请尝试刷新页面或返回首页"
              extra={[
                <Button type="primary" key="reload" icon={<ReloadOutlined />} onClick={this.handleReload}>
                  刷新页面
                </Button>,
                <Button key="home" icon={<HomeOutlined />} onClick={this.handleGoHome}>
                  返回首页
                </Button>
              ]}
            >
              {process.env.NODE_ENV === 'development' && this.state.error && (
                <div style={{ marginTop: '24px', textAlign: 'left' }}>
                  <Title level={5}>错误详情（开发环境）:</Title>
                  <Paragraph code>
                    {this.state.error.toString()}
                  </Paragraph>
                  {this.state.errorInfo && (
                    <Paragraph code>
                      {this.state.errorInfo.componentStack}
                    </Paragraph>
                  )}
                </div>
              )}
            </Result>
          </Card>
        </div>
      )
    }

    return this.props.children
  }
}

/**
 * 网络错误组件
 */
interface NetworkErrorProps {
  onRetry?: () => void
  onGoHome?: () => void
  message?: string
}

export const NetworkError: React.FC<NetworkErrorProps> = ({ 
  onRetry, 
  onGoHome, 
  message = '网络连接失败，请检查网络设置后重试' 
}) => {
  return (
    <Card>
      <Result
        status="error"
        icon={<ExclamationCircleOutlined />}
        title="网络错误"
        subTitle={message}
        extra={[
          onRetry && (
            <Button type="primary" key="retry" icon={<ReloadOutlined />} onClick={onRetry}>
              重试
            </Button>
          ),
          onGoHome && (
            <Button key="home" icon={<HomeOutlined />} onClick={onGoHome}>
              返回首页
            </Button>
          )
        ]}
      />
    </Card>
  )
}

/**
 * 权限错误组件
 */
interface PermissionErrorProps {
  onGoHome?: () => void
  message?: string
}

export const PermissionError: React.FC<PermissionErrorProps> = ({ 
  onGoHome, 
  message = '您没有权限访问此页面' 
}) => {
  return (
    <Card>
      <Result
        status="403"
        title="权限不足"
        subTitle={message}
        extra={[
          onGoHome && (
            <Button type="primary" key="home" icon={<HomeOutlined />} onClick={onGoHome}>
              返回首页
            </Button>
          )
        ]}
      />
    </Card>
  )
}

/**
 * 页面不存在组件
 */
interface NotFoundErrorProps {
  onGoHome?: () => void
  message?: string
}

export const NotFoundError: React.FC<NotFoundErrorProps> = ({ 
  onGoHome, 
  message = '抱歉，您访问的页面不存在' 
}) => {
  return (
    <Card>
      <Result
        status="404"
        title="页面不存在"
        subTitle={message}
        extra={[
          onGoHome && (
            <Button type="primary" key="home" icon={<HomeOutlined />} onClick={onGoHome}>
              返回首页
            </Button>
          )
        ]}
      />
    </Card>
  )
}

/**
 * 服务器错误组件
 */
interface ServerErrorProps {
  onRetry?: () => void
  onGoHome?: () => void
  message?: string
}

export const ServerError: React.FC<ServerErrorProps> = ({ 
  onRetry, 
  onGoHome, 
  message = '服务器内部错误，请稍后重试' 
}) => {
  return (
    <Card>
      <Result
        status="500"
        title="服务器错误"
        subTitle={message}
        extra={[
          onRetry && (
            <Button type="primary" key="retry" icon={<ReloadOutlined />} onClick={onRetry}>
              重试
            </Button>
          ),
          onGoHome && (
            <Button key="home" icon={<HomeOutlined />} onClick={onGoHome}>
              返回首页
            </Button>
          )
        ]}
      />
    </Card>
  )
}

/**
 * 加载错误组件
 */
interface LoadingErrorProps {
  onRetry?: () => void
  message?: string
}

export const LoadingError: React.FC<LoadingErrorProps> = ({ 
  onRetry, 
  message = '数据加载失败' 
}) => {
  return (
    <Card>
      <div style={{ textAlign: 'center', padding: '40px 0' }}>
        <WarningOutlined style={{ fontSize: '48px', color: '#faad14', marginBottom: '16px' }} />
        <Title level={4}>加载失败</Title>
        <Paragraph type="secondary">{message}</Paragraph>
        {onRetry && (
          <Button type="primary" icon={<ReloadOutlined />} onClick={onRetry}>
            重新加载
          </Button>
        )}
      </div>
    </Card>
  )
}

/**
 * 表单验证错误组件
 */
interface ValidationErrorProps {
  errors: Record<string, string>
  onClose?: () => void
}

export const ValidationError: React.FC<ValidationErrorProps> = ({ errors, onClose }) => {
  return (
    <Card 
      size="small" 
      style={{ 
        border: '1px solid #ff4d4f',
        backgroundColor: '#fff2f0'
      }}
    >
      <Space direction="vertical" style={{ width: '100%' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Space>
            <CloseCircleOutlined style={{ color: '#ff4d4f' }} />
            <Text strong style={{ color: '#ff4d4f' }}>表单验证失败</Text>
          </Space>
          {onClose && (
            <Button type="text" size="small" onClick={onClose}>
              ×
            </Button>
          )}
        </div>
        
        <div>
          {Object.entries(errors).map(([field, message]) => (
            <div key={field} style={{ marginBottom: '4px' }}>
              <Text type="secondary">{field}: </Text>
              <Text style={{ color: '#ff4d4f' }}>{message}</Text>
            </div>
          ))}
        </div>
      </Space>
    </Card>
  )
}

export default ErrorBoundary
