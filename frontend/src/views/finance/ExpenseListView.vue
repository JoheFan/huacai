<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh } from '@element-plus/icons-vue'
import { expenseApi, payeeApi, type ExpenseVO, type PayeeVO } from '../../api/finance'

const loading = ref(false)
const rows = ref<ExpenseVO[]>([])
const total = ref(0)

const filters = reactive({
  keyword: '',
  expenseType: '',
  pageNum: 1,
  pageSize: 20,
})

const drawerVisible = ref(false)
const drawerTitle = ref('新增支出')
const isEdit = ref(false)
const editId = ref<number | null>(null)
const saving = ref(false)

const payees = ref<PayeeVO[]>([])
const attachedFileId = ref<number | null>(null)

const form = reactive({
  expenseName: '',
  expenseType: '',
  bizDate: '',
  amount: undefined as number | undefined,
  payeeId: null as number | null,
  payeeName: '',
  remark: '',
})

const fetchList = async () => {
  loading.value = true
  try {
    const data = await expenseApi.page({
      keyword: filters.keyword || undefined,
      expenseType: filters.expenseType || undefined,
      pageNum: filters.pageNum,
      pageSize: filters.pageSize,
    })
    rows.value = data.records
    total.value = data.total
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '加载失败')
  } finally {
    loading.value = false
  }
}

const fetchPayees = async () => {
  try {
    const data = await payeeApi.page({ pageSize: 500 })
    payees.value = data.records.filter(p => p.status === 'ENABLE')
  } catch (error) {
    // ignore
  }
}

const handleSearch = async () => {
  filters.pageNum = 1
  await fetchList()
}

const handleReset = async () => {
  filters.keyword = ''
  filters.expenseType = ''
  filters.pageNum = 1
  await fetchList()
}

const handlePageChange = async (page: number) => {
  filters.pageNum = page
  await fetchList()
}

const handleSizeChange = async (size: number) => {
  filters.pageSize = size
  filters.pageNum = 1
  await fetchList()
}

const openCreate = async () => {
  isEdit.value = false
  editId.value = null
  drawerTitle.value = '新增支出'
  Object.assign(form, {
    expenseName: '',
    expenseType: '',
    bizDate: '',
    amount: undefined,
    payeeId: null,
    payeeName: '',
    remark: '',
  })
  attachedFileId.value = null
  await fetchPayees()
  drawerVisible.value = true
}

const openEdit = async (row: ExpenseVO) => {
  isEdit.value = true
  editId.value = row.id
  drawerTitle.value = '编辑支出'
  Object.assign(form, {
    expenseName: row.expenseName || '',
    expenseType: row.expenseType || '',
    bizDate: row.bizDate || '',
    amount: row.amount != null ? Number(row.amount) : undefined,
    payeeId: row.payeeId || null,
    payeeName: row.payeeName || '',
    remark: row.remark || '',
  })
  attachedFileId.value = row.fileId || null
  await fetchPayees()
  drawerVisible.value = true
}

const handlePayeeChange = (payeeId: number) => {
  const payee = payees.value.find(p => p.id === payeeId)
  if (payee) {
    form.payeeName = payee.payeeName
  }
}

const handleSave = async () => {
  if (!form.expenseName?.trim()) {
    ElMessage.error('支出名称不能为空')
    return
  }
  if (!form.expenseType?.trim()) {
    ElMessage.error('支出类型不能为空')
    return
  }
  if (!form.bizDate?.trim()) {
    ElMessage.error('日期不能为空')
    return
  }
  if (!form.amount || form.amount <= 0) {
    ElMessage.error('金额必须大于0')
    return
  }
  if (!form.payeeId) {
    ElMessage.error('收款方不能为空')
    return
  }
  saving.value = true
  try {
    const payload = {
      expenseName: form.expenseName,
      expenseType: form.expenseType || undefined,
      bizDate: form.bizDate || undefined,
      amount: form.amount,
      payeeId: form.payeeId || undefined,
      payeeName: form.payeeName || undefined,
      fileId: attachedFileId.value || undefined,
      remark: form.remark || undefined,
    }
    if (isEdit.value && editId.value) {
      await expenseApi.update(editId.value, payload)
      ElMessage.success('更新成功')
    } else {
      await expenseApi.create(payload)
      ElMessage.success('创建成功')
    }
    drawerVisible.value = false
    await fetchList()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '保存失败')
  } finally {
    saving.value = false
  }
}

const handleDelete = async (row: ExpenseVO) => {
  try {
    await ElMessageBox.confirm(`确定要删除支出记录"${row.expenseName}"吗？`, '删除确认', { type: 'warning' })
    await expenseApi.delete(row.id)
    ElMessage.success('删除成功')
    await fetchList()
  } catch (error) {
    if (error instanceof Error && error.message !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

const formatDate = (dateStr: string | undefined | null) => {
  if (!dateStr) return '-'
  return String(dateStr).split('T')[0]
}

const formatAmount = (amount: any) => {
  if (amount == null) return '-'
  return Number(amount).toLocaleString('zh-CN', { minimumFractionDigits: 2 })
}

onMounted(() => {
  void fetchList()
})
</script>

<template>
  <section class="page-shell">
    <section class="card">
      <div class="card__section page-intro">
        <div class="page-intro__copy">
          <span class="page-intro__eyebrow">财务管理</span>
          <h2 class="page-intro__title">支出管理</h2>
          <p class="page-intro__desc">统一维护日常支出记录，支持按类型和关键字筛选，便于核对金额、收款方和业务日期。</p>
        </div>

        <div class="page-intro__actions">
          <el-button type="primary" :icon="Plus" @click="openCreate">新增支出</el-button>
        </div>
      </div>
    </section>

    <section class="card">
      <div class="card__section list-toolbar">
        <div class="list-toolbar__filters">
          <el-input
            v-model="filters.keyword"
            placeholder="支出名称"
            clearable
            @keyup.enter="handleSearch"
          />
          <el-select v-model="filters.expenseType" placeholder="支出类型" clearable>
            <el-option label="办公用品" value="OFFICE" />
            <el-option label="差旅费" value="TRAVEL" />
            <el-option label="招待费" value="ENTERTAIN" />
            <el-option label="交通费" value="TRANSPORT" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </div>
        <div class="list-toolbar__actions">
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button plain @click="handleReset">重置</el-button>
          <el-button plain :icon="Refresh" @click="fetchList">刷新</el-button>
        </div>
      </div>
    </section>

    <section class="card">
      <div class="card__section">
        <div class="table-wrap">
      <el-table v-loading="loading" :data="rows" stripe border>
        <el-table-column type="index" width="60" label="序号" />
        <el-table-column prop="expenseName" label="支出名称" min-width="140" />
        <el-table-column prop="expenseType" label="支出类型" width="100">
          <template #default="{ row }">
            {{
              row.expenseType === 'OFFICE' ? '办公用品' :
              row.expenseType === 'TRAVEL' ? '差旅费' :
              row.expenseType === 'ENTERTAIN' ? '招待费' :
              row.expenseType === 'TRANSPORT' ? '交通费' :
              row.expenseType === 'OTHER' ? '其他' : '-'
            }}
          </template>
        </el-table-column>
        <el-table-column prop="bizDate" label="日期" width="110">
          <template #default="{ row }">{{ formatDate(row.bizDate) }}</template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" width="120" align="right">
          <template #default="{ row }">¥{{ formatAmount(row.amount) }}</template>
        </el-table-column>
        <el-table-column prop="payeeName" label="收款方" min-width="140" />
        <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
        </div>

      <el-pagination
        v-model:current-page="filters.pageNum"
        v-model:page-size="filters.pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
      </div>
    </section>

    <el-drawer v-model="drawerVisible" :title="drawerTitle" size="500px" @close="drawerVisible = false">
      <el-form :model="form" label-width="100px">
        <el-form-item label="支出名称" required>
          <el-input v-model="form.expenseName" placeholder="请输入支出名称" />
        </el-form-item>
        <el-form-item label="支出类型">
          <el-select v-model="form.expenseType" placeholder="请选择" style="width: 100%">
            <el-option label="办公用品" value="OFFICE" />
            <el-option label="差旅费" value="TRAVEL" />
            <el-option label="招待费" value="ENTERTAIN" />
            <el-option label="交通费" value="TRANSPORT" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期">
          <el-date-picker v-model="form.bizDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="金额" required>
          <el-input-number v-model="form.amount" :min="0" :precision="2" style="width: 100%" placeholder="0.00" />
        </el-form-item>
        <el-form-item label="收款方">
          <el-select v-model="form.payeeId" placeholder="请选择收款方" style="width: 100%" clearable @change="handlePayeeChange">
            <el-option v-for="p in payees" :key="p.id" :label="p.payeeName" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="drawerVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-drawer>
  </section>
</template>

<style scoped>
</style>
