import { describe, it, expect } from 'vitest'
import {
  formatNumber,
  formatTime,
  performanceUtils,
  objectUtils,
} from '../utils'

describe('工具函数测试', () => {
  describe('formatNumber', () => {
    it('应该正确格式化价格', () => {
      expect(formatNumber.price(1234.56)).toBe('¥1234.56')
      expect(formatNumber.price(0)).toBe('¥0.00')
      expect(formatNumber.price(999999)).toBe('¥999999.00')
    })

    it('应该正确格式化数量', () => {
      expect(formatNumber.quantity(1000)).toBe('1.0K')
      expect(formatNumber.quantity(1000000)).toBe('1.0M')
      expect(formatNumber.quantity(100)).toBe('100')
    })

    it('应该正确格式化百分比', () => {
      expect(formatNumber.percentage(0.5)).toBe('50.0%')
      expect(formatNumber.percentage(0.123, 2)).toBe('12.30%')
    })
  })

  describe('formatTime', () => {
    it('应该正确格式化相对时间', () => {
      const now = new Date()
      const oneHourAgo = new Date(now.getTime() - 60 * 60 * 1000)
      const formatted = formatTime.fromNow(oneHourAgo.toISOString())
      expect(formatted).toContain('小时前')
    })

    it('应该处理刚刚的时间', () => {
      const now = new Date()
      const formatted = formatTime.fromNow(now.toISOString())
      expect(formatted).toBe('刚刚')
    })
  })

  describe('performanceUtils.debounce', () => {
    it('应该延迟执行函数', async () => {
      let callCount = 0
      const fn = () => {
        callCount++
      }
      const debouncedFn = performanceUtils.debounce(fn, 100)

      debouncedFn()
      debouncedFn()
      debouncedFn()

      expect(callCount).toBe(0)

      await new Promise((resolve) => setTimeout(resolve, 150))
      expect(callCount).toBe(1)
    })
  })

  describe('performanceUtils.throttle', () => {
    it('应该限制函数执行频率', async () => {
      let callCount = 0
      const fn = () => {
        callCount++
      }
      const throttledFn = performanceUtils.throttle(fn, 100)

      throttledFn()
      throttledFn()
      throttledFn()

      expect(callCount).toBe(1)

      await new Promise((resolve) => setTimeout(resolve, 150))
      throttledFn()
      expect(callCount).toBe(2)
    })
  })

  describe('objectUtils.deepClone', () => {
    it('应该深度克隆对象', () => {
      const obj = {
        a: 1,
        b: { c: 2, d: [3, 4] },
      }
      const cloned = objectUtils.deepClone(obj)

      expect(cloned).toEqual(obj)
      expect(cloned).not.toBe(obj)
      expect(cloned.b).not.toBe(obj.b)
    })

    it('应该处理数组', () => {
      const arr = [1, 2, { a: 3 }]
      const cloned = objectUtils.deepClone(arr)

      expect(cloned).toEqual(arr)
      expect(cloned).not.toBe(arr)
      expect(cloned[2]).not.toBe(arr[2])
    })
  })
})

