<script setup lang="ts">
import {
  CollectionTag,
  Coin,
  CreditCard,
  DataAnalysis,
  Document,
  DocumentChecked,
  HomeFilled,
  Money,
  OfficeBuilding,
  Pointer,
  SetUp,
  Stamp,
  TrendCharts,
  User,
  UserFilled,
} from '@element-plus/icons-vue'
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getVisibleMenuGroups } from '../access/moduleAccess'
import { useAuthStore } from '../stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const iconMap = {
  CollectionTag,
  Coin,
  CreditCard,
  DataAnalysis,
  Document,
  DocumentChecked,
  HomeFilled,
  Money,
  OfficeBuilding,
  Pointer,
  SetUp,
  Stamp,
  TrendCharts,
  User,
  UserFilled,
}

const currentTitle = computed(() => (route.meta.title as string) ?? '华彩系统')
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

const resolveIcon = (icon: string) => iconMap[icon as keyof typeof iconMap] ?? HomeFilled
const isMenuItemActive = (path: string) => route.path === path || route.path.startsWith(`${path}/`)
const isGroupActive = (group: { items: Array<{ path: string }> }) =>
  group.items.some((item) => isMenuItemActive(item.path))

// Find which group contains the current route
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

// Groups that are currently expanded
const openGroups = ref(new Set<string>())

const openGroup = (title: string) => {
  if (openGroups.value.has(title)) return

  const next = new Set(openGroups.value)
  next.add(title)
  openGroups.value = next
}

// Sync open groups when route changes
watch(
  () => route.path,
  (path) => {
    const group = findGroupByRoute(path)
    if (group) {
      openGroup(group)
    }
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

const handleLogout = async () => {
  await authStore.logout()
  router.push('/login')
}

const goDashboard = () => {
  router.push(homePath.value)
}
</script>

<template>
  <el-container class="app-layout">
    <el-aside class="app-layout__aside">
      <div class="aside-shell card">
        <button class="brand-block" type="button" aria-label="返回工作台" @click="goDashboard">
          <div class="brand-block__top">
            <span class="brand-block__eyebrow">华彩系统</span>
            <span class="brand-block__badge">经营平台</span>
          </div>
          <strong class="brand-block__title">华彩经营管理平台</strong>
        </button>

        <div class="aside-menu">
          <div v-for="group in navigationGroups" :key="group.title" class="aside-menu__group">
            <button
              class="aside-menu__group-header"
              :class="{ 'is-open': openGroups.has(group.title), 'is-current': isGroupActive(group) }"
              type="button"
              :aria-expanded="openGroups.has(group.title)"
              @click="toggleGroup(group.title)"
            >
              <span class="aside-menu__group-label">
                <span class="aside-menu__group-icon">
                  <el-icon>
                    <component :is="resolveIcon(group.items[0]?.icon || 'HomeFilled')" />
                  </el-icon>
                </span>
                <span>{{ group.title }}</span>
              </span>
              <span class="aside-menu__group-arrow">
                <svg viewBox="0 0 1024 1024" width="12" height="12">
                  <path d="M512 672c-8.4 0-16.8-3.2-23.2-9.6L136 309.6c-13.6-13.6-13.6-34.4 0-48s34.4-13.6 48 0l320 320c12.8 12.8 32.8 12.8 45.6 0l320-320c13.6-13.6 13.6-34.4 0-48s-34.4-13.6-48 0L536 662.4c-6.4 6.4-14.4 9.6-23.2 9.6z" fill="currentColor"/>
                </svg>
              </span>
            </button>

            <div v-show="openGroups.has(group.title)" class="aside-menu__items" role="group">
              <router-link
                v-for="item in group.items"
                :key="item.path"
                :to="item.path"
                class="aside-menu__item"
                :class="{ 'is-active': isMenuItemActive(item.path) }"
              >
                <span class="aside-menu__item-icon">
                  <el-icon>
                    <component :is="resolveIcon(item.icon)" />
                  </el-icon>
                </span>
                <span class="aside-menu__item-label">{{ item.title }}</span>
              </router-link>
            </div>
          </div>
        </div>
      </div>
    </el-aside>

    <el-container class="app-layout__content">
      <el-header class="app-layout__header">
        <div class="app-layout__header-main">
          <h1 class="app-layout__header-title">{{ currentTitle }}</h1>
        </div>

        <div class="app-layout__header-actions">
          <el-button v-if="!isHomeRoute" size="small" plain @click="goDashboard">
            <el-icon><HomeFilled /></el-icon>
            {{ homeLabel }}
          </el-button>

          <div class="app-layout__user">
            <span class="app-layout__user-avatar">{{ currentInitial }}</span>
            <div class="app-layout__user-info">
              <strong>{{ currentUserName }}</strong>
              <span>{{ currentOrgName }}</span>
            </div>
          </div>

          <el-button size="small" text @click="handleLogout">退出</el-button>
        </div>
      </el-header>

      <el-main class="app-layout__main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.app-layout {
  min-height: 100vh;
  padding: 18px;
  gap: 18px;
}

.app-layout__aside {
  display: flex;
  width: 248px;
  flex-shrink: 0;
}

.aside-shell {
  width: 100%;
  padding: 18px 14px;
  border-radius: var(--hc-panel-radius);
}

.brand-block {
  width: 100%;
  padding: 4px 8px 16px;
  border: 0;
  border-bottom: 1px solid var(--hc-border);
  background: transparent;
  color: inherit;
  font: inherit;
  text-align: left;
  cursor: pointer;
}

.brand-block:hover .brand-block__title,
.brand-block:hover .brand-block__eyebrow {
  color: var(--hc-primary);
}

.brand-block__top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.brand-block__eyebrow,
.brand-block__badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 28px;
  padding: 0 11px;
  border-radius: 999px;
  font-size: 13px;
  white-space: nowrap;
}

.brand-block__eyebrow {
  background: var(--hc-primary-soft);
  color: var(--hc-primary);
  font-weight: 600;
}

.brand-block__badge {
  border: 1px solid var(--hc-border);
  color: var(--hc-text-soft);
  background: var(--hc-surface-secondary);
}

.brand-block__title {
  display: block;
  margin-top: 14px;
  font-size: 23px;
  line-height: 1.25;
  font-weight: 700;
}

.aside-menu {
  display: flex;
  flex-direction: column;
  gap: 9px;
  padding: 16px 2px 4px;
}

.aside-menu__group {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.aside-menu__group-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  width: 100%;
  min-height: 46px;
  padding: 7px 10px;
  border: none;
  border-radius: 14px;
  background: transparent;
  color: var(--hc-text);
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  transition:
    background 0.15s ease,
    color 0.15s ease;
  text-align: left;
}

.aside-menu__group-header:hover {
  background: rgba(37, 99, 235, 0.055);
  color: var(--hc-primary);
}

.aside-menu__group-header.is-open,
.aside-menu__group-header.is-current {
  background: rgba(37, 99, 235, 0.045);
  color: var(--hc-primary);
}

.aside-menu__group-label {
  display: inline-flex;
  align-items: center;
  gap: 11px;
  flex: 1;
  min-width: 0;
}

.aside-menu__group-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 34px;
  height: 34px;
  border-radius: 12px;
  background: rgba(15, 23, 42, 0.045);
  color: var(--hc-text-soft);
  font-size: 16px;
  flex-shrink: 0;
  transition:
    background 0.15s ease,
    color 0.15s ease,
    box-shadow 0.15s ease;
}

.aside-menu__group-header:hover .aside-menu__group-icon,
.aside-menu__group-header.is-open .aside-menu__group-icon,
.aside-menu__group-header.is-current .aside-menu__group-icon {
  background: rgba(37, 99, 235, 0.11);
  color: var(--hc-primary);
  box-shadow: inset 0 0 0 1px rgba(37, 99, 235, 0.06);
}

.aside-menu__group-arrow {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border-radius: 9px;
  color: currentColor;
  transition: transform 0.2s ease;
}

.aside-menu__group-header.is-open .aside-menu__group-arrow {
  transform: rotate(180deg);
}

.aside-menu__items {
  position: relative;
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 3px 0 5px 18px;
  margin-left: 14px;
}

.aside-menu__items::before {
  content: '';
  position: absolute;
  left: 2px;
  top: 7px;
  bottom: 10px;
  width: 1px;
  border-radius: 999px;
  background: rgba(148, 163, 184, 0.28);
}

.aside-menu__item {
  position: relative;
  display: flex;
  align-items: center;
  gap: 10px;
  min-height: 42px;
  padding: 6px 10px 6px 8px;
  border-radius: 12px;
  color: var(--hc-text);
  transition:
    background 0.15s ease,
    color 0.15s ease,
    box-shadow 0.15s ease;
}

.aside-menu__item:hover {
  background: rgba(15, 23, 42, 0.035);
  color: var(--hc-primary);
}

.aside-menu__item.is-active {
  background: rgba(37, 99, 235, 0.1);
  color: var(--hc-primary);
  box-shadow: inset 2px 0 0 var(--hc-primary);
}

.aside-menu__item.is-active::before {
  content: '';
  position: absolute;
  left: -17px;
  top: 50%;
  transform: translateY(-50%);
  width: 6px;
  height: 6px;
  background: var(--hc-primary);
  border-radius: 999px;
}

.aside-menu__item-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 30px;
  height: 30px;
  border-radius: 10px;
  background: rgba(15, 23, 42, 0.045);
  color: var(--hc-text-soft);
  font-size: 15px;
  flex-shrink: 0;
  transition:
    background 0.15s ease,
    color 0.15s ease;
}

.aside-menu__item.is-active .aside-menu__item-icon {
  background: rgba(255, 255, 255, 0.82);
  color: var(--hc-primary);
}

.aside-menu__item:hover .aside-menu__item-icon {
  background: rgba(37, 99, 235, 0.08);
  color: var(--hc-primary);
}

.aside-menu__item-label {
  min-width: 0;
  font-size: 14px;
  font-weight: 600;
  line-height: 1.2;
  white-space: nowrap;
}

.app-layout__content {
  min-width: 0;
}

.app-layout__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 18px;
  height: auto;
  padding: 6px 8px 0 8px;
}

.app-layout__header-main {
  min-width: 0;
}

.app-layout__header-title {
  margin: 0;
  font-size: 29px;
  line-height: 1.2;
  font-weight: 700;
}

.app-layout__header-actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 10px;
  flex-shrink: 0;
}

.app-layout__user {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 172px;
  padding: 8px 10px;
  border: 1px solid var(--hc-border);
  border-radius: 14px;
  background: var(--hc-surface);
}

.app-layout__user-avatar {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 12px;
  background: var(--hc-primary-soft);
  color: var(--hc-primary);
  font-size: 15px;
  font-weight: 700;
}

.app-layout__user-info {
  display: flex;
  align-items: baseline;
  gap: 8px;
  min-width: 0;
  white-space: nowrap;
}

.app-layout__user-info strong {
  font-size: 15px;
  line-height: 1.2;
}

.app-layout__user-info span {
  color: var(--hc-text-soft);
  font-size: 13px;
  line-height: 1.2;
}

.app-layout__main {
  padding: 14px 8px 8px 8px;
}

@media (max-width: 1180px) {
  .app-layout {
    padding: 14px;
    gap: 14px;
  }

  .app-layout__header {
    flex-direction: column;
    align-items: stretch;
  }

  .app-layout__header-actions {
    justify-content: flex-start;
    flex-wrap: wrap;
  }
}

@media (max-width: 960px) {
  .app-layout {
    display: block;
  }

  .app-layout__aside {
    width: 100%;
    margin-bottom: 14px;
  }

  .app-layout__header-title {
    font-size: 26px;
  }

  .brand-block__title {
    font-size: 20px;
  }
}

@media (max-width: 720px) {
  .app-layout {
    padding: 10px;
  }

  .aside-shell {
    padding: 14px 10px;
  }

  .app-layout__header-actions {
    gap: 8px;
  }

  .app-layout__user {
    min-width: 0;
    flex: 1 1 auto;
  }

  .app-layout__header-actions :deep(.el-button) {
    flex: 0 0 auto;
  }

  .brand-block__top {
    flex-wrap: wrap;
  }

  .brand-block__badge {
    display: none;
  }

  .brand-block__title {
    font-size: 18px;
    margin-top: 12px;
  }
}
</style>
