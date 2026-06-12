<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh } from '@element-plus/icons-vue'
import { salaryApi, type SalaryStandardVO, type SalaryStandardPageQuery } from '../../api/hr'

const loading = ref(false)
const rows = ref<SalaryStandardVO[]>([])
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref<number | null>(null)

const filters = reactive<SalaryStandardPageQuery>({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  status: '',
})

const form = reactive({
  salaryName: '',
  amount: 0,
  jobTitle: '',
  orgId: undefined as number | undefined,
  orgName: '',
  description: '',
  sortNo: 0,
  status: 'ENABLE',
  effectiveDate: '',
  expireDate: '',
  versionNo: '',
  applicableScope: '',
})

const fetchList = async () => {
  loading.value = true
  try {
    const data = await salaryApi.page(filters)
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
  Object.assign(form, {
    salaryName: '',
    amount: 0,
    jobTitle: '',
    orgId: undefined,
    orgName: '',
    description: '',
    sortNo: 0,
    status: 'ENABLE',
    effectiveDate: '',
    expireDate: '',
    versionNo: '',
    applicableScope: '',
  })
  dialogVisible.value = true
}

const handleEdit = (row: SalaryStandardVO) => {
  isEdit.value = true
  editId.value = row.id
  Object.assign(form, {
    salaryName: row.salaryName,
    amount: row.amount,
    jobTitle: row.jobTitle || '',
    orgId: row.orgId,
    orgName: row.orgName || '',
    description: row.description || '',
    sortNo: row.sortNo || 0,
    status: row.status,
    effectiveDate: row.effectiveDate || '',
    expireDate: row.expireDate || '',
    versionNo: row.versionNo || '',
    applicableScope: row.applicableScope || '',
  })
  dialogVisible.value = true
}

const handleDelete = async (row: SalaryStandardVO) => {
  try {
    await ElMessageBox.confirm(`确定要删除工资标准 "${row.salaryName}" 吗？`, '删除确认', {
      type: 'warning',
    })
    await salaryApi.delete(row.id)
    ElMessage.success('删除成功')
    await fetchList()
  } catch (error) {
    if (error instanceof Error && error.message !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

const handleSave = async () => {
  try {
    if (isEdit.value && editId.value) {
      await salaryApi.update(editId.value, form)
    } else {
      await salaryApi.create(form)
    }
    ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
    dialogVisible.value = false
    await fetchList()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '保存失败')
  }
}

const getStatusText = (status: string) => {
  return status === 'ENABLE' ? '有效' : '无效'
}

onMounted(() => {
  void fetchList()
})
</script>

<template>
  <section class="page-shell">
    <section class="card">
      <div class="card__section page-intro">
        <div class="page-intro__copy">
          <span class="page-intro__eyebrow">人事管理</span>
          <h2 class="page-intro__title">工资管理</h2>
          <p class="page-intro__desc">统一维护岗位工资标准，支持按名称和状态筛选，便于人事异动和薪资核定时快速引用。</p>
        </div>

        <div class="page-intro__actions">
          <el-button type="primary" :icon="Plus" @click="openCreate">新增工资标准</el-button>
        </div>
      </div>
    </section>

    <section class="card">
      <div class="card__section list-toolbar">
        <div class="list-toolbar__filters">
          <el-input
            v-model="filters.keyword"
            placeholder="搜索工资名称"
            clearable
            @clear="handleReset"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <Search />
            </template>
          </el-input>
          <el-select v-model="filters.status" placeholder="状态" clearable>
            <el-option label="有效" value="ENABLE" />
            <el-option label="无效" value="DISABLE" />
          </el-select>
        </div>
        <div class="list-toolbar__actions">
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button plain @click="handleReset">重置</el-button>
          <el-button plain :icon="Refresh" @click="fetchList">刷新</el-button>
        </div>
      </div>
    </section>

    <section class="card">
      <div class="card__section">
        <div class="table-wrap">
      <el-table v-loading="loading" :data="rows" stripe border style="width: 100%">
        <el-table-column type="index" width="60" label="序号" />
        <el-table-column prop="salaryName" label="工资名称" width="150" />
        <el-table-column prop="amount" label="工资金额" width="120">
          <template #default="{ row }">
            ¥{{ row.amount.toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column prop="jobTitle" label="所属岗位" width="120" />
        <el-table-column prop="orgName" label="所属部门" width="150" />
        <el-table-column prop="description" label="工资描述" min-width="150" show-overflow-tooltip />
        <el-table-column prop="sortNo" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ENABLE' ? 'success' : 'info'">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">修改</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
        </div>

      <el-pagination
        v-model:current-page="filters.pageNum"
        v-model:page-size="filters.pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
      </div>
    </section>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑工资标准' : '新增工资标准'" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="工资名称" required>
          <el-input v-model="form.salaryName" />
        </el-form-item>
        <el-form-item label="工资金额" required>
          <el-input-number v-model="form.amount" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="所属岗位">
              <el-input v-model="form.jobTitle" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属部门">
              <el-input v-model="form.orgName" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="排序">
              <el-input-number v-model="form.sortNo" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-select v-model="form.status" style="width: 100%">
                <el-option label="有效" value="ENABLE" />
                <el-option label="无效" value="DISABLE" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="生效日期">
              <el-date-picker v-model="form.effectiveDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="失效日期">
              <el-date-picker v-model="form.expireDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="工资描述">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<style scoped>
</style>
