<template>
  <div class="user-layout">
    <header class="mobile-header">
      <div class="brand-mark">校</div>
      <div class="brand-copy">
        <strong>校内兼职</strong>
        <span>代送需求 · 兼职申请 · 结果回看</span>
      </div>
      <button class="profile-button" type="button" @click="goToUser">我的</button>
    </header>

    <main class="main-content">
      <slot></slot>
    </main>

    <footer v-if="showCustomerNav" class="mobile-footer">
      <nav class="footer-nav">
        <router-link to="/user" class="nav-item" :class="{ active: route.path === '/user' }">
          <span class="nav-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>
          </span>
          <span>首页</span>
        </router-link>
        <router-link to="/user/campus/orders" class="nav-item" :class="{ active: route.path.startsWith('/user/campus/orders') }">
          <span class="nav-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 5v14M5 12h14"/></svg>
          </span>
          <span>发布</span>
        </router-link>
        <router-link to="/user/campus/order-result" class="nav-item" :class="{ active: route.path.startsWith('/user/campus/order-result') }">
          <span class="nav-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 11l3 3L22 4"/><path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11"/></svg>
          </span>
          <span>结果</span>
        </router-link>
        <router-link to="/user/campus/courier-onboarding" class="nav-item" :class="{ active: route.path.startsWith('/user/campus/courier-onboarding') }">
          <span class="nav-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M15 3h6v6"/><path d="M10 14L21 3"/><path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6"/></svg>
          </span>
          <span>入驻</span>
        </router-link>
        <router-link to="/user/profile" class="nav-item" :class="{ active: route.path.startsWith('/user/profile') }">
          <span class="nav-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
          </span>
          <span>我的</span>
        </router-link>
      </nav>
    </footer>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import '../styles/mobile-theme.css'

const router = useRouter()
const route = useRoute()

const hasCustomerToken = computed(() => Boolean(localStorage.getItem('customer_token')))
const showCustomerNav = computed(() => hasCustomerToken.value && !route.path.startsWith('/parttime/') && !route.path.startsWith('/courier/'))

const goToUser = () => {
  router.push('/user/profile')
}
</script>

<style scoped lang="scss">
.user-layout {
  min-height: 100vh;
  min-height: 100dvh;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
  color: #18181b;
  overflow-x: hidden;
}

.mobile-header {
  position: sticky;
  top: 0;
  z-index: 100;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: env(safe-area-inset-top, 0px) 16px 0;
  min-height: calc(56px + env(safe-area-inset-top, 0px));
  background: #ffffff;
  border-bottom: 1px solid #e4e4e7;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}

.brand-mark {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: grid;
  place-items: center;
  background: linear-gradient(135deg, #0f9f8f, #14b8a6);
  color: #fff;
  font-weight: 800;
  font-size: 16px;
  flex-shrink: 0;
}

.brand-copy {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;

  strong {
    font-size: 16px;
    line-height: 1.2;
    color: #18181b;
  }

  span {
    margin-top: 2px;
    font-size: 12px;
    color: #71717a;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
}

.profile-button {
  border: 1px solid #e4e4e7;
  border-radius: 8px;
  background: #ffffff;
  color: #0f9f8f;
  font-size: 13px;
  font-weight: 700;
  padding: 6px 12px;
  cursor: pointer;
  flex-shrink: 0;
  min-height: 36px;
  transition: background 0.15s ease;

  &:active {
    background: #f5f5f5;
  }
}

.main-content {
  flex: 1;
  padding-bottom: 72px;
  overflow-x: hidden;
}

.mobile-footer {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 100;
  border-top: 1px solid #e4e4e7;
  background: #ffffff;
  box-shadow: 0 -1px 3px rgba(0, 0, 0, 0.04);
  padding-bottom: env(safe-area-inset-bottom, 0px);
}

.footer-nav {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  height: 56px;
  align-items: center;
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  color: #71717a;
  text-decoration: none;
  font-size: 11px;
  font-weight: 600;
  padding: 4px 0;
  transition: color 0.15s ease;

  &.active {
    color: #0f9f8f;

    .nav-icon {
      color: #0f9f8f;
    }
  }
}

.nav-icon {
  width: 24px;
  height: 24px;
  display: grid;
  place-items: center;
  color: #71717a;
  transition: color 0.15s ease;

  svg {
    width: 22px;
    height: 22px;
  }
}

@media (min-width: 768px) {
  .mobile-header {
    max-width: 520px;
    width: 100%;
    margin: 0 auto;
  }

  .main-content {
    max-width: 520px;
    width: 100%;
    margin: 0 auto;
  }

  .mobile-footer {
    left: 50%;
    right: auto;
    width: min(520px, 100%);
    transform: translateX(-50%);
  }
}
</style>
