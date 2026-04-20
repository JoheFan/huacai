<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'
import {
  createCustomerRisk,
  deleteCustomerRisk,
  fetchCustomerRisks,
  updateCustomerRisk,
  type CustomerRiskPayload,
  type CustomerRiskQuery,
  type CustomerRiskRecord,
} from '../../api/customer'
import { fetchCustomerPage, type CustomerRecord } from '../../api/customer'
import { getRecordListRowActions, getRecordListToolbarActions } from './customerActionLayout'

const route = useRoute()
const router = useRouter()
const toolbarActions = getRecordListToolbarActions()
const rowActions = getRecordListRowActions()

const loading = ref(false)
const submitting = ref(false)
const drawerVisible = ref(false)
const editingId = ref<number | null>(null)
const rows = ref<CustomerRiskRecord[]>([])
const total = ref(0)
const customerOptions = ref<CustomerRecord[]>([])

const resolveQueryCustomerId = () => {
  const raw = route.query.customerId
  const value = Array.isArray(raw) ? raw[0] : raw
  const parsed = Number(value)
  return Number.isFinite(parsed) && parsed > 0 ? parsed : undefined
}

const filters = reactive<CustomerRiskQuery>({
  pageNum: 1,
  pageSize: 10,
  customerId: resolveQueryCustomerId(),
  keyword: '',
})

const form = reactive<CustomerRiskPayload>({
  customerId: 0,
  testDate: '',
  testLimit: null,
  trafficValue: null,
  compositeScore: null,
  thirdPartyScore: null,
  remark: '',
})

const drawerTitle = ref('新增风险评估')

const selectedCustomer = computed(
  () => customerOptions.value.find((customer) => customer.id === filters.customerId) ?? null,
)

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
    const data = await fetchCustomerRisks(filters)
    rows.value = data.records
    total.value = data.total
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '风险评估列表加载失败')
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  editingId.value = null
  drawerTitle.value = '新增风险评估'
  form.customerId = filters.customerId ?? 0
  form.testDate = ''
  form.testLimit = null
  form.trafficValue = null
  form.compositeScore = null
  form.thirdPartyScore = null
  form.remark = ''
}

const fillForm = (row: CustomerRiskRecord) => {
  form.customerId = row.customerId
  form.testDate = row.testDate ?? ''
  form.testLimit = row.testLimit ?? null
  form.trafficValue = row.trafficValue ?? null
  form.compositeScore = row.compositeScore ?? null
  form.thirdPartyScore = row.thirdPartyScore ?? null
  form.remark = row.remark ?? ''
}

const openCreate = () => {
  resetForm()
  drawerVisible.value = true
}

const openEdit = (row: CustomerRiskRecord) => {
  resetForm()
  editingId.value = row.id ?? null
  drawerTitle.value = '编辑风险评估'
  fillForm(row)
  drawerVisible.value = true
}

const openArchive = (customerId: number) => {
  router.push(`/customers/archive/${customerId}`)
}

const handleToolbarCommand = (command: string) => {
  if (command === 'refresh') {
    void fetchList()
  }
}

const handleRowCommand = (command: string, row: CustomerRiskRecord) => {
  if (command === 'archive') {
    openArchive(row.customerId)
    return
  }
  if (command === 'delete') {
    void handleDelete(row)
  }
}

const handleRowMenuCommand =
  (row: CustomerRiskRecord) =>
  (command: string | number | object) => {
    handleRowCommand(String(command), row)
  }

const syncCustomerQuery = () => {
  const query = filters.customerId
    ? {
        customerId: String(filters.customerId),
      }
    : {}

  router.replace({
    path: '/customer-risks',
    query,
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
      await updateCustomerRisk(editingId.value, form)
      ElMessage.success('风险评估已更新')
    } else {
      await createCustomerRisk(form)
      ElMessage.success('风险评估已创建')
    }
    drawerVisible.value = false
    await fetchList()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '风险评估保存失败')
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (row: CustomerRiskRecord) => {
  if (!row.id) {
    return
  }
  try {
    await ElMessageBox.confirm(`确认删除 ${row.customerName || '该客户'} 的风险评估记录吗？`, '删除确认', {
      type: 'warning',
    })
    await deleteCustomerRisk(row.id)
    ElMessage.success('风险评估已删除')
    await fetchList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error instanceof Error ? error.message : '风险评估删除失败')
    }
  }
}

const handleSearch = async () => {
  filters.pageNum = 1
  syncCustomerQuery()
  await fetchList()
}

const handleReset = async () => {
  filters.customerId = undefined
  filters.keyword = ''
  filters.pageNum = 1
  syncCustomerQuery()
  await fetchList()
}

const handlePageChange = async (page: number) => {
  filters.pageNum = page
  await fetchList()
}

const formatNumber = (value: number | null | undefined) => {
  if (value == null) {
    return '-'
  }
  return value.toLocaleString('zh-CN', {
    maximumFractionDigits: 2,
  })
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
          <span class="page-intro__eyebrow">客户管理 / 风险评估</span>
          <h2>风险评估</h2>
          <p>按记录维度维护客户风险评估。可按客户筛选，点击客户名称直接回到客户档案，列表与档案页共用同一份数据。</p>
        </div>

        <div class="page-intro__actions">
          <el-button
            v-for="action in toolbarActions.secondary"
            :key="action.key"
            plain
            @click="handleToolbarCommand(action.key)"
          >
            {{ action.label }}
          </el-button>
          <el-dropdown trigger="click">
            <el-button plain class="action-dropdown__trigger">
              更多
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item
                  v-for="action in toolbarActions.overflow"
                  :key="action.key"
                  :disabled="action.disabled"
                >
                  {{ action.label }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <el-button type="primary" @click="openCreate">{{ toolbarActions.primary.label }}</el-button>
        </div>
      </div>
    </section>

    <section class="card">
      <div class="card__section list-toolbar">
        <div class="list-toolbar__filters">
          <el-select v-model="filters.customerId" placeholder="客户筛选" clearable filterable>
            <el-option
              v-for="customer in customerOptions"
              :key="customer.id"
              :label="`${customer.customerName} / ${customer.customerNo}`"
              :value="customer.id"
            />
          </el-select>
          <el-input
            v-model="filters.keyword"
            placeholder="客户名称 / 联系电话 / 公司名称"
            clearable
            @keyup.enter="handleSearch"
          />
        </div>

        <div class="list-toolbar__actions">
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button plain @click="handleReset">重置</el-button>
        </div>
      </div>
    </section>

    <section class="card">
      <div class="card__section">
        <el-table v-loading="loading" :data="rows" table-layout="fixed">
          <el-table-column prop="customerNo" label="客户ID" min-width="120" />
          <el-table-column label="客户名称" min-width="128" fixed="left">
            <template #default="{ row }">
              <el-button text type="primary" @click="openArchive(row.customerId)">{{ row.customerName }}</el-button>
            </template>
          </el-table-column>
          <el-table-column prop="mobile" label="联系电话" min-width="130" />
          <el-table-column prop="companyName" label="公司名称" min-width="170" show-overflow-tooltip />
          <el-table-column prop="creditCode" label="统一社会信用代码" min-width="190" show-overflow-tooltip />
          <el-table-column prop="industry" label="所属行业" min-width="120" />
          <el-table-column prop="businessAddress" label="经营地址" min-width="180" show-overflow-tooltip />
          <el-table-column prop="testDate" label="测试日期" min-width="120">
            <template #default="{ row }">{{ row.testDate || '-' }}</template>
          </el-table-column>
          <el-table-column prop="testLimit" label="测试额度" min-width="110">
            <template #default="{ row }">{{ formatNumber(row.testLimit) }}</template>
          </el-table-column>
          <el-table-column prop="trafficValue" label="流量" min-width="110">
            <template #default="{ row }">{{ formatNumber(row.trafficValue) }}</template>
          </el-table-column>
          <el-table-column prop="compositeScore" label="综合评分" min-width="110">
            <template #default="{ row }">{{ formatNumber(row.compositeScore) }}</template>
          </el-table-column>
          <el-table-column prop="thirdPartyScore" label="龙信商" min-width="100">
            <template #default="{ row }">{{ formatNumber(row.thirdPartyScore) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="148" fixed="right">
            <template #default="{ row }">
              <div class="row-actions">
                <el-button text type="primary" @click="openEdit(row)">{{ rowActions.primary.label }}</el-button>
                <el-dropdown trigger="click" @command="handleRowMenuCommand(row)">
                  <el-button text class="action-dropdown__trigger action-dropdown__trigger--text">
                    更多
                    <el-icon class="el-icon--right"><ArrowDown /></el-icon>
                  </el-button>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item
                        v-for="action in rowActions.overflow"
                        :key="action.key"
                        :command="action.key"
                        :divided="action.key === 'delete'"
                        :class="{ 'action-dropdown__danger': action.tone === 'danger' }"
                      >
                        {{ action.label }}
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <div class="list-pagination">
          <span class="list-pagination__meta">共 {{ total }} 条</span>
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

    <el-drawer v-model="drawerVisible" :title="drawerTitle" size="560px">
      <div class="drawer-shell">
        <el-alert
          :closable="false"
          type="info"
          show-icon
          class="drawer-alert"
          :title="selectedCustomer ? `当前筛选客户：${selectedCustomer.customerName}` : '未指定客户筛选，新建时可直接选择客户'"
        />

        <el-form label-position="top">
          <div class="form-grid">
            <el-form-item label="客户" required>
              <el-select v-model="form.customerId" placeholder="请选择客户" filterable>
                <el-option
                  v-for="customer in customerOptions"
                  :key="customer.id"
                  :label="`${customer.customerName} / ${customer.customerNo}`"
                  :value="customer.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="测试日期">
              <el-date-picker v-model="form.testDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择日期" />
            </el-form-item>
            <el-form-item label="测试额度">
              <el-input-number v-model="form.testLimit" :min="0" :precision="2" controls-position="right" />
            </el-form-item>
            <el-form-item label="流量">
              <el-input-number v-model="form.trafficValue" :min="0" :precision="2" controls-position="right" />
            </el-form-item>
            <el-form-item label="综合评分">
              <el-input-number v-model="form.compositeScore" :precision="2" controls-position="right" />
            </el-form-item>
            <el-form-item label="龙信商">
              <el-input-number v-model="form.thirdPartyScore" :precision="2" controls-position="right" />
            </el-form-item>
            <el-form-item label="备注" class="form-grid__full">
              <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
            </el-form-item>
          </div>
        </el-form>
      </div>

      <template #footer>
        <div class="drawer-footer">
          <el-button @click="drawerVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">提交</el-button>
        </div>
      </template>
    </el-drawer>
  </section>
</template>

<style scoped>
.page-intro {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.page-intro__copy {
  max-width: 760px;
}

.page-intro__eyebrow {
  display: inline-flex;
  margin-bottom: 10px;
  color: var(--hc-primary);
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.08em;
}

.page-intro h2 {
  margin: 0;
  font-size: 26px;
  line-height: 1.2;
}

.page-intro p {
  margin: 10px 0 0;
  color: var(--hc-text-soft);
  line-height: 1.6;
}

.page-intro__actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 10px;
}

.action-dropdown__trigger {
  min-width: 88px;
}

.action-dropdown__trigger--text {
  min-width: auto;
  padding: 0;
}

.list-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.list-toolbar__filters {
  display: grid;
  flex: 1;
  grid-template-columns: minmax(220px, 280px) minmax(220px, 1fr);
  gap: 12px;
}

.list-toolbar__actions,
.row-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

:deep(.action-dropdown__danger) {
  color: var(--el-color-danger);
}

.list-pagination {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-top: 16px;
}

.list-pagination__meta {
  color: var(--hc-text-soft);
  font-size: 13px;
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

@media (max-width: 1180px) {
  .page-intro,
  .list-toolbar,
  .list-pagination {
    flex-direction: column;
    align-items: stretch;
  }

  .page-intro__actions,
  .list-toolbar__actions {
    justify-content: flex-start;
  }

  .list-toolbar__filters {
    grid-template-columns: 1fr;
  }
}
</style>
