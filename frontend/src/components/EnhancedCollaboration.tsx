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
  Badge,
  Dropdown,
  Menu,
  Modal,
  Form,
  Select,
  Switch,
  Slider,
  message,
  Drawer,
  Tabs,
  Divider,
  Progress,
  Alert
} from 'antd'
import {
  TeamOutlined,
  UserOutlined,
  SendOutlined,
  VideoCameraOutlined,
  PhoneOutlined,
  ShareAltOutlined,
  SettingOutlined,
  SoundOutlined,
  SoundFilledOutlined,
  DownloadOutlined,
  HeartOutlined,
  ShoppingCartOutlined,
  BulbOutlined,
  QuestionCircleOutlined,
  ThunderboltOutlined,
  CrownOutlined,
  StarOutlined,
  FireOutlined,
  GiftOutlined,
  ExperimentOutlined,
  PlusOutlined,
  MinusOutlined,
  EyeOutlined,
  CommentOutlined,
  LikeOutlined,
  DislikeOutlined,
  MoreOutlined,
  CloseOutlined,
  FullscreenOutlined,
  PictureOutlined,
  FileTextOutlined,
  LinkOutlined
} from '@ant-design/icons'
import { message } from 'antd'
import { motion, AnimatePresence } from 'framer-motion'
import { useStore } from '../store/useStore'
import { ApiService } from '../services/api'
import { formatTime } from '../utils/utils'
import AnnotationCanvas from './AnnotationCanvas'

const { Title, Paragraph, Text } = Typography
const { TextArea } = Input
const { TabPane } = Tabs

interface CollaborationMessage {
  id: string
  userId: number
  username: string
  avatar?: string
  message: string
  timestamp: string
  type: 'text' | 'image' | 'product_card' | 'annotation' | 'voice'
  metadata?: {
    productId?: number
    productName?: string
    productPrice?: number
    annotation?: {
      x: number
      y: number
      content: string
    }
  }
}

interface CollaborationParticipant {
  id: number
  username: string
  avatar?: string
  role: 'host' | 'participant'
  status: 'online' | 'offline' | 'away'
  joinTime: string
  lastActive: string
}

interface CollaborationSession {
  id: string
  hostUserId: number
  participants: CollaborationParticipant[]
  productId: number
  productName: string
  productPrice: number
  status: 'ACTIVE' | 'ENDED'
  createTime: string
  updateTime: string
}

interface CollaborationProps {
  sessionId?: string
  productId: number
  productName: string
  productPrice: number
  onClose?: () => void
  onProductClick?: (product: any) => void
  onAddToCart?: (product: any) => void
  onLike?: (product: any) => void
  onShare?: (product: any) => void
  className?: string
}

// 协作消息组件
const CollaborationMessage: React.FC<{
  message: CollaborationMessage
  isOwn: boolean
  onProductClick?: (product: any) => void
  onAddToCart?: (product: any) => void
  onLike?: (product: any) => void
  onShare?: (product: any) => void
}> = ({ message, isOwn, onProductClick, onAddToCart, onLike, onShare }) => {
  const renderMessageContent = () => {
    switch (message.type) {
      case 'text':
        return (
          <div style={{
            background: isOwn ? '#1890ff' : '#f5f5f5',
            color: isOwn ? 'white' : 'black',
            padding: '8px 12px',
            borderRadius: '12px',
            maxWidth: '300px',
            wordWrap: 'break-word'
          }}>
            {message.message}
          </div>
        )
      
      case 'product_card':
        return (
          <Card
            size="small"
            style={{ width: '250px' }}
            cover={
              <div style={{ 
                height: '120px', 
                background: '#f0f0f0',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center'
              }}>
                <PictureOutlined style={{ fontSize: '24px', color: '#999' }} />
              </div>
            }
            actions={[
              <Tooltip title="查看详情">
                <Button 
                  size="small" 
                  icon={<EyeOutlined />}
                  onClick={() => onProductClick?.(message.metadata)}
                />
              </Tooltip>,
              <Tooltip title="加入购物车">
                <Button 
                  size="small" 
                  icon={<ShoppingCartOutlined />}
                  onClick={() => onAddToCart?.(message.metadata)}
                />
              </Tooltip>,
              <Tooltip title="喜欢">
                <Button 
                  size="small" 
                  icon={<HeartOutlined />}
                  onClick={() => onLike?.(message.metadata)}
                />
              </Tooltip>
            ]}
          >
            <Card.Meta
              title={message.metadata?.productName || '商品'}
              description={`¥${message.metadata?.productPrice || 0}`}
            />
          </Card>
        )
      
      case 'image':
        return (
          <div style={{
            background: '#f5f5f5',
            padding: '8px',
            borderRadius: '8px',
            maxWidth: '200px'
          }}>
            <div style={{ 
              height: '120px', 
              background: '#e0e0e0',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              borderRadius: '4px'
            }}>
              <PictureOutlined style={{ fontSize: '24px', color: '#999' }} />
            </div>
            <Text type="secondary" style={{ fontSize: '12px' }}>
              {message.message}
            </Text>
          </div>
        )
      
      case 'annotation':
        return (
          <div style={{
            background: '#fff7e6',
            border: '1px solid #ffd591',
            padding: '8px 12px',
            borderRadius: '8px',
            maxWidth: '250px'
          }}>
            <Text strong style={{ fontSize: '12px', color: '#d46b08' }}>
              标注: {message.metadata?.annotation?.content}
            </Text>
            <div style={{ fontSize: '10px', color: '#999', marginTop: '4px' }}>
              位置: ({message.metadata?.annotation?.x}, {message.metadata?.annotation?.y})
            </div>
          </div>
        )
      
      default:
        return (
          <div style={{
            background: '#f5f5f5',
            padding: '8px 12px',
            borderRadius: '12px',
            maxWidth: '300px'
          }}>
            {message.message}
          </div>
        )
    }
  }

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.3 }}
      style={{
        display: 'flex',
        justifyContent: isOwn ? 'flex-end' : 'flex-start',
        marginBottom: '12px',
        alignItems: 'flex-start',
        gap: '8px'
      }}
    >
      {!isOwn && (
        <Avatar 
          src={message.avatar}
          icon={<UserOutlined />}
          size="small"
        />
      )}
      
      <div style={{ maxWidth: '70%' }}>
        {!isOwn && (
          <div style={{ 
            fontSize: '12px', 
            color: '#999', 
            marginBottom: '4px',
            marginLeft: '4px'
          }}>
            {message.username}
          </div>
        )}
        
        {renderMessageContent()}
        
        <div style={{
          fontSize: '10px',
          color: '#999',
          marginTop: '4px',
          textAlign: isOwn ? 'right' : 'left'
        }}>
          {formatTime.fromNow(message.timestamp)}
        </div>
      </div>
      
      {isOwn && (
        <Avatar 
          src={message.avatar}
          icon={<UserOutlined />}
          size="small"
        />
      )}
    </motion.div>
  )
}

// 参与者列表组件
const ParticipantsList: React.FC<{
  participants: CollaborationParticipant[]
  currentUserId: number
}> = ({ participants, currentUserId }) => {
  const getStatusColor = (status: string) => {
    switch (status) {
      case 'online': return 'green'
      case 'away': return 'orange'
      case 'offline': return 'red'
      default: return 'gray'
    }
  }

  const getRoleIcon = (role: string) => {
    switch (role) {
      case 'host': return <CrownOutlined style={{ color: '#faad14' }} />
      case 'participant': return <UserOutlined />
      default: return <UserOutlined />
    }
  }

  return (
    <div>
      <Title level={5} style={{ marginBottom: '12px' }}>
        参与者 ({participants.length})
      </Title>
      
      <List
        size="small"
        dataSource={participants}
        renderItem={(participant) => (
          <List.Item
            style={{ padding: '8px 0' }}
            actions={[
              <Badge 
                status={getStatusColor(participant.status) as any}
                text={
                  <Text type="secondary" style={{ fontSize: '12px' }}>
                    {participant.status === 'online' ? '在线' : 
                     participant.status === 'away' ? '离开' : '离线'}
                  </Text>
                }
              />
            ]}
          >
            <List.Item.Meta
              avatar={
                <Space>
                  {getRoleIcon(participant.role)}
                  <Avatar 
                    src={participant.avatar}
                    icon={<UserOutlined />}
                    size="small"
                  />
                </Space>
              }
              title={
                <Space>
                  <Text strong={participant.id === currentUserId}>
                    {participant.username}
                  </Text>
                  {participant.id === currentUserId && (
                    <Tag size="small" color="blue">我</Tag>
                  )}
                </Space>
              }
              description={
                <Text type="secondary" style={{ fontSize: '12px' }}>
                  加入时间: {formatTime.fromNow(participant.joinTime)}
                </Text>
              }
            />
          </List.Item>
        )}
      />
    </div>
  )
}

// 增强的协作购物主组件
const EnhancedCollaboration: React.FC<CollaborationProps> = ({
  sessionId,
  productId,
  productName,
  productPrice,
  onClose,
  onProductClick,
  onAddToCart,
  onLike,
  onShare,
  className
}) => {
  const { user } = useStore()
  const [session, setSession] = useState<CollaborationSession | null>(null)
  const [messages, setMessages] = useState<CollaborationMessage[]>([])
  const [inputValue, setInputValue] = useState('')
  const [loading, setLoading] = useState(false)
  const [isFullscreen, setIsFullscreen] = useState(false)
  const [showParticipants, setShowParticipants] = useState(false)
  const [showSettings, setShowSettings] = useState(false)
  const [soundEnabled, setSoundEnabled] = useState(false)
  const [isRecording, setIsRecording] = useState(false)
  const [activeTab, setActiveTab] = useState('chat')
  const [isVideoCallActive, setIsVideoCallActive] = useState(false)
  const [isAudioEnabled, setIsAudioEnabled] = useState(false)
  const [isVideoEnabled, setIsVideoEnabled] = useState(false)
  const [localStream, setLocalStream] = useState<MediaStream | null>(null)
  const [remoteStreams, setRemoteStreams] = useState<Map<number, MediaStream>>(new Map())
  const [isScreenSharing, setIsScreenSharing] = useState(false)
  const [screenStream, setScreenStream] = useState<MediaStream | null>(null)
  const [annotations, setAnnotations] = useState<Array<{
    id: string
    type: 'draw' | 'text'
    x: number
    y: number
    content?: string
    path?: Array<{ x: number; y: number }>
    color: string
    lineWidth: number
    userId: number
    username: string
    timestamp: string
  }>>([])
  
  const messagesEndRef = useRef<HTMLDivElement>(null)
  const inputRef = useRef<HTMLTextAreaElement>(null)
  const localVideoRef = useRef<HTMLVideoElement>(null)
  const remoteVideoRefs = useRef<Map<number, HTMLVideoElement>>(new Map())
  const screenShareVideoRef = useRef<HTMLVideoElement>(null)

  // 滚动到底部
  const scrollToBottom = useCallback(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' })
  }, [])

  useEffect(() => {
    scrollToBottom()
  }, [messages, scrollToBottom])

  // 加载协作会话
  useEffect(() => {
    const loadSession = async () => {
      if (sessionId) {
        try {
          const sessionData = await ApiService.getCollaborationSession(sessionId)
          setSession(sessionData)
        } catch (error) {
          console.error('Failed to load session:', error)
        }
      } else {
        // 创建新会话
        if (user?.id) {
          try {
            const newSession = await ApiService.createCollaborationSession(user.id, productId)
            // 这里应该重新加载会话数据
          } catch (error) {
            console.error('Failed to create session:', error)
          }
        }
      }
    }
    
    loadSession()
  }, [sessionId, user?.id, productId])

  // 发送消息
  const sendMessage = useCallback(async (content: string, type: 'text' | 'product_card' = 'text') => {
    if (!content.trim() || !user?.id) return

    const message: CollaborationMessage = {
      id: Date.now().toString(),
      userId: user.id,
      username: user.username,
      avatar: user.avatar,
      message: content.trim(),
      timestamp: new Date().toISOString(),
      type,
      metadata: type === 'product_card' ? {
        productId,
        productName,
        productPrice
      } : undefined
    }

    setMessages(prev => [...prev, message])
    setInputValue('')
    setLoading(true)

    try {
      // 这里应该发送到WebSocket或API
      // await ApiService.sendCollaborationMessage(sessionId, message)
      
      setTimeout(() => {
        setLoading(false)
      }, 500)
    } catch (error) {
      console.error('Failed to send message:', error)
      setLoading(false)
    }
  }, [user, productId, productName, productPrice])

  // 处理键盘事件
  const handleKeyPress = useCallback((e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault()
      sendMessage(inputValue)
    }
  }, [inputValue, sendMessage])

  // 发送商品卡片
  const sendProductCard = useCallback(() => {
    sendMessage(`分享商品: ${productName}`, 'product_card')
  }, [sendMessage, productName])

  // 邀请用户
  const inviteUser = useCallback(() => {
    Modal.confirm({
      title: '邀请用户',
      content: '请输入要邀请的用户名或邮箱',
      onOk: () => {
        message.success('邀请已发送')
      }
    })
  }, [])

  // 结束会话
  const endSession = useCallback(async () => {
    if (sessionId) {
      try {
        // 停止所有媒体流
        if (localStream) {
          localStream.getTracks().forEach(track => track.stop())
          setLocalStream(null)
        }
        if (screenStream) {
          screenStream.getTracks().forEach(track => track.stop())
          setScreenStream(null)
        }
        remoteStreams.forEach(stream => {
          stream.getTracks().forEach(track => track.stop())
        })
        setRemoteStreams(new Map())
        setIsVideoCallActive(false)
        setIsScreenSharing(false)
        
        await ApiService.endCollaborationSession(sessionId)
        message.success('会话已结束')
        onClose?.()
      } catch (error) {
        console.error('Failed to end session:', error)
      }
    }
  }, [sessionId, onClose, localStream, remoteStreams])

  // 启动音视频通话
  const startVideoCall = useCallback(async () => {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({
        video: true,
        audio: true
      })
      
      setLocalStream(stream)
      setIsVideoCallActive(true)
      setIsAudioEnabled(true)
      setIsVideoEnabled(true)
      
      if (localVideoRef.current) {
        localVideoRef.current.srcObject = stream
      }
      
      // 通过WebSocket发送信令，通知其他参与者
      // 这里应该通过WebSocket发送offer/answer等信令
      message.success('音视频通话已启动')
    } catch (error) {
      console.error('启动音视频通话失败:', error)
      message.error('无法访问摄像头或麦克风，请检查权限设置')
    }
  }, [])

  // 停止音视频通话
  const stopVideoCall = useCallback(() => {
    if (localStream) {
      localStream.getTracks().forEach(track => track.stop())
      setLocalStream(null)
    }
    remoteStreams.forEach(stream => {
      stream.getTracks().forEach(track => track.stop())
    })
    setRemoteStreams(new Map())
    setIsVideoCallActive(false)
    setIsAudioEnabled(false)
    setIsVideoEnabled(false)
    message.info('音视频通话已结束')
  }, [localStream, remoteStreams])

  // 切换音频
  const toggleAudio = useCallback(() => {
    if (localStream) {
      const audioTracks = localStream.getAudioTracks()
      audioTracks.forEach(track => {
        track.enabled = !track.enabled
      })
      setIsAudioEnabled(!isAudioEnabled)
    }
  }, [localStream, isAudioEnabled])

  // 切换视频
  const toggleVideo = useCallback(() => {
    if (localStream) {
      const videoTracks = localStream.getVideoTracks()
      videoTracks.forEach(track => {
        track.enabled = !track.enabled
      })
      setIsVideoEnabled(!isVideoEnabled)
    }
  }, [localStream, isVideoEnabled])

  // 开始屏幕共享
  const startScreenShare = useCallback(async () => {
    try {
      const stream = await navigator.mediaDevices.getDisplayMedia({
        video: true,
        audio: true
      })
      
      setScreenStream(stream)
      setIsScreenSharing(true)
      
      if (screenShareVideoRef.current) {
        screenShareVideoRef.current.srcObject = stream
      }
      
      // 监听屏幕共享结束
      stream.getVideoTracks()[0].addEventListener('ended', () => {
        stopScreenShare()
      })
      
      message.success('屏幕共享已开始')
    } catch (error) {
      console.error('启动屏幕共享失败:', error)
      message.error('无法启动屏幕共享，请检查权限设置')
    }
  }, [])

  // 停止屏幕共享
  const stopScreenShare = useCallback(() => {
    if (screenStream) {
      screenStream.getTracks().forEach(track => track.stop())
      setScreenStream(null)
    }
    setIsScreenSharing(false)
    message.info('屏幕共享已结束')
  }, [screenStream])

  return (
    <div className={className}>
      <Card
        title={
          <Space>
            <TeamOutlined style={{ color: '#1890ff' }} />
            <span>协作购物</span>
            {session && (
              <Tag color="green">
                {session.participants.length} 人在线
              </Tag>
            )}
          </Space>
        }
        extra={
          <Space>
            <Tooltip title="参与者">
              <Button 
                icon={<TeamOutlined />} 
                size="small"
                onClick={() => setShowParticipants(true)}
              />
            </Tooltip>
            <Tooltip title="邀请用户">
              <Button 
                icon={<PlusOutlined />} 
                size="small"
                onClick={inviteUser}
              />
            </Tooltip>
            <Tooltip title="设置">
              <Button 
                icon={<SettingOutlined />} 
                size="small"
                onClick={() => setShowSettings(true)}
              />
            </Tooltip>
            <Tooltip title={isFullscreen ? '退出全屏' : '全屏'}>
              <Button 
                icon={<FullscreenOutlined />} 
                size="small"
                onClick={() => setIsFullscreen(!isFullscreen)}
              />
            </Tooltip>
            <Tooltip title="关闭">
              <Button 
                icon={<CloseOutlined />} 
                size="small"
                danger
                onClick={onClose}
              />
            </Tooltip>
          </Space>
        }
        style={{ 
          height: isFullscreen ? '100vh' : '600px',
          position: isFullscreen ? 'fixed' : 'relative',
          top: isFullscreen ? 0 : 'auto',
          left: isFullscreen ? 0 : 'auto',
          zIndex: isFullscreen ? 1000 : 'auto',
          width: isFullscreen ? '100vw' : '100%'
        }}
        bodyStyle={{ 
          height: isFullscreen ? 'calc(100vh - 57px)' : '500px', 
          display: 'flex', 
          flexDirection: 'column',
          padding: '16px'
        }}
      >
        <Tabs 
          activeKey={activeTab} 
          onChange={setActiveTab}
          style={{ flex: 1, display: 'flex', flexDirection: 'column' }}
        >
          <TabPane tab="聊天" key="chat">
            <div style={{ 
              flex: 1, 
              overflowY: 'auto', 
              marginBottom: '16px',
              padding: '0 8px',
              display: 'flex',
              flexDirection: 'column'
            }}>
              {messages.length === 0 ? (
                <Empty
                  description="开始协作购物吧！"
                  image={Empty.PRESENTED_IMAGE_SIMPLE}
                >
                  <Space>
                    <Button 
                      type="primary" 
                      icon={<ShoppingCartOutlined />}
                      onClick={sendProductCard}
                    >
                      分享商品
                    </Button>
                    <Button 
                      icon={<TeamOutlined />}
                      onClick={inviteUser}
                    >
                      邀请好友
                    </Button>
                  </Space>
                </Empty>
              ) : (
                <>
                  {messages.map((message) => (
                    <CollaborationMessage
                      key={message.id}
                      message={message}
                      isOwn={message.userId === user?.id}
                      onProductClick={onProductClick}
                      onAddToCart={onAddToCart}
                      onLike={onLike}
                      onShare={onShare}
                    />
                  ))}
                  
                  <div ref={messagesEndRef} />
                </>
              )}
            </div>

            {/* 输入区域 */}
            <div style={{ borderTop: '1px solid #f0f0f0', paddingTop: '16px' }}>
              <Space.Compact style={{ width: '100%', marginBottom: '8px' }}>
                <TextArea
                  ref={inputRef}
                  value={inputValue}
                  onChange={(e) => setInputValue(e.target.value)}
                  onKeyPress={handleKeyPress}
                  placeholder="输入消息..."
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
              
              <Space>
                <Button 
                  size="small" 
                  icon={<ShoppingCartOutlined />}
                  onClick={sendProductCard}
                >
                  商品
                </Button>
                <Button 
                  size="small" 
                  icon={<PictureOutlined />}
                >
                  图片
                </Button>
                <Button 
                  size="small" 
                  icon={<FileTextOutlined />}
                >
                  文件
                </Button>
                <Button 
                  size="small" 
                  icon={<LinkOutlined />}
                >
                  链接
                </Button>
              </Space>
            </div>
          </TabPane>

          <TabPane tab="商品" key="product">
            <div style={{ textAlign: 'center', padding: '40px 0' }}>
              <Card
                style={{ maxWidth: '300px', margin: '0 auto' }}
                cover={
                  <div style={{ 
                    height: '200px', 
                    background: '#f0f0f0',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center'
                  }}>
                    <PictureOutlined style={{ fontSize: '48px', color: '#999' }} />
                  </div>
                }
                actions={[
                  <Tooltip title="查看详情">
                    <Button 
                      icon={<EyeOutlined />}
                      onClick={() => onProductClick?.({ id: productId, name: productName, price: productPrice })}
                    />
                  </Tooltip>,
                  <Tooltip title="加入购物车">
                    <Button 
                      icon={<ShoppingCartOutlined />}
                      onClick={() => onAddToCart?.({ id: productId, name: productName, price: productPrice })}
                    />
                  </Tooltip>,
                  <Tooltip title="喜欢">
                    <Button 
                      icon={<HeartOutlined />}
                      onClick={() => onLike?.({ id: productId, name: productName, price: productPrice })}
                    />
                  </Tooltip>,
                  <Tooltip title="分享">
                    <Button 
                      icon={<ShareAltOutlined />}
                      onClick={() => onShare?.({ id: productId, name: productName, price: productPrice })}
                    />
                  </Tooltip>
                ]}
              >
                <Card.Meta
                  title={productName}
                  description={`¥${productPrice}`}
                />
              </Card>
            </div>
          </TabPane>

          <TabPane tab="标注" key="annotations">
            <div style={{ padding: '16px' }}>
              <AnnotationCanvas
                width={800}
                height={600}
                productImage={undefined} // 可以从商品信息获取
                userId={user?.id || 0}
                username={user?.username || '用户'}
                annotations={annotations}
                onAnnotationAdd={async (annotation) => {
                  setAnnotations(prev => [...prev, annotation])
                  
                  // 通过WebSocket或API发送标注到服务器
                  if (sessionId && user?.id) {
                    try {
                      await ApiService.addAnnotation(
                        sessionId,
                        user.id,
                        annotation.type === 'text' ? annotation.content || '' : '绘制标注',
                        annotation.x,
                        annotation.y
                      )
                    } catch (error) {
                      console.error('发送标注失败:', error)
                    }
                  }
                }}
              />
            </div>
          </TabPane>
        </Tabs>
      </Card>

      {/* 参与者抽屉 */}
      <Drawer
        title="参与者"
        placement="right"
        onClose={() => setShowParticipants(false)}
        open={showParticipants}
        width={300}
      >
        {session && (
          <ParticipantsList 
            participants={session.participants}
            currentUserId={user?.id || 0}
          />
        )}
      </Drawer>

      {/* 设置抽屉 */}
      <Drawer
        title="协作设置"
        placement="right"
        onClose={() => setShowSettings(false)}
        open={showSettings}
        width={300}
      >
        <Space direction="vertical" style={{ width: '100%' }}>
          <div>
            <Text strong>音效设置</Text>
            <div style={{ marginTop: '8px' }}>
              <Switch 
                checked={soundEnabled}
                onChange={setSoundEnabled}
              />
              <span style={{ marginLeft: '8px' }}>消息提示音</span>
            </div>
          </div>
          
          <Divider />
          
          <div>
            <Text strong>会话管理</Text>
            <div style={{ marginTop: '8px' }}>
              <Button 
                type="primary" 
                danger 
                block
                onClick={endSession}
              >
                结束会话
              </Button>
            </div>
          </div>
        </Space>
      </Drawer>
    </div>
  )
}

export default EnhancedCollaboration
