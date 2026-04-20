<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../../stores/auth'

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
  <section class="login-screen">
    <div class="login-screen__panel login-screen__panel--intro">
      <span class="login-screen__eyebrow">华彩系统</span>
      <h1>华彩经营管理平台</h1>
      <p class="login-screen__intro">客户、借贷、财务与系统管理统一入口</p>
    </div>

    <div class="login-screen__panel card">
      <div class="card__section">
        <p class="login-screen__form-label">登录系统</p>
        <h2>账号登录</h2>
        <p class="login-screen__tip">请输入您的账号和密码登录系统。</p>

        <el-form class="login-screen__form" label-position="top">
          <el-form-item label="账号">
            <el-input v-model="form.username" placeholder="请输入账号" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
          </el-form-item>
          <el-button type="primary" class="login-screen__submit" :loading="loading" @click="handleSubmit">登录</el-button>
        </el-form>
      </div>
    </div>
  </section>
</template>

<style scoped>
.login-screen {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 1.1fr 0.9fr;
  gap: 24px;
  padding: 24px;
}

.login-screen__panel {
  border-radius: 28px;
  min-height: calc(100vh - 48px);
}

.login-screen__panel--intro {
  padding: 48px;
  background:
    linear-gradient(135deg, rgba(20, 113, 93, 0.95), rgba(178, 91, 47, 0.92)),
    linear-gradient(180deg, #173f3a, #b25b2f);
  color: #fff;
  box-shadow: var(--hc-shadow);
}

.login-screen__eyebrow {
  display: inline-block;
  padding: 8px 14px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.14);
  font-size: 13px;
}

.login-screen__panel--intro h1 {
  margin: 24px 0 0;
  font-size: 50px;
  line-height: 1.1;
}

.login-screen__intro {
  margin: 18px 0 0;
  max-width: 460px;
  color: rgba(255, 255, 255, 0.9);
  font-size: 16px;
  line-height: 1.8;
}

.login-screen__panel.card {
  display: flex;
  align-items: center;
}

.login-screen__panel.card .card__section {
  width: min(440px, 100%);
  margin: 0 auto;
}

.login-screen__form-label {
  color: var(--hc-text-soft);
}

.login-screen__panel.card h2 {
  margin: 12px 0;
  font-size: 34px;
}

.login-screen__tip {
  margin: 0;
  color: var(--hc-text-soft);
  font-size: 13px;
  line-height: 1.7;
}

.login-screen__form {
  margin-top: 28px;
}

.login-screen__submit {
  width: 100%;
  height: 48px;
  margin-top: 8px;
}

@media (max-width: 960px) {
  .login-screen {
    grid-template-columns: 1fr;
    padding: 20px;
  }

  .login-screen__panel {
    min-height: auto;
  }

  .login-screen__panel--intro {
    padding: 32px 24px;
  }

  .login-screen__panel--intro h1 {
    font-size: 36px;
  }
}

@media (max-width: 640px) {
  .login-screen {
    gap: 16px;
    padding: 14px;
  }

  .login-screen__panel--intro,
  .login-screen__panel.card .card__section {
    padding-left: 20px;
    padding-right: 20px;
  }

  .login-screen__panel.card h2 {
    font-size: 28px;
  }
}
</style>
