<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../../stores/auth'
import IconSymbol from '../../components/IconSymbol.vue'

const router = useRouter()
const authStore = useAuthStore()

const loading = ref(false)
const form = reactive({
  username: '',
  password: '',
})

const handleSubmit = async () => {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入账号和密码')
    return
  }

  loading.value = true

  try {
    await authStore.login(form)
    router.push('/dashboard')
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '登录失败，请稍后重试')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-root">
    <!-- 外层装饰背景 -->
    <div class="login-root__bg" aria-hidden="true"></div>

    <div class="login-page">
      <!-- 左侧品牌区 -->
      <section class="hero-panel">
        <!-- 屋脊高光 -->
        <div class="hero-panel__roof" aria-hidden="true"></div>

        <!-- 品牌标识 -->
        <div class="brand-chip">
          <span class="brand-icon">
            <IconSymbol name="logo" :size="18" />
          </span>
          <span>华彩系统</span>
        </div>

        <h1 class="hero-panel__title">华彩经营管理平台</h1>

        <div class="hero-panel__divider"></div>

        <p class="hero-panel__subtitle">客户、借贷、财务与系统管理统一入口</p>

        <!-- 装饰图形 -->
        <div class="hero-stage" aria-hidden="true">
          <div class="orbit"></div>
          <div class="platform">
            <div class="layer layer--l1"></div>
            <div class="layer layer--l2"></div>
            <div class="layer layer--l3"></div>

            <div class="glass-card glass-card--a">
              <div class="glass-avatar"></div>
              <div class="glass-line" style="width:90px;margin-top:20px"></div>
              <div class="glass-line" style="width:110px"></div>
              <div class="glass-line" style="width:72px"></div>
            </div>

            <div class="glass-card glass-card--b">
              <div class="glass-line" style="width:70px;margin-top:16px"></div>
              <div class="glass-line" style="width:110px"></div>
              <div class="glass-line" style="width:80px"></div>
              <div class="glass-chart">
                <div class="bars">
                  <span></span><span></span><span></span><span></span>
                </div>
                <div class="linechart">
                  <svg viewBox="0 0 200 80" fill="none">
                    <path d="M4 56 L42 36 L74 50 L108 28 L140 44 L196 14" stroke="#4A85FF" stroke-width="4" stroke-linecap="round" stroke-linejoin="round"/>
                    <circle cx="42" cy="36" r="4" fill="#4A85FF"/>
                    <circle cx="74" cy="50" r="4" fill="#4A85FF"/>
                    <circle cx="108" cy="28" r="4" fill="#4A85FF"/>
                    <circle cx="140" cy="44" r="4" fill="#4A85FF"/>
                    <circle cx="196" cy="14" r="4" fill="#4A85FF"/>
                  </svg>
                </div>
              </div>
            </div>

            <div class="glass-card glass-card--c">
              <div class="glass-line" style="width:92px;margin-top:16px"></div>
              <div class="glass-line" style="width:122px"></div>
              <div class="donut-chart"></div>
            </div>
          </div>
        </div>
      </section>

      <!-- 右侧登录面板 -->
      <section class="login-panel">
        <div class="login-card">
          <div class="login-icon">
            <IconSymbol name="lock" :size="24" />
          </div>
          <div class="login-label">登录系统</div>
          <h2>账号登录</h2>
          <p class="login-hint">请输入您的账号和密码登录系统。</p>

          <el-form class="login-form" label-position="top" @submit.prevent="handleSubmit">
            <el-form-item label="账号">
              <el-input
                v-model="form.username"
                placeholder="请输入账号"
                size="large"
              >
                <template #prefix>
                  <IconSymbol name="userCircle" :size="18" />
                </template>
              </el-input>
            </el-form-item>
            <el-form-item label="密码">
              <el-input
                v-model="form.password"
                type="password"
                show-password
                placeholder="请输入密码"
                size="large"
              >
                <template #prefix>
                  <IconSymbol name="lock" :size="18" />
                </template>
              </el-input>
            </el-form-item>
            <el-button
              type="primary"
              class="login-submit"
              size="large"
              :loading="loading"
              @click="handleSubmit"
            >
              登录
            </el-button>
          </el-form>
        </div>
      </section>
    </div>
  </div>
</template>

<style scoped>
.login-root {
  position: relative;
  min-height: 100vh;
  padding: 24px;
  box-sizing: border-box;
}

.login-root__bg {
  position: fixed;
  inset: 0;
  z-index: -1;
  background:
    radial-gradient(circle at 15% 15%, rgba(132, 176, 255, 0.14), transparent 22%),
    radial-gradient(circle at 85% 80%, rgba(90, 148, 255, 0.16), transparent 24%),
    linear-gradient(180deg, #EEF4FF 0%, #F6F9FF 100%);
  pointer-events: none;
}

.login-page {
  width: min(1680px, calc(100vw - 48px));
  margin: 0 auto;
  display: grid;
  grid-template-columns: 1.25fr 0.95fr;
  gap: 24px;
  min-height: calc(100vh - 48px);
}

/* ── 左侧品牌区 ── */
.hero-panel {
  position: relative;
  border-radius: 30px;
  padding: 54px 56px;
  background:
    linear-gradient(140deg, rgba(255, 255, 255, 0.88), rgba(231, 241, 255, 0.96)),
    linear-gradient(180deg, #EDF5FF 0%, #DFEEFF 100%);
  border: 1px solid #D8E6FF;
  box-shadow: 0 20px 60px rgba(58, 108, 214, 0.12);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.hero-panel__roof {
  position: absolute;
  inset: auto -8% -8% auto;
  width: 62%;
  height: 68%;
  background: radial-gradient(circle at 50% 50%, rgba(56, 109, 255, 0.18), rgba(56, 109, 255, 0) 60%);
  filter: blur(10px);
  pointer-events: none;
}

.brand-chip {
  position: relative;
  z-index: 2;
  display: inline-flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  border: 1px solid #C9DCFF;
  border-radius: 999px;
  color: #2F64F3;
  background: rgba(255, 255, 255, 0.7);
  font-weight: 700;
  font-size: 14px;
  letter-spacing: 0.5px;
  align-self: flex-start;
}

.brand-icon {
  width: 32px;
  height: 32px;
  border-radius: 10px;
  background: linear-gradient(180deg, #4F8DFF, #2F64F3);
  display: grid;
  place-items: center;
  color: #fff;
  font-size: 16px;
  box-shadow: 0 8px 16px rgba(47, 100, 243, 0.26);
}

.hero-panel__title {
  position: relative;
  z-index: 2;
  font-size: 62px;
  line-height: 1.1;
  letter-spacing: 1px;
  margin: 34px 0 18px;
  font-weight: 800;
  color: #102A5C;
}

.hero-panel__divider {
  width: 74px;
  height: 5px;
  border-radius: 999px;
  background: linear-gradient(90deg, #4A85FF, #7BA9FF);
  margin: 0 0 26px;
}

.hero-panel__subtitle {
  position: relative;
  z-index: 2;
  color: #6F84AB;
  font-size: 22px;
  margin: 0;
  letter-spacing: 0.5px;
}

/* ── 装饰图形 ── */
.hero-stage {
  position: absolute;
  left: 55px;
  right: 45px;
  bottom: 38px;
  height: 48%;
  display: flex;
  align-items: flex-end;
  justify-content: center;
}

.orbit {
  position: absolute;
  bottom: 14px;
  width: 82%;
  aspect-ratio: 16 / 7;
  border-radius: 50%;
  border: 1px solid rgba(122, 167, 255, 0.35);
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.55);
}

.orbit::before,
.orbit::after {
  content: '';
  position: absolute;
  inset: 12% 8%;
  border-radius: 50%;
  border: 1px solid rgba(255, 255, 255, 0.42);
}

.orbit::after {
  inset: 24% 18%;
}

.platform {
  position: relative;
  width: 450px;
  height: 220px;
}

.layer {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  width: 100%;
  border-radius: 34px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.95), rgba(208, 226, 255, 0.92));
  border: 1px solid rgba(216, 230, 255, 0.9);
  box-shadow: 0 10px 30px rgba(78, 120, 199, 0.1);
}

.layer--l1 { bottom: 0; height: 95px; width: 100%; }
.layer--l2 { bottom: 28px; height: 82px; width: 87%; }
.layer--l3 {
  bottom: 58px;
  height: 76px;
  width: 72%;
  background: linear-gradient(180deg, #79A8FF, #2F64F3);
  border-color: rgba(111, 160, 255, 0.8);
  box-shadow: 0 18px 34px rgba(47, 100, 243, 0.35);
}

.glass-card {
  position: absolute;
  border-radius: 26px;
  border: 1px solid rgba(255, 255, 255, 0.58);
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.4), rgba(205, 225, 255, 0.22));
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  box-shadow: 0 18px 40px rgba(91, 127, 198, 0.16);
  overflow: hidden;
}

.glass-card--a {
  width: 190px;
  height: 180px;
  left: 48px;
  bottom: 112px;
  transform: rotate(-12deg);
}

.glass-card--b {
  width: 220px;
  height: 210px;
  left: 165px;
  bottom: 136px;
  transform: rotate(-3deg);
}

.glass-card--c {
  width: 220px;
  height: 220px;
  left: 292px;
  bottom: 130px;
  transform: rotate(7deg);
}

.glass-avatar {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  background: linear-gradient(180deg, #8BB3FF, #4A7DFF);
  margin: 24px 0 0 18px;
}

.glass-line {
  height: 10px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.55);
  margin-left: 18px;
  display: block;
}

.glass-chart {
  position: absolute;
  left: 18px;
  right: 18px;
  bottom: 24px;
  height: 68px;
}

.bars {
  position: absolute;
  left: 0;
  bottom: 0;
  display: flex;
  gap: 10px;
  align-items: flex-end;
}

.bars span {
  width: 16px;
  border-radius: 8px 8px 4px 4px;
  background: linear-gradient(180deg, #86B1FF, #4A85FF);
  display: block;
}

.bars span:nth-child(1) { height: 28px; }
.bars span:nth-child(2) { height: 44px; }
.bars span:nth-child(3) { height: 58px; }
.bars span:nth-child(4) { height: 36px; }

.linechart {
  position: absolute;
  inset: 0 0 0 60px;
}

.linechart svg {
  width: 100%;
  height: 100%;
}

.donut-chart {
  position: absolute;
  width: 70px;
  height: 70px;
  right: 20px;
  bottom: 20px;
  border-radius: 50%;
  background: conic-gradient(#4A85FF 0 35%, #8EC8FF 35% 72%, rgba(255, 255, 255, 0.85) 72% 100%);
}

.donut-chart::after {
  content: '';
  position: absolute;
  inset: 18px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.9);
}

/* ── 右侧登录面板 ── */
.login-panel {
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 30px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.92), rgba(247, 250, 255, 0.96));
  border: 1px solid #D8E6FF;
  box-shadow: 0 20px 60px rgba(58, 108, 214, 0.12);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.login-card {
  width: min(480px, 100%);
  padding: 44px 40px 40px;
}

.login-icon {
  width: 52px;
  height: 52px;
  border-radius: 16px;
  display: grid;
  place-items: center;
  color: #2F64F3;
  background: linear-gradient(180deg, #EDF4FF, #DCE9FF);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.8);
  margin-bottom: 18px;
}

.login-label {
  color: #2F64F3;
  font-weight: 700;
  font-size: 16px;
  margin-bottom: 20px;
}

.login-card h2 {
  margin: 0;
  font-size: 54px;
  line-height: 1.08;
  font-weight: 800;
  color: #102A5C;
}

.login-hint {
  color: #6F84AB;
  font-size: 16px;
  margin: 18px 0 32px;
  line-height: 1.6;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 20px;
}

.login-form :deep(.el-form-item__label) {
  font-size: 15px;
  font-weight: 600;
  color: #102A5C;
  margin-bottom: 10px;
}

.login-submit {
  width: 100%;
  height: 52px;
  font-size: 17px;
  font-weight: 700;
  letter-spacing: 1px;
  margin-top: 8px;
}

/* ── 响应式 ── */
@media (max-width: 1200px) {
  .login-page {
    grid-template-columns: 1fr;
    min-height: auto;
  }

  .hero-panel {
    min-height: 600px;
  }

  .login-card {
    max-width: 600px;
  }
}

@media (max-width: 760px) {
  .login-root {
    padding: 12px;
  }

  .login-page {
    width: min(100vw - 24px, 100%);
    margin: 12px auto;
    gap: 12px;
  }

  .hero-panel,
  .login-panel {
    padding: 28px 24px;
    border-radius: 24px;
  }

  .hero-panel {
    min-height: 500px;
  }

  .hero-panel__title {
    font-size: 38px;
  }

  .hero-panel__subtitle {
    font-size: 16px;
  }

  .hero-stage {
    left: 18px;
    right: 18px;
  }

  .platform {
    transform: scale(0.75);
    transform-origin: bottom center;
  }

  .login-card {
    padding: 28px 20px;
  }

  .login-card h2 {
    font-size: 36px;
  }
}
</style>