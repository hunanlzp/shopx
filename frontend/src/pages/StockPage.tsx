import React from 'react'
import { Tabs } from 'antd'
import {
  StockNotificationManager,
  ReservationManager,
  AlternativeProducts,
} from '../components/StockComponents'
import { StockNotificationCard, ReservationCard } from '../components/StockComponents'

const { TabPane } = Tabs

const StockPage: React.FC = () => {
  const notificationManager = StockNotificationManager()
  const reservationManager = ReservationManager()

  return (
    <div style={{ padding: 24, maxWidth: 1200, margin: '0 auto' }}>
      <Tabs>
        <TabPane tab="缺货提醒" key="notifications">
          <div>
            {notificationManager.notifications.length === 0 ? (
              <div>暂无缺货提醒</div>
            ) : (
              notificationManager.notifications.map(notification => (
                <StockNotificationCard
                  key={notification.id}
                  notification={notification}
                  onCancel={notificationManager.loadNotifications}
                />
              ))
            )}
          </div>
        </TabPane>
        <TabPane tab="商品预订" key="reservations">
          <div>
            {reservationManager.reservations.length === 0 ? (
              <div>暂无商品预订</div>
            ) : (
              reservationManager.reservations.map(reservation => (
                <ReservationCard
                  key={reservation.id}
                  reservation={reservation}
                  onCancel={reservationManager.loadReservations}
                />
              ))
            )}
          </div>
        </TabPane>
        <TabPane tab="替代商品" key="alternatives">
          <AlternativeProducts productId={0} /> {/* 应该从路由或状态获取 */}
        </TabPane>
      </Tabs>
    </div>
  )
}

export default StockPage

