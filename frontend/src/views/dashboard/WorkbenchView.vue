<script setup lang="ts">
import { ElMessage } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { fetchWorkbenchOverview, type WorkbenchMetric, type WorkbenchRecord, type WorkbenchReminder, type WorkbenchTodo } from '../../api/workbench'
import IconSymbol from '../../components/IconSymbol.vue'

const router = useRouter()
const loading = ref(false)

const filters = reactive({
  keyword: '',
})

const metricCards = ref<WorkbenchMetric[]>([])
const focusRows = ref<WorkbenchRecord[]>([])
const todoItems = ref<WorkbenchTodo[]>([])
const reminderItems = ref<WorkbenchReminder[]>([])

const metricIconMap: Record<string, string> = {
  客户总数: 'users',
  运行中借贷单: 'receipt',
  今日还款登记: 'calendar',
  资料待补客户: 'mail',
}

const resolveMetricIcon = (card: WorkbenchMetric) => metricIconMap[card.title] || 'chart'

const metricArtMap: Record<string, string> = {
  客户总数: 'users',
  运行中借贷单: 'receipt',
  今日还款登记: 'pieChart',
  资料待补客户: 'receipt',
}

const resolveMetricArt = (card: WorkbenchMetric) => metricArtMap[card.title] || 'document'

const todoIconMap: Record<string, string> = {
  检查运行中借贷单余额: 'receipt',
  核对今日还款登记: 'checkCalendar',
  补齐客户关键资料: 'mail',
}

const reminderIconMap: Record<string, string> = {
  danger: 'warningBell',
  warning: 'warningBell',
  success: 'checkCircle',
}

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

const priorityClass = (p: string) => ({
  'is-high': p === '高',
  'is-medium': p === '中',
  'is-low': p === '低',
})

const resolveTodoIcon = (item: WorkbenchTodo) => todoIconMap[item.title] || 'document'
const resolveReminderIcon = (item: WorkbenchReminder) => reminderIconMap[item.tone] || 'warningBell'
</script>

<template>
  <div class="workbench-root">
    <div class="workbench-hero" aria-hidden="true"></div>

        <!-- 操作栏 -->
        <header class="topbar">
          <div class="topbar__actions">
            <el-button type="primary" size="large" @click="goTo('/customers')">
              <IconSymbol name="plus" :size="17" />
              新增客户
            </el-button>
            <el-button size="large" plain @click="goTo('/loan-orders')">登记借贷</el-button>
            <el-button size="large" text @click="resetFilters">
              <IconSymbol name="refresh" :size="17" />
              重置视图
            </el-button>
          </div>
        </header>

        <!-- 指标卡区域 -->
        <section class="stats-grid">
          <article
            v-for="card in metricCards"
            :key="card.title"
            class="metric-card"
            :class="{ 'metric-card--active': card.emphasized }"
          >
            <div class="metric-card__inner">
              <div class="metric-card__top">
                <span class="metric-card__icon">
                  <IconSymbol :name="resolveMetricIcon(card)" :size="18" />
                </span>
                <div class="metric-card__heading">
                  <span class="metric-card__title">{{ card.title }}</span>
                  <span class="metric-card__helper" v-if="card.helper">{{ card.helper }}</span>
                </div>
              </div>
              <strong class="metric-card__value">{{ card.value }}</strong>
            </div>
            <!-- 装饰 radial glow -->
            <div class="metric-card__glow" aria-hidden="true"></div>
            <!-- 右下角业务装饰图标 -->
            <div class="metric-card__art" aria-hidden="true">
              <IconSymbol :name="resolveMetricArt(card)" :size="56" />
            </div>
          </article>
        </section>

        <!-- 主内容区 -->
        <section class="content-grid">
          <!-- 左侧：最近业务记录 -->
          <div class="content-main card" v-loading="loading">
            <div class="card__section">
              <div class="section-header section-header--accent">
                <h3 class="section-title">最近业务记录</h3>
              </div>

              <!-- 筛选区 -->
              <div class="filter-panel">
                <div class="filter-panel__row">
                  <el-input
                    v-model="filters.keyword"
                    placeholder="客户名称 / 客户ID"
                    clearable
                    size="large"
                    class="filter-panel__input"
                  >
                    <template #prefix>
                      <span class="filter-panel__search">
                        <IconSymbol name="search" :size="16" />
                      </span>
                    </template>
                  </el-input>
                  <div class="filter-panel__btns">
                    <el-button type="primary" size="large" @click="handleSearch">查询</el-button>
                    <el-button size="large" plain @click="resetFilters">重置</el-button>
                  </div>
                </div>
              </div>

              <!-- 表格 -->
              <div class="table-wrap">
                <el-table
                  :data="focusRows"
                  class="data-table"
                  table-layout="fixed"
                  stripe
                >
                  <el-table-column prop="customerName" label="客户名称" min-width="180" />
                  <el-table-column prop="recordType" label="记录类型" min-width="120" />
                  <el-table-column prop="relationInfo" label="关联信息" min-width="140" />
                  <el-table-column prop="actionDate" label="最近日期" min-width="120" />
                  <el-table-column prop="status" label="当前状态" min-width="120" />
                  <el-table-column label="操作" width="96">
                    <template #default="{ row }">
                      <el-button text type="primary" @click="goTo(row.routePath)">打开</el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </div>

              <!-- 分页信息 -->
              <div class="table-footer">
                <span class="table-footer__count">{{ focusRows.length }} 条记录</span>
              </div>
            </div>
          </div>

          <!-- 右侧：侧边栏 -->
          <aside class="content-side">

            <!-- 今日待办 -->
            <div class="card side-card">
              <div class="card__section">
                <div class="section-header section-header--accent">
                  <h3 class="section-title">今日待办</h3>
                  <span class="side-pill" v-if="todoItems.length">{{ todoItems.length }} 项</span>
                </div>
                <ul class="todo-list">
                  <li v-for="item in todoItems" :key="item.title" class="todo-item">
                    <span class="todo-item__icon">
                      <IconSymbol :name="resolveTodoIcon(item)" :size="18" />
                    </span>
                    <div class="todo-item__body">
                      <strong class="todo-item__title">{{ item.title }}</strong>
                      <span class="todo-item__deadline">{{ item.deadline }}</span>
                    </div>
                    <span class="todo-item__level" :class="priorityClass(item.level)">
                      {{ item.level }}优先
                    </span>
                  </li>
                  <li v-if="todoItems.length === 0" class="todo-empty">暂无待办事项</li>
                </ul>
              </div>
            </div>

            <!-- 运行提醒 -->
            <div class="card side-card">
              <div class="card__section">
                <div class="section-header section-header--accent">
                  <h3 class="section-title">运行提醒</h3>
                </div>
                <ul class="reminder-list">
                  <li v-for="item in reminderItems" :key="item.title" class="reminder-item">
                    <span class="reminder-icon" :class="`is-${item.tone}`">
                      <IconSymbol :name="resolveReminderIcon(item)" :size="18" />
                    </span>
                    <div class="reminder-item__body">
                      <strong>{{ item.title }}</strong>
                      <span class="reminder-item__tag">{{ item.tag }}</span>
                    </div>
                  </li>
                  <li v-if="reminderItems.length === 0" class="reminder-empty">暂无提醒</li>
                </ul>
              </div>
            </div>

          </aside>
        </section>
  </div>
</template>

<style scoped>
/* ── 根层 ── */
.workbench-root {
  position: relative;
  min-height: 100%;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.workbench-hero {
  position: absolute;
  top: -56px;
  right: -20px;
  width: min(920px, 76vw);
  height: 240px;
  pointer-events: none;
  background:
    radial-gradient(circle at 18% 84%, rgba(255, 255, 255, 0.78), rgba(255, 255, 255, 0) 18%),
    linear-gradient(152deg, transparent 0 42%, rgba(255, 255, 255, 0.55) 42.4%, transparent 43%),
    linear-gradient(164deg, transparent 0 51%, rgba(255, 255, 255, 0.45) 51.35%, transparent 52%),
    linear-gradient(173deg, transparent 0 61%, rgba(255, 255, 255, 0.38) 61.3%, transparent 61.8%);
  opacity: 0.85;
  z-index: 0;
}

/* ── 操作栏 ── */
.topbar {
  display: flex;
  justify-content: flex-end;
  position: relative;
  z-index: 1;
}

.topbar__actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.topbar__actions :deep(.el-button) {
  min-width: 132px;
  height: 46px;
  border-radius: 18px;
  font-size: 16px;
  font-weight: 600;
}

.topbar__actions :deep(.el-button--default.is-plain) {
  border-color: rgba(213, 225, 248, 0.96);
  background: rgba(255, 255, 255, 0.8);
}

.topbar__actions :deep(.el-button.is-text) {
  min-width: auto;
  padding: 0 4px;
  background: transparent;
  color: #5c6f97;
}

/* ── 指标卡网格 ── */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 18px;
  position: relative;
  z-index: 1;
}

.metric-card {
  position: relative;
  display: flex;
  border-radius: 22px;
  border: 1px solid rgba(210, 226, 252, 0.98);
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(248, 251, 255, 0.95));
  box-shadow: 0 14px 34px rgba(91, 126, 193, 0.08);
  padding: 22px 22px 20px;
  min-height: 138px;
  overflow: hidden;
  transition: box-shadow 0.2s ease, border-color 0.2s ease;
}

.metric-card:hover {
  box-shadow: 0 14px 32px rgba(88, 123, 191, 0.12);
}

.metric-card--active {
  background: linear-gradient(180deg, #f8fbff, #edf4ff);
  border-color: #c7d9ff;
}

.metric-card__inner {
  position: relative;
  z-index: 1;
  display: flex;
  flex: 1;
  flex-direction: column;
}

.metric-card__top {
  display: grid;
  grid-template-columns: 36px minmax(0, 1fr);
  align-items: start;
  column-gap: 12px;
}

.metric-card__icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 34px;
  height: 34px;
  border-radius: 12px;
  background: linear-gradient(180deg, #f5f9ff, #edf4ff);
  border: 1px solid rgba(203, 221, 255, 0.92);
  color: #4a85ff;
  flex-shrink: 0;
}

.metric-card__heading {
  display: flex;
  min-width: 0;
  flex-direction: column;
  gap: 6px;
  padding-top: 2px;
}

.metric-card__title {
  display: block;
  font-weight: 700;
  font-size: 15px;
  line-height: 1.35;
  color: #122D5F;
  min-width: 0;
  word-break: break-word;
}

.metric-card__helper {
  display: inline-flex;
  align-items: center;
  justify-content: flex-start;
  min-height: 18px;
  color: #4A85FF;
  font-size: 12px;
  font-weight: 600;
  line-height: 1;
  white-space: nowrap;
}

.metric-card__value {
  display: block;
  margin-top: auto;
  padding-top: 26px;
  font-size: 48px;
  line-height: 1;
  font-weight: 800;
  color: #1a4fba;
}

.metric-card__glow {
  position: absolute;
  left: 18px;
  bottom: -18px;
  width: 128px;
  height: 72px;
  background: radial-gradient(circle, rgba(124, 170, 255, 0.12), rgba(124, 170, 255, 0) 72%);
  pointer-events: none;
}

.metric-card__art {
  position: absolute;
  right: 14px;
  bottom: 8px;
  opacity: 0.12;
  pointer-events: none;
  color: #8db6ff;
}

/* ── 内容网格 ── */
.content-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.8fr) minmax(320px, 0.82fr);
  gap: 16px;
  align-items: start;
  position: relative;
  z-index: 1;
}

/* ── Section Header ── */
.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.section-title {
  margin: 0;
  font-size: 20px;
  font-weight: 800;
  color: #122D5F;
}

.section-header--accent {
  position: relative;
  padding-left: 22px;
}

.section-header--accent::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  width: 8px;
  height: 24px;
  border-radius: 999px;
  transform: translateY(-50%);
  background: linear-gradient(180deg, #7eb0ff, #356bf5);
  box-shadow: 0 6px 16px rgba(53, 107, 245, 0.18);
}

/* ── 主内容卡片 ── */
.content-main {
  border-radius: 24px;
  min-width: 0;
}

/* ── 筛选区 ── */
.filter-panel {
  margin-top: 18px;
  padding: 16px 18px;
  border: 1px solid #dbe8ff;
  border-radius: 18px;
  background: linear-gradient(180deg, #f8fbff, #f5f9ff);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.9);
}

.filter-panel__row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.filter-panel__input {
  flex: 1;
}

.filter-panel__search {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: #7d93bc;
}

.filter-panel__btns {
  display: flex;
  gap: 10px;
  flex-shrink: 0;
}

.filter-panel__btns :deep(.el-button) {
  min-width: 88px;
  height: 42px;
  border-radius: 14px;
}

/* ── 表格 ── */
.table-wrap {
  margin-top: 16px;
  overflow-x: auto;
  border-radius: 18px;
  border: 1px solid #edf3ff;
}

:deep(.data-table) {
  min-width: 760px;
  border-radius: 18px;
  overflow: hidden;
  border: none;
}

:deep(.data-table .el-table__header th) {
  background: #f8fbff;
  color: #7085AD;
  font-size: 13px;
  font-weight: 700;
  padding: 12px 18px;
  border-bottom: 1px solid #dbe8ff;
}

:deep(.data-table .el-table__cell) {
  padding: 12px 18px;
  font-size: 14px;
  line-height: 1.65;
  color: #122D5F;
  border-bottom: 1px solid #edf3ff;
}

/* ── 表格 Footer ── */
.table-footer {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  margin-top: 14px;
}

.table-footer__count {
  color: #7085AD;
  font-size: 13px;
}

/* ── 侧边栏 ── */
.content-side {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.side-card {
  border-radius: 24px;
}

.side-pill {
  display: inline-flex;
  align-items: center;
  height: 30px;
  padding: 0 12px;
  border-radius: 999px;
  border: 1px solid rgba(74, 133, 255, 0.18);
  background: rgba(74, 133, 255, 0.08);
  color: #4A85FF;
  font-size: 14px;
  font-weight: 600;
}

/* ── 待办列表 ── */
.todo-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin: 14px 0 0;
  padding: 0;
  list-style: none;
}

.todo-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  padding: 14px 16px;
  border: 1px solid #dbe8ff;
  border-radius: 16px;
  background: linear-gradient(180deg, #f9fbff, #f6faff);
}

.todo-item__icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 44px;
  border-radius: 16px;
  background: linear-gradient(180deg, #77a7ff, #4b7df8);
  color: #ffffff;
  box-shadow: 0 10px 20px rgba(53, 107, 245, 0.18);
  flex-shrink: 0;
}

.todo-item__body {
  flex: 1;
  min-width: 0;
}

.todo-item__title {
  display: block;
  font-size: 15px;
  font-weight: 600;
  color: #122D5F;
  line-height: 1.45;
}

.todo-item__deadline {
  display: block;
  margin-top: 3px;
  color: #7085AD;
  font-size: 13px;
}

.todo-item__level {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 70px;
  height: 32px;
  padding: 0 10px;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 600;
  flex-shrink: 0;
}

.todo-item__level.is-high {
  background: rgba(239, 115, 134, 0.1);
  color: #EF7386;
}

.todo-item__level.is-medium {
  background: rgba(219, 125, 20, 0.1);
  color: #DB7D14;
}

.todo-item__level.is-low {
  background: rgba(24, 166, 157, 0.1);
  color: #18A69D;
}

.todo-empty,
.reminder-empty {
  color: #95A7C4;
  font-size: 13px;
  text-align: center;
  padding: 20px 0;
}

/* ── 提醒列表 ── */
.reminder-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin: 14px 0 0;
  padding: 0;
  list-style: none;
}

.reminder-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
}

.reminder-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 12px;
  flex-shrink: 0;
}

.reminder-icon.is-danger {
  color: #ff8a35;
  background: rgba(255, 138, 53, 0.12);
}

.reminder-icon.is-warning {
  color: #2f6cff;
  background: rgba(47, 108, 255, 0.1);
}

.reminder-icon.is-success {
  color: #18a69d;
  background: rgba(24, 166, 157, 0.1);
}

.reminder-item__body {
  flex: 1;
  min-width: 0;
}

.reminder-item__body strong {
  display: block;
  font-size: 15px;
  font-weight: 600;
  color: #122D5F;
  line-height: 1.4;
}

.reminder-item__tag {
  display: block;
  margin-top: 3px;
  color: #7085AD;
  font-size: 13px;
}

/* ── 响应式 ── */
@media (max-width: 1280px) {
  .stats-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 1024px) {
  .content-grid {
    grid-template-columns: 1fr;
  }

  .content-side {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 14px;
  }

  .filter-panel__row {
    flex-wrap: wrap;
  }

  .filter-panel__input {
    min-width: 200px;
  }
}

@media (max-width: 720px) {
  .main-surface {
    padding: 16px 14px;
    border-radius: 24px;
    gap: 14px;
  }

  .topbar__actions {
    width: 100%;
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 8px;
  }

  .topbar__actions :deep(.el-button) {
    width: 100%;
  }

  .stats-grid {
    grid-template-columns: 1fr 1fr;
    gap: 12px;
  }

  .metric-card {
    min-height: 148px;
    padding: 16px 16px 16px;
    border-radius: 20px;
  }

  .metric-card__top {
    grid-template-columns: 34px minmax(0, 1fr);
    max-width: calc(100% - 54px);
  }

  .metric-card__title {
    font-size: 16px;
  }

  .metric-card__heading {
    gap: 5px;
  }

  .metric-card__value {
    font-size: 34px;
    padding-top: 18px;
  }

  .content-side {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>
