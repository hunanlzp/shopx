import React, { useState, useEffect } from 'react'
import { 
  Card, 
  Row, 
  Col, 
  Button, 
  Input, 
  Space, 
  Spin, 
  Empty,
  Tag,
  Typography,
  List,
  Avatar,
  Badge,
  message,
  Modal
} from 'antd'
import { 
  TeamOutlined, 
  PlusOutlined,
  SendOutlined,
  UserOutlined,
  MessageOutlined,
  EyeOutlined,
  ShoppingCartOutlined
} from '@ant-design/icons'
import { useSearchParams, useNavigate } from 'react-router-dom'
import { useStore } from '../store/useStore'
import { ApiService } from '../services/api'
import { CollaborationSession, Product } from '../store/useStore'

const { Title, Paragraph } = Typography
const { TextArea } = Input

const Collaboration: React.FC = () => {
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()
  const { addToCart, addNotification } = useStore()
  const [loading, setLoading] = useState(false)
  const [session, setSession] = useState<CollaborationSession | null>(null)
  const [product, setProduct] = useState<Product | null>(null)
  const [chatMessage, setChatMessage] = useState('')
  const [isCreatingSession, setIsCreatingSession] = useState(false)
  const [createSessionModalVisible, setCreateSessionModalVisible] = useState(false)
  const [selectedProductId, setSelectedProductId] = useState<number | null>(null)

  const productId = searchParams.get('productId')
  const currentUserId = 1 // 模拟当前用户ID

  useEffect(() => {
    if (productId) {
      loadProduct(parseInt(productId))
    }
  }, [productId])

  const loadProduct = async (id: number) => {
    try {
      setLoading(true)
      const response = await ApiService.getProductById(id)
      setProduct(response.data)
    } catch (error) {
      console.error('加载商品失败:', error)
      message.error('加载商品失败')
    } finally {
      setLoading(false)
    }
  }

  const createSession = async () => {
    if (!selectedProductId) {
      message.warning('请选择商品')
      return
    }

    try {
      setLoading(true)
      const response = await ApiService.createCollaborationSession(currentUserId, selectedProductId)
      
      const newSession: CollaborationSession = {
        id: response.sessionId,
        hostUserId: currentUserId,
        participantIds: [currentUserId],
        productId: selectedProductId,
        status: 'ACTIVE',
        chatHistory: [],
        annotations: [],
        createTime: new Date().toISOString(),
        updateTime: new Date().toISOString()
      }
      
      setSession(newSession)
      setCreateSessionModalVisible(false)
      addNotification({
        title: '会话创建成功',
        message: `协作购物会话已创建，邀请码: ${response.sessionId}`,
        type: 'success'
      })
    } catch (error) {
      console.error('创建会话失败:', error)
      message.error('创建会话失败')
    } finally {
      setLoading(false)
    }
  }

  const joinSession = async (sessionId: string) => {
    try {
      setLoading(true)
      const response = await ApiService.joinCollaborationSession(sessionId, currentUserId)
      
      if (response.code === 200) {
        // 加载会话信息
        const sessionResponse = await ApiService.getCollaborationSession(sessionId)
        setSession(sessionResponse.data)
        addNotification({
          title: '加入成功',
          message: '您已成功加入协作购物会话',
          type: 'success'
        })
      } else {
        message.error(response.message)
      }
    } catch (error) {
      console.error('加入会话失败:', error)
      message.error('加入会话失败')
    } finally {
      setLoading(false)
    }
  }

  const sendMessage = () => {
    if (!chatMessage.trim() || !session) return

    const newMessage = {
      user: currentUserId,
      message: chatMessage,
      time: new Date().toISOString()
    }

    const updatedSession = {
      ...session,
      chatHistory: [...session.chatHistory, newMessage]
    }

    setSession(updatedSession)
    setChatMessage('')
  }

  const handleAddToCart = (product: Product) => {
    addToCart(product)
    addNotification({
      title: '添加成功',
      message: `${product.name} 已添加到购物车`,
      type: 'success'
    })
  }

  const users = [
    { id: 1, name: 'Alice Wang', avatar: null },
    { id: 2, name: 'Bob Li', avatar: null },
    { id: 3, name: 'Carol Zhang', avatar: null },
    { id: 4, name: 'David Chen', avatar: null },
    { id: 5, name: 'Eve Liu', avatar: null }
  ]

  const getUserName = (userId: number) => {
    return users.find(user => user.id === userId)?.name || `用户${userId}`
  }

  return (
    <div className="collaboration-page">
      <Card>
        <Title level={2}>
          <TeamOutlined /> 协作购物
        </Title>
        <Paragraph>
          与朋友一起购物，实时讨论商品，分享购物体验
        </Paragraph>
      </Card>

      {!session ? (
        <Card style={{ marginTop: 16 }}>
          <Row gutter={[24, 24]}>
            <Col xs={24} lg={12}>
              <Card title="创建协作会话" size="small">
                <Space direction="vertical" style={{ width: '100%' }}>
                  <Button 
                    type="primary" 
                    icon={<PlusOutlined />}
                    onClick={() => setCreateSessionModalVisible(true)}
                    block
                  >
                    创建新会话
                  </Button>
                  
                  <div>
                    <Title level={4}>加入现有会话</Title>
                    <Input
                      placeholder="输入会话ID"
                      onPressEnter={(e) => {
                        const sessionId = e.currentTarget.value
                        if (sessionId) {
                          joinSession(sessionId)
                        }
                      }}
                    />
                  </div>
                </Space>
              </Card>
            </Col>
            
            <Col xs={24} lg={12}>
              <Card title="功能说明" size="small">
                <ul>
                  <li>多人实时浏览商品</li>
                  <li>实时聊天讨论</li>
                  <li>商品标注和评论</li>
                  <li>协作决策购买</li>
                  <li>分享购物体验</li>
                </ul>
              </Card>
            </Col>
          </Row>
        </Card>
      ) : (
        <Row gutter={[16, 16]} style={{ marginTop: 16 }}>
          {/* 商品信息区域 */}
          <Col xs={24} lg={12}>
            <Card title="商品信息" size="small">
              {product ? (
                <div className="product-info">
                  <img
                    src={`https://picsum.photos/400/300?random=${product.id}`}
                    alt={product.name}
                    style={{ width: '100%', borderRadius: '8px', marginBottom: 16 }}
                  />
                  <Title level={4}>{product.name}</Title>
                  <Paragraph>{product.description}</Paragraph>
                  <div style={{ marginBottom: 16 }}>
                    <Space>
                      <span style={{ fontSize: '18px', fontWeight: 'bold', color: '#1890ff' }}>
                        ¥{product.price}
                      </span>
                      {product.isRecyclable && (
                        <Tag color="green">可回收</Tag>
                      )}
                      {product.isRentable && (
                        <Tag color="orange">可租赁</Tag>
                      )}
                    </Space>
                  </div>
                  <Button 
                    type="primary" 
                    icon={<ShoppingCartOutlined />}
                    onClick={() => handleAddToCart(product)}
                    block
                  >
                    加入购物车
                  </Button>
                </div>
              ) : (
                <Spin />
              )}
            </Card>
          </Col>

          {/* 聊天区域 */}
          <Col xs={24} lg={12}>
            <Card 
              title={
                <Space>
                  <TeamOutlined />
                  <span>协作会话</span>
                  <Badge count={session.participantIds.length} />
                </Space>
              }
              size="small"
              style={{ height: '600px' }}
            >
              <div className="chat-container">
                {/* 参与者列表 */}
                <div className="participants" style={{ marginBottom: 16 }}>
                  <Space wrap>
                    {session.participantIds.map(userId => (
                      <Tag key={userId} color="blue">
                        <UserOutlined /> {getUserName(userId)}
                      </Tag>
                    ))}
                  </Space>
                </div>

                {/* 聊天记录 */}
                <div className="chat-messages" style={{ height: '400px', overflowY: 'auto', marginBottom: 16 }}>
                  {session.chatHistory.length > 0 ? (
                    <List
                      dataSource={session.chatHistory}
                      renderItem={(message) => (
                        <List.Item>
                          <List.Item.Meta
                            avatar={<Avatar icon={<UserOutlined />} />}
                            title={getUserName(message.user)}
                            description={message.message}
                          />
                        </List.Item>
                      )}
                    />
                  ) : (
                    <Empty
                      description="暂无聊天记录"
                      image={Empty.PRESENTED_IMAGE_SIMPLE}
                    />
                  )}
                </div>

                {/* 消息输入 */}
                <div className="chat-input">
                  <Space.Compact style={{ width: '100%' }}>
                    <TextArea
                      placeholder="输入消息..."
                      value={chatMessage}
                      onChange={(e) => setChatMessage(e.target.value)}
                      onPressEnter={(e) => {
                        if (!e.shiftKey) {
                          e.preventDefault()
                          sendMessage()
                        }
                      }}
                      autoSize={{ minRows: 1, maxRows: 3 }}
                    />
                    <Button 
                      type="primary" 
                      icon={<SendOutlined />}
                      onClick={sendMessage}
                    >
                      发送
                    </Button>
                  </Space.Compact>
                </div>
              </div>
            </Card>
          </Col>
        </Row>
      )}

      {/* 创建会话模态框 */}
      <Modal
        title="创建协作购物会话"
        open={createSessionModalVisible}
        onOk={createSession}
        onCancel={() => setCreateSessionModalVisible(false)}
        confirmLoading={loading}
      >
        <Space direction="vertical" style={{ width: '100%' }}>
          <div>
            <label>选择商品:</label>
import React, { useState, useEffect, useRef } from 'react'
import { 
  Card, 
  Row, 
  Col, 
  Button, 
  Input, 
  Space, 
  Spin, 
  Empty,
  Tag,
  Typography,
  List,
  Avatar,
  Badge,
  message,
  Modal,
  Alert
} from 'antd'
import { 
  TeamOutlined, 
  PlusOutlined,
  SendOutlined,
  UserOutlined,
  MessageOutlined,
  EyeOutlined,
  ShoppingCartOutlined,
  WifiOutlined,
  DisconnectOutlined
} from '@ant-design/icons'
import { useSearchParams, useNavigate } from 'react-router-dom'
import { useStore } from '../store/useStore'
import { ApiService } from '../services/api'
import { CollaborationSession, Product } from '../store/useStore'
import webSocketService, { ChatMessage } from '../services/websocket'

const { Title, Paragraph } = Typography
const { TextArea } = Input

const Collaboration: React.FC = () => {
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()
  const { addToCart, addNotification, user } = useStore()
  const [loading, setLoading] = useState(false)
  const [session, setSession] = useState<CollaborationSession | null>(null)
  const [product, setProduct] = useState<Product | null>(null)
  const [chatMessage, setChatMessage] = useState('')
  const [isCreatingSession, setIsCreatingSession] = useState(false)
  const [createSessionModalVisible, setCreateSessionModalVisible] = useState(false)
  const [selectedProductId, setSelectedProductId] = useState<number | null>(null)
  const [isConnected, setIsConnected] = useState(false)
  const [chatMessages, setChatMessages] = useState<ChatMessage[]>([])
  const [participants, setParticipants] = useState<number[]>([])
  const chatEndRef = useRef<HTMLDivElement>(null)

  const productId = searchParams.get('productId')
  const currentUserId = user?.id || 1

  useEffect(() => {
    if (productId) {
      loadProduct(parseInt(productId))
    }
  }, [productId])

  useEffect(() => {
    // 设置WebSocket回调
    webSocketService.setOnChatMessage(handleChatMessage)
    webSocketService.setOnUserJoin(handleUserJoin)
    webSocketService.setOnUserLeave(handleUserLeave)
    webSocketService.setOnAnnotation(handleAnnotation)
    webSocketService.setOnProductChange(handleProductChange)
    webSocketService.setOnExperience(handleExperience)

    return () => {
      // 组件卸载时断开连接
      webSocketService.disconnect()
    }
  }, [])

  useEffect(() => {
    // 滚动到聊天底部
    if (chatEndRef.current) {
      chatEndRef.current.scrollIntoView({ behavior: 'smooth' })
    }
  }, [chatMessages])

  const loadProduct = async (id: number) => {
    try {
      setLoading(true)
      const response = await ApiService.getProductById(id)
      setProduct(response.data)
    } catch (error) {
      console.error('加载商品失败:', error)
      message.error('加载商品失败')
    } finally {
      setLoading(false)
    }
  }

  const createSession = async () => {
    if (!selectedProductId) {
      message.warning('请选择商品')
      return
    }

    try {
      setLoading(true)
      const response = await ApiService.createCollaborationSession(currentUserId, selectedProductId)
      
      const newSession: CollaborationSession = {
        id: response.sessionId,
        hostUserId: currentUserId,
        participantIds: [currentUserId],
        productId: selectedProductId,
        status: 'ACTIVE',
        chatHistory: [],
        annotations: [],
        createTime: new Date().toISOString(),
        updateTime: new Date().toISOString()
      }
      
      setSession(newSession)
      setParticipants([currentUserId])
      setCreateSessionModalVisible(false)
      
      // 连接到WebSocket
      await connectWebSocket(response.sessionId)
      
      addNotification({
        title: '会话创建成功',
        message: `协作购物会话已创建，邀请码: ${response.sessionId}`,
        type: 'success'
      })
    } catch (error) {
      console.error('创建会话失败:', error)
      message.error('创建会话失败')
    } finally {
      setLoading(false)
    }
  }

  const joinSession = async (sessionId: string) => {
    try {
      setLoading(true)
      const response = await ApiService.joinCollaborationSession(sessionId, currentUserId)
      
      if (response.code === 200) {
        // 加载会话信息
        const sessionResponse = await ApiService.getCollaborationSession(sessionId)
        setSession(sessionResponse.data)
        setParticipants(sessionResponse.data.participantIds)
        
        // 连接到WebSocket
        await connectWebSocket(sessionId)
        
        addNotification({
          title: '加入成功',
          message: '您已成功加入协作购物会话',
          type: 'success'
        })
      } else {
        message.error(response.message)
      }
    } catch (error) {
      console.error('加入会话失败:', error)
      message.error('加入会话失败')
    } finally {
      setLoading(false)
    }
  }

  const connectWebSocket = async (sessionId: string) => {
    try {
      await webSocketService.connect(sessionId, currentUserId, user?.username || '用户')
      setIsConnected(true)
      
      // 加载历史聊天记录
      if (session?.chatHistory) {
        const history = JSON.parse(session.chatHistory)
        setChatMessages(history)
      }
    } catch (error) {
      console.error('WebSocket连接失败:', error)
      message.error('实时通信连接失败')
    }
  }

  const sendMessage = () => {
    if (!chatMessage.trim()) return

    webSocketService.sendChatMessage(chatMessage)
    setChatMessage('')
  }

  const handleChatMessage = (message: ChatMessage) => {
    setChatMessages(prev => [...prev, message])
  }

  const handleUserJoin = (data: any) => {
    setParticipants(prev => {
      if (!prev.includes(data.userId)) {
        return [...prev, data.userId]
      }
      return prev
    })
    
    addNotification({
      title: '用户加入',
      message: `${data.username} 加入了会话`,
      type: 'info'
    })
  }

  const handleUserLeave = (data: any) => {
    setParticipants(prev => prev.filter(id => id !== data.userId))
    
    addNotification({
      title: '用户离开',
      message: `${data.username} 离开了会话`,
      type: 'info'
    })
  }

  const handleAnnotation = (data: any) => {
    // 处理标注消息
    console.log('收到标注:', data)
  }

  const handleProductChange = (data: any) => {
    // 处理商品切换
    console.log('商品切换:', data)
    if (data.productId) {
      loadProduct(data.productId)
    }
  }

  const handleExperience = (data: any) => {
    // 处理AR/VR体验
    console.log('AR/VR体验:', data)
  }

  const handleAddToCart = (product: Product) => {
    addToCart(product)
    addNotification({
      title: '添加成功',
      message: `${product.name} 已添加到购物车`,
      type: 'success'
    })
  }

  const users = [
    { id: 1, name: 'Alice Wang', avatar: null },
    { id: 2, name: 'Bob Li', avatar: null },
    { id: 3, name: 'Carol Zhang', avatar: null },
    { id: 4, name: 'David Chen', avatar: null },
    { id: 5, name: 'Eve Liu', avatar: null }
  ]

  const getUserName = (userId: number) => {
    return users.find(user => user.id === userId)?.name || `用户${userId}`
  }

  return (
    <div className="collaboration-page">
      <Card>
        <Title level={2}>
          <TeamOutlined /> 协作购物
        </Title>
        <Paragraph>
          与朋友一起购物，实时讨论商品，分享购物体验
        </Paragraph>
        
        {/* 连接状态指示器 */}
        <div style={{ marginBottom: 16 }}>
          {isConnected ? (
            <Alert
              message="实时通信已连接"
              type="success"
              icon={<WifiOutlined />}
              showIcon
            />
          ) : (
            <Alert
              message="实时通信未连接"
              type="warning"
              icon={<DisconnectOutlined />}
              showIcon
            />
          )}
        </div>
      </Card>

      {!session ? (
        <Card style={{ marginTop: 16 }}>
          <Row gutter={[24, 24]}>
            <Col xs={24} lg={12}>
              <Card title="创建协作会话" size="small">
                <Space direction="vertical" style={{ width: '100%' }}>
                  <Button 
                    type="primary" 
                    icon={<PlusOutlined />}
                    onClick={() => setCreateSessionModalVisible(true)}
                    block
                  >
                    创建新会话
                  </Button>
                  
                  <div>
                    <Title level={4}>加入现有会话</Title>
                    <Input
                      placeholder="输入会话ID"
                      onPressEnter={(e) => {
                        const sessionId = e.currentTarget.value
                        if (sessionId) {
                          joinSession(sessionId)
                        }
                      }}
                    />
                  </div>
                </Space>
              </Card>
            </Col>
            
            <Col xs={24} lg={12}>
              <Card title="功能说明" size="small">
                <ul>
                  <li>多人实时浏览商品</li>
                  <li>实时聊天讨论</li>
                  <li>商品标注和评论</li>
                  <li>协作决策购买</li>
                  <li>分享购物体验</li>
                </ul>
              </Card>
            </Col>
          </Row>
        </Card>
      ) : (
        <Row gutter={[16, 16]} style={{ marginTop: 16 }}>
          {/* 商品信息区域 */}
          <Col xs={24} lg={12}>
            <Card title="商品信息" size="small">
              {product ? (
                <div className="product-info">
                  <img
                    src={`https://picsum.photos/400/300?random=${product.id}`}
                    alt={product.name}
                    style={{ width: '100%', borderRadius: '8px', marginBottom: 16 }}
                  />
                  <Title level={4}>{product.name}</Title>
                  <Paragraph>{product.description}</Paragraph>
                  <div style={{ marginBottom: 16 }}>
                    <Space>
                      <span style={{ fontSize: '18px', fontWeight: 'bold', color: '#1890ff' }}>
                        ¥{product.price}
                      </span>
                      {product.isRecyclable && (
                        <Tag color="green">可回收</Tag>
                      )}
                      {product.isRentable && (
                        <Tag color="orange">可租赁</Tag>
                      )}
                    </Space>
                  </div>
                  <Button 
                    type="primary" 
                    icon={<ShoppingCartOutlined />}
                    onClick={() => handleAddToCart(product)}
                    block
                  >
                    加入购物车
                  </Button>
                </div>
              ) : (
                <Spin />
              )}
            </Card>
          </Col>

          {/* 聊天区域 */}
          <Col xs={24} lg={12}>
            <Card 
              title={
                <Space>
                  <TeamOutlined />
                  <span>协作会话</span>
                  <Badge count={participants.length} />
                </Space>
              }
              size="small"
              style={{ height: '600px' }}
            >
              <div className="chat-container">
                {/* 参与者列表 */}
                <div className="participants" style={{ marginBottom: 16 }}>
                  <Space wrap>
                    {participants.map(userId => (
                      <Tag key={userId} color="blue">
                        <UserOutlined /> {getUserName(userId)}
                      </Tag>
                    ))}
                  </Space>
                </div>

                {/* 聊天记录 */}
                <div className="chat-messages" style={{ height: '400px', overflowY: 'auto', marginBottom: 16 }}>
                  {chatMessages.length > 0 ? (
                    <List
                      dataSource={chatMessages}
                      renderItem={(message) => (
                        <List.Item>
                          <List.Item.Meta
                            avatar={<Avatar icon={<UserOutlined />} />}
                            title={message.username}
                            description={message.message}
                          />
                          <div style={{ fontSize: '12px', color: '#999' }}>
                            {new Date(message.timestamp).toLocaleTimeString()}
                          </div>
                        </List.Item>
                      )}
                    />
                  ) : (
                    <Empty
                      description="暂无聊天记录"
                      image={Empty.PRESENTED_IMAGE_SIMPLE}
                    />
                  )}
                  <div ref={chatEndRef} />
                </div>

                {/* 消息输入 */}
                <div className="chat-input">
                  <Space.Compact style={{ width: '100%' }}>
                    <TextArea
                      placeholder="输入消息..."
                      value={chatMessage}
                      onChange={(e) => setChatMessage(e.target.value)}
                      onPressEnter={(e) => {
                        if (!e.shiftKey) {
                          e.preventDefault()
                          sendMessage()
                        }
                      }}
                      autoSize={{ minRows: 1, maxRows: 3 }}
                    />
                    <Button 
                      type="primary" 
                      icon={<SendOutlined />}
                      onClick={sendMessage}
                      disabled={!isConnected}
                    >
                      发送
                    </Button>
                  </Space.Compact>
                </div>
              </div>
            </Card>
          </Col>
        </Row>
      )}

      {/* 创建会话模态框 */}
      <Modal
        title="创建协作购物会话"
        open={createSessionModalVisible}
        onOk={createSession}
        onCancel={() => setCreateSessionModalVisible(false)}
        confirmLoading={loading}
      >
        <Space direction="vertical" style={{ width: '100%' }}>
          <div>
            <label>选择商品:</label>
            <Select
              style={{ width: '100%', marginTop: 8 }}
              placeholder="选择要协作浏览的商品"
              value={selectedProductId}
              onChange={setSelectedProductId}
            >
              <Option value={1}>智能运动手环</Option>
              <Option value={2}>时尚连衣裙</Option>
              <Option value={3}>无线蓝牙耳机</Option>
              <Option value={4}>有机护肤套装</Option>
              <Option value={5}>智能空气净化器</Option>
              <Option value={6}>便携式咖啡机</Option>
              <Option value={7}>儿童益智玩具</Option>
              <Option value={8}>商务笔记本电脑</Option>
              <Option value={9}>瑜伽垫套装</Option>
              <Option value={10}>智能门锁</Option>
            </Select>
          </div>
        </Space>
      </Modal>
    </div>
  )
}

export default Collaboration
              <Option value={1}>智能运动手环</Option>
              <Option value={2}>时尚连衣裙</Option>
              <Option value={3}>无线蓝牙耳机</Option>
              <Option value={4}>有机护肤套装</Option>
              <Option value={5}>智能空气净化器</Option>
              <Option value={6}>便携式咖啡机</Option>
              <Option value={7}>儿童益智玩具</Option>
              <Option value={8}>商务笔记本电脑</Option>
              <Option value={9}>瑜伽垫套装</Option>
              <Option value={10}>智能门锁</Option>
            </Select>
          </div>
        </Space>
      </Modal>
    </div>
  )
}

export default Collaboration

