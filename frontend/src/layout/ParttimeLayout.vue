<template>
  <div class="parttime-layout">
    <header class="parttime-header">
      <div class="brand">
        <span class="brand-mark">兼</span>
        <div>
          <h1>兼职工作台</h1>
          <p>接单、取件、送达与异常上报</p>
        </div>
      </div>
      <button class="logout-btn" type="button" @click="handleLogout">退出</button>
    </header>

    <main class="parttime-main">
      <slot></slot>
    </main>

    <nav class="parttime-nav">
      <router-link to="/parttime/workbench" class="nav-item" active-class="active">
        <span class="nav-icon">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="3" width="20" height="14" rx="2" ry="2"/><line x1="8" y1="21" x2="16" y2="21"/><line x1="12" y1="17" x2="12" y2="21"/></svg>
        </span>
        <span>工作台</span>
      </router-link>
      <router-link to="/parttime/profile" class="nav-item" active-class="active">
        <span class="nav-icon">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
        </span>
        <span>我的</span>
      </router-link>
      <router-link to="/user/campus/courier-onboarding" class="nav-item" active-class="active">
        <span class="nav-icon">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M15 3h6v6"/><path d="M10 14L21 3"/><path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6"/></svg>
        </span>
        <span>入驻</span>
      </router-link>
    </nav>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useCourierStore } from '../stores/courier'
import '../styles/mobile-theme.css'

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
  min-height: 100dvh;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
  color: #18181b;
  overflow-x: hidden;
}

.parttime-header {
  position: sticky;
  top: 0;
  z-index: 100;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: env(safe-area-inset-top, 0px) 16px 0;
  min-height: calc(56px + env(safe-area-inset-top, 0px));
  background: #ffffff;
  border-bottom: 1px solid #e4e4e7;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
  flex: 1;
}

.brand-mark {
  width: 36px;
  height: 36px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  color: #fff;
  font-weight: 800;
  font-size: 16px;
  background: linear-gradient(135deg, #0f9f8f, #14b8a6);
  flex-shrink: 0;
}

.brand h1 {
  margin: 0;
  font-size: 16px;
  line-height: 1.2;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.brand p {
  margin: 2px 0 0;
  color: #71717a;
  font-size: 12px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.logout-btn {
  border: 1px solid #e4e4e7;
  border-radius: 8px;
  padding: 6px 14px;
  background: #ffffff;
  color: #0f9f8f;
  cursor: pointer;
  font-size: 13px;
  font-weight: 700;
  flex-shrink: 0;
  min-height: 36px;
  transition: background 0.15s ease;

  &:active {
    background: #f5f5f5;
  }
}

.parttime-main {
  flex: 1;
  padding: 14px 14px 80px;
  overflow-x: hidden;
}

.parttime-nav {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 100;
  display: flex;
  justify-content: space-around;
  height: 56px;
  border-top: 1px solid #e4e4e7;
  background: #ffffff;
  box-shadow: 0 -1px 3px rgba(0, 0, 0, 0.04);
  padding-bottom: env(safe-area-inset-bottom, 0px);
}

.nav-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 2px;
  text-decoration: none;
  color: #71717a;
  font-weight: 600;
  font-size: 11px;
  padding: 4px 0;
  transition: color 0.15s ease;
}

.nav-item.active {
  color: #0f9f8f;
}

.nav-icon {
  width: 24px;
  height: 24px;
  display: grid;
  place-items: center;
  transition: color 0.15s ease;

  svg {
    width: 22px;
    height: 22px;
  }
}

.nav-item.active .nav-icon {
  color: #0f9f8f;
}

@media (max-width: 640px) {
  .brand p {
    display: none;
  }
}

@media (min-width: 768px) {
  .parttime-header {
    max-width: 520px;
    width: 100%;
    margin: 0 auto;
  }

  .parttime-main {
    max-width: 520px;
    width: 100%;
    margin: 0 auto;
    padding: 14px 14px 80px;
  }

  .parttime-nav {
    left: 50%;
    right: auto;
    width: min(520px, 100%);
    transform: translateX(-50%);
  }
}
</style>
