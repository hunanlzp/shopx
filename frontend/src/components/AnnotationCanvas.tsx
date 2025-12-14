import React, { useRef, useEffect, useState, useCallback } from 'react'
import { Button, Space, Input, ColorPicker, Slider, Select } from 'antd'
import { 
  EditOutlined, 
  UndoOutlined, 
  RedoOutlined, 
  ClearOutlined,
  SaveOutlined,
  FontSizeOutlined
} from '@ant-design/icons'
import { message } from 'antd'

interface Annotation {
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
}

interface AnnotationCanvasProps {
  width: number
  height: number
  productImage?: string
  userId: number
  username: string
  onAnnotationAdd?: (annotation: Annotation) => void
  annotations?: Annotation[]
  className?: string
}

const AnnotationCanvas: React.FC<AnnotationCanvasProps> = ({
  width = 800,
  height = 600,
  productImage,
  userId,
  username,
  onAnnotationAdd,
  annotations = [],
  className
}) => {
  const canvasRef = useRef<HTMLCanvasElement>(null)
  const [isDrawing, setIsDrawing] = useState(false)
  const [drawMode, setDrawMode] = useState<'draw' | 'text'>('draw')
  const [currentColor, setCurrentColor] = useState('#ff0000')
  const [lineWidth, setLineWidth] = useState(3)
  const [textInput, setTextInput] = useState('')
  const [textPosition, setTextPosition] = useState<{ x: number; y: number } | null>(null)
  const [drawingPath, setDrawingPath] = useState<Array<{ x: number; y: number }>>([])
  const [history, setHistory] = useState<ImageData[]>([])
  const [historyIndex, setHistoryIndex] = useState(-1)

  // 初始化画布
  useEffect(() => {
    const canvas = canvasRef.current
    if (!canvas) return

    const ctx = canvas.getContext('2d')
    if (!ctx) return

    // 设置画布大小
    canvas.width = width
    canvas.height = height

    // 绘制背景
    if (productImage) {
      const img = new Image()
      img.crossOrigin = 'anonymous'
      img.onload = () => {
        ctx.drawImage(img, 0, 0, width, height)
        saveHistory()
      }
      img.src = productImage
    } else {
      ctx.fillStyle = '#f5f5f5'
      ctx.fillRect(0, 0, width, height)
      saveHistory()
    }
  }, [width, height, productImage])

  // 保存历史记录
  const saveHistory = useCallback(() => {
    const canvas = canvasRef.current
    if (!canvas) return

    const ctx = canvas.getContext('2d')
    if (!ctx) return

    const imageData = ctx.getImageData(0, 0, canvas.width, canvas.height)
    setHistory(prev => {
      const newHistory = prev.slice(0, historyIndex + 1)
      newHistory.push(imageData)
      return newHistory.slice(-20) // 最多保存20步历史
    })
    setHistoryIndex(prev => Math.min(prev + 1, 19))
  }, [historyIndex])

  // 绘制所有标注
  useEffect(() => {
    const canvas = canvasRef.current
    if (!canvas) return

    const ctx = canvas.getContext('2d')
    if (!ctx) return

    // 清除画布
    ctx.clearRect(0, 0, canvas.width, canvas.height)

    // 重新绘制背景
    if (productImage) {
      const img = new Image()
      img.crossOrigin = 'anonymous'
      img.onload = () => {
        ctx.drawImage(img, 0, 0, width, height)
        drawAllAnnotations()
      }
      img.src = productImage
    } else {
      ctx.fillStyle = '#f5f5f5'
      ctx.fillRect(0, 0, width, height)
      drawAllAnnotations()
    }
  }, [annotations, productImage, width, height])

  // 绘制所有标注
  const drawAllAnnotations = useCallback(() => {
    const canvas = canvasRef.current
    if (!canvas) return

    const ctx = canvas.getContext('2d')
    if (!ctx) return

    annotations.forEach(annotation => {
      ctx.strokeStyle = annotation.color
      ctx.fillStyle = annotation.color
      ctx.lineWidth = annotation.lineWidth
      ctx.lineCap = 'round'
      ctx.lineJoin = 'round'

      if (annotation.type === 'draw' && annotation.path) {
        ctx.beginPath()
        annotation.path.forEach((point, index) => {
          if (index === 0) {
            ctx.moveTo(point.x, point.y)
          } else {
            ctx.lineTo(point.x, point.y)
          }
        })
        ctx.stroke()
      } else if (annotation.type === 'text' && annotation.content) {
        ctx.font = `${annotation.lineWidth * 5}px Arial`
        ctx.fillText(annotation.content, annotation.x, annotation.y)
      }
    })
  }, [annotations])

  // 获取鼠标位置
  const getMousePos = useCallback((e: React.MouseEvent<HTMLCanvasElement>) => {
    const canvas = canvasRef.current
    if (!canvas) return { x: 0, y: 0 }

    const rect = canvas.getBoundingClientRect()
    return {
      x: e.clientX - rect.left,
      y: e.clientY - rect.top
    }
  }, [])

  // 开始绘制
  const handleMouseDown = useCallback((e: React.MouseEvent<HTMLCanvasElement>) => {
    if (drawMode === 'text') {
      const pos = getMousePos(e)
      setTextPosition(pos)
      return
    }

    setIsDrawing(true)
    const pos = getMousePos(e)
    setDrawingPath([pos])
  }, [drawMode, getMousePos])

  // 绘制中
  const handleMouseMove = useCallback((e: React.MouseEvent<HTMLCanvasElement>) => {
    if (!isDrawing || drawMode !== 'draw') return

    const canvas = canvasRef.current
    if (!canvas) return

    const ctx = canvas.getContext('2d')
    if (!ctx) return

    const pos = getMousePos(e)
    setDrawingPath(prev => [...prev, pos])

    // 实时绘制
    ctx.strokeStyle = currentColor
    ctx.lineWidth = lineWidth
    ctx.lineCap = 'round'
    ctx.lineJoin = 'round'

    if (drawingPath.length > 0) {
      const lastPoint = drawingPath[drawingPath.length - 1]
      ctx.beginPath()
      ctx.moveTo(lastPoint.x, lastPoint.y)
      ctx.lineTo(pos.x, pos.y)
      ctx.stroke()
    }
  }, [isDrawing, drawMode, drawingPath, currentColor, lineWidth, getMousePos])

  // 结束绘制
  const handleMouseUp = useCallback(() => {
    if (!isDrawing) return

    setIsDrawing(false)

    if (drawMode === 'draw' && drawingPath.length > 0) {
      const annotation: Annotation = {
        id: `anno_${Date.now()}`,
        type: 'draw',
        x: drawingPath[0].x,
        y: drawingPath[0].y,
        path: [...drawingPath],
        color: currentColor,
        lineWidth,
        userId,
        username,
        timestamp: new Date().toISOString()
      }

      onAnnotationAdd?.(annotation)
      setDrawingPath([])
      saveHistory()
    }
  }, [isDrawing, drawMode, drawingPath, currentColor, lineWidth, userId, username, onAnnotationAdd, saveHistory])

  // 添加文字标注
  const handleAddText = useCallback(() => {
    if (!textPosition || !textInput.trim()) return

    const annotation: Annotation = {
      id: `anno_${Date.now()}`,
      type: 'text',
      x: textPosition.x,
      y: textPosition.y,
      content: textInput,
      color: currentColor,
      lineWidth,
      userId,
      username,
      timestamp: new Date().toISOString()
    }

    onAnnotationAdd?.(annotation)
    setTextInput('')
    setTextPosition(null)
    saveHistory()
    message.success('标注已添加')
  }, [textPosition, textInput, currentColor, lineWidth, userId, username, onAnnotationAdd, saveHistory])

  // 撤销
  const handleUndo = useCallback(() => {
    if (historyIndex <= 0) return

    const canvas = canvasRef.current
    if (!canvas) return

    const ctx = canvas.getContext('2d')
    if (!ctx) return

    const prevIndex = historyIndex - 1
    const imageData = history[prevIndex]
    if (imageData) {
      ctx.putImageData(imageData, 0, 0)
      setHistoryIndex(prevIndex)
    }
  }, [history, historyIndex])

  // 重做
  const handleRedo = useCallback(() => {
    if (historyIndex >= history.length - 1) return

    const canvas = canvasRef.current
    if (!canvas) return

    const ctx = canvas.getContext('2d')
    if (!ctx) return

    const nextIndex = historyIndex + 1
    const imageData = history[nextIndex]
    if (imageData) {
      ctx.putImageData(imageData, 0, 0)
      setHistoryIndex(nextIndex)
    }
  }, [history, historyIndex])

  // 清空画布
  const handleClear = useCallback(() => {
    const canvas = canvasRef.current
    if (!canvas) return

    const ctx = canvas.getContext('2d')
    if (!ctx) return

    ctx.clearRect(0, 0, canvas.width, canvas.height)
    
    if (productImage) {
      const img = new Image()
      img.crossOrigin = 'anonymous'
      img.onload = () => {
        ctx.drawImage(img, 0, 0, width, height)
        saveHistory()
      }
      img.src = productImage
    } else {
      ctx.fillStyle = '#f5f5f5'
      ctx.fillRect(0, 0, width, height)
      saveHistory()
    }
  }, [productImage, width, height, saveHistory])

  return (
    <div className={className}>
      <div style={{ marginBottom: '16px' }}>
        <Space>
          <Button
            type={drawMode === 'draw' ? 'primary' : 'default'}
            icon={<EditOutlined />}
            onClick={() => setDrawMode('draw')}
          >
            绘制
          </Button>
          <Button
            type={drawMode === 'text' ? 'primary' : 'default'}
            icon={<FontSizeOutlined />}
            onClick={() => setDrawMode('text')}
          >
            文字
          </Button>
          <ColorPicker
            value={currentColor}
            onChange={(color) => setCurrentColor(color.toHexString())}
            showText
          />
          <span>线宽:</span>
          <Slider
            min={1}
            max={10}
            value={lineWidth}
            onChange={setLineWidth}
            style={{ width: '100px' }}
          />
          <Button icon={<UndoOutlined />} onClick={handleUndo} disabled={historyIndex <= 0}>
            撤销
          </Button>
          <Button icon={<RedoOutlined />} onClick={handleRedo} disabled={historyIndex >= history.length - 1}>
            重做
          </Button>
          <Button icon={<ClearOutlined />} onClick={handleClear} danger>
            清空
          </Button>
        </Space>
      </div>

      <div style={{ position: 'relative', border: '1px solid #d9d9d9', borderRadius: '8px', overflow: 'hidden' }}>
        <canvas
          ref={canvasRef}
          onMouseDown={handleMouseDown}
          onMouseMove={handleMouseMove}
          onMouseUp={handleMouseUp}
          onMouseLeave={handleMouseUp}
          style={{ cursor: drawMode === 'draw' ? 'crosshair' : 'text', display: 'block' }}
        />
        
        {textPosition && (
          <div
            style={{
              position: 'absolute',
              left: textPosition.x,
              top: textPosition.y,
              background: 'white',
              padding: '4px 8px',
              borderRadius: '4px',
              border: '1px solid #d9d9d9',
              zIndex: 10
            }}
          >
            <Input
              value={textInput}
              onChange={(e) => setTextInput(e.target.value)}
              onPressEnter={handleAddText}
              onBlur={handleAddText}
              placeholder="输入文字..."
              autoFocus
              style={{ width: '200px' }}
            />
          </div>
        )}
      </div>

      {annotations.length > 0 && (
        <div style={{ marginTop: '16px' }}>
          <div style={{ fontSize: '12px', color: '#666' }}>
            共有 {annotations.length} 个标注
          </div>
        </div>
      )}
    </div>
  )
}

export default AnnotationCanvas

