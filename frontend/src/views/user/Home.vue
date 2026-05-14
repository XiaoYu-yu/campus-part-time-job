<template>
  <UserLayout>
    <div class="home-page">
      <section class="hero-card">
        <div class="hero-copy">
          <div class="hero-kicker">校内兼职</div>
          <h1>
            <span>校内代送</span>
            <span>一键发布</span>
          </h1>
          <p>想让同学帮忙取餐、送东西，或者自己想接单赚点零花钱，都从这里开始。</p>
        </div>
        <div class="hero-illustration" aria-hidden="true">
          <span class="campus-building"></span>
          <span class="route-dot dot-a"></span>
          <span class="route-dot dot-b"></span>
          <span class="route-line"></span>
        </div>
        <div class="status-panel">
          <div class="avatar-bubble">Hi</div>
          <div class="status-copy">
            <strong>欢迎回来</strong>
            <span>今日校园服务已开启</span>
          </div>
          <div class="status-metrics">
            <span><strong>发布</strong>代送单</span>
            <span><strong>查看</strong>订单进度</span>
            <span><strong>报名</strong>接单兼职</span>
          </div>
        </div>
      </section>

      <button class="primary-banner-action" type="button" @click="goTo('/user/campus/orders')">
        <span>➤</span>
        发一单校园代送
        <small>填取餐点和送达位置</small>
      </button>

      <section class="quick-grid">
        <button class="quick-card" type="button" @click="goTo('/user/campus/orders')">
          <span class="quick-icon order">▣</span>
          <strong>发布代送单</strong>
          <p>取餐点、宿舍楼、备注一次填好。</p>
        </button>
        <button class="quick-card" type="button" @click="goTo('/user/campus/order-result')">
          <span class="quick-icon result">☑</span>
          <strong>查看订单进度</strong>
          <p>输入订单号，看是否送达和完成。</p>
        </button>
        <button class="quick-card" type="button" @click="goTo('/user/campus/courier-onboarding')">
          <span class="quick-icon onboard">✦</span>
          <strong>报名做兼职</strong>
          <p>填资料，审核通过后就能接单。</p>
        </button>
        <button class="quick-card" type="button" @click="goTo('/parttime/login')">
          <span class="quick-icon workbench">↗</span>
          <strong>兼职端登录</strong>
          <p>审核通过后，到这里接单干活。</p>
        </button>
      </section>

      <section class="campus-entry-card">
        <div>
          <span>校园代送主入口</span>
          <h2>从校内取件到宿舍送达</h2>
          <p>这里先走模拟支付，不会真的扣钱。</p>
        </div>
        <button type="button" @click="goTo('/user/campus/orders')">立即使用</button>
      </section>

      <section class="card order-card">
        <div class="section-heading">
          <div>
            <span>订单结果</span>
            <h2>查一下我的代送单</h2>
          </div>
        </div>
        <div class="order-search">
          <el-input v-model="orderId" placeholder="输入订单号，例如 CR202604060001" clearable @keyup.enter="openOrderResult" />
          <el-button type="primary" @click="openOrderResult">查询</el-button>
        </div>
        <p class="helper-text">这里只会打开订单详情，不会自动确认或发起售后。</p>
      </section>

      <section class="card status-card">
        <div class="section-heading">
          <div>
            <span>兼职入驻</span>
            <h2>我的兼职报名</h2>
          </div>
          <button class="text-link" type="button" @click="refreshOnboardingStatus">刷新</button>
        </div>

        <div v-if="statusLoading" class="state-row">
          <span class="state-dot loading"></span>
          <div>
            <strong>正在读取报名状态</strong>
            <p>正在看看你的报名资料有没有更新。</p>
          </div>
        </div>
        <div v-else-if="statusError" class="state-row warning">
          <span class="state-dot warning"></span>
          <div>
            <strong>暂时没拿到报名状态</strong>
            <p>{{ statusError }}</p>
          </div>
        </div>
        <div v-else class="onboarding-summary">
          <div class="summary-item">
            <span>审核状态</span>
            <strong>{{ statusLabel(reviewStatus.reviewStatus) }}</strong>
          </div>
          <div class="summary-item">
            <span>接单资格</span>
            <strong>{{ eligibility.eligible ? '可以申请' : '暂不可申请' }}</strong>
          </div>
          <p>{{ eligibility.message || reviewStatus.message || '可以进入报名页查看资料、审核状态和接单资格。' }}</p>
        </div>

        <button class="wide-action" type="button" @click="goTo('/user/campus/courier-onboarding')">
          去报名兼职
        </button>
      </section>

    </div>
  </UserLayout>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import UserLayout from '../../layout/UserLayout.vue'
import {
  getCourierOnboardingReviewStatus,
  getCourierTokenEligibility
} from '../../api/campus-customer'

const router = useRouter()
const orderId = ref('CR202604060001')
const statusLoading = ref(false)
const statusError = ref('')
const reviewStatus = ref({})
const eligibility = ref({})

const goTo = (path) => {
  router.push(path)
}

const statusLabel = (status) => {
  const map = {
    PENDING: '待审核',
    APPROVED: '已通过',
    REJECTED: '未通过',
    NOT_SUBMITTED: '未提交'
  }
  return map[status] || status || '未提交'
}

const openOrderResult = () => {
  const normalized = orderId.value?.trim()
  if (!normalized) {
    ElMessage.warning('请先输入校园代送订单号')
    return
  }
  router.push({
    path: '/user/campus/order-result',
    query: { orderId: normalized }
  })
}

const refreshOnboardingStatus = async () => {
  statusLoading.value = true
  statusError.value = ''
  try {
    const [reviewRes, eligibilityRes] = await Promise.all([
      getCourierOnboardingReviewStatus(),
      getCourierTokenEligibility()
    ])
    reviewStatus.value = reviewRes || {}
    eligibility.value = eligibilityRes || {}
  } catch (error) {
    statusError.value = error?.message || '入驻状态读取失败，可直接进入入驻页重试。'
  } finally {
    statusLoading.value = false
  }
}

onMounted(() => {
  refreshOnboardingStatus()
})
</script>

<style scoped lang="scss">
.home-page {
  padding: 14px 14px 24px;
  overflow-x: hidden;
}

.hero-card,
.card,
.quick-card,
.campus-entry-card {
  background: #ffffff;
  border: 1px solid #e4e4e7;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05), 0 4px 12px rgba(0, 0, 0, 0.03);
}

.hero-card {
  position: relative;
  overflow: hidden;
  border-radius: 14px;
  padding: 22px 18px 16px;
  margin-bottom: 16px;
  background: linear-gradient(135deg, #eefdfa, #f0fdfa);

  h1 {
    display: flex;
    flex-direction: column;
    gap: 2px;
    position: relative;
    z-index: 1;
    margin: 8px 0 10px;
    font-size: 26px;
    line-height: 1.15;
    letter-spacing: -0.5px;
    color: #18181b;
    font-weight: 700;
  }

  p {
    position: relative;
    z-index: 1;
    margin: 0;
    color: #52525b;
    line-height: 1.65;
    font-size: 14px;
  }
}

.hero-copy {
  position: relative;
  z-index: 2;
  max-width: 100%;
}

.hero-kicker,
.section-heading span {
  color: #0f9f8f;
  font-size: 12px;
  font-weight: 800;
  letter-spacing: 0.06em;
}

.hero-illustration {
  display: none;
}

.campus-building,
.route-dot,
.dot-a,
.dot-b,
.route-line {
  display: none;
}

.status-panel {
  position: relative;
  z-index: 2;
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 18px;
  padding: 14px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  border: 1px solid #e4e4e7;
}

.avatar-bubble {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: grid;
  place-items: center;
  background: #eefdfa;
  color: #0f9f8f;
  font-weight: 900;
  flex-shrink: 0;
}

.status-copy {
  min-width: 0;
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;

  strong {
    color: #18181b;
    font-size: 15px;
  }

  span {
    color: #71717a;
    font-size: 12px;
  }
}

.status-metrics {
  display: flex;
  gap: 6px;

  span {
    min-width: 48px;
    display: flex;
    flex-direction: column;
    align-items: center;
    color: #71717a;
    font-size: 11px;
    line-height: 1.3;
  }

  strong {
    color: #0f9f8f;
    font-size: 13px;
  }
}

.primary-banner-action,
.wide-action,
.text-link,
.quick-card {
  border: none;
  cursor: pointer;
  font-family: inherit;
}

.primary-banner-action,
.wide-action {
  background: #0f9f8f;
  color: #fff;
  transition: background 0.2s, box-shadow 0.2s;
}

.primary-banner-action:hover,
.wide-action:hover {
  background: #0d8a7e;
  box-shadow: 0 4px 14px rgba(15, 159, 143, 0.3);
}

.primary-banner-action:active,
.wide-action:active {
  background: #0b7a70;
}

.primary-banner-action {
  width: 100%;
  min-height: 52px;
  border-radius: 14px;
  margin: 0 0 14px;
  padding: 12px 16px;
  display: grid;
  grid-template-columns: 32px 1fr;
  align-items: center;
  gap: 10px;
  text-align: left;
  font-size: 15px;
  font-weight: 700;

  span {
    width: 32px;
    height: 32px;
    border-radius: 10px;
    display: grid;
    place-items: center;
    background: rgba(255, 255, 255, 0.2);
  }

  small {
    grid-column: 2;
    margin-top: -4px;
    color: rgba(255, 255, 255, 0.8);
    font-size: 12px;
    font-weight: 500;
  }
}

.quick-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  margin-bottom: 16px;
}

.quick-card {
  display: flex;
  flex-direction: column;
  gap: 8px;
  text-align: left;
  border-radius: 14px;
  padding: 14px;
  color: #18181b;
  transition: box-shadow 0.2s, transform 0.15s;

  &:active {
    transform: scale(0.98);
  }

  strong {
    font-size: 15px;
    color: #18181b;
  }

  p {
    margin: 0;
    color: #71717a;
    line-height: 1.5;
    font-size: 13px;
  }
}

.quick-icon {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  display: grid;
  place-items: center;
  color: #fff;
  font-weight: 900;
}

.quick-icon.result {
  background: #10b981;
}

.quick-icon.order {
  background: #0f9f8f;
}

.quick-icon.onboard {
  background: #3b82f6;
}

.quick-icon.workbench {
  background: #f59e0b;
}

.campus-entry-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  border-radius: 14px;
  padding: 16px;
  margin-bottom: 16px;

  span {
    color: #0f9f8f;
    font-size: 12px;
    font-weight: 800;
    letter-spacing: 0.06em;
  }

  h2 {
    margin: 4px 0 4px;
    font-size: 17px;
  }

  p {
    margin: 0;
    color: #71717a;
    line-height: 1.5;
    font-size: 13px;
  }

  button {
    flex: 0 0 auto;
    border: none;
    border-radius: 10px;
    padding: 10px 14px;
    background: rgba(15, 159, 143, 0.1);
    color: #0f9f8f;
    font-weight: 700;
    font-size: 13px;
    cursor: pointer;
    transition: background 0.2s;

    &:active {
      background: rgba(15, 159, 143, 0.2);
    }
  }
}

.card {
  border-radius: 14px;
  padding: 16px;
  margin-bottom: 16px;
}

.section-heading {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 14px;

  h2 {
    margin: 4px 0 0;
    font-size: 17px;
    color: #18181b;
  }
}

.order-search {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 10px;
}

.helper-text,
.state-row p,
.onboarding-summary p {
  margin: 10px 0 0;
  color: #71717a;
  line-height: 1.6;
  font-size: 13px;
}

.text-link {
  background: transparent;
  color: #0f9f8f;
  font-weight: 700;
  padding: 4px 0;
  font-size: 13px;
}

.state-row {
  display: flex;
  gap: 12px;
  padding: 14px;
  border-radius: 12px;
  background: #f0fdfa;
  border: 1px solid #e4e4e7;

  strong {
    color: #18181b;
  }
}

.state-row.warning {
  background: #fffbeb;
  border-color: #fde68a;
}

.state-dot {
  flex: 0 0 auto;
  width: 10px;
  height: 10px;
  margin-top: 4px;
  border-radius: 50%;
  background: #10b981;
}

.state-dot.loading {
  background: #3b82f6;
  animation: spin 1s linear infinite;
}

.state-dot.warning {
  background: #f59e0b;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.onboarding-summary {
  display: grid;
  gap: 10px;
}

.summary-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 14px;
  border-radius: 12px;
  background: #fafafa;
  border: 1px solid #e4e4e7;

  span {
    color: #71717a;
    font-size: 14px;
  }

  strong {
    color: #0f9f8f;
    font-size: 14px;
  }
}

.wide-action {
  width: 100%;
  min-height: 48px;
  margin-top: 14px;
  border-radius: 12px;
  padding: 12px 16px;
  font-size: 15px;
  font-weight: 700;
}

@media (min-width: 768px) {
  .home-page {
    max-width: 520px;
    margin: 0 auto;
  }
}

@media (max-width: 480px) {
  .quick-grid {
    grid-template-columns: 1fr;
  }

  .order-search {
    grid-template-columns: 1fr;
  }

  .status-metrics {
    display: none;
  }
}

@media (max-width: 360px) {
  .home-page {
    padding: 10px 10px 20px;
  }

  .hero-card {
    padding: 18px 14px 14px;

    h1 {
      font-size: 24px;
    }
  }

  .card {
    padding: 14px 12px;
  }

  .quick-card {
    padding: 12px;
  }

  .campus-entry-card {
    padding: 14px 12px;
    flex-direction: column;
    align-items: flex-start;

    button {
      align-self: stretch;
      text-align: center;
    }
  }
}
</style>
