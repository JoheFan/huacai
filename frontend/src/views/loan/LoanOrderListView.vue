<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { fetchCustomerPage, type CustomerRecord } from '../../api/customer'
import {
  createLoanOrder,
  fetchLoanOrderOverviewPage,
  fetchLoanOrderPage,
  updateLoanOrder,
  type LoanOrderOverviewRecord,
  type LoanOrderQuery,
  type LoanOrderRecord,
  type LoanOrderSavePayload,
} from '../../api/loan'
import { getLoanOrderFormMode } from './loanOrderFormMode'
import { getLoanOverviewMode } from './loanOverviewMode'

interface LoanDetailScope {
  customerId: number | null
  customerName: string
  customerNo: string
  capitalSourceType: string
}

const router = useRouter()

const loading = ref(false)
const submitting = ref(false)
const drawerVisible = ref(false)
const detailDrawerVisible = ref(false)
const detailLoading = ref(false)
const editingId = ref<number | null>(null)
const rows = ref<LoanOrderOverviewRecord[]>([])
const total = ref(0)
const customerOptions = ref<CustomerRecord[]>([])
const detailRows = ref<LoanOrderRecord[]>([])

const filters = reactive<LoanOrderQuery>({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  capitalSourceType: 'SELF',
  status: '',
})

const form = reactive<LoanOrderSavePayload>({
  customerId: null,
  capitalSourceType: 'SELF',
  bankName: '',
  loanDate: '',
  depositGoldAmount: null,
  creditCardRepaymentAmount: null,
  loanAmount: null,
  balanceAmount: null,
  monthlyInterestAmount: null,
  loanCount: null,
  status: 'ACTIVE',
  remark: '',
})

const detailScope = reactive<LoanDetailScope>({
  customerId: null,
  customerName: '',
  customerNo: '',
  capitalSourceType: 'SELF',
})

const drawerTitle = computed(() => (editingId.value ? '编辑借贷单' : '新增借贷单'))
const formMode = computed(() => getLoanOrderFormMode(editingId.value))
const overviewMode = computed(() => getLoanOverviewMode(filters.capitalSourceType || 'SELF'))
const detailScopeLabel = computed(() => getLoanOverviewMode(detailScope.capitalSourceType).scopeLabel)
const detailDrawerTitle = computed(() => {
  const customerText = detailScope.customerName || (detailScope.customerId ? `客户 #${detailScope.customerId}` : '借贷单')
  return `${customerText} ${detailScopeLabel.value ? `借贷单明细（${detailScopeLabel.value}）` : '借贷单明细'}`
})

const resetForm = () => {
  editingId.value = null
  form.customerId = null
  form.capitalSourceType = filters.capitalSourceType || 'SELF'
  form.bankName = ''
  form.loanDate = ''
  form.depositGoldAmount = null
  form.creditCardRepaymentAmount = null
  form.loanAmount = null
  form.balanceAmount = null
  form.monthlyInterestAmount = null
  form.loanCount = null
  form.status = 'ACTIVE'
  form.remark = ''
}

const fillForm = (row: LoanOrderRecord) => {
  form.customerId = row.customerId
  form.capitalSourceType = row.capitalSourceType
  form.bankName = row.bankName
  form.loanDate = row.loanDate
  form.depositGoldAmount = row.depositGoldAmount
  form.creditCardRepaymentAmount = row.creditCardRepaymentAmount
  form.loanAmount = row.loanAmount
  form.balanceAmount = row.balanceAmount
  form.monthlyInterestAmount = row.monthlyInterestAmount
  form.loanCount = row.loanCount
  form.status = row.status
  form.remark = row.remark
}

const formatAmount = (value: number | null | undefined) => {
  if (value == null) {
    return '-'
  }
  return `¥${value.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`
}

const formatDate = (value: string | null | undefined) => value || '-'

const formatCapitalSource = (value: string | null | undefined) => {
  if (value === 'BANK') {
    return '银行'
  }
  if (value === 'SELF') {
    return '我方'
  }
  return value || '-'
}

const formatStatus = (value: string | null | undefined) => {
  if (value === 'SETTLED') {
    return '已结清'
  }
  if (value === 'ACTIVE') {
    return '运行中'
  }
  return value || '-'
}

const getColumnMinWidth = (columnKey: string) => {
  const widthMap: Record<string, number> = {
    customerNo: 130,
    customerName: 120,
    mobile: 130,
    companyName: 180,
    depositGoldAmount: 150,
    creditCardRepaymentAmount: 150,
    balanceAmount: 140,
    loanCount: 90,
    monthlyInterestAmount: 130,
    totalLoanAmount: 150,
    firstLoanDate: 140,
    repaidAmount: 140,
    pendingAmount: 140,
    remark: 180,
  }
  return widthMap[columnKey] ?? 140
}

const formatOverviewCell = (row: LoanOrderOverviewRecord, columnKey: string) => {
  switch (columnKey) {
    case 'customerNo':
      return row.customerNo || '-'
    case 'customerName':
      return row.customerName || '-'
    case 'mobile':
      return row.mobile || '-'
    case 'companyName':
      return row.companyName || '-'
    case 'depositGoldAmount':
      return formatAmount(row.depositGoldAmount)
    case 'creditCardRepaymentAmount':
      return formatAmount(row.creditCardRepaymentAmount)
    case 'balanceAmount':
      return formatAmount(row.balanceAmount)
    case 'loanCount':
      return row.loanCount ?? '-'
    case 'monthlyInterestAmount':
      return formatAmount(row.monthlyInterestAmount)
    case 'totalLoanAmount':
      return formatAmount(row.totalLoanAmount)
    case 'firstLoanDate':
      return formatDate(row.firstLoanDate)
    case 'repaidAmount':
      return formatAmount(row.repaidAmount)
    case 'pendingAmount':
      return formatAmount(row.pendingAmount)
    case 'remark':
      return row.remark || '-'
    default:
      return '-'
  }
}

const fetchCustomers = async () => {
  try {
    const data = await fetchCustomerPage({ pageNum: 1, pageSize: 200, keyword: '' })
    customerOptions.value = data.records
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '客户选项加载失败')
  }
}

const fetchList = async () => {
  loading.value = true
  try {
    const data = await fetchLoanOrderOverviewPage(filters)
    rows.value = data.records
    total.value = data.total
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '借贷汇总加载失败')
  } finally {
    loading.value = false
  }
}

const fetchDetailRows = async () => {
  if (!detailScope.customerId) {
    detailRows.value = []
    return
  }
  detailLoading.value = true
  try {
    const data = await fetchLoanOrderPage({
      pageNum: 1,
      pageSize: 100,
      keyword: '',
      customerId: detailScope.customerId,
      capitalSourceType: detailScope.capitalSourceType,
    })
    detailRows.value = data.records
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '借贷单明细加载失败')
  } finally {
    detailLoading.value = false
  }
}

const handleSearch = async () => {
  filters.pageNum = 1
  await fetchList()
}

const handleReset = async () => {
  filters.keyword = ''
  filters.capitalSourceType = 'SELF'
  filters.status = ''
  filters.pageNum = 1
  await fetchList()
}

const openCreate = (scope?: Partial<LoanDetailScope>) => {
  resetForm()
  form.customerId = scope?.customerId ?? null
  form.capitalSourceType = scope?.capitalSourceType || filters.capitalSourceType || 'SELF'
  drawerVisible.value = true
}

const openEdit = (row: LoanOrderRecord) => {
  resetForm()
  editingId.value = row.id
  fillForm(row)
  drawerVisible.value = true
}

const openOrderDetail = async (row: LoanOrderOverviewRecord) => {
  detailScope.customerId = row.customerId
  detailScope.customerName = row.customerName
  detailScope.customerNo = row.customerNo
  detailScope.capitalSourceType = row.capitalSourceType
  detailDrawerVisible.value = true
  await fetchDetailRows()
}

const openRepaymentDetail = (scope: { customerId: number; customerName: string; capitalSourceType: string }) => {
  router.push({
    path: '/repayments',
    query: {
      customerId: String(scope.customerId),
      customerName: scope.customerName,
      capitalSourceType: scope.capitalSourceType,
    },
  })
}

const handleSubmit = async () => {
  if (!form.customerId) {
    ElMessage.warning('请选择客户')
    return
  }

  submitting.value = true
  try {
    if (editingId.value) {
      await updateLoanOrder(editingId.value, form)
      ElMessage.success('借贷单已更新')
    } else {
      await createLoanOrder(form)
      ElMessage.success('借贷单已创建')
    }
    drawerVisible.value = false
    await fetchList()
    if (detailDrawerVisible.value && detailScope.customerId === form.customerId && detailScope.capitalSourceType === form.capitalSourceType) {
      await fetchDetailRows()
    }
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '借贷单保存失败')
  } finally {
    submitting.value = false
  }
}

const handlePageChange = async (page: number) => {
  filters.pageNum = page
  await fetchList()
}

onMounted(async () => {
  await Promise.all([fetchCustomers(), fetchList()])
})
</script>

<template>
  <section class="page-shell">
    <section class="card">
      <div class="card__section page-intro">
        <div class="page-intro__copy">
          <span class="page-intro__eyebrow">借贷管理</span>
          <h2 class="page-intro__title">借贷单汇总</h2>
          <p class="page-intro__desc">按客户维度汇总借贷情况，支持按资金来源和状态筛选。点击「借贷单明细」展开单笔记录，点击「还款」跳转对应还款页。</p>
        </div>
        <div class="page-intro__actions">
          <el-button type="primary" plain @click="openCreate()">登记借贷</el-button>
        </div>
      </div>
    </section>

    <section class="card">
      <div class="card__section list-toolbar">
        <div class="list-toolbar__filters">
          <el-input v-model="filters.keyword" placeholder="客户ID / 客户名称 / 联系电话 / 企业名称" clearable />
          <el-select v-model="filters.capitalSourceType" placeholder="资金来源">
            <el-option label="我方资金" value="SELF" />
            <el-option label="银行资金" value="BANK" />
          </el-select>
          <el-select v-model="filters.status" placeholder="借贷状态" clearable>
            <el-option label="运行中" value="ACTIVE" />
            <el-option label="已结清" value="SETTLED" />
          </el-select>
        </div>
        <div class="list-toolbar__actions">
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button plain @click="handleReset">重置</el-button>
          <el-button type="primary" plain @click="openCreate()">登记借贷</el-button>
        </div>
      </div>
    </section>

    <section class="card">
      <div class="card__section">
        <div class="section-caption">
          当前口径：{{ overviewMode.scopeLabel }}资金客户汇总
        </div>

        <div class="table-wrap">
          <el-table v-loading="loading" :data="rows" table-layout="fixed">
          <el-table-column
            v-for="column in overviewMode.columns"
            :key="column.key"
            :label="column.label"
            :min-width="getColumnMinWidth(column.key)"
            show-overflow-tooltip
          >
            <template #default="{ row }">
              {{ formatOverviewCell(row, column.key) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="140" fixed="right">
            <template #default="{ row }">
              <el-button text type="primary" @click="openOrderDetail(row)">借贷单明细</el-button>
              <el-button text type="primary" @click="openRepaymentDetail({ customerId: row.customerId, customerName: row.customerName, capitalSourceType: row.capitalSourceType })">
                {{ overviewMode.actionLabel }}还款
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        </div>

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

    <el-drawer v-model="detailDrawerVisible" :title="detailDrawerTitle" size="780px">
      <div class="drawer-shell">
        <div class="detail-toolbar">
          <div class="detail-toolbar__meta">
            <span>客户ID：{{ detailScope.customerNo || '-' }}</span>
            <span>资金来源：{{ formatCapitalSource(detailScope.capitalSourceType) }}</span>
          </div>
          <div class="detail-toolbar__actions">
            <el-button plain @click="openCreate(detailScope)">新增该客户借贷</el-button>
            <el-button
              type="primary"
              plain
              @click="detailScope.customerId && openRepaymentDetail({ customerId: detailScope.customerId, customerName: detailScope.customerName, capitalSourceType: detailScope.capitalSourceType })"
            >
              查看该客户还款
            </el-button>
          </div>
        </div>

        <el-table v-loading="detailLoading" :data="detailRows" table-layout="fixed">
          <el-table-column prop="loanDate" label="借款日期" min-width="120">
            <template #default="{ row }">
              {{ formatDate(row.loanDate) }}
            </template>
          </el-table-column>
          <el-table-column prop="bankName" label="银行名称" min-width="140" show-overflow-tooltip>
            <template #default="{ row }">
              {{ row.bankName || '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="loanAmount" label="借款金额" min-width="130">
            <template #default="{ row }">
              {{ formatAmount(row.loanAmount) }}
            </template>
          </el-table-column>
          <el-table-column prop="balanceAmount" label="当前余额" min-width="130">
            <template #default="{ row }">
              {{ formatAmount(row.balanceAmount) }}
            </template>
          </el-table-column>
          <el-table-column prop="monthlyInterestAmount" label="每月利息" min-width="120">
            <template #default="{ row }">
              {{ formatAmount(row.monthlyInterestAmount) }}
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" min-width="100">
            <template #default="{ row }">
              {{ formatStatus(row.status) }}
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" min-width="160" show-overflow-tooltip>
            <template #default="{ row }">
              {{ row.remark || '-' }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button text type="primary" @click="openEdit(row)">编辑</el-button>
              <el-button
                text
                type="primary"
                @click="openRepaymentDetail({ customerId: row.customerId, customerName: detailScope.customerName, capitalSourceType: row.capitalSourceType })"
              >
                查看还款
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-drawer>

    <el-drawer v-model="drawerVisible" :title="drawerTitle" size="520px">
      <div class="drawer-shell">
        <el-form label-position="top">
          <div class="form-grid">
            <el-form-item label="客户" required class="form-grid__full">
              <el-select v-model="form.customerId" filterable placeholder="请选择客户">
                <el-option
                  v-for="item in customerOptions"
                  :key="item.id"
                  :label="`${item.customerName} / ${item.customerNo}`"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="资金来源">
              <el-select v-model="form.capitalSourceType">
                <el-option label="我方资金" value="SELF" />
                <el-option label="银行资金" value="BANK" />
              </el-select>
            </el-form-item>
            <el-form-item label="借款日期">
              <el-date-picker v-model="form.loanDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择日期" />
            </el-form-item>
            <el-form-item label="银行名称">
              <el-input v-model="form.bankName" placeholder="请输入银行名称" />
            </el-form-item>
            <div v-if="!formMode.showManagedFields" class="form-note form-grid__full">
              {{ formMode.createHint }}
            </div>
            <el-form-item v-if="formMode.showManagedFields" label="借贷状态">
              <el-select v-model="form.status">
                <el-option label="运行中" value="ACTIVE" />
                <el-option label="已结清" value="SETTLED" />
              </el-select>
            </el-form-item>
            <el-form-item label="借款金额">
              <el-input-number v-model="form.loanAmount" :min="0" :precision="2" controls-position="right" />
            </el-form-item>
            <el-form-item v-if="formMode.showManagedFields" label="当前余额">
              <el-input-number v-model="form.balanceAmount" :min="0" :precision="2" controls-position="right" />
            </el-form-item>
            <el-form-item label="月利息">
              <el-input-number v-model="form.monthlyInterestAmount" :min="0" :precision="2" controls-position="right" />
            </el-form-item>
            <el-form-item label="理财定存/黄金金额">
              <el-input-number v-model="form.depositGoldAmount" :min="0" :precision="2" controls-position="right" />
            </el-form-item>
            <el-form-item label="垫还金额">
              <el-input-number
                v-model="form.creditCardRepaymentAmount"
                :min="0"
                :precision="2"
                controls-position="right"
              />
            </el-form-item>
            <el-form-item label="贷款笔数">
              <el-input-number v-model="form.loanCount" :min="0" controls-position="right" />
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

.list-toolbar__filters {
  flex: 1 1 640px;
}

.list-toolbar__actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  align-items: center;
}

.list-toolbar__filters :deep(.el-input),
.list-toolbar__filters :deep(.el-select) {
  width: 160px;
}

.drawer-alert {
  margin-bottom: 16px;
}

:deep(.el-form-item__label) {
  font-weight: 700;
  color: var(--hc-text);
}

:deep(.el-table th .cell) {
  font-weight: 700;
  color: var(--hc-text);
}
</style>
