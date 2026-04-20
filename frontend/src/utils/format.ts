/**
 * 通用格式化工具函数
 *
 * 各模块 Agent 应从此文件引入格式化函数，避免在各 View 中重复定义。
 */

/**
 * 格式化数值为带千分位的字符串
 * @param value 数值或字符串
 * @param decimals 保留小数位数，默认 2
 * @returns 格式化后的字符串，如 '1,234.56'
 */
export function formatNumber(value: number | string | null | undefined, decimals = 2): string {
  if (value == null || value === '') return '-'
  const num = typeof value === 'string' ? parseFloat(value) : value
  if (isNaN(num)) return '-'
  return num.toLocaleString('zh-CN', {
    minimumFractionDigits: decimals,
    maximumFractionDigits: decimals,
  })
}

/**
 * 格式化金额（人民币）
 * @param value 金额数值
 * @returns 如 '¥1,234.56'
 */
export function formatCurrency(value: number | string | null | undefined): string {
  if (value == null || value === '') return '-'
  const num = typeof value === 'string' ? parseFloat(value) : value
  if (isNaN(num)) return '-'
  return `¥${formatNumber(num)}`
}

/**
 * 格式化日期
 * @param value ISO 日期字符串或 Date 对象
 * @param format 'date' | 'datetime'，默认 'date'
 * @returns 如 '2026-04-18' 或 '2026-04-18 10:30:00'
 */
export function formatDate(value: string | Date | null | undefined, format: 'date' | 'datetime' = 'date'): string {
  if (!value) return '-'
  const date = typeof value === 'string' ? new Date(value) : value
  if (isNaN(date.getTime())) return '-'

  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')

  if (format === 'date') {
    return `${y}-${m}-${d}`
  }

  const h = String(date.getHours()).padStart(2, '0')
  const min = String(date.getMinutes()).padStart(2, '0')
  const s = String(date.getSeconds()).padStart(2, '0')
  return `${y}-${m}-${d} ${h}:${min}:${s}`
}

/**
 * 格式化百分比
 * @param value 小数值（0.05 = 5%）或百分比值
 * @param isDecimal 是否为小数形式，默认 true
 */
export function formatPercent(value: number | null | undefined, isDecimal = true): string {
  if (value == null) return '-'
  const pct = isDecimal ? value * 100 : value
  return `${pct.toFixed(2)}%`
}

/**
 * 格式化文件大小
 */
export function formatFileSize(bytes: number | null | undefined): string {
  if (bytes == null || bytes === 0) return '0 B'
  const units = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(1024))
  return `${(bytes / Math.pow(1024, i)).toFixed(1)} ${units[i]}`
}

/**
 * 截断文本，超长则显示省略号
 */
export function truncateText(text: string | null | undefined, maxLength = 20): string {
  if (!text) return '-'
  return text.length > maxLength ? `${text.slice(0, maxLength)}...` : text
}
