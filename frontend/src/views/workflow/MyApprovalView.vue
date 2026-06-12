<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { myApprovalApi, type MyApprovalTaskVO, type ActionLogVO } from '../../api/workflow'

const loading = ref(false)
const rows = ref<MyApprovalTaskVO[]>([])
const total = ref(0)
const activeTab = ref<'todos' | 'initiated' | 'processed'>('todos')
const detailDrawerVisible = ref(false)
const detailTask = ref<MyApprovalTaskVO | null>(null)
const approvalHistory = ref<ActionLogVO[]>([])
const opinion = ref('')
const saving = ref(false)

const filters = reactive({
  pageNum: 1,
  pageSize: 10,
  bizType: '',
})

const fetchList = async () => {
  loading.value = true
  try {
    let data
    if (activeTab.value === 'todos') {
      data = await myApprovalApi.pageTodos({ ...filters, bizType: filters.bizType || undefined })
    } else if (activeTab.value === 'initiated') {
      data = await myApprovalApi.pageInitiated({ ...filters, bizType: filters.bizType || undefined })
    } else {
      data = await myApprovalApi.pageProcessed({ ...filters, bizType: filters.bizType || undefined })
    }
    rows.value = data.records
    total.value = data.total
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '加载失败')
  } finally {
    loading.value = false
  }
}

const handleTabChange = async (tab: string) => {
  activeTab.value = tab as 'todos' | 'initiated' | 'processed'
  filters.pageNum = 1
  await fetchList()
}

const handleSearch = async () => {
  filters.pageNum = 1
  await fetchList()
}

const handleReset = async () => {
  filters.bizType = ''
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

const openDetail = async (row: MyApprovalTaskVO) => {
  detailTask.value = row
  opinion.value = ''
  await fetchTimeline()
  detailDrawerVisible.value = true
}

const fetchTimeline = async () => {
  if (!detailTask.value) return
  try {
    approvalHistory.value = await myApprovalApi.getTimeline(detailTask.value.bizType, detailTask.value.bizId)
  } catch (error) {
    approvalHistory.value = []
  }
}

const handleApprove = async () => {
  if (!detailTask.value?.taskId) return
  saving.value = true
  try {
    await myApprovalApi.approveTask(detailTask.value.taskId, opinion.value)
    ElMessage.success('审批通过')
    opinion.value = ''
    detailDrawerVisible.value = false
    await fetchList()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '审批失败')
  } finally {
    saving.value = false
  }
}

const handleReject = async () => {
  if (!detailTask.value?.taskId) return
  saving.value = true
  try {
    await myApprovalApi.rejectTask(detailTask.value.taskId, opinion.value)
    ElMessage.success('已驳回')
    opinion.value = ''
    detailDrawerVisible.value = false
    await fetchList()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '驳回失败')
  } finally {
    saving.value = false
  }
}

const formatDateTime = (dateStr: string | undefined | null) => {
  if (!dateStr) return '-'
  return String(dateStr).replace('T', ' ').substring(0, 19)
}

const getBizTypeText = (type: string) => {
  const map: Record<string, string> = {
    TRANSITION: '转正申请',
    SALARY_ADJUST: '调薪申请',
    REIMBURSEMENT: '报销申请',
  }
  return map[type] || type
}

const getActionResultText = (result: string) => {
  const map: Record<string, string> = {
    APPROVE: '通过',
    REJECT: '驳回',
    WITHDRAW: '撤回',
  }
  return map[result] || result
}

const getActionCodeText = (code: string) => {
  const map: Record<string, string> = {
    SUBMIT: '提交',
    APPROVE: '同意',
    REJECT: '驳回',
    WITHDRAW: '撤回',
    RESUBMIT: '重提',
  }
  return map[code] || code
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
          <span class="page-intro__eyebrow">我的审批</span>
          <h2 class="page-intro__title">审批中心</h2>
          <p class="page-intro__desc">集中查看待我审批、我发起和我已审批的流程任务，支持按业务类型筛选并进入审批详情处理。</p>
        </div>
      </div>
    </section>

    <section class="card">
      <div class="card__section page-stack">
        <el-tabs v-model="activeTab" class="page-tabs" @tab-change="handleTabChange">
          <el-tab-pane label="待我审批" name="todos" />
          <el-tab-pane label="我发起的" name="initiated" />
          <el-tab-pane label="我已审批" name="processed" />
        </el-tabs>

        <div class="list-toolbar">
          <div class="list-toolbar__filters">
            <el-select v-model="filters.bizType" placeholder="业务类型" clearable>
              <el-option label="转正申请" value="TRANSITION" />
              <el-option label="调薪申请" value="SALARY_ADJUST" />
              <el-option label="报销申请" value="REIMBURSEMENT" />
            </el-select>
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
        <el-table-column prop="bizType" label="业务类型" width="120">
          <template #default="{ row }">{{ getBizTypeText(row.bizType) }}</template>
        </el-table-column>
        <el-table-column prop="bizNo" label="单号" width="180" />
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="applicantName" label="申请人" width="100" />
        <el-table-column prop="applicantOrgName" label="部门" width="120" />
        <el-table-column v-if="activeTab === 'todos'" prop="nodeName" label="待审批节点" width="120" />
        <el-table-column v-else prop="taskStatus" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.taskStatus === 'APPROVED' ? 'success' : row.taskStatus === 'REJECTED' ? 'danger' : 'info'">
              {{ row.taskStatus === 'APPROVED' ? '已通过' : row.taskStatus === 'REJECTED' ? '已驳回' : row.taskStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column v-if="activeTab !== 'todos'" prop="nodeName" label="审批节点" width="120" />
        <el-table-column prop="createdAt" label="创建时间" width="160">
          <template #default="{ row }">{{ formatDateTime(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDetail(row)">查看</el-button>
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

    <!-- 详情抽屉 -->
    <el-drawer
      v-model="detailDrawerVisible"
      title="审批详情"
      size="600px"
      @close="detailDrawerVisible = false"
    >
      <div v-if="detailTask">
        <div class="section">
          <h4 class="section-title">申请信息</h4>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="业务类型">{{ getBizTypeText(detailTask.bizType) }}</el-descriptions-item>
            <el-descriptions-item label="单号">{{ detailTask.bizNo }}</el-descriptions-item>
            <el-descriptions-item label="申请人">{{ detailTask.applicantName }}</el-descriptions-item>
            <el-descriptions-item label="部门">{{ detailTask.applicantOrgName }}</el-descriptions-item>
            <el-descriptions-item label="当前节点" :span="2">{{ detailTask.nodeName }}</el-descriptions-item>
            <el-descriptions-item label="标题" :span="2">{{ detailTask.title }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="section">
          <h4 class="section-title">审批历史</h4>
          <el-table v-if="approvalHistory.length" :data="approvalHistory" border size="small">
            <el-table-column prop="actionCode" label="动作" width="100">
              <template #default="{ row }">{{ getActionCodeText(row.actionCode) }}</template>
            </el-table-column>
            <el-table-column prop="nodeName" label="节点" width="120" />
            <el-table-column prop="operatorName" label="操作人" width="100" />
            <el-table-column prop="actionTime" label="操作时间" width="160">
              <template #default="{ row }">{{ formatDateTime(row.actionTime) }}</template>
            </el-table-column>
            <el-table-column prop="actionResult" label="结果" width="80">
              <template #default="{ row }">
                <el-tag v-if="row.actionResult" :type="row.actionResult === 'APPROVE' ? 'success' : 'danger'" size="small">
                  {{ getActionResultText(row.actionResult) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="actionOpinion" label="意见" />
          </el-table>
          <el-empty v-else description="暂无审批记录" :image-size="60" />
        </div>

        <div v-if="activeTab === 'todos'" class="section">
          <h4 class="section-title">审批操作</h4>
          <el-form label-width="80px" size="small">
            <el-form-item label="审批意见">
              <el-input v-model="opinion" type="textarea" :rows="3" placeholder="请输入审批意见（选填）" />
            </el-form-item>
          </el-form>
          <div class="approval-actions">
            <el-button type="success" :loading="saving" @click="handleApprove">通过</el-button>
            <el-button type="danger" :loading="saving" @click="handleReject">驳回</el-button>
          </div>
        </div>
      </div>
    </el-drawer>
  </section>
</template>

<style scoped>
.section { margin-bottom: 24px; }
.section-title { font-size: 14px; font-weight: 600; margin-bottom: 12px; padding-bottom: 8px; border-bottom: 1px solid #eee; }
.approval-actions { display: flex; gap: 12px; margin-top: 12px; }
</style>
