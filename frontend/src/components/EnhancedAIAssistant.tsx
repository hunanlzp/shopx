import React, { useState, useRef, useEffect, useCallback, useMemo } from 'react'
import { 
  Card, 
  Input, 
  Button, 
  Space, 
  Typography, 
  Avatar, 
  List, 
  Tag, 
  Tooltip, 
  Spin, 
  Empty,
  Divider,
  Badge,
  Dropdown,
  Menu,
  Modal,
  Form,
  Select,
  Switch,
  Slider,
  message
} from 'antd'
import {
  RobotOutlined,
  UserOutlined,
  SendOutlined,
  ClearOutlined,
  SettingOutlined,
  SoundOutlined,
  SoundFilledOutlined,
  DownloadOutlined,
  ShareAltOutlined,
  HeartOutlined,
  ShoppingCartOutlined,
  BulbOutlined,
  QuestionCircleOutlined,
  ThunderboltOutlined,
  CrownOutlined,
  StarOutlined,
  FireOutlined,
  GiftOutlined,
  ExperimentOutlined
} from '@ant-design/icons'
import { motion, AnimatePresence } from 'framer-motion'
import { useStore } from '../store/useStore'
import { ApiService } from '../services/api'
import { useDebounce } from '../hooks/useHooks'
import { formatTime } from '../utils/utils'

const { Title, Paragraph, Text } = Typography
const { TextArea } = Input

interface AIMessage {
  id: string
  type: 'user' | 'assistant'
  content: string
  timestamp: string
  suggestedProducts?: any[]
  actions?: Array<{
    type: 'product' | 'action' | 'link'
    label: string
    data: any
  }>
  metadata?: {
    confidence?: number
    category?: string
    intent?: string
  }
}

interface AIAssistantProps {
  userId: number
  onProductClick?: (product: any) => void
  onAddToCart?: (product: any) => void
  onLike?: (product: any) => void
  onShare?: (product: any) => void
  className?: string
}

// AI消息组件
const AIMessageComponent: React.FC<{
  message: AIMessage
  onProductClick?: (product: any) => void
  onAddToCart?: (product: any) => void
  onLike?: (product: any) => void
  onShare?: (product: any) => void
}> = ({ message, onProductClick, onAddToCart, onLike, onShare }) => {
  const isUser = message.type === 'user'
  
  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.3 }}
      style={{
        display: 'flex',
        justifyContent: isUser ? 'flex-end' : 'flex-start',
        marginBottom: '16px'
      }}
    >
      <div style={{
        maxWidth: '70%',
        display: 'flex',
        alignItems: 'flex-start',
        gap: '8px',
        flexDirection: isUser ? 'row-reverse' : 'row'
      }}>
        <Avatar 
          icon={isUser ? <UserOutlined /> : <RobotOutlined />}
          style={{
            backgroundColor: isUser ? '#1890ff' : '#52c41a',
            flexShrink: 0
          }}
        />
        
        <div style={{
          background: isUser ? '#1890ff' : '#f5f5f5',
          color: isUser ? 'white' : 'black',
          padding: '12px 16px',
          borderRadius: '18px',
          position: 'relative'
        }}>
          <div style={{ marginBottom: '8px' }}>
            {message.content}
          </div>
          
          {message.metadata && (
            <div style={{ 
              fontSize: '10px', 
              opacity: 0.7,
              marginBottom: '8px'
            }}>
              {message.metadata.confidence && (
                <Tag size="small" color="blue">
                  置信度: {Math.round(message.metadata.confidence * 100)}%
                </Tag>
              )}
              {message.metadata.category && (
                <Tag size="small" color="green">
                  {message.metadata.category}
                </Tag>
              )}
            </div>
          )}
          
          {message.suggestedProducts && message.suggestedProducts.length > 0 && (
            <div style={{ marginTop: '8px' }}>
              <Text style={{ fontSize: '12px', opacity: 0.8 }}>
                推荐商品:
              </Text>
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: '4px', marginTop: '4px' }}>
                {message.suggestedProducts.map((product, index) => (
                  <Tag
                    key={index}
                    color="blue"
                    style={{ cursor: 'pointer' }}
                    onClick={() => onProductClick?.(product)}
                  >
                    {product.name}
                  </Tag>
                ))}
              </div>
            </div>
          )}
          
          {message.actions && message.actions.length > 0 && (
            <div style={{ marginTop: '8px' }}>
              <Space size="small">
                {message.actions.map((action, index) => (
                  <Button
                    key={index}
                    size="small"
                    type="primary"
                    ghost={isUser}
                    onClick={() => {
                      switch (action.type) {
                        case 'product':
                          onProductClick?.(action.data)
                          break
                        case 'action':
                          // 处理其他操作
                          break
                        default:
                          break
                      }
                    }}
                  >
                    {action.label}
                  </Button>
                ))}
              </Space>
            </div>
          )}
          
          <div style={{
            fontSize: '10px',
            opacity: 0.6,
            marginTop: '4px',
            textAlign: 'right'
          }}>
            {formatTime.fromNow(message.timestamp)}
          </div>
        </div>
      </div>
    </motion.div>
  )
}

// 快速操作按钮组件
const QuickActions: React.FC<{
  onAction: (action: string) => void
}> = ({ onAction }) => {
  const quickActions = [
    { key: 'recommend', label: '推荐商品', icon: <BulbOutlined />, color: 'blue' },
    { key: 'compare', label: '商品对比', icon: <ThunderboltOutlined />, color: 'green' },
    { key: 'trending', label: '热门趋势', icon: <FireOutlined />, color: 'red' },
    { key: 'deals', label: '优惠活动', icon: <GiftOutlined />, color: 'orange' },
    { key: 'help', label: '使用帮助', icon: <QuestionCircleOutlined />, color: 'purple' },
    { key: 'experiment', label: 'AR体验', icon: <ExperimentOutlined />, color: 'cyan' }
  ]

  return (
    <div style={{ marginBottom: '16px' }}>
      <Text type="secondary" style={{ fontSize: '12px' }}>快速操作:</Text>
      <div style={{ display: 'flex', flexWrap: 'wrap', gap: '8px', marginTop: '4px' }}>
        {quickActions.map((action) => (
          <Button
            key={action.key}
            size="small"
            icon={action.icon}
            onClick={() => onAction(action.key)}
            style={{ color: action.color }}
          >
            {action.label}
          </Button>
        ))}
      </div>
    </div>
  )
}

// AI设置组件
const AISettings: React.FC<{
  visible: boolean
  onClose: () => void
  settings: any
  onSettingsChange: (settings: any) => void
}> = ({ visible, onClose, settings, onSettingsChange }) => {
  const [form] = Form.useForm()

  useEffect(() => {
    if (visible) {
      form.setFieldsValue(settings)
    }
  }, [visible, settings, form])

  const handleSave = () => {
    form.validateFields().then(values => {
      onSettingsChange(values)
      onClose()
      message.success('设置已保存')
    })
  }

  return (
    <Modal
      title="AI助手设置"
      open={visible}
      onCancel={onClose}
      onOk={handleSave}
      width={500}
    >
      <Form form={form} layout="vertical">
        <Form.Item label="AI个性" name="personality">
          <Select>
            <Select.Option value="friendly">友好型</Select.Option>
            <Select.Option value="professional">专业型</Select.Option>
            <Select.Option value="casual">随意型</Select.Option>
            <Select.Option value="enthusiastic">热情型</Select.Option>
          </Select>
        </Form.Item>
        
        <Form.Item label="响应速度" name="responseSpeed">
          <Slider
            min={1}
            max={5}
            marks={{
              1: '慢',
              3: '正常',
              5: '快'
            }}
          />
        </Form.Item>
        
        <Form.Item label="建议详细程度" name="detailLevel">
          <Slider
            min={1}
            max={5}
            marks={{
              1: '简洁',
              3: '适中',
              5: '详细'
            }}
          />
        </Form.Item>
        
        <Form.Item label="功能开关" name="features">
          <Space direction="vertical">
            <div>
              <Switch defaultChecked /> 商品推荐
            </div>
            <div>
              <Switch defaultChecked /> 价格比较
            </div>
            <div>
              <Switch defaultChecked /> 使用建议
            </div>
            <div>
              <Switch /> 个性化学习
            </div>
          </Space>
        </Form.Item>
      </Form>
    </Modal>
  )
}

// 增强的AI助手主组件
const EnhancedAIAssistant: React.FC<AIAssistantProps> = ({
  userId,
  onProductClick,
  onAddToCart,
  onLike,
  onShare,
  className
}) => {
  const { user } = useStore()
  const [messages, setMessages] = useState<AIMessage[]>([])
  const [inputValue, setInputValue] = useState('')
  const [loading, setLoading] = useState(false)
  const [aiStatus, setAiStatus] = useState<any>(null)
  const [settings, setSettings] = useState({
    personality: 'friendly',
    responseSpeed: 3,
    detailLevel: 3,
    features: {
      recommendations: true,
      priceComparison: true,
      usageTips: true,
      personalization: false
    }
  })
  const [showSettings, setShowSettings] = useState(false)
  const [soundEnabled, setSoundEnabled] = useState(false)
  const [isTyping, setIsTyping] = useState(false)
  
  const messagesEndRef = useRef<HTMLDivElement>(null)
  const inputRef = useRef<HTMLTextAreaElement>(null)
  
  const debouncedInput = useDebounce(inputValue, 300)

  // 滚动到底部
  const scrollToBottom = useCallback(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' })
  }, [])

  useEffect(() => {
    scrollToBottom()
  }, [messages, scrollToBottom])

  // 加载AI状态
  useEffect(() => {
    const loadAIStatus = async () => {
      try {
        const status = await ApiService.getAIStatus()
        setAiStatus(status)
      } catch (error) {
        console.error('Failed to load AI status:', error)
      }
    }
    
    loadAIStatus()
  }, [])

  // 加载聊天历史
  useEffect(() => {
    const loadChatHistory = async () => {
      try {
        const history = await ApiService.getAIChatHistory(userId)
        if (history.data) {
          setMessages(history.data)
        }
      } catch (error) {
        console.error('Failed to load chat history:', error)
      }
    }
    
    loadChatHistory()
  }, [userId])

  // 发送消息
  const sendMessage = useCallback(async (content: string) => {
    if (!content.trim()) return

    const userMessage: AIMessage = {
      id: Date.now().toString(),
      type: 'user',
      content: content.trim(),
      timestamp: new Date().toISOString()
    }

    setMessages(prev => [...prev, userMessage])
    setInputValue('')
    setLoading(true)
    setIsTyping(true)

    try {
      const response = await ApiService.sendAIMessage(userId, content.trim())
      
      const aiMessage: AIMessage = {
        id: (Date.now() + 1).toString(),
        type: 'assistant',
        content: response.response || '抱歉，我暂时无法回答您的问题。',
        timestamp: new Date().toISOString(),
        suggestedProducts: response.suggestedProducts,
        metadata: {
          confidence: 0.85,
          category: 'general',
          intent: 'assist'
        }
      }

      setTimeout(() => {
        setMessages(prev => [...prev, aiMessage])
        setLoading(false)
        setIsTyping(false)
      }, 1000 + Math.random() * 2000) // 模拟AI思考时间

    } catch (error) {
      console.error('Failed to send message:', error)
      const errorMessage: AIMessage = {
        id: (Date.now() + 1).toString(),
        type: 'assistant',
        content: '抱歉，发生了错误，请稍后重试。',
        timestamp: new Date().toISOString(),
        metadata: {
          confidence: 0.1,
          category: 'error',
          intent: 'error'
        }
      }
      
      setMessages(prev => [...prev, errorMessage])
      setLoading(false)
      setIsTyping(false)
    }
  }, [userId])

  // 处理快速操作
  const handleQuickAction = useCallback((action: string) => {
    const actionMessages = {
      recommend: '请为我推荐一些商品',
      compare: '帮我对比一下商品',
      trending: '现在有什么热门商品吗？',
      deals: '有什么优惠活动吗？',
      help: '如何使用这个AI助手？',
      experiment: '我想体验AR功能'
    }
    
    const message = actionMessages[action as keyof typeof actionMessages]
    if (message) {
      sendMessage(message)
    }
  }, [sendMessage])

  // 清空聊天记录
  const clearChat = useCallback(async () => {
    try {
      await ApiService.clearAIChatHistory(userId)
      setMessages([])
      message.success('聊天记录已清空')
    } catch (error) {
      console.error('Failed to clear chat:', error)
      message.error('清空失败')
    }
  }, [userId])

  // 处理键盘事件
  const handleKeyPress = useCallback((e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault()
      sendMessage(inputValue)
    }
  }, [inputValue, sendMessage])

  // 获取AI状态显示
  const getAIStatusDisplay = useMemo(() => {
    if (!aiStatus) return null
    
    return (
      <div style={{ 
        display: 'flex', 
        alignItems: 'center', 
        gap: '8px',
        marginBottom: '16px',
        padding: '8px 12px',
        background: '#f0f9ff',
        borderRadius: '8px',
        border: '1px solid #bae7ff'
      }}>
        <Badge 
          status="processing" 
          text={
            <Space>
              <Text type="secondary">AI助手在线</Text>
              <Tag size="small" color="blue">
                {aiStatus.model || 'GPT-4'}
              </Tag>
              <Tag size="small" color="green">
                响应时间: {aiStatus.responseTime || '< 2s'}
              </Tag>
            </Space>
          }
        />
      </div>
    )
  }, [aiStatus])

  return (
    <div className={className}>
      <Card
        title={
          <Space>
            <RobotOutlined style={{ color: '#52c41a' }} />
            <span>AI购物助手</span>
            {getAIStatusDisplay}
          </Space>
        }
        extra={
          <Space>
            <Tooltip title="设置">
              <Button 
                icon={<SettingOutlined />} 
                size="small"
                onClick={() => setShowSettings(true)}
              />
            </Tooltip>
            <Tooltip title={soundEnabled ? '关闭音效' : '开启音效'}>
              <Button 
                icon={soundEnabled ? <SoundFilledOutlined /> : <SoundOutlined />}
                size="small"
                onClick={() => setSoundEnabled(!soundEnabled)}
              />
            </Tooltip>
            <Tooltip title="清空聊天">
              <Button 
                icon={<ClearOutlined />} 
                size="small"
                onClick={clearChat}
              />
            </Tooltip>
          </Space>
        }
        style={{ height: '600px' }}
        bodyStyle={{ 
          height: '500px', 
          display: 'flex', 
          flexDirection: 'column',
          padding: '16px'
        }}
      >
        {/* 聊天消息区域 */}
        <div style={{ 
          flex: 1, 
          overflowY: 'auto', 
          marginBottom: '16px',
          padding: '0 8px'
        }}>
          {messages.length === 0 ? (
            <Empty
              description="开始与AI助手对话吧！"
              image={Empty.PRESENTED_IMAGE_SIMPLE}
            >
              <QuickActions onAction={handleQuickAction} />
            </Empty>
          ) : (
            <>
              {messages.map((message) => (
                <AIMessageComponent
                  key={message.id}
                  message={message}
                  onProductClick={onProductClick}
                  onAddToCart={onAddToCart}
                  onLike={onLike}
                  onShare={onShare}
                />
              ))}
              
              {isTyping && (
                <motion.div
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  style={{
                    display: 'flex',
                    alignItems: 'center',
                    gap: '8px',
                    marginBottom: '16px'
                  }}
                >
                  <Avatar icon={<RobotOutlined />} style={{ backgroundColor: '#52c41a' }} />
                  <div style={{
                    background: '#f5f5f5',
                    padding: '12px 16px',
                    borderRadius: '18px',
                    display: 'flex',
                    alignItems: 'center',
                    gap: '8px'
                  }}>
                    <Spin size="small" />
                    <Text type="secondary">AI正在思考...</Text>
                  </div>
                </motion.div>
              )}
              
              <div ref={messagesEndRef} />
            </>
          )}
        </div>

        {/* 输入区域 */}
        <div style={{ borderTop: '1px solid #f0f0f0', paddingTop: '16px' }}>
          <Space.Compact style={{ width: '100%' }}>
            <TextArea
              ref={inputRef}
              value={inputValue}
              onChange={(e) => setInputValue(e.target.value)}
              onKeyPress={handleKeyPress}
              placeholder="输入您的问题..."
              autoSize={{ minRows: 1, maxRows: 4 }}
              style={{ flex: 1 }}
            />
            <Button
              type="primary"
              icon={<SendOutlined />}
              onClick={() => sendMessage(inputValue)}
              loading={loading}
              disabled={!inputValue.trim()}
            >
              发送
            </Button>
          </Space.Compact>
          
          <div style={{ marginTop: '8px' }}>
            <QuickActions onAction={handleQuickAction} />
          </div>
        </div>
      </Card>

      {/* AI设置模态框 */}
      <AISettings
        visible={showSettings}
        onClose={() => setShowSettings(false)}
        settings={settings}
        onSettingsChange={setSettings}
      />
    </div>
  )
}

export default EnhancedAIAssistant
