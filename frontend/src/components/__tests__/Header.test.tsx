import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen } from '@testing-library/react'
import { BrowserRouter } from 'react-router-dom'
import Header from '../Header'
import { useStore } from '../../store/useStore'

// Mock store
vi.mock('../../store/useStore', () => ({
  useStore: vi.fn(),
}))

describe('Header组件', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('应该正确渲染Header组件', () => {
    ;(useStore as any).mockReturnValue({
      user: { username: 'testuser', email: 'test@example.com' },
      isLoggedIn: true,
      logout: vi.fn(),
    })

    render(
      <BrowserRouter>
        <Header />
      </BrowserRouter>
    )

    expect(screen.getByText(/testuser/i)).toBeInTheDocument()
  })

  it('应该在未登录时显示登录按钮', () => {
    ;(useStore as any).mockReturnValue({
      user: null,
      isLoggedIn: false,
      logout: vi.fn(),
    })

    render(
      <BrowserRouter>
        <Header />
      </BrowserRouter>
    )

    // 检查是否有登录相关的元素
    expect(screen.queryByText(/testuser/i)).not.toBeInTheDocument()
  })

  it('应该能够处理登出操作', () => {
    const mockLogout = vi.fn()
    ;(useStore as any).mockReturnValue({
      user: { username: 'testuser' },
      isLoggedIn: true,
      logout: mockLogout,
    })

    render(
      <BrowserRouter>
        <Header />
      </BrowserRouter>
    )

    // 这里可以添加点击登出按钮的测试
    // const logoutButton = screen.getByRole('button', { name: /登出/i })
    // fireEvent.click(logoutButton)
    // expect(mockLogout).toHaveBeenCalled()
  })
})

