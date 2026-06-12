<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
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
import { fetchCustomerPage, type CustomerRecord } from '../../api/customer'

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
  capitalSourceType: 'BANK',
  status: '',
})

const detailScope = reactive({
  customerId: null as number | null,
  customerName: '',
  customerNo: '',
  capitalSourceType: 'BANK',
})

const form = reactive<LoanOrderSavePayload>({
  customerId: null,
  capitalSourceType: 'BANK',
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

const drawerTitle = computed(() => (editingId.value ? '编辑借贷单（银行）' : '新增借贷单（银行）'))

const resetForm = () => {
  editingId.value = null
  form.customerId = null
  form.capitalSourceType = 'BANK'
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
  if (value == null) return '-'
  return `¥${value.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`
}

const formatDate = (value: string | null | undefined) => value || '-'

const formatStatus = (value: string | null | undefined) => {
  if (value === 'SETTLED') return '已结清'
  if (value === 'ACTIVE') return '运行中'
  return value || '-'
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
  filters.status = ''
  filters.pageNum = 1
  await fetchList()
}

const openCreate = (scope?: Partial<{ customerId: number | null; customerName: string }>) => {
  resetForm()
  form.customerId = scope?.customerId ?? null
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
    if (detailDrawerVisible.value && detailScope.customerId === form.customerId) {
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

<script lang="ts">
export default { name: 'LoanOrderBankListView' }
</script>

<template>
  <section class="page-shell">
    <section class="card">
      <div class="card__section list-toolbar">
        <div class="list-toolbar__filters">
          <el-input v-model="filters.keyword" placeholder="客户ID / 客户名称 / 联系电话 / 企业名称" clearable />
          <el-select v-model="filters.status" placeholder="借贷状态" clearable style="width: 160px">
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
        <div class="section-caption">银行资金客户汇总</div>

        <el-table v-loading="loading" :data="rows" table-layout="fixed">
          <el-table-column prop="customerNo" label="客户ID" min-width="130" />
          <el-table-column prop="customerName" label="客户名称" min-width="120" />
          <el-table-column prop="mobile" label="联系电话" min-width="130" />
          <el-table-column prop="companyName" label="企业名称" min-width="180" show-overflow-tooltip />
          <el-table-column prop="totalLoanAmount" label="总贷款金额（银行）" min-width="160">
            <template #default="{ row }">
              {{ formatAmount(row.totalLoanAmount) }}
            </template>
          </el-table-column>
          <el-table-column prop="firstLoanDate" label="出款日期（银行）" min-width="150">
            <template #default="{ row }">
              {{ formatDate(row.firstLoanDate) }}
            </template>
          </el-table-column>
          <el-table-column prop="repaidAmount" label="已还款（银行）" min-width="140">
            <template #default="{ row }">
              {{ formatAmount(row.repaidAmount) }}
            </template>
          </el-table-column>
          <el-table-column prop="pendingAmount" label="待还款（银行）" min-width="140">
            <template #default="{ row }">
              {{ formatAmount(row.pendingAmount) }}
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" min-width="180" show-overflow-tooltip />
          <el-table-column label="操作" width="140" fixed="right">
            <template #default="{ row }">
              <el-button text type="primary" @click="openOrderDetail(row)">借贷单明细</el-button>
              <el-button text type="primary" @click="openRepaymentDetail({ customerId: row.customerId, customerName: row.customerName, capitalSourceType: row.capitalSourceType })">
                还款
              </el-button>
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

    <el-drawer v-model="detailDrawerVisible" :title="`${detailScope.customerName} 借贷单明细（银行）`" size="780px">
      <div class="drawer-shell">
        <div class="detail-toolbar">
          <div class="detail-toolbar__meta">
            <span>客户ID：{{ detailScope.customerNo || '-' }}</span>
          </div>
          <div class="detail-toolbar__actions">
            <el-button plain @click="openCreate({ customerId: detailScope.customerId, customerName: detailScope.customerName })">新增该客户借贷</el-button>
            <el-button type="primary" plain @click="openRepaymentDetail({ customerId: detailScope.customerId!, customerName: detailScope.customerName, capitalSourceType: detailScope.capitalSourceType })">
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
          <el-table-column prop="bankName" label="银行名称" min-width="140" show-overflow-tooltip />
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
          <el-table-column prop="remark" label="备注" min-width="160" show-overflow-tooltip />
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button text type="primary" @click="openEdit(row)">编辑</el-button>
              <el-button text type="primary" @click="openRepaymentDetail({ customerId: row.customerId, customerName: detailScope.customerName, capitalSourceType: row.capitalSourceType })">
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
            <el-form-item label="借款日期">
              <el-date-picker v-model="form.loanDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择日期" />
            </el-form-item>
            <el-form-item label="银行名称">
              <el-input v-model="form.bankName" placeholder="请输入银行名称" />
            </el-form-item>
            <el-form-item label="借款金额">
              <el-input-number v-model="form.loanAmount" :min="0" :precision="2" controls-position="right" />
            </el-form-item>
            <el-form-item label="当前余额">
              <el-input-number v-model="form.balanceAmount" :min="0" :precision="2" controls-position="right" />
            </el-form-item>
            <el-form-item label="月利息">
              <el-input-number v-model="form.monthlyInterestAmount" :min="0" :precision="2" controls-position="right" />
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
.list-toolbar__filters :deep(.el-input),
.list-toolbar__filters :deep(.el-select) {
  width: 220px;
}

.list-pagination {
  justify-content: flex-end;
  margin-right: 0;
}

.drawer-shell {
  height: 100%;
}

@media (max-width: 960px) {
  .list-pagination {
    flex-direction: column;
    align-items: stretch;
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
