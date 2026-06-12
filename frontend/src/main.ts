import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import 'element-plus/dist/index.css'
import './style.css'
import App from './App.vue'
import router from './router'
import { useAuthStore } from './stores/auth'
import { restoreAuthSession } from './bootstrap/restoreAuthSession'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)
app.use(ElementPlus, { locale: zhCn })

const bootstrap = async () => {
  await router.isReady()
  await restoreAuthSession(useAuthStore(pinia), router)
  app.mount('#app')
}

void bootstrap()
