<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh } from '@element-plus/icons-vue'
import { incomeApi, type IncomeVO } from '../../api/finance'

const loading = ref(false)
const rows = ref<IncomeVO[]>([])
const total = ref(0)

const filters = reactive({
  keyword: '',
  incomeType: '',
  pageNum: 1,
  pageSize: 20,
})

const drawerVisible = ref(false)
const drawerTitle = ref('新增收入')
const isEdit = ref(false)
const editId = ref<number | null>(null)
const saving = ref(false)

const form = reactive({
  incomeName: '',
  incomeType: '',
  bizDate: '',
  amount: undefined as number | undefined,
  payerName: '',
  remark: '',
})

const fetchList = async () => {
  loading.value = true
  try {
    const data = await incomeApi.page({
      keyword: filters.keyword || undefined,
      incomeType: filters.incomeType || undefined,
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

const handleSearch = async () => {
  filters.pageNum = 1
  await fetchList()
}

const handleReset = async () => {
  filters.keyword = ''
  filters.incomeType = ''
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

const openCreate = () => {
  isEdit.value = false
  editId.value = null
  drawerTitle.value = '新增收入'
  Object.assign(form, {
    incomeName: '',
    incomeType: '',
    bizDate: '',
    amount: undefined,
    payerName: '',
    remark: '',
  })
  drawerVisible.value = true
}

const openEdit = (row: IncomeVO) => {
  isEdit.value = true
  editId.value = row.id
  drawerTitle.value = '编辑收入'
  Object.assign(form, {
    incomeName: row.incomeName || '',
    incomeType: row.incomeType || '',
    bizDate: row.bizDate || '',
    amount: row.amount != null ? Number(row.amount) : undefined,
    payerName: row.payerName || '',
    remark: row.remark || '',
  })
  drawerVisible.value = true
}

const handleSave = async () => {
  if (!form.incomeName?.trim()) {
    ElMessage.error('收入名称不能为空')
    return
  }
  if (!form.incomeType?.trim()) {
    ElMessage.error('收入类型不能为空')
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
  saving.value = true
  try {
    const payload = {
      incomeName: form.incomeName,
      incomeType: form.incomeType || undefined,
      bizDate: form.bizDate || undefined,
      amount: form.amount,
      payerName: form.payerName || undefined,
      remark: form.remark || undefined,
    }
    if (isEdit.value && editId.value) {
      await incomeApi.update(editId.value, payload)
      ElMessage.success('更新成功')
    } else {
      await incomeApi.create(payload)
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

const handleDelete = async (row: IncomeVO) => {
  try {
    await ElMessageBox.confirm(`确定要删除收入记录"${row.incomeName}"吗？`, '删除确认', { type: 'warning' })
    await incomeApi.delete(row.id)
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
          <h2 class="page-intro__title">收入管理</h2>
          <p class="page-intro__desc">统一登记公司收入记录，支持按收入类型和关键字筛选，便于核对金额、转入方和业务日期。</p>
        </div>

        <div class="page-intro__actions">
          <el-button type="primary" :icon="Plus" @click="openCreate">新增收入</el-button>
        </div>
      </div>
    </section>

    <section class="card">
      <div class="card__section list-toolbar">
        <div class="list-toolbar__filters">
          <el-input
            v-model="filters.keyword"
            placeholder="收入名称"
            clearable
            @keyup.enter="handleSearch"
          />
          <el-select v-model="filters.incomeType" placeholder="收入类型" clearable>
            <el-option label="服务费" value="SERVICE" />
            <el-option label="咨询费" value="CONSULT" />
            <el-option label="代理费" value="AGENCY" />
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
        <el-table-column prop="incomeName" label="收入名称" min-width="140" />
        <el-table-column prop="incomeType" label="收入类型" width="100">
          <template #default="{ row }">
            {{
              row.incomeType === 'SERVICE' ? '服务费' :
              row.incomeType === 'CONSULT' ? '咨询费' :
              row.incomeType === 'AGENCY' ? '代理费' :
              row.incomeType === 'OTHER' ? '其他' : '-'
            }}
          </template>
        </el-table-column>
        <el-table-column prop="bizDate" label="日期" width="110">
          <template #default="{ row }">{{ formatDate(row.bizDate) }}</template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" width="120" align="right">
          <template #default="{ row }">¥{{ formatAmount(row.amount) }}</template>
        </el-table-column>
        <el-table-column prop="payerName" label="转入方" min-width="140" />
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
        <el-form-item label="收入名称" required>
          <el-input v-model="form.incomeName" placeholder="请输入收入名称" />
        </el-form-item>
        <el-form-item label="收入类型">
          <el-select v-model="form.incomeType" placeholder="请选择" style="width: 100%">
            <el-option label="服务费" value="SERVICE" />
            <el-option label="咨询费" value="CONSULT" />
            <el-option label="代理费" value="AGENCY" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期">
          <el-date-picker v-model="form.bizDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="金额" required>
          <el-input-number v-model="form.amount" :min="0" :precision="2" style="width: 100%" placeholder="0.00" />
        </el-form-item>
        <el-form-item label="转入方">
          <el-input v-model="form.payerName" placeholder="请输入转入方" />
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
