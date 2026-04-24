<template>
  <div class="parttime-layout">
    <header class="parttime-header">
      <div class="brand">
        <span class="brand-mark">兼</span>
        <div>
          <h1>校园兼职端</h1>
          <p>接单、配送与异常处理工作台</p>
        </div>
      </div>
      <button class="logout-btn" @click="handleLogout">退出</button>
    </header>

    <main class="parttime-main">
      <slot></slot>
    </main>

    <nav class="parttime-nav">
      <router-link to="/parttime/workbench" class="nav-item" active-class="active">
        <span>任务</span>
      </router-link>
      <router-link to="/parttime/profile" class="nav-item" active-class="active">
        <span>我的</span>
      </router-link>
      <router-link to="/user/campus/courier-onboarding" class="nav-item" active-class="active">
        <span>入驻</span>
      </router-link>
    </nav>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useCourierStore } from '../stores/courier'

const router = useRouter()
const courierStore = useCourierStore()

const handleLogout = () => {
  courierStore.logout()
  ElMessage.success('已退出兼职端')
  router.push('/parttime/login')
}
</script>

<style scoped lang="scss">
.parttime-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background:
    radial-gradient(circle at top left, rgba(28, 178, 184, 0.12), transparent 34%),
    linear-gradient(180deg, #f4fbfb 0%, #edf6f7 100%);
  color: #102a43;
}

.parttime-header {
  position: sticky;
  top: 0;
  z-index: 20;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 14px 18px;
  background: rgba(255, 255, 255, 0.84);
  border-bottom: 1px solid rgba(190, 221, 225, 0.75);
  backdrop-filter: blur(18px);
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
}

.brand-mark {
  width: 42px;
  height: 42px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 16px;
  color: #047481;
  font-weight: 800;
  background: linear-gradient(135deg, #dbfbf9 0%, #b8f5ee 100%);
  box-shadow: 0 10px 22px rgba(15, 127, 142, 0.12);
}

.brand h1 {
  margin: 0;
  font-size: 18px;
  line-height: 1.2;
}

.brand p {
  margin: 3px 0 0;
  color: #627d98;
  font-size: 12px;
}

.logout-btn {
  border: 1px solid rgba(15, 127, 142, 0.18);
  border-radius: 999px;
  padding: 8px 14px;
  background: rgba(255, 255, 255, 0.72);
  color: #0f7f8e;
  cursor: pointer;
}

.parttime-main {
  flex: 1;
  width: min(1120px, 100%);
  margin: 0 auto;
  padding: 16px 16px 82px;
}

.parttime-nav {
  position: fixed;
  left: 50%;
  bottom: 16px;
  transform: translateX(-50%);
  z-index: 30;
  display: flex;
  gap: 8px;
  padding: 8px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(190, 221, 225, 0.78);
  box-shadow: 0 18px 44px rgba(26, 87, 100, 0.12);
  backdrop-filter: blur(18px);
}

.nav-item {
  min-width: 74px;
  text-align: center;
  text-decoration: none;
  color: #627d98;
  padding: 10px 14px;
  border-radius: 999px;
  font-weight: 700;
  font-size: 13px;
}

.nav-item.active {
  color: #ffffff;
  background: linear-gradient(135deg, #0f9f92 0%, #0b86b5 100%);
  box-shadow: 0 10px 24px rgba(15, 127, 142, 0.18);
}

@media (max-width: 640px) {
  .parttime-header {
    padding: 12px 14px;
  }

  .parttime-main {
    padding: 12px 12px 82px;
  }

  .brand p {
    display: none;
  }

  .nav-item {
    min-width: 68px;
    padding: 9px 12px;
  }
}
</style>
