<template>
  <UserLayout>
    <div class="home-page">
      <section class="hero-card">
        <div class="hero-kicker">校园兼职用户端</div>
        <h1>找人代送、查看结果、申请兼职，一处完成</h1>
        <p>当前移动端默认展示校园兼职主链路，旧外卖模块继续保留为兼容入口，不再作为首页主内容。</p>
        <div class="hero-actions">
          <button class="primary-action" type="button" @click="goTo('/user/campus/order-result')">查看代送结果</button>
          <button class="ghost-action" type="button" @click="goTo('/user/campus/courier-onboarding')">申请成为兼职</button>
        </div>
      </section>

      <section class="quick-grid">
        <button class="quick-card" type="button" @click="goTo('/user/campus/order-result')">
          <span class="quick-icon result">✓</span>
          <strong>结果回看</strong>
          <p>输入订单号查看等待确认、已完成和异常摘要。</p>
        </button>
        <button class="quick-card" type="button" @click="goTo('/user/campus/courier-onboarding')">
          <span class="quick-icon onboard">＋</span>
          <strong>兼职入驻</strong>
          <p>提交资料、查看审核状态，并在通过后申请兼职 token。</p>
        </button>
        <button class="quick-card" type="button" @click="goTo('/parttime/login')">
          <span class="quick-icon workbench">↗</span>
          <strong>兼职端登录</strong>
          <p>已审核人员可进入兼职端工作台处理代送单。</p>
        </button>
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
            <h2>外卖功能仍保留</h2>
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
  padding: 16px 14px 24px;
}

.hero-card,
.card,
.quick-card {
  border: 1px solid rgba(15, 118, 110, 0.1);
  background: rgba(255, 255, 255, 0.86);
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.08);
  backdrop-filter: blur(18px);
}

.hero-card {
  position: relative;
  overflow: hidden;
  border-radius: 28px;
  padding: 26px 22px;
  margin-bottom: 16px;
  background:
    radial-gradient(circle at 88% 12%, rgba(56, 189, 248, 0.24), transparent 28%),
    linear-gradient(135deg, rgba(240, 253, 250, 0.98), rgba(255, 255, 255, 0.86));

  &::after {
    content: "";
    position: absolute;
    right: -44px;
    bottom: -54px;
    width: 150px;
    height: 150px;
    border-radius: 48px;
    background: linear-gradient(135deg, rgba(20, 184, 166, 0.16), rgba(56, 189, 248, 0.08));
    transform: rotate(16deg);
  }

  h1 {
    position: relative;
    z-index: 1;
    margin: 8px 0 10px;
    font-size: 27px;
    line-height: 1.18;
    letter-spacing: -0.8px;
    color: #0f172a;
  }

  p {
    position: relative;
    z-index: 1;
    margin: 0;
    color: #475569;
    line-height: 1.65;
  }
}

.hero-kicker,
.section-heading span {
  color: #0f766e;
  font-size: 12px;
  font-weight: 800;
  letter-spacing: 0.08em;
}

.hero-actions {
  position: relative;
  z-index: 1;
  display: flex;
  gap: 10px;
  margin-top: 18px;
}

.primary-action,
.ghost-action,
.wide-action,
.text-link,
.legacy-links button,
.quick-card {
  border: none;
  cursor: pointer;
  font-family: inherit;
}

.primary-action,
.wide-action {
  background: linear-gradient(135deg, #0f766e 0%, #0ea5e9 100%);
  color: #fff;
  box-shadow: 0 16px 28px rgba(14, 165, 233, 0.22);
}

.primary-action,
.ghost-action {
  border-radius: 999px;
  padding: 11px 14px;
  font-size: 14px;
  font-weight: 800;
}

.ghost-action {
  border: 1px solid rgba(15, 118, 110, 0.16);
  background: rgba(255, 255, 255, 0.7);
  color: #0f766e;
}

.quick-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 12px;
  margin-bottom: 16px;
}

.quick-card {
  display: grid;
  grid-template-columns: 42px 1fr;
  column-gap: 12px;
  text-align: left;
  border-radius: 22px;
  padding: 16px;
  color: #102a43;

  strong {
    font-size: 16px;
    color: #0f172a;
  }

  p {
    grid-column: 2;
    margin: 4px 0 0;
    color: #64748b;
    line-height: 1.55;
  }
}

.quick-icon {
  grid-row: span 2;
  width: 42px;
  height: 42px;
  border-radius: 16px;
  display: grid;
  place-items: center;
  color: #fff;
  font-weight: 900;
}

.quick-icon.result {
  background: linear-gradient(135deg, #10b981, #14b8a6);
}

.quick-icon.onboard {
  background: linear-gradient(135deg, #38bdf8, #0ea5e9);
}

.quick-icon.workbench {
  background: linear-gradient(135deg, #f59e0b, #f97316);
}

.card {
  border-radius: 24px;
  padding: 18px;
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
    font-size: 19px;
    color: #0f172a;
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
  color: #64748b;
  line-height: 1.6;
}

.text-link {
  background: transparent;
  color: #0f766e;
  font-weight: 800;
  padding: 4px 0;
}

.state-row {
  display: flex;
  gap: 12px;
  padding: 14px;
  border-radius: 18px;
  background: rgba(240, 253, 250, 0.8);

  strong {
    color: #0f172a;
  }
}

.state-row.warning {
  background: rgba(255, 247, 237, 0.9);
}

.state-dot {
  flex: 0 0 auto;
  width: 12px;
  height: 12px;
  margin-top: 4px;
  border-radius: 999px;
  background: #10b981;
  box-shadow: 0 0 0 6px rgba(16, 185, 129, 0.12);
}

.state-dot.loading {
  background: #0ea5e9;
  box-shadow: 0 0 0 6px rgba(14, 165, 233, 0.12);
}

.state-dot.warning {
  background: #f97316;
  box-shadow: 0 0 0 6px rgba(249, 115, 22, 0.12);
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
  border-radius: 16px;
  background: rgba(248, 250, 252, 0.9);

  span {
    color: #64748b;
  }

  strong {
    color: #0f766e;
  }
}

.wide-action {
  width: 100%;
  margin-top: 14px;
  border-radius: 18px;
  padding: 13px 16px;
  font-size: 15px;
  font-weight: 800;
}

.legacy-card {
  background: rgba(255, 255, 255, 0.66);
}

.legacy-links {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
  margin-top: 14px;

  button {
    border: 1px solid rgba(15, 118, 110, 0.12);
    border-radius: 16px;
    background: rgba(240, 253, 250, 0.72);
    color: #0f766e;
    padding: 11px;
    font-weight: 800;
  }
}

@media (min-width: 768px) {
  .home-page {
    max-width: 520px;
    margin: 0 auto;
  }
}
</style>
