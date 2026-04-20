<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { fetchOperationLogPage, type OperationLogQuery, type OperationLogRecord } from '../../../api/system'

const loading = ref(false)
const rows = ref<OperationLogRecord[]>([])
const total = ref(0)

const filters = reactive<OperationLogQuery>({
  pageNum: 1,
  pageSize: 20,
  keyword: '',
  moduleCode: '',
  bizType: '',
})

const moduleCodeOptions = [
  { label: '全部模块', value: '' },
  { label: '组织管理', value: 'system.orgs' },
  { label: '用户管理', value: 'system.users' },
  { label: '角色管理', value: 'system.roles' },
  { label: '客户管理', value: 'customers' },
]

const bizTypeOptions = [
  { label: '全部类型', value: '' },
  { label: '新增', value: 'CREATE' },
  { label: '修改', value: 'UPDATE' },
  { label: '删除', value: 'DELETE' },
  { label: '状态变更', value: 'STATUS' },
  { label: '权限变更', value: 'PERMISSION' },
]

const fetchList = async () => {
  loading.value = true
  try {
    const data = await fetchOperationLogPage(filters)
    rows.value = data.records
    total.value = data.total
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '日志列表加载失败')
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
  filters.moduleCode = ''
  filters.bizType = ''
  filters.pageNum = 1
  await fetchList()
}

const handlePageChange = async (page: number) => {
  filters.pageNum = page
  await fetchList()
}

const formatDate = (dateStr: string | null | undefined) => {
  if (!dateStr) return '-'
  return dateStr.replace('T', ' ').substring(0, 19)
}

const getResultBadge = (code: string | null | undefined) => {
  if (code === 'SUCCESS') {
    return { type: 'success' as const, text: '成功' }
  }
  return { type: 'danger' as const, text: '失败' }
}

const getActionDesc = (record: OperationLogRecord) => {
  return record.actionDesc || record.bizType || '-'
}

onMounted(fetchList)
</script>

<template>
  <section class="page-shell">
    <section class="card">
      <div class="card__section page-intro">
        <div class="page-intro__copy">
          <span class="page-intro__eyebrow">系统管理</span>
          <h2>管理日志</h2>
          <p>记录系统关键操作，支持按模块、操作类型、关键词和时间范围筛选。</p>
        </div>
      </div>
    </section>

    <section class="card">
      <div class="card__section list-toolbar">
        <div class="list-toolbar__filters">
          <el-input
            v-model="filters.keyword"
            placeholder="搜索操作描述、结果信息"
            clearable
            @keyup.enter="handleSearch"
          />
          <el-select v-model="filters.moduleCode" placeholder="模块" clearable style="width: 140px">
            <el-option
              v-for="option in moduleCodeOptions"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
          <el-select v-model="filters.bizType" placeholder="操作类型" clearable style="width: 120px">
            <el-option
              v-for="option in bizTypeOptions"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
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
          <el-table-column prop="createdAt" label="操作时间" min-width="160">
            <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
          </el-table-column>
          <el-table-column prop="userName" label="操作人" min-width="100" />
          <el-table-column prop="moduleCode" label="模块" min-width="120">
            <template #default="{ row }">
              {{ moduleCodeOptions.find(o => o.value === row.moduleCode)?.label || row.moduleCode || '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="actionDesc" label="操作描述" min-width="140">
            <template #default="{ row }">{{ getActionDesc(row) }}</template>
          </el-table-column>
          <el-table-column prop="targetType" label="操作对象" min-width="100" />
          <el-table-column prop="targetId" label="对象ID" min-width="100">
            <template #default="{ row }">{{ row.targetId || '-' }}</template>
          </el-table-column>
          <el-table-column prop="resultCode" label="结果" min-width="80">
            <template #default="{ row }">
              <el-tag :type="getResultBadge(row.resultCode).type" size="small">
                {{ getResultBadge(row.resultCode).text }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="ip" label="IP地址" min-width="130" />
          <el-table-column prop="resultMsg" label="备注" min-width="160" show-overflow-tooltip />
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

.list-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.list-toolbar__filters {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  flex: 1;
}

.list-toolbar__actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
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
}
</style>