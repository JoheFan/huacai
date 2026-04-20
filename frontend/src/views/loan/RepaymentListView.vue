<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  createRepayment,
  fetchLoanOrderPage,
  fetchRepaymentPage,
  fetchRepaymentSummary,
  updateRepayment,
  type LoanOrderRecord,
  type LoanRepaymentQuery,
  type LoanRepaymentRecord,
  type LoanRepaymentSavePayload,
  type LoanRepaymentSummary,
} from '../../api/loan'
import { buildRepaymentScopeState } from './repaymentScopeModel'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const submitting = ref(false)
const drawerVisible = ref(false)
const editingId = ref<number | null>(null)
const rows = ref<LoanRepaymentRecord[]>([])
const total = ref(0)
const loanOrderOptions = ref<LoanOrderRecord[]>([])
const summary = ref<LoanRepaymentSummary | null>(null)

const filters = reactive<LoanRepaymentQuery>({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  capitalSourceType: '',
  customerId: undefined,
  loanOrderId: undefined,
})

const scope = reactive({
  customerId: undefined as number | undefined,
  customerName: '',
  loanOrderId: undefined as number | undefined,
  capitalSourceType: '',
})

const form = reactive<LoanRepaymentSavePayload>({
  loanOrderId: null,
  repaymentDate: '',
  repaymentAmount: null,
  principalAmount: null,
  interestAmount: null,
  repaymentChannel: '',
  remark: '',
})

const drawerTitle = computed(() => (editingId.value ? '编辑还款记录' : '新增还款记录'))
const scopeState = computed(() =>
  buildRepaymentScopeState({
    customerId: scope.customerId,
    customerName: scope.customerName,
    loanOrderId: scope.loanOrderId,
    capitalSourceType: scope.capitalSourceType,
  }),
)

const resetForm = () => {
  editingId.value = null
  form.loanOrderId = scope.loanOrderId ?? null
  form.repaymentDate = ''
  form.repaymentAmount = null
  form.principalAmount = null
  form.interestAmount = null
  form.repaymentChannel = ''
  form.remark = ''
}

const fillForm = (row: LoanRepaymentRecord) => {
  form.loanOrderId = row.loanOrderId
  form.repaymentDate = row.repaymentDate
  form.repaymentAmount = row.repaymentAmount
  form.principalAmount = row.principalAmount
  form.interestAmount = row.interestAmount
  form.repaymentChannel = row.repaymentChannel
  form.remark = row.remark
}

const parsePositiveNumber = (value: unknown) => {
  if (typeof value !== 'string' || !value.trim()) {
    return undefined
  }
  const parsed = Number(value)
  return Number.isFinite(parsed) && parsed > 0 ? parsed : undefined
}

const applyRouteScope = () => {
  scope.customerId = parsePositiveNumber(route.query.customerId)
  scope.customerName = typeof route.query.customerName === 'string' ? route.query.customerName : ''
  scope.loanOrderId = parsePositiveNumber(route.query.loanOrderId)
  scope.capitalSourceType = typeof route.query.capitalSourceType === 'string' ? route.query.capitalSourceType : ''

  filters.customerId = scope.customerId
  filters.loanOrderId = scope.loanOrderId
  filters.capitalSourceType = scope.capitalSourceType
}

const formatAmount = (value: number | null | undefined) => {
  if (value == null) {
    return '-'
  }
  return `¥${value.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`
}

const formatCapitalSource = (value: string | null | undefined) => {
  if (value === 'BANK') {
    return '银行'
  }
  if (value === 'SELF') {
    return '我方'
  }
  return value || '-'
}

const fetchLoanOrders = async () => {
  try {
    const data = await fetchLoanOrderPage({
      pageNum: 1,
      pageSize: 200,
      keyword: '',
      customerId: scope.customerId,
      capitalSourceType: scope.capitalSourceType || undefined,
    })
    loanOrderOptions.value = data.records
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '借贷单选项加载失败')
  }
}

const fetchList = async () => {
  loading.value = true
  try {
    const data = await fetchRepaymentPage(filters)
    rows.value = data.records
    total.value = data.total
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '还款列表加载失败')
  } finally {
    loading.value = false
  }
}

const fetchSummaryData = async () => {
  if (!scopeState.value.isScoped) {
    summary.value = null
    return
  }
  try {
    summary.value = await fetchRepaymentSummary(filters)
  } catch (error) {
    summary.value = null
    ElMessage.error(error instanceof Error ? error.message : '还款汇总加载失败')
  }
}

const reloadPageData = async () => {
  await Promise.all([fetchLoanOrders(), fetchList(), fetchSummaryData()])
}

const handleSearch = async () => {
  filters.pageNum = 1
  await Promise.all([fetchList(), fetchSummaryData()])
}

const handleReset = async () => {
  filters.keyword = ''
  filters.pageNum = 1
  applyRouteScope()
  await Promise.all([fetchList(), fetchSummaryData(), fetchLoanOrders()])
}

const openCreate = () => {
  resetForm()
  drawerVisible.value = true
}

const openEdit = (row: LoanRepaymentRecord) => {
  resetForm()
  editingId.value = row.id
  fillForm(row)
  drawerVisible.value = true
}

const handleSubmit = async () => {
  if (!form.loanOrderId) {
    ElMessage.warning('请选择借贷单')
    return
  }
  if (!form.repaymentDate) {
    ElMessage.warning('请选择还款日期')
    return
  }

  submitting.value = true
  try {
    if (editingId.value) {
      await updateRepayment(editingId.value, form)
      ElMessage.success('还款记录已更新')
    } else {
      await createRepayment(form)
      ElMessage.success('还款记录已创建')
    }
    drawerVisible.value = false
    await Promise.all([fetchList(), fetchSummaryData()])
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '还款记录保存失败')
  } finally {
    submitting.value = false
  }
}

const handlePageChange = async (page: number) => {
  filters.pageNum = page
  await fetchList()
}

const exitScopedView = () => {
  router.push({ path: '/repayments' })
}

watch(
  () => route.fullPath,
  async () => {
    applyRouteScope()
    resetForm()
    await reloadPageData()
  },
)

onMounted(async () => {
  applyRouteScope()
  resetForm()
  await reloadPageData()
})
</script>

<template>
  <section class="page-shell">
    <section class="card">
      <div class="card__section list-toolbar">
        <div class="list-toolbar__filters">
          <el-input v-model="filters.keyword" placeholder="客户名称 / 客户编号 / 还款途径" clearable />
          <el-select v-model="filters.capitalSourceType" placeholder="资金来源" :disabled="scopeState.isScoped" clearable>
            <el-option label="我方资金" value="SELF" />
            <el-option label="银行资金" value="BANK" />
          </el-select>
        </div>
        <div class="list-toolbar__actions">
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button plain @click="handleReset">重置</el-button>
          <el-button v-if="scopeState.isScoped" plain @click="exitScopedView">返回全局列表</el-button>
          <el-button type="primary" plain @click="openCreate">登记还款</el-button>
        </div>
      </div>
    </section>

    <section class="card">
      <div class="card__section">
        <div class="section-caption">
          {{ scopeState.title }}
        </div>

        <div v-if="scopeState.isScoped && summary" class="summary-panel">
          <div class="summary-panel__item">
            <span class="summary-panel__label">{{ scopeState.summaryLabel }}</span>
            <strong>{{ summary.recordCount }} 条</strong>
          </div>
          <div class="summary-panel__item">
            <span class="summary-panel__label">还款总额</span>
            <strong>{{ formatAmount(summary.repaymentAmountTotal) }}</strong>
          </div>
          <div class="summary-panel__item">
            <span class="summary-panel__label">本金合计</span>
            <strong>{{ formatAmount(summary.principalAmountTotal) }}</strong>
          </div>
          <div class="summary-panel__item">
            <span class="summary-panel__label">利息合计</span>
            <strong>{{ formatAmount(summary.interestAmountTotal) }}</strong>
          </div>
        </div>

        <el-table v-loading="loading" :data="rows" table-layout="fixed">
          <el-table-column label="序号" width="80">
            <template #default="{ $index }">
              {{ (filters.pageNum - 1) * filters.pageSize + $index + 1 }}
            </template>
          </el-table-column>
          <el-table-column v-if="!scopeState.isScoped" prop="customerName" label="客户名称" min-width="150" />
          <el-table-column v-if="!scopeState.isScoped" prop="loanOrderId" label="借贷单ID" min-width="110" />
          <el-table-column v-if="!scopeState.isScoped" prop="capitalSourceType" label="资金来源" min-width="100">
            <template #default="{ row }">
              {{ formatCapitalSource(row.capitalSourceType) }}
            </template>
          </el-table-column>
          <el-table-column prop="repaymentDate" label="还款日期" min-width="120" />
          <el-table-column prop="repaymentAmount" label="还款金额" min-width="120">
            <template #default="{ row }">
              {{ formatAmount(row.repaymentAmount) }}
            </template>
          </el-table-column>
          <el-table-column prop="principalAmount" label="本金" min-width="110">
            <template #default="{ row }">
              {{ formatAmount(row.principalAmount) }}
            </template>
          </el-table-column>
          <el-table-column prop="interestAmount" label="每月利息" min-width="110">
            <template #default="{ row }">
              {{ formatAmount(row.interestAmount) }}
            </template>
          </el-table-column>
          <el-table-column prop="repaymentChannel" label="还款途径" min-width="140" show-overflow-tooltip>
            <template #default="{ row }">
              {{ row.repaymentChannel || '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" min-width="180" show-overflow-tooltip>
            <template #default="{ row }">
              {{ row.remark || '-' }}
            </template>
          </el-table-column>
          <el-table-column v-if="!scopeState.isScoped" prop="updatedAt" label="更新时间" min-width="170" />
          <el-table-column label="操作" width="110" fixed="right">
            <template #default="{ row }">
              <el-button text type="primary" @click="openEdit(row)">编辑</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="list-pagination">
          <el-pagination
            layout="prev, pager, next"
            :total="total"
            :page-size="filters.pageSize"
            :current-page="filters.pageNum"
            @current-change="handlePageChange"
          />
        </div>
      </div>
    </section>

    <el-drawer v-model="drawerVisible" :title="drawerTitle" size="520px">
      <div class="drawer-shell">
        <el-form label-position="top">
          <div class="form-grid">
            <el-form-item label="借贷单" required class="form-grid__full">
              <el-select v-model="form.loanOrderId" filterable placeholder="请选择借贷单">
                <el-option
                  v-for="item in loanOrderOptions"
                  :key="item.id"
                  :label="`#${item.id} / ${item.customerName} / ${formatCapitalSource(item.capitalSourceType)}`"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="还款日期" required>
              <el-date-picker
                v-model="form.repaymentDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择日期"
              />
            </el-form-item>
            <el-form-item label="还款金额">
              <el-input-number v-model="form.repaymentAmount" :min="0" :precision="2" controls-position="right" />
            </el-form-item>
            <el-form-item label="本金">
              <el-input-number v-model="form.principalAmount" :min="0" :precision="2" controls-position="right" />
            </el-form-item>
            <el-form-item label="每月利息">
              <el-input-number v-model="form.interestAmount" :min="0" :precision="2" controls-position="right" />
            </el-form-item>
            <el-form-item label="还款途径" class="form-grid__full">
              <el-input v-model="form.repaymentChannel" placeholder="请输入还款途径" />
            </el-form-item>
            <el-form-item label="备注" class="form-grid__full">
              <el-input v-model="form.remark" type="textarea" :rows="4" placeholder="请输入备注" />
            </el-form-item>
          </div>
        </el-form>

        <div class="drawer-actions">
          <el-button plain @click="drawerVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">提交</el-button>
        </div>
      </div>
    </el-drawer>
  </section>
</template>

<style scoped>
.list-toolbar {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
}

.list-toolbar__filters,
.list-toolbar__actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.list-toolbar__filters {
  flex: 1 1 540px;
}

.list-toolbar__filters :deep(.el-input),
.list-toolbar__filters :deep(.el-select) {
  width: 220px;
}

.section-caption {
  margin-bottom: 12px;
  color: var(--hc-text-soft);
  font-size: 13px;
}

.summary-panel {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
  margin-bottom: 16px;
}

.summary-panel__item {
  padding: 12px 14px;
  border-radius: 12px;
  background: var(--hc-surface-secondary);
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.summary-panel__label {
  color: var(--hc-text-soft);
  font-size: 13px;
}

.list-pagination {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-top: 18px;
}

.drawer-shell {
  display: flex;
  height: 100%;
  flex-direction: column;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.form-grid__full {
  grid-column: 1 / -1;
}

.drawer-actions {
  margin-top: auto;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 12px;
}

@media (max-width: 960px) {
  .list-pagination,
  .list-toolbar {
    flex-direction: column;
    align-items: stretch;
  }

  .summary-panel {
    grid-template-columns: 1fr;
  }

  .list-toolbar__filters :deep(.el-input),
  .list-toolbar__filters :deep(.el-select) {
    width: 100%;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }

  .drawer-actions {
    display: grid;
    grid-template-columns: 1fr 1fr;
  }

  .drawer-actions :deep(.el-button) {
    width: 100%;
  }
}
</style>
