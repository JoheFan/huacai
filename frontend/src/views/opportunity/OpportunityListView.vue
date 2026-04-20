<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  fetchOpportunityPage,
  createOpportunity,
  updateOpportunity,
  deleteOpportunity,
  updateOpportunityStatus,
  type OpportunityRecord,
  type OpportunityQuery,
  type OpportunitySavePayload,
} from '../../api/opportunity'
import { fetchCustomerPage, type CustomerRecord } from '../../api/customer'
import { formatLatestFollowSummary } from './opportunityFollowSummary'

const router = useRouter()
const submitting = ref(false)
const drawerVisible = ref(false)
const loading = ref(false)
const editingId = ref<number | null>(null)
const rows = ref<OpportunityRecord[]>([])
const total = ref(0)
const customerOptions = ref<CustomerRecord[]>([])

const filters = reactive<OpportunityQuery>({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  stageCode: '',
  status: '',
})

const form = reactive<OpportunitySavePayload>({
  customerId: undefined,
  priorityLevel: 'MEDIUM',
  stageCode: 'NEW',
  ownerUserId: undefined,
  estimatedAmount: undefined,
  intentLevel: 'MEDIUM',
  status: 'OPEN',
  nextFollowTime: undefined,
  remark: '',
})

const drawerTitle = ref('新增商机')

const stageOptions = [
  { value: 'NEW', label: '新建' },
  { value: 'CONTACTED', label: '已联系' },
  { value: 'QUALIFIED', label: '意向确认' },
  { value: 'PROPOSAL', label: '方案报价' },
  { value: 'NEGOTIATION', label: '商务谈判' },
  { value: 'CLOSED_WON', label: '赢单' },
  { value: 'CLOSED_LOST', label: '输单' },
]

const priorityOptions = [
  { value: 'HIGH', label: '高' },
  { value: 'MEDIUM', label: '中' },
  { value: 'LOW', label: '低' },
]

const intentOptions = [
  { value: 'HIGH', label: '高' },
  { value: 'MEDIUM', label: '中' },
  { value: 'LOW', label: '低' },
]

const resetForm = () => {
  editingId.value = null
  drawerTitle.value = '新增商机'
  form.customerId = undefined
  form.priorityLevel = 'MEDIUM'
  form.stageCode = 'NEW'
  form.ownerUserId = undefined
  form.estimatedAmount = undefined
  form.intentLevel = 'MEDIUM'
  form.status = 'OPEN'
  form.nextFollowTime = undefined
  form.remark = ''
}

const fillForm = (row: OpportunityRecord) => {
  form.customerId = row.customerId
  form.priorityLevel = row.priorityLevel || 'MEDIUM'
  form.stageCode = row.stageCode || 'NEW'
  form.ownerUserId = row.ownerUserId
  form.estimatedAmount = row.estimatedAmount ?? undefined
  form.intentLevel = row.intentLevel || 'MEDIUM'
  form.status = row.status || 'OPEN'
  form.nextFollowTime = row.nextFollowTime ?? undefined
  form.remark = row.remark || ''
}

const fetchList = async () => {
  loading.value = true
  try {
    const data = await fetchOpportunityPage(filters)
    rows.value = data.records
    total.value = data.total
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '商机列表加载失败')
  } finally {
    loading.value = false
  }
}

const loadCustomerOptions = async () => {
  try {
    const data = await fetchCustomerPage({ pageNum: 1, pageSize: 100 })
    customerOptions.value = data.records
  } catch (error) {
    console.error('Failed to load customers:', error)
  }
}

const handleSearch = async () => {
  filters.pageNum = 1
  await fetchList()
}

const handleReset = async () => {
  filters.keyword = ''
  filters.stageCode = ''
  filters.status = ''
  filters.pageNum = 1
  await fetchList()
}

const openCreate = () => {
  resetForm()
  drawerVisible.value = true
}

const openEdit = (row: OpportunityRecord) => {
  resetForm()
  editingId.value = row.id
  drawerTitle.value = '编辑商机'
  fillForm(row)
  drawerVisible.value = true
}

const handleSubmit = async () => {
  if (!form.customerId) {
    ElMessage.warning('请选择客户')
    return
  }

  submitting.value = true
  try {
    if (editingId.value) {
      await updateOpportunity(editingId.value, form)
      ElMessage.success('商机已更新')
    } else {
      await createOpportunity(form)
      ElMessage.success('商机已创建')
    }
    drawerVisible.value = false
    await fetchList()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '商机保存失败')
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (row: OpportunityRecord) => {
  try {
    await ElMessageBox.confirm('确定要删除该商机吗？', '确认删除', { type: 'warning' })
    await deleteOpportunity(row.id)
    ElMessage.success('商机已删除')
    await fetchList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error instanceof Error ? error.message : '删除失败')
    }
  }
}

const handleStatusChange = async (row: OpportunityRecord, status: string) => {
  try {
    await updateOpportunityStatus(row.id, status)
    ElMessage.success('状态已更新')
    await fetchList()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '状态更新失败')
  }
}

const openFollowRecords = (row: OpportunityRecord) => {
  router.push({
    path: `/opportunities/${row.id}/follow`,
  })
}

const handlePageChange = async (page: number) => {
  filters.pageNum = page
  await fetchList()
}

const getPriorityLabel = (code: string) => {
  return priorityOptions.find(o => o.value === code)?.label || code
}

const formatAmount = (value: number | null | undefined) => {
  if (value == null) {
    return '-'
  }
  return `¥${value.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`
}

const getLatestFollowSummary = (row: OpportunityRecord) =>
  formatLatestFollowSummary({
    latestFollowTime: row.latestFollowTime,
    latestFollowerName: row.latestFollowerName,
    latestFollowContent: row.latestFollowContent,
  })

onMounted(() => {
  fetchList()
  loadCustomerOptions()
})
</script>

<template>
  <section class="page-shell">
    <section class="card">
        <div class="card__section list-toolbar">
        <div class="list-toolbar__filters">
          <el-input v-model="filters.keyword" placeholder="客户ID / 客户名称 / 联系方式" clearable />
          <el-select v-model="filters.stageCode" placeholder="商机阶段" clearable>
            <el-option
              v-for="item in stageOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
          <el-select v-model="filters.status" placeholder="状态" clearable>
            <el-option label="进行中" value="OPEN" />
            <el-option label="已关闭" value="CLOSED" />
          </el-select>
        </div>
        <div class="list-toolbar__actions">
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button plain @click="handleReset">重置</el-button>
          <el-button type="primary" plain @click="openCreate">新增商机</el-button>
        </div>
      </div>
    </section>

    <section class="card">
      <div class="card__section">
        <el-table v-loading="loading" :data="rows" table-layout="fixed">
          <el-table-column prop="customerNo" label="客户ID" min-width="130" />
          <el-table-column prop="customerName" label="客户名称" min-width="120" />
          <el-table-column prop="mobile" label="联系方式" min-width="130" />
          <el-table-column prop="companyName" label="企业名称" min-width="180" show-overflow-tooltip />
          <el-table-column prop="creditCode" label="统一社会信用代码" min-width="190" show-overflow-tooltip />
          <el-table-column prop="priorityLevel" label="优先级" min-width="80">
            <template #default="{ row }">
              <el-tag :type="row.priorityLevel === 'HIGH' ? 'danger' : row.priorityLevel === 'LOW' ? 'info' : 'warning'">
                {{ getPriorityLabel(row.priorityLevel) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="latestFollowTime" label="最近一次跟进时间" min-width="170">
            <template #default="{ row }">
              {{ getLatestFollowSummary(row).timeText }}
            </template>
          </el-table-column>
          <el-table-column prop="latestFollowerName" label="跟进人" min-width="100">
            <template #default="{ row }">
              {{ getLatestFollowSummary(row).followerText }}
            </template>
          </el-table-column>
          <el-table-column prop="latestFollowContent" label="跟进记录" min-width="220" show-overflow-tooltip>
            <template #default="{ row }">
              {{ getLatestFollowSummary(row).contentText }}
            </template>
          </el-table-column>
          <el-table-column prop="estimatedAmount" label="预计申请额度" min-width="130">
            <template #default="{ row }">
              {{ formatAmount(row.estimatedAmount) }}
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" min-width="180" show-overflow-tooltip />
          <el-table-column label="操作" width="260" fixed="right">
            <template #default="{ row }">
              <el-button text type="primary" @click="openEdit(row)">编辑</el-button>
              <el-button text type="primary" @click="openFollowRecords(row)">跟进记录</el-button>
              <el-button
                text
                :type="row.status === 'OPEN' ? 'warning' : 'success'"
                @click="handleStatusChange(row, row.status === 'OPEN' ? 'CLOSED' : 'OPEN')"
              >
                {{ row.status === 'OPEN' ? '关闭' : '重新打开' }}
              </el-button>
              <el-button text type="danger" @click="handleDelete(row)">删除</el-button>
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

    <el-drawer v-model="drawerVisible" :title="drawerTitle" size="560px">
      <div class="drawer-shell">
        <el-form label-position="top">
          <div class="form-grid">
            <el-form-item label="客户" required class="form-grid__full">
              <el-select v-model="form.customerId" placeholder="请选择客户" filterable>
                <el-option
                  v-for="customer in customerOptions"
                  :key="customer.id"
                  :label="`${customer.customerName} (${customer.customerNo})`"
                  :value="customer.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="优先级">
              <el-select v-model="form.priorityLevel" placeholder="请选择优先级">
                <el-option
                  v-for="item in priorityOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="商机阶段">
              <el-select v-model="form.stageCode" placeholder="请选择阶段">
                <el-option
                  v-for="item in stageOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="预估金额">
              <el-input-number
                v-model="form.estimatedAmount"
                :min="0"
                :precision="2"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
            <el-form-item label="意向等级">
              <el-select v-model="form.intentLevel" placeholder="请选择意向等级">
                <el-option
                  v-for="item in intentOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="下次跟进时间" class="form-grid__full">
              <el-date-picker
                v-model="form.nextFollowTime"
                type="datetime"
                placeholder="请选择时间"
                style="width: 100%"
              />
            </el-form-item>
            <el-form-item label="备注" class="form-grid__full">
              <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
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
  flex: 1 1 680px;
}

.list-toolbar__filters :deep(.el-input),
.list-toolbar__filters :deep(.el-select) {
  width: 180px;
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
</style>
