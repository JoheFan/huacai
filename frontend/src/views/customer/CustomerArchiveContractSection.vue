<script setup lang="ts">
import type { CustomerArchiveForm } from './customerModels'
import type { UploadRequestOptions, UploadUserFile } from 'element-plus'
import { deleteFile, uploadFile } from '../../api/file'

const props = defineProps<{
  form: CustomerArchiveForm
  contractAttachments: { id: number; fileName: string }[][]
}>()

const appendContractSection = () => {
  props.form.contractRecords.push({
    id: 0,
    customerName: '',
    companyName: '',
    creditCode: '',
    contractNo: '',
    contractName: '',
    remark: '',
    fileIds: [],
  })
  props.contractAttachments.push([])
}

const toUploadFiles = (attachments: { id: number; fileName: string }[]): UploadUserFile[] =>
  attachments.map((item) => ({
    name: item.fileName,
    uid: item.id,
    status: 'success',
  }))

const syncContractFileIds = (index: number) => {
  const attachments = props.contractAttachments[index] ?? []
  props.form.contractRecords[index].fileIds = attachments.map((item) => item.id)
}

const handleContractUpload = (index: number) => async (options: UploadRequestOptions) => {
  try {
    const result = await uploadFile(options.file as File)
    props.contractAttachments[index].push({ id: result.id, fileName: result.fileName })
    syncContractFileIds(index)
    options.onSuccess(result)
  } catch (error) {
    options.onError(error as never)
  }
}

const handleContractRemove = (index: number) => async (file: UploadUserFile) => {
  const fileId = Number(file.uid)
  props.contractAttachments[index] = (props.contractAttachments[index] ?? []).filter((item) => item.id !== fileId)
  syncContractFileIds(index)
  if (Number.isFinite(fileId) && fileId > 0) {
    await deleteFile(fileId).catch(() => undefined)
  }
}

const removeContractRecord = async (index: number) => {
  const attachments = props.contractAttachments[index] ?? []
  await Promise.all(attachments.map((attachment) => deleteFile(attachment.id).catch(() => undefined)))
  if (props.form.contractRecords.length === 1) {
    props.form.contractRecords[0] = {
      id: 0,
      customerName: '',
      companyName: '',
      creditCode: '',
      contractNo: '',
      contractName: '',
      remark: '',
      fileIds: [],
    }
    props.contractAttachments[0] = []
    return
  }
  props.form.contractRecords.splice(index, 1)
  props.contractAttachments.splice(index, 1)
}
</script>

<template>
  <div class="card__section">
    <div class="section-head">
      <div>
        <div class="section-head__title-row">
          <h3 class="section-title">合同管理</h3>
          <el-tag size="small" effect="plain">{{ form.contractRecords.length }} 条</el-tag>
        </div>
        <p class="section-head__description">合同记录仍挂在客户档案页内，不单独出左侧菜单。每条合同支持多个附件。</p>
      </div>
      <el-button plain @click="appendContractSection">新增一条</el-button>
    </div>
    <el-table :data="form.contractRecords" table-layout="fixed">
      <el-table-column type="index" label="序号" width="70" />
      <el-table-column label="客户名称" min-width="130">
        <template #default="{ row }">
          <el-input v-model="row.customerName" placeholder="请输入客户名称" />
        </template>
      </el-table-column>
      <el-table-column label="公司名称" min-width="160">
        <template #default="{ row }">
          <el-input v-model="row.companyName" placeholder="请输入公司名称" />
        </template>
      </el-table-column>
      <el-table-column label="统一社会信用代码" min-width="180">
        <template #default="{ row }">
          <el-input v-model="row.creditCode" placeholder="请输入统一社会信用代码" />
        </template>
      </el-table-column>
      <el-table-column label="合同编号" min-width="140">
        <template #default="{ row }">
          <el-input v-model="row.contractNo" placeholder="请输入合同编号" />
        </template>
      </el-table-column>
      <el-table-column label="合同名称" min-width="160">
        <template #default="{ row }">
          <el-input v-model="row.contractName" placeholder="请输入合同名称" />
        </template>
      </el-table-column>
      <el-table-column label="合同附件" min-width="180">
        <template #default="{ $index }">
          <el-upload
            :http-request="handleContractUpload($index)"
            :file-list="toUploadFiles(contractAttachments[$index] ?? [])"
            :on-remove="handleContractRemove($index)"
            multiple
          >
            <el-button plain>上传附件</el-button>
          </el-upload>
        </template>
      </el-table-column>
      <el-table-column label="备注" min-width="150">
        <template #default="{ row }">
          <el-input v-model="row.remark" placeholder="请输入备注" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="{ $index }">
          <el-button text type="danger" @click="removeContractRecord($index)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<style scoped>
.section-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}
.section-head__title-row {
  display: flex;
  align-items: center;
  gap: 10px;
}
.section-title {
  margin: 0;
}
.section-head__description {
  margin: 8px 0 0;
  color: var(--hc-text-soft);
  line-height: 1.6;
}
:deep(.el-table th .cell) {
  font-weight: 700;
  color: var(--hc-text);
}
</style>
