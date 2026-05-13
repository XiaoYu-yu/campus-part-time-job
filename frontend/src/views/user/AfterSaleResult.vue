<template>
  <UserLayout>
    <div class="after-sale-page">
      <section class="card">
        <div class="section-header">
          <div>
            <h2>售后处理结果</h2>
            <p>输入代送订单号，查看这单的售后处理进度和结果。</p>
          </div>
        </div>

        <div class="search-row">
          <el-input v-model="orderId" placeholder="请输入校园代送订单号，例如 CR202604060001" clearable @keyup.enter="loadResult" />
          <el-button type="primary" @click="loadResult">查询</el-button>
        </div>
      </section>

      <section v-if="result" class="card result-card">
        <div class="result-header">
          <div>
            <p class="label">订单号</p>
            <h3>{{ result.relayOrderId }}</h3>
          </div>
          <div class="receipt-pill" :class="receiptClass(result.customerReceiptStatus)">
            {{ result.customerReceiptTitle }}
          </div>
        </div>

        <div class="message-box">
          {{ result.customerReceiptMessage }}
        </div>

        <div class="detail-grid">
          <div class="detail-item">
            <span>订单状态</span>
            <strong>{{ result.orderStatus }}</strong>
          </div>
          <div class="detail-item">
            <span>售后申请时间</span>
            <strong>{{ formatDateTime(result.afterSaleAppliedAt) }}</strong>
          </div>
          <div class="detail-item">
            <span>处理方式</span>
            <strong>{{ result.afterSaleHandleAction || '处理中' }}</strong>
          </div>
          <div class="detail-item">
            <span>处理类型</span>
            <strong>{{ result.decisionType || '待记录' }}</strong>
          </div>
          <div class="detail-item">
            <span>涉及金额</span>
            <strong>{{ formatDecisionAmount(result.decisionAmount) }}</strong>
          </div>
          <div class="detail-item">
            <span>执行状态</span>
            <strong>{{ result.afterSaleExecutionStatus || '待处理' }}</strong>
          </div>
          <div class="detail-item">
            <span>最近更新时间</span>
            <strong>{{ formatDateTime(result.lastUpdatedAt) }}</strong>
          </div>
        </div>

        <div class="remark-block">
          <p class="label">售后原因</p>
          <div>{{ result.afterSaleReason || '无' }}</div>
        </div>

        <div class="remark-block">
          <p class="label">处理说明</p>
          <div>{{ result.afterSaleHandleRemark || '暂无处理说明' }}</div>
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
import { getCampusCustomerAfterSaleResult } from '../../api/campus-customer'

const route = useRoute()
const router = useRouter()

const orderId = ref(route.query.orderId || '')
const result = ref(null)

const loadResult = async () => {
  if (!orderId.value?.trim()) {
    ElMessage.warning('请先输入校园代送订单号')
    return
  }
  const normalizedOrderId = orderId.value.trim()
  result.value = await getCampusCustomerAfterSaleResult(normalizedOrderId)
  router.replace({
    path: route.path,
    query: { orderId: normalizedOrderId }
  })
}

const formatDecisionAmount = (value) => {
  if (value === null || value === undefined || value === '') {
    return '无金额型处理'
  }
  return `¥${Number(value).toFixed(2)}`
}

const formatDateTime = (value) => value || '暂无'

const receiptClass = (status) => ({
  PROCESSING: 'processing',
  COMPLETED: 'completed',
  REJECTED: 'rejected',
  NEED_MANUAL_FOLLOW_UP: 'warning'
}[status] || 'processing')

onMounted(() => {
  if (orderId.value) {
    loadResult()
  }
})
</script>

<style scoped lang="scss">
.after-sale-page {
  padding: 16px;
  overflow-x: hidden;
}

.card {
  background: #ffffff;
  border-radius: 14px;
  padding: 16px;
  margin-bottom: 14px;
  border: 1px solid #e4e4e7;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05), 0 4px 12px rgba(0, 0, 0, 0.03);
}

.section-header h2 {
  margin: 0 0 6px;
  font-size: 18px;
  color: #18181b;
  font-weight: 700;
}

.section-header p {
  margin: 0;
  color: #71717a;
  font-size: 14px;
  line-height: 1.5;
}

.search-row {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 12px;
  margin-top: 16px;

  :deep(.el-input__wrapper) {
    min-height: 44px;
    border-radius: 10px;
  }

  :deep(.el-button) {
    min-height: 44px;
    border-radius: 10px;
  }
}

.result-card {
  border-radius: 14px;
}

.result-header {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.result-header h3 {
  margin: 4px 0 0;
  font-size: 16px;
  color: #18181b;
  font-weight: 700;
  word-break: break-all;
}

.receipt-pill {
  padding: 6px 14px;
  border-radius: 999px;
  font-weight: 600;
  font-size: 13px;
  white-space: nowrap;
  flex-shrink: 0;
}

.receipt-pill.processing {
  background: #ecf5ff;
  color: #409eff;
}

.receipt-pill.completed {
  background: #f0f9eb;
  color: #67c23a;
}

.receipt-pill.rejected {
  background: #fef0f0;
  color: #f56c6c;
}

.receipt-pill.warning {
  background: #fff7e6;
  color: #e6a23c;
}

.message-box {
  background: #fffbeb;
  color: #8a5a00;
  border-radius: 12px;
  padding: 14px;
  margin-bottom: 16px;
  line-height: 1.6;
  font-size: 14px;
  border: 1px solid #fde68a;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 10px;
}

.detail-item {
  background: #fafafa;
  border-radius: 12px;
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 6px;
  border: 1px solid #e4e4e7;

  strong {
    color: #18181b;
    font-size: 14px;
    word-break: break-all;
  }
}

.detail-item span,
.label {
  color: #71717a;
  font-size: 13px;
}

.remark-block {
  margin-top: 16px;
  padding: 14px;
  border-radius: 12px;
  background: #fafafa;
  border: 1px solid #e4e4e7;

  .label {
    display: block;
    margin-bottom: 6px;
  }

  div {
    color: #3f3f46;
    font-size: 14px;
    line-height: 1.6;
    word-break: break-all;
  }
}

@media (max-width: 480px) {
  .search-row {
    grid-template-columns: 1fr;
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }

  .result-header {
    flex-direction: column;
    align-items: flex-start;
  }
}

@media (max-width: 360px) {
  .after-sale-page {
    padding: 12px;
  }

  .card {
    padding: 14px 12px;
    border-radius: 12px;
  }

  .section-header h2 {
    font-size: 16px;
  }

  .receipt-pill {
    font-size: 12px;
    padding: 5px 10px;
  }
}
</style>
