<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { workflowApi, type ManagementRecordVO } from '../../api/workflow'

const loading = ref(false)
const rows = ref<ManagementRecordVO[]>([])
const total = ref(0)
const activeTab = ref<'TRANSITION' | 'TERMINATION' | 'TRANSFER'>('TRANSITION')

const filters = reactive({
  pageNum: 1,
  pageSize: 10,
  employeeId: undefined as number | undefined,
  recordType: 'TRANSITION',
})

const recordTypeMap: Record<string, string> = {
  TRANSITION: 'TRANSITION',
  TERMINATION: 'TERMINATION',
  TRANSFER: 'TRANSFER',
}

const fetchList = async () => {
  loading.value = true
  try {
    filters.recordType = recordTypeMap[activeTab.value]
    const data = await workflowApi.getManagementRecords({
      pageNum: filters.pageNum,
      pageSize: filters.pageSize,
      employeeId: filters.employeeId,
      recordType: filters.recordType,
    })
    rows.value = data.records
    total.value = data.total
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '加载失败')
  } finally {
    loading.value = false
  }
}

const handleTabChange = async (tab: string) => {
  activeTab.value = tab as 'TRANSITION' | 'TERMINATION' | 'TRANSFER'
  filters.pageNum = 1
  filters.recordType = recordTypeMap[activeTab.value]
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

const formatDateTime = (dateStr: string | undefined | null) => {
  if (!dateStr) return '-'
  return dateStr.replace('T', ' ').substring(0, 19)
}

const getRecordTypeText = (type: string) => {
  const map: Record<string, string> = {
    TRANSITION: '转正',
    TERMINATION: '离职',
    TRANSFER: '调岗',
  }
  return map[type] || type
}

onMounted(() => {
  void fetchList()
})
</script>

<template>
  <section class="page-shell">
    <section class="card">
      <div class="card__header">
        <h2>管理记录</h2>
      </div>

      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="转正记录" name="TRANSITION" />
        <el-tab-pane label="离职记录" name="TERMINATION" />
        <el-tab-pane label="调岗记录" name="TRANSFER" />
      </el-tabs>

      <div class="filter-bar">
        <div class="filter-bar__right">
          <el-button :icon="Refresh" @click="fetchList">刷新</el-button>
        </div>
      </div>

      <el-table v-loading="loading" :data="rows" stripe border style="width: 100%">
        <el-table-column type="index" width="60" label="序号" />
        <el-table-column prop="employeeName" label="员工姓名" width="100" />
        <el-table-column prop="recordType" label="记录类型" width="100">
          <template #default="{ row }">{{ getRecordTypeText(row.recordType) }}</template>
        </el-table-column>
        <el-table-column prop="content" label="内容" min-width="300" show-overflow-tooltip />
        <el-table-column prop="operatorName" label="操作人" width="100" />
        <el-table-column prop="operatedAt" label="操作时间" width="160">
          <template #default="{ row }">{{ formatDateTime(row.operatedAt) }}</template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="160" show-overflow-tooltip />
      </el-table>

      <el-pagination
        v-model:current-page="filters.pageNum"
        v-model:page-size="filters.pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
    </section>
  </section>
</template>

<style scoped>
.page-shell { padding: 16px; }
.card { background: #fff; border-radius: 8px; padding: 16px; }
.card__header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.card__header h2 { font-size: 18px; font-weight: 600; margin: 0; }
.filter-bar { display: flex; gap: 8px; margin-bottom: 16px; align-items: center; }
.filter-bar__right { margin-left: auto; }
</style>
