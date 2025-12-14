 // 国际化配置
export type Language = 'zh' | 'en'

export interface Translations {
  common: {
    welcome: string
    loading: string
    error: string
    success: string
    confirm: string
    cancel: string
    save: string
    delete: string
    edit: string
    search: string
    submit: string
  }
  auth: {
    login: string
    logout: string
    register: string
    username: string
    password: string
    email: string
    rememberMe: string
    forgotPassword: string
  }
  product: {
    title: string
    price: string
    description: string
    addToCart: string
    buyNow: string
    inStock: string
    outOfStock: string
  }
  cart: {
    title: string
    empty: string
    total: string
    checkout: string
    remove: string
  }
}

const translations: Record<Language, Translations> = {
  zh: {
    common: {
      welcome: '欢迎',
      loading: '加载中...',
      error: '错误',
      success: '成功',
      confirm: '确认',
      cancel: '取消',
      save: '保存',
      delete: '删除',
      edit: '编辑',
      search: '搜索',
      submit: '提交',
    },
    auth: {
      login: '登录',
      logout: '登出',
      register: '注册',
      username: '用户名',
      password: '密码',
      email: '邮箱',
      rememberMe: '记住我',
      forgotPassword: '忘记密码',
    },
    product: {
      title: '商品',
      price: '价格',
      description: '描述',
      addToCart: '加入购物车',
      buyNow: '立即购买',
      inStock: '有货',
      outOfStock: '缺货',
    },
    cart: {
      title: '购物车',
      empty: '购物车为空',
      total: '总计',
      checkout: '结算',
      remove: '移除',
    },
  },
  en: {
    common: {
      welcome: 'Welcome',
      loading: 'Loading...',
      error: 'Error',
      success: 'Success',
      confirm: 'Confirm',
      cancel: 'Cancel',
      save: 'Save',
      delete: 'Delete',
      edit: 'Edit',
      search: 'Search',
      submit: 'Submit',
    },
    auth: {
      login: 'Login',
      logout: 'Logout',
      register: 'Register',
      username: 'Username',
      password: 'Password',
      email: 'Email',
      rememberMe: 'Remember Me',
      forgotPassword: 'Forgot Password',
    },
    product: {
      title: 'Product',
      price: 'Price',
      description: 'Description',
      addToCart: 'Add to Cart',
      buyNow: 'Buy Now',
      inStock: 'In Stock',
      outOfStock: 'Out of Stock',
    },
    cart: {
      title: 'Shopping Cart',
      empty: 'Cart is empty',
      total: 'Total',
      checkout: 'Checkout',
      remove: 'Remove',
    },
  },
}

class I18n {
  private currentLanguage: Language = 'zh'

  constructor() {
    // 从localStorage或浏览器语言设置中获取语言
    const savedLanguage = localStorage.getItem('language') as Language
    const browserLanguage = navigator.language.split('-')[0] as Language
    
    if (savedLanguage && (savedLanguage === 'zh' || savedLanguage === 'en')) {
      this.currentLanguage = savedLanguage
    } else if (browserLanguage === 'en') {
      this.currentLanguage = 'en'
    }
  }

  setLanguage(language: Language): void {
    this.currentLanguage = language
    localStorage.setItem('language', language)
    // 更新HTML lang属性
    document.documentElement.lang = language === 'zh' ? 'zh-CN' : 'en'
  }

  getLanguage(): Language {
    return this.currentLanguage
  }

  t(key: keyof Translations): Translations[keyof Translations] {
    return translations[this.currentLanguage][key]
  }

  // 获取翻译对象的便捷方法
  getTranslations(): Translations {
    return translations[this.currentLanguage]
  }
}

export const i18n = new I18n()

// React Hook for i18n
export function useI18n() {
  return {
    t: i18n.t.bind(i18n),
    language: i18n.getLanguage(),
    setLanguage: i18n.setLanguage.bind(i18n),
    translations: i18n.getTranslations(),
  }
}

