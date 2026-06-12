<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, type UploadRequestOptions } from 'element-plus'
import { deleteFile, uploadFile } from '../../api/file'
import {
  createCustomerArchive,
  fetchCustomerArchive,
  fetchCustomerStatusLogs,
  updateCustomerArchive,
  updateCustomerStatus,
  type CustomerArchiveRecord,
} from '../../api/customer'
import {
  buildCustomerArchivePayload,
  createEmptyCustomerArchiveForm,
  type CustomerAttachment,
} from './customerModels'
import CustomerArchiveBasicSection from './CustomerArchiveBasicSection.vue'
import CustomerArchiveRiskSection from './CustomerArchiveRiskSection.vue'
import CustomerArchiveDebtSection from './CustomerArchiveDebtSection.vue'
import CustomerArchiveContractSection from './CustomerArchiveContractSection.vue'

const route = useRoute()
const router = useRouter()

const submitting = ref(false)
const loading = ref(false)
const form = reactive(createEmptyCustomerArchiveForm())
const contractAttachments = ref<CustomerAttachment[][]>([[]])

const basicSectionRef = ref<HTMLElement | null>(null)
const riskSectionRef = ref<HTMLElement | null>(null)
const debtSectionRef = ref<HTMLElement | null>(null)
const contractSectionRef = ref<HTMLElement | null>(null)
const statusSectionRef = ref<HTMLElement | null>(null)

const statusLogs = ref<{ id: number; customerId: number; statusCode: string; statusName: string; changedAt: string; changedBy: number; changedByName: string; remark: string }[]>([])
const statusUpdateDialogVisible = ref(false)
const statusUpdateForm = reactive({ status: '', statusName: '', remark: '' })
const submittingStatus = ref(false)

const auditStatusOptions = [
  { label: '待审核', value: 'PENDING' },
  { label: '已通过', value: 'APPROVED' },
  { label: '已驳回', value: 'REJECTED' },
]

const editingId = computed(() => {
  const raw = route.params.id
  const parsed = typeof raw === 'string' ? Number(raw) : Number.NaN
  return Number.isFinite(parsed) && parsed > 0 ? parsed : null
})

const pageTitle = computed(() => (editingId.value ? '客户档案信息' : '新增客户档案'))

const sectionButtons = computed(() => [
  { key: 'basic', label: '基本信息', count: null },
  { key: 'risk', label: '风险评估', count: form.riskRecords.length },
  { key: 'debt', label: '负债登记', count: form.debtRecords.length },
  { key: 'contract', label: '合同管理', count: form.contractRecords.length },
  { key: 'status', label: '状态历史', count: statusLogs.value.length },
])

const toAttachment = (file: { id: number; fileName: string }): CustomerAttachment => ({ id: file.id, fileName: file.fileName })

const toUploadFiles = (attachments: CustomerAttachment[]): { name: string; uid: number; status: string }[] =>
  attachments.map((item) => ({ name: item.fileName, uid: item.id, status: 'success' }))

const scrollToSection = (key: 'basic' | 'risk' | 'debt' | 'contract' | 'status') => {
  const targetMap = { basic: basicSectionRef.value, risk: riskSectionRef.value, debt: debtSectionRef.value, contract: contractSectionRef.value, status: statusSectionRef.value }
  targetMap[key]?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

const resetForm = () => {
  const next = createEmptyCustomerArchiveForm()
  Object.assign(form, next)
  contractAttachments.value = next.contractRecords.map(() => [])
}

const applyArchiveDetail = (detail: CustomerArchiveRecord) => {
  form.customerNo = detail.customerNo || ''
  form.customerName = detail.customerName || ''
  form.gender = detail.gender || ''
  form.idCard = detail.idCard || ''
  form.birthday = detail.birthday || ''
  form.mobile = detail.mobile || ''
  form.companyName = detail.companyName || ''
  form.creditCode = detail.creditCode || ''
  form.establishedDate = detail.establishedDate || ''
  form.industry = detail.industry || ''
  form.businessAddress = detail.businessAddress || ''
  form.bankName = detail.bankName || ''
  form.bankAccount = detail.bankAccount || ''
  form.recommenderName = detail.recommenderName || ''
  form.recommenderRate = detail.recommenderRate ?? null
  form.serviceFee = detail.serviceFee ?? null
  form.auditStatus = detail.auditStatus || ''
  form.bizStatus = detail.bizStatus || ''
  form.taxRegistrationNormal = detail.taxRegistrationNormal
  form.archiveAttachments = detail.archiveAttachments.map(toAttachment)
  form.riskRecords = detail.riskRecords.length > 0 ? detail.riskRecords.map((item) => ({ id: item.id, testDate: item.testDate || '', testLimit: item.testLimit ?? null, trafficValue: item.trafficValue ?? null, compositeScore: item.compositeScore ?? null, thirdPartyScore: item.thirdPartyScore ?? null, remark: item.remark || '' })) : createEmptyCustomerArchiveForm().riskRecords
  form.debtRecords = detail.debtRecords.length > 0 ? detail.debtRecords.map((item) => ({ id: item.id, debtType: item.debtType, debtAmount: item.debtAmount ?? null, advancePaidAmount: item.advancePaidAmount ?? null, installmentAmount: item.installmentAmount ?? null, repaymentDay: item.repaymentDay ?? null, remark: item.remark || '' })) : createEmptyCustomerArchiveForm().debtRecords
  form.contractRecords = detail.contractRecords.length > 0 ? detail.contractRecords.map((item) => ({ id: item.id, customerName: item.customerName || '', companyName: item.companyName || '', creditCode: item.creditCode || '', contractNo: item.contractNo || '', contractName: item.contractName, remark: item.remark || '', fileIds: item.attachments.map((a) => a.id) })) : createEmptyCustomerArchiveForm().contractRecords
  contractAttachments.value = detail.contractRecords.length > 0 ? detail.contractRecords.map((item) => item.attachments.map(toAttachment)) : [[]]
}

const loadDetail = async () => {
  if (!editingId.value) { resetForm(); return }
  loading.value = true
  try {
    const detail = await fetchCustomerArchive(editingId.value)
    applyArchiveDetail(detail)
    await loadStatusLogs()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '客户档案加载失败')
  } finally {
    loading.value = false
  }
}

const loadStatusLogs = async () => {
  if (!editingId.value) return
  try {
    statusLogs.value = await fetchCustomerStatusLogs(editingId.value)
  } catch { /* silent */ }
}

const openStatusUpdateDialog = () => {
  statusUpdateForm.status = ''
  statusUpdateForm.statusName = ''
  statusUpdateForm.remark = ''
  statusUpdateDialogVisible.value = true
}

const handleStatusUpdate = async () => {
  if (!statusUpdateForm.status) { ElMessage.warning('请选择审核状态'); return }
  if (!editingId.value) return
  submittingStatus.value = true
  try {
    const option = auditStatusOptions.find((o) => o.value === statusUpdateForm.status)
    statusUpdateForm.statusName = option?.label ?? statusUpdateForm.status
    await updateCustomerStatus(editingId.value, statusUpdateForm)
    ElMessage.success('状态已更新')
    statusUpdateDialogVisible.value = false
    await loadStatusLogs()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '状态更新失败')
  } finally {
    submittingStatus.value = false
  }
}

const handleArchiveUpload = async (options: UploadRequestOptions) => {
  try {
    const result = await uploadFile(options.file as File)
    form.archiveAttachments.push({ id: result.id, fileName: result.fileName })
    options.onSuccess(result)
  } catch (error) {
    options.onError(error as never)
  }
}

const handleArchiveRemove = async (file: { uid: number | string }) => {
  const fileId = Number(file.uid)
  form.archiveAttachments = form.archiveAttachments.filter((item) => item.id !== fileId)
  if (Number.isFinite(fileId) && fileId > 0) {
    await deleteFile(fileId).catch(() => undefined)
  }
}

const handleSubmit = async () => {
  if (!form.customerName.trim()) { ElMessage.warning('请输入姓名'); return }
  contractAttachments.value.forEach((_, index) => {
    form.contractRecords[index].fileIds = (contractAttachments.value[index] ?? []).map((item) => item.id)
  })
  const payload = buildCustomerArchivePayload(form)
  submitting.value = true
  try {
    if (editingId.value) {
      await updateCustomerArchive(editingId.value, payload)
      ElMessage.success('客户档案已更新')
    } else {
      await createCustomerArchive(payload)
      ElMessage.success('客户档案已创建')
    }
    router.push('/customers')
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '客户档案保存失败')
  } finally {
    submitting.value = false
  }
}

onMounted(loadDetail)
</script>

<template>
  <section class="page-shell">
    <section v-loading="loading" class="card">
      <div class="card__section detail-header">
        <div class="detail-header__copy">
          <span class="detail-header__eyebrow">客户管理 / 档案页</span>
          <h2 class="detail-header__title">{{ pageTitle }}</h2>
          <p class="detail-header__desc">一次提交客户基本信息、风险评估、负债登记和合同管理。风险评估与负债登记独立列表页新增的数据会同步回显到这里。</p>
        </div>
        <div class="detail-header__actions">
          <el-button @click="router.push('/customers')">返回列表</el-button>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">提交档案</el-button>
        </div>
      </div>
      <div class="card__section archive-nav">
        <el-button v-for="section in sectionButtons" :key="section.key" plain size="small" class="archive-nav__button" @click="scrollToSection(section.key as 'basic' | 'risk' | 'debt' | 'contract')">
          {{ section.label }}
          <span v-if="section.count != null" class="archive-nav__count">{{ section.count }}</span>
        </el-button>
      </div>
    </section>

    <section ref="basicSectionRef" class="card form-section">
      <div class="card__section form-section__header">
        <div>
          <div class="form-section__title-row">
            <h3 class="form-section__title">基本信息</h3>
            <el-tag size="small" effect="plain">主档</el-tag>
          </div>
          <p class="form-section__desc">先录入客户身份、企业主体、银行与推荐人信息，客户资料原件支持多附件上传。</p>
        </div>
      </div>
      <CustomerArchiveBasicSection :form="form" @update-status="openStatusUpdateDialog">
        <template #archive-upload>
          <el-upload :http-request="handleArchiveUpload" :file-list="toUploadFiles(form.archiveAttachments)" :on-remove="handleArchiveRemove" multiple>
            <el-button plain>上传附件</el-button>
          </el-upload>
        </template>
      </CustomerArchiveBasicSection>
    </section>

    <section ref="riskSectionRef" class="card form-section">
      <CustomerArchiveRiskSection :form="form" />
    </section>

    <section ref="debtSectionRef" class="card form-section">
      <CustomerArchiveDebtSection :form="form" />
    </section>

    <section ref="contractSectionRef" class="card form-section">
      <CustomerArchiveContractSection :form="form" :contract-attachments="contractAttachments" />
    </section>

    <section ref="statusSectionRef" class="card form-section">
      <div class="card__section form-section__header">
        <div>
          <div class="form-section__title-row">
            <h3 class="form-section__title">审核状态历史</h3>
            <el-tag size="small" effect="plain">{{ statusLogs.length }} 条</el-tag>
          </div>
          <p class="form-section__desc">审核状态变更记录，每次变更都会生成一条历史。</p>
        </div>
        <div class="form-section__actions">
          <el-button type="primary" plain @click="openStatusUpdateDialog">变更审核状态</el-button>
        </div>
      </div>
      <div class="card__section">
        <el-table :data="statusLogs" table-layout="fixed">
          <el-table-column prop="statusCode" label="状态代码" min-width="120">
            <template #default="{ row }">
              <el-tag :type="row.statusCode === 'APPROVED' ? 'success' : row.statusCode === 'REJECTED' ? 'danger' : 'warning'" size="small">{{ row.statusName }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="changedByName" label="操作人" min-width="120" />
          <el-table-column prop="changedAt" label="变更时间" min-width="180">
            <template #default="{ row }">{{ row.changedAt ? new Date(row.changedAt).toLocaleString('zh-CN') : '-' }}</template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" min-width="200" show-overflow-tooltip />
        </el-table>
        <div v-if="statusLogs.length === 0" class="empty-tip">暂无状态变更记录</div>
      </div>
    </section>
  </section>

  <el-dialog v-model="statusUpdateDialogVisible" title="变更审核状态" width="400px">
    <el-form label-position="top">
      <el-form-item label="审核状态" required>
        <el-select v-model="statusUpdateForm.status" placeholder="请选择审核状态" style="width: 100%">
          <el-option v-for="option in auditStatusOptions" :key="option.value" :label="option.label" :value="option.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="statusUpdateForm.remark" type="textarea" :rows="3" placeholder="请输入备注（可选）" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="statusUpdateDialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="submittingStatus" @click="handleStatusUpdate">确认变更</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
/* 仅保留需要局部处理的样式，大部分已上移到全局 */
</style>
