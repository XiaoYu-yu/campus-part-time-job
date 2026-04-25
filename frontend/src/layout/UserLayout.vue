<template>
  <div class="user-layout">
    <header class="mobile-header">
      <div class="brand-mark">校</div>
      <div class="brand-copy">
        <strong>校内兼职</strong>
        <span>校园代送 · 兼职报名 · 结果回看</span>
      </div>
      <button class="profile-button" type="button" @click="goToUser">我的</button>
    </header>

    <main class="main-content">
      <slot></slot>
    </main>

    <footer v-if="showCustomerNav" class="mobile-footer">
      <nav class="footer-nav">
        <router-link to="/user" class="nav-item" :class="{ active: route.path === '/user' }">
          <span class="nav-icon">⌂</span>
          <span>首页</span>
        </router-link>
        <router-link to="/user/campus/order-result" class="nav-item" :class="{ active: route.path.startsWith('/user/campus/order-result') }">
          <span class="nav-icon">✓</span>
          <span>结果</span>
        </router-link>
        <router-link to="/user/campus/courier-onboarding" class="nav-item" :class="{ active: route.path.startsWith('/user/campus/courier-onboarding') }">
          <span class="nav-icon">＋</span>
          <span>入驻</span>
        </router-link>
        <router-link to="/user/profile" class="nav-item" :class="{ active: route.path.startsWith('/user/profile') }">
          <span class="nav-icon">◎</span>
          <span>我的</span>
        </router-link>
      </nav>
    </footer>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'

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
  display: flex;
  flex-direction: column;
  background:
    radial-gradient(circle at 12% 0%, rgba(45, 212, 191, 0.2), transparent 28%),
    linear-gradient(180deg, #effdfa 0%, #f7fbff 42%, #f8fafc 100%);
  color: #102a43;
}

.mobile-header {
  position: sticky;
  top: 0;
  z-index: 100;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  background: rgba(255, 255, 255, 0.86);
  border-bottom: 1px solid rgba(15, 118, 110, 0.1);
  box-shadow: 0 12px 28px rgba(15, 118, 110, 0.08);
  backdrop-filter: blur(16px);
}

.brand-mark {
  width: 42px;
  height: 42px;
  border-radius: 16px;
  display: grid;
  place-items: center;
  background: linear-gradient(135deg, #14b8a6 0%, #38bdf8 100%);
  color: #fff;
  font-weight: 800;
  box-shadow: 0 12px 24px rgba(20, 184, 166, 0.22);
}

.brand-copy {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;

  strong {
    font-size: 17px;
    line-height: 1.2;
    color: #0f172a;
  }

  span {
    margin-top: 3px;
    font-size: 12px;
    color: #64748b;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
}

.profile-button {
  border: 1px solid rgba(15, 118, 110, 0.16);
  border-radius: 999px;
  background: rgba(240, 253, 250, 0.86);
  color: #0f766e;
  font-size: 13px;
  font-weight: 700;
  padding: 8px 12px;
  cursor: pointer;
}

.main-content {
  flex: 1;
  padding-bottom: 78px;
}

.mobile-footer {
  position: fixed;
  left: 12px;
  right: 12px;
  bottom: 12px;
  z-index: 100;
  border: 1px solid rgba(15, 118, 110, 0.1);
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.92);
  box-shadow: 0 20px 40px rgba(15, 23, 42, 0.12);
  backdrop-filter: blur(18px);
}

.footer-nav {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  height: 64px;
  align-items: center;
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  color: #64748b;
  text-decoration: none;
  font-size: 12px;
  font-weight: 700;

  &.active {
    color: #0f766e;

    .nav-icon {
      background: linear-gradient(135deg, #14b8a6 0%, #38bdf8 100%);
      color: #fff;
      box-shadow: 0 10px 20px rgba(20, 184, 166, 0.2);
    }
  }
}

.nav-icon {
  width: 28px;
  height: 28px;
  border-radius: 12px;
  display: grid;
  place-items: center;
  background: rgba(15, 118, 110, 0.08);
  color: #0f766e;
  font-size: 15px;
  transition: all 0.2s ease;
}

@media (min-width: 768px) {
  .mobile-header,
  .main-content {
    max-width: 520px;
    width: 100%;
    margin: 0 auto;
  }

  .mobile-footer {
    left: 50%;
    right: auto;
    width: min(496px, calc(100% - 24px));
    transform: translateX(-50%);
  }
}
</style>
