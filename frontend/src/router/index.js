import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'
import { useCustomerStore } from '../stores/customer'

const shellHomeMap = {
  user: '/user/login',
  parttime: '/parttime/login',
  admin: '/dashboard'
}

const viteMode = import.meta.env.MODE || ''
const shellFromMode = viteMode.startsWith('android-user')
  ? 'user'
  : viteMode.startsWith('android-parttime')
    ? 'parttime'
    : ''
const appShell = import.meta.env.VITE_APP_SHELL || shellFromMode || 'admin'
const rootRedirect = shellHomeMap[appShell] || shellHomeMap.admin

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: rootRedirect
    },
    {
      path: '/login',
      name: 'Login',
      meta: { public: true },
      component: () => import('../views/Login.vue')
    },
    {
      path: '/legal/privacy',
      name: 'PrivacyPolicy',
      meta: { public: true },
      component: () => import('../views/common/LegalDocument.vue')
    },
    {
      path: '/legal/terms',
      name: 'UserAgreement',
      meta: { public: true },
      component: () => import('../views/common/LegalDocument.vue')
    },
    {
      path: '/feedback',
      name: 'CampusFeedback',
      meta: { public: true },
      component: () => import('../views/common/Feedback.vue')
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
      path: '/campus/after-sale-executions',
      name: 'CampusAfterSaleExecutions',
      meta: { requiresAdminAuth: true },
      component: () => import('../views/CampusAfterSaleExecutionList.vue')
    },
    {
      path: '/campus/courier-ops',
      name: 'CampusCourierOps',
      meta: { requiresAdminAuth: true },
      component: () => import('../views/CampusCourierOpsView.vue')
    },
    {
      path: '/campus/settlements',
      name: 'CampusSettlements',
      meta: { requiresAdminAuth: true },
      component: () => import('../views/CampusSettlementOpsView.vue')
    },
    {
      path: '/campus/exceptions',
      name: 'CampusExceptions',
      meta: { requiresAdminAuth: true },
      component: () => import('../views/CampusExceptionOpsView.vue')
    },
    {
      path: '/campus/feedback',
      name: 'CampusFeedbackOps',
      meta: { requiresAdminAuth: true },
      component: () => import('../views/CampusFeedbackOpsView.vue')
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
      path: '/user/campus/order-result',
      name: 'UserCampusOrderResult',
      meta: { requiresCustomerAuth: true },
      component: () => import('../views/user/CampusOrderResult.vue')
    },
    {
      path: '/user/campus/orders',
      name: 'UserCampusRelayOrders',
      meta: { requiresCustomerAuth: true },
      component: () => import('../views/user/CampusRelayOrders.vue')
    },
    {
      path: '/user/campus/courier-onboarding',
      name: 'UserCampusCourierOnboarding',
      meta: { requiresCustomerAuth: true },
      component: () => import('../views/user/CourierOnboarding.vue')
    },
    {
      path: '/parttime/login',
      alias: '/courier/login',
      name: 'ParttimeLogin',
      meta: { public: true },
      component: () => import('../views/courier/Login.vue')
    },
    {
      path: '/parttime/workbench',
      alias: '/courier/workbench',
      name: 'ParttimeWorkbench',
      meta: { requiresCourierAuth: true },
      component: () => import('../views/courier/CourierWorkbench.vue')
    },
    {
      path: '/parttime/profile',
      name: 'ParttimeProfile',
      meta: { requiresCourierAuth: true },
      component: () => import('../views/courier/Profile.vue')
    }
  ]
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const customerStore = useCustomerStore()
  const hasCourierToken = Boolean(localStorage.getItem('courier_token'))

  if (to.meta.requiresAdminAuth && !userStore.isLoggedIn) {
    next('/login')
    return
  }

  if (to.meta.requiresCustomerAuth && !customerStore.isLoggedIn) {
    next('/user/login')
    return
  }

  if (to.meta.requiresCourierAuth && !hasCourierToken) {
    next({
      path: '/parttime/login',
      query: {
        redirect: to.fullPath
      }
    })
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

  if ((to.path === '/parttime/login' || to.path === '/courier/login') && hasCourierToken) {
    next('/parttime/workbench')
    return
  }

  next()
})

export default router
