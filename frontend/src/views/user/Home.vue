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
          <p>发布校园代送需求、查看订单结果，也可以申请成为兼职人员。</p>
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
            <span><strong>发布</strong>代送需求</span>
            <span><strong>回看</strong>订单结果</span>
            <span><strong>申请</strong>兼职入驻</span>
          </div>
        </div>
      </section>

      <button class="primary-banner-action" type="button" @click="goTo('/user/campus/orders')">
        <span>➤</span>
        发布校园代送需求
        <small>填写取件与送达信息</small>
      </button>

      <section class="quick-grid">
        <button class="quick-card" type="button" @click="goTo('/user/campus/orders')">
          <span class="quick-icon order">▣</span>
          <strong>发布代送需求</strong>
          <p>取餐点、楼栋与备注一页填写。</p>
        </button>
        <button class="quick-card" type="button" @click="goTo('/user/campus/order-result')">
          <span class="quick-icon result">☑</span>
          <strong>查看我的订单</strong>
          <p>按订单号回看确认与完成状态。</p>
        </button>
        <button class="quick-card" type="button" @click="goTo('/user/campus/courier-onboarding')">
          <span class="quick-icon onboard">✦</span>
          <strong>申请成为兼职人员</strong>
          <p>提交资料，通过后申请兼职 token。</p>
        </button>
        <button class="quick-card" type="button" @click="goTo('/parttime/login')">
          <span class="quick-icon workbench">↗</span>
          <strong>兼职端登录</strong>
          <p>已审核人员进入工作台接任务。</p>
        </button>
      </section>

      <section class="campus-entry-card">
        <div>
          <span>校园代送主入口</span>
          <h2>从校内取件到宿舍送达</h2>
          <p>模拟支付只用于流程演示，不产生真实扣费。</p>
        </div>
        <button type="button" @click="goTo('/user/campus/orders')">立即使用</button>
      </section>

      <section class="card order-card">
        <div class="section-heading">
          <div>
            <span>订单结果</span>
            <h2>快速回看校园代送单</h2>
          </div>
        </div>
        <div class="order-search">
          <el-input v-model="orderId" placeholder="输入订单号，例如 CR202604060001" clearable @keyup.enter="openOrderResult" />
          <el-button type="primary" @click="openOrderResult">回看</el-button>
        </div>
        <p class="helper-text">只读跳转到结果回看页，不触发确认、售后或其它写操作。</p>
      </section>

      <section class="card status-card">
        <div class="section-heading">
          <div>
            <span>兼职入驻</span>
            <h2>当前申请状态</h2>
          </div>
          <button class="text-link" type="button" @click="refreshOnboardingStatus">刷新</button>
        </div>

        <div v-if="statusLoading" class="state-row">
          <span class="state-dot loading"></span>
          <div>
            <strong>正在读取入驻状态</strong>
            <p>正在调用 customer onboarding 读取接口。</p>
          </div>
        </div>
        <div v-else-if="statusError" class="state-row warning">
          <span class="state-dot warning"></span>
          <div>
            <strong>暂未读取到状态</strong>
            <p>{{ statusError }}</p>
          </div>
        </div>
        <div v-else class="onboarding-summary">
          <div class="summary-item">
            <span>审核状态</span>
            <strong>{{ statusLabel(reviewStatus.reviewStatus) }}</strong>
          </div>
          <div class="summary-item">
            <span>token 资格</span>
            <strong>{{ eligibility.eligible ? '可以申请' : '暂不可申请' }}</strong>
          </div>
          <p>{{ eligibility.message || reviewStatus.message || '可进入入驻页查看资料、审核状态和 token 申请入口。' }}</p>
        </div>

        <button class="wide-action" type="button" @click="goTo('/user/campus/courier-onboarding')">
          进入兼职入驻页
        </button>
      </section>

      <section class="card legacy-card">
        <div class="section-heading">
          <div>
            <span>旧模块兼容</span>
            <h2>兼容功能仍保留</h2>
          </div>
        </div>
        <p>旧分类、购物车、历史订单和地址能力未删除，当前仅从首页主视觉中降级为兼容入口。</p>
        <div class="legacy-links">
          <button type="button" @click="goTo('/user/category')">分类</button>
          <button type="button" @click="goTo('/user/cart')">购物车</button>
          <button type="button" @click="goTo('/user/orders')">旧订单</button>
          <button type="button" @click="goTo('/user/profile')">地址 / 我的</button>
        </div>
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
}

.hero-card,
.card,
.quick-card,
.campus-entry-card {
  border: 1px solid #e4e4e7;
  background: #ffffff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}

.hero-card {
  position: relative;
  overflow: hidden;
  border-radius: 16px;
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
  max-width: 76%;
}

.hero-kicker,
.section-heading span {
  color: #0f9f8f;
  font-size: 12px;
  font-weight: 800;
  letter-spacing: 0.06em;
}

.hero-illustration {
  position: absolute;
  right: 14px;
  top: 24px;
  width: 130px;
  height: 112px;
  z-index: 1;
  opacity: 0.6;
}

.campus-building {
  position: absolute;
  right: 8px;
  bottom: 18px;
  width: 76px;
  height: 56px;
  border-radius: 14px 14px 10px 10px;
  background:
    linear-gradient(90deg, rgba(255, 255, 255, 0.62) 1px, transparent 1px) 0 0 / 18px 100%,
    linear-gradient(180deg, #c7f2ff, #f7ffff);
  box-shadow: 0 4px 12px rgba(28, 128, 160, 0.1);
}

.route-dot {
  position: absolute;
  width: 14px;
  height: 14px;
  border-radius: 50%;
  background: #21c4b6;
  box-shadow: 0 0 0 5px rgba(33, 196, 182, 0.1);
}

.dot-a {
  left: 12px;
  top: 26px;
}

.dot-b {
  right: 14px;
  bottom: 8px;
  background: #28aef5;
  box-shadow: 0 0 0 5px rgba(40, 174, 245, 0.1);
}

.route-line {
  position: absolute;
  left: 28px;
  right: 28px;
  top: 48px;
  height: 46px;
  border: 2px dashed rgba(15, 118, 110, 0.15);
  border-top: 0;
  border-left: 0;
  border-radius: 0 0 40px 0;
}

.status-panel {
  position: relative;
  z-index: 2;
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 18px;
  padding: 14px;
  border-radius: 14px;
  background: #fafafa;
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
.legacy-links button,
.quick-card {
  border: none;
  cursor: pointer;
  font-family: inherit;
}

.primary-banner-action,
.wide-action {
  background: #0f9f8f;
  color: #fff;
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
  background: #ffffff;

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
.legacy-card p,
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
}

.state-dot.warning {
  background: #f59e0b;
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
  margin-top: 14px;
  border-radius: 12px;
  padding: 12px 16px;
  font-size: 15px;
  font-weight: 700;
}

.legacy-card {
  background: #fafafa;
}

.legacy-links {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
  margin-top: 14px;

  button {
    border: 1px solid #e4e4e7;
    border-radius: 12px;
    background: #ffffff;
    color: #0f9f8f;
    padding: 11px;
    font-weight: 700;
    font-size: 14px;
  }
}

@media (min-width: 768px) {
  .home-page {
    max-width: 520px;
    margin: 0 auto;
  }
}

@media (max-width: 390px) {
  .hero-card {
    padding: 20px 16px 16px;

    h1 {
      font-size: 24px;
    }
  }

  .hero-copy {
    max-width: 78%;
  }

  .hero-illustration {
    right: 8px;
    width: 112px;
  }
}
</style>
