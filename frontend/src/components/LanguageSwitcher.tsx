import React from 'react'
import { Select } from 'antd'
import { GlobalOutlined } from '@ant-design/icons'
import { useI18n } from '../i18n'
import type { Language } from '../i18n'

const LanguageSwitcher: React.FC = () => {
  const { language, setLanguage } = useI18n()

  const handleChange = (value: Language) => {
    setLanguage(value)
    // 刷新页面以应用语言更改（可选）
    // window.location.reload()
  }

  return (
    <Select
      value={language}
      onChange={handleChange}
      style={{ width: 100 }}
      suffixIcon={<GlobalOutlined />}
    >
      <Select.Option value="zh">中文</Select.Option>
      <Select.Option value="en">English</Select.Option>
    </Select>
  )
}

export default LanguageSwitcher

