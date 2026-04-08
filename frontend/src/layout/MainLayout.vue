<template>
  <div class="main-layout">
    <aside class="sidebar">
      <div class="logo-section">
        <div class="logo-icon">
          <svg viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M20 3L5 12L20 21L35 12L20 3Z" fill="url(#logoGrad1)"/>
            <path d="M5 26L20 35L35 26" stroke="url(#logoGrad2)" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>
            <defs>
              <linearGradient id="logoGrad1" x1="5" y1="3" x2="35" y2="35" gradientUnits="userSpaceOnUse">
                <stop stop-color="#ff6b35"/>
                <stop offset="1" stop-color="#ec4f1a"/>
              </linearGradient>
              <linearGradient id="logoGrad2" x1="5" y1="26" x2="35" y2="26" gradientUnits="userSpaceOnUse">
                <stop stop-color="#ff6b35"/>
                <stop offset="1" stop-color="#ec4f1a"/>
              </linearGradient>
            </defs>
          </svg>
        </div>
        <div class="logo-text">
          <h1>智慧餐饮</h1>
          <p>管理系统</p>
        </div>
      </div>

      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        router
        :collapse="isCollapse"
      >
        <el-menu-item index="/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <template #title>仪表盘</template>
        </el-menu-item>
        <el-menu-item index="/employee">
          <el-icon><User /></el-icon>
          <template #title>员工管理</template>
        </el-menu-item>
        <el-menu-item index="/category">
          <el-icon><Grid /></el-icon>
          <template #title>分类管理</template>
        </el-menu-item>
        <el-menu-item index="/dish">
          <el-icon><Mug /></el-icon>
          <template #title>菜品管理</template>
        </el-menu-item>
        <el-menu-item index="/setmeal">
          <el-icon><Sugar /></el-icon>
          <template #title>套餐管理</template>
        </el-menu-item>
        <el-menu-item index="/order">
          <el-icon><ShoppingCart /></el-icon>
          <template #title>订单管理</template>
        </el-menu-item>
        <el-menu-item index="/statistics">
          <el-icon><DataAnalysis /></el-icon>
          <template #title>数据统计</template>
        </el-menu-item>
        <el-menu-item index="/campus/settlement-batches">
          <el-icon><Tickets /></el-icon>
          <template #title>校园结算批次</template>
        </el-menu-item>
        <el-menu-item index="/campus/after-sale-executions">
          <el-icon><Document /></el-icon>
          <template #title>校园售后执行</template>
        </el-menu-item>
        <el-menu-item index="/shop-status">
          <el-icon><Position /></el-icon>
          <template #title>店铺营业状态</template>
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
            </el-breadcrumb-item>
            <el-breadcrumb-item>{{ breadcrumb }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="nav-right">
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
                  <span class="user-name">{{ userStore.currentUserInfo?.name || '管理员' }}</span>
                  <span class="user-role">{{ userStore.currentUserInfo?.role || 'admin' }}</span>
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
import { 
  HomeFilled, User, Grid, Mug, Sugar, ShoppingCart, DataAnalysis, Position, Tickets, Document,
  DArrowLeft, DArrowRight, House, FullScreen, Bell, ArrowDown, Setting, SwitchButton
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
  return path
})

const breadcrumb = computed(() => {
  const path = router.currentRoute.value.path
  if (path.startsWith('/campus/settlement-batches/')) {
    return '校园结算批次详情'
  }
  const pathMap = {
    '/dashboard': '仪表盘',
    '/employee': '员工管理',
    '/category': '分类管理',
    '/dish': '菜品管理',
    '/setmeal': '套餐管理',
    '/order': '订单管理',
    '/statistics': '数据统计',
    '/campus/settlement-batches': '校园结算批次',
    '/campus/after-sale-executions': '校园售后执行',
    '/shop-status': '店铺营业状态'
  }
  return pathMap[path] || ''
})

const userInitial = computed(() => {
  const name = userStore.currentUserInfo?.name || '管理员'
  return name.charAt(0)
})

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
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
  display: flex;
  height: 100vh;
  overflow: hidden;
  background-color: #f4f4f5;
}

.sidebar {
  width: 256px;
  background: linear-gradient(180deg, #ffffff 0%, #fafafa 100%);
  border-right: 1px solid #e4e4e7;
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease;

  &.is-collapsed {
    width: 64px;
  }
}

.logo-section {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px 16px;
  border-bottom: 1px solid #f4f4f5;
}

.logo-icon {
  width: 40px;
  height: 40px;
  flex-shrink: 0;
}

.logo-text {
  h1 {
    font-size: 16px;
    font-weight: 700;
    color: #18181b;
    margin: 0;
    line-height: 1.2;
  }

  p {
    font-size: 12px;
    color: #71717a;
    margin: 0;
    line-height: 1.2;
  }
}

.sidebar-menu {
  flex: 1;
  border-right: none;
  background: transparent;
  padding: 12px 8px;
  overflow-y: auto;

  &::-webkit-scrollbar {
    width: 4px;
  }

  &::-webkit-scrollbar-thumb {
    background: #e4e4e7;
    border-radius: 2px;
  }

  .el-menu-item {
    height: 44px;
    line-height: 44px;
    margin: 4px 0;
    border-radius: 10px;
    font-weight: 500;
    color: #52525b;
    transition: all 0.2s ease;

    .el-icon {
      font-size: 18px;
    }

    &:hover {
      background-color: rgba(255, 107, 53, 0.08);
      color: #ff6b35;
    }

    &.is-active {
      background: linear-gradient(135deg, #ff6b35 0%, #ec4f1a 100%);
      color: white;
      box-shadow: 0 4px 14px rgba(255, 107, 53, 0.35);

      &::before {
        display: none;
      }
    }
  }
}

.sidebar-footer {
  padding: 12px;
  border-top: 1px solid #f4f4f5;
}

.collapse-btn {
  width: 100%;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  color: #71717a;
  transition: all 0.2s ease;

  &:hover {
    background-color: #f4f4f5;
    color: #ff6b35;
  }

  .el-icon {
    font-size: 18px;
  }
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: #f4f4f5;
  min-width: 0;
}

.top-nav {
  height: 64px;
  background-color: #ffffff;
  border-bottom: 1px solid #e4e4e7;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.02);
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
            color: #ff6b35;
          }
        }

        .el-icon {
          font-size: 16px;
        }
      }

      &:last-child {
        .el-breadcrumb__inner {
          color: #18181b;
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

.nav-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  padding-right: 16px;
  border-right: 1px solid #e4e4e7;
}

.icon-btn {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #71717a;
  transition: all 0.2s ease;
  position: relative;

  &:hover {
    background-color: #f4f4f5;
    color: #ff6b35;
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
    background: #ef4444;
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

    &:hover {
      background-color: #f4f4f5;
    }
  }

  .user-avatar {
    background: linear-gradient(135deg, #ff6b35 0%, #ec4f1a 100%);
    font-weight: 600;
    box-shadow: 0 2px 8px rgba(255, 107, 53, 0.3);
  }

  .user-details {
    display: flex;
    flex-direction: column;
    line-height: 1.3;
  }

  .user-name {
    font-size: 14px;
    font-weight: 600;
    color: #18181b;
  }

  .user-role {
    font-size: 12px;
    color: #a1a1aa;
  }

  .dropdown-icon {
    color: #a1a1aa;
    font-size: 14px;
    transition: transform 0.2s ease;
  }
}

.content {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
  overflow-x: hidden;
}
</style>
