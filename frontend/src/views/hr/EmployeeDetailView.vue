<script setup lang="ts">
import { onMounted, reactive, ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Plus } from '@element-plus/icons-vue'
import {
  employeeApi,
  type EmployeeDetailVO,
  type EmployeeCreateRequest,
  type EmployeeUpdateRequest,
  type CertificateVO,
  type AssessmentVO,
  type GrowthVO,
  type FamilyVO,
  type ContractVO,
  type ChangeVO,
  type CertificateSaveRequest,
  type AssessmentSaveRequest,
  type GrowthSaveRequest,
  type FamilySaveRequest,
  type ContractSaveRequest,
} from '../../api/hr'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const saving = ref(false)
const isEdit = ref(false)
const isNew = computed(() => route.path.endsWith('/create'))

const detail = ref<EmployeeDetailVO | null>(null)
const form = reactive<EmployeeCreateRequest>({
  employeeCode: '',
  realName: '',
  gender: '',
  idCardNo: '',
  birthday: '',
  age: undefined,
  nation: '',
  politicalStatus: '',
  hometown: '',
  maritalStatus: '',
  phone: '',
  email: '',
  graduateSchool: '',
  highestEducation: '',
  workStartDate: '',
  homeAddress: '',
  emergencyContact: '',
  emergencyContactPhone: '',
  bankCardNo: '',
  employmentStatus: 'ONBOARD',
  createSystemAccount: 0,
  systemUsername: '',
  systemPasswordPlain: '',
  orgId: undefined,
  jobTitle: '',
  remark: '',
})

const activeTab = ref('basic')

// Job Info
const jobInfoForm = reactive({
  employeeId: 0,
  joinDate: '',
  formalDate: '',
  workUnit: '',
  workMode: '',
  department: '',
  rankLevel: '',
  jobCategory: '',
  position: '',
  sortNo: undefined as number | undefined,
  is编制: 0,
})

// Sub-table dialog states
const certDialogVisible = ref(false)
const certEditId = ref<number | null>(null)
const certForm = reactive({
  certificateName: '',
  certificateNo: '',
  issueDate: '',
  certificateType: '',
  issueOrg: '',
  isPermanent: 0,
  expireDate: '',
})

const assessDialogVisible = ref(false)
const assessEditId = ref<number | null>(null)
const assessForm = reactive({
  assessmentMonth: '',
  assessmentScore: undefined as number | undefined,
  assessmentGrade: '',
  remark: '',
})

const growthDialogVisible = ref(false)
const growthEditId = ref<number | null>(null)
const growthForm = reactive({
  startDate: '',
  endDate: '',
  workName: '',
})

const familyDialogVisible = ref(false)
const familyEditId = ref<number | null>(null)
const familyForm = reactive({
  relation: '',
  name: '',
  birthday: '',
  idCardNo: '',
  politicalStatus: '',
  workUnitPosition: '',
})

const contractDialogVisible = ref(false)
const contractEditId = ref<number | null>(null)
const contractForm = reactive({
  contractName: '',
  contractNo: '',
  contractStartDate: '',
  contractEndDate: '',
  remark: '',
})

const changeDialogVisible = ref(false)
const changeEditId = ref<number | null>(null)
const changeForm = reactive({
  currentDepartment: '',
  currentPosition: '',
  currentRankLevel: '',
  originalDepartment: '',
  originalPosition: '',
  originalRankLevel: '',
  reportDate: '',
  changeType: '',
  changeReason: '',
})

// --- Fetch ---
const fetchDetail = async (id: number) => {
  loading.value = true
  try {
    detail.value = await employeeApi.detail(id)
    if (detail.value) {
      Object.assign(form, {
        employeeCode: detail.value.employeeCode,
        realName: detail.value.realName,
        gender: detail.value.gender || '',
        idCardNo: detail.value.idCardNo || '',
        birthday: detail.value.birthday || '',
        age: detail.value.age,
        nation: detail.value.nation || '',
        politicalStatus: detail.value.politicalStatus || '',
        hometown: detail.value.hometown || '',
        maritalStatus: detail.value.maritalStatus || '',
        phone: detail.value.phone || '',
        email: detail.value.email || '',
        graduateSchool: detail.value.graduateSchool || '',
        highestEducation: detail.value.highestEducation || '',
        workStartDate: detail.value.workStartDate || '',
        homeAddress: detail.value.homeAddress || '',
        emergencyContact: detail.value.emergencyContact || '',
        emergencyContactPhone: detail.value.emergencyContactPhone || '',
        bankCardNo: detail.value.bankCardNo || '',
        employmentStatus: detail.value.employmentStatus,
        createSystemAccount: detail.value.createSystemAccount || 0,
        systemUsername: detail.value.systemUsername || '',
        orgId: detail.value.orgId,
        jobTitle: detail.value.jobTitle || '',
        remark: detail.value.remark || '',
      })

      if (detail.value.jobInfo) {
        Object.assign(jobInfoForm, {
          employeeId: detail.value.jobInfo.employeeId,
          joinDate: detail.value.jobInfo.joinDate || '',
          formalDate: detail.value.jobInfo.formalDate || '',
          workUnit: detail.value.jobInfo.workUnit || '',
          workMode: detail.value.jobInfo.workMode || '',
          department: detail.value.jobInfo.department || '',
          rankLevel: detail.value.jobInfo.rankLevel || '',
          jobCategory: detail.value.jobInfo.jobCategory || '',
          position: detail.value.jobInfo.position || '',
          sortNo: detail.value.jobInfo.sortNo,
          is编制: detail.value.jobInfo.is编制 || 0,
        })
      } else {
        Object.assign(jobInfoForm, {
          employeeId: id,
          joinDate: '',
          formalDate: '',
          workUnit: '',
          workMode: '',
          department: '',
          rankLevel: '',
          jobCategory: '',
          position: '',
          sortNo: undefined,
          is编制: 0,
        })
      }
    }
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '加载失败')
  } finally {
    loading.value = false
  }
}

// --- Main Save ---
const handleSave = async () => {
  saving.value = true
  try {
    if (isNew.value) {
      await employeeApi.create(form as EmployeeCreateRequest)
      ElMessage.success('创建成功')
    } else {
      const id = Number(route.params.id)
      const updateData: EmployeeUpdateRequest = { ...form }
      delete (updateData as any).employeeCode
      await employeeApi.update(id, updateData)
      ElMessage.success('保存成功')
    }
    isEdit.value = false
    if (isNew.value) {
      router.back()
    } else {
      await fetchDetail(Number(route.params.id))
    }
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '保存失败')
  } finally {
    saving.value = false
  }
}

// --- Job Info Save ---
const handleSaveJobInfo = async () => {
  if (!detail.value) return
  saving.value = true
  try {
    await employeeApi.saveJobInfo({
      employeeId: detail.value.id,
      employeeCode: detail.value.employeeCode,
      joinDate: jobInfoForm.joinDate || undefined,
      formalDate: jobInfoForm.formalDate || undefined,
      workUnit: jobInfoForm.workUnit || undefined,
      workMode: jobInfoForm.workMode || undefined,
      department: jobInfoForm.department || undefined,
      rankLevel: jobInfoForm.rankLevel || undefined,
      jobCategory: jobInfoForm.jobCategory || undefined,
      position: jobInfoForm.position || undefined,
      sortNo: jobInfoForm.sortNo,
      is编制: jobInfoForm.is编制,
    })
    ElMessage.success('任职信息保存成功')
    await fetchDetail(Number(route.params.id))
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '保存失败')
  } finally {
    saving.value = false
  }
}

// --- Certificate CRUD ---
const openCertCreate = () => {
  certEditId.value = null
  Object.assign(certForm, {
    certificateName: '',
    certificateNo: '',
    issueDate: '',
    certificateType: '',
    issueOrg: '',
    isPermanent: 0,
    expireDate: '',
  })
  certDialogVisible.value = true
}

const openCertEdit = (row: CertificateVO) => {
  certEditId.value = row.id
  Object.assign(certForm, {
    certificateName: row.certificateName,
    certificateNo: row.certificateNo || '',
    issueDate: row.issueDate ? row.issueDate.split('T')[0] : '',
    certificateType: row.certificateType || '',
    issueOrg: row.issueOrg || '',
    isPermanent: row.isPermanent || 0,
    expireDate: row.expireDate ? row.expireDate.split('T')[0] : '',
  })
  certDialogVisible.value = true
}

const handleSaveCert = async () => {
  if (!detail.value || !certForm.certificateName) {
    ElMessage.error('请填写证书名称')
    return
  }
  saving.value = true
  try {
    const data: CertificateSaveRequest = {
      id: certEditId.value || undefined,
      employeeId: detail.value.id,
      certificateName: certForm.certificateName,
      certificateNo: certForm.certificateNo || undefined,
      issueDate: certForm.issueDate || undefined,
      certificateType: certForm.certificateType || undefined,
      issueOrg: certForm.issueOrg || undefined,
      isPermanent: certForm.isPermanent,
      expireDate: certForm.expireDate || undefined,
    }
    await employeeApi.saveCertificate(data)
    ElMessage.success(certEditId.value ? '更新成功' : '添加成功')
    certDialogVisible.value = false
    await fetchDetail(Number(route.params.id))
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '保存失败')
  } finally {
    saving.value = false
  }
}

const handleDeleteCert = async (row: CertificateVO) => {
  try {
    await ElMessageBox.confirm(`确定要删除证书"${row.certificateName}"吗？`, '删除确认', { type: 'warning' })
    await employeeApi.deleteCertificate(row.id)
    ElMessage.success('删除成功')
    await fetchDetail(Number(route.params.id))
  } catch (error) {
    if (error instanceof Error && error.message !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// --- Assessment CRUD ---
const openAssessCreate = () => {
  assessEditId.value = null
  Object.assign(assessForm, {
    assessmentMonth: '',
    assessmentScore: undefined,
    assessmentGrade: '',
    remark: '',
  })
  assessDialogVisible.value = true
}

const openAssessEdit = (row: AssessmentVO) => {
  assessEditId.value = row.id
  Object.assign(assessForm, {
    assessmentMonth: row.assessmentMonth ? row.assessmentMonth.split('T')[0] : '',
    assessmentScore: row.assessmentScore,
    assessmentGrade: row.assessmentGrade || '',
    remark: row.remark || '',
  })
  assessDialogVisible.value = true
}

const handleSaveAssess = async () => {
  if (!detail.value) return
  saving.value = true
  try {
    const data: AssessmentSaveRequest = {
      id: assessEditId.value || undefined,
      employeeId: detail.value.id,
      assessmentMonth: assessForm.assessmentMonth || undefined,
      assessmentScore: assessForm.assessmentScore,
      assessmentGrade: assessForm.assessmentGrade || undefined,
      remark: assessForm.remark || undefined,
    }
    await employeeApi.saveAssessment(data)
    ElMessage.success(assessEditId.value ? '更新成功' : '添加成功')
    assessDialogVisible.value = false
    await fetchDetail(Number(route.params.id))
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '保存失败')
  } finally {
    saving.value = false
  }
}

const handleDeleteAssess = async (row: AssessmentVO) => {
  try {
    await ElMessageBox.confirm(`确定要删除该考核记录吗？`, '删除确认', { type: 'warning' })
    await employeeApi.deleteAssessment(row.id)
    ElMessage.success('删除成功')
    await fetchDetail(Number(route.params.id))
  } catch (error) {
    if (error instanceof Error && error.message !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// --- Growth CRUD ---
const openGrowthCreate = () => {
  growthEditId.value = null
  Object.assign(growthForm, {
    startDate: '',
    endDate: '',
    workName: '',
  })
  growthDialogVisible.value = true
}

const openGrowthEdit = (row: GrowthVO) => {
  growthEditId.value = row.id
  Object.assign(growthForm, {
    startDate: row.startDate ? row.startDate.split('T')[0] : '',
    endDate: row.endDate ? row.endDate.split('T')[0] : '',
    workName: row.workName || '',
  })
  growthDialogVisible.value = true
}

const handleSaveGrowth = async () => {
  if (!detail.value || !growthForm.workName) {
    ElMessage.error('请填写工作/学习名称')
    return
  }
  saving.value = true
  try {
    const data: GrowthSaveRequest = {
      id: growthEditId.value || undefined,
      employeeId: detail.value.id,
      startDate: growthForm.startDate || undefined,
      endDate: growthForm.endDate || undefined,
      workName: growthForm.workName,
    }
    await employeeApi.saveGrowth(data)
    ElMessage.success(growthEditId.value ? '更新成功' : '添加成功')
    growthDialogVisible.value = false
    await fetchDetail(Number(route.params.id))
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '保存失败')
  } finally {
    saving.value = false
  }
}

const handleDeleteGrowth = async (row: GrowthVO) => {
  try {
    await ElMessageBox.confirm(`确定要删除该成长记录吗？`, '删除确认', { type: 'warning' })
    await employeeApi.deleteGrowth(row.id)
    ElMessage.success('删除成功')
    await fetchDetail(Number(route.params.id))
  } catch (error) {
    if (error instanceof Error && error.message !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// --- Family CRUD ---
const openFamilyCreate = () => {
  familyEditId.value = null
  Object.assign(familyForm, {
    relation: '',
    name: '',
    birthday: '',
    idCardNo: '',
    politicalStatus: '',
    workUnitPosition: '',
  })
  familyDialogVisible.value = true
}

const openFamilyEdit = (row: FamilyVO) => {
  familyEditId.value = row.id
  Object.assign(familyForm, {
    relation: row.relation || '',
    name: row.name || '',
    birthday: row.birthday ? row.birthday.split('T')[0] : '',
    idCardNo: row.idCardNo || '',
    politicalStatus: row.politicalStatus || '',
    workUnitPosition: row.workUnitPosition || '',
  })
  familyDialogVisible.value = true
}

const handleSaveFamily = async () => {
  if (!detail.value || !familyForm.name) {
    ElMessage.error('请填写姓名')
    return
  }
  saving.value = true
  try {
    const data: FamilySaveRequest = {
      id: familyEditId.value || undefined,
      employeeId: detail.value.id,
      relation: familyForm.relation || undefined,
      name: familyForm.name,
      birthday: familyForm.birthday || undefined,
      idCardNo: familyForm.idCardNo || undefined,
      politicalStatus: familyForm.politicalStatus || undefined,
      workUnitPosition: familyForm.workUnitPosition || undefined,
    }
    await employeeApi.saveFamily(data)
    ElMessage.success(familyEditId.value ? '更新成功' : '添加成功')
    familyDialogVisible.value = false
    await fetchDetail(Number(route.params.id))
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '保存失败')
  } finally {
    saving.value = false
  }
}

const handleDeleteFamily = async (row: FamilyVO) => {
  try {
    await ElMessageBox.confirm(`确定要删除家庭成员"${row.name}"吗？`, '删除确认', { type: 'warning' })
    await employeeApi.deleteFamily(row.id)
    ElMessage.success('删除成功')
    await fetchDetail(Number(route.params.id))
  } catch (error) {
    if (error instanceof Error && error.message !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// --- Contract CRUD ---
const openContractCreate = () => {
  contractEditId.value = null
  Object.assign(contractForm, {
    contractName: '',
    contractNo: '',
    contractStartDate: '',
    contractEndDate: '',
    remark: '',
  })
  contractDialogVisible.value = true
}

const openContractEdit = (row: ContractVO) => {
  contractEditId.value = row.id
  Object.assign(contractForm, {
    contractName: row.contractName || '',
    contractNo: row.contractNo || '',
    contractStartDate: row.contractStartDate ? row.contractStartDate.split('T')[0] : '',
    contractEndDate: row.contractEndDate ? row.contractEndDate.split('T')[0] : '',
    remark: row.remark || '',
  })
  contractDialogVisible.value = true
}

const handleSaveContract = async () => {
  if (!detail.value || !contractForm.contractName) {
    ElMessage.error('请填写合同名称')
    return
  }
  saving.value = true
  try {
    const data: ContractSaveRequest = {
      id: contractEditId.value || undefined,
      employeeId: detail.value.id,
      contractName: contractForm.contractName,
      contractNo: contractForm.contractNo || undefined,
      contractStartDate: contractForm.contractStartDate || undefined,
      contractEndDate: contractForm.contractEndDate || undefined,
      remark: contractForm.remark || undefined,
    }
    await employeeApi.saveContract(data)
    ElMessage.success(contractEditId.value ? '更新成功' : '添加成功')
    contractDialogVisible.value = false
    await fetchDetail(Number(route.params.id))
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '保存失败')
  } finally {
    saving.value = false
  }
}

const handleDeleteContract = async (row: ContractVO) => {
  try {
    await ElMessageBox.confirm(`确定要删除合同"${row.contractName}"吗？`, '删除确认', { type: 'warning' })
    await employeeApi.deleteContract(row.id)
    ElMessage.success('删除成功')
    await fetchDetail(Number(route.params.id))
  } catch (error) {
    if (error instanceof Error && error.message !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// --- Change Add/Edit ---
const openChangeCreate = () => {
  changeEditId.value = null
  Object.assign(changeForm, {
    currentDepartment: '',
    currentPosition: '',
    currentRankLevel: '',
    originalDepartment: '',
    originalPosition: '',
    originalRankLevel: '',
    reportDate: '',
    changeType: '',
    changeReason: '',
  })
  changeDialogVisible.value = true
}

const openChangeEdit = (row: ChangeVO) => {
  changeEditId.value = row.id
  Object.assign(changeForm, {
    currentDepartment: row.currentDepartment || '',
    currentPosition: row.currentPosition || '',
    currentRankLevel: row.currentRankLevel || '',
    originalDepartment: row.originalDepartment || '',
    originalPosition: row.originalPosition || '',
    originalRankLevel: row.originalRankLevel || '',
    reportDate: row.reportDate ? row.reportDate.split('T')[0] : '',
    changeType: row.changeType || '',
    changeReason: row.changeReason || '',
  })
  changeDialogVisible.value = true
}

const handleSaveChange = async () => {
  if (!detail.value) return
  saving.value = true
  try {
    await employeeApi.saveChange({
      id: changeEditId.value || undefined,
      employeeId: detail.value.id,
      currentDepartment: changeForm.currentDepartment || undefined,
      currentPosition: changeForm.currentPosition || undefined,
      currentRankLevel: changeForm.currentRankLevel || undefined,
      originalDepartment: changeForm.originalDepartment || undefined,
      originalPosition: changeForm.originalPosition || undefined,
      originalRankLevel: changeForm.originalRankLevel || undefined,
      reportDate: changeForm.reportDate || undefined,
      changeType: changeForm.changeType || undefined,
      changeReason: changeForm.changeReason || undefined,
    })
    ElMessage.success(changeEditId.value ? '更新成功' : '添加成功')
    changeDialogVisible.value = false
    await fetchDetail(Number(route.params.id))
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '保存失败')
  } finally {
    saving.value = false
  }
}

// --- Utils ---
const handleCancel = () => {
  if (isNew.value) {
    router.back()
  } else {
    isEdit.value = false
    if (detail.value) {
      void fetchDetail(Number(route.params.id))
    }
  }
}

const goBack = () => {
  router.push('/hr/employees')
}

const formatDate = (dateStr: string | undefined | null) => {
  if (!dateStr) return ''
  return dateStr.split('T')[0]
}

const computedAge = () => {
  if (form.birthday) {
    const birth = new Date(form.birthday)
    const now = new Date()
    return now.getFullYear() - birth.getFullYear()
  }
  return undefined
}

watch(() => form.birthday, () => {
  if (form.birthday && !isEdit.value) {
    form.age = computedAge()
  }
})

onMounted(() => {
  if (!isNew.value) {
    void fetchDetail(Number(route.params.id))
  } else {
    isEdit.value = true
  }
})
</script>

<template>
  <section class="page-shell">
    <section class="card">
      <div class="card__section page-intro">
        <div class="page-intro__copy">
          <span class="page-intro__eyebrow">人事管理 / 员工档案</span>
          <div class="detail-heading">
            <el-button :icon="ArrowLeft" plain @click="goBack">返回</el-button>
            <h2 class="page-intro__title">{{ isNew ? '新增员工' : (isEdit ? '编辑员工' : '员工详情') }}</h2>
          </div>
          <p class="page-intro__desc">统一维护员工档案、任职信息和相关从表记录。新增、编辑和查看共用同一套详情页结构。</p>
        </div>

        <div class="page-intro__actions">
          <template v-if="!isEdit && !isNew">
            <el-button type="primary" @click="isEdit = true">编辑</el-button>
          </template>
          <template v-else>
            <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
            <el-button @click="handleCancel">取消</el-button>
          </template>
        </div>
      </div>

      <div class="card__section page-stack">
      <el-tabs v-model="activeTab" class="page-tabs">
        <!-- 档案信息 -->
        <el-tab-pane label="档案信息" name="basic">
          <el-form :model="form" label-width="120px" :disabled="!isEdit">
            <div class="form-section">
              <h3>基本信息</h3>
              <el-row :gutter="16">
                <el-col :span="8">
                  <el-form-item label="员工编号" required>
                    <el-input v-model="form.employeeCode" :disabled="!isNew" />
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="姓名" required>
                    <el-input v-model="form.realName" />
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="性别">
                    <el-select v-model="form.gender" placeholder="请选择">
                      <el-option label="男" value="MALE" />
                      <el-option label="女" value="FEMALE" />
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="16">
                <el-col :span="8">
                  <el-form-item label="身份证号">
                    <el-input v-model="form.idCardNo" />
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="出生年月">
                    <el-date-picker v-model="form.birthday" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="年龄">
                    <el-input v-model.number="form.age" type="number" />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="16">
                <el-col :span="8">
                  <el-form-item label="民族">
                    <el-input v-model="form.nation" />
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="政治面貌">
                    <el-input v-model="form.politicalStatus" />
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="籍贯">
                    <el-input v-model="form.hometown" />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="16">
                <el-col :span="8">
                  <el-form-item label="婚姻状况">
                    <el-select v-model="form.maritalStatus" placeholder="请选择">
                      <el-option label="未婚" value="UNMARRIED" />
                      <el-option label="已婚" value="MARRIED" />
                      <el-option label="离异" value="DIVORCED" />
                      <el-option label="丧偶" value="WIDOWED" />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="手机号" required>
                    <el-input v-model="form.phone" />
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="邮箱">
                    <el-input v-model="form.email" />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="16">
                <el-col :span="8">
                  <el-form-item label="毕业学校">
                    <el-input v-model="form.graduateSchool" />
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="最高学历">
                    <el-input v-model="form.highestEducation" />
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="参加工作时间">
                    <el-date-picker v-model="form.workStartDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="16">
                <el-col :span="12">
                  <el-form-item label="家庭住址">
                    <el-input v-model="form.homeAddress" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="人员状态">
                    <el-select v-model="form.employmentStatus">
                      <el-option label="在职" value="ONBOARD" />
                      <el-option label="离职" value="OFFBOARD" />
                      <el-option label="退休" value="RETIRED" />
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="16">
                <el-col :span="8">
                  <el-form-item label="紧急联系人">
                    <el-input v-model="form.emergencyContact" />
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="紧急联系人电话">
                    <el-input v-model="form.emergencyContactPhone" />
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="银行卡号">
                    <el-input v-model="form.bankCardNo" />
                  </el-form-item>
                </el-col>
              </el-row>
            </div>

            <div class="form-section">
              <h3>任职信息</h3>
              <el-row :gutter="16">
                <el-col :span="8">
                  <el-form-item label="所属部门">
                    <el-input v-model.number="form.orgId" type="number" placeholder="部门ID" />
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="职位">
                    <el-input v-model="form.jobTitle" />
                  </el-form-item>
                </el-col>
              </el-row>
            </div>

            <div class="form-section">
              <h3>系统账号</h3>
              <el-row :gutter="16">
                <el-col :span="8">
                  <el-form-item label="开通系统账号">
                    <el-switch v-model="form.createSystemAccount" :active-value="1" :inactive-value="0" />
                  </el-form-item>
                </el-col>
                <template v-if="form.createSystemAccount === 1">
                  <el-col :span="8">
                    <el-form-item label="账号">
                      <el-input v-model="form.systemUsername" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item label="密码">
                      <el-input v-model="form.systemPasswordPlain" type="password" show-password />
                    </el-form-item>
                  </el-col>
                </template>
              </el-row>
            </div>

            <div class="form-section">
              <h3>备注</h3>
              <el-form-item label="备注">
                <el-input v-model="form.remark" type="textarea" :rows="3" />
              </el-form-item>
            </div>
          </el-form>
        </el-tab-pane>

        <!-- 任职信息 -->
        <el-tab-pane v-if="!isNew" label="任职信息" name="job">
          <el-form :model="jobInfoForm" label-width="120px" :disabled="!isEdit">
            <el-row :gutter="16">
              <el-col :span="8">
                <el-form-item label="入职日期">
                  <el-date-picker v-model="jobInfoForm.joinDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="转正日期">
                  <el-date-picker v-model="jobInfoForm.formalDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="所属单位">
                  <el-input v-model="jobInfoForm.workUnit" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="16">
              <el-col :span="8">
                <el-form-item label="部门">
                  <el-input v-model="jobInfoForm.department" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="职级">
                  <el-input v-model="jobInfoForm.rankLevel" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="职位">
                  <el-input v-model="jobInfoForm.position" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
          <div v-if="isEdit" style="margin-top: 16px">
            <el-button type="primary" :loading="saving" @click="handleSaveJobInfo">保存任职信息</el-button>
          </div>
        </el-tab-pane>

        <!-- 资格证书 -->
        <el-tab-pane v-if="!isNew" label="资格证书" name="cert">
          <div class="table-toolbar">
            <el-button v-if="isEdit" type="primary" :icon="Plus" @click="openCertCreate">新增</el-button>
          </div>
          <el-table :data="detail?.certificates || []" border stripe style="width: 100%">
            <el-table-column prop="certificateName" label="证书名称" />
            <el-table-column prop="certificateNo" label="证书编号" />
            <el-table-column prop="issueDate" label="发证日期">
              <template #default="{ row }">{{ formatDate(row.issueDate) }}</template>
            </el-table-column>
            <el-table-column prop="certificateType" label="证书类型" />
            <el-table-column prop="issueOrg" label="发证单位" />
            <el-table-column prop="isPermanent" label="是否永久">
              <template #default="{ row }">{{ row.isPermanent ? '是' : '否' }}</template>
            </el-table-column>
            <el-table-column v-if="isEdit" label="操作" width="120">
              <template #default="{ row }">
                <el-button link type="primary" @click="openCertEdit(row)">编辑</el-button>
                <el-button link type="danger" @click="handleDeleteCert(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 考核记录 -->
        <el-tab-pane v-if="!isNew" label="考核记录" name="assess">
          <div class="table-toolbar">
            <el-button v-if="isEdit" type="primary" :icon="Plus" @click="openAssessCreate">新增</el-button>
          </div>
          <el-table :data="detail?.assessments || []" border stripe style="width: 100%">
            <el-table-column prop="assessmentMonth" label="考核年月">
              <template #default="{ row }">{{ formatDate(row.assessmentMonth) }}</template>
            </el-table-column>
            <el-table-column prop="assessmentScore" label="考核分数" />
            <el-table-column prop="assessmentGrade" label="考核等级" />
            <el-table-column prop="remark" label="备注" />
            <el-table-column v-if="isEdit" label="操作" width="120">
              <template #default="{ row }">
                <el-button link type="primary" @click="openAssessEdit(row)">编辑</el-button>
                <el-button link type="danger" @click="handleDeleteAssess(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 工作成长记录 -->
        <el-tab-pane v-if="!isNew" label="工作成长记录" name="growth">
          <div class="table-toolbar">
            <el-button v-if="isEdit" type="primary" :icon="Plus" @click="openGrowthCreate">新增</el-button>
          </div>
          <el-table :data="detail?.growthRecords || []" border stripe style="width: 100%">
            <el-table-column prop="startDate" label="开始时间">
              <template #default="{ row }">{{ formatDate(row.startDate) }}</template>
            </el-table-column>
            <el-table-column prop="endDate" label="结束时间">
              <template #default="{ row }">{{ formatDate(row.endDate) }}</template>
            </el-table-column>
            <el-table-column prop="workName" label="工作/学习名称" />
            <el-table-column v-if="isEdit" label="操作" width="120">
              <template #default="{ row }">
                <el-button link type="primary" @click="openGrowthEdit(row)">编辑</el-button>
                <el-button link type="danger" @click="handleDeleteGrowth(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 家庭成员 -->
        <el-tab-pane v-if="!isNew" label="家庭成员" name="family">
          <div class="table-toolbar">
            <el-button v-if="isEdit" type="primary" :icon="Plus" @click="openFamilyCreate">新增</el-button>
          </div>
          <el-table :data="detail?.familyMembers || []" border stripe style="width: 100%">
            <el-table-column prop="relation" label="称谓" />
            <el-table-column prop="name" label="姓名" />
            <el-table-column prop="birthday" label="出生年月">
              <template #default="{ row }">{{ formatDate(row.birthday) }}</template>
            </el-table-column>
            <el-table-column prop="politicalStatus" label="政治面貌" />
            <el-table-column prop="workUnitPosition" label="工作单位及职务" />
            <el-table-column v-if="isEdit" label="操作" width="120">
              <template #default="{ row }">
                <el-button link type="primary" @click="openFamilyEdit(row)">编辑</el-button>
                <el-button link type="danger" @click="handleDeleteFamily(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 人事合同 -->
        <el-tab-pane v-if="!isNew" label="人事合同" name="contract">
          <div class="table-toolbar">
            <el-button v-if="isEdit" type="primary" :icon="Plus" @click="openContractCreate">新增</el-button>
          </div>
          <el-table :data="detail?.contracts || []" border stripe style="width: 100%">
            <el-table-column prop="contractName" label="合同名称" />
            <el-table-column prop="contractNo" label="合同编号" />
            <el-table-column prop="contractStartDate" label="开始日期">
              <template #default="{ row }">{{ formatDate(row.contractStartDate) }}</template>
            </el-table-column>
            <el-table-column prop="contractEndDate" label="结束日期">
              <template #default="{ row }">{{ formatDate(row.contractEndDate) }}</template>
            </el-table-column>
            <el-table-column prop="remark" label="备注" />
            <el-table-column v-if="isEdit" label="操作" width="120">
              <template #default="{ row }">
                <el-button link type="primary" @click="openContractEdit(row)">编辑</el-button>
                <el-button link type="danger" @click="handleDeleteContract(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 异动记录 -->
        <el-tab-pane v-if="!isNew" label="异动记录" name="change">
          <div class="table-toolbar">
            <el-button v-if="isEdit" type="primary" :icon="Plus" @click="openChangeCreate">新增</el-button>
          </div>
          <el-table :data="detail?.changeRecords || []" border stripe style="width: 100%">
            <el-table-column prop="changeType" label="异动类型" />
            <el-table-column prop="reportDate" label="异动日期">
              <template #default="{ row }">{{ formatDate(row.reportDate) }}</template>
            </el-table-column>
            <el-table-column prop="originalDepartment" label="原部门" />
            <el-table-column prop="originalPosition" label="原职位" />
            <el-table-column prop="currentDepartment" label="现部门" />
            <el-table-column prop="currentPosition" label="现职位" />
            <el-table-column prop="changeReason" label="异动原因" />
            <el-table-column v-if="isEdit" label="操作" width="120">
              <template #default="{ row }">
                <el-button link type="primary" @click="openChangeEdit(row)">编辑</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
      </div>
    </section>

    <!-- Certificate Dialog -->
    <el-dialog v-model="certDialogVisible" :title="certEditId ? '编辑资格证书' : '新增资格证书'" width="600px">
      <el-form :model="certForm" label-width="100px">
        <el-form-item label="证书名称" required>
          <el-input v-model="certForm.certificateName" />
        </el-form-item>
        <el-form-item label="证书编号">
          <el-input v-model="certForm.certificateNo" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="发证日期">
              <el-date-picker v-model="certForm.issueDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="证书类型">
              <el-input v-model="certForm.certificateType" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="发证单位">
          <el-input v-model="certForm.issueOrg" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="是否永久">
              <el-select v-model="certForm.isPermanent" style="width: 100%">
                <el-option label="否" :value="0" />
                <el-option label="是" :value="1" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="失效日期">
              <el-date-picker v-model="certForm.expireDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="certDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSaveCert">保存</el-button>
      </template>
    </el-dialog>

    <!-- Assessment Dialog -->
    <el-dialog v-model="assessDialogVisible" :title="assessEditId ? '编辑考核记录' : '新增考核记录'" width="600px">
      <el-form :model="assessForm" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="考核年月">
              <el-date-picker v-model="assessForm.assessmentMonth" type="month" value-format="YYYY-MM" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="考核分数">
              <el-input-number v-model="assessForm.assessmentScore" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="考核等级">
          <el-input v-model="assessForm.assessmentGrade" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="assessForm.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assessDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSaveAssess">保存</el-button>
      </template>
    </el-dialog>

    <!-- Growth Dialog -->
    <el-dialog v-model="growthDialogVisible" :title="growthEditId ? '编辑成长记录' : '新增成长记录'" width="600px">
      <el-form :model="growthForm" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="开始时间">
              <el-date-picker v-model="growthForm.startDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间">
              <el-date-picker v-model="growthForm.endDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="工作/学习名称" required>
          <el-input v-model="growthForm.workName" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="growthDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSaveGrowth">保存</el-button>
      </template>
    </el-dialog>

    <!-- Family Dialog -->
    <el-dialog v-model="familyDialogVisible" :title="familyEditId ? '编辑家庭成员' : '新增家庭成员'" width="600px">
      <el-form :model="familyForm" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="称谓">
              <el-input v-model="familyForm.relation" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" required>
              <el-input v-model="familyForm.name" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="出生年月">
              <el-date-picker v-model="familyForm.birthday" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="政治面貌">
              <el-input v-model="familyForm.politicalStatus" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="工作单位及职务">
          <el-input v-model="familyForm.workUnitPosition" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="familyDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSaveFamily">保存</el-button>
      </template>
    </el-dialog>

    <!-- Contract Dialog -->
    <el-dialog v-model="contractDialogVisible" :title="contractEditId ? '编辑合同' : '新增合同'" width="600px">
      <el-form :model="contractForm" label-width="100px">
        <el-form-item label="合同名称" required>
          <el-input v-model="contractForm.contractName" />
        </el-form-item>
        <el-form-item label="合同编号">
          <el-input v-model="contractForm.contractNo" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="开始日期">
              <el-date-picker v-model="contractForm.contractStartDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束日期">
              <el-date-picker v-model="contractForm.contractEndDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="contractForm.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="contractDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSaveContract">保存</el-button>
      </template>
    </el-dialog>

    <!-- Change Dialog -->
    <el-dialog v-model="changeDialogVisible" :title="changeEditId ? '编辑异动记录' : '新增异动记录'" width="700px">
      <el-form :model="changeForm" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="异动类型">
              <el-input v-model="changeForm.changeType" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="异动日期">
              <el-date-picker v-model="changeForm.reportDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-divider>原信息</el-divider>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="原部门">
              <el-input v-model="changeForm.originalDepartment" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="原职位">
              <el-input v-model="changeForm.originalPosition" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="原职级">
              <el-input v-model="changeForm.originalRankLevel" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-divider>现信息</el-divider>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="现部门">
              <el-input v-model="changeForm.currentDepartment" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="现职位">
              <el-input v-model="changeForm.currentPosition" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="现职级">
              <el-input v-model="changeForm.currentRankLevel" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="异动原因">
          <el-input v-model="changeForm.changeReason" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="changeDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSaveChange">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<style scoped>
.detail-heading {
  display: flex;
  align-items: center;
  gap: 16px;
}

.detail-heading :deep(.el-button) {
  flex-shrink: 0;
}

.form-section {
  margin-bottom: 24px;
}

.form-section h3 {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid #eee;
}

.table-toolbar {
  margin-bottom: 12px;
}
</style>
