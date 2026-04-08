<template>
  <MainLayout>
    <div class="campus-admin-page">
      <div class="page-header">
        <div>
          <h2>校园结算批次详情</h2>
          <p>只读查看批次汇总与 settlement 明细，不提供编辑写入。</p>
        </div>
        <el-button @click="router.push('/campus/settlement-batches')">返回列表</el-button>
      </div>

      <section class="summary-card" v-loading="loading">
        <template v-if="detail">
          <div class="summary-top">
            <div class="summary-title">
              <span class="label">批次号</span>
              <h3>{{ detail.payoutBatchNo }}</h3>
            </div>
            <div class="summary-amount">
              <span class="label">待结算金额合计</span>
              <strong>{{ formatAmount(detail.totalPendingAmount) }}</strong>
            </div>
          </div>

          <div class="summary-grid">
            <div class="summary-item">
              <span>总条数</span>
              <strong>{{ detail.totalCount || 0 }}</strong>
            </div>
            <div class="summary-item">
              <span>已打款</span>
              <strong>{{ detail.paidCount || 0 }}</strong>
            </div>
            <div class="summary-item">
              <span>失败数</span>
              <strong>{{ detail.failedCount || 0 }}</strong>
            </div>
            <div class="summary-item">
              <span>已核对</span>
              <strong>{{ detail.verifiedCount || 0 }}</strong>
            </div>
            <div class="summary-item">
              <span>未核对</span>
              <strong>{{ detail.unverifiedCount || 0 }}</strong>
            </div>
            <div class="summary-item">
              <span>首次记录</span>
              <strong>{{ formatDateTime(detail.firstRecordedAt) }}</strong>
            </div>
            <div class="summary-item">
              <span>最近记录</span>
              <strong>{{ formatDateTime(detail.lastRecordedAt) }}</strong>
            </div>
          </div>
        </template>
      </section>

      <section class="table-card">
        <el-table v-loading="loading" :data="detail?.records || []" border>
          <el-table-column prop="id" label="结算ID" width="100" />
          <el-table-column prop="relayOrderId" label="订单号" min-width="200" />
          <el-table-column prop="courierProfileId" label="配送员ID" width="110" align="center" />
          <el-table-column label="待结算金额" width="130" align="right">
            <template #default="{ row }">
              {{ formatAmount(row.pendingAmount) }}
            </template>
          </el-table-column>
          <el-table-column label="打款状态" width="110" align="center">
            <template #default="{ row }">
              <el-tag :type="payoutTagType(row.payoutStatus)">{{ row.payoutStatus || 'UNPAID' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="已核对" width="90" align="center">
            <template #default="{ row }">
              <el-tag :type="row.payoutVerified === 1 ? 'success' : 'info'">
                {{ row.payoutVerified === 1 ? '是' : '否' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="payoutVerifyRemark" label="核对备注" min-width="180" show-overflow-tooltip />
          <el-table-column label="打款记录时间" min-width="170">
            <template #default="{ row }">
              {{ formatDateTime(row.payoutRecordedAt) }}
            </template>
          </el-table-column>
          <el-table-column label="核对时间" min-width="170">
            <template #default="{ row }">
              {{ formatDateTime(row.payoutVerifiedAt) }}
            </template>
          </el-table-column>
        </el-table>
      </section>
    </div>
  </MainLayout>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import MainLayout from '../layout/MainLayout.vue'
import { getCampusSettlementPayoutBatchDetail } from '../api/campus-admin'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const detail = ref(null)

const formatAmount = (value) => `¥${Number(value || 0).toFixed(2)}`
const formatDateTime = (value) => value || '暂无'

const payoutTagType = (status) => ({
  PAID: 'success',
  FAILED: 'danger',
  UNPAID: 'warning'
}[status] || 'info')

const loadDetail = async () => {
  loading.value = true
  try {
    detail.value = await getCampusSettlementPayoutBatchDetail(route.params.batchNo)
  } finally {
    loading.value = false
  }
}

onMounted(() => loadDetail())
</script>

<style scoped lang="scss">
.campus-admin-page {
  padding: 20px;
}

.page-header,
.summary-card,
.table-card {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: 0 6px 20px rgba(15, 23, 42, 0.05);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;

  h2 {
    margin: 0 0 8px;
    font-size: 24px;
    color: #18181b;
  }

  p {
    margin: 0;
    color: #71717a;
  }
}

.summary-top {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 20px;
}

.summary-title,
.summary-amount {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.summary-title h3,
.summary-amount strong {
  margin: 0;
  font-size: 24px;
  color: #18181b;
}

.label {
  color: #71717a;
  font-size: 13px;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  gap: 12px;
}

.summary-item {
  background: #f8fafc;
  border-radius: 12px;
  padding: 14px;
  display: flex;
  flex-direction: column;
  gap: 8px;

  span {
    color: #71717a;
    font-size: 13px;
  }

  strong {
    color: #18181b;
  }
}

@media (max-width: 768px) {
  .page-header,
  .summary-top {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
