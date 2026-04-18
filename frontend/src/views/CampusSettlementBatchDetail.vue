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
        <div class="section-heading">
          <div>
            <h3>批次结算明细</h3>
            <p>来源于当前批次下的 settlement 记录，继续只读展示单笔打款与核对摘要。</p>
          </div>
        </div>
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

      <section class="table-card">
        <div class="section-heading">
          <div>
            <h3>批次操作历史</h3>
            <p>只读展示 review / withdraw 操作审计，不改变打款状态、批次号或核对结果。</p>
          </div>
          <el-tag type="info" effect="plain">只读审计</el-tag>
        </div>

        <el-alert
          v-if="operationError"
          :title="operationError"
          type="error"
          show-icon
          :closable="false"
          class="section-alert"
        />

        <el-table
          v-loading="operationLoading"
          :data="operationRecords"
          border
          empty-text="暂无批次操作记录"
        >
          <el-table-column prop="id" label="记录ID" width="100" />
          <el-table-column label="操作类型" width="120" align="center">
            <template #default="{ row }">
              <el-tag :type="operationTypeTag(row.operationType)">
                {{ formatOperationType(row.operationType) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作结果" width="130" align="center">
            <template #default="{ row }">
              <el-tag :type="operationResultTag(row.operationResult)">
                {{ formatOperationResult(row.operationResult) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="operationRemark" label="操作备注" min-width="220" show-overflow-tooltip>
            <template #default="{ row }">
              {{ row.operationRemark || '暂无' }}
            </template>
          </el-table-column>
          <el-table-column prop="operatedByEmployeeId" label="操作员工ID" width="120" align="center">
            <template #default="{ row }">
              {{ row.operatedByEmployeeId || '暂无' }}
            </template>
          </el-table-column>
          <el-table-column label="操作时间" min-width="170">
            <template #default="{ row }">
              {{ formatDateTime(row.operatedAt) }}
            </template>
          </el-table-column>
        </el-table>

        <div class="operation-footer">
          <span>共 {{ operationTotal }} 条操作记录</span>
          <el-button text type="primary" :loading="operationLoading" @click="loadOperations">刷新操作历史</el-button>
        </div>
      </section>
    </div>
  </MainLayout>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import MainLayout from '../layout/MainLayout.vue'
import {
  getCampusSettlementBatchOperations,
  getCampusSettlementPayoutBatchDetail
} from '../api/campus-admin'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const detail = ref(null)
const operationLoading = ref(false)
const operationRecords = ref([])
const operationTotal = ref(0)
const operationError = ref('')

const formatAmount = (value) => `¥${Number(value || 0).toFixed(2)}`
const formatDateTime = (value) => value || '暂无'

const payoutTagType = (status) => ({
  PAID: 'success',
  FAILED: 'danger',
  UNPAID: 'warning'
}[status] || 'info')

const operationTypeTag = (type) => ({
  REVIEW: 'success',
  WITHDRAW: 'warning'
}[type] || 'info')

const operationResultTag = (result) => ({
  PASSED: 'success',
  REJECTED: 'danger',
  REQUESTED: 'warning',
  RECORDED: 'info'
}[result] || 'info')

const formatOperationType = (type) => ({
  REVIEW: '批次复核',
  WITHDRAW: '撤回留痕'
}[type] || type || '暂无')

const formatOperationResult = (result) => ({
  PASSED: '通过',
  REJECTED: '驳回',
  REQUESTED: '已发起',
  RECORDED: '已记录'
}[result] || result || '暂无')

const loadDetail = async () => {
  loading.value = true
  try {
    detail.value = await getCampusSettlementPayoutBatchDetail(route.params.batchNo)
  } finally {
    loading.value = false
  }
}

const loadOperations = async () => {
  operationLoading.value = true
  operationError.value = ''
  try {
    const data = await getCampusSettlementBatchOperations(route.params.batchNo, {
      page: 1,
      pageSize: 10
    })
    operationRecords.value = data?.records || []
    operationTotal.value = data?.total || 0
  } catch (error) {
    operationError.value = error?.message || '批次操作历史加载失败'
    operationRecords.value = []
    operationTotal.value = 0
  } finally {
    operationLoading.value = false
  }
}

onMounted(() => {
  loadDetail()
  loadOperations()
})
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

.section-heading {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 16px;

  h3 {
    margin: 0 0 6px;
    font-size: 18px;
    color: #18181b;
  }

  p {
    margin: 0;
    color: #71717a;
    font-size: 13px;
  }
}

.section-alert {
  margin-bottom: 14px;
}

.operation-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
  color: #71717a;
  font-size: 13px;
}

@media (max-width: 768px) {
  .page-header,
  .summary-top,
  .section-heading,
  .operation-footer {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
