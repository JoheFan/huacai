<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh } from '@element-plus/icons-vue'
import { payeeApi, type PayeeVO, type PayeeSaveRequest } from '../../api/finance'

const loading = ref(false)
const rows = ref<PayeeVO[]>([])
const total = ref(0)

const filters = reactive({
  keyword: '',
  status: '',
  pageNum: 1,
  pageSize: 20,
})

const dialogVisible = ref(false)
const dialogTitle = ref('新增收款方')
const isEdit = ref(false)
const editId = ref<number | null>(null)
const saving = ref(false)

const form = reactive<PayeeSaveRequest>({
  payeeName: '',
  payeeType: '',
  bankName: '',
  bankAccount: '',
  contactName: '',
  contactPhone: '',
  status: 'ENABLE',
  remark: '',
})

const fetchList = async () => {
  loading.value = true
  try {
    const data = await payeeApi.page({
      keyword: filters.keyword || undefined,
      status: filters.status || undefined,
      pageNum: filters.pageNum,
      pageSize: filters.pageSize,
    })
    rows.value = data.records
    total.value = data.total
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '加载失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = async () => {
  filters.pageNum = 1
  await fetchList()
}

const handleReset = async () => {
  filters.keyword = ''
  filters.status = ''
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
  dialogTitle.value = '新增收款方'
  Object.assign(form, {
    payeeName: '',
    payeeType: '',
    bankName: '',
    bankAccount: '',
    contactName: '',
    contactPhone: '',
    status: 'ENABLE',
    remark: '',
  })
  dialogVisible.value = true
}

const openEdit = (row: PayeeVO) => {
  isEdit.value = true
  editId.value = row.id
  dialogTitle.value = '编辑收款方'
  Object.assign(form, {
    payeeName: row.payeeName || '',
    payeeType: row.payeeType || '',
    bankName: row.bankName || '',
    bankAccount: row.bankAccount || '',
    contactName: row.contactName || '',
    contactPhone: row.contactPhone || '',
    status: row.status || 'ENABLE',
    remark: row.remark || '',
  })
  dialogVisible.value = true
}

const handleSave = async () => {
  if (!form.payeeName?.trim()) {
    ElMessage.error('收款方名称不能为空')
    return
  }
  saving.value = true
  try {
    if (isEdit.value && editId.value) {
      await payeeApi.update(editId.value, form)
      ElMessage.success('更新成功')
    } else {
      await payeeApi.create(form)
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

const handleDelete = async (row: PayeeVO) => {
  try {
    await ElMessageBox.confirm(`确定要删除收款方"${row.payeeName}"吗？`, '删除确认', { type: 'warning' })
    await payeeApi.delete(row.id)
    ElMessage.success('删除成功')
    await fetchList()
  } catch (error) {
    if (error instanceof Error && error.message !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

const handleToggleStatus = async (row: PayeeVO) => {
  const newStatus = row.status === 'ENABLE' ? 'DISABLE' : 'ENABLE'
  const action = newStatus === 'ENABLE' ? '启用' : '禁用'
  try {
    await ElMessageBox.confirm(`确定要${action}收款方"${row.payeeName}"吗？`, `${action}确认`, { type: 'warning' })
    await payeeApi.updateStatus(row.id, newStatus)
    ElMessage.success(`${action}成功`)
    await fetchList()
  } catch (error) {
    if (error instanceof Error && error.message !== 'cancel') {
      ElMessage.error(error.message || `${action}失败`)
    }
  }
}

const getStatusText = (status: string) => {
  return status === 'ENABLE' ? '正常' : '禁用'
}

const getStatusType = (status: string) => {
  return status === 'ENABLE' ? 'success' : 'info'
}

onMounted(() => {
  void fetchList()
})
</script>

<template>
  <section class="page-shell">
    <section class="card">
      <div class="card__header">
        <h2>收款方管理</h2>
        <div class="toolbar">
          <el-button type="primary" :icon="Plus" @click="openCreate">新增</el-button>
        </div>
      </div>

      <div class="filter-bar">
        <el-input
          v-model="filters.keyword"
          placeholder="收款方名称"
          style="width: 200px"
          clearable
          @keyup.enter="handleSearch"
        />
        <el-select v-model="filters.status" placeholder="状态" style="width: 120px" clearable>
          <el-option label="正常" value="ENABLE" />
          <el-option label="禁用" value="DISABLE" />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
        <el-button :icon="Refresh" @click="fetchList">刷新</el-button>
      </div>

      <el-table v-loading="loading" :data="rows" stripe border>
        <el-table-column type="index" width="60" label="序号" />
        <el-table-column prop="payeeName" label="收款方名称" min-width="160" />
        <el-table-column prop="payeeType" label="类型" width="100" />
        <el-table-column prop="bankName" label="开户行" width="140" />
        <el-table-column prop="bankAccount" label="账号" width="180" />
        <el-table-column prop="contactName" label="联系人" width="100" />
        <el-table-column prop="contactPhone" label="联系电话" width="130" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openEdit(row)">编辑</el-button>
            <el-button link :type="row.status === 'ENABLE' ? 'danger' : 'success'" size="small" @click="handleToggleStatus(row)">
              {{ row.status === 'ENABLE' ? '禁用' : '启用' }}
            </el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" @close="dialogVisible = false">
      <el-form :model="form" label-width="120px">
        <el-form-item label="收款方名称" required>
          <el-input v-model="form.payeeName" placeholder="请输入收款方名称" />
        </el-form-item>
        <el-form-item label="类型">
          <el-input v-model="form.payeeType" placeholder="请输入类型" />
        </el-form-item>
        <el-form-item label="开户行">
          <el-input v-model="form.bankName" placeholder="请输入开户行" />
        </el-form-item>
        <el-form-item label="账号">
          <el-input v-model="form.bankAccount" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item label="联系人">
          <el-input v-model="form.contactName" placeholder="请输入联系人" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width: 100%">
            <el-option label="正常" value="ENABLE" />
            <el-option label="禁用" value="DISABLE" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<style scoped>
.page-shell {
  padding: 16px;
}

.card {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
}

.card__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.card__header h2 {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}

.toolbar {
  display: flex;
  gap: 8px;
}

.filter-bar {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
  align-items: center;
}
</style>
