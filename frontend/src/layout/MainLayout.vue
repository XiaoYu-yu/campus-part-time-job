<template>
  <div class="main-layout">
    <aside class="sidebar" :class="{ 'is-collapsed': isCollapse }">
      <button class="logo-section" type="button" @click="goDashboard">
        <div class="logo-icon">
          <svg viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M20 3L5 12L20 21L35 12L20 3Z" fill="url(#logoGrad1)"/>
            <path d="M5 26L20 35L35 26" stroke="url(#logoGrad2)" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>
            <defs>
              <linearGradient id="logoGrad1" x1="5" y1="3" x2="35" y2="35" gradientUnits="userSpaceOnUse">
                <stop stop-color="#0b3b91"/>
                <stop offset="1" stop-color="#1d6cf2"/>
              </linearGradient>
              <linearGradient id="logoGrad2" x1="5" y1="26" x2="35" y2="26" gradientUnits="userSpaceOnUse">
                <stop stop-color="#0b3b91"/>
                <stop offset="1" stop-color="#1d6cf2"/>
              </linearGradient>
            </defs>
          </svg>
        </div>
        <div class="logo-text">
          <h1>校内兼职平台</h1>
          <p>Campus Admin</p>
        </div>
      </button>

      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        router
        :collapse="isCollapse"
      >
        <div class="menu-section-label">校园运营</div>
        <el-menu-item index="/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <template #title>运营总览</template>
        </el-menu-item>
        <el-menu-item index="/employee">
          <el-icon><User /></el-icon>
          <template #title>运营人员</template>
        </el-menu-item>
        <el-menu-item index="/statistics">
          <el-icon><DataAnalysis /></el-icon>
          <template #title>数据看板</template>
        </el-menu-item>
        <el-menu-item index="/campus/settlement-batches">
          <el-icon><Tickets /></el-icon>
          <template #title>校园结算批次</template>
        </el-menu-item>
        <el-menu-item index="/campus/after-sale-executions">
          <el-icon><Document /></el-icon>
          <template #title>校园售后执行</template>
        </el-menu-item>
        <el-menu-item index="/campus/courier-ops">
          <el-icon><LocationInformation /></el-icon>
          <template #title>校园配送运营</template>
        </el-menu-item>
        <el-menu-item index="/campus/settlements">
          <el-icon><Coin /></el-icon>
          <template #title>校园结算运营</template>
        </el-menu-item>
        <el-menu-item index="/campus/exceptions">
          <el-icon><Document /></el-icon>
          <template #title>校园异常处理</template>
        </el-menu-item>

        <div class="menu-section-label">旧模块兼容</div>
        <el-menu-item index="/category">
          <el-icon><Grid /></el-icon>
          <template #title>分类兼容</template>
        </el-menu-item>
        <el-menu-item index="/dish">
          <el-icon><Mug /></el-icon>
          <template #title>商品兼容</template>
        </el-menu-item>
        <el-menu-item index="/setmeal">
          <el-icon><Sugar /></el-icon>
          <template #title>套餐兼容</template>
        </el-menu-item>
        <el-menu-item index="/order">
          <el-icon><ShoppingCart /></el-icon>
          <template #title>订单兼容</template>
        </el-menu-item>
        <el-menu-item index="/shop-status">
          <el-icon><Position /></el-icon>
          <template #title>店铺状态兼容</template>
        </el-menu-item>
      </el-menu>

      <div class="sidebar-footer">
        <button class="collapse-btn" @click="toggleCollapse">
          <el-icon v-if="!isCollapse"><DArrowLeft /></el-icon>
          <el-icon v-else><DArrowRight /></el-icon>
        </button>
      </div>
    </aside>

    <div class="main-content">
      <header class="top-nav">
        <div class="nav-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">
              <el-icon><House /></el-icon>
              <span class="home-crumb">运营总览</span>
            </el-breadcrumb-item>
            <el-breadcrumb-item>{{ breadcrumb }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="nav-right">
          <div class="global-search">
            <el-icon><Search /></el-icon>
            <span>搜索订单、用户、配送员等</span>
          </div>

          <div class="nav-actions">
            <el-tooltip content="全屏" placement="bottom">
              <button class="icon-btn" @click="toggleFullscreen">
                <el-icon><FullScreen /></el-icon>
              </button>
            </el-tooltip>
            <el-tooltip content="通知" placement="bottom">
              <button class="icon-btn notification-btn">
                <el-icon><Bell /></el-icon>
                <span class="badge">3</span>
              </button>
            </el-tooltip>
          </div>

          <div class="user-section">
            <el-dropdown>
              <div class="user-info">
                <el-avatar :size="36" class="user-avatar">
                  {{ userInitial }}
                </el-avatar>
                <div class="user-details">
                  <span class="user-name">{{ currentUserName }}</span>
                  <span class="user-role">{{ currentUserRole }}</span>
                </div>
                <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item>
                    <el-icon><User /></el-icon>
                    个人中心
                  </el-dropdown-item>
                  <el-dropdown-item>
                    <el-icon><Setting /></el-icon>
                    系统设置
                  </el-dropdown-item>
                  <el-dropdown-item divided @click="logout">
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </header>

      <div class="content">
        <slot></slot>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { normalizeDisplayText } from '../utils/text'
import { 
  HomeFilled, User, Grid, Mug, Sugar, ShoppingCart, DataAnalysis, Position, Tickets, Document, LocationInformation, Coin,
  DArrowLeft, DArrowRight, House, FullScreen, Bell, ArrowDown, Setting, SwitchButton, Search
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const isCollapse = ref(false)

const activeMenu = computed(() => {
  const path = router.currentRoute.value.path
  if (path.startsWith('/campus/settlement-batches')) {
    return '/campus/settlement-batches'
  }
  if (path.startsWith('/campus/after-sale-executions')) {
    return '/campus/after-sale-executions'
  }
  if (path.startsWith('/campus/courier-ops')) {
    return '/campus/courier-ops'
  }
  if (path.startsWith('/campus/settlements')) {
    return '/campus/settlements'
  }
  if (path.startsWith('/campus/exceptions')) {
    return '/campus/exceptions'
  }
  return path
})

const breadcrumb = computed(() => {
  const path = router.currentRoute.value.path
  if (path.startsWith('/campus/settlement-batches/')) {
    return '校园结算批次详情'
  }
  const pathMap = {
    '/dashboard': '运营总览',
    '/employee': '运营人员',
    '/category': '分类兼容',
    '/dish': '商品兼容',
    '/setmeal': '套餐兼容',
    '/order': '订单兼容',
    '/statistics': '数据看板',
    '/campus/settlement-batches': '校园结算批次',
    '/campus/after-sale-executions': '校园售后执行',
    '/campus/courier-ops': '校园配送运营',
    '/campus/settlements': '校园结算运营',
    '/campus/exceptions': '校园异常处理',
    '/shop-status': '店铺状态兼容'
  }
  return pathMap[path] || ''
})

const currentUserName = computed(() => normalizeDisplayText(userStore.currentUserInfo?.name || '管理员'))
const currentUserRole = computed(() => normalizeDisplayText(userStore.currentUserInfo?.role || '校园运营'))

const userInitial = computed(() => {
  const name = currentUserName.value || '管理员'
  return name.charAt(0)
})

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

const goDashboard = () => {
  router.push('/dashboard')
}

const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
  } else {
    document.exitFullscreen()
  }
}

const logout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style lang="scss" scoped>
.main-layout {
  --admin-blue: #1d6cf2;
  --admin-blue-dark: #1357d8;
  --admin-blue-soft: #eaf2ff;
  --admin-text: #13264d;
  --admin-muted: #64748b;

  display: flex;
  height: 100vh;
  overflow: hidden;
  background:
    radial-gradient(circle at 90% 4%, rgba(29, 108, 242, 0.1), transparent 26%),
    linear-gradient(135deg, #f8fbff 0%, #f3f7ff 54%, #ffffff 100%);
  color: var(--admin-text);
}

.sidebar {
  position: relative;
  width: 272px;
  background: rgba(255, 255, 255, 0.96);
  border-right: 1px solid rgba(148, 163, 184, 0.18);
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease;
  box-shadow: 18px 0 42px rgba(30, 64, 175, 0.06);
  backdrop-filter: blur(18px);

  &.is-collapsed {
    width: 78px;

    .logo-text,
    .menu-section-label {
      display: none;
    }

    .logo-section {
      justify-content: center;
      padding: 24px 12px;
    }

    .sidebar-menu {
      padding: 14px 10px;
    }
  }
}

.logo-section {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 28px 18px 26px;
  border-bottom: 1px solid rgba(148, 163, 184, 0.16);
  width: 100%;
  text-align: left;
  cursor: pointer;
  background: transparent;

  &:hover {
    .logo-icon {
      transform: translateY(-1px) scale(1.02);
      box-shadow: 0 18px 38px rgba(29, 108, 242, 0.22);
    }
  }
}

.logo-icon {
  width: 50px;
  height: 50px;
  flex-shrink: 0;
  padding: 9px;
  border-radius: 18px;
  background: linear-gradient(135deg, #eff6ff 0%, #dbeafe 52%, #eef2ff 100%);
  box-shadow: 0 14px 34px rgba(29, 108, 242, 0.15);
  transition: all 0.2s ease;

  svg path:first-child {
    fill: var(--admin-blue);
  }
}

.logo-text {
  h1 {
    font-size: 18px;
    font-weight: 900;
    color: var(--admin-text);
    margin: 0;
    line-height: 1.2;
    letter-spacing: -0.02em;
  }

  p {
    font-size: 11px;
    color: var(--admin-blue);
    margin: 0;
    line-height: 1.2;
    text-transform: uppercase;
    letter-spacing: 0.14em;
  }
}

.sidebar-menu {
  flex: 1;
  border-right: none;
  background: transparent;
  padding: 18px 14px;
  overflow-y: auto;

  &::-webkit-scrollbar {
    width: 4px;
  }

  &::-webkit-scrollbar-thumb {
    background: rgba(29, 108, 242, 0.24);
    border-radius: 2px;
  }

  .menu-section-label {
    padding: 18px 12px 10px;
    color: #94a3b8;
    font-size: 11px;
    font-weight: 800;
    letter-spacing: 0.16em;
    text-transform: uppercase;
  }

  .el-menu-item {
    height: 46px;
    line-height: 46px;
    margin: 6px 0;
    border-radius: 12px;
    font-weight: 700;
    color: #475569;
    transition: all 0.2s ease;
    letter-spacing: 0.01em;

    .el-icon {
      font-size: 18px;
      color: #64748b;
    }

    &:hover {
      background-color: #f1f5ff;
      color: var(--admin-blue);

      .el-icon {
        color: var(--admin-blue);
      }
    }

    &.is-active {
      background: linear-gradient(135deg, #1d6cf2 0%, #0f5be8 100%);
      color: #ffffff;
      box-shadow: 0 16px 34px rgba(29, 108, 242, 0.24);

      &::before {
        display: none;
      }

      .el-icon {
        color: #ffffff;
      }
    }
  }
}

.sidebar-footer {
  padding: 14px;
  border-top: 1px solid rgba(148, 163, 184, 0.16);
}

.collapse-btn {
  width: 100%;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  color: var(--admin-blue);
  transition: all 0.2s ease;

  &:hover {
    background-color: rgba(29, 108, 242, 0.08);
    color: var(--admin-blue);
  }

  .el-icon {
    font-size: 18px;
  }
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  background:
    linear-gradient(rgba(29, 108, 242, 0.028) 1px, transparent 1px),
    linear-gradient(90deg, rgba(29, 108, 242, 0.028) 1px, transparent 1px);
  background-size: 52px 52px;
  min-width: 0;
}

.top-nav {
  height: 88px;
  margin: 0;
  background-color: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(148, 163, 184, 0.16);
  border-radius: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 34px;
  box-shadow: none;
}

.nav-left {
  .el-breadcrumb {
    font-size: 14px;

    .el-breadcrumb__item {
      .el-breadcrumb__inner {
        color: #71717a;
        font-weight: 500;
        display: flex;
        align-items: center;
        gap: 4px;

        &.is-link {
          &:hover {
            color: var(--admin-blue);
          }
        }

        .el-icon {
          font-size: 16px;
        }

        .home-crumb {
          margin-left: 4px;
        }
      }

      &:last-child {
        .el-breadcrumb__inner {
          color: #0f172a;
          font-weight: 600;
        }
      }
    }
  }
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.global-search {
  display: flex;
  align-items: center;
  gap: 10px;
  width: min(320px, 26vw);
  height: 42px;
  padding: 0 16px;
  border: 1px solid rgba(148, 163, 184, 0.26);
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.86);
  color: #94a3b8;
  font-size: 13px;
  box-shadow: 0 12px 28px rgba(30, 64, 175, 0.06);

  .el-icon {
    color: #64748b;
  }
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  padding-right: 16px;
  border-right: 1px solid rgba(148, 163, 184, 0.2);
}

.icon-btn {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #475569;
  transition: all 0.2s ease;
  position: relative;

  &:hover {
    background-color: #eef4ff;
    color: var(--admin-blue);
  }

  .el-icon {
    font-size: 20px;
  }
}

.notification-btn {
  .badge {
    position: absolute;
    top: 6px;
    right: 6px;
    width: 8px;
    height: 8px;
    background: #f97316;
    border-radius: 50%;
    border: 2px solid #ffffff;
  }
}

.user-section {
  .user-info {
    display: flex;
    align-items: center;
    gap: 12px;
    cursor: pointer;
    padding: 6px 12px;
    border-radius: 12px;
    transition: all 0.2s ease;
    border: 1px solid rgba(29, 108, 242, 0.14);
    background: rgba(255, 255, 255, 0.82);
    color: #0f172a;

    &:hover {
      background-color: rgba(239, 246, 255, 0.96);
    }
  }

  .user-avatar {
    background: linear-gradient(135deg, #1d6cf2 0%, #60a5fa 100%);
    font-weight: 600;
    box-shadow: 0 12px 24px rgba(29, 108, 242, 0.22);
  }

  .user-details {
    display: flex;
    flex-direction: column;
    line-height: 1.3;
  }

  .user-name {
    font-size: 14px;
    font-weight: 600;
    color: #0f172a;
  }

  .user-role {
    font-size: 12px;
    color: var(--admin-blue);
  }

  .dropdown-icon {
    color: #a1a1aa;
    font-size: 14px;
    transition: transform 0.2s ease;
  }
}

.content {
  flex: 1;
  padding: 28px 28px 20px;
  overflow-y: auto;
  overflow-x: hidden;
}

@media (max-width: 1180px) {
  .global-search {
    display: none;
  }
}
</style>
