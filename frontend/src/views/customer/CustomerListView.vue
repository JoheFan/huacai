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

const formatNumber = (value: number | null | undefined) => {
  if (value == null) {
    return '-'
  }
  return value.toLocaleString('zh-CN', {
    maximumFractionDigits: 2,
  })
}

onMounted(fetchList)
</script>

<template>
  <section class="page-shell">
    <section class="card">
      <div class="card__section page-intro">
        <div class="page-intro__copy">
          <span class="page-intro__eyebrow">客户主档</span>
          <h2>客户管理</h2>
          <p>统一维护客户档案、最新风险摘要和负债登记入口。点击客户名称进入整页档案，点击操作区可直接切到对应记录页。</p>
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
        <el-table v-loading="loading" :data="rows" table-layout="fixed">
          <el-table-column prop="customerNo" label="客户ID" min-width="130" />
          <el-table-column label="客户名称" min-width="128" fixed="left">
            <template #default="{ row }">
              <el-button text type="primary" @click="openArchive(row.id)">{{ row.customerName }}</el-button>
            </template>
          </el-table-column>
          <el-table-column prop="mobile" label="联系电话" min-width="130" />
          <el-table-column prop="companyName" label="公司名称" min-width="170" show-overflow-tooltip />
          <el-table-column prop="creditCode" label="统一社会信用代码" min-width="190" show-overflow-tooltip />
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
          <el-table-column prop="recommenderName" label="推荐人" min-width="100" />
          <el-table-column prop="recommenderRate" label="推荐人返点" min-width="100">
            <template #default="{ row }">{{ row.recommenderRate != null ? row.recommenderRate + '%' : '-' }}</template>
          </el-table-column>
          <el-table-column prop="serviceFee" label="服务费" min-width="100">
            <template #default="{ row }">{{ formatNumber(row.serviceFee) }}</template>
          </el-table-column>
          <el-table-column prop="testDate" label="测试日期" min-width="120">
            <template #default="{ row }">{{ row.testDate || '-' }}</template>
          </el-table-column>
          <el-table-column prop="testLimit" label="测试额度" min-width="110">
            <template #default="{ row }">{{ formatNumber(row.testLimit) }}</template>
          </el-table-column>
          <el-table-column prop="trafficValue" label="流量" min-width="110">
            <template #default="{ row }">{{ formatNumber(row.trafficValue) }}</template>
          </el-table-column>
          <el-table-column prop="compositeScore" label="综合评分" min-width="110">
            <template #default="{ row }">{{ formatNumber(row.compositeScore) }}</template>
          </el-table-column>
          <el-table-column prop="thirdPartyScore" label="龙信商" min-width="100">
            <template #default="{ row }">{{ formatNumber(row.thirdPartyScore) }}</template>
          </el-table-column>
          <el-table-column label="垫付情况" min-width="110">
            <template #default="{ row }">
              <el-tag v-if="row.loanStatus === 'RUNNING'" type="warning" size="small">有垫付</el-tag>
              <el-tag v-else-if="row.loanStatus === 'SETTLED'" type="success" size="small">已结清</el-tag>
              <el-tag v-else-if="row.loanStatus === 'NOT_STARTED'" size="small">未开始</el-tag>
              <span v-else>{{ row.loanStatus || '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="220" fixed="right">
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
.page-intro {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.page-intro__copy {
  max-width: 760px;
}

.page-intro__eyebrow {
  display: inline-flex;
  margin-bottom: 10px;
  color: var(--hc-primary);
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.08em;
}

.page-intro h2 {
  margin: 0;
  font-size: 26px;
  line-height: 1.2;
}

.page-intro p {
  margin: 10px 0 0;
  color: var(--hc-text-soft);
  line-height: 1.6;
}

.page-intro__actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 10px;
}

.action-dropdown__trigger {
  min-width: 88px;
}

.action-dropdown__trigger--text {
  min-width: auto;
  padding: 0;
}

.list-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.list-toolbar__filters {
  flex: 1;
  max-width: 520px;
}

.list-toolbar__actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.row-actions {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
}

.list-pagination {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-top: 16px;
}

.list-pagination__meta {
  color: var(--hc-text-soft);
  font-size: 13px;
}

@media (max-width: 1180px) {
  .page-intro,
  .list-toolbar,
  .list-pagination {
    flex-direction: column;
    align-items: stretch;
  }

  .page-intro__actions,
  .list-toolbar__actions {
    justify-content: flex-start;
  }

}
</style>
