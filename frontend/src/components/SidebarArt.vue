<script setup lang="ts">
// SidebarArt - 左侧导航底部装饰艺术组件
// 风格：统一线性 SVG，与 IconSymbol 一致的视觉语言
</script>

<template>
  <div class="sidebar-art" aria-hidden="true">
    <div class="sidebar-art__stage">
      <!-- 底层椭圆环 -->
      <div class="sidebar-art__ring"></div>

      <!-- 三层玻璃卡片底座 -->
      <div class="sidebar-art__base sidebar-art__base--b1"></div>
      <div class="sidebar-art__base sidebar-art__base--b2"></div>
      <div class="sidebar-art__base sidebar-art__base--b3"></div>

      <!-- SVG 装饰艺术层：线性风格卡片/折线图形 -->
      <svg class="sidebar-art__svg" viewBox="0 0 214 160" fill="none" xmlns="http://www.w3.org/2000/svg">
        <!-- 左侧玻璃卡片 -->
        <g opacity="0.6">
          <rect x="12" y="50" width="62" height="70" rx="12" stroke="currentColor" stroke-width="1.5" fill="rgba(255,255,255,0.5)"/>
          <path d="M24 68h24M24 76h16M24 84h20" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
          <circle cx="52" cy="94" r="8" stroke="currentColor" stroke-width="1.5"/>
          <path d="M48 94h8M52 90v8" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
        </g>

        <!-- 中间大玻璃卡片 -->
        <g opacity="0.5">
          <rect x="56" y="38" width="100" height="88" rx="14" stroke="currentColor" stroke-width="1.5" fill="rgba(255,255,255,0.35)"/>
          <!-- 折线图 -->
          <path d="M72 108L88 86l12 12 16-20 20 12" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/>
          <circle cx="72" cy="108" r="3" fill="currentColor"/>
          <circle cx="100" cy="98" r="3" fill="currentColor"/>
          <circle cx="116" cy="110" r="3" fill="currentColor"/>
          <circle cx="132" cy="90" r="3" fill="currentColor"/>
          <circle cx="152" cy="102" r="3" fill="currentColor"/>
          <!-- 顶部装饰线 -->
          <path d="M72 72h64" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
          <path d="M72 80h40" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" opacity="0.6"/>
        </g>

        <!-- 右侧玻璃卡片 -->
        <g opacity="0.55">
          <rect x="140" y="48" width="66" height="72" rx="12" stroke="currentColor" stroke-width="1.5" fill="rgba(255,255,255,0.45)" transform="rotate(6 173 84)"/>
          <!-- 圆环进度 -->
          <circle cx="173" cy="82" r="16" stroke="currentColor" stroke-width="1.5" opacity="0.5"/>
          <path d="M173 70a12 12 0 0 1 12 12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          <!-- 小节点 -->
          <circle cx="158" cy="102" r="2.5" fill="currentColor"/>
          <circle cx="188" cy="102" r="2.5" fill="currentColor"/>
          <path d="M160 104h28" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
        </g>
      </svg>
    </div>
  </div>
</template>

<style scoped>
.sidebar-art {
  position: relative;
  left: auto;
  right: auto;
  bottom: auto;
  height: 190px;
  z-index: 1;
  pointer-events: none;
  display: flex;
  align-items: flex-end;
  justify-content: center;
  margin-top: auto;
  padding-top: 20px;
  flex: 0 0 190px;
}

.sidebar-art__stage {
  position: relative;
  width: 214px;
  height: 160px;
}

@media (max-height: 860px) {
  .sidebar-art {
    height: 148px;
    flex-basis: 148px;
    padding-top: 12px;
  }

  .sidebar-art__stage {
    width: 176px;
    height: 132px;
    transform: scale(0.88);
    transform-origin: center bottom;
  }
}

.sidebar-art__ring {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  bottom: 4px;
  width: 210px;
  height: 68px;
  border-radius: 50%;
  border: 1px solid rgba(160, 191, 255, 0.45);
}

.sidebar-art__ring::before,
.sidebar-art__ring::after {
  content: '';
  position: absolute;
  inset: 10px 16px;
  border-radius: 50%;
  border: 1px solid rgba(205, 223, 255, 0.45);
}

.sidebar-art__ring::after {
  inset: 18px 34px;
}

.sidebar-art__base {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  border-radius: 28px;
  border: 1px solid rgba(215, 228, 255, 0.95);
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(218, 232, 255, 0.92));
  box-shadow: 0 10px 26px rgba(88, 123, 191, 0.08);
}

.sidebar-art__base--b1 {
  width: 196px;
  height: 60px;
  bottom: 0;
}

.sidebar-art__base--b2 {
  width: 164px;
  height: 50px;
  bottom: 18px;
}

.sidebar-art__base--b3 {
  width: 132px;
  height: 46px;
  bottom: 36px;
  background: linear-gradient(180deg, #7EAAFF, #3167F4);
  border-color: rgba(118, 164, 255, 0.9);
  box-shadow: 0 16px 34px rgba(49, 103, 244, 0.28);
}

.sidebar-art__svg {
  position: absolute;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  color: var(--hc-primary, #4a85ff);
  pointer-events: none;
}
</style>
