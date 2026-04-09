<template>
  <div class="user-layout">
    <header class="header">
      <div class="header-content">
        <div class="shop-info">
          <h1 class="shop-name">{{ shopName }}</h1>
          <p class="shop-desc">
            {{ shopStatus.isOpen ? '营业中，欢迎下单' : '当前休息中' }}
            <span v-if="shopStatus.restNotice"> · {{ shopStatus.restNotice }}</span>
          </p>
        </div>
        <div v-if="showCustomerNav" class="header-actions">
          <button class="header-btn" @click="goToCart">
            <i class="el-icon-shopping-cart-2"></i>
            <span v-if="cartCount > 0" class="cart-badge">{{ cartCount }}</span>
          </button>
          <button class="header-btn" @click="goToUser">
            <i class="el-icon-user"></i>
          </button>
        </div>
      </div>
    </header>

    <main class="main-content">
      <slot></slot>
    </main>

    <footer v-if="showCustomerNav" class="footer">
      <nav class="footer-nav">
        <router-link to="/user" class="nav-item" active-class="active">
          <i class="el-icon-s-home"></i>
          <span>首页</span>
        </router-link>
        <router-link to="/user/category" class="nav-item" active-class="active">
          <i class="el-icon-menu"></i>
          <span>分类</span>
        </router-link>
        <router-link to="/user/cart" class="nav-item" active-class="active">
          <i class="el-icon-shopping-cart-2"></i>
          <span>购物车</span>
          <span v-if="cartCount > 0" class="cart-badge-small">{{ cartCount }}</span>
        </router-link>
        <router-link to="/user/profile" class="nav-item" active-class="active">
          <i class="el-icon-user"></i>
          <span>我的</span>
        </router-link>
      </nav>
    </footer>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getCartCount } from '../api/cart'
import { getPublicShopStatus } from '../api/shop'

const router = useRouter()
const route = useRoute()

const shopName = '苍穹外卖'
const cartCount = ref(0)
const shopStatus = ref({
  isOpen: true,
  restNotice: ''
})
const hasCustomerToken = computed(() => Boolean(localStorage.getItem('customer_token')))
const showCustomerNav = computed(() => hasCustomerToken.value && !route.path.startsWith('/courier/'))

const loadCartCount = async () => {
  if (!hasCustomerToken.value) {
    cartCount.value = 0
    return
  }
  try {
    cartCount.value = await getCartCount()
  } catch (error) {
    cartCount.value = 0
  }
}

const loadShopStatus = async () => {
  try {
    shopStatus.value = await getPublicShopStatus()
  } catch (error) {
    console.error('加载店铺状态失败:', error)
  }
}

const goToCart = () => {
  router.push('/user/cart')
}

const goToUser = () => {
  router.push('/user/profile')
}

onMounted(async () => {
  await Promise.all([loadShopStatus(), loadCartCount()])
})

watch(() => route.fullPath, () => {
  loadCartCount()
})
</script>

<style scoped lang="scss">
.user-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f5f5;
}

.header {
  background: linear-gradient(135deg, #ff7d00 0%, #ffb74d 100%);
  color: white;
  padding: 16px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;

  .header-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    max-width: 1200px;
    margin: 0 auto;
  }

  .shop-name {
    font-size: 20px;
    font-weight: bold;
    margin: 0 0 4px;
  }

  .shop-desc {
    font-size: 12px;
    margin: 0;
    opacity: 0.95;
  }
}

.header-actions {
  display: flex;
  gap: 12px;
}

.header-btn {
  position: relative;
  width: 40px;
  height: 40px;
  border: none;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  cursor: pointer;
}

.cart-badge,
.cart-badge-small {
  position: absolute;
  background: #f56c6c;
  color: white;
  border-radius: 999px;
  text-align: center;
}

.cart-badge {
  top: -4px;
  right: -4px;
  min-width: 20px;
  padding: 2px 6px;
  font-size: 12px;
}

.main-content {
  flex: 1;
  padding-bottom: 60px;
}

.footer {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  background: #fff;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
}

.footer-nav {
  display: flex;
  justify-content: space-around;
  height: 56px;
  align-items: center;
}

.nav-item {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #909399;
  text-decoration: none;

  &.active {
    color: #ff7d00;
  }

  span {
    font-size: 12px;
  }
}

.cart-badge-small {
  top: 0;
  right: -8px;
  min-width: 16px;
  padding: 1px 4px;
  font-size: 10px;
}
</style>
