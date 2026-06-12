<script setup lang="ts">
import type { CustomerArchiveForm } from './customerModels'
import { appendRiskRecord, createEmptyCustomerArchiveForm } from './customerArchiveState'

const props = defineProps<{
  form: CustomerArchiveForm
}>()

const removeRiskRecord = (index: number) => {
  if (props.form.riskRecords.length === 1) {
    props.form.riskRecords[0] = createEmptyCustomerArchiveForm().riskRecords[0]
    return
  }
  props.form.riskRecords.splice(index, 1)
}
</script>

<template>
  <div class="form-section card__section">
    <div class="form-section__header">
      <div>
        <div class="form-section__title-row">
          <h3 class="form-section__title">客户风险综合评估</h3>
          <el-tag size="small" effect="plain">{{ form.riskRecords.length }} 条</el-tag>
        </div>
        <p class="form-section__desc">综合评分、龙信商和流量均支持手填，独立风险评估页新增的记录会同步到这里。</p>
      </div>
      <el-button plain @click="appendRiskRecord(form)">新增一条</el-button>
    </div>
    <div class="record-section">
      <el-table :data="form.riskRecords" table-layout="fixed">
        <el-table-column type="index" label="序号" width="70" />
        <el-table-column label="测试日期" min-width="140">
          <template #default="{ row }">
            <el-date-picker v-model="row.testDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择日期" />
          </template>
        </el-table-column>
        <el-table-column label="测试额度" min-width="140">
          <template #default="{ row }">
            <el-input-number v-model="row.testLimit" :precision="2" controls-position="right" />
          </template>
        </el-table-column>
        <el-table-column label="流量" min-width="140">
          <template #default="{ row }">
            <el-input-number v-model="row.trafficValue" :precision="2" controls-position="right" />
          </template>
        </el-table-column>
        <el-table-column label="综合评分" min-width="140">
          <template #default="{ row }">
            <el-input-number v-model="row.compositeScore" :precision="2" controls-position="right" />
          </template>
        </el-table-column>
        <el-table-column label="龙信商" min-width="140">
          <template #default="{ row }">
            <el-input-number v-model="row.thirdPartyScore" :precision="2" controls-position="right" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ $index }">
            <el-button text type="danger" @click="removeRiskRecord($index)">删除</el-button>
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
