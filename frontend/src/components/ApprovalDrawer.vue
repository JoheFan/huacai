<script setup lang="ts">
import { ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { transitionApi, salaryAdjustApi, workflowApi, type ApprovalRecordVO } from '../api/workflow'
import { uploadWorkflowAttachment, deleteWorkflowAttachment, listWorkflowAttachments, type UploadedFileRecord } from '../api/file'

const props = defineProps<{
  visible: boolean
  type: 'transition' | 'salary-adjust'
  id: number | null
  isEdit: boolean
}>()

const emit = defineEmits<{
  'update:visible': [val: boolean]
  refreshed: []
}>()

const detailLoading = ref(false)
const detail = ref<any>(null)
const approvalHistory = ref<ApprovalRecordVO[]>([])
const opinion = ref('')
const saving = ref(false)
const attachments = ref<UploadedFileRecord[]>([])
const uploading = ref(false)

const bizType = () => props.type === 'transition' ? 'TRANSITION' : 'SALARY_ADJUST'

const fetchDetail = async () => {
  if (!props.id) return
  detailLoading.value = true
  try {
    if (props.type === 'transition') {
      detail.value = await transitionApi.detail(props.id)
    } else {
      detail.value = await salaryAdjustApi.detail(props.id)
    }
  } catch (error) {
    ElMessage.error('加载详情失败')
  } finally {
    detailLoading.value = false
  }
}

const fetchApprovalHistory = async () => {
  if (!props.id) return
  try {
    approvalHistory.value = await workflowApi.getApprovalRecords(bizType(), props.id)
  } catch (error) {
    approvalHistory.value = []
  }
}

const fetchAttachments = async () => {
  if (!props.id) return
  try {
    attachments.value = await listWorkflowAttachments(bizType(), props.id)
  } catch (error) {
    attachments.value = []
  }
}

const canApprove = () => {
  return detail.value?.canApprove === true
}

const handleApprove = async () => {
  if (!props.id) return
  saving.value = true
  try {
    await (props.type === 'transition'
      ? transitionApi.approve(props.id, { opinion: opinion.value, result: 'APPROVE' })
      : salaryAdjustApi.approve(props.id, { opinion: opinion.value, result: 'APPROVE' }))
    ElMessage.success('审批通过')
    opinion.value = ''
    emit('refreshed')
    emit('update:visible', false)
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '审批失败')
  } finally {
    saving.value = false
  }
}

const handleReject = async () => {
  if (!props.id) return
  saving.value = true
  try {
    await (props.type === 'transition'
      ? transitionApi.reject(props.id, { opinion: opinion.value, result: 'REJECT' })
      : salaryAdjustApi.reject(props.id, { opinion: opinion.value, result: 'REJECT' }))
    ElMessage.success('已驳回')
    opinion.value = ''
    emit('refreshed')
    emit('update:visible', false)
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '驳回失败')
  } finally {
    saving.value = false
  }
}

const fileInputRef = ref<HTMLInputElement | null>(null)

const handleFileUpload = async (event: Event) => {
  const input = event.target as HTMLInputElement
  if (!input.files || !input.files.length || !props.id) return
  uploading.value = true
  try {
    const file = input.files[0]
    await uploadWorkflowAttachment(file, bizType(), props.id)
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
    await deleteWorkflowAttachment(row.id, bizType(), props.id!)
    ElMessage.success('删除成功')
    await fetchAttachments()
  } catch (error) {
    if (error instanceof Error && error.message !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

const formatDate = (dateStr: string | undefined | null) => {
  if (!dateStr) return '-'
  return dateStr.split('T')[0]
}

const formatFileSize = (bytes: number | undefined) => {
  if (!bytes) return '-'
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
}

const getStatusText = (status: string) => {
  const transitionMap: Record<string, string> = {
    DRAFT: '草稿',
    PENDING_DEPT: '待部门审核',
    PENDING_HR: '待人事审核',
    PENDING_COMPANY: '待公司审批',
    PENDING_ADMIN: '待行政审批',
    APPROVED: '已通过',
    REJECTED: '已驳回',
    CANCELLED: '已取消',
  }
  const salaryMap: Record<string, string> = {
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
  return props.type === 'transition' ? (transitionMap[status] || status) : (salaryMap[status] || status)
}

watch(() => props.visible, async (val) => {
  if (val && props.id) {
    await Promise.all([fetchDetail(), fetchApprovalHistory(), fetchAttachments()])
    opinion.value = ''
  }
})

const closeDrawer = () => {
  emit('update:visible', false)
}
</script>

<template>
  <el-drawer
    :model-value="visible"
    :title="isEdit ? '编辑申请' : '查看申请'"
    size="700px"
    @update:model-value="emit('update:visible', $event)"
    @close="closeDrawer"
  >
    <div v-loading="detailLoading">
      <!-- 基本信息 -->
      <div class="section">
        <h4 class="section-title">申请信息</h4>
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="申请单号">{{ detail?.applyNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="detail?.processStatus === 'APPROVED' ? 'success' : detail?.processStatus === 'REJECTED' ? 'danger' : 'warning'">
              {{ getStatusText(detail?.processStatus) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="申请人">{{ detail?.employeeName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="申请部门">{{ detail?.department || '-' }}</el-descriptions-item>
          <template v-if="type === 'salary-adjust'">
            <el-descriptions-item label="当前薪资">¥{{ detail?.currentSalary?.toLocaleString() || '-' }}</el-descriptions-item>
            <el-descriptions-item label="申请调薪幅度">
              {{ detail?.adjustAmount > 0 ? '+' : '' }}{{ detail?.adjustAmount?.toLocaleString() || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="调薪后薪资">¥{{ detail?.newSalary?.toLocaleString() || '-' }}</el-descriptions-item>
          </template>
          <template v-else>
            <el-descriptions-item label="职位">{{ detail?.position || '-' }}</el-descriptions-item>
            <el-descriptions-item label="入职日期">{{ formatDate(detail?.joinDate) }}</el-descriptions-item>
            <el-descriptions-item label="预计转正日期">{{ formatDate(detail?.expectedFormalDate) }}</el-descriptions-item>
          </template>
          <el-descriptions-item label="申请时间">{{ formatDate(detail?.applyTime) }}</el-descriptions-item>
          <el-descriptions-item label="当前节点">{{ detail?.currentNode || '-' }}</el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 申请内容 -->
      <div class="section">
        <h4 class="section-title">申请内容</h4>
        <el-form label-width="100px" size="small">
          <el-form-item label="申请事由">{{ detail?.applyReason || '-' }}</el-form-item>
          <template v-if="type === 'transition'">
            <el-form-item label="自我评价">{{ detail?.selfEvaluation || '-' }}</el-form-item>
            <el-form-item label="成长">{{ detail?.growth || '-' }}</el-form-item>
            <el-form-item label="不足">{{ detail?.shortcomings || '-' }}</el-form-item>
            <el-form-item label="发展建议">{{ detail?.developmentSuggestion || '-' }}</el-form-item>
          </template>
        </el-form>
      </div>

      <!-- 附件 -->
      <div class="section">
        <h4 class="section-title">附件</h4>
        <div v-if="id" class="attachment-toolbar">
          <el-button type="primary" size="small" :loading="uploading" @click="fileInputRef?.click()">
            上传附件
          </el-button>
          <input ref="fileInputRef" type="file" style="display:none" @change="handleFileUpload" />
        </div>
        <el-table v-if="attachments.length" :data="attachments" border size="small" style="width:100%">
          <el-table-column prop="fileName" label="文件名" />
          <el-table-column prop="fileSize" label="大小" width="100">
            <template #default="{ row }">{{ formatFileSize(row.fileSize) }}</template>
          </el-table-column>
          <el-table-column v-if="id" label="操作" width="80">
            <template #default="{ row }">
              <el-button link type="danger" size="small" @click="handleDeleteFile(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-else description="暂无附件" :image-size="60" />
      </div>

      <!-- 审批历史 -->
      <div class="section">
        <h4 class="section-title">审批历史</h4>
        <el-table v-if="approvalHistory.length" :data="approvalHistory" border size="small">
          <el-table-column prop="nodeName" label="节点" width="120" />
          <el-table-column prop="operatorName" label="操作人" width="100" />
          <el-table-column prop="operateTime" label="操作时间" width="160">
            <template #default="{ row }">{{ formatDate(row.operateTime) }}</template>
          </el-table-column>
          <el-table-column prop="result" label="结果" width="80">
            <template #default="{ row }">
              <el-tag :type="row.result === 'APPROVE' ? 'success' : 'danger'" size="small">
                {{ row.result === 'APPROVE' ? '通过' : '驳回' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="opinion" label="意见" />
        </el-table>
        <el-empty v-else description="暂无审批记录" :image-size="60" />
      </div>

      <!-- 审批操作区 -->
      <div v-if="canApprove() && !isEdit" class="section">
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
</template>

<style scoped>
.section {
  margin-bottom: 24px;
}
.section-title {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #eee;
}
.approval-actions {
  display: flex;
  gap: 12px;
  margin-top: 12px;
}
.attachment-toolbar {
  margin-bottom: 12px;
}
</style>
