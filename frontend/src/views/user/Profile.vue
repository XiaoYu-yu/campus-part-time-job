<template>
  <UserLayout>
    <div class="profile-page">
      <section class="profile-card">
        <img :src="userInfo.avatar || defaultAvatar" :alt="userInfo.name" class="avatar" />
        <div class="profile-info">
          <h2>{{ userInfo.name || '未命名用户' }}</h2>
          <p>{{ userInfo.phone || '暂无手机号' }}</p>
        </div>
      </section>

      <section class="stats-card">
        <div class="stat-item">
          <strong>{{ stats.campusOrders }}</strong>
          <span>代送单</span>
        </div>
        <div class="stat-item">
          <strong>{{ stats.serviceLabel }}</strong>
          <span>当前角色</span>
        </div>
      </section>

      <section class="card">
        <div class="section-header">
          <h3>校园代送</h3>
          <span class="section-tip">常用入口</span>
        </div>
        <div class="menu-item" @click="router.push('/user/campus/orders')">发布校园代送</div>
        <div class="menu-item" @click="router.push('/user/campus/order-result')">查看代送进度</div>
        <div class="menu-item" @click="router.push('/user/campus/after-sale-result')">查看售后结果</div>
        <div class="menu-item" @click="router.push('/user/campus/courier-onboarding')">报名校园兼职</div>
        <div class="menu-item" @click="router.push('/parttime/login')">兼职端登录</div>
        <div class="menu-item" @click="router.push('/parttime/workbench')">兼职工作台</div>
        <div class="menu-item" @click="router.push('/feedback?role=USER&from=user-profile')">问题反馈</div>
        <div class="menu-item" @click="router.push('/legal/privacy')">隐私政策</div>
      </section>

      <button class="logout-btn" @click="handleLogout">退出登录</button>
    </div>
  </UserLayout>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import UserLayout from '../../layout/UserLayout.vue'
import { customerLogout, getCustomerInfo } from '../../api/customer'
import { getCampusCustomerOrders } from '../../api/campus-customer'
import { useCustomerStore } from '../../stores/customer'

const router = useRouter()
const customerStore = useCustomerStore()
const defaultAvatar = 'https://dummyimage.com/120x120/f3f4f6/9ca3af&text=U'

const userInfo = ref({ ...customerStore.userInfo })
const stats = ref({ campusOrders: 0, serviceLabel: '用户' })

const loadProfile = async () => {
  const [userRes, campusOrderRes] = await Promise.all([
    getCustomerInfo(),
    getCampusCustomerOrders({ page: 1, pageSize: 1 })
  ])
  userInfo.value = userRes
  customerStore.setUserInfo(userRes)
  stats.value = {
    campusOrders: campusOrderRes?.total || 0,
    serviceLabel: '用户'
  }
}

const handleLogout = async () => {
  await customerLogout()
  customerStore.logout()
  ElMessage.success('已退出登录')
  router.push('/user/login')
}

onMounted(() => loadProfile())
</script>

<style scoped lang="scss">
.profile-page {
  padding: 16px;
  overflow-x: hidden;
}

.profile-card,
.stats-card,
.card {
  background: #ffffff;
  border-radius: 14px;
  padding: 16px;
  margin-bottom: 14px;
  border: 1px solid #e4e4e7;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05), 0 4px 12px rgba(0, 0, 0, 0.03);
}

.profile-card {
  display: flex;
  align-items: center;
  gap: 16px;
}

.avatar {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #e4e4e7;
  flex-shrink: 0;
}

.profile-info {
  min-width: 0;

  h2 {
    margin: 0 0 4px;
    font-size: 18px;
    color: #18181b;
    font-weight: 700;
  }

  p {
    margin: 0;
    color: #71717a;
    font-size: 14px;
  }
}

.stats-card {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.stat-item {
  text-align: center;
  padding: 12px 8px;
  border-radius: 12px;
  background: #fafafa;
  border: 1px solid #e4e4e7;

  strong {
    display: block;
    font-size: 22px;
    color: #0f9f8f;
    font-weight: 700;
    margin-bottom: 4px;
  }

  span {
    color: #71717a;
    font-size: 13px;
  }
}

.section-header,
.address-item,
.menu-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-header {
  margin-bottom: 12px;

  h3 {
    margin: 0;
    font-size: 16px;
    color: #18181b;
    font-weight: 700;
  }
}

.section-tip {
  color: #a1a1aa;
  font-size: 12px;
}

.menu-item {
  padding: 14px 0;
  border-bottom: 1px solid #f4f4f5;
  cursor: pointer;
  font-size: 14px;
  color: #18181b;
  transition: background 0.15s;

  &::after {
    content: '';
    width: 7px;
    height: 7px;
    border-top: 2px solid #a1a1aa;
    border-right: 2px solid #a1a1aa;
    transform: rotate(45deg);
    flex-shrink: 0;
    margin-left: 8px;
  }

  &:active {
    background: #f4f4f5;
  }
}

.menu-item:last-child {
  border-bottom: none;
}

.logout-btn,
.text-btn {
  border: none;
  cursor: pointer;
  font-family: inherit;
}

.logout-btn {
  width: 100%;
  min-height: 48px;
  background: #ef4444;
  color: #ffffff;
  border-radius: 12px;
  padding: 14px 0;
  font-size: 15px;
  font-weight: 600;
  margin-top: 8px;
  transition: background 0.2s, box-shadow 0.2s;

  &:hover {
    background: #dc2626;
    box-shadow: 0 4px 14px rgba(239, 68, 68, 0.3);
  }

  &:active {
    background: #b91c1c;
  }
}

.text-btn {
  background: transparent;
  color: #0f9f8f;
  font-weight: 600;
  font-size: 13px;
  padding: 4px 0;
}

.empty-tip {
  color: #a1a1aa;
  font-size: 14px;
  padding: 12px 0;
}

@media (max-width: 360px) {
  .profile-page {
    padding: 12px;
  }

  .profile-card,
  .stats-card,
  .card {
    padding: 14px 12px;
    border-radius: 12px;
  }

  .avatar {
    width: 60px;
    height: 60px;
  }

  .profile-info h2 {
    font-size: 16px;
  }

  .stat-item strong {
    font-size: 20px;
  }
}
</style>
