<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  createIncrementSummary,
  deleteIncrementSummary,
  fetchIncrementSummaryPage,
  updateIncrementSummary,
  type IncrementSummaryRecord,
  type IncrementSummaryQuery,
  type IncrementSummarySavePayload,
} from '../../api/analysis'

const loading = ref(false)
const submitting = ref(false)
const drawerVisible = ref(false)
const editingId = ref<number | null>(null)
const rows = ref<IncrementSummaryRecord[]>([])
const total = ref(0)

const filters = reactive<IncrementSummaryQuery>({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
})

const form = reactive<IncrementSummarySavePayload>({
  companyName: '',
  businessAddress: '',
  industry: '',
  startDate: '',
  janAmount: null,
  febAmount: null,
  marAmount: null,
  aprAmount: null,
  mayAmount: null,
  junAmount: null,
  julAmount: null,
  augAmount: null,
  sepAmount: null,
  octAmount: null,
  novAmount: null,
  decAmount: null,
  remark: '',
})

const drawerTitle = computed(() => (editingId.value ? '编辑增量总览' : '新增增量总览'))

const resetForm = () => {
  editingId.value = null
  form.companyName = ''
  form.businessAddress = ''
  form.industry = ''
  form.startDate = ''
  form.janAmount = null
  form.febAmount = null
  form.marAmount = null
  form.aprAmount = null
  form.mayAmount = null
  form.junAmount = null
  form.julAmount = null
  form.augAmount = null
  form.sepAmount = null
  form.octAmount = null
  form.novAmount = null
  form.decAmount = null
  form.remark = ''
}

const fillForm = (row: IncrementSummaryRecord) => {
  editingId.value = row.id
  form.companyName = row.companyName
  form.businessAddress = row.businessAddress || ''
  form.industry = row.industry || ''
  form.startDate = row.startDate || ''
  form.janAmount = row.janAmount
  form.febAmount = row.febAmount
  form.marAmount = row.marAmount
  form.aprAmount = row.aprAmount
  form.mayAmount = row.mayAmount
  form.junAmount = row.junAmount
  form.julAmount = row.julAmount
  form.augAmount = row.augAmount
  form.sepAmount = row.sepAmount
  form.octAmount = row.octAmount
  form.novAmount = row.novAmount
  form.decAmount = row.decAmount
  form.remark = row.remark || ''
}

const fetchList = async () => {
  loading.value = true
  try {
    const data = await fetchIncrementSummaryPage(filters)
    rows.value = data.records
    total.value = data.total
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '增量总览加载失败')
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
  filters.pageNum = 1
  await fetchList()
}

const openCreate = () => {
  resetForm()
  drawerVisible.value = true
}

const openEdit = (row: IncrementSummaryRecord) => {
  fillForm(row)
  drawerVisible.value = true
}

const handleSubmit = async () => {
  if (!form.companyName) {
    ElMessage.warning('请输入公司名称')
    return
  }

  submitting.value = true
  try {
    if (editingId.value) {
      await updateIncrementSummary(editingId.value, form)
      ElMessage.success('增量总览已更新')
    } else {
      await createIncrementSummary(form)
      ElMessage.success('增量总览已创建')
    }
    drawerVisible.value = false
    await fetchList()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '保存失败')
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (row: IncrementSummaryRecord) => {
  try {
    await ElMessageBox.confirm('确定要删除该记录吗？', '确认删除', { type: 'warning' })
    await deleteIncrementSummary(row.id)
    ElMessage.success('删除成功')
    await fetchList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error instanceof Error ? error.message : '删除失败')
    }
  }
}

const handlePageChange = async (page: number) => {
  filters.pageNum = page
  await fetchList()
}

const formatAmount = (value: number | null | undefined) => {
  if (value == null) return '-'
  return `¥${value.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`
}

const formatDate = (value: string | null | undefined) => value || '-'

const getMonthAmount = (row: IncrementSummaryRecord, month: string) => {
  const key = `${month}Amount` as keyof IncrementSummaryRecord
  const val = row[key] as number | null
  return formatAmount(val)
}

onMounted(() => {
  fetchList()
})
</script>

<script lang="ts">
import { computed } from 'vue'
export default { name: 'IncrementOverviewView' }
</script>

<template>
  <section class="page-shell">
    <section class="card">
      <div class="card__section list-toolbar">
        <div class="list-toolbar__filters">
          <el-input v-model="filters.keyword" placeholder="公司名称 / 经营地址 / 行业" clearable />
        </div>
        <div class="list-toolbar__actions">
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button plain @click="handleReset">重置</el-button>
          <el-button type="primary" plain @click="openCreate">新增</el-button>
        </div>
      </div>
    </section>

    <section class="card">
      <div class="card__section">
        <el-table v-loading="loading" :data="rows" table-layout="fixed">
          <el-table-column prop="companyName" label="公司名称" min-width="180" show-overflow-tooltip />
          <el-table-column prop="businessAddress" label="经营地址" min-width="200" show-overflow-tooltip />
          <el-table-column prop="industry" label="行业" min-width="120" show-overflow-tooltip />
          <el-table-column prop="startDate" label="开始日期" min-width="120">
            <template #default="{ row }">
              {{ formatDate(row.startDate) }}
            </template>
          </el-table-column>
          <el-table-column label="一月" min-width="120">
            <template #default="{ row }">{{ getMonthAmount(row, 'jan') }}</template>
          </el-table-column>
          <el-table-column label="二月" min-width="120">
            <template #default="{ row }">{{ getMonthAmount(row, 'feb') }}</template>
          </el-table-column>
          <el-table-column label="三月" min-width="120">
            <template #default="{ row }">{{ getMonthAmount(row, 'mar') }}</template>
          </el-table-column>
          <el-table-column label="四月" min-width="120">
            <template #default="{ row }">{{ getMonthAmount(row, 'apr') }}</template>
          </el-table-column>
          <el-table-column label="五月" min-width="120">
            <template #default="{ row }">{{ getMonthAmount(row, 'may') }}</template>
          </el-table-column>
          <el-table-column label="六月" min-width="120">
            <template #default="{ row }">{{ getMonthAmount(row, 'jun') }}</template>
          </el-table-column>
          <el-table-column label="七月" min-width="120">
            <template #default="{ row }">{{ getMonthAmount(row, 'jul') }}</template>
          </el-table-column>
          <el-table-column label="八月" min-width="120">
            <template #default="{ row }">{{ getMonthAmount(row, 'aug') }}</template>
          </el-table-column>
          <el-table-column label="九月" min-width="120">
            <template #default="{ row }">{{ getMonthAmount(row, 'sep') }}</template>
          </el-table-column>
          <el-table-column label="十月" min-width="120">
            <template #default="{ row }">{{ getMonthAmount(row, 'oct') }}</template>
          </el-table-column>
          <el-table-column label="十一月" min-width="120">
            <template #default="{ row }">{{ getMonthAmount(row, 'nov') }}</template>
          </el-table-column>
          <el-table-column label="十二月" min-width="120">
            <template #default="{ row }">{{ getMonthAmount(row, 'dec') }}</template>
          </el-table-column>
          <el-table-column prop="totalAmount" label="累计" min-width="140">
            <template #default="{ row }">
              <strong>{{ formatAmount(row.totalAmount) }}</strong>
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button text type="primary" @click="openEdit(row)">编辑</el-button>
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

    <el-drawer v-model="drawerVisible" :title="drawerTitle" size="720px">
      <div class="drawer-shell">
        <el-form label-position="top">
          <div class="form-grid">
            <el-form-item label="公司名称" required>
              <el-input v-model="form.companyName" placeholder="请输入公司名称" />
            </el-form-item>
            <el-form-item label="开始日期">
              <el-date-picker v-model="form.startDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择日期" style="width: 100%" />
            </el-form-item>
            <el-form-item label="经营地址">
              <el-input v-model="form.businessAddress" placeholder="请输入经营地址" />
            </el-form-item>
            <el-form-item label="行业">
              <el-input v-model="form.industry" placeholder="请输入行业" />
            </el-form-item>
          </div>

          <div class="form-section-title">月度增量</div>
          <div class="form-grid form-grid--4">
            <el-form-item label="一月">
              <el-input-number v-model="form.janAmount" :min="0" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
            <el-form-item label="二月">
              <el-input-number v-model="form.febAmount" :min="0" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
            <el-form-item label="三月">
              <el-input-number v-model="form.marAmount" :min="0" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
            <el-form-item label="四月">
              <el-input-number v-model="form.aprAmount" :min="0" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
            <el-form-item label="五月">
              <el-input-number v-model="form.mayAmount" :min="0" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
            <el-form-item label="六月">
              <el-input-number v-model="form.junAmount" :min="0" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
            <el-form-item label="七月">
              <el-input-number v-model="form.julAmount" :min="0" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
            <el-form-item label="八月">
              <el-input-number v-model="form.augAmount" :min="0" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
            <el-form-item label="九月">
              <el-input-number v-model="form.sepAmount" :min="0" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
            <el-form-item label="十月">
              <el-input-number v-model="form.octAmount" :min="0" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
            <el-form-item label="十一月">
              <el-input-number v-model="form.novAmount" :min="0" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
            <el-form-item label="十二月">
              <el-input-number v-model="form.decAmount" :min="0" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
          </div>

          <el-form-item label="备注" class="form-grid__full">
            <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
          </el-form-item>
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
  flex: 1 1 500px;
}

.list-toolbar__filters :deep(.el-input) {
  width: 240px;
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

.form-grid--4 {
  grid-template-columns: repeat(4, minmax(0, 1fr));
}

.form-grid__full {
  grid-column: 1 / -1;
}

.form-section-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--hc-text-primary);
  margin: 8px 0 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid var(--hc-border-light);
}

.drawer-actions {
  margin-top: auto;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 12px;
}

@media (max-width: 960px) {
  .list-toolbar,
  .list-pagination {
    flex-direction: column;
    align-items: stretch;
  }

  .list-toolbar__filters :deep(.el-input) {
    width: 100%;
  }

  .form-grid,
  .form-grid--4 {
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
