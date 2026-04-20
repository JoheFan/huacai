<script setup lang="ts">
defineProps<{
  label: string
  value: string
  note: string
}>()
</script>

<template>
  <article class="stat-card card">
    <div class="card__section">
      <p class="stat-card__label">{{ label }}</p>
      <strong class="stat-card__value">{{ value }}</strong>
      <p class="stat-card__note">{{ note }}</p>
    </div>
  </article>
</template>

<style scoped>
.stat-card {
  min-height: 152px;
}

.stat-card__label {
  margin: 0;
  color: var(--hc-text-soft);
  font-size: 14px;
}

.stat-card__value {
  display: block;
  margin-top: 18px;
  font-size: 34px;
  line-height: 1;
  color: var(--hc-text);
}

.stat-card__note {
  margin: 18px 0 0;
  color: var(--hc-text-soft);
  line-height: 1.6;
}
</style>
