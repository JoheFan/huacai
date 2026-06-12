<script setup lang="ts">
import { HomeFilled } from '@element-plus/icons-vue'
import IconSymbol from '../components/IconSymbol.vue'
import SidebarArt from '../components/SidebarArt.vue'
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getVisibleMenuGroups } from '../access/moduleAccess'
import { useAuthStore } from '../stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const currentTitle = computed(() => (route.meta.title as string) ?? '华彩系统')
const currentSubtitle = computed(() => (isHomeRoute.value ? '欢迎使用华彩经营管理平台' : ''))
const currentUserName = computed(() => authStore.user?.realName ?? '管理员')
const currentOrgName = computed(() => authStore.user?.orgName ?? '总部')
const currentInitial = computed(() => currentUserName.value.slice(0, 1))
const visibleMenuGroups = computed(() => getVisibleMenuGroups(authStore.user))
const homePath = computed(() => '/dashboard')
const homeLabel = computed(() => '工作台')
const isHomeRoute = computed(() => route.path === homePath.value)
const navigationGroups = computed(() =>
  visibleMenuGroups.value.filter((group) => !group.items.some((item) => item.path === homePath.value))
)
const isMenuItemActive = (path: string) => route.path === path || route.path.startsWith(`${path}/`)
const isGroupActive = (group: { items: Array<{ path: string }> }) =>
  group.items.some((item) => isMenuItemActive(item.path))

const findGroupByRoute = (path: string): string | null => {
  for (const group of visibleMenuGroups.value) {
    for (const item of group.items) {
      if (item.path === path || path.startsWith(`${item.path}/`)) {
        return group.title
      }
    }
  }
  return null
}

const openGroups = ref(new Set<string>())
const sidebarCollapsed = ref(false)

const openGroup = (title: string) => {
  if (openGroups.value.has(title)) return
  const next = new Set(openGroups.value)
  next.add(title)
  openGroups.value = next
}

watch(
  () => route.path,
  (path) => {
    const group = findGroupByRoute(path)
    if (group) openGroup(group)
  },
  { immediate: true }
)

const toggleGroup = (title: string) => {
  const next = new Set(openGroups.value)
  if (next.has(title)) {
    next.delete(title)
  } else {
    next.add(title)
  }
  openGroups.value = next
}

const toggleSidebar = () => {
  sidebarCollapsed.value = !sidebarCollapsed.value
}

const handleLogout = async () => {
  await authStore.logout()
  router.push('/login')
}

const goDashboard = () => {
  router.push(homePath.value)
}
</script>

<template>
  <div class="layout-root">
    <div class="layout-root__bg" aria-hidden="true"></div>

    <div class="shell" :class="{ 'shell--collapsed': sidebarCollapsed }">
      <!-- 左侧导航 -->
      <aside class="sidebar" :class="{ 'is-collapsed': sidebarCollapsed }">
        <!-- 屋脊高光层 -->
        <div class="sidebar__roof" aria-hidden="true"></div>

        <!-- 品牌区 -->
        <button class="brand-block" type="button" aria-label="返回工作台" @click="goDashboard">
          <div class="brand-block__top">
            <span class="brand-block__eyebrow">华彩系统</span>
            <span class="brand-block__badge">经营平台</span>
          </div>
          <strong class="brand-block__title">华彩经营管理平台</strong>
        </button>

        <!-- 折叠按钮 -->
        <button
          class="sidebar-collapse-btn"
          :class="{ 'is-collapsed': sidebarCollapsed }"
          type="button"
          :aria-label="sidebarCollapsed ? '展开侧栏' : '折叠侧栏'"
          @click="toggleSidebar"
        >
          <svg viewBox="0 0 1024 1024" width="16" height="16">
            <path v-if="!sidebarCollapsed" d="M672 128c-8.4 0-16.8 3.2-23.2 9.6L296 490.4c-13.6 13.6-13.6 34.4 0 48s34.4 13.6 48 0l320-320c12.8-12.8 12.8-32.8 0-45.6L344 118.4c-6.4-6.4-14.4-9.6-23.2-9.6zM352 544c8.4 0 16.8-3.2 23.2-9.6l352-352c13.6-13.6 13.6-34.4 0-48s-34.4-13.6-48 0L376 486.4c-12.8 12.8-12.8 32.8 0 45.6l320 320c6.4 6.4 14.4 9.6 23.2 9.6z" fill="currentColor"/>
            <path v-else d="M352 128c-8.4 0-16.8 3.2-23.2 9.6L680 490.4c13.6 13.6 13.6 34.4 0 48s-34.4 13.6-48 0l-320-320c-12.8-12.8-12.8-32.8 0-45.6l320-320c6.4-6.4 14.4-9.6 23.2-9.6zM672 544c8.4 0 16.8-3.2 23.2-9.6L344 182.4c-13.6-13.6-13.6-34.4 0-48s34.4-13.6 48 0l320 320c12.8 12.8 12.8 32.8 0 45.6L696 534.4c-6.4 6.4-14.4 9.6-23.2 9.6z" fill="currentColor"/>
          </svg>
        </button>

        <!-- 导航菜单 -->
        <nav class="sidebar__nav" aria-label="主导航">
          <div v-for="group in navigationGroups" :key="group.title" class="nav-group">
            <button
              class="nav-group__header"
              :class="{ 'is-open': openGroups.has(group.title), 'is-current': isGroupActive(group) }"
              type="button"
              :aria-expanded="openGroups.has(group.title)"
              @click="toggleGroup(group.title)"
            >
              <span class="nav-group__label">
                <span class="nav-group__icon">
                  <IconSymbol :name="group.icon || group.items[0]?.icon || 'home'" :size="20" />
                </span>
                <span class="nav-group__text">{{ group.title }}</span>
              </span>
              <span class="nav-group__arrow">
                <svg viewBox="0 0 1024 1024" width="12" height="12">
                  <path d="M512 672c-8.4 0-16.8-3.2-23.2-9.6L136 309.6c-13.6-13.6-13.6-34.4 0-48s34.4-13.6 48 0l320 320c12.8 12.8 32.8 12.8 45.6 0l320-320c13.6-13.6 13.6-34.4 0-48s-34.4-13.6-48 0L536 662.4c-6.4 6.4-14.4 9.6-23.2 9.6z" fill="currentColor"/>
                </svg>
              </span>
            </button>

            <div v-show="openGroups.has(group.title)" class="nav-group__items" role="group">
              <router-link
                v-for="item in group.items"
                :key="item.path"
                :to="item.path"
                class="nav-item"
                :class="{ 'is-active': isMenuItemActive(item.path) }"
                :title="item.title"
              >
                <span class="nav-item__icon">
                  <IconSymbol :name="item.icon || 'customer'" :size="16" />
                </span>
                <span class="nav-item__label">{{ item.title }}</span>
              </router-link>
            </div>
          </div>
        </nav>

        <!-- 底部装饰艺术 -->
        <SidebarArt class="sidebar__art" />
      </aside>

      <!-- 右侧主内容 -->
      <div class="main-surface">
        <!-- Header -->
        <header class="top-header">
          <div class="top-header__main">
            <h1 class="top-header__title" :class="{ 'is-home': isHomeRoute }">{{ currentTitle }}</h1>
            <p v-if="currentSubtitle" class="top-header__subtitle">{{ currentSubtitle }}</p>
          </div>

          <div class="top-header__actions">
            <el-button v-if="!isHomeRoute" size="default" plain @click="goDashboard">
              <el-icon><HomeFilled /></el-icon>
              {{ homeLabel }}
            </el-button>

            <div class="user-card">
              <span class="user-card__avatar">{{ currentInitial }}</span>
              <div class="user-card__info">
                <strong>{{ currentUserName }}</strong>
                <span>{{ currentOrgName }}</span>
              </div>
            </div>

            <el-button size="default" text class="top-header__logout" @click="handleLogout">退出</el-button>
          </div>
        </header>

        <!-- 页面内容 -->
        <main class="main-content">
          <router-view />
        </main>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* ── 根层 ── */
.layout-root {
  position: relative;
  min-height: 100vh;
  padding: 14px;
  box-sizing: border-box;
}

.layout-root__bg {
  position: fixed;
  inset: 0;
  z-index: -1;
  background:
    radial-gradient(circle at 12% -8%, rgba(116, 162, 255, 0.18), transparent 28%),
    radial-gradient(circle at 92% 14%, rgba(149, 185, 255, 0.12), transparent 22%),
    linear-gradient(180deg, #EEF4FF 0%, #F8FBFF 100%);
  pointer-events: none;
}

/* ── 外层 grid 容器 ── */
.shell {
  width: min(1800px, calc(100vw - 28px));
  margin: 0 auto;
  min-height: calc(100vh - 28px);
  display: grid;
  grid-template-columns: 286px 1fr;
  gap: 22px;
  min-width: 0;
}

@media (max-width: 1420px) {
  .shell {
    grid-template-columns: 264px 1fr;
  }
}

@media (max-width: 1180px) {
  .shell {
    grid-template-columns: 220px 1fr;
  }
}

@media (max-width: 960px) {
  .shell {
    grid-template-columns: 1fr;
  }
}

/* ── 左侧 Sidebar ── */
.sidebar {
  position: relative;
  border-radius: 32px;
  padding: 24px 20px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.82), rgba(248, 251, 255, 0.94));
  border: 1px solid rgba(216, 229, 255, 0.9);
  box-shadow: 0 18px 50px rgba(77, 120, 200, 0.12);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

/* 屋脊高光 */
.sidebar__roof {
  position: absolute;
  inset: 0;
  pointer-events: none;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.5), rgba(255, 255, 255, 0) 22%),
    radial-gradient(circle at 15% 16%, rgba(135, 177, 255, 0.14), transparent 24%);
  border-radius: inherit;
}

/* ── 品牌区 ── */
.brand-block {
  position: relative;
  z-index: 2;
  width: 100%;
  padding: 6px 66px 22px 8px;
  border: 0;
  border-bottom: 1px solid var(--hc-divider);
  background: transparent;
  color: inherit;
  font: inherit;
  text-align: left;
  cursor: pointer;
  border-radius: 18px;
  transition: background 0.15s ease;
  margin-bottom: 4px;
}

.brand-block:hover {
  background: rgba(74, 133, 255, 0.06);
}

.brand-block__top {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 10px;
  min-height: 28px;
  flex-wrap: wrap;
}

.brand-block__eyebrow,
.brand-block__badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 28px;
  padding: 0 12px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
  white-space: nowrap;
}

.brand-block__eyebrow {
  background: linear-gradient(180deg, #6DA0FF, #356BF5);
  color: #ffffff;
  box-shadow: 0 6px 16px rgba(53, 107, 245, 0.26);
}

.brand-block__badge {
  border: 1px solid var(--hc-border);
  color: var(--hc-text-soft);
  background: rgba(255, 255, 255, 0.84);
}

.brand-block__title {
  display: block;
  margin-top: 14px;
  font-size: 19px;
  line-height: 1.18;
  font-weight: 800;
  color: var(--hc-text);
  letter-spacing: 0.1px;
  white-space: nowrap;
}

/* ── 导航 ── */
.sidebar__nav {
  position: relative;
  z-index: 2;
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 16px 2px 8px;
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  overflow-x: hidden;
}

.nav-group {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

/* Group 折叠头 — 参考 72px nav-item 风格 */
.nav-group__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  width: 100%;
  min-height: 40px;
  padding: 6px 10px;
  border: none;
  border-radius: 12px;
  background: transparent;
  color: var(--hc-text);
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  transition: background 0.18s ease, color 0.18s ease;
  text-align: left;
}

.nav-group__header:hover {
  background: linear-gradient(180deg, rgba(240, 246, 255, 0.9), rgba(235, 243, 255, 0.75));
  color: var(--hc-primary-hover);
}

.nav-group__header.is-open,
.nav-group__header.is-current {
  background: linear-gradient(180deg, rgba(240, 246, 255, 0.9), rgba(233, 241, 255, 0.75));
  color: var(--hc-primary-hover);
}

.nav-group__label {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  min-width: 0;
}

/* 图标底板 — 参考 42x42 梯度风格 */
.nav-group__icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border-radius: 9px;
  background: linear-gradient(180deg, #F7FAFF, #EDF3FF);
  border: 1px solid #E2ECFF;
  color: var(--hc-primary-hover);
  font-size: 17px;
  flex-shrink: 0;
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.95);
  transition: background 0.18s ease, box-shadow 0.18s ease;
}

.nav-group__header:hover .nav-group__icon,
.nav-group__header.is-open .nav-group__icon,
.nav-group__header.is-current .nav-group__icon {
  background: linear-gradient(180deg, #6DA0FF, #356BF5);
  border-color: rgba(118, 164, 255, 0.9);
  color: #ffffff;
  box-shadow: 0 6px 16px rgba(53, 107, 245, 0.28);
}

.nav-group__arrow {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border-radius: 10px;
  color: #7D93BC;
  transition: transform 0.22s ease;
  flex-shrink: 0;
}

.nav-group__header.is-open .nav-group__arrow {
  transform: rotate(180deg);
  color: var(--hc-primary-hover);
}

/* 子菜单 */
.nav-group__items {
  position: relative;
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 5px 0 8px 18px;
  margin-left: 10px;
}

.nav-group__items::before {
  content: '';
  position: absolute;
  left: 2px;
  top: 10px;
  bottom: 10px;
  width: 1px;
  border-radius: 999px;
  background: rgba(160, 191, 255, 0.4);
}

.nav-item {
  position: relative;
  display: flex;
  align-items: center;
  gap: 10px;
  min-height: 34px;
  padding: 4px 10px 4px 8px;
  border-radius: 10px;
  color: var(--hc-text-soft);
  font-size: 14px;
  font-weight: 600;
  transition: background 0.15s ease, color 0.15s ease;
}

.nav-item:hover {
  background: linear-gradient(180deg, rgba(240, 246, 255, 0.86), rgba(235, 243, 255, 0.72));
  color: var(--hc-primary-hover);
}

.nav-item.is-active {
  background: linear-gradient(180deg, rgba(74, 133, 255, 0.1), rgba(74, 133, 255, 0.06));
  color: var(--hc-primary-hover);
}

.nav-item.is-active::before {
  content: '';
  position: absolute;
  left: -10px;
  top: 50%;
  transform: translateY(-50%);
  width: 5px;
  height: 5px;
  background: var(--hc-primary);
  border-radius: 999px;
}

.nav-item__icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border-radius: 8px;
  background: rgba(74, 133, 255, 0.06);
  color: var(--hc-text-muted);
  font-size: 14px;
  flex-shrink: 0;
  transition: background 0.15s ease, color 0.15s ease;
}

.nav-item.is-active .nav-item__icon {
  background: rgba(255, 255, 255, 0.88);
  color: var(--hc-primary);
  box-shadow: inset 0 0 0 1px rgba(74, 133, 255, 0.1);
}

.nav-item:hover .nav-item__icon {
  background: rgba(74, 133, 255, 0.1);
  color: var(--hc-primary);
}

.nav-item__label {
  min-width: 0;
  line-height: 1.2;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* ── 右侧主内容 ── */
.main-surface {
  border-radius: 16px;
  padding: 16px 18px 18px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.82), rgba(248, 251, 255, 0.94));
  border: 1px solid rgba(216, 229, 255, 0.9);
  box-shadow: 0 8px 24px rgba(77, 120, 200, 0.1);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  display: flex;
  flex-direction: column;
  min-height: calc(100vh - 28px);
  min-width: 0;
}

/* ── 顶部 Header ── */
.top-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--hc-divider);
  margin-bottom: 16px;
}

.top-header__main {
  min-width: 0;
}

.top-header__title {
  margin: 0;
  font-size: 26px;
  line-height: 1.2;
  font-weight: 700;
  color: var(--hc-text);
  letter-spacing: -0.01em;
}

.top-header__title.is-home {
  font-size: 42px;
  line-height: 1.08;
  font-weight: 800;
}

.top-header__subtitle {
  margin: 10px 0 0;
  color: var(--hc-text-soft);
  font-size: 17px;
  line-height: 1.35;
}

.top-header__actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 12px;
  flex-shrink: 0;
}

/* ── 用户卡片 ── */
.user-card {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 184px;
  padding: 10px 16px;
  border: 1px solid var(--hc-border);
  border-radius: 22px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.92), rgba(247, 250, 255, 0.88));
  box-shadow: 0 10px 24px rgba(88, 123, 191, 0.08);
}

.user-card__avatar {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 14px;
  background: linear-gradient(180deg, #6DA0FF, #356BF5);
  color: #ffffff;
  font-size: 14px;
  font-weight: 700;
  box-shadow: 0 6px 14px rgba(53, 107, 245, 0.24);
}

.user-card__info {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 3px;
  min-width: 0;
}

.user-card__info strong {
  font-size: 14px;
  line-height: 1.2;
  color: var(--hc-text);
}

.user-card__info span {
  color: var(--hc-text-soft);
  font-size: 12px;
  line-height: 1.2;
}

.top-header__logout {
  color: var(--hc-text-soft);
}

/* ── 主内容 ── */
.main-content {
  flex: 1;
}

/* ── 响应式 ── */
@media (max-width: 1180px) {
  .layout-root {
    padding: 12px;
  }

  .shell {
    gap: 12px;
  }

  .top-header {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }

  .top-header__actions {
    justify-content: flex-start;
    flex-wrap: wrap;
  }
}

@media (max-width: 960px) {
  .layout-root {
    padding: 10px;
  }

  .shell {
    grid-template-columns: 1fr;
    min-height: auto;
  }

  .sidebar {
    padding: 16px 14px;
    min-height: auto;
    max-height: none;
  }

  .sidebar__nav {
    padding: 12px 2px 8px;
  }

  .sidebar__art {
    display: none;
  }

  .top-header__title {
    font-size: 24px;
  }

  .top-header__title.is-home {
    font-size: 34px;
  }

  .top-header__subtitle {
    margin-top: 8px;
    font-size: 15px;
  }

  .brand-block__title {
    font-size: 18px;
  }

  .main-surface {
    min-height: auto;
    padding: 16px 18px;
  }
}

@media (max-width: 720px) {
  .layout-root {
    padding: 8px;
  }

  .top-header__actions {
    gap: 8px;
  }

  .user-card {
    padding: 6px 10px;
    flex: 1 1 auto;
  }

  .user-card__info span {
    display: none;
  }

  .brand-block__badge {
    display: none;
  }

  .brand-block__top {
    flex-wrap: wrap;
  }

  .brand-block__title {
    font-size: 16px;
    margin-top: 10px;
  }

  .nav-group__header {
    min-height: 46px;
    font-size: 14px;
  }
}

/* ── 侧栏折叠 ── */
.sidebar-collapse-btn {
  position: absolute;
  right: 16px;
  top: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 30px;
  height: 30px;
  border: 1px solid var(--hc-border);
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.92);
  color: var(--hc-text-soft);
  cursor: pointer;
  box-shadow: 0 6px 16px rgba(88, 123, 191, 0.08);
  transition: all 0.2s ease;
  z-index: 10;
}

.sidebar-collapse-btn:hover {
  background: linear-gradient(180deg, #6DA0FF, #356BF5);
  color: #ffffff;
  border-color: rgba(118, 164, 255, 0.9);
  box-shadow: 0 8px 18px rgba(53, 107, 245, 0.18);
}

.sidebar-collapse-btn.is-collapsed {
  top: 50%;
  transform: translateY(-50%);
  right: 50%;
  margin-right: -16px;
}

/* Collapsed sidebar - icon-only nav */
.sidebar.is-collapsed {
  width: 84px;
  min-width: 84px;
  padding: 20px 12px;
}

.sidebar.is-collapsed .brand-block {
  padding: 4px 0 16px;
  border-bottom-color: transparent;
  margin-bottom: 0;
}

.sidebar.is-collapsed .brand-block__top {
  justify-content: center;
}

.sidebar.is-collapsed .brand-block__eyebrow,
.sidebar.is-collapsed .brand-block__badge,
.sidebar.is-collapsed .brand-block__title {
  display: none;
}

.sidebar.is-collapsed .nav-group__header {
  justify-content: center;
  padding: 8px;
  min-height: 48px;
}

.sidebar.is-collapsed .nav-group__text {
  display: none;
}

.sidebar.is-collapsed .nav-group__icon {
  width: 34px;
  height: 34px;
}

.sidebar.is-collapsed .nav-group__arrow {
  display: none;
}

/* Show items as icon-only vertical strip */
.sidebar.is-collapsed .nav-group__items {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding: 6px 0;
  margin-left: 0;
}

.sidebar.is-collapsed .nav-group__items::before {
  display: none;
}

.sidebar.is-collapsed .nav-item {
  justify-content: center;
  padding: 8px 4px;
  min-height: 38px;
  width: 56px;
}

.sidebar.is-collapsed .nav-item__label {
  display: none;
}

.sidebar.is-collapsed .nav-item__icon {
  width: 28px;
  height: 28px;
}

.sidebar.is-collapsed .nav-item::before {
  display: none;
}

.sidebar.is-collapsed .nav-item__icon {
  background: transparent;
}

.sidebar.is-collapsed .nav-item:hover .nav-item__icon {
  background: rgba(74, 133, 255, 0.1);
}

.sidebar.is-collapsed .nav-item.is-active .nav-item__icon {
  background: rgba(255, 255, 255, 0.88);
}

.sidebar.is-collapsed .sidebar__art {
  display: none;
}

/* Collapsed shell grid - desktop only */
@media (min-width: 961px) {
  .shell--collapsed {
    grid-template-columns: 84px 1fr;
  }
}

@media (max-width: 960px) {
  .shell,
  .shell.shell--collapsed {
    grid-template-columns: 1fr;
  }

  .sidebar.is-collapsed {
    width: auto;
    min-width: 0;
  }
}
</style>
