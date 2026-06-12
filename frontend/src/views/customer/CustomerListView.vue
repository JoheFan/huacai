<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'
import { fetchCustomerPage, type CustomerQuery, type CustomerRecord } from '../../api/customer'
import { getCustomerListRowActions, getCustomerListToolbarActions } from './customerActionLayout'

const router = useRouter()
const toolbarActions = getCustomerListToolbarActions()
const rowActions = getCustomerListRowActions()

const loading = ref(false)
const rows = ref<CustomerRecord[]>([])
const total = ref(0)

const filters = reactive<CustomerQuery>({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
})

const fetchList = async () => {
  loading.value = true
  try {
    const data = await fetchCustomerPage(filters)
    rows.value = data.records
    total.value = data.total
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '客户列表加载失败')
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

const handlePageChange = async (page: number) => {
  filters.pageNum = page
  await fetchList()
}

const openCreate = () => {
  router.push('/customers/archive/create')
}

const openArchive = (id: number) => {
  router.push(`/customers/archive/${id}`)
}

const openRiskRecords = (id: number) => {
  router.push({
    path: '/customer-risks',
    query: {
      customerId: String(id),
    },
  })
}

const openDebtRecords = (id: number) => {
  router.push({
    path: '/customer-debts',
    query: {
      customerId: String(id),
    },
  })
}

const handleToolbarCommand = (command: string) => {
  if (command === 'refresh') {
    void fetchList()
  }
}

const handleRowCommand = (command: string, id: number) => {
  if (command === 'risks') {
    openRiskRecords(id)
    return
  }
  if (command === 'debts') {
    openDebtRecords(id)
  }
}

const handleRowMenuCommand =
  (id: number) =>
  (command: string | number | object) => {
    handleRowCommand(String(command), id)
  }


onMounted(fetchList)
</script>

<template>
  <section class="page-shell">
    <section class="card">
      <div class="card__section page-intro">
        <div class="page-intro__copy">
          <span class="page-intro__eyebrow">客户主档</span>
          <h2 class="page-intro__title">客户管理</h2>
          <p class="page-intro__desc">统一维护客户档案、最新风险摘要和负债登记入口。点击客户名称进入整页档案，点击操作区可直接切到对应记录页。</p>
        </div>

        <div class="page-intro__actions">
          <el-button
            v-for="action in toolbarActions.secondary"
            :key="action.key"
            plain
            @click="handleToolbarCommand(action.key)"
          >
            {{ action.label }}
          </el-button>
          <el-dropdown trigger="click">
            <el-button plain class="action-dropdown__trigger">
              更多
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item
                  v-for="action in toolbarActions.overflow"
                  :key="action.key"
                  :disabled="action.disabled"
                >
                  {{ action.label }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <el-button type="primary" @click="openCreate">{{ toolbarActions.primary.label }}</el-button>
        </div>
      </div>
    </section>

    <section class="card">
      <div class="card__section list-toolbar">
        <div class="list-toolbar__filters">
          <el-input
            v-model="filters.keyword"
            placeholder="客户ID / 客户名称 / 联系电话 / 公司名称"
            clearable
            @keyup.enter="handleSearch"
          />
        </div>

        <div class="list-toolbar__actions">
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button plain @click="handleReset">重置</el-button>
        </div>
      </div>
    </section>

    <section class="card">
      <div class="card__section">
        <div class="table-wrap">
          <el-table v-loading="loading" :data="rows" table-layout="fixed">
          <el-table-column label="客户名称" min-width="128" fixed="left">
            <template #default="{ row }">
              <el-tooltip :content="row.customerName" placement="top">
                <el-button text type="primary" @click="openArchive(row.id)">{{ row.customerName }}</el-button>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column prop="customerNo" label="客户ID" min-width="130" />
          <el-table-column prop="mobile" label="联系电话" min-width="130" />
          <el-table-column prop="companyName" label="公司名称" min-width="170" show-overflow-tooltip />
          <el-table-column prop="auditStatus" label="审核状态" min-width="100">
            <template #default="{ row }">
              <el-tag v-if="row.auditStatus === 'PENDING'" type="warning" size="small">待审核</el-tag>
              <el-tag v-else-if="row.auditStatus === 'APPROVED'" type="success" size="small">已通过</el-tag>
              <el-tag v-else-if="row.auditStatus === 'REJECTED'" type="danger" size="small">已驳回</el-tag>
              <span v-else>{{ row.auditStatus || '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="bizStatus" label="状态" min-width="100">
            <template #default="{ row }">
              <el-tag v-if="row.bizStatus === 'INIT'" size="small">未开始借款</el-tag>
              <el-tag v-else-if="row.bizStatus === 'FOLLOWING'" type="warning" size="small">开始借款</el-tag>
              <el-tag v-else-if="row.bizStatus === 'SIGNED'" type="success" size="small">已结清</el-tag>
              <span v-else>{{ row.bizStatus || '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="垫付情况" min-width="110">
            <template #default="{ row }">
              <el-tag v-if="row.loanStatus === 'RUNNING'" type="warning" size="small">有垫付</el-tag>
              <el-tag v-else-if="row.loanStatus === 'SETTLED'" type="success" size="small">已结清</el-tag>
              <el-tag v-else-if="row.loanStatus === 'NOT_STARTED'" size="small">未开始</el-tag>
              <span v-else>{{ row.loanStatus || '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="140" fixed="right">
            <template #default="{ row }">
              <div class="row-actions">
                <el-button text type="primary" @click="openArchive(row.id)">详情</el-button>
                <el-dropdown trigger="click" @command="handleRowMenuCommand(row.id)">
                  <el-button text class="action-dropdown__trigger action-dropdown__trigger--text">
                    更多
                    <el-icon class="el-icon--right"><ArrowDown /></el-icon>
                  </el-button>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item
                        v-for="action in rowActions.overflow"
                        :key="action.key"
                        :command="action.key"
                      >
                        {{ action.label }}
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>
            </template>
          </el-table-column>
        </el-table>
        </div>

        <div class="list-pagination">
          <span class="list-pagination__meta">共 {{ total }} 条</span>
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
  </section>
</template>

<style scoped>
@media (max-width: 960px) {
  .list-toolbar__filters {
    width: 100%;
  }

  .list-toolbar__filters :deep(.el-input) {
    width: 100%;
  }
}
</style>
