<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus, Search, Refresh, Download, Upload } from '@element-plus/icons-vue'
import { employeeApi, type EmployeeVO, type EmployeePageQuery } from '../../api/hr'

const router = useRouter()

const loading = ref(false)
const rows = ref<EmployeeVO[]>([])
const total = ref(0)
const activeTab = ref<'ONBOARD' | 'ALL'>('ONBOARD')

const filters = reactive<EmployeePageQuery>({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  employmentStatus: 'ONBOARD',
})

const fetchList = async () => {
  loading.value = true
  try {
    filters.employmentStatus = activeTab.value === 'ONBOARD' ? 'ONBOARD' : undefined
    const data = await employeeApi.page(filters)
    rows.value = data.records
    total.value = data.total
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '员工列表加载失败')
  } finally {
    loading.value = false
  }
}

const handleTabChange = async (tab: string) => {
  activeTab.value = tab as 'ONBOARD' | 'ALL'
  filters.pageNum = 1
  await fetchList()
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
  router.push('/hr/employees/create')
}

const handleEdit = (row: EmployeeVO) => {
  router.push(`/hr/employees/${row.id}`)
}

const handleRefresh = () => {
  void fetchList()
}

const formatDate = (dateStr: string | undefined | null) => {
  if (!dateStr) return '-'
  return dateStr.split('T')[0]
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    ONBOARD: '在职',
    OFFBOARD: '离职',
    RETIRED: '退休',
  }
  return map[status] || status
}

const getGenderText = (gender: string) => {
  return gender === 'MALE' ? '男' : gender === 'FEMALE' ? '女' : '-'
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
          <span class="page-intro__eyebrow">人事管理</span>
          <h2 class="page-intro__title">员工档案</h2>
          <p class="page-intro__desc">统一维护员工基础信息、任职状态和组织归属，支持按在职范围切换查看，便于快速进入员工详情。</p>
        </div>

        <div class="page-intro__actions">
          <el-button type="primary" :icon="Plus" @click="openCreate">新增员工</el-button>
        </div>
      </div>
    </section>

    <section class="card">
      <div class="card__section page-stack">
        <el-tabs v-model="activeTab" class="page-tabs" @tab-change="handleTabChange">
          <el-tab-pane label="在职" name="ONBOARD" />
          <el-tab-pane label="全部" name="ALL" />
        </el-tabs>

        <div class="list-toolbar">
          <div class="list-toolbar__filters">
            <el-input
              v-model="filters.keyword"
              placeholder="搜索姓名、员工编号"
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
            <el-button plain :icon="Refresh" @click="handleRefresh">刷新</el-button>
            <el-button plain :icon="Upload" :disabled="true" title="功能开发中">导入</el-button>
            <el-button plain :icon="Download" :disabled="true" title="功能开发中">导出</el-button>
          </div>
        </div>
      </div>
    </section>

    <section class="card">
      <div class="card__section">
        <div class="table-wrap">
      <el-table
        v-loading="loading"
        :data="rows"
        stripe
        border
        style="width: 100%"
      >
        <el-table-column type="index" width="60" label="序号" />
        <el-table-column prop="employeeCode" label="员工编号" width="120" />
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="60">
          <template #default="{ row }">
            {{ getGenderText(row.gender) }}
          </template>
        </el-table-column>
        <el-table-column prop="birthday" label="出生年月" width="120">
          <template #default="{ row }">
            {{ formatDate(row.birthday) }}
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="workStartDate" label="参加工作时间" width="120">
          <template #default="{ row }">
            {{ formatDate(row.workStartDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="hometown" label="籍贯" width="120" show-overflow-tooltip />
        <el-table-column prop="politicalStatus" label="政治面貌" width="100" />
        <el-table-column prop="orgName" label="部门" width="120" show-overflow-tooltip />
        <el-table-column prop="jobTitle" label="职位" width="100" />
        <el-table-column prop="employmentStatus" label="人员状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.employmentStatus === 'ONBOARD' ? 'success' : 'info'">
              {{ getStatusText(row.employmentStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">修改</el-button>
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
  </section>
</template>

<style scoped>
</style>
