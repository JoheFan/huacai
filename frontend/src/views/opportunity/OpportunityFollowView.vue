<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  createFollowRecord,
  deleteFollowRecord,
  fetchFollowRecordPage,
  updateFollowRecord,
  type FollowRecord,
  type FollowRecordQuery,
  type FollowRecordSavePayload,
} from '../../api/opportunity'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const submitting = ref(false)
const drawerVisible = ref(false)
const editingId = ref<number | null>(null)
const rows = ref<FollowRecord[]>([])
const total = ref(0)

const opportunityId = computed(() => Number(route.params.id))

const filters = reactive<FollowRecordQuery>({
  pageNum: 1,
  pageSize: 100,
  opportunityId: opportunityId.value,
})

const form = reactive<FollowRecordSavePayload>({
  opportunityId: opportunityId.value,
  followTime: '',
  followerName: '',
  followContent: '',
  nextAction: '',
  remark: '',
})

const drawerTitle = computed(() => (editingId.value ? '编辑跟进记录' : '新增跟进记录'))

const resetForm = () => {
  editingId.value = null
  form.opportunityId = opportunityId.value
  form.followTime = ''
  form.followerName = ''
  form.followContent = ''
  form.nextAction = ''
  form.remark = ''
}

const fillForm = (row: FollowRecord) => {
  editingId.value = row.id
  form.opportunityId = row.opportunityId
  form.followTime = row.followTime
  form.followerName = row.followerName
  form.followContent = row.followContent
  form.nextAction = row.nextAction || ''
  form.remark = row.remark || ''
}

const fetchList = async () => {
  loading.value = true
  try {
    const data = await fetchFollowRecordPage(filters)
    rows.value = data.records
    total.value = data.total
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '跟进记录加载失败')
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.push('/opportunities')
}

const openCreate = () => {
  resetForm()
  drawerVisible.value = true
}

const openEdit = (row: FollowRecord) => {
  fillForm(row)
  drawerVisible.value = true
}

const handleSubmit = async () => {
  if (!form.followTime) {
    ElMessage.warning('请选择跟进时间')
    return
  }
  if (!form.followContent) {
    ElMessage.warning('请输入跟进详情')
    return
  }

  submitting.value = true
  try {
    if (editingId.value) {
      await updateFollowRecord(editingId.value, form)
      ElMessage.success('跟进记录已更新')
    } else {
      await createFollowRecord(form)
      ElMessage.success('跟进记录已创建')
    }
    drawerVisible.value = false
    await fetchList()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '保存失败')
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (row: FollowRecord) => {
  try {
    await ElMessageBox.confirm('确定要删除该跟进记录吗？', '确认删除', { type: 'warning' })
    await deleteFollowRecord(row.id)
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

const formatDateTime = (value: string | null | undefined) => value || '-'

onMounted(() => {
  fetchList()
})
</script>

<script lang="ts">
import { computed } from 'vue'
export default { name: 'OpportunityFollowView' }
</script>

<template>
  <section class="page-shell">
    <section class="card">
      <div class="card__section detail-header">
        <div class="detail-header__copy">
          <span class="detail-header__eyebrow">商机管理 / 跟进记录</span>
          <h2 class="detail-header__title">商机跟进记录</h2>
          <p class="detail-header__desc">集中维护该商机的沟通时间、跟进详情和下一步动作，便于从商机列表快速回溯最新进展。</p>
          <div class="detail-meta">
            <span class="detail-meta__item">商机ID：<strong>{{ opportunityId }}</strong></span>
            <span class="detail-meta__item">记录数：<strong>{{ total }}</strong></span>
          </div>
        </div>
        <div class="detail-header__actions">
          <el-button plain @click="goBack">返回商机列表</el-button>
          <el-button type="primary" plain @click="openCreate">新增跟进记录</el-button>
        </div>
      </div>
    </section>

    <section class="card">
      <div class="card__section">
        <el-table v-loading="loading" :data="rows" table-layout="fixed">
          <el-table-column prop="followTime" label="跟进时间" min-width="170">
            <template #default="{ row }">
              {{ formatDateTime(row.followTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="followerName" label="跟进人" min-width="120" />
          <el-table-column prop="followContent" label="跟进详情" min-width="300" show-overflow-tooltip />
          <el-table-column prop="nextAction" label="下次跟进动作" min-width="180" show-overflow-tooltip />
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

    <el-drawer v-model="drawerVisible" :title="drawerTitle" size="520px">
      <div class="drawer-shell">
        <el-form label-position="top">
          <div class="form-grid">
            <el-form-item label="跟进时间" required>
              <el-date-picker
                v-model="form.followTime"
                type="datetime"
                value-format="YYYY-MM-DD HH:mm:ss"
                placeholder="请选择时间"
                style="width: 100%"
              />
            </el-form-item>
            <el-form-item label="跟进人">
              <el-input v-model="form.followerName" placeholder="请输入跟进人姓名" />
            </el-form-item>
            <el-form-item label="跟进详情" required class="form-grid__full">
              <el-input v-model="form.followContent" type="textarea" :rows="4" placeholder="请输入跟进详情" />
            </el-form-item>
            <el-form-item label="下次跟进动作" class="form-grid__full">
              <el-input v-model="form.nextAction" placeholder="请输入下次跟进动作" />
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
