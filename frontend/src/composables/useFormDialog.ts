import { ref, type Ref } from 'vue'
import { ElMessage } from 'element-plus'

/**
 * 通用表单弹窗 composable
 *
 * 封装了弹窗的打开/关闭、编辑/新增模式判断、提交逻辑和 loading 状态。
 * 各模块 Agent 在新增/编辑弹窗中应优先使用此 composable。
 *
 * @example
 * ```ts
 * const { dialogVisible, isEdit, editingId, formData, submitting, openCreate, openEdit, closeDialog, handleSubmit } =
 *   useFormDialog<CustomerForm>({
 *     defaultForm: () => ({ name: '', phone: '' }),
 *     onSubmit: async (data, isEdit, id) => {
 *       if (isEdit) await updateCustomer(id!, data)
 *       else await createCustomer(data)
 *     },
 *     onSuccess: () => fetchList(),
 *   })
 * ```
 */

export interface UseFormDialogOptions<T> {
  /** 返回表单默认值的函数 */
  defaultForm: () => T
  /** 提交回调 */
  onSubmit: (data: T, isEdit: boolean, id?: number | string) => Promise<void>
  /** 提交成功后的回调（如刷新列表） */
  onSuccess?: () => void
  /** 成功提示文案，默认'操作成功' */
  successMessage?: string
}

export function useFormDialog<T extends Record<string, unknown>>(options: UseFormDialogOptions<T>) {
  const { defaultForm, onSubmit, onSuccess, successMessage = '操作成功' } = options

  const dialogVisible = ref(false)
  const isEdit = ref(false)
  const editingId: Ref<number | string | undefined> = ref(undefined)
  const formData = ref<T>(defaultForm()) as Ref<T>
  const submitting = ref(false)

  const openCreate = () => {
    isEdit.value = false
    editingId.value = undefined
    formData.value = defaultForm()
    dialogVisible.value = true
  }

  const openEdit = (id: number | string, data: Partial<T>) => {
    isEdit.value = true
    editingId.value = id
    formData.value = { ...defaultForm(), ...data }
    dialogVisible.value = true
  }

  const closeDialog = () => {
    dialogVisible.value = false
  }

  const handleSubmit = async () => {
    submitting.value = true
    try {
      await onSubmit(formData.value, isEdit.value, editingId.value)
      ElMessage.success(successMessage)
      closeDialog()
      onSuccess?.()
    } catch (error: unknown) {
      const msg = error instanceof Error ? error.message : '操作失败'
      ElMessage.error(msg)
    } finally {
      submitting.value = false
    }
  }

  return {
    dialogVisible,
    isEdit,
    editingId,
    formData,
    submitting,
    openCreate,
    openEdit,
    closeDialog,
    handleSubmit,
  }
}
