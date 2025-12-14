import React, { useState, useEffect, useCallback } from 'react'
import { 
  Card, 
  Button, 
  Space, 
  Typography, 
  List, 
  Avatar, 
  Input, 
  Tag, 
  Empty,
  Row,
  Col,
  Statistic,
  Modal,
  Form,
  message,
  Tabs
} from 'antd'
import {
  UserOutlined,
  LikeOutlined,
  CommentOutlined,
  ShareAltOutlined,
  EyeOutlined,
  PlusOutlined,
  TrophyOutlined,
  FireOutlined,
  StarOutlined
} from '@ant-design/icons'
import { ApiService } from '../services/api'
import { formatTime } from '../utils/utils'

const { Title, Paragraph, Text } = Typography
const { TextArea } = Input
const { TabPane } = Tabs

interface CommunityPost {
  id: number
  userId: number
  username?: string
  avatar?: string
  title: string
  content: string
  postType: string
  category?: string
  imageUrls?: string[]
  tags?: string
  likeCount: number
  commentCount: number
  shareCount: number
  viewCount: number
  createTime: string
}

interface CommunityComment {
  id: number
  postId: number
  userId: number
  username?: string
  avatar?: string
  content: string
  parentId?: number
  likeCount: number
  replyCount: number
  createTime: string
}

interface CommunityTabProps {
  userId: number
}

const CommunityTab: React.FC<CommunityTabProps> = ({ userId }) => {
  const [posts, setPosts] = useState<CommunityPost[]>([])
  const [loading, setLoading] = useState(false)
  const [activeTab, setActiveTab] = useState('posts')
  const [showPostModal, setShowPostModal] = useState(false)
  const [selectedPost, setSelectedPost] = useState<CommunityPost | null>(null)
  const [comments, setComments] = useState<CommunityComment[]>([])
  const [ranking, setRanking] = useState<any[]>([])
  const [form] = Form.useForm()
  const [commentForm] = Form.useForm()

  // 加载帖子列表
  const loadPosts = useCallback(async (type?: string) => {
    setLoading(true)
    try {
      const response = await ApiService.getCommunityPosts(type, undefined, 1, 20)
      setPosts(response.data || [])
    } catch (error) {
      console.error('加载帖子失败:', error)
    } finally {
      setLoading(false)
    }
  }, [])

  // 加载排行榜
  const loadRanking = useCallback(async () => {
    try {
      const response = await ApiService.getUserRanking('sustainability', 10)
      setRanking(response.data || [])
    } catch (error) {
      console.error('加载排行榜失败:', error)
    }
  }, [])

  useEffect(() => {
    loadPosts()
    loadRanking()
  }, [loadPosts, loadRanking])

  // 发布帖子
  const handlePostSubmit = useCallback(async (values: any) => {
    try {
      await ApiService.createCommunityPost({
        title: values.title,
        content: values.content,
        postType: 'RECYCLE',
        category: '环保',
        tags: values.tags
      })
      message.success('帖子发布成功')
      setShowPostModal(false)
      form.resetFields()
      loadPosts()
    } catch (error) {
      console.error('发布帖子失败:', error)
      message.error('发布失败')
    }
  }, [form, loadPosts])

  // 查看帖子详情
  const handleViewPost = useCallback(async (post: CommunityPost) => {
    setSelectedPost(post)
    try {
      const response = await ApiService.getPostComments(post.id, 1, 20)
      setComments(response.data || [])
    } catch (error) {
      console.error('加载评论失败:', error)
    }
  }, [])

  // 添加评论
  const handleCommentSubmit = useCallback(async (values: any) => {
    if (!selectedPost) return
    
    try {
      await ApiService.addCommunityComment({
        postId: selectedPost.id,
        content: values.content
      })
      message.success('评论成功')
      commentForm.resetFields()
      handleViewPost(selectedPost)
    } catch (error) {
      console.error('评论失败:', error)
      message.error('评论失败')
    }
  }, [selectedPost, commentForm, handleViewPost])

  // 点赞帖子
  const handleLikePost = useCallback(async (postId: number) => {
    try {
      await ApiService.likeCommunityPost(postId)
      loadPosts()
    } catch (error) {
      console.error('点赞失败:', error)
    }
  }, [loadPosts])

  return (
    <div>
      <Tabs activeKey={activeTab} onChange={setActiveTab}>
        <TabPane tab="帖子" key="posts">
          <div style={{ marginBottom: '16px' }}>
            <Button 
              type="primary" 
              icon={<PlusOutlined />}
              onClick={() => setShowPostModal(true)}
            >
              发布帖子
            </Button>
          </div>

          {loading ? (
            <div style={{ textAlign: 'center', padding: '40px 0' }}>
              <Spin size="large" />
            </div>
          ) : posts.length === 0 ? (
            <Empty description="暂无帖子" />
          ) : (
            <List
              dataSource={posts}
              renderItem={(post) => (
                <List.Item>
                  <Card
                    style={{ width: '100%' }}
                    actions={[
                      <Button 
                        type="text" 
                        icon={<LikeOutlined />}
                        onClick={() => handleLikePost(post.id)}
                      >
                        {post.likeCount || 0}
                      </Button>,
                      <Button 
                        type="text" 
                        icon={<CommentOutlined />}
                        onClick={() => handleViewPost(post)}
                      >
                        {post.commentCount || 0}
                      </Button>,
                      <Button type="text" icon={<ShareAltOutlined />}>
                        {post.shareCount || 0}
                      </Button>
                    ]}
                  >
                    <Card.Meta
                      avatar={<Avatar icon={<UserOutlined />} />}
                      title={post.title}
                      description={
                        <div>
                          <Paragraph ellipsis={{ rows: 3 }}>
                            {post.content}
                          </Paragraph>
                          <Space>
                            {post.category && <Tag>{post.category}</Tag>}
                            <Text type="secondary">
                              <EyeOutlined /> {post.viewCount || 0}
                            </Text>
                            <Text type="secondary">
                              {formatTime.fromNow(post.createTime)}
                            </Text>
                          </Space>
                        </div>
                      }
                    />
                  </Card>
                </List.Item>
              )}
            />
          )}
        </TabPane>

        <TabPane tab="排行榜" key="ranking">
          <Row gutter={16} style={{ marginBottom: '24px' }}>
            <Col span={8}>
              <Card>
                <Statistic
                  title="环保贡献"
                  value={ranking.length}
                  prefix={<TrophyOutlined />}
                />
              </Card>
            </Col>
            <Col span={8}>
              <Card>
                <Statistic
                  title="活跃用户"
                  value={ranking.length}
                  prefix={<FireOutlined />}
                />
              </Card>
            </Col>
            <Col span={8}>
              <Card>
                <Statistic
                  title="总帖子数"
                  value={posts.length}
                  prefix={<StarOutlined />}
                />
              </Card>
            </Col>
          </Row>

          {ranking.length === 0 ? (
            <Empty description="暂无排行榜数据" />
          ) : (
            <List
              dataSource={ranking}
              renderItem={(item, index) => (
                <List.Item>
                  <Space>
                    <Avatar 
                      style={{ 
                        backgroundColor: index < 3 ? '#faad14' : '#1890ff' 
                      }}
                    >
                      {index + 1}
                    </Avatar>
                    <Text strong>用户 {item.userId}</Text>
                    <Text type="secondary">贡献值: {item.score || 0}</Text>
                  </Space>
                </List.Item>
              )}
            />
          )}
        </TabPane>
      </Tabs>

      {/* 发布帖子模态框 */}
      <Modal
        title="发布帖子"
        open={showPostModal}
        onCancel={() => {
          setShowPostModal(false)
          form.resetFields()
        }}
        onOk={() => form.submit()}
        width={600}
      >
        <Form form={form} onFinish={handlePostSubmit} layout="vertical">
          <Form.Item
            name="title"
            label="标题"
            rules={[{ required: true, message: '请输入标题' }]}
          >
            <Input placeholder="请输入帖子标题" />
          </Form.Item>
          <Form.Item
            name="content"
            label="内容"
            rules={[{ required: true, message: '请输入内容' }]}
          >
            <TextArea rows={6} placeholder="请输入帖子内容" />
          </Form.Item>
          <Form.Item name="tags" label="标签">
            <Input placeholder="请输入标签，用逗号分隔" />
          </Form.Item>
        </Form>
      </Modal>

      {/* 帖子详情模态框 */}
      <Modal
        title={selectedPost?.title}
        open={!!selectedPost}
        onCancel={() => {
          setSelectedPost(null)
          setComments([])
        }}
        footer={null}
        width={800}
      >
        {selectedPost && (
          <div>
            <Paragraph>{selectedPost.content}</Paragraph>
            
            <Divider>评论 ({comments.length})</Divider>
            
            <List
              dataSource={comments}
              renderItem={(comment) => (
                <List.Item>
                  <List.Item.Meta
                    avatar={<Avatar icon={<UserOutlined />} />}
                    title={comment.username || `用户${comment.userId}`}
                    description={
                      <div>
                        <Paragraph>{comment.content}</Paragraph>
                        <Space>
                          <Text type="secondary">
                            {formatTime.fromNow(comment.createTime)}
                          </Text>
                          <Button type="text" size="small" icon={<LikeOutlined />}>
                            {comment.likeCount || 0}
                          </Button>
                        </Space>
                      </div>
                    }
                  />
                </List.Item>
              )}
            />

            <Divider />
            
            <Form form={commentForm} onFinish={handleCommentSubmit}>
              <Form.Item
                name="content"
                rules={[{ required: true, message: '请输入评论内容' }]}
              >
                <TextArea rows={3} placeholder="写下你的评论..." />
              </Form.Item>
              <Form.Item>
                <Button type="primary" htmlType="submit">
                  发表评论
                </Button>
              </Form.Item>
            </Form>
          </div>
        )}
      </Modal>
    </div>
  )
}

export default CommunityTab

