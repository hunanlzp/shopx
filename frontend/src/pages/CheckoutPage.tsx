import React, { useState, useEffect } from 'react'
import { Steps, Card, Button, message, Spin } from 'antd'
import { ShoppingCartOutlined, EnvironmentOutlined, CreditCardOutlined } from '@ant-design/icons'
import { CartStatusChecker, CartItemCard } from '../components/CartComponents'
import { ShippingAddressManager, ShippingOptionsSelector } from '../components/LogisticsComponents'
import { TotalPriceCalculator } from '../components/PriceComponents'
import ApiService from '../services/api'
import { useStore } from '../store/useStore'

const { Step } = Steps

const CheckoutPage: React.FC = () => {
  const [currentStep, setCurrentStep] = useState(0)
  const [cartItems, setCartItems] = useState<any[]>([])
  const [selectedAddress, setSelectedAddress] = useState<any>(null)
  const [selectedShippingMethod, setSelectedShippingMethod] = useState<any>(null)
  const [selectedPaymentMethod, setSelectedPaymentMethod] = useState<any>(null)
  const [loading, setLoading] = useState(false)
  const { cart } = useStore()

  useEffect(() => {
    loadCart()
  }, [])

  const loadCart = async () => {
    try {
      const response = await ApiService.getCart()
      if (response.code === 200) {
        setCartItems(response.data || [])
      }
    } catch (error: any) {
      message.error(error.message || '加载购物车失败')
    }
  }

  const handleCheckout = async () => {
    if (!selectedAddress) {
      message.error('请选择收货地址')
      return
    }
    if (!selectedShippingMethod) {
      message.error('请选择配送方式')
      return
    }

    try {
      setLoading(true)
      const cartItemIds = cartItems.map(item => item.id)
      const response = await ApiService.createCheckoutOrder({
        cartItemIds,
        shippingAddressId: selectedAddress.id,
        paymentMethodId: selectedPaymentMethod?.id,
        shippingMethod: selectedShippingMethod.method,
      })
      
      if (response.code === 200) {
        message.success('订单创建成功')
        // 跳转到订单详情或支付页面
      }
    } catch (error: any) {
      message.error(error.message || '结算失败')
    } finally {
      setLoading(false)
    }
  }

  const steps = [
    {
      title: '购物车',
      icon: <ShoppingCartOutlined />,
      content: (
        <div>
          <CartStatusChecker cartItemIds={cartItems.map(item => item.id)} autoCheck />
          {cartItems.map(item => (
            <CartItemCard key={item.id} item={item} />
          ))}
        </div>
      ),
    },
    {
      title: '收货地址',
      icon: <EnvironmentOutlined />,
      content: (
        <div>
          <ShippingAddressManager
            onSelect={setSelectedAddress}
            selectedAddressId={selectedAddress?.id}
          />
          {selectedAddress && (
            <div style={{ marginTop: 16 }}>
              <ShippingOptionsSelector
                productId={cartItems[0]?.product?.id || 0}
                addressId={selectedAddress.id}
                onChange={setSelectedShippingMethod}
              />
            </div>
          )}
        </div>
      ),
    },
    {
      title: '支付',
      icon: <CreditCardOutlined />,
      content: (
        <div>
          <Card title="订单确认">
            <div>
              <TotalPriceCalculator
                productId={cartItems[0]?.product?.id || 0}
                quantity={cartItems.reduce((sum, item) => sum + item.quantity, 0)}
                addressId={selectedAddress?.id}
              />
            </div>
            <Button
              type="primary"
              size="large"
              block
              onClick={handleCheckout}
              loading={loading}
              style={{ marginTop: 24 }}
            >
              确认订单
            </Button>
          </Card>
        </div>
      ),
    },
  ]

  return (
    <div style={{ padding: 24, maxWidth: 1200, margin: '0 auto' }}>
      <Steps current={currentStep} items={steps.map(s => ({ title: s.title, icon: s.icon }))} />
      <Card style={{ marginTop: 24 }}>
        {steps[currentStep].content}
        <div style={{ marginTop: 24, display: 'flex', justifyContent: 'space-between' }}>
          {currentStep > 0 && (
            <Button onClick={() => setCurrentStep(currentStep - 1)}>上一步</Button>
          )}
          {currentStep < steps.length - 1 && (
            <Button type="primary" onClick={() => setCurrentStep(currentStep + 1)}>
              下一步
            </Button>
          )}
        </div>
      </Card>
    </div>
  )
}

export default CheckoutPage

