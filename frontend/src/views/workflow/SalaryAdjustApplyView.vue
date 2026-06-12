<script setup lang="ts">
import { onMounted, reactive, ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh } from '@element-plus/icons-vue'
import { salaryAdjustApi, type SalaryAdjustApplyVO, type SalaryAdjustApplyPageQuery } from '../../api/workflow'
import { employeeApi, type EmployeeVO } from '../../api/hr'
import ApprovalDrawer from '../../components/ApprovalDrawer.vue'

const loading = ref(false)
const rows = ref<SalaryAdjustApplyVO[]>([])
const total = ref(0)
const activeTab = ref<'my' | 'all'>('my')
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref<number | null>(null)
const detailDrawerVisible = ref(false)
const detailId = ref<number | null>(null)
const detailIsEdit = ref(false)
const employees = ref<EmployeeVO[]>([])
const currentSalary = ref<number>(0)

const filters = reactive<SalaryAdjustApplyPageQuery>({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  processStatus: '',
  scope: 'my',
})

const form = reactive({
  employeeId: undefined as number | undefined,
  adjustAmount: 0,
  applyReason: '',
  draftOpinion: '',
})

const computedNewSalary = computed(() => {
  if (currentSalary.value && form.adjustAmount) {
    return currentSalary.value + form.adjustAmount
  }
  return currentSalary.value || 0
})

const formEmployee = computed(() => {
  return employees.value.find(e => e.id === form.employeeId)
})

const fetchList = async () => {
  loading.value = true
  try {
    filters.scope = activeTab.value
    const data = await salaryAdjustApi.page(filters)
    rows.value = data.records
    total.value = data.total
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '加载失败')
  } finally {
    loading.value = false
  }
}

const fetchEmployees = async () => {
  try {
    employees.value = await employeeApi.list()
  } catch (error) {
    console.error('加载员工列表失败', error)
  }
}

const fetchCurrentSalary = async (employeeId: number) => {
  try {
    const salary = await employeeApi.getCurrentSalary(employeeId)
    currentSalary.value = salary || 0
  } catch (error) {
    currentSalary.value = 0
  }
}

watch(() => form.employeeId, (newId) => {
  if (newId && !isEdit.value) {
    void fetchCurrentSalary(newId)
  }
})

const handleTabChange = async (tab: string) => {
  activeTab.value = tab as 'my' | 'all'
  filters.pageNum = 1
  await fetchList()
}

const handleSearch = async () => {
  filters.pageNum = 1
  await fetchList()
}

const handleReset = async () => {
  filters.keyword = ''
  filters.processStatus = ''
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
  await fetchEmployees()
  isEdit.value = false
  editId.value = null
  currentSalary.value = 0
  Object.assign(form, {
    employeeId: undefined,
    adjustAmount: 0,
    applyReason: '',
    draftOpinion: '',
  })
  dialogVisible.value = true
}

const openDetail = (row: SalaryAdjustApplyVO) => {
  detailId.value = row.id
  detailIsEdit.value = false
  detailDrawerVisible.value = true
}

const openEditDetail = async (row: SalaryAdjustApplyVO) => {
  await fetchEmployees()
  isEdit.value = true
  editId.value = row.id
  detailId.value = row.id
  detailIsEdit.value = true
  currentSalary.value = row.currentSalary
  Object.assign(form, {
    employeeId: row.employeeId,
    adjustAmount: row.adjustAmount,
    applyReason: row.applyReason,
    draftOpinion: row.draftOpinion,
  })
  dialogVisible.value = true
}

const handleSave = async () => {
  if (!form.employeeId) {
    ElMessage.error('请选择调薪员工')
    return
  }
  if (!form.adjustAmount) {
    ElMessage.error('请输入申请调薪幅度')
    return
  }
  if (currentSalary.value === 0) {
    ElMessage.error('该员工没有工资标准，请先在工资管理中维护')
    return
  }
  try {
    if (isEdit.value && editId.value) {
      await salaryAdjustApi.update(editId.value, form as any)
    } else {
      await salaryAdjustApi.create(form as any)
    }
    ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
    dialogVisible.value = false
    detailDrawerVisible.value = false
    await fetchList()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '保存失败')
  }
}

const handleSubmit = async (row: SalaryAdjustApplyVO) => {
  try {
    await ElMessageBox.confirm('确定要提交此调薪申请吗？', '提交确认', { type: 'warning' })
    await salaryAdjustApi.submit(row.id)
    ElMessage.success('提交成功')
    await fetchList()
  } catch (error) {
    if (error instanceof Error && error.message !== 'cancel') {
      ElMessage.error(error.message || '提交失败')
    }
  }
}

const formatDate = (dateStr: string | undefined | null) => {
  if (!dateStr) return '-'
  return dateStr.split('T')[0]
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    DRAFT: '草稿',
    PENDING_DEPT: '待部门审核',
    PENDING_HR: '待人事审核',
    PENDING_LEADER: '待分管领导',
    PENDING_SCHOOL: '待校领导',
    PENDING_FINANCE: '待财务办理',
    APPROVED: '已通过',
    REJECTED: '已驳回',
    CANCELLED: '已取消',
  }
  return map[status] || status
}

const getStatusType = (status: string) => {
  const map: Record<string, string> = {
    DRAFT: 'info',
    PENDING_DEPT: 'warning',
    PENDING_HR: 'warning',
    PENDING_LEADER: 'warning',
    PENDING_SCHOOL: 'warning',
    PENDING_FINANCE: 'warning',
    APPROVED: 'success',
    REJECTED: 'danger',
    CANCELLED: 'info',
  }
  return map[status] || 'info'
}

import { watch } from 'vue'

onMounted(() => {
  void fetchList()
})
</script>

<template>
  <section class="page-shell">
    <section class="card">
      <div class="card__section page-intro">
        <div class="page-intro__copy">
          <span class="page-intro__eyebrow">工作流 / 人事异动</span>
          <h2 class="page-intro__title">调薪申请</h2>
          <p class="page-intro__desc">统一管理员工调薪申请，支持按创建范围切换查看，并可直接进入申请详情、编辑和提交流程。</p>
        </div>

        <div class="page-intro__actions">
          <el-button type="primary" :icon="Plus" @click="openCreate">新增申请</el-button>
        </div>
      </div>
    </section>

    <section class="card">
      <div class="card__section page-stack">
        <el-tabs v-model="activeTab" class="page-tabs" @tab-change="handleTabChange">
          <el-tab-pane label="我创建的" name="my" />
          <el-tab-pane label="全部" name="all" />
        </el-tabs>

        <div class="list-toolbar">
          <div class="list-toolbar__filters">
            <el-input
              v-model="filters.keyword"
              placeholder="搜索姓名"
              clearable
              @clear="handleReset"
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <Search />
              </template>
            </el-input>
          </div>
          <div class="list-toolbar__actions">
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button plain @click="handleReset">重置</el-button>
            <el-button plain :icon="Refresh" @click="fetchList">刷新</el-button>
          </div>
        </div>
      </div>
    </section>

    <section class="card">
      <div class="card__section">
        <div class="table-wrap">
      <el-table v-loading="loading" :data="rows" stripe border style="width: 100%">
        <el-table-column type="index" width="60" label="序号" />
        <el-table-column prop="applyReason" label="调薪理由" min-width="120" show-overflow-tooltip />
        <el-table-column prop="employeeName" label="申请人" width="100" />
        <el-table-column prop="department" label="申请部门" width="120" />
        <el-table-column prop="applyTime" label="申请时间" width="120">
          <template #default="{ row }">
            {{ formatDate(row.applyTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="currentSalary" label="当前薪资" width="120">
          <template #default="{ row }">
            ¥{{ row.currentSalary?.toLocaleString() || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="adjustAmount" label="申请调薪幅度" width="120">
          <template #default="{ row }">
            {{ row.adjustAmount > 0 ? '+' : '' }}{{ row.adjustAmount?.toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column prop="newSalary" label="调薪后薪资" width="120">
          <template #default="{ row }">
            ¥{{ row.newSalary?.toLocaleString() || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="processStatus" label="流转状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.processStatus)">
              {{ getStatusText(row.processStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDetail(row)">查看</el-button>
            <el-button
              v-if="row.processStatus === 'DRAFT' || row.processStatus === 'REJECTED'"
              link type="primary"
              @click="openEditDetail(row)"
            >
              编辑
            </el-button>
            <el-button
              v-if="row.processStatus === 'DRAFT' || row.processStatus === 'REJECTED'"
              link type="success"
              @click="handleSubmit(row)"
            >
              提交
            </el-button>
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

    <!-- 新建/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑调薪申请' : '新增调薪申请'" width="700px">
      <el-form :model="form" label-width="120px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="调薪员工" required>
              <el-select
                v-model="form.employeeId"
                placeholder="请选择"
                style="width: 100%"
                :disabled="isEdit"
              >
                <el-option
                  v-for="emp in employees"
                  :key="emp.id"
                  :label="emp.realName"
                  :value="emp.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="申请部门">
              <el-input :value="formEmployee?.orgName || '-'" disabled />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="当前薪资">
              <el-input :value="currentSalary > 0 ? '¥' + currentSalary.toLocaleString() : '-'" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="申请调薪幅度" required>
              <el-input-number
                v-model="form.adjustAmount"
                :precision="2"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="调薪后薪资">
              <el-input :value="computedNewSalary > 0 ? '¥' + computedNewSalary.toLocaleString() : '-'" disabled />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="申请事由">
          <el-input v-model="form.applyReason" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>

    <!-- 详情抽屉 -->
    <ApprovalDrawer
      v-model:visible="detailDrawerVisible"
      type="salary-adjust"
      :id="detailId"
      :is-edit="detailIsEdit"
      @refreshed="fetchList"
    />
  </section>
</template>

<style scoped>
</style>
