<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import type { ModuleRouteMeta } from '../../types/navigation'

const route = useRoute()
const meta = computed(
  () => (route.meta as unknown as ModuleRouteMeta) ?? { title: '', description: '', highlights: [] },
)
</script>

<template>
  <section class="page-shell">
    <header class="page-heading">
      <div>
        <h2 class="page-heading__title">{{ meta.title }}</h2>
      </div>
    </header>

    <section class="page-grid">
      <article class="card module-card module-card--main">
        <div class="card__section">
          <h3 class="section-title">模块能力</h3>
          <ul class="module-list">
            <li v-for="item in meta.highlights" :key="item">{{ item }}</li>
          </ul>
        </div>
      </article>

      <article class="card module-card">
        <div class="card__section">
          <el-empty description="功能建设中" />
        </div>
      </article>
    </section>
  </section>
</template>

<style scoped>
.module-card--main {
  grid-column: span 7;
}

.module-card {
  grid-column: span 5;
}

.module-list {
  margin: 12px 0 0;
  padding-left: 18px;
  color: var(--hc-text-soft);
  line-height: 1.9;
}

@media (max-width: 960px) {
  .module-card,
  .module-card--main {
    grid-column: span 1;
  }
}
</style>
