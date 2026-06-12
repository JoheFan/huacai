<script setup lang="ts">
import type { CustomerArchiveForm } from './customerModels'
import { appendDebtRecord, createEmptyCustomerArchiveForm, derivePendingDebtAmount } from './customerArchiveState'

const props = defineProps<{
  form: CustomerArchiveForm
}>()

const formatAmountText = (value: number | null | undefined) =>
  (value ?? 0).toLocaleString('zh-CN', { maximumFractionDigits: 2 })

const removeDebtRecord = (index: number) => {
  if (props.form.debtRecords.length === 1) {
    props.form.debtRecords[0] = createEmptyCustomerArchiveForm().debtRecords[0]
    return
  }
  props.form.debtRecords.splice(index, 1)
}
</script>

<template>
  <div class="form-section card__section">
    <div class="form-section__header">
      <div>
        <div class="form-section__title-row">
          <h3 class="form-section__title">负债情况登记</h3>
          <el-tag size="small" effect="plain">{{ form.debtRecords.length }} 条</el-tag>
        </div>
        <p class="form-section__desc">总需还按负债总额显示，剩余需还额度自动用负债总额减已垫还回算。</p>
      </div>
      <el-button plain @click="appendDebtRecord(form)">新增一条</el-button>
    </div>
    <div class="record-section">
      <el-table :data="form.debtRecords" table-layout="fixed">
        <el-table-column type="index" label="序号" width="70" />
        <el-table-column label="负债类型" min-width="140">
          <template #default="{ row }">
            <el-input v-model="row.debtType" placeholder="请输入负债类型" />
          </template>
        </el-table-column>
        <el-table-column label="负债总额" min-width="140">
          <template #default="{ row }">
            <el-input-number v-model="row.debtAmount" :precision="2" controls-position="right" />
          </template>
        </el-table-column>
        <el-table-column label="总需还" min-width="140">
          <template #default="{ row }">
            <el-input :model-value="formatAmountText(row.debtAmount)" disabled />
          </template>
        </el-table-column>
        <el-table-column label="已垫还" min-width="140">
          <template #default="{ row }">
            <el-input-number v-model="row.advancePaidAmount" :precision="2" controls-position="right" />
          </template>
        </el-table-column>
        <el-table-column label="剩余需还额度" min-width="150">
          <template #default="{ row }">
            <el-input :model-value="formatAmountText(derivePendingDebtAmount(row.debtAmount, row.advancePaidAmount))" disabled />
          </template>
        </el-table-column>
        <el-table-column label="每期金额" min-width="140">
          <template #default="{ row }">
            <el-input-number v-model="row.installmentAmount" :precision="2" controls-position="right" />
          </template>
        </el-table-column>
        <el-table-column label="还款日" min-width="120">
          <template #default="{ row }">
            <el-input-number v-model="row.repaymentDay" :min="1" :max="31" controls-position="right" />
          </template>
        </el-table-column>
        <el-table-column label="备注" min-width="160">
          <template #default="{ row }">
            <el-input v-model="row.remark" placeholder="请输入备注" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ $index }">
            <el-button text type="danger" @click="removeDebtRecord($index)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<style scoped>
:deep(.el-table th .cell) {
  font-weight: 600;
  color: var(--hc-text);
}
</style>
