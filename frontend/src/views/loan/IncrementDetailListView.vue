<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  createIncrementDetail,
  deleteIncrementDetail,
  fetchIncrementDetailPage,
  updateIncrementDetail,
  type IncrementDetailRecord,
  type IncrementDetailQuery,
  type IncrementDetailSavePayload,
} from '../../api/analysis'

const router = useRouter()
const loading = ref(false)
const submitting = ref(false)
const drawerVisible = ref(false)
const editingId = ref<number | null>(null)
const rows = ref<IncrementDetailRecord[]>([])
const total = ref(0)

const filters = reactive<IncrementDetailQuery>({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
})

const form = reactive<IncrementDetailSavePayload>({
  customerId: undefined,
  customerName: '',
  incrementDate: '',
  totalAmount: null,
  businessAddress: '',
  industry: '',
  dailyCount: null,
  remark: '',
})

const drawerTitle = computed(() => (editingId.value ? '编辑增量详情' : '新增增量详情'))

const resetForm = () => {
  editingId.value = null
  form.customerId = undefined
  form.customerName = ''
  form.incrementDate = ''
  form.totalAmount = null
  form.businessAddress = ''
  form.industry = ''
  form.dailyCount = null
  form.remark = ''
}

const fillForm = (row: IncrementDetailRecord) => {
  editingId.value = row.id
  form.customerId = row.customerId ?? undefined
  form.customerName = row.customerName
  form.incrementDate = row.incrementDate
  form.totalAmount = row.totalAmount
  form.businessAddress = row.businessAddress || ''
  form.industry = row.industry || ''
  form.dailyCount = row.dailyCount
  form.remark = row.remark || ''
}

const fetchList = async () => {
  loading.value = true
  try {
    const data = await fetchIncrementDetailPage(filters)
    rows.value = data.records
    total.value = data.total
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '增量详情加载失败')
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
  filters.pageNum = 1
  await fetchList()
}

const openCreate = () => {
  resetForm()
  drawerVisible.value = true
}

const openEdit = (row: IncrementDetailRecord) => {
  fillForm(row)
  drawerVisible.value = true
}

const handleSubmit = async () => {
  if (!form.customerName) {
    ElMessage.warning('请输入客户名称')
    return
  }
  if (!form.incrementDate) {
    ElMessage.warning('请选择增量日期')
    return
  }

  submitting.value = true
  try {
    if (editingId.value) {
      await updateIncrementDetail(editingId.value, form)
      ElMessage.success('增量详情已更新')
    } else {
      await createIncrementDetail(form)
      ElMessage.success('增量详情已创建')
    }
    drawerVisible.value = false
    await fetchList()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '保存失败')
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (row: IncrementDetailRecord) => {
  try {
    await ElMessageBox.confirm('确定要删除该记录吗？', '确认删除', { type: 'warning' })
    await deleteIncrementDetail(row.id)
    ElMessage.success('删除成功')
    await fetchList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error instanceof Error ? error.message : '删除失败')
    }
  }
}

const openDailyDetail = (row: IncrementDetailRecord) => {
  router.push({
    path: `/increment-details/daily/${row.id}`,
    query: {
      customerName: row.customerName,
      incrementDate: row.incrementDate,
    },
  })
}

const handlePageChange = async (page: number) => {
  filters.pageNum = page
  await fetchList()
}

const formatAmount = (value: number | null | undefined) => {
  if (value == null) return '-'
  return `¥${value.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`
}

const formatDate = (value: string | null | undefined) => value || '-'

onMounted(() => {
  fetchList()
})
</script>

<script lang="ts">
import { computed } from 'vue'
export default { name: 'IncrementDetailListView' }
</script>

<template>
  <section class="page-shell">
    <section class="card">
      <div class="card__section list-toolbar">
        <div class="list-toolbar__filters">
          <el-input v-model="filters.keyword" placeholder="客户名称" clearable />
        </div>
        <div class="list-toolbar__actions">
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button plain @click="handleReset">重置</el-button>
          <el-button type="primary" plain @click="openCreate">新增</el-button>
        </div>
      </div>
    </section>

    <section class="card">
      <div class="card__section">
        <el-table v-loading="loading" :data="rows" table-layout="fixed">
          <el-table-column prop="customerName" label="客户名称" min-width="180" show-overflow-tooltip />
          <el-table-column prop="incrementDate" label="增量日期" min-width="120">
            <template #default="{ row }">
              {{ formatDate(row.incrementDate) }}
            </template>
          </el-table-column>
          <el-table-column prop="totalAmount" label="增量总额" min-width="150">
            <template #default="{ row }">
              {{ formatAmount(row.totalAmount) }}
            </template>
          </el-table-column>
          <el-table-column prop="businessAddress" label="经营地址" min-width="200" show-overflow-tooltip />
          <el-table-column prop="industry" label="经营行业" min-width="120" show-overflow-tooltip />
          <el-table-column prop="dailyCount" label="当天共几笔" min-width="120">
            <template #default="{ row }">
              {{ row.dailyCount ?? '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
          <el-table-column label="操作" width="220" fixed="right">
            <template #default="{ row }">
              <el-button text type="primary" @click="openDailyDetail(row)">日增量明细</el-button>
              <el-button text type="primary" @click="openEdit(row)">编辑</el-button>
              <el-button text type="danger" @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="list-pagination">
          <el-pagination
            layout="prev, pager, next"
            :total="total"
            :page-size="filters.pageSize"
            :current-page="filters.pageNum"
            @current-change="handlePageChange"
          />
        </div>
      </div>
    </section>

    <el-drawer v-model="drawerVisible" :title="drawerTitle" size="520px">
      <div class="drawer-shell">
        <el-form label-position="top">
          <div class="form-grid">
            <el-form-item label="客户名称" required>
              <el-input v-model="form.customerName" placeholder="请输入客户名称" />
            </el-form-item>
            <el-form-item label="增量日期" required>
              <el-date-picker
                v-model="form.incrementDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择日期"
                style="width: 100%"
              />
            </el-form-item>
            <el-form-item label="增量总额">
              <el-input-number v-model="form.totalAmount" :min="0" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
            <el-form-item label="当天共几笔">
              <el-input-number v-model="form.dailyCount" :min="0" controls-position="right" style="width: 100%" />
            </el-form-item>
            <el-form-item label="经营地址">
              <el-input v-model="form.businessAddress" placeholder="请输入经营地址" />
            </el-form-item>
            <el-form-item label="经营行业">
              <el-input v-model="form.industry" placeholder="请输入经营行业" />
            </el-form-item>
            <el-form-item label="备注" class="form-grid__full">
              <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
            </el-form-item>
          </div>
        </el-form>

        <div class="drawer-actions">
          <el-button plain @click="drawerVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">提交</el-button>
        </div>
      </div>
    </el-drawer>
  </section>
</template>

<style scoped>
.list-toolbar__filters :deep(.el-input) {
  width: 240px;
}

.list-pagination {
  justify-content: flex-end;
  margin-right: 0;
}

@media (max-width: 960px) {
  .list-pagination {
    flex-direction: column;
    align-items: stretch;
  }

  .list-toolbar__filters :deep(.el-input) {
    width: 100%;
  }

}
</style>
