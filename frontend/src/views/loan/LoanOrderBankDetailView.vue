<script setup lang="ts">
import { useRouter } from 'vue-router'
const router = useRouter()
router.replace('/loan-orders-bank')
</script>
<template>
  <section class="page-shell">
    <section class="card">
      <div class="card__section detail-header">
        <div class="detail-header__copy">
          <span class="detail-header__eyebrow">借贷管理 / 银行资金</span>
          <h2 class="detail-header__title">正在返回银行借贷列表</h2>
          <p class="detail-header__desc">当前详情入口已收口到银行资金列表的明细抽屉，系统会自动跳转。</p>
        </div>
      </div>
    </section>
  </section>
</template>
