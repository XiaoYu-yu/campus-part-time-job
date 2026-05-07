import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import pinia from './stores'
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import 'element-plus/dist/index.css'
import './styles/variables.scss'
import './styles/element-plus.scss'
import './styles/global.scss'
import './styles/mobile-app.scss'
import './style.css'

const app = createApp(App)

app.use(router)
app.use(pinia)
app.use(ElementPlus, { locale: zhCn })

app.mount('#app')
