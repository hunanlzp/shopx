import React, { useRef, useEffect, useState, useCallback, useMemo } from 'react'
import { Canvas, useFrame, useLoader, useThree } from '@react-three/fiber'
import { 
  OrbitControls, 
  Environment, 
  Html, 
  Text, 
  Box,
  Sphere,
  Cylinder,
  Plane,
  Octahedron,
  Torus,
  Cone,
  Icosahedron,
  useTexture,
  useGLTF,
  ContactShadows,
  Sky,
  Stars,
  Cloud,
  Float,
  PresentationControls,
  Stage,
  Center,
  AccumulativeShadows,
  RandomizedLight,
  Decal,
  useDecal
} from '@react-three/drei'
import { Card, Button, Space, Typography, Spin, Alert, Slider, Switch, Tooltip, Badge, message } from 'antd'
import { 
  ExperimentOutlined, 
  RotateLeftOutlined, 
  ZoomInOutlined, 
  ZoomOutOutlined,
  FullscreenOutlined,
  CloseOutlined,
  PlayCircleOutlined,
  PauseCircleOutlined,
  SoundOutlined,
  SoundFilledOutlined,
  SettingOutlined,
  InfoCircleOutlined,
  ShareAltOutlined,
  DownloadOutlined,
  CameraOutlined,
  EyeOutlined,
  HeartOutlined,
  ShoppingCartOutlined
} from '@ant-design/icons'
import { motion, AnimatePresence } from 'framer-motion'
import * as THREE from 'three'
import { useStore } from '../store/useStore'
import { ApiService } from '../services/api'
import { formatNumber } from '../utils/utils'

const { Title, Paragraph, Text: AntText } = Typography

interface ARVRExperienceProps {
  productId: number
  productName: string
  productPrice: number
  productDescription: string
  experienceType: 'AR' | 'VR'
  onClose: () => void
  onAddToCart?: (productId: number) => void
  onLike?: (productId: number) => void
  onShare?: (productId: number) => void
}

// 增强的3D商品模型组件
const EnhancedProductModel: React.FC<{ 
  productId: number
  productName: string
  productPrice: number
  experienceType: string
  autoRotate: boolean
  animationSpeed: number
  onInteraction?: (type: string) => void
  modelUrl?: string
}> = ({ 
  productId, 
  productName, 
  productPrice,
  experienceType,
  autoRotate,
  animationSpeed,
  onInteraction,
  modelUrl
}) => {
  const meshRef = useRef<THREE.Mesh>(null)
  const groupRef = useRef<THREE.Group>(null)
  const [loading, setLoading] = useState(true)
  const [hovered, setHovered] = useState(false)
  const [clicked, setClicked] = useState(false)
  const [modelError, setModelError] = useState(false)

  // 尝试加载真实3D模型
  let gltf: any = null
  try {
    if (modelUrl) {
      gltf = useGLTF(modelUrl)
    }
  } catch (error) {
    console.warn('无法加载GLTF模型，使用默认模型:', error)
    setModelError(true)
  }

  // 加载状态管理
  useEffect(() => {
    if (gltf) {
      setLoading(false)
    } else {
      const timer = setTimeout(() => setLoading(false), 1500)
      return () => clearTimeout(timer)
    }
  }, [productId, gltf])

  // 动画帧更新
  useFrame((state) => {
    if (meshRef.current && autoRotate) {
      meshRef.current.rotation.y += animationSpeed * 0.01
    }
    
    if (groupRef.current) {
      // 悬停效果
      groupRef.current.scale.setScalar(hovered ? 1.1 : 1)
      
      // 点击效果
      if (clicked) {
        groupRef.current.rotation.x = Math.sin(state.clock.elapsedTime * 10) * 0.1
      }
    }
  })

  // 获取产品特定的3D模型
  const getProductModel = () => {
    const models = {
      1: { // 智能运动手环
        geometry: <boxGeometry args={[2, 0.3, 1]} />,
        material: <meshStandardMaterial color="#1890ff" metalness={0.8} roughness={0.2} />,
        position: [0, 0, 0],
        scale: [1, 1, 1]
      },
      2: { // 时尚连衣裙
        geometry: <cylinderGeometry args={[1, 1.2, 3, 8]} />,
        material: <meshStandardMaterial color="#eb2f96" metalness={0.1} roughness={0.8} />,
        position: [0, 0, 0],
        scale: [1, 1, 1]
      },
      3: { // 无线蓝牙耳机
        geometry: <sphereGeometry args={[0.8, 16, 16]} />,
        material: <meshStandardMaterial color="#52c41a" metalness={0.3} roughness={0.4} />,
        position: [0, 0, 0],
        scale: [1, 1, 1]
      },
      4: { // 有机护肤套装
        geometry: <boxGeometry args={[1.5, 2, 1]} />,
        material: <meshStandardMaterial color="#fa8c16" metalness={0.2} roughness={0.6} />,
        position: [0, 0, 0],
        scale: [1, 1, 1]
      },
      5: { // 智能空气净化器
        geometry: <cylinderGeometry args={[1.5, 1.5, 2, 16]} />,
        material: <meshStandardMaterial color="#722ed1" metalness={0.4} roughness={0.3} />,
        position: [0, 0, 0],
        scale: [1, 1, 1]
      },
      6: { // 便携式咖啡机
        geometry: <boxGeometry args={[2, 1.5, 1]} />,
        material: <meshStandardMaterial color="#13c2c2" metalness={0.6} roughness={0.2} />,
        position: [0, 0, 0],
        scale: [1, 1, 1]
      },
      7: { // 儿童益智玩具
        geometry: <octahedronGeometry args={[1]} />,
        material: <meshStandardMaterial color="#f5222d" metalness={0.1} roughness={0.7} />,
        position: [0, 0, 0],
        scale: [1, 1, 1]
      },
      8: { // 商务笔记本电脑
        geometry: <boxGeometry args={[3, 0.2, 2]} />,
        material: <meshStandardMaterial color="#595959" metalness={0.7} roughness={0.1} />,
        position: [0, 0, 0],
        scale: [1, 1, 1]
      },
      9: { // 瑜伽垫套装
        geometry: <planeGeometry args={[3, 1]} />,
        material: <meshStandardMaterial color="#52c41a" metalness={0.1} roughness={0.9} />,
        position: [0, 0, 0],
        scale: [1, 1, 1]
      },
      10: { // 智能门锁
        geometry: <boxGeometry args={[1, 2, 0.5]} />,
        material: <meshStandardMaterial color="#1890ff" metalness={0.5} roughness={0.3} />,
        position: [0, 0, 0],
        scale: [1, 1, 1]
      }
    }

    return models[productId as keyof typeof models] || models[1]
  }

  const model = getProductModel()

  const handleClick = () => {
    setClicked(!clicked)
    onInteraction?.('click')
  }

  const handlePointerOver = () => {
    setHovered(true)
    onInteraction?.('hover')
  }

  const handlePointerOut = () => {
    setHovered(false)
  }

  if (loading) {
    return (
      <Html center>
        <div style={{ textAlign: 'center' }}>
          <Spin size="large" />
          <div style={{ marginTop: 16, color: 'white' }}>
            正在加载3D模型...
          </div>
        </div>
      </Html>
    )
  }

  return (
    <Float speed={2} rotationIntensity={1} floatIntensity={2}>
      <group ref={groupRef}>
        <mesh 
          ref={meshRef}
          position={model.position}
          scale={model.scale}
          onClick={handleClick}
          onPointerOver={handlePointerOver}
          onPointerOut={handlePointerOut}
          castShadow
          receiveShadow
        >
          {model.geometry}
          {model.material}
        </mesh>
        
        {/* 产品标签 */}
        <Text
          position={[0, 2, 0]}
          fontSize={0.3}
          color="#ffffff"
          anchorX="center"
          anchorY="middle"
          maxWidth={4}
        >
          {productName}
        </Text>
        
        {/* 价格标签 */}
        <Text
          position={[0, 1.5, 0]}
          fontSize={0.2}
          color="#52c41a"
          anchorX="center"
          anchorY="middle"
        >
          {formatNumber.price(productPrice)}
        </Text>
      </group>
    </Float>
  )
}

// 环境设置组件
const EnvironmentSettings: React.FC<{
  environmentType: string
  onEnvironmentChange: (type: string) => void
}> = ({ environmentType, onEnvironmentChange }) => {
  const environments = [
    { key: 'sunset', name: '日落', icon: '🌅' },
    { key: 'dawn', name: '黎明', icon: '🌄' },
    { key: 'night', name: '夜晚', icon: '🌙' },
    { key: 'warehouse', name: '仓库', icon: '🏭' },
    { key: 'forest', name: '森林', icon: '🌲' },
    { key: 'apartment', name: '公寓', icon: '🏠' },
    { key: 'studio', name: '工作室', icon: '🎨' },
    { key: 'city', name: '城市', icon: '🏙️' },
  ]

  return (
    <div style={{ 
      background: 'rgba(255, 255, 255, 0.9)', 
      padding: '12px', 
      borderRadius: '8px',
      backdropFilter: 'blur(10px)',
      marginBottom: '16px'
    }}>
      <Title level={5} style={{ margin: '0 0 8px 0' }}>环境设置</Title>
      <div style={{ display: 'flex', flexWrap: 'wrap', gap: '8px' }}>
        {environments.map((env) => (
          <Button
            key={env.key}
            size="small"
            type={environmentType === env.key ? 'primary' : 'default'}
            onClick={() => onEnvironmentChange(env.key)}
            style={{ minWidth: '60px' }}
          >
            <span style={{ marginRight: '4px' }}>{env.icon}</span>
            {env.name}
          </Button>
        ))}
      </div>
    </div>
  )
}

// 增强的AR/VR体验主组件
const EnhancedARVRExperience: React.FC<ARVRExperienceProps> = ({
  productId,
  productName,
  productPrice,
  productDescription,
  experienceType,
  onClose,
  onAddToCart,
  onLike,
  onShare
}) => {
  const { user } = useStore()
  const [isFullscreen, setIsFullscreen] = useState(false)
  const [cameraPosition, setCameraPosition] = useState<[number, number, number]>([5, 5, 5])
  const [isLoading, setIsLoading] = useState(true)
  const [autoRotate, setAutoRotate] = useState(true)
  const [animationSpeed, setAnimationSpeed] = useState(1)
  const [environmentType, setEnvironmentType] = useState('sunset')
  const [showSettings, setShowSettings] = useState(false)
  const [soundEnabled, setSoundEnabled] = useState(false)
  const [interactionCount, setInteractionCount] = useState(0)
  const [isRecording, setIsRecording] = useState(false)
  const mediaRecorderRef = useRef<MediaRecorder | null>(null)
  const recordedChunksRef = useRef<Blob[]>([])

  // 加载3D模型信息
  useEffect(() => {
    const loadModelInfo = async () => {
      try {
        const response = await ApiService.getModelInfo(productId)
        if (response.data?.modelConfig?.modelUrl) {
          setModelUrl(response.data.modelConfig.modelUrl)
        } else if (response.data?.arModelUrl) {
          setModelUrl(response.data.arModelUrl)
        } else if (response.data?.vrExperienceUrl) {
          setModelUrl(response.data.vrExperienceUrl)
        }
      } catch (error) {
        console.warn('无法加载模型信息，使用默认模型:', error)
      } finally {
        const timer = setTimeout(() => setIsLoading(false), 2000)
        return () => clearTimeout(timer)
      }
    }
    
    loadModelInfo()
  }, [productId])

  const handleFullscreen = useCallback(() => {
    setIsFullscreen(!isFullscreen)
  }, [isFullscreen])

  const handleResetView = useCallback(() => {
    setCameraPosition([5, 5, 5])
  }, [])

  const handleZoomIn = useCallback(() => {
    setCameraPosition(prev => [prev[0] * 0.8, prev[1] * 0.8, prev[2] * 0.8])
  }, [])

  const handleZoomOut = useCallback(() => {
    setCameraPosition(prev => [prev[0] * 1.2, prev[1] * 1.2, prev[2] * 1.2])
  }, [])

  const handleInteraction = useCallback((type: string) => {
    setInteractionCount(prev => prev + 1)
    
    // 记录用户交互行为
    if (user?.id) {
      ApiService.recordUserBehavior(user.id, productId, `3d_${type}`)
    }
  }, [user?.id, productId])

  const handleAddToCartClick = useCallback(() => {
    onAddToCart?.(productId)
    handleInteraction('add_to_cart')
  }, [onAddToCart, productId, handleInteraction])

  const handleLikeClick = useCallback(() => {
    onLike?.(productId)
    handleInteraction('like')
  }, [onLike, productId, handleInteraction])

  const handleShareClick = useCallback(() => {
    onShare?.(productId)
    handleInteraction('share')
  }, [onShare, productId, handleInteraction])

  const canvasRef = useRef<HTMLCanvasElement>(null)
  const rendererRef = useRef<any>(null)

  const handleScreenshot = useCallback(() => {
    try {
      // 获取Canvas元素
      const canvas = document.querySelector('canvas')
      if (!canvas) {
        message.error('无法获取Canvas元素')
        return
      }

      // 将Canvas转换为图片
      canvas.toBlob((blob) => {
        if (!blob) {
          message.error('截图失败')
          return
        }

        // 创建下载链接
        const url = URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = `shopx-${productName}-${Date.now()}.png`
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        URL.revokeObjectURL(url)

        message.success('截图已保存')
        handleInteraction('screenshot')
      }, 'image/png', 1.0)
    } catch (error) {
      console.error('截图失败:', error)
      message.error('截图失败，请稍后重试')
    }
  }, [productName, handleInteraction])

  const handleRecord = useCallback(async () => {
    try {
      if (!isRecording) {
        // 开始录制
        const canvas = document.querySelector('canvas')
        if (!canvas) {
          message.error('无法获取Canvas元素')
          return
        }

        // 获取Canvas流
        const stream = canvas.captureStream(30) // 30 FPS
        
        // 创建MediaRecorder
        const options: MediaRecorderOptions = {
          mimeType: 'video/webm;codecs=vp9',
          videoBitsPerSecond: 2500000
        }
        
        // 如果浏览器不支持vp9，尝试vp8
        if (!MediaRecorder.isTypeSupported(options.mimeType!)) {
          options.mimeType = 'video/webm;codecs=vp8'
        }
        
        // 如果还不支持，使用默认
        if (!MediaRecorder.isTypeSupported(options.mimeType!)) {
          delete options.mimeType
        }

        const mediaRecorder = new MediaRecorder(stream, options)
        mediaRecorderRef.current = mediaRecorder
        recordedChunksRef.current = []

        mediaRecorder.ondataavailable = (event) => {
          if (event.data.size > 0) {
            recordedChunksRef.current.push(event.data)
          }
        }

        mediaRecorder.onstop = () => {
          const blob = new Blob(recordedChunksRef.current, { type: 'video/webm' })
          const url = URL.createObjectURL(blob)
          const link = document.createElement('a')
          link.href = url
          link.download = `shopx-${productName}-${Date.now()}.webm`
          document.body.appendChild(link)
          link.click()
          document.body.removeChild(link)
          URL.revokeObjectURL(url)
          message.success('录制已保存')
        }

        mediaRecorder.start()
        setIsRecording(true)
        message.info('开始录制...')
        handleInteraction('record_start')
      } else {
        // 停止录制
        if (mediaRecorderRef.current && mediaRecorderRef.current.state !== 'inactive') {
          mediaRecorderRef.current.stop()
          setIsRecording(false)
          message.info('正在保存录制...')
          handleInteraction('record_stop')
        }
      }
    } catch (error) {
      console.error('录制失败:', error)
      message.error('录制失败，请稍后重试')
      setIsRecording(false)
    }
  }, [isRecording, productName, handleInteraction])

  if (isLoading) {
    return (
      <Card style={{ height: '600px', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
        <Space direction="vertical" align="center">
          <Spin size="large" />
          <Title level={4}>正在加载{experienceType}体验...</Title>
          <Paragraph type="secondary">
            {experienceType === 'AR' ? '准备AR相机和模型' : '初始化VR环境'}
          </Paragraph>
        </Space>
      </Card>
    )
  }

  return (
    <motion.div
      initial={{ opacity: 0, scale: 0.9 }}
      animate={{ opacity: 1, scale: 1 }}
      exit={{ opacity: 0, scale: 0.9 }}
      transition={{ duration: 0.3 }}
    >
      <Card 
        style={{ 
          height: isFullscreen ? '100vh' : '600px',
          position: isFullscreen ? 'fixed' : 'relative',
          top: isFullscreen ? 0 : 'auto',
          left: isFullscreen ? 0 : 'auto',
          zIndex: isFullscreen ? 1000 : 'auto',
          width: isFullscreen ? '100vw' : '100%'
        }}
      >
        <div style={{ position: 'relative', height: '100%' }}>
          {/* 主控制面板 */}
          <div style={{
            position: 'absolute',
            top: 16,
            left: 16,
            zIndex: 10,
            background: 'rgba(255, 255, 255, 0.9)',
            padding: '8px 12px',
            borderRadius: '8px',
            backdropFilter: 'blur(10px)'
          }}>
            <Space wrap>
              <Tooltip title="重置视角">
                <Button 
                  icon={<RotateLeftOutlined />} 
                  onClick={handleResetView}
                  size="small"
                />
              </Tooltip>
              <Tooltip title="放大">
                <Button 
                  icon={<ZoomInOutlined />} 
                  onClick={handleZoomIn}
                  size="small"
                />
              </Tooltip>
              <Tooltip title="缩小">
                <Button 
                  icon={<ZoomOutOutlined />} 
                  onClick={handleZoomOut}
                  size="small"
                />
              </Tooltip>
              <Tooltip title={autoRotate ? '停止旋转' : '开始旋转'}>
                <Button 
                  icon={autoRotate ? <PauseCircleOutlined /> : <PlayCircleOutlined />}
                  onClick={() => setAutoRotate(!autoRotate)}
                  size="small"
                  type={autoRotate ? 'primary' : 'default'}
                />
              </Tooltip>
              <Tooltip title={isFullscreen ? '退出全屏' : '全屏'}>
                <Button 
                  icon={<FullscreenOutlined />} 
                  onClick={handleFullscreen}
                  size="small"
                />
              </Tooltip>
              <Tooltip title="设置">
                <Button 
                  icon={<SettingOutlined />} 
                  onClick={() => setShowSettings(!showSettings)}
                  size="small"
                />
              </Tooltip>
              <Tooltip title="关闭">
                <Button 
                  icon={<CloseOutlined />} 
                  onClick={onClose}
                  size="small"
                  danger
                />
              </Tooltip>
            </Space>
          </div>

          {/* 产品信息面板 */}
          <div style={{
            position: 'absolute',
            top: 16,
            right: 16,
            zIndex: 10,
            background: 'rgba(255, 255, 255, 0.9)',
            padding: '12px 16px',
            borderRadius: '8px',
            backdropFilter: 'blur(10px)',
            maxWidth: '300px'
          }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
              <Title level={4} style={{ margin: 0 }}>
                {experienceType} 体验
              </Title>
              <Badge count={interactionCount} size="small" />
            </div>
            
            <Paragraph style={{ margin: 0, marginBottom: '8px', fontWeight: 'bold' }}>
              {productName}
            </Paragraph>
            
            <div style={{ marginBottom: '8px' }}>
              <AntText strong style={{ fontSize: '18px', color: '#52c41a' }}>
                {formatNumber.price(productPrice)}
              </AntText>
            </div>
            
            <Paragraph style={{ margin: 0, fontSize: '12px', color: '#666' }}>
              {productDescription}
            </Paragraph>
            
            <Alert
              message={experienceType === 'AR' ? 'AR体验提示' : 'VR体验提示'}
              description={
                experienceType === 'AR' 
                  ? '请允许相机权限，将手机对准商品进行AR体验'
                  : '使用鼠标拖拽旋转视角，滚轮缩放，享受沉浸式VR体验'
              }
              type="info"
              showIcon
              size="small"
              style={{ marginTop: '8px' }}
            />
          </div>

          {/* 设置面板 */}
          <AnimatePresence>
            {showSettings && (
              <motion.div
                initial={{ opacity: 0, x: -300 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0, x: -300 }}
                style={{
                  position: 'absolute',
                  top: 16,
                  left: 16,
                  zIndex: 10,
                  width: '280px',
                  background: 'rgba(255, 255, 255, 0.95)',
                  borderRadius: '8px',
                  backdropFilter: 'blur(10px)',
                  padding: '16px'
                }}
              >
                <EnvironmentSettings 
                  environmentType={environmentType}
                  onEnvironmentChange={setEnvironmentType}
                />
                
                <div style={{ marginBottom: '16px' }}>
                  <Title level={5} style={{ margin: '0 0 8px 0' }}>动画速度</Title>
                  <Slider
                    min={0}
                    max={3}
                    step={0.1}
                    value={animationSpeed}
                    onChange={setAnimationSpeed}
                    marks={{
                      0: '静止',
                      1: '正常',
                      2: '快速',
                      3: '极快'
                    }}
                  />
                </div>
                
                <div style={{ marginBottom: '16px' }}>
                  <Space>
                    <Switch 
                      checked={soundEnabled}
                      onChange={setSoundEnabled}
                    />
                    <span>音效</span>
                  </Space>
                </div>
              </motion.div>
            )}
          </AnimatePresence>

          {/* 操作按钮 */}
          <div style={{
            position: 'absolute',
            bottom: 16,
            left: 16,
            zIndex: 10,
            background: 'rgba(255, 255, 255, 0.9)',
            padding: '8px 12px',
            borderRadius: '8px',
            backdropFilter: 'blur(10px)'
          }}>
            <Space>
              <Tooltip title="加入购物车">
                <Button 
                  icon={<ShoppingCartOutlined />}
                  onClick={handleAddToCartClick}
                  size="small"
                  type="primary"
                />
              </Tooltip>
              <Tooltip title="喜欢">
                <Button 
                  icon={<HeartOutlined />}
                  onClick={handleLikeClick}
                  size="small"
                />
              </Tooltip>
              <Tooltip title="分享">
                <Button 
                  icon={<ShareAltOutlined />}
                  onClick={handleShareClick}
                  size="small"
                />
              </Tooltip>
              <Tooltip title="截图">
                <Button 
                  icon={<CameraOutlined />}
                  onClick={handleScreenshot}
                  size="small"
                />
              </Tooltip>
              <Tooltip title={isRecording ? '停止录制' : '开始录制'}>
                <Button 
                  icon={<DownloadOutlined />}
                  onClick={handleRecord}
                  size="small"
                  type={isRecording ? 'primary' : 'default'}
                />
              </Tooltip>
            </Space>
          </div>

          {/* 3D场景 */}
          <Canvas
            camera={{ position: cameraPosition, fov: 75 }}
            style={{ height: '100%', background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' }}
            shadows
          >
            {/* 光照设置 */}
            <ambientLight intensity={0.4} />
            <directionalLight 
              position={[10, 10, 5]} 
              intensity={1} 
              castShadow
              shadow-mapSize-width={2048}
              shadow-mapSize-height={2048}
            />
            <pointLight position={[-10, -10, -5]} intensity={0.5} />
            
            {/* 3D模型 */}
            <Stage preset="rembrandt" intensity={1} environment={environmentType}>
              <Center>
                <EnhancedProductModel 
                  productId={productId}
                  productName={productName}
                  productPrice={productPrice}
                  experienceType={experienceType}
                  autoRotate={autoRotate}
                  animationSpeed={animationSpeed}
                  onInteraction={handleInteraction}
                  modelUrl={modelUrl}
                />
              </Center>
            </Stage>
            
            {/* 环境 */}
            <Environment preset={environmentType as any} />
            
            {/* 阴影 */}
            <ContactShadows 
              opacity={0.4} 
              scale={10} 
              blur={2} 
              far={4.5} 
              resolution={256} 
              color="#000000" 
            />
            
            {/* 控制器 */}
            <OrbitControls 
              enablePan={true}
              enableZoom={true}
              enableRotate={true}
              minDistance={2}
              maxDistance={20}
              autoRotate={false}
            />
          </Canvas>

          {/* 交互提示 */}
          <div style={{
            position: 'absolute',
            bottom: 16,
            right: 16,
            zIndex: 10,
            background: 'rgba(0, 0, 0, 0.7)',
            color: 'white',
            padding: '8px 16px',
            borderRadius: '20px',
            fontSize: '14px'
          }}>
            {experienceType === 'AR' 
              ? '📱 移动设备查看AR效果' 
              : '🖱️ 拖拽旋转 • 滚轮缩放 • 右键平移'
            }
          </div>
        </div>
      </Card>
    </motion.div>
  )
}

export default EnhancedARVRExperience
