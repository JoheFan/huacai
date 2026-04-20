export type ActionTone = 'primary' | 'default' | 'danger'

export interface ActionItem {
  key: string
  label: string
  tone: ActionTone
  disabled?: boolean
}

export interface ToolbarActionLayout {
  secondary: ActionItem[]
  primary: ActionItem
  overflow: ActionItem[]
}

export interface RowActionLayout {
  primary: ActionItem
  overflow: ActionItem[]
}

export const getCustomerListToolbarActions = (): ToolbarActionLayout => ({
  secondary: [
    {
      key: 'refresh',
      label: '刷新',
      tone: 'default',
    },
  ],
  primary: {
    key: 'create',
    label: '新增客户',
    tone: 'primary',
  },
  overflow: [
    {
      key: 'import',
      label: '导入档案',
      tone: 'default',
      disabled: true,
    },
    {
      key: 'export',
      label: '导出',
      tone: 'default',
      disabled: true,
    },
  ],
})

export const getRecordListToolbarActions = (): ToolbarActionLayout => ({
  secondary: [
    {
      key: 'refresh',
      label: '刷新',
      tone: 'default',
    },
  ],
  primary: {
    key: 'create',
    label: '新增记录',
    tone: 'primary',
  },
  overflow: [
    {
      key: 'import',
      label: '导入档案',
      tone: 'default',
      disabled: true,
    },
    {
      key: 'export',
      label: '导出',
      tone: 'default',
      disabled: true,
    },
  ],
})

export const getCustomerListRowActions = (): RowActionLayout => ({
  primary: {
    key: 'archive',
    label: '档案',
    tone: 'primary',
  },
  overflow: [
    {
      key: 'risks',
      label: '风险评估',
      tone: 'default',
    },
    {
      key: 'debts',
      label: '负债登记',
      tone: 'default',
    },
  ],
})

export const getRecordListRowActions = (): RowActionLayout => ({
  primary: {
    key: 'edit',
    label: '编辑',
    tone: 'primary',
  },
  overflow: [
    {
      key: 'archive',
      label: '档案',
      tone: 'default',
    },
    {
      key: 'delete',
      label: '删除',
      tone: 'danger',
    },
  ],
})
