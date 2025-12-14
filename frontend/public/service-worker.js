const CACHE_NAME = 'shopx-v1'
const RUNTIME_CACHE = 'shopx-runtime'
const STATIC_CACHE = 'shopx-static'

// 需要缓存的静态资源
const STATIC_ASSETS = [
  '/',
  '/index.html',
  '/manifest.json',
]

// 需要运行时缓存的API路径
const API_CACHE_PATTERNS = [
  /\/api\/v1\/products/,
  /\/api\/v1\/cart/,
]

// 安装Service Worker
self.addEventListener('install', (event) => {
  event.waitUntil(
    caches.open(STATIC_CACHE).then((cache) => {
      return cache.addAll(STATIC_ASSETS)
    })
  )
  self.skipWaiting()
})

// 激活Service Worker
self.addEventListener('activate', (event) => {
  event.waitUntil(
    caches.keys().then((cacheNames) => {
      return Promise.all(
        cacheNames
          .filter((cacheName) => {
            return cacheName !== STATIC_CACHE && cacheName !== RUNTIME_CACHE
          })
          .map((cacheName) => {
            return caches.delete(cacheName)
          })
      )
    })
  )
  return self.clients.claim()
})

// 拦截网络请求
self.addEventListener('fetch', (event) => {
  const { request } = event
  const url = new URL(request.url)

  // 静态资源：Cache First策略
  if (STATIC_ASSETS.some((asset) => url.pathname === asset)) {
    event.respondWith(
      caches.match(request).then((response) => {
        return response || fetch(request).then((response) => {
          const responseClone = response.clone()
          caches.open(STATIC_CACHE).then((cache) => {
            cache.put(request, responseClone)
          })
          return response
        })
      })
    )
    return
  }

  // API请求：Network First策略
  if (API_CACHE_PATTERNS.some((pattern) => pattern.test(url.pathname))) {
    event.respondWith(
      fetch(request)
        .then((response) => {
          const responseClone = response.clone()
          caches.open(RUNTIME_CACHE).then((cache) => {
            cache.put(request, responseClone)
          })
          return response
        })
        .catch(() => {
          return caches.match(request)
        })
    )
    return
  }

  // 其他请求：Network First
  event.respondWith(
    fetch(request).catch(() => {
      return caches.match(request)
    })
  )
})

// 后台同步
self.addEventListener('sync', (event) => {
  if (event.tag === 'sync-cart') {
    event.waitUntil(syncCart())
  }
})

async function syncCart() {
  // 同步购物车数据
  try {
    const cartData = await getCartFromIndexedDB()
    if (cartData && cartData.length > 0) {
      // 同步到服务器
      await fetch('/api/v1/cart/sync', {
        method: 'POST',
        body: JSON.stringify(cartData),
        headers: {
          'Content-Type': 'application/json',
        },
      })
    }
  } catch (error) {
    console.error('同步购物车失败:', error)
  }
}

async function getCartFromIndexedDB() {
  // 从IndexedDB获取购物车数据
  return new Promise((resolve) => {
    const request = indexedDB.open('shopx-db', 1)
    request.onsuccess = (event) => {
      const db = event.target.result
      const transaction = db.transaction(['cart'], 'readonly')
      const store = transaction.objectStore('cart')
      const getAllRequest = store.getAll()
      getAllRequest.onsuccess = () => {
        resolve(getAllRequest.result)
      }
    }
    request.onerror = () => {
      resolve([])
    }
  })
}

// 推送通知
self.addEventListener('push', (event) => {
  const data = event.data ? event.data.json() : {}
  const title = data.title || 'ShopX通知'
  const options = {
    body: data.body || '您有新的消息',
    icon: '/icon-192.png',
    badge: '/icon-192.png',
    data: data.url || '/',
  }

  event.waitUntil(
    self.registration.showNotification(title, options)
  )
})

// 通知点击
self.addEventListener('notificationclick', (event) => {
  event.notification.close()
  event.waitUntil(
    clients.openWindow(event.notification.data || '/')
  )
})

