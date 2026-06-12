<script setup lang="ts">
import { computed } from 'vue'
import type { CustomerArchiveForm } from './customerModels'

const props = defineProps<{
  form: CustomerArchiveForm
}>()

const bankOptions = ['中国银行', '工商银行', '建设银行', '农业银行', '招商银行', '平安银行']
const bizStatusOptions = [
  { label: '初始化', value: 'INIT' },
  { label: '跟进中', value: 'FOLLOWING' },
  { label: '已签约', value: 'SIGNED' },
]

const customerAge = computed(() => {
  if (!props.form.birthday) return null
  const birthday = new Date(props.form.birthday)
  const today = new Date()
  const age = today.getFullYear() - birthday.getFullYear()
  const monthDiff = today.getMonth() - birthday.getMonth()
  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthday.getDate())) {
    return age - 1
  }
  return age
})

const formatAuditStatus = (status: string | undefined) => {
  if (status === 'PENDING') return '待审核'
  if (status === 'APPROVED') return '已通过'
  if (status === 'REJECTED') return '已驳回'
  return status || '-'
}
</script>

<template>
  <div class="card__section">
    <el-form label-position="top">
      <div class="archive-grid">
        <el-form-item label="姓名" required>
          <el-input v-model="form.customerName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="性别" required>
          <el-radio-group v-model="form.gender">
            <el-radio value="MALE">男</el-radio>
            <el-radio value="FEMALE">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="客户资料原件" class="archive-grid__full">
          <slot name="archive-upload" />
        </el-form-item>
        <el-form-item label="身份证号">
          <el-input v-model="form.idCard" placeholder="请输入身份证号" />
        </el-form-item>
        <el-form-item label="出生年月">
          <el-date-picker v-model="form.birthday" type="date" value-format="YYYY-MM-DD" placeholder="请选择日期" />
        </el-form-item>
        <el-form-item label="年龄">
          <el-input :model-value="customerAge == null ? '' : String(customerAge)" disabled />
        </el-form-item>
        <el-form-item label="税务登记是否正常">
          <el-radio-group v-model="form.taxRegistrationNormal">
            <el-radio :value="true">是</el-radio>
            <el-radio :value="false">否</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="企业名称">
          <el-input v-model="form.companyName" placeholder="请输入企业名称" />
        </el-form-item>
        <el-form-item label="统一社会信用代码">
          <el-input v-model="form.creditCode" placeholder="请输入统一社会信用代码" />
        </el-form-item>
        <el-form-item label="成立日期">
          <el-date-picker v-model="form.establishedDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择日期" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.mobile" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="经营地址">
          <el-input v-model="form.businessAddress" placeholder="请输入经营地址" />
        </el-form-item>
        <el-form-item label="行业">
          <el-input v-model="form.industry" placeholder="请输入行业" />
        </el-form-item>
        <el-form-item label="开户行">
          <el-select v-model="form.bankName" placeholder="请选择开户行" filterable allow-create default-first-option>
            <el-option v-for="bank in bankOptions" :key="bank" :label="bank" :value="bank" />
          </el-select>
        </el-form-item>
        <el-form-item label="银行开户账号">
          <el-input v-model="form.bankAccount" placeholder="请输入银行开户账号" />
        </el-form-item>
        <el-form-item label="推荐人">
          <el-input v-model="form.recommenderName" placeholder="请输入推荐人" />
        </el-form-item>
        <el-form-item label="推荐人返点">
          <el-input-number v-model="form.recommenderRate" :precision="2" controls-position="right" />
        </el-form-item>
        <el-form-item label="服务费">
          <el-input-number v-model="form.serviceFee" :precision="2" controls-position="right" />
        </el-form-item>
        <el-form-item label="审核状态">
          <div class="status-display">
            <el-tag :type="form.auditStatus === 'APPROVED' ? 'success' : form.auditStatus === 'REJECTED' ? 'danger' : 'warning'" size="small">
              {{ formatAuditStatus(form.auditStatus) }}
            </el-tag>
            <el-button text type="primary" size="small" @click="$emit('update-status')">变更</el-button>
          </div>
        </el-form-item>
        <el-form-item label="客户状态">
          <el-select v-model="form.bizStatus" placeholder="请选择客户状态">
            <el-option
              v-for="option in bizStatusOptions"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
        </el-form-item>
      </div>
    </el-form>
  </div>
</template>

<style scoped>
.archive-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}
.archive-grid__full {
  grid-column: 1 / -1;
}
.status-display {
  display: flex;
  align-items: center;
  gap: 10px;
}
</style>
