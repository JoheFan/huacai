<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  createEmployeePerformance,
  deleteEmployeePerformance,
  fetchEmployeePerformancePage,
  updateEmployeePerformance,
  type EmployeePerformanceRecord,
  type EmployeePerformanceQuery,
  type EmployeePerformanceSavePayload,
} from '../../api/analysis'
import { employeeApi, type EmployeeVO } from '../../api/hr'

const loading = ref(false)
const submitting = ref(false)
const drawerVisible = ref(false)
const editingId = ref<number | null>(null)
const rows = ref<EmployeePerformanceRecord[]>([])
const total = ref(0)
const employeeOptions = ref<EmployeeVO[]>([])

const filters = reactive<EmployeePerformanceQuery>({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  periodDate: '',
})

const form = reactive<EmployeePerformanceSavePayload>({
  employeeId: 0,
  employeeName: '',
  orgId: undefined,
  orgName: '',
  periodDate: '',
  targetAmount: null,
  actualAmount: null,
  dealAmount: null,
  dealCount: null,
  remark: '',
})

const drawerTitle = computed(() => (editingId.value ? '编辑员工绩效' : '新增员工绩效'))

const resetForm = () => {
  editingId.value = null
  form.employeeId = 0
  form.employeeName = ''
  form.orgId = undefined
  form.orgName = ''
  form.periodDate = ''
  form.targetAmount = null
  form.actualAmount = null
  form.dealAmount = null
  form.dealCount = null
  form.remark = ''
}

const fillForm = (row: EmployeePerformanceRecord) => {
  editingId.value = row.id
  form.employeeId = row.employeeId
  form.employeeName = row.employeeName
  form.orgId = row.orgId ?? undefined
  form.orgName = row.orgName || ''
  form.periodDate = row.periodDate
  form.targetAmount = row.targetAmount
  form.actualAmount = row.actualAmount
  form.dealAmount = row.dealAmount
  form.dealCount = row.dealCount
  form.remark = row.remark || ''
}

const handleEmployeeChange = (employeeId: number) => {
  const emp = employeeOptions.value.find(e => e.id === employeeId)
  if (emp) {
    form.employeeName = emp.realName || ''
    form.orgId = emp.orgId ?? undefined
    form.orgName = emp.orgName || ''
  }
}

const fetchList = async () => {
  loading.value = true
  try {
    const data = await fetchEmployeePerformancePage(filters)
    rows.value = data.records
    total.value = data.total
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '员工绩效加载失败')
  } finally {
    loading.value = false
  }
}

const fetchEmployees = async () => {
  try {
    const data = await employeeApi.page({ pageNum: 1, pageSize: 200 })
    employeeOptions.value = data.records
  } catch (error) {
    console.error('Failed to load employees:', error)
  }
}

const handleSearch = async () => {
  filters.pageNum = 1
  await fetchList()
}

const handleReset = async () => {
  filters.keyword = ''
  filters.periodDate = ''
  filters.pageNum = 1
  await fetchList()
}

const openCreate = () => {
  resetForm()
  drawerVisible.value = true
}

const openEdit = (row: EmployeePerformanceRecord) => {
  fillForm(row)
  drawerVisible.value = true
}

const handleSubmit = async () => {
  if (!form.employeeId) {
    ElMessage.warning('请选择员工')
    return
  }
  if (!form.periodDate) {
    ElMessage.warning('请选择日期')
    return
  }

  submitting.value = true
  try {
    if (editingId.value) {
      await updateEmployeePerformance(editingId.value, form)
      ElMessage.success('员工绩效已更新')
    } else {
      await createEmployeePerformance(form)
      ElMessage.success('员工绩效已创建')
    }
    drawerVisible.value = false
    await fetchList()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '保存失败')
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (row: EmployeePerformanceRecord) => {
  try {
    await ElMessageBox.confirm('确定要删除该记录吗？', '确认删除', { type: 'warning' })
    await deleteEmployeePerformance(row.id)
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

onMounted(() => {
  fetchList()
  fetchEmployees()
})
</script>

<script lang="ts">
import { computed } from 'vue'
export default { name: 'PerformanceView' }
</script>

<template>
  <section class="page-shell">
    <section class="card">
      <div class="card__section list-toolbar">
        <div class="list-toolbar__filters">
          <el-input v-model="filters.keyword" placeholder="员工名称" clearable />
          <el-date-picker v-model="filters.periodDate" type="month" value-format="YYYY-MM" placeholder="日期月份" style="width: 160px" />
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
          <el-table-column prop="employeeName" label="员工名称" min-width="120" />
          <el-table-column prop="orgName" label="部门" min-width="150" show-overflow-tooltip />
          <el-table-column prop="periodDate" label="日期（月）" min-width="120" />
          <el-table-column prop="targetAmount" label="绩效目标" min-width="140">
            <template #default="{ row }">
              {{ formatAmount(row.targetAmount) }}
            </template>
          </el-table-column>
          <el-table-column prop="actualAmount" label="实际达成" min-width="140">
            <template #default="{ row }">
              {{ formatAmount(row.actualAmount) }}
            </template>
          </el-table-column>
          <el-table-column prop="dealAmount" label="成交金额" min-width="140">
            <template #default="{ row }">
              {{ formatAmount(row.dealAmount) }}
            </template>
          </el-table-column>
          <el-table-column prop="dealCount" label="成交数" min-width="100">
            <template #default="{ row }">
              {{ row.dealCount ?? '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="updatedAt" label="更新时间" min-width="170" />
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

    <el-drawer v-model="drawerVisible" :title="drawerTitle" size="520px">
      <div class="drawer-shell">
        <el-form label-position="top">
          <div class="form-grid">
            <el-form-item label="员工" required>
              <el-select
                v-model="form.employeeId"
                placeholder="请选择员工"
                filterable
                style="width: 100%"
                @change="handleEmployeeChange"
              >
                <el-option
                  v-for="emp in employeeOptions"
                  :key="emp.id"
                  :label="emp.realName"
                  :value="emp.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="部门">
              <el-input v-model="form.orgName" placeholder="自动带出" disabled />
            </el-form-item>
            <el-form-item label="日期（月）" required>
              <el-date-picker v-model="form.periodDate" type="month" value-format="YYYY-MM" placeholder="请选择月份" style="width: 100%" />
            </el-form-item>
            <el-form-item label="绩效目标">
              <el-input-number v-model="form.targetAmount" :min="0" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
            <el-form-item label="实际达成">
              <el-input-number v-model="form.actualAmount" :min="0" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
            <el-form-item label="成交金额">
              <el-input-number v-model="form.dealAmount" :min="0" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
            <el-form-item label="成交数">
              <el-input-number v-model="form.dealCount" :min="0" controls-position="right" style="width: 100%" />
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
  flex: 1 1 500px;
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

@media (max-width: 960px) {
  .list-toolbar,
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
