import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'
import { useCustomerStore } from '../stores/customer'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/dashboard'
    },
    {
      path: '/login',
      name: 'Login',
      meta: { public: true },
      component: () => import('../views/Login.vue')
    },
    {
      path: '/dashboard',
      name: 'Dashboard',
      meta: { requiresAdminAuth: true },
      component: () => import('../views/Dashboard.vue')
    },
    {
      path: '/employee',
      name: 'Employee',
      meta: { requiresAdminAuth: true },
      component: () => import('../views/Employee.vue')
    },
    {
      path: '/category',
      name: 'Category',
      meta: { requiresAdminAuth: true },
      component: () => import('../views/Category.vue')
    },
    {
      path: '/dish',
      name: 'Dish',
      meta: { requiresAdminAuth: true },
      component: () => import('../views/Dish.vue')
    },
    {
      path: '/setmeal',
      name: 'Setmeal',
      meta: { requiresAdminAuth: true },
      component: () => import('../views/Setmeal.vue')
    },
    {
      path: '/order',
      name: 'Order',
      meta: { requiresAdminAuth: true },
      component: () => import('../views/Order.vue')
    },
    {
      path: '/statistics',
      name: 'Statistics',
      meta: { requiresAdminAuth: true },
      component: () => import('../views/Statistics.vue')
    },
    {
      path: '/campus/settlement-batches',
      name: 'CampusSettlementBatches',
      meta: { requiresAdminAuth: true },
      component: () => import('../views/CampusSettlementBatchList.vue')
    },
    {
      path: '/campus/settlement-batches/:batchNo',
      name: 'CampusSettlementBatchDetail',
      meta: { requiresAdminAuth: true },
      component: () => import('../views/CampusSettlementBatchDetail.vue')
    },
    {
      path: '/shop-status',
      name: 'ShopStatus',
      meta: { requiresAdminAuth: true },
      component: () => import('../views/ShopStatus.vue')
    },
    {
      path: '/component-demo',
      name: 'ComponentDemo',
      meta: { requiresAdminAuth: true },
      component: () => import('../views/ComponentDemo.vue')
    },
    // 用户端路由
    {
      path: '/user/login',
      name: 'UserLogin',
      meta: { public: true },
      component: () => import('../views/user/Login.vue')
    },
    {
      path: '/user',
      name: 'UserHome',
      meta: { requiresCustomerAuth: true },
      component: () => import('../views/user/Home.vue')
    },
    {
      path: '/user/category',
      name: 'UserCategory',
      meta: { requiresCustomerAuth: true },
      component: () => import('../views/user/Category.vue')
    },
    {
      path: '/user/dish/:id',
      name: 'UserDishDetail',
      meta: { requiresCustomerAuth: true },
      component: () => import('../views/user/DishDetail.vue')
    },
    {
      path: '/user/cart',
      name: 'UserCart',
      meta: { requiresCustomerAuth: true },
      component: () => import('../views/user/Cart.vue')
    },
    {
      path: '/user/checkout',
      name: 'UserCheckout',
      meta: { requiresCustomerAuth: true },
      component: () => import('../views/user/Checkout.vue')
    },
    {
      path: '/user/orders',
      name: 'UserOrders',
      meta: { requiresCustomerAuth: true },
      component: () => import('../views/user/Orders.vue')
    },
    {
      path: '/user/profile',
      name: 'UserProfile',
      meta: { requiresCustomerAuth: true },
      component: () => import('../views/user/Profile.vue')
    },
    {
      path: '/user/campus/after-sale-result',
      name: 'UserCampusAfterSaleResult',
      meta: { requiresCustomerAuth: true },
      component: () => import('../views/user/AfterSaleResult.vue')
    },
    {
      path: '/user/campus/courier-onboarding',
      name: 'UserCampusCourierOnboarding',
      meta: { requiresCustomerAuth: true },
      component: () => import('../views/user/CourierOnboarding.vue')
    }
  ]
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const customerStore = useCustomerStore()

  if (to.meta.requiresAdminAuth && !userStore.isLoggedIn) {
    next('/login')
    return
  }

  if (to.meta.requiresCustomerAuth && !customerStore.isLoggedIn) {
    next('/user/login')
    return
  }

  if (to.path === '/login' && userStore.isLoggedIn) {
    next('/dashboard')
    return
  }

  if (to.path === '/user/login' && customerStore.isLoggedIn) {
    next('/user')
    return
  }

  next()
})

export default router
