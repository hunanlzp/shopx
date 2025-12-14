import React, { useState, useEffect } from 'react'
import { Card, Form, Input, Rate, Button, Tag, message, Spin, Empty, List, Image, Select, Space, Modal } from 'antd'
import { StarOutlined, LikeOutlined, DislikeOutlined, UploadOutlined, CheckCircleOutlined } from '@ant-design/icons'
import ApiService from '../services/api'
import { ProductReview, ReviewStats, ReviewVote } from '../types'

const { TextArea } = Input
const { Option } = Select

// 评价表单组件（支持图片/视频）
export const ReviewForm: React.FC<{
  productId: number
  orderId?: number
  onSuccess?: () => void
}> = ({ productId, orderId, onSuccess }) => {
  const [form] = Form.useForm()
  const [submitting, setSubmitting] = useState(false)
  const [imageList, setImageList] = useState<string[]>([])
  const [videoList, setVideoList] = useState<string[]>([])

  const handleSubmit = async (values: any) => {
    try {
      setSubmitting(true)
      const response = await ApiService.createReview({
        productId,
        orderId,
        rating: values.rating,
        content: values.content,
        images: imageList,
        videos: videoList,
      })
      
      if (response.code === 200) {
        message.success('评价提交成功')
        form.resetFields()
        setImageList([])
        setVideoList([])
        onSuccess?.()
      }
    } catch (error: any) {
      message.error(error.message || '提交评价失败')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <Card title="写评价">
      <Form form={form} layout="vertical" onFinish={handleSubmit}>
        <Form.Item
          name="rating"
          label="评分"
          rules={[{ required: true, message: '请选择评分' }]}
        >
          <Rate />
        </Form.Item>
        <Form.Item
          name="content"
          label="评价内容"
          rules={[{ required: true, message: '请输入评价内容' }]}
        >
          <TextArea rows={6} placeholder="请分享您的使用体验..." />
        </Form.Item>
        <Form.Item label="上传图片（可选，最多5张）">
          <Upload
            listType="picture-card"
            fileList={imageList.map((url, index) => ({
              uid: index.toString(),
              url,
              name: `image-${index}`,
            }))}
            onChange={(info) => {
              // 这里应该上传到服务器并获取URL
              const newList = info.fileList.map(file => file.url || '').filter(Boolean)
              setImageList(newList)
            }}
            beforeUpload={() => false}
            maxCount={5}
          >
            {imageList.length < 5 && '+ 上传图片'}
          </Upload>
        </Form.Item>
        <Form.Item label="上传视频（可选，最多1个）">
          <Upload
            fileList={videoList.map((url, index) => ({
              uid: index.toString(),
              url,
              name: `video-${index}`,
            }))}
            onChange={(info) => {
              const newList = info.fileList.map(file => file.url || '').filter(Boolean)
              setVideoList(newList)
            }}
            beforeUpload={() => false}
            maxCount={1}
          >
            <Button icon={<UploadOutlined />}>上传视频</Button>
          </Upload>
        </Form.Item>
        <Form.Item>
          <Button type="primary" htmlType="submit" loading={submitting} block>
            提交评价
          </Button>
        </Form.Item>
      </Form>
    </Card>
  )
}

// 评价列表组件（支持筛选排序）
export const ReviewList: React.FC<{
  productId: number
  page?: number
  size?: number
}> = ({ productId, page = 1, size = 20 }) => {
  const [loading, setLoading] = useState(false)
  const [reviews, setReviews] = useState<ProductReview[]>([])
  const [total, setTotal] = useState(0)
  const [sortBy, setSortBy] = useState('createTime')
  const [order, setOrder] = useState<'asc' | 'desc'>('desc')

  useEffect(() => {
    loadReviews()
  }, [productId, page, size, sortBy, order])

  const loadReviews = async () => {
    try {
      setLoading(true)
      const response = await ApiService.getProductReviews(productId, page, size, sortBy, order)
      if (response.code === 200) {
        setReviews(response.data?.list || [])
        setTotal(response.data?.total || 0)
      }
    } catch (error: any) {
      message.error(error.message || '加载评价列表失败')
    } finally {
      setLoading(false)
    }
  }

  const handleVote = async (reviewId: number, helpful: boolean) => {
    try {
      await ApiService.voteReview(reviewId, helpful)
      message.success(helpful ? '已标记为有用' : '已标记为无用')
      await loadReviews()
    } catch (error: any) {
      message.error(error.message || '投票失败')
    }
  }

  return (
    <div>
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h3>商品评价 ({total})</h3>
        <Space>
          <Select
            value={sortBy}
            onChange={setSortBy}
            style={{ width: 120 }}
          >
            <Option value="createTime">时间</Option>
            <Option value="rating">评分</Option>
            <Option value="helpfulCount">有用数</Option>
          </Select>
          <Select
            value={order}
            onChange={setOrder}
            style={{ width: 100 }}
          >
            <Option value="desc">降序</Option>
            <Option value="asc">升序</Option>
          </Select>
        </Space>
      </div>

      {loading ? (
        <Spin />
      ) : reviews.length === 0 ? (
        <Empty description="暂无评价" />
      ) : (
        <List
          dataSource={reviews}
          renderItem={(review) => (
            <List.Item>
              <Card style={{ width: '100%' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                  <div style={{ flex: 1 }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 8 }}>
                      <div style={{ fontWeight: 'bold' }}>{review.username || '匿名用户'}</div>
                      {review.isVerifiedPurchase && (
                        <Tag color="blue" icon={<CheckCircleOutlined />}>已购买</Tag>
                      )}
                      <Rate disabled value={review.rating} style={{ fontSize: 14 }} />
                    </div>
                    <div style={{ marginTop: 8 }}>{review.content}</div>
                    {review.images && review.images.length > 0 && (
                      <div style={{ marginTop: 8 }}>
                        <Image.PreviewGroup>
                          {review.images.map((url, index) => (
                            <Image
                              key={index}
                              src={url}
                              width={80}
                              height={80}
                              style={{ marginRight: 8, marginBottom: 8 }}
                            />
                          ))}
                        </Image.PreviewGroup>
                      </div>
                    )}
                    {review.videos && review.videos.length > 0 && (
                      <div style={{ marginTop: 8 }}>
                        {review.videos.map((url, index) => (
                          <video
                            key={index}
                            src={url}
                            controls
                            style={{ maxWidth: 200, maxHeight: 200 }}
                          />
                        ))}
                      </div>
                    )}
                    {review.merchantReply && (
                      <div style={{ marginTop: 12, padding: 12, background: '#f5f5f5', borderRadius: 4 }}>
                        <div style={{ fontWeight: 'bold', marginBottom: 4 }}>商家回复:</div>
                        <div>{review.merchantReply}</div>
                        {review.merchantReplyTime && (
                          <div style={{ marginTop: 4, color: '#999', fontSize: 12 }}>
                            {new Date(review.merchantReplyTime).toLocaleString()}
                          </div>
                        )}
                      </div>
                    )}
                    <div style={{ marginTop: 12, color: '#999', fontSize: 12 }}>
                      {new Date(review.createTime).toLocaleString()}
                    </div>
                  </div>
                  <div>
                    <Space>
                      <Button
                        type="text"
                        icon={<LikeOutlined />}
                        onClick={() => handleVote(review.id, true)}
                      >
                        有用 ({review.helpfulCount})
                      </Button>
                    </Space>
                  </div>
                </div>
              </Card>
            </List.Item>
          )}
        />
      )}
    </div>
  )
}

// 评价统计组件
export const ReviewStats: React.FC<{
  productId: number
}> = ({ productId }) => {
  const [loading, setLoading] = useState(false)
  const [stats, setStats] = useState<ReviewStats | null>(null)

  useEffect(() => {
    loadStats()
  }, [productId])

  const loadStats = async () => {
    try {
      setLoading(true)
      const response = await ApiService.getReviewStats(productId)
      if (response.code === 200) {
        setStats(response.data)
      }
    } catch (error: any) {
      message.error(error.message || '加载评价统计失败')
    } finally {
      setLoading(false)
    }
  }

  if (loading) {
    return <Spin />
  }

  if (!stats) {
    return null
  }

  return (
    <Card title="评价统计">
      <div style={{ textAlign: 'center', marginBottom: 16 }}>
        <div style={{ fontSize: 48, fontWeight: 'bold', color: '#1890ff' }}>
          {stats.averageRating.toFixed(1)}
        </div>
        <Rate disabled value={stats.averageRating} style={{ fontSize: 24 }} />
        <div style={{ marginTop: 8, color: '#999' }}>
          共 {stats.totalReviews} 条评价
        </div>
      </div>
      <div>
        {[5, 4, 3, 2, 1].map(rating => {
          const count = stats.ratingDistribution[rating.toString()] || 0
          const percentage = stats.totalReviews > 0 ? (count / stats.totalReviews) * 100 : 0
          return (
            <div key={rating} style={{ display: 'flex', alignItems: 'center', marginBottom: 8 }}>
              <span style={{ width: 60 }}>{rating}星</span>
              <div style={{ flex: 1, margin: '0 16px', background: '#f0f0f0', height: 8, borderRadius: 4 }}>
                <div
                  style={{
                    width: `${percentage}%`,
                    background: '#1890ff',
                    height: '100%',
                    borderRadius: 4,
                  }}
                />
              </div>
              <span style={{ width: 60, textAlign: 'right' }}>{count}</span>
            </div>
          )
        })}
      </div>
      <div style={{ marginTop: 16, paddingTop: 16, borderTop: '1px solid #f0f0f0' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between' }}>
          <div>
            <div style={{ color: '#999' }}>已购买评价</div>
            <div style={{ fontSize: 18, fontWeight: 'bold' }}>{stats.verifiedPurchaseCount}</div>
          </div>
          <div>
            <div style={{ color: '#999' }}>带图评价</div>
            <div style={{ fontSize: 18, fontWeight: 'bold' }}>{stats.withImagesCount}</div>
          </div>
          <div>
            <div style={{ color: '#999' }}>带视频评价</div>
            <div style={{ fontSize: 18, fontWeight: 'bold' }}>{stats.withVideosCount}</div>
          </div>
        </div>
      </div>
    </Card>
  )
}

// 评价投票组件
export const ReviewVote: React.FC<{
  reviewId: number
  helpfulCount: number
  onVote?: (helpful: boolean) => void
}> = ({ reviewId, helpfulCount, onVote }) => {
  const handleVote = async (helpful: boolean) => {
    try {
      await ApiService.voteReview(reviewId, helpful)
      message.success(helpful ? '已标记为有用' : '已标记为无用')
      onVote?.(helpful)
    } catch (error: any) {
      message.error(error.message || '投票失败')
    }
  }

  return (
    <Space>
      <Button
        type="text"
        icon={<LikeOutlined />}
        onClick={() => handleVote(true)}
      >
        有用 ({helpfulCount})
      </Button>
      <Button
        type="text"
        icon={<DislikeOutlined />}
        onClick={() => handleVote(false)}
      >
        无用
      </Button>
    </Space>
  )
}

