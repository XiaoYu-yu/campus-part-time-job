<template>
  <UserLayout>
    <div class="order-result-page">
      <section class="card hero-card">
        <div class="section-header">
          <div>
            <div class="title-row">
              <h2>校园代送结果回看</h2>
              <span class="readonly-badge">只读回看</span>
            </div>
            <p>输入校园代送订单号，查看确认送达前后的当前结果，不新增写操作。</p>
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
            <p>支持从 URL query 自动带入，也可手工输入。</p>
          </div>
          <div class="guide-item">
            <span>2</span>
            <strong>查看确认状态</strong>
            <p>重点区分等待确认与已完成两种演示状态。</p>
          </div>
          <div class="guide-item">
            <span>3</span>
            <strong>核对结果字段</strong>
            <p>只读展示现有 customer 订单详情可返回字段。</p>
          </div>
        </div>
      </section>

      <section v-if="showInitialHint" class="card state-card">
        <div class="state-icon">ID</div>
        <div>
          <h3>等待输入订单号</h3>
          <p>输入校园代送订单号后，可回看等待确认或已完成状态下的当前结果。</p>
          <p class="sub-tip">示例：`CR202604070002`，也可通过 `?orderId=` 直接打开结果回看。</p>
        </div>
      </section>

      <section v-if="loading" class="card state-card">
        <div class="state-icon loading-state">...</div>
        <div>
          <h3>正在读取结果</h3>
          <p>正在查询订单 {{ queryingOrderId || orderId || '...' }} 的当前结果，请稍候。</p>
          <p class="sub-tip">本页只调用 customer 订单详情读取接口，不会触发确认或写操作。</p>
        </div>
      </section>

      <section v-if="errorMessage" class="card state-card error-state">
        <div class="state-icon error-icon">!</div>
        <div>
          <h3>结果读取失败</h3>
          <p>{{ errorMessage }}</p>
          <p class="sub-tip">请检查订单号是否正确、当前账号是否有权限查看该订单，或稍后重试。</p>
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
              <span>customer 备注</span>
              <strong>{{ result.remark || '暂无' }}</strong>
            </div>
          </div>
        </div>

        <div class="section-block">
          <div class="section-block__header">
            <h4>异常信息摘要</h4>
            <span>{{ result.exceptionType ? '存在异常记录' : '暂无异常记录' }}</span>
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

        <div class="section-block">
          <div class="section-block__header">
            <h4>时间信息</h4>
            <span>按现有接口可读字段展示</span>
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
import { getCampusCustomerOrderDetail } from '../../api/campus-customer'

const route = useRoute()
const router = useRouter()

const orderId = ref(route.query.orderId || '')
const result = ref(null)
const loading = ref(false)
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
    errorMessage.value = error?.response?.data?.msg || error?.message || '读取校园代送结果失败'
  } finally {
    loading.value = false
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
    return '配送员已送达，当前正在等待你确认送达结果。确认前这里会持续展示最新的送达结果。'
  }
  if (order.status === 'COMPLETED') {
    return '订单已完成。这里展示的是确认送达后的最终结果回看，可直接用于核对本次配送结果。'
  }
  return '当前订单仍在处理中，这里展示的是现有接口可读取到的最新结果。'
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
  padding: 16px;
}

.card {
  background: rgba(255, 255, 255, 0.88);
  border: 1px solid rgba(15, 118, 110, 0.1);
  border-radius: 22px;
  padding: 18px;
  margin-bottom: 16px;
  box-shadow: 0 16px 36px rgba(15, 118, 110, 0.08);
  backdrop-filter: blur(18px);
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
  background: rgba(254, 242, 242, 0.88);
}

.sub-tip {
  margin-top: 8px !important;
  color: #94a3b8 !important;
  font-size: 13px;
}

.state-icon {
  width: 44px;
  height: 44px;
  border-radius: 16px;
  background: rgba(240, 253, 250, 0.86);
  color: #0f766e;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  font-weight: 800;
  font-size: 16px;
}

.state-icon.loading-state {
  background: rgba(241, 245, 249, 0.86);
  color: #64748b;
}

.state-icon.error-icon {
  background: rgba(254, 226, 226, 0.86);
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
    border-radius: 14px;
    font-weight: 700;
  }
}

.guide-strip {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-top: 14px;
}

.guide-item {
  background: rgba(248, 250, 252, 0.82);
  border-radius: 14px;
  padding: 14px;
}

.guide-item span {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border-radius: 10px;
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
  border-radius: 14px;
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
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 12px;
  margin-bottom: 16px;
}

.highlight-item {
  background: linear-gradient(180deg, rgba(240, 253, 250, 0.86) 0%, rgba(236, 254, 255, 0.78) 100%);
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
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 12px;
}

.detail-item {
  background: rgba(248, 250, 252, 0.82);
  border-radius: 14px;
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

@media (max-width: 640px) {
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
</style>
