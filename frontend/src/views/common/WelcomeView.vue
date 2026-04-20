<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { getAccessibleBusinessItems } from '../../access/moduleAccess'
import { useAuthStore } from '../../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const currentUserName = computed(() => authStore.user?.realName ?? authStore.user?.username ?? '同事')
const accessibleModules = computed(() => getAccessibleBusinessItems(authStore.user))

const openModule = async (path: string) => {
  await router.push(path)
}
</script>

<template>
  <section class="page-shell">
    <header class="page-heading">
      <div>
        <h2 class="page-heading__title">欢迎使用</h2>
        <p class="page-heading__desc">
          {{ currentUserName }}，当前账号已按业务模块授权。这里先展示你现在可以直接进入的页面，后续如需补充首页指标或提醒区，再按实际业务补。
        </p>
      </div>
    </header>

    <section class="page-grid">
      <article class="card welcome-card welcome-card--hero">
        <div class="card__section">
          <p class="welcome-card__eyebrow">当前可见模块</p>
          <h3 class="welcome-card__title">从已授权业务页开始工作</h3>
          <p class="section-desc">
            未授权模块不会出现在左侧菜单，对应接口也已经在后端拦截。
          </p>

          <div class="welcome-module-list">
            <button
              v-for="item in accessibleModules"
              :key="item.path"
              type="button"
              class="welcome-module-list__item"
              @click="openModule(item.path)"
            >
              <strong>{{ item.title }}</strong>
              <span>{{ item.description }}</span>
            </button>
          </div>
        </div>
      </article>

      <article class="card welcome-card">
        <div class="card__section">
          <h3 class="section-title">使用说明</h3>
          <ul class="welcome-notes">
            <li>如需新增可见模块，请联系管理员在系统用户里调整。</li>
            <li>刷新页面后会按 token 自动恢复当前账号信息和菜单状态。</li>
            <li>如果你需要工作台指标或更多首页内容，后续可以按具体场景补充。</li>
          </ul>
        </div>
      </article>
    </section>
  </section>
</template>

<style scoped>
.welcome-card--hero {
  grid-column: span 8;
}

.welcome-card {
  grid-column: span 4;
}

.welcome-card__eyebrow {
  margin: 0 0 8px;
  color: var(--hc-primary);
  font-size: 13px;
  font-weight: 600;
}

.welcome-card__title {
  margin: 0 0 10px;
  font-size: 24px;
  line-height: 1.3;
}

.welcome-module-list {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin-top: 20px;
}

.welcome-module-list__item {
  display: flex;
  min-height: 120px;
  flex-direction: column;
  gap: 8px;
  padding: 16px;
  border: 1px solid var(--hc-border);
  border-radius: 16px;
  background: linear-gradient(180deg, rgba(239, 246, 255, 0.8), rgba(255, 255, 255, 1));
  color: var(--hc-text);
  text-align: left;
  cursor: pointer;
  transition: transform 0.18s ease, box-shadow 0.18s ease, border-color 0.18s ease;
}

.welcome-module-list__item:hover {
  transform: translateY(-1px);
  border-color: rgba(59, 130, 246, 0.32);
  box-shadow: 0 12px 24px rgba(30, 64, 175, 0.08);
}

.welcome-module-list__item strong {
  font-size: 16px;
  font-weight: 600;
}

.welcome-module-list__item span {
  color: var(--hc-text-soft);
  font-size: 14px;
  line-height: 1.6;
}

.welcome-notes {
  margin: 10px 0 0;
  padding-left: 18px;
  color: var(--hc-text-soft);
  line-height: 1.9;
}

@media (max-width: 960px) {
  .welcome-card,
  .welcome-card--hero {
    grid-column: span 1;
  }

  .welcome-module-list {
    grid-template-columns: 1fr;
  }
}
</style>
