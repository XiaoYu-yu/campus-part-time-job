<template>
  <UserLayout>
    <div class="order-result-page">
      <section class="card">
        <div class="section-header">
          <div>
            <h2>校园代送结果回看</h2>
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
          <el-button type="primary" @click="loadOrderResult">查询结果</el-button>
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
          {{ statusMessage(result) }}
        </div>

        <div class="detail-grid">
          <div class="detail-item">
            <span>当前状态</span>
            <strong>{{ result.status || '暂无' }}</strong>
          </div>
          <div class="detail-item">
            <span>送达时间</span>
            <strong>{{ formatDateTime(result.deliveredAt) }}</strong>
          </div>
          <div class="detail-item">
            <span>完成时间</span>
            <strong>{{ formatDateTime(result.autoCompleteAt) }}</strong>
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
          <div class="detail-item">
            <span>异常类型</span>
            <strong>{{ result.exceptionType || '暂无' }}</strong>
          </div>
          <div class="detail-item">
            <span>异常说明</span>
            <strong>{{ result.exceptionRemark || '暂无' }}</strong>
          </div>
          <div class="detail-item">
            <span>最近更新时间</span>
            <strong>{{ formatDateTime(result.updatedAt) }}</strong>
          </div>
        </div>
      </section>
    </div>
  </UserLayout>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import UserLayout from '../../layout/UserLayout.vue'
import { getCampusCustomerOrderDetail } from '../../api/campus-customer'

const route = useRoute()
const router = useRouter()

const orderId = ref(route.query.orderId || '')
const result = ref(null)

const loadOrderResult = async () => {
  if (!orderId.value?.trim()) {
    ElMessage.warning('请先输入校园代送订单号')
    return
  }
  const normalizedOrderId = orderId.value.trim()
  result.value = await getCampusCustomerOrderDetail(normalizedOrderId)
  router.replace({
    path: route.path,
    query: { orderId: normalizedOrderId }
  })
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
    return '配送员已送达，当前正在等待你确认送达结果。'
  }
  if (order.status === 'COMPLETED') {
    return '订单已完成。这里展示的是确认送达后的最小结果回看。'
  }
  return '当前订单仍在处理中，这里展示的是现有接口可读取到的最新结果。'
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
  background: white;
  border-radius: 16px;
  padding: 16px;
  margin-bottom: 14px;
}

.section-header h2 {
  margin: 0 0 6px;
}

.section-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.search-row {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 12px;
  margin-top: 16px;
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
  font-weight: 600;
}

.status-pill.processing {
  background: #ecf5ff;
  color: #409eff;
}

.status-pill.completed {
  background: #f0f9eb;
  color: #67c23a;
}

.status-pill.delivery {
  background: #eef5ff;
  color: #337ecc;
}

.status-pill.picked-up,
.status-pill.accepted {
  background: #fff7e6;
  color: #e6a23c;
}

.message-box {
  border-radius: 12px;
  padding: 14px;
  margin-bottom: 16px;
  line-height: 1.6;
}

.message-box.processing {
  background: #ecf5ff;
  color: #1d4ed8;
}

.message-box.completed {
  background: #f0f9eb;
  color: #2f7d32;
}

.message-box.delivery {
  background: #eef5ff;
  color: #1e40af;
}

.message-box.picked-up,
.message-box.accepted {
  background: #fff7e6;
  color: #8a5a00;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 12px;
}

.detail-item {
  background: #f8fafc;
  border-radius: 12px;
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.detail-item span,
.label {
  color: #909399;
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
}
</style>
