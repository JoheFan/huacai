<script setup lang="ts">
import { Plus, RefreshRight } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { fetchWorkbenchOverview, type WorkbenchMetric, type WorkbenchRecord, type WorkbenchReminder, type WorkbenchTodo } from '../../api/workbench'

const router = useRouter()
const loading = ref(false)

const filters = reactive({
  keyword: '',
})

const metricCards = ref<WorkbenchMetric[]>([])
const focusRows = ref<WorkbenchRecord[]>([])
const todoItems = ref<WorkbenchTodo[]>([])
const reminderItems = ref<WorkbenchReminder[]>([])

const quickEntries = [
  { label: '新增客户', path: '/customers' },
  { label: '借贷管理', path: '/loan-orders' },
  { label: '收入管理', path: '/finance/incomes' },
  { label: '系统用户', path: '/system/users' },
]

const loadOverview = async () => {
  loading.value = true
  try {
    const data = await fetchWorkbenchOverview({ keyword: filters.keyword || undefined })
    metricCards.value = data.metricCards
    focusRows.value = data.focusRows
    todoItems.value = data.todoItems
    reminderItems.value = data.reminderItems
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '工作台数据加载失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = async () => {
  await loadOverview()
}

const resetFilters = async () => {
  filters.keyword = ''
  await loadOverview()
}

const goTo = (path: string) => {
  router.push(path)
}

onMounted(loadOverview)
</script>

<template>
  <section class="page-shell">
    <header class="workbench-toolbar">
      <div class="workbench-heading__actions">
        <el-button type="primary" @click="goTo('/customers')">
          <el-icon><Plus /></el-icon>
          新增客户
        </el-button>
        <el-button plain @click="goTo('/loan-orders')">登记借贷</el-button>
        <el-button text @click="resetFilters">
          <el-icon><RefreshRight /></el-icon>
          重置视图
        </el-button>
      </div>
    </header>

    <section class="workbench-metrics">
      <article
        v-for="card in metricCards"
        :key="card.title"
        class="metric-card card"
        :class="{ 'metric-card--emphasized': card.emphasized }"
      >
        <div class="card__section">
          <div class="metric-card__top">
            <span class="metric-card__title">{{ card.title }}</span>
            <span class="metric-card__helper">{{ card.helper }}</span>
          </div>
          <strong class="metric-card__value">{{ card.value }}</strong>
        </div>
      </article>
    </section>

    <section class="workbench-content">
      <article class="card workbench-main" v-loading="loading">
        <div class="card__section">
          <div class="workbench-section__header">
            <h3 class="section-title">最近业务记录</h3>
          </div>

          <div class="workbench-filter">
            <div class="workbench-filter__core">
              <el-input v-model="filters.keyword" placeholder="客户名称 / 客户ID" clearable />
              <div class="workbench-filter__actions">
                <el-button type="primary" @click="handleSearch">查询</el-button>
                <el-button plain @click="resetFilters">重置</el-button>
              </div>
            </div>
          </div>

          <div class="workbench-table-wrap">
            <el-table :data="focusRows" class="workbench-table" table-layout="fixed">
              <el-table-column prop="customerName" label="客户名称" min-width="180" />
              <el-table-column prop="recordType" label="记录类型" min-width="120" />
              <el-table-column prop="relationInfo" label="关联信息" min-width="140" />
              <el-table-column prop="actionDate" label="最近日期" min-width="120" />
              <el-table-column prop="status" label="当前状态" min-width="120" />
              <el-table-column prop="priority" label="优先级" min-width="90">
                <template #default="{ row }">
                  <span
                    class="priority-tag"
                    :class="{
                      'is-high': row.priority === '高',
                      'is-medium': row.priority === '中',
                      'is-low': row.priority === '低',
                    }"
                  >
                    {{ row.priority }}
                  </span>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="140" fixed="right">
                <template #default="{ row }">
                  <div class="table-actions">
                    <el-button text type="primary" @click="goTo(row.routePath)">打开</el-button>
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <div class="workbench-pagination">
            <span class="workbench-pagination__count">{{ focusRows.length }} 条记录</span>
          </div>
        </div>
      </article>

      <aside class="workbench-side">
        <article class="card workbench-side__card">
          <div class="card__section">
            <div class="workbench-side__header">
              <h3 class="section-title">今日待办</h3>
              <span class="workbench-pill workbench-pill--highlight">{{ todoItems.length }} 项</span>
            </div>
            <ul class="side-list">
              <li v-for="item in todoItems" :key="item.title" class="side-list__item">
                <div>
                  <strong>{{ item.title }}</strong>
                  <p>{{ item.deadline }}</p>
                </div>
                <span
                  class="side-list__tag"
                  :class="{
                    'is-high': item.level === '高',
                    'is-medium': item.level === '中',
                    'is-low': item.level === '低',
                  }"
                >
                  {{ item.level }}优先
                </span>
              </li>
            </ul>
          </div>
        </article>

        <article class="card workbench-side__card">
          <div class="card__section">
            <div class="workbench-side__header">
              <h3 class="section-title">运行提醒</h3>
            </div>
            <ul class="reminder-list">
              <li v-for="item in reminderItems" :key="item.title" class="reminder-list__item">
                <span class="reminder-list__dot" :class="`is-${item.tone}`"></span>
                <div class="reminder-list__content">
                  <strong>{{ item.title }}</strong>
                  <p>{{ item.tag }}</p>
                </div>
              </li>
            </ul>
          </div>
        </article>

        <article class="card workbench-side__card">
          <div class="card__section">
            <div class="workbench-side__header">
              <h3 class="section-title">快捷入口</h3>
            </div>
            <div class="quick-grid">
              <el-button
                v-for="entry in quickEntries"
                :key="entry.label"
                plain
                size="small"
                class="quick-grid__button"
                @click="goTo(entry.path)"
              >
                {{ entry.label }}
              </el-button>
            </div>
          </div>
        </article>
      </aside>
    </section>
  </section>
</template>

<style scoped>
.workbench-toolbar {
  display: flex;
  justify-content: flex-end;
}

.workbench-heading__actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.workbench-metrics {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.metric-card {
  min-width: 0;
}

.metric-card--emphasized {
  background: var(--hc-primary-soft);
  border-color: rgba(59, 130, 246, 0.2);
}

.metric-card__top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.metric-card__title {
  font-size: 15px;
  font-weight: 600;
  color: var(--hc-text);
}

.metric-card__helper {
  color: var(--hc-primary);
  font-size: 13px;
}

.metric-card__value {
  display: block;
  margin-top: 14px;
  font-size: 33px;
  line-height: 1;
  font-weight: 700;
}

.workbench-content {
  display: grid;
  grid-template-columns: minmax(0, 2fr) minmax(300px, 0.92fr);
  gap: 16px;
}

.workbench-main {
  min-width: 0;
  border-radius: var(--hc-panel-radius);
}

.workbench-section__header,
.workbench-side__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.workbench-pill {
  display: inline-flex;
  align-items: center;
  height: 28px;
  padding: 0 11px;
  border-radius: 999px;
  border: 1px solid var(--hc-border);
  background: var(--hc-surface-secondary);
  color: var(--hc-text-soft);
  font-size: 13px;
}

.workbench-pill--highlight {
  border-color: rgba(59, 130, 246, 0.18);
  background: var(--hc-primary-soft);
  color: var(--hc-primary);
}

.workbench-filter {
  margin-top: 18px;
  padding: 14px;
  border: 1px solid var(--hc-border);
  border-radius: var(--hc-filter-radius);
  background: var(--hc-surface-secondary);
}

.workbench-filter__core,
.workbench-filter__extra {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

.workbench-filter__actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.workbench-filter__extra {
  grid-template-columns: repeat(3, minmax(0, 1fr));
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px dashed rgba(148, 163, 184, 0.35);
}

.workbench-table-wrap {
  margin-top: 16px;
  overflow-x: auto;
}

:deep(.workbench-table) {
  min-width: 760px;
}

:deep(.workbench-table .el-table__header th) {
  background: var(--hc-surface-secondary);
  color: var(--hc-text-soft);
  font-size: 13px;
  font-weight: 600;
}

:deep(.workbench-table .el-table__cell) {
  padding: 8px 0;
  font-size: 14px;
}

:deep(.workbench-table .el-table__row:hover > td.el-table__cell) {
  background: rgba(59, 130, 246, 0.04);
}

.priority-tag,
.side-list__tag {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 64px;
  height: 26px;
  padding: 0 8px;
  border-radius: 999px;
  font-size: 13px;
}

.priority-tag.is-high,
.side-list__tag.is-high {
  background: rgba(217, 72, 95, 0.12);
  color: var(--hc-danger);
}

.priority-tag.is-medium,
.side-list__tag.is-medium {
  background: rgba(217, 119, 6, 0.12);
  color: var(--hc-warning);
}

.priority-tag.is-low,
.side-list__tag.is-low {
  background: rgba(15, 118, 110, 0.12);
  color: var(--hc-success);
}

.table-actions {
  display: inline-flex;
  gap: 4px;
}

.workbench-pagination {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 14px;
}

.workbench-pagination__count {
  color: var(--hc-text-soft);
  font-size: 13px;
  line-height: 1.6;
}

.workbench-side {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.workbench-side__card {
  border-radius: var(--hc-panel-radius);
}

.side-list,
.reminder-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin: 16px 0 0;
  padding: 0;
  list-style: none;
}

.side-list__item {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  padding: 12px;
  border: 1px solid var(--hc-border);
  border-radius: 14px;
  background: var(--hc-surface-secondary);
}

.side-list__item strong,
.reminder-list__content strong {
  display: block;
  font-size: 14px;
  line-height: 1.5;
}

.side-list__item p,
.reminder-list__content p {
  margin: 6px 0 0;
  color: var(--hc-text-soft);
  font-size: 13px;
  line-height: 1.6;
}

.reminder-list__item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 10px 0;
}

.reminder-list__dot {
  width: 10px;
  height: 10px;
  margin-top: 5px;
  border-radius: 999px;
  flex-shrink: 0;
}

.reminder-list__dot.is-danger {
  background: var(--hc-danger);
}

.reminder-list__dot.is-warning {
  background: var(--hc-warning);
}

.reminder-list__dot.is-success {
  background: var(--hc-success);
}

.quick-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
  margin-top: 16px;
}

.quick-grid__button {
  justify-content: flex-start;
  width: 100%;
}

@media (max-width: 1280px) {
  .workbench-metrics {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .workbench-content {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 1024px) {
  .workbench-filter__core {
    grid-template-columns: 1fr 1fr;
  }

  .workbench-pagination {
    flex-direction: column;
    align-items: stretch;
  }
}

@media (max-width: 720px) {
  .workbench-heading__actions {
    width: 100%;
    display: grid;
    grid-template-columns: 1fr;
  }

  .workbench-heading__actions :deep(.el-button) {
    width: 100%;
  }

  .workbench-section__header,
  .workbench-side__header,
  .side-list__item {
    flex-direction: column;
    align-items: stretch;
  }

  .workbench-metrics,
  .workbench-filter__core,
  .quick-grid {
    grid-template-columns: 1fr;
  }

  .workbench-filter__actions {
    display: grid;
    grid-template-columns: 1fr 1fr;
  }

  .workbench-filter__actions :deep(.el-button) {
    width: 100%;
  }
}
</style>
