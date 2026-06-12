<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  createDailyIncrement,
  deleteDailyIncrement,
  fetchDailyIncrementPage,
  updateDailyIncrement,
  type DailyIncrementRecord,
  type DailyIncrementQuery,
  type DailyIncrementSavePayload,
} from '../../api/analysis'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const submitting = ref(false)
const drawerVisible = ref(false)
const editingId = ref<number | null>(null)
const rows = ref<DailyIncrementRecord[]>([])
const total = ref(0)

const detailId = computed(() => Number(route.params.summaryId))
const customerName = computed(() => String(route.query.customerName || ''))
const incrementDate = computed(() => String(route.query.incrementDate || ''))

const filters = reactive<DailyIncrementQuery>({
  pageNum: 1,
  pageSize: 100,
  detailId: detailId.value,
})

const form = reactive<DailyIncrementSavePayload>({
  detailId: detailId.value,
  customerName: '',
  incrementDate: '',
  incrementAmount: 0,
  channelRate: null,
  channelFee: null,
  seqNo: null,
  remark: '',
})

const drawerTitle = computed(() => (editingId.value ? '编辑日增量' : '新增日增量'))

const resetForm = () => {
  editingId.value = null
  form.detailId = detailId.value
  form.customerName = customerName.value
  form.incrementDate = incrementDate.value
  form.incrementAmount = 0
  form.channelRate = null
  form.channelFee = null
  form.seqNo = null
  form.remark = ''
}

const fillForm = (row: DailyIncrementRecord) => {
  editingId.value = row.id
  form.detailId = row.detailId
  form.customerName = row.customerName
  form.incrementDate = row.incrementDate
  form.incrementAmount = row.incrementAmount
  form.channelRate = row.channelRate
  form.channelFee = row.channelFee
  form.seqNo = row.seqNo
  form.remark = row.remark || ''
}

const fetchList = async () => {
  loading.value = true
  try {
    const data = await fetchDailyIncrementPage(filters)
    rows.value = data.records
    total.value = data.total
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '日增量加载失败')
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.push('/increment-details')
}

const openCreate = () => {
  resetForm()
  drawerVisible.value = true
}

const openEdit = (row: DailyIncrementRecord) => {
  fillForm(row)
  drawerVisible.value = true
}

const handleSubmit = async () => {
  if (!form.incrementAmount) {
    ElMessage.warning('请输入增量金额')
    return
  }

  submitting.value = true
  try {
    if (editingId.value) {
      await updateDailyIncrement(editingId.value, form)
      ElMessage.success('日增量已更新')
    } else {
      await createDailyIncrement(form)
      ElMessage.success('日增量已创建')
    }
    drawerVisible.value = false
    await fetchList()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '保存失败')
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (row: DailyIncrementRecord) => {
  try {
    await ElMessageBox.confirm('确定要删除该记录吗？', '确认删除', { type: 'warning' })
    await deleteDailyIncrement(row.id)
    ElMessage.success('删除成功')
    await fetchList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error instanceof Error ? error.message : '删除失败')
    }
  }
}

const handlePageChange = async (page: number) => {
  filters.pageNum = page
  await fetchList()
}

const formatAmount = (value: number | null | undefined) => {
  if (value == null) return '-'
  return `¥${value.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`
}

const formatRate = (value: number | null | undefined) => {
  if (value == null) return '-'
  return `${(value * 100).toFixed(2)}%`
}

onMounted(() => {
  fetchList()
})
</script>

<script lang="ts">
import { computed } from 'vue'
export default { name: 'IncrementDailyDetailView' }
</script>

<template>
  <section class="page-shell">
    <section class="card">
      <div class="card__section list-toolbar">
        <div class="list-toolbar__info">
          <span>客户：{{ customerName }}</span>
          <span>日期：{{ incrementDate }}</span>
        </div>
        <div class="list-toolbar__actions">
          <el-button plain @click="goBack">返回增量详情</el-button>
          <el-button type="primary" plain @click="openCreate">新增日增量</el-button>
        </div>
      </div>
    </section>

    <section class="card">
      <div class="card__section">
        <el-table v-loading="loading" :data="rows" table-layout="fixed">
          <el-table-column prop="seqNo" label="第几笔" min-width="100" />
          <el-table-column prop="incrementAmount" label="增量金额" min-width="150">
            <template #default="{ row }">
              {{ formatAmount(row.incrementAmount) }}
            </template>
          </el-table-column>
          <el-table-column prop="channelRate" label="渠道费率" min-width="120">
            <template #default="{ row }">
              {{ formatRate(row.channelRate) }}
            </template>
          </el-table-column>
          <el-table-column prop="channelFee" label="渠道费" min-width="150">
            <template #default="{ row }">
              {{ formatAmount(row.channelFee) }}
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" min-width="200" show-overflow-tooltip />
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
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

    <el-drawer v-model="drawerVisible" :title="drawerTitle" size="480px">
      <div class="drawer-shell">
        <el-form label-position="top">
          <div class="form-grid">
            <el-form-item label="客户名称">
              <el-input v-model="form.customerName" disabled />
            </el-form-item>
            <el-form-item label="增量日期">
              <el-input v-model="form.incrementDate" disabled />
            </el-form-item>
            <el-form-item label="第几笔">
              <el-input-number v-model="form.seqNo" :min="0" controls-position="right" style="width: 100%" />
            </el-form-item>
            <el-form-item label="增量金额" required>
              <el-input-number v-model="form.incrementAmount" :min="0" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
            <el-form-item label="渠道费率">
              <el-input-number v-model="form.channelRate" :min="0" :precision="4" controls-position="right" style="width: 100%" />
            </el-form-item>
            <el-form-item label="渠道费">
              <el-input-number v-model="form.channelFee" :min="0" :precision="2" controls-position="right" style="width: 100%" />
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
.list-pagination {
  justify-content: flex-end;
  margin-right: 0;
}

@media (max-width: 960px) {
  .list-pagination {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
