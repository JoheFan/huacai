export interface UserModuleOption {
  key: string
  label: string
  description: string
}

export interface UserModuleGroup {
  title: string
  items: UserModuleOption[]
}

export const userModuleGroups: UserModuleGroup[] = [
  {
    title: '客户管理',
    items: [
      {
        key: 'customers',
        label: '客户管理',
        description: '查看和维护客户档案主数据。',
      },
      {
        key: 'customer-risks',
        label: '风险评估',
        description: '维护客户风险评估记录。',
      },
      {
        key: 'customer-debts',
        label: '负债登记',
        description: '维护客户负债登记记录。',
      },
    ],
  },
  {
    title: '经营管理',
    items: [
      {
        key: 'opportunities',
        label: '商机管理',
        description: '跟进商机推进和记录。',
      },
    ],
  },
  {
    title: '经营台账',
    items: [
      {
        key: 'loan-orders',
        label: '借贷管理',
        description: '处理借贷主单和余额变化。',
      },
      {
        key: 'repayments',
        label: '还款明细',
        description: '登记本金、利息和还款日期。',
      },
    ],
  },
]

const registryOrder = userModuleGroups.flatMap((group) => group.items.map((item) => item.key))

export const defaultVisibleModuleKeys = [...registryOrder]

export const sanitizeVisibleModuleKeys = (moduleKeys: string[] | undefined | null) => {
  const requested = new Set((moduleKeys ?? []).filter(Boolean))
  return registryOrder.filter((key) => requested.has(key))
}

export const getModuleSelectionSummary = (moduleKeys: string[] | undefined | null) =>
  `已选 ${sanitizeVisibleModuleKeys(moduleKeys).length} 个模块`

export const getUserModuleAccessMode = (roleCodes: string[] | undefined | null) => {
  const isAdmin = (roleCodes ?? []).includes('ADMIN')

  return {
    editable: !isAdmin,
    hint: isAdmin
      ? '管理员默认拥有全部模块访问能力，这里不做单独勾选。'
      : '未勾选的模块不会出现在左侧菜单，对应接口也不可访问。',
  }
}
