<template>
  <UserLayout>
    <div class="order-result-page">
      <section class="card hero-card">
        <div class="section-header">
          <div>
            <div class="title-row">
              <h2>代送单结果</h2>
              <span class="readonly-badge">订单结果</span>
            </div>
            <p>输入订单号就能看进度。配送员送达后，你也可以在这里确认收货。</p>
          </div>
        </div>

        <div class="search-row">
          <el-input
            v-model="orderId"
            placeholder="请输入校园代送订单号，例如 CR202604070002"
            clearable
            @keyup.enter="loadOrderResult"
          />
          <el-button type="primary" :loading="loading" @click="loadOrderResult">查询结果</el-button>
        </div>

        <div class="guide-strip">
          <div class="guide-item">
            <span>1</span>
            <strong>输入订单号</strong>
            <p>从订单列表跳转会自动带入，也可以手动输入。</p>
          </div>
          <div class="guide-item">
            <span>2</span>
            <strong>查看确认状态</strong>
            <p>看清楚现在是等你确认，还是已经完成。</p>
          </div>
          <div class="guide-item">
            <span>3</span>
            <strong>查看配送信息</strong>
            <p>取餐点、送达位置、金额和备注都会列出来。</p>
          </div>
        </div>
      </section>

      <section v-if="showInitialHint" class="card state-card hint-state">
        <div class="state-icon">ID</div>
        <div>
          <h3>等待输入订单号</h3>
          <p>输入订单号后，可以查看配送进度，也能确认已经送到的订单。</p>
          <p class="sub-tip">示例：CR202604070002。也可以从订单列表直接点进来。</p>
        </div>
      </section>

      <section v-if="loading" class="card state-card">
        <div class="state-icon loading-state"><span class="spinner-ring"></span></div>
        <div>
          <h3>正在查询订单</h3>
          <p>正在查看 {{ queryingOrderId || orderId || '...' }} 的最新进度，请稍等。</p>
          <p class="sub-tip">放心，查询不会自动确认收货，只有你点确认按钮才会提交。</p>
        </div>
      </section>

      <section v-if="errorMessage" class="card state-card error-state">
        <div class="state-icon error-icon">!</div>
        <div>
          <h3>没查到订单</h3>
          <p>{{ errorMessage }}</p>
          <p class="sub-tip">检查一下订单号和登录账号是否正确，或者稍后再试。</p>
        </div>
      </section>

      <section v-if="result" class="card result-card">
        <div class="result-header">
          <div>
            <p class="label">订单号</p>
            <h3>{{ result.id }}</h3>
          </div>
          <div class="status-pill" :class="statusClass(result.status)">
            {{ statusTitle(result.status) }}
          </div>
        </div>

        <div class="message-box" :class="statusClass(result.status)">
          <strong>{{ statusTitle(result.status) }}</strong>
          <span>{{ statusMessage(result) }}</span>
        </div>

        <div v-if="result.status === 'AWAITING_CONFIRMATION'" class="confirm-section">
          <div class="confirm-card">
            <div class="confirm-icon">
              ✓
              <span class="pulse-ring-sm"></span>
            </div>
            <div class="confirm-body">
              <strong>确认已经收到</strong>
              <p>确认后这单会变成已完成，页面会自动刷新最新结果。</p>
            </div>
            <el-button
              type="primary"
              class="confirm-button"
              :loading="confirming"
              @click="confirmOrderResult"
            >
              确认已收到
            </el-button>
          </div>
        </div>

        <div class="summary-grid">
          <div class="detail-item highlight-item">
            <span>当前状态</span>
            <strong>{{ statusTitle(result.status) }}</strong>
          </div>
          <div class="detail-item highlight-item">
            <span>送达时间</span>
            <strong>{{ formatDateTime(result.deliveredAt) }}</strong>
          </div>
          <div class="detail-item highlight-item">
            <span>完成时间</span>
            <strong>{{ formatDateTime(result.autoCompleteAt) }}</strong>
          </div>
          <div class="detail-item highlight-item">
            <span>最近更新时间</span>
            <strong>{{ formatDateTime(result.updatedAt) }}</strong>
          </div>
        </div>

        <div class="section-block">
          <div class="section-block__header">
            <h4>配送结果摘要</h4>
            <span>{{ resultSummaryTitle(result.status) }}</span>
          </div>
          <div class="detail-grid">
            <div class="detail-item">
              <span>当前状态</span>
              <strong>{{ result.status || '暂无' }}</strong>
            </div>
            <div class="detail-item">
              <span>取餐点</span>
              <strong>{{ result.pickupPointName || '暂无' }}</strong>
            </div>
            <div class="detail-item">
              <span>配送楼栋</span>
              <strong>{{ result.deliveryBuilding || '暂无' }}</strong>
            </div>
            <div class="detail-item">
              <span>配送详情</span>
              <strong>{{ result.deliveryDetail || '暂无' }}</strong>
            </div>
            <div class="detail-item">
              <span>订单金额</span>
              <strong>{{ formatAmount(result.totalAmount) }}</strong>
            </div>
            <div class="detail-item">
            <span>下单备注</span>
              <strong>{{ result.remark || '暂无' }}</strong>
            </div>
          </div>
        </div>

        <div class="section-block">
          <div class="section-block__header">
            <h4>异常信息摘要</h4>
          <span>{{ result.exceptionType ? '有异常记录' : '没有异常记录' }}</span>
          </div>
          <div class="detail-grid">
            <div class="detail-item">
              <span>异常类型</span>
              <strong>{{ result.exceptionType || '暂无' }}</strong>
            </div>
            <div class="detail-item">
              <span>异常说明</span>
              <strong>{{ result.exceptionRemark || '暂无' }}</strong>
            </div>
          </div>
        </div>

        <div v-if="result.exceptionType" class="exception-notice">
          <strong>这单有异常记录</strong>
          <p>可以留意后续售后处理结果。有疑问的话，联系平台处理就行。</p>
        </div>

        <div class="section-block">
          <div class="section-block__header">
            <h4>时间信息</h4>
            <span>配送过程中的关键时间</span>
          </div>
          <div class="detail-grid">
            <div class="detail-item">
              <span>送达时间</span>
              <strong>{{ formatDateTime(result.deliveredAt) }}</strong>
            </div>
            <div class="detail-item">
              <span>完成时间</span>
              <strong>{{ formatDateTime(result.autoCompleteAt) }}</strong>
            </div>
            <div class="detail-item">
              <span>最近更新时间</span>
              <strong>{{ formatDateTime(result.updatedAt) }}</strong>
            </div>
          </div>
        </div>
      </section>
    </div>
  </UserLayout>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import UserLayout from '../../layout/UserLayout.vue'
import { confirmCampusCustomerOrder, getCampusCustomerOrderDetail } from '../../api/campus-customer'

const route = useRoute()
const router = useRouter()

const orderId = ref(route.query.orderId || '')
const result = ref(null)
const loading = ref(false)
const confirming = ref(false)
const errorMessage = ref('')
const queryingOrderId = ref('')
const showInitialHint = computed(() => !loading.value && !result.value && !errorMessage.value && !orderId.value?.trim())

const loadOrderResult = async () => {
  const normalizedOrderId = orderId.value?.trim()
  if (!normalizedOrderId) {
    result.value = null
    errorMessage.value = ''
    queryingOrderId.value = ''
    router.replace({
      path: route.path,
      query: {}
    })
    ElMessage.warning('请先输入校园代送订单号')
    return
  }
  loading.value = true
  result.value = null
  errorMessage.value = ''
  queryingOrderId.value = normalizedOrderId
  try {
    result.value = await getCampusCustomerOrderDetail(normalizedOrderId)
    router.replace({
      path: route.path,
      query: { orderId: normalizedOrderId }
    })
  } catch (error) {
    result.value = null
    errorMessage.value = error?.response?.data?.msg || error?.message || '订单查询失败，请稍后再试'
  } finally {
    loading.value = false
  }
}

const confirmOrderResult = async () => {
  const targetOrderId = result.value?.id || orderId.value?.trim()
  if (!targetOrderId) {
    ElMessage.warning('请先查询订单后再确认')
    return
  }
  confirming.value = true
  try {
    await confirmCampusCustomerOrder(targetOrderId)
    ElMessage.success('已确认收货')
    orderId.value = targetOrderId
    await loadOrderResult()
  } catch (error) {
    ElMessage.error(error?.response?.data?.msg || error?.message || '确认失败，请稍后再试')
  } finally {
    confirming.value = false
  }
}

const formatAmount = (value) => {
  if (value === null || value === undefined || value === '') {
    return '暂无'
  }
  return `¥${Number(value).toFixed(2)}`
}

const formatDateTime = (value) => value || '暂无'

const statusTitle = (status) => ({
  AWAITING_CONFIRMATION: '等待确认',
  COMPLETED: '已完成',
  DELIVERING: '配送中',
  PICKED_UP: '已取餐',
  ACCEPTED: '已接单'
}[status] || '处理中')

const statusClass = (status) => ({
  AWAITING_CONFIRMATION: 'processing',
  COMPLETED: 'completed',
  DELIVERING: 'delivery',
  PICKED_UP: 'picked-up',
  ACCEPTED: 'accepted'
}[status] || 'processing')

const statusMessage = (order) => {
  if (order.status === 'AWAITING_CONFIRMATION') {
    return '配送员已经送到，等你确认收货。确认前，这里会一直显示最新进度。'
  }
  if (order.status === 'COMPLETED') {
    return '订单已完成。你可以在这里核对送达时间、金额和备注。'
  }
  return '这单还在处理中，页面会展示目前能查到的最新进度。'
}

const resultSummaryTitle = (status) => {
  if (status === 'AWAITING_CONFIRMATION') {
    return '等待确认中'
  }
  if (status === 'COMPLETED') {
    return '确认完成'
  }
  return '处理中'
}


onMounted(() => {
  if (orderId.value) {
    loadOrderResult()
  }
})
</script>

<style scoped lang="scss">
.order-result-page {
  padding: 14px 14px 24px;
  overflow-x: hidden;
}

.card {
  background: #ffffff;
  border: 1px solid #e4e4e7;
  border-radius: 14px;
  padding: 16px;
  margin-bottom: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}

.hero-card {
  position: relative;
  overflow: hidden;
  border-radius: 16px;
  background: linear-gradient(135deg, #eefdfa, #f0fdfa);

  > * {
    position: relative;
    z-index: 1;
  }
}

.state-card {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  text-align: left;
}

.state-card h3 {
  margin: 0 0 8px;
  color: #0f172a;
}

.state-card p {
  margin: 0;
  line-height: 1.7;
  color: #64748b;
}

.error-state {
  border-color: rgba(239, 68, 68, 0.18);
  background: #fef2f2;
}

.sub-tip {
  margin-top: 8px !important;
  color: #94a3b8 !important;
  font-size: 13px;
}

.state-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: #f0fdfa;
  color: #0f766e;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  font-weight: 800;
  font-size: 16px;
}

.state-icon.loading-state {
  background: #f1f5f9;
  color: #64748b;
}

.spinner-ring {
  display: inline-block;
  width: 18px;
  height: 18px;
  border: 2.5px solid rgba(100, 116, 139, 0.2);
  border-top-color: #64748b;
  border-radius: 50%;
  animation: campus-spin 0.7s linear infinite;
}

@keyframes campus-spin {
  to { transform: rotate(360deg); }
}

.hint-state {
  background: #f0fdfa;
  border-color: rgba(20, 184, 166, 0.12);
}

.result-header__right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.status-hero-icon {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  font-size: 20px;
  font-weight: 900;
  flex-shrink: 0;
}

.completed-icon {
  background: linear-gradient(135deg, #10b981, #059669);
  color: #fff;
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.18);
}

.waiting-icon {
  background: linear-gradient(135deg, #0ea5e9, #0284c7);
  color: #fff;
  box-shadow: 0 4px 12px rgba(14, 165, 233, 0.18);
  position: relative;
}

.pulse-ring {
  position: absolute;
  inset: -4px;
  border-radius: 50%;
  border: 2px solid rgba(14, 165, 233, 0.25);
  animation: campus-pulse 2s ease-in-out infinite;
}

@keyframes campus-pulse {
  0%, 100% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.12); opacity: 0.4; }
}

.exception-notice {
  margin-top: 16px;
  border-radius: 12px;
  background: #fef9ee;
  border: 1px solid rgba(245, 158, 11, 0.15);
  padding: 14px 16px;

  strong {
    display: block;
    color: #92400e;
    font-size: 13px;
    font-weight: 700;
    margin-bottom: 4px;
  }

  p {
    margin: 0;
    color: #a16207;
    font-size: 13px;
    line-height: 1.6;
  }
}

.state-icon.error-icon {
  background: #fee2e2;
  color: #ef4444;
}

.title-row {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.section-header h2 {
  margin: 0 0 6px;
  color: #0f172a;
}

.section-header p {
  margin: 0;
  color: #64748b;
  font-size: 14px;
}

.readonly-badge {
  display: inline-flex;
  align-items: center;
  border-radius: 999px;
  padding: 5px 12px;
  background: rgba(16, 185, 129, 0.1);
  color: #059669;
  font-size: 12px;
  font-weight: 700;
}

.search-row {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 12px;
  margin-top: 16px;

  .el-button--primary {
    border-radius: 10px;
    font-weight: 700;
    min-height: 44px;
  }
}

.search-row :deep(.el-input__wrapper) {
  min-height: 44px;
  border-radius: 10px;
}

.guide-strip {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-top: 14px;
}

.guide-item {
  background: #f8fafc;
  border-radius: 12px;
  padding: 14px;
}

.guide-item span {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border-radius: 8px;
  background: linear-gradient(135deg, #14b8a6, #38bdf8);
  color: #fff;
  font-size: 12px;
  font-weight: 800;
  margin-bottom: 8px;
}

.guide-item strong {
  display: block;
  margin-bottom: 6px;
  color: #0f172a;
}

.guide-item p {
  margin: 0;
  color: #64748b;
  font-size: 13px;
  line-height: 1.5;
}

.result-header {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
  margin-bottom: 16px;
}

.result-header h3 {
  margin: 4px 0 0;
}

.status-pill {
  padding: 8px 14px;
  border-radius: 999px;
  font-weight: 700;
  font-size: 13px;
}

.status-pill.processing {
  background: rgba(14, 165, 233, 0.1);
  color: #0284c7;
}

.status-pill.completed {
  background: rgba(16, 185, 129, 0.1);
  color: #059669;
}

.status-pill.delivery {
  background: rgba(20, 184, 166, 0.1);
  color: #0d9488;
}

.status-pill.picked-up,
.status-pill.accepted {
  background: rgba(245, 158, 11, 0.1);
  color: #d97706;
}

.message-box {
  border-radius: 12px;
  padding: 14px 16px;
  margin-bottom: 16px;
  line-height: 1.6;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.message-box.processing {
  background: rgba(14, 165, 233, 0.08);
  color: #0369a1;
}

.message-box.completed {
  background: rgba(16, 185, 129, 0.08);
  color: #047857;
}

.message-box.delivery {
  background: rgba(20, 184, 166, 0.08);
  color: #0f766e;
}

.message-box.picked-up,
.message-box.accepted {
  background: rgba(245, 158, 11, 0.08);
  color: #92400e;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 12px;
  margin-bottom: 16px;
}

.highlight-item {
  background: #f0fdfa;
  border: 1px solid rgba(15, 118, 110, 0.08);
}

.section-block {
  margin-top: 18px;
}

.section-block__header {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
  margin-bottom: 10px;
}

.section-block__header h4 {
  margin: 0;
  color: #0f172a;
}

.section-block__header span {
  color: #94a3b8;
  font-size: 12px;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 12px;
}

.detail-item {
  background: #f8fafc;
  border-radius: 10px;
  padding: 14px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.detail-item span,
.label {
  color: #94a3b8;
  font-size: 13px;
}

.timeline-section {
  margin-top: 18px;

  h4 {
    margin: 0 0 12px;
    color: #0f172a;
    font-size: 15px;
  }
}

.timeline {
  position: relative;
  padding-left: 28px;
}

.timeline-step {
  position: relative;
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding-bottom: 20px;

  &:last-child {
    padding-bottom: 0;
  }

  &:not(:last-child)::before {
    content: '';
    position: absolute;
    left: 0;
    top: 18px;
    bottom: 0;
    width: 2px;
    background: #e2e8f0;
  }

  &.done:not(:last-child)::before {
    background: linear-gradient(180deg, #14b8a6, #0f766e);
  }
}

.timeline-marker {
  position: absolute;
  left: -28px;
  top: 0;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  flex-shrink: 0;
}

.marker-check {
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: linear-gradient(135deg, #14b8a6, #0f766e);
  color: #fff;
  font-size: 10px;
  font-weight: 900;
  display: grid;
  place-items: center;
}

.marker-pulse {
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: linear-gradient(135deg, #0ea5e9, #0284c7);
  position: relative;

  &::after {
    content: '';
    position: absolute;
    inset: -3px;
    border-radius: 50%;
    border: 2px solid rgba(14, 165, 233, 0.25);
    animation: campus-pulse 2s ease-in-out infinite;
  }
}

.marker-empty {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #e2e8f0;
  border: 2px solid #f1f5f9;
}

.timeline-content {
  display: flex;
  flex-direction: column;
  gap: 2px;

  strong {
    color: #0f172a;
    font-size: 14px;
  }

  span {
    color: #94a3b8;
    font-size: 12px;
  }
}

.timeline-step.done .timeline-content strong {
  color: #0f766e;
}

.timeline-step.current .timeline-content strong {
  color: #0284c7;
}

.confirm-section {
  margin-top: 20px;
}

.confirm-card {
  display: flex;
  align-items: center;
  gap: 14px;
  background: linear-gradient(135deg, rgba(14, 165, 233, 0.06), rgba(20, 184, 166, 0.06));
  border: 1px solid rgba(14, 165, 233, 0.15);
  border-radius: 14px;
  padding: 16px;
  flex-wrap: wrap;
}

.confirm-icon {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: linear-gradient(135deg, #0ea5e9, #0284c7);
  color: #fff;
  font-size: 20px;
  display: grid;
  place-items: center;
  flex-shrink: 0;
  position: relative;
}

.pulse-ring-sm {
  position: absolute;
  inset: -3px;
  border-radius: 50%;
  border: 2px solid rgba(14, 165, 233, 0.2);
  animation: campus-pulse 2s ease-in-out infinite;
}

.confirm-body {
  flex: 1;
  min-width: 140px;

  strong {
    display: block;
    color: #0f172a;
    font-size: 15px;
    margin-bottom: 4px;
  }

  p {
    margin: 0;
    color: #64748b;
    font-size: 13px;
    line-height: 1.5;
  }
}

.confirm-button {
  min-height: 44px;
  border-radius: 10px;
  font-weight: 800;
  padding: 0 24px;
}

@media (max-width: 480px) {
  .search-row {
    grid-template-columns: 1fr;
  }

  .result-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .guide-strip {
    grid-template-columns: 1fr;
  }

  .state-card {
    flex-direction: column;
  }
}

@media (max-width: 360px) {
  .summary-grid,
  .detail-grid,
  .guide-strip {
    grid-template-columns: 1fr;
  }
}
</style>
