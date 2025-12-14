import React, { useEffect } from 'react'
import { Routes, Route, Navigate } from 'react-router-dom'
import { Layout } from 'antd'
import Header from './components/Header'
import Sidebar from './components/Sidebar'
import HomePage from './pages/HomePage'
import ProductList from './pages/ProductList'
import ProductDetail from './pages/ProductDetail'
import Recommendation from './pages/Recommendation'
import Collaboration from './pages/Collaboration'
import ARVRPage from './pages/ARVRPage'
import RecyclePage from './pages/RecyclePage'
import Profile from './pages/Profile'
import AuthPage from './pages/AuthPage'
import CheckoutPage from './pages/CheckoutPage'
import LogisticsPage from './pages/LogisticsPage'
import ReturnPage from './pages/ReturnPage'
import SecurityPage from './pages/SecurityPage'
import StockPage from './pages/StockPage'
import ComparisonPage from './pages/ComparisonPage'
import WishlistPage from './pages/WishlistPage'
import CustomerServicePage from './pages/CustomerServicePage'
import PriceHistoryPage from './pages/PriceHistoryPage'
import ErrorBoundary from './components/ErrorBoundary'
import { useStore } from './store/useStore'
import './App.css'

const { Content } = Layout

const App: React.FC = () => {
  const { user, isLoggedIn, setUser } = useStore()

  useEffect(() => {
    // 检查本地存储的用户信息
    const savedUser = localStorage.getItem('user')
    const savedToken = localStorage.getItem('token')
    
    if (savedUser && savedToken) {
      try {
        const userData = JSON.parse(savedUser)
        setUser(userData)
      } catch (error) {
        console.error('解析用户数据失败:', error)
        localStorage.removeItem('user')
        localStorage.removeItem('token')
      }
    }
  }, [setUser])

  // 受保护的路由组件
  const ProtectedRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    return isLoggedIn ? <>{children}</> : <Navigate to="/auth" replace />
  }

  return (
    <ErrorBoundary>
      <Layout className="app-layout">
        <Routes>
          {/* 认证页面 */}
          <Route path="/auth" element={<AuthPage />} />
          
          {/* 受保护的页面 */}
          <Route path="/*" element={
            <ProtectedRoute>
              <Layout>
                <Header />
                <Layout>
                  <Sidebar />
                  <Layout className="main-layout">
                    <Content className="main-content">
                      <Routes>
                        <Route path="/" element={<HomePage />} />
                        <Route path="/products" element={<ProductList />} />
                        <Route path="/products/:id" element={<ProductDetail />} />
                        <Route path="/recommendation" element={<Recommendation />} />
                        <Route path="/collaboration" element={<Collaboration />} />
                        <Route path="/ar-vr" element={<ARVRPage />} />
                        <Route path="/recycle" element={<RecyclePage />} />
                        <Route path="/profile" element={<Profile />} />
                        <Route path="/checkout" element={<CheckoutPage />} />
                        <Route path="/logistics/:orderId" element={<LogisticsPage />} />
                        <Route path="/return" element={<ReturnPage />} />
                        <Route path="/security" element={<SecurityPage />} />
                        <Route path="/stock" element={<StockPage />} />
                        <Route path="/comparison" element={<ComparisonPage />} />
                        <Route path="/wishlist" element={<WishlistPage />} />
                        <Route path="/customer-service" element={<CustomerServicePage />} />
                        <Route path="/price-history/:productId" element={<PriceHistoryPage />} />
                      </Routes>
                    </Content>
                  </Layout>
                </Layout>
              </Layout>
            </ProtectedRoute>
          } />
        </Routes>
      </Layout>
    </ErrorBoundary>
  )
}

export default App
