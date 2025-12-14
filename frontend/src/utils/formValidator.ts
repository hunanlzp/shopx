/**
 * 表单验证工具
 */
export class FormValidator {
  
  /**
   * 验证规则类型
   */
  static readonly RULES = {
    // 必填
    required: (message: string = '此字段为必填项') => ({
      required: true,
      message
    }),
    
    // 邮箱
    email: (message: string = '请输入有效的邮箱地址') => ({
      type: 'email' as const,
      message
    }),
    
    // 手机号
    phone: (message: string = '请输入有效的手机号码') => ({
      pattern: /^1[3-9]\d{9}$/,
      message
    }),
    
    // 用户名
    username: (message: string = '用户名应为3-20位字母、数字或下划线') => ({
      pattern: /^[a-zA-Z0-9_]{3,20}$/,
      message
    }),
    
    // 密码
    password: (message: string = '密码应为6-20位，包含字母和数字') => ({
      pattern: /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d@$!%*#?&]{6,20}$/,
      message
    }),
    
    // 长度
    length: (min: number, max: number, message?: string) => ({
      min,
      max,
      message: message || `长度应在${min}-${max}位之间`
    }),
    
    // 最小长度
    minLength: (min: number, message?: string) => ({
      min,
      message: message || `长度不能少于${min}位`
    }),
    
    // 最大长度
    maxLength: (max: number, message?: string) => ({
      max,
      message: message || `长度不能超过${max}位`
    }),
    
    // 数字范围
    range: (min: number, max: number, message?: string) => ({
      type: 'number' as const,
      min,
      max,
      message: message || `数值应在${min}-${max}之间`
    }),
    
    // 最小数值
    min: (min: number, message?: string) => ({
      type: 'number' as const,
      min,
      message: message || `数值不能小于${min}`
    }),
    
    // 最大数值
    max: (max: number, message?: string) => ({
      type: 'number' as const,
      max,
      message: message || `数值不能大于${max}`
    }),
    
    // 正则表达式
    pattern: (pattern: RegExp, message: string) => ({
      pattern,
      message
    }),
    
    // 自定义验证
    validator: (validator: (rule: any, value: any) => Promise<void>) => ({
      validator
    })
  }
  
  /**
   * 常用验证规则组合
   */
  static readonly COMMON_RULES = {
    // 用户名规则
    username: [
      FormValidator.RULES.required('用户名不能为空'),
      FormValidator.RULES.username()
    ],
    
    // 邮箱规则
    email: [
      FormValidator.RULES.required('邮箱不能为空'),
      FormValidator.RULES.email()
    ],
    
    // 手机号规则
    phone: [
      FormValidator.RULES.required('手机号不能为空'),
      FormValidator.RULES.phone()
    ],
    
    // 密码规则
    password: [
      FormValidator.RULES.required('密码不能为空'),
      FormValidator.RULES.password()
    ],
    
    // 确认密码规则
    confirmPassword: (passwordField: string = 'password') => [
      FormValidator.RULES.required('请确认密码'),
      FormValidator.RULES.validator(async (rule: any, value: any) => {
        const form = rule.field.split('.')[0]
        const password = form ? form[passwordField] : value
        if (value !== password) {
          throw new Error('两次输入的密码不一致')
        }
      })
    ],
    
    // 商品名称规则
    productName: [
      FormValidator.RULES.required('商品名称不能为空'),
      FormValidator.RULES.length(2, 100, '商品名称长度应在2-100位之间')
    ],
    
    // 商品价格规则
    productPrice: [
      FormValidator.RULES.required('商品价格不能为空'),
      FormValidator.RULES.min(0.01, '商品价格必须大于0')
    ],
    
    // 商品库存规则
    productStock: [
      FormValidator.RULES.required('商品库存不能为空'),
      FormValidator.RULES.min(0, '商品库存不能小于0')
    ],
    
    // 商品描述规则
    productDescription: [
      FormValidator.RULES.required('商品描述不能为空'),
      FormValidator.RULES.length(10, 1000, '商品描述长度应在10-1000位之间')
    ]
  }
  
  /**
   * 验证表单数据
   */
  static async validateForm(form: any, rules: Record<string, any[]>): Promise<boolean> {
    try {
      const errors: Record<string, string[]> = {}
      
      for (const [field, fieldRules] of Object.entries(rules)) {
        const value = form[field]
        
        for (const rule of fieldRules) {
          try {
            await this.validateField(value, rule)
          } catch (error) {
            if (!errors[field]) {
              errors[field] = []
            }
            errors[field].push(error.message)
          }
        }
      }
      
      if (Object.keys(errors).length > 0) {
        throw new Error('表单验证失败')
      }
      
      return true
    } catch (error) {
      console.error('表单验证失败:', error)
      return false
    }
  }
  
  /**
   * 验证单个字段
   */
  static async validateField(value: any, rule: any): Promise<void> {
    // 必填验证
    if (rule.required && (value === undefined || value === null || value === '')) {
      throw new Error(rule.message || '此字段为必填项')
    }
    
    // 如果值为空且不是必填，跳过其他验证
    if (!rule.required && (value === undefined || value === null || value === '')) {
      return
    }
    
    // 类型验证
    if (rule.type === 'email') {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
      if (!emailRegex.test(value)) {
        throw new Error(rule.message || '请输入有效的邮箱地址')
      }
    }
    
    if (rule.type === 'number') {
      const numValue = Number(value)
      if (isNaN(numValue)) {
        throw new Error(rule.message || '请输入有效的数字')
      }
      
      if (rule.min !== undefined && numValue < rule.min) {
        throw new Error(rule.message || `数值不能小于${rule.min}`)
      }
      
      if (rule.max !== undefined && numValue > rule.max) {
        throw new Error(rule.message || `数值不能大于${rule.max}`)
      }
    }
    
    // 长度验证
    if (rule.min !== undefined && value.length < rule.min) {
      throw new Error(rule.message || `长度不能少于${rule.min}位`)
    }
    
    if (rule.max !== undefined && value.length > rule.max) {
      throw new Error(rule.message || `长度不能超过${rule.max}位`)
    }
    
    // 正则验证
    if (rule.pattern && !rule.pattern.test(value)) {
      throw new Error(rule.message || '格式不正确')
    }
    
    // 自定义验证
    if (rule.validator) {
      await rule.validator(rule, value)
    }
  }
  
  /**
   * 获取字段错误信息
   */
  static getFieldError(errors: Record<string, string[]>, field: string): string | undefined {
    return errors[field]?.[0]
  }
  
  /**
   * 检查表单是否有错误
   */
  static hasErrors(errors: Record<string, string[]>): boolean {
    return Object.keys(errors).length > 0
  }
  
  /**
   * 清除表单错误
   */
  static clearErrors(errors: Record<string, string[]>, field?: string): Record<string, string[]> {
    if (field) {
      const newErrors = { ...errors }
      delete newErrors[field]
      return newErrors
    }
    return {}
  }
  
  /**
   * 实时验证
   */
  static createRealTimeValidator(rules: any[]) {
    return async (value: any) => {
      for (const rule of rules) {
        try {
          await this.validateField(value, rule)
        } catch (error) {
          throw error
        }
      }
    }
  }
}

/**
 * 表单验证Hook
 */
export const useFormValidation = () => {
  const [errors, setErrors] = React.useState<Record<string, string[]>>({})
  const [touched, setTouched] = React.useState<Record<string, boolean>>({})
  
  const validateField = async (field: string, value: any, rules: any[]) => {
    try {
      for (const rule of rules) {
        await FormValidator.validateField(value, rule)
      }
      
      // 清除字段错误
      setErrors(prev => {
        const newErrors = { ...prev }
        delete newErrors[field]
        return newErrors
      })
      
      return true
    } catch (error) {
      // 设置字段错误
      setErrors(prev => ({
        ...prev,
        [field]: [error.message]
      }))
      
      return false
    }
  }
  
  const validateForm = async (form: any, rules: Record<string, any[]>) => {
    const newErrors: Record<string, string[]> = {}
    
    for (const [field, fieldRules] of Object.entries(rules)) {
      const value = form[field]
      
      try {
        for (const rule of fieldRules) {
          await FormValidator.validateField(value, rule)
        }
      } catch (error) {
        newErrors[field] = [error.message]
      }
    }
    
    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }
  
  const setFieldTouched = (field: string) => {
    setTouched(prev => ({
      ...prev,
      [field]: true
    }))
  }
  
  const clearErrors = (field?: string) => {
    if (field) {
      setErrors(prev => {
        const newErrors = { ...prev }
        delete newErrors[field]
        return newErrors
      })
    } else {
      setErrors({})
    }
  }
  
  const getFieldError = (field: string) => {
    return errors[field]?.[0]
  }
  
  const hasFieldError = (field: string) => {
    return !!errors[field]?.length
  }
  
  const isFieldTouched = (field: string) => {
    return !!touched[field]
  }
  
  const shouldShowError = (field: string) => {
    return isFieldTouched(field) && hasFieldError(field)
  }
  
  return {
    errors,
    touched,
    validateField,
    validateForm,
    setFieldTouched,
    clearErrors,
    getFieldError,
    hasFieldError,
    isFieldTouched,
    shouldShowError
  }
}
