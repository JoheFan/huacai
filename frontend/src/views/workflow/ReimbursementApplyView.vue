<script setup lang="ts">
import { onMounted, reactive, ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh } from '@element-plus/icons-vue'
import { reimbursementApi, type ReimbursementVO, type ReimbursementPageQuery } from '../../api/finance'
import { uploadWorkflowAttachment, deleteWorkflowAttachment, listWorkflowAttachments, type UploadedFileRecord } from '../../api/file'
import { type ActionLogVO } from '../../api/workflow'
import { useAuthStore } from '../../stores/auth'

const authStore = useAuthStore()
const currentUserId = computed(() => authStore.user?.id ?? null)

const loading = ref(false)
const rows = ref<ReimbursementVO[]>([])
const total = ref(0)
const activeTab = ref<'my' | 'all'>('my')
const dialogVisible = ref(false)
const detailDrawerVisible = ref(false)
const detailId = ref<number | null>(null)
const isEdit = ref(false)
const editId = ref<number | null>(null)
const saving = ref(false)
const detailLoading = ref(false)
const detailError = ref<string | null>(null)
const detail = ref<ReimbursementVO | null>(null)
const approvalHistoryLoading = ref(false)
const approvalHistoryError = ref<string | null>(null)
const approvalHistory = ref<ActionLogVO[]>([])
const attachmentsLoading = ref(false)
const attachmentsError = ref<string | null>(null)
const attachments = ref<UploadedFileRecord[]>([])
const uploading = ref(false)

const filters = reactive<ReimbursementPageQuery>({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  processStatus: '',
  scope: 'my',
})

const form = reactive({
  amount: undefined as number | undefined,
  reason: '',
})

const fetchList = async () => {
  loading.value = true
  try {
    filters.scope = activeTab.value
    const data = await reimbursementApi.page(filters)
    rows.value = data.records
    total.value = data.total
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '加载失败')
  } finally {
    loading.value = false
  }
}

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

const openCreate = () => {
  isEdit.value = false
  editId.value = null
  Object.assign(form, { amount: undefined, reason: '' })
  dialogVisible.value = true
}

const openDetail = async (row: ReimbursementVO) => {
  detailId.value = row.id
  isEdit.value = false
  detailLoading.value = true
  detailError.value = null
  approvalHistoryError.value = null
  attachmentsError.value = null
  try {
    detail.value = await reimbursementApi.detail(row.id)
  } catch (error) {
    detail.value = null
    detailError.value = error instanceof Error ? error.message : '详情加载失败'
    ElMessage.error(detailError.value)
    detailLoading.value = false
    return
  }
  detailLoading.value = false
  await Promise.all([fetchApprovalHistory(), fetchAttachments()])
  detailDrawerVisible.value = true
}

const openEdit = (row: ReimbursementVO) => {
  isEdit.value = true
  editId.value = row.id
  detailId.value = row.id
  Object.assign(form, {
    amount: row.amount != null ? Number(row.amount) : undefined,
    reason: row.reason || '',
  })
  dialogVisible.value = true
}

const handleSave = async () => {
  if (!form.amount || form.amount <= 0) {
    ElMessage.error('报销金额必须大于0')
    return
  }
  saving.value = true
  try {
    if (isEdit.value && editId.value) {
      await reimbursementApi.update(editId.value, {
        amount: form.amount,
        reason: form.reason || undefined,
      })
      ElMessage.success('更新成功')
    } else {
      await reimbursementApi.create({
        amount: form.amount,
        reason: form.reason || undefined,
      })
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    await fetchList()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '保存失败')
  } finally {
    saving.value = false
  }
}

const handleSubmit = async (row: ReimbursementVO) => {
  try {
    await ElMessageBox.confirm('确定要提交此报销申请吗？', '提交确认', { type: 'warning' })
    await reimbursementApi.submit(row.id, {
      amount: row.amount,
      reason: row.reason,
    })
    ElMessage.success('提交成功')
    await fetchList()
  } catch (error) {
    if (error instanceof Error && error.message !== 'cancel') {
      ElMessage.error(error.message || '提交失败')
    }
  }
}

const handleWithdraw = async (row: ReimbursementVO) => {
  try {
    await ElMessageBox.confirm('确定要撤回此报销申请吗？', '撤回确认', { type: 'warning' })
    await reimbursementApi.withdraw(row.id)
    ElMessage.success('撤回成功')
    await fetchList()
  } catch (error) {
    if (error instanceof Error && error.message !== 'cancel') {
      ElMessage.error(error.message || '撤回失败')
    }
  }
}

const handleResubmit = async (row: ReimbursementVO) => {
  try {
    await ElMessageBox.confirm('确定要重新提交此报销申请吗？', '重提确认', { type: 'warning' })
    await reimbursementApi.resubmit(row.id, {
      amount: row.amount,
      reason: row.reason,
    })
    ElMessage.success('重提成功')
    dialogVisible.value = false
    await fetchList()
  } catch (error) {
    if (error instanceof Error && error.message !== 'cancel') {
      ElMessage.error(error.message || '重提失败')
    }
  }
}

const fetchApprovalHistory = async () => {
  if (!detailId.value) return
  approvalHistoryLoading.value = true
  approvalHistoryError.value = null
  try {
    const { myApprovalApi } = await import('../../api/workflow')
    approvalHistory.value = await myApprovalApi.getTimeline('REIMBURSEMENT', detailId.value)
  } catch (error) {
    approvalHistoryError.value = error instanceof Error ? error.message : '审批历史加载失败'
    approvalHistory.value = []
  } finally {
    approvalHistoryLoading.value = false
  }
}

const fetchAttachments = async () => {
  if (!detailId.value) return
  attachmentsLoading.value = true
  attachmentsError.value = null
  try {
    attachments.value = await listWorkflowAttachments('REIMBURSEMENT', detailId.value)
  } catch (error) {
    attachmentsError.value = error instanceof Error ? error.message : '附件加载失败'
    attachments.value = []
  } finally {
    attachmentsLoading.value = false
  }
}

const fileInputRef = ref<HTMLInputElement | null>(null)

const handleFileUpload = async (event: Event) => {
  const input = event.target as HTMLInputElement
  if (!input.files || !input.files.length || !detailId.value) return
  uploading.value = true
  try {
    const file = input.files[0]
    await uploadWorkflowAttachment(file, 'REIMBURSEMENT', detailId.value)
    ElMessage.success('上传成功')
    await fetchAttachments()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '上传失败')
  } finally {
    uploading.value = false
    input.value = ''
  }
}

const handleDeleteFile = async (row: UploadedFileRecord) => {
  try {
    await ElMessageBox.confirm(`确定要删除附件"${row.fileName}"吗？`, '删除确认', { type: 'warning' })
    await deleteWorkflowAttachment(row.id, 'REIMBURSEMENT', detailId.value!)
    ElMessage.success('删除成功')
    await fetchAttachments()
  } catch (error) {
    if (error instanceof Error && error.message !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

const formatDateTime = (dateStr: string | undefined | null) => {
  if (!dateStr) return '-'
  return dateStr.replace('T', ' ').substring(0, 19)
}

const formatFileSize = (bytes: number | undefined) => {
  if (!bytes) return '-'
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
}

const formatAmount = (amount: any) => {
  if (amount == null) return '-'
  return Number(amount).toLocaleString('zh-CN', { minimumFractionDigits: 2 })
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    DRAFT: '草稿',
    PENDING_DEPT: '待部门审核',
    PENDING_HR: '待人事审核',
    PENDING_LEADER: '待分管领导',
    PENDING_FINANCE: '待财务办理',
    APPROVED: '已通过',
    REJECTED: '已驳回',
    WITHDRAWN: '已撤回',
  }
  return map[status] || status
}

const getStatusType = (status: string) => {
  const map: Record<string, string> = {
    DRAFT: 'info',
    PENDING_DEPT: 'warning',
    PENDING_HR: 'warning',
    PENDING_LEADER: 'warning',
    PENDING_FINANCE: 'warning',
    APPROVED: 'success',
    REJECTED: 'danger',
    WITHDRAWN: 'info',
  }
  return map[status] || 'info'
}

const isDraftOrRejected = (row: ReimbursementVO) => {
  return row.processStatus === 'DRAFT' || row.processStatus === 'REJECTED'
}

const isDraft = (row: ReimbursementVO) => row.processStatus === 'DRAFT'
const isRejected = (row: ReimbursementVO) => row.processStatus === 'REJECTED'

const isPendingApproval = (row: ReimbursementVO) => {
  return ['PENDING_DEPT', 'PENDING_HR', 'PENDING_LEADER', 'PENDING_FINANCE'].includes(row.processStatus)
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
          <span class="page-intro__eyebrow">工作流 / 财务审批</span>
          <h2 class="page-intro__title">报销申请</h2>
          <p class="page-intro__desc">统一管理报销流程，支持按申请范围和流程状态筛选，并可直接进入详情、编辑和提交流程。</p>
        </div>

        <div class="page-intro__actions">
          <el-button type="primary" :icon="Plus" @click="openCreate">新增报销</el-button>
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
              placeholder="搜索申请人/单号"
              clearable
              @clear="handleReset"
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <Search />
              </template>
            </el-input>
            <el-select v-model="filters.processStatus" placeholder="流程状态" clearable>
              <el-option label="草稿" value="DRAFT" />
              <el-option label="待部门审核" value="PENDING_DEPT" />
              <el-option label="待人事审核" value="PENDING_HR" />
              <el-option label="待分管领导" value="PENDING_LEADER" />
              <el-option label="待财务办理" value="PENDING_FINANCE" />
              <el-option label="已通过" value="APPROVED" />
              <el-option label="已驳回" value="REJECTED" />
              <el-option label="已撤回" value="WITHDRAWN" />
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
        <el-table-column prop="applyNo" label="申请单号" width="180" />
        <el-table-column prop="applicantName" label="申请人" width="100" />
        <el-table-column prop="applicantOrgName" label="所属部门" min-width="140" show-overflow-tooltip />
        <el-table-column prop="amount" label="报销金额" width="120" align="right">
          <template #default="{ row }">¥{{ formatAmount(row.amount) }}</template>
        </el-table-column>
        <el-table-column prop="reason" label="报销事由" min-width="160" show-overflow-tooltip />
        <el-table-column prop="currentNodeName" label="当前节点" width="120">
          <template #default="{ row }">{{ row.currentNodeName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="processStatus" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.processStatus)">{{ getStatusText(row.processStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="160">
          <template #default="{ row }">{{ formatDateTime(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openDetail(row)">查看</el-button>
            <el-button
              v-if="isDraftOrRejected(row) && row.canEdit"
              link type="primary" size="small"
              @click="openEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              v-if="isDraft(row)"
              link type="success" size="small"
              @click="handleSubmit(row)"
            >
              提交
            </el-button>
            <el-button
              v-if="isRejected(row)"
              link type="warning" size="small"
              @click="handleResubmit(row)"
            >
              重新提交
            </el-button>
            <el-button
              v-if="isPendingApproval(row) && row.canWithdraw"
              link type="warning" size="small"
              @click="handleWithdraw(row)"
            >
              撤回
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

    <!-- 新建/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑报销申请' : '新建报销申请'"
      width="500px"
      @close="dialogVisible = false"
    >
      <el-form :model="form" label-width="100px">
        <el-form-item label="报销金额" required>
          <el-input-number
            v-model="form.amount"
            :min="0.01"
            :precision="2"
            style="width: 100%"
            placeholder="0.00"
          />
        </el-form-item>
        <el-form-item label="报销事由">
          <el-input v-model="form.reason" type="textarea" :rows="4" placeholder="请输入报销事由" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">
          {{ isEdit ? '保存' : '创建草稿' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 详情抽屉 -->
    <el-drawer
      v-model="detailDrawerVisible"
      title="报销申请详情"
      size="600px"
      @close="detailDrawerVisible = false"
    >
      <div v-loading="detailLoading">
        <!-- 基本信息 -->
        <div class="section">
          <h4 class="section-title">申请信息</h4>
          <template v-if="detailError">
            <el-alert :title="detailError" type="error" show-icon :closable="false" />
          </template>
          <template v-else>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="申请单号">{{ detail?.applyNo || '-' }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag v-if="detail" :type="getStatusType(detail.processStatus)">
                {{ getStatusText(detail.processStatus) }}
              </el-tag>
              <span v-else>-</span>
            </el-descriptions-item>
            <el-descriptions-item label="申请人">{{ detail?.applicantName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="所属部门">{{ detail?.applicantOrgName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="报销人">{{ detail?.reimbursementUserName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="当前节点">{{ detail?.currentNodeName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="报销金额" :span="2">
              <span v-if="detail" style="color: #E6A23C; font-weight: 600;">
                ¥{{ formatAmount(detail.amount) }}
              </span>
              <span v-else>-</span>
            </el-descriptions-item>
            <el-descriptions-item label="申请时间">{{ formatDateTime(detail?.createdAt) }}</el-descriptions-item>
            <el-descriptions-item label="提交时间">{{ formatDateTime(detail?.submitTime) }}</el-descriptions-item>
            <el-descriptions-item v-if="detail?.processStatus === 'REJECTED'" label="重提次数">
              {{ detail?.resubmitCount ?? 0 }} 次
            </el-descriptions-item>
          </el-descriptions>
          </template>
        </div>

        <!-- 报销事由 -->
        <div v-if="!detailError" class="section">
          <h4 class="section-title">报销事由</h4>
          <div class="reason-box">{{ detail?.reason || '无' }}</div>
        </div>

        <!-- 附件 -->
        <div class="section">
          <h4 class="section-title">附件</h4>
          <div class="attachment-toolbar">
            <el-button type="primary" size="small" :loading="uploading" @click="fileInputRef?.click()">
              上传附件
            </el-button>
            <input ref="fileInputRef" type="file" style="display:none" @change="handleFileUpload" />
          </div>
          <div v-if="attachmentsLoading" style="text-align:center;padding:20px;color:#909399;">加载中...</div>
          <el-alert v-else-if="attachmentsError" :title="attachmentsError" type="error" show-icon :closable="false" style="margin-bottom:8px;" />
          <div v-if="attachmentsError && !attachmentsLoading" style="margin-bottom:12px;">
            <el-button size="small" @click="fetchAttachments">重试</el-button>
          </div>
          <el-table v-else-if="attachments.length" :data="attachments" border size="small" style="width:100%">
            <el-table-column prop="fileName" label="文件名" />
            <el-table-column prop="fileSize" label="大小" width="100">
              <template #default="{ row }">{{ formatFileSize(row.fileSize) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="80">
              <template #default="{ row }">
                <el-button
                  v-if="row.uploaderId === currentUserId"
                  link type="danger" size="small"
                  @click="handleDeleteFile(row)"
                >删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-else description="暂无附件" :image-size="60" />
        </div>

        <!-- 审批历史 -->
        <div class="section">
          <h4 class="section-title">审批历史</h4>
          <div v-if="approvalHistoryLoading" style="text-align:center;padding:20px;color:#909399;">加载中...</div>
          <el-alert v-else-if="approvalHistoryError" :title="approvalHistoryError" type="error" show-icon :closable="false" style="margin-bottom:8px;" />
          <div v-if="approvalHistoryError && !approvalHistoryLoading" style="margin-bottom:12px;">
            <el-button size="small" @click="fetchApprovalHistory">重试</el-button>
          </div>
          <el-table v-else-if="approvalHistory.length" :data="approvalHistory" border size="small">
            <el-table-column prop="nodeName" label="节点" width="120" />
            <el-table-column prop="operatorName" label="操作人" width="100" />
            <el-table-column prop="actionTime" label="操作时间" width="160">
              <template #default="{ row }">{{ formatDateTime(row.actionTime) }}</template>
            </el-table-column>
            <el-table-column prop="actionResult" label="结果" width="80">
              <template #default="{ row }">
                <el-tag
                  v-if="row.actionResult"
                  :type="row.actionResult === 'AGREE' || row.actionResult === 'APPROVE' ? 'success' : 'danger'"
                  size="small"
                >
                  {{ row.actionResult === 'AGREE' || row.actionResult === 'APPROVE' ? '通过' : '驳回' }}
                </el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="actionOpinion" label="意见" />
          </el-table>
          <el-empty v-else description="暂无审批记录" :image-size="60" />
        </div>

        <!-- 底部操作 -->
        <div v-if="detail" class="detail-footer">
          <template v-if="(isDraft(detail) || isRejected(detail)) && detail.canEdit">
            <el-button type="primary" @click="openEdit(detail)">编辑</el-button>
          </template>
          <template v-if="isDraft(detail)">
            <el-button type="success" @click="handleSubmit(detail)">提交</el-button>
          </template>
          <template v-if="isRejected(detail) && detail.canResubmit">
            <el-button type="warning" @click="handleResubmit(detail)">重新提交</el-button>
          </template>
          <template v-if="isPendingApproval(detail) && detail.canWithdraw">
            <el-button type="info" @click="handleWithdraw(detail)">撤回</el-button>
          </template>
        </div>
      </div>
    </el-drawer>
  </section>
</template>

<style scoped>
.section { margin-bottom: 24px; }
.section-title { font-size: 14px; font-weight: 600; margin-bottom: 12px; padding-bottom: 8px; border-bottom: 1px solid #eee; }
.reason-box { padding: 12px; background: #f5f7fa; border-radius: 4px; white-space: pre-wrap; min-height: 60px; }
.attachment-toolbar { margin-bottom: 12px; }
.detail-footer { display: flex; gap: 12px; padding-top: 16px; border-top: 1px solid #eee; }
</style>
