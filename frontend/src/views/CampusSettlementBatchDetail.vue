<template>
  <MainLayout>
    <div class="campus-admin-page">
      <section class="page-hero">
        <div>
          <span class="eyebrow">Campus Ops</span>
          <h2>结算批次详情</h2>
          <p>查看单个结算批次的详细信息，包含结算明细和操作历史。</p>
        </div>
        <div class="hero-actions">
          <el-button @click="goBack">
            <el-icon><ArrowLeft /></el-icon>
            返回列表
          </el-button>
        </div>
      </section>

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
        <div class="table-heading">
          <div>
            <span class="section-kicker">结算明细</span>
            <h3>批次结算明细</h3>
            <p>来源于当前批次下的 settlement 记录，继续只读展示单笔打款与核对摘要。</p>
          </div>
        </div>
        <el-table v-loading="loading" :data="detail?.records || []" class="campus-table" style="width: 100%;">
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
        <div class="table-heading">
          <div>
            <span class="section-kicker">操作审计</span>
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
          class="campus-table"
          style="width: 100%;"
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
import { ArrowLeft } from '@element-plus/icons-vue'
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

const goBack = () => {
  router.push('/campus/settlement-batches')
}

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
  display: flex;
  flex-direction: column;
  gap: 22px;

  .page-hero {
    position: relative;
    overflow: hidden;
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
    gap: 24px;
    min-height: 170px;
    padding: 34px 40px;
    border: 1px solid rgba(15, 118, 110, 0.12);
    border-radius: 28px;
    background:
      radial-gradient(circle at 86% 18%, rgba(132, 204, 22, 0.28), transparent 28%),
      radial-gradient(circle at 16% 20%, rgba(14, 165, 233, 0.16), transparent 32%),
      linear-gradient(135deg, rgba(255, 255, 255, 0.94) 0%, rgba(236, 253, 245, 0.86) 54%, rgba(224, 242, 254, 0.86) 100%);
    box-shadow: 0 24px 60px rgba(15, 23, 42, 0.09);
    color: #0f172a;

    &::after {
      content: '';
      position: absolute;
      right: -80px;
      bottom: -110px;
      width: 270px;
      height: 270px;
      border-radius: 50%;
      border: 38px solid rgba(15, 118, 110, 0.07);
    }

    h2 {
      position: relative;
      margin: 8px 0 10px;
      font-size: 34px;
      font-weight: 900;
      letter-spacing: -0.03em;
    }

    p {
      position: relative;
      max-width: 560px;
      margin: 0;
      color: #475569;
      font-size: 15px;
      line-height: 1.8;
    }
  }

  .eyebrow,
  .section-kicker {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    color: #0f766e;
    font-size: 12px;
    font-weight: 900;
    letter-spacing: 0.14em;
    text-transform: uppercase;
  }

  .hero-actions {
    position: relative;
    z-index: 1;
    display: flex;
    align-items: center;

    .el-button {
      border: 1px solid rgba(15, 118, 110, 0.2);
      border-radius: 14px;
      background: rgba(255, 255, 255, 0.72);
      backdrop-filter: blur(14px);
      color: #0f766e;
      font-weight: 700;
      padding: 10px 20px;

      &:hover {
        background: rgba(255, 255, 255, 0.9);
        border-color: rgba(15, 118, 110, 0.3);
      }
    }
  }

  .summary-card,
  .table-card {
    border: 1px solid rgba(15, 118, 110, 0.1);
    border-radius: 24px;
    background: rgba(255, 255, 255, 0.9);
    box-shadow: 0 18px 42px rgba(15, 23, 42, 0.08);
    padding: 22px 24px;
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

  .summary-title h3 {
    margin: 0;
    font-size: 24px;
    font-weight: 900;
    color: #0f172a;
  }

  .summary-amount strong {
    font-size: 24px;
    color: #0f766e;
  }

  .label {
    color: #64748b;
    font-size: 13px;
  }

  .summary-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
    gap: 12px;
  }

  .summary-item {
    background: #f8fafc;
    border: 1px solid rgba(15, 118, 110, 0.06);
    border-radius: 14px;
    padding: 14px;
    display: flex;
    flex-direction: column;
    gap: 8px;

    span {
      color: #64748b;
      font-size: 13px;
    }

    strong {
      color: #0f172a;
      font-size: 16px;
      font-weight: 800;
    }
  }

  .table-heading {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    gap: 16px;
    margin-bottom: 16px;

    h3 {
      margin: 6px 0 6px;
      color: #0f172a;
      font-size: 20px;
      font-weight: 900;
    }

    p {
      margin: 0;
      color: #64748b;
      font-size: 13px;
    }
  }

  .campus-table {
    overflow: hidden;
    border: 1px solid #e2e8f0;
    border-radius: 18px;

    :deep(.el-table__header-wrapper th) {
      background: #f8fafc;
      color: #0f172a;
      font-weight: 800;
    }

    :deep(.el-table__row) {
      color: #334155;
    }

    :deep(.el-table__cell) {
      border-bottom-color: #edf2f7;
    }
  }

  .section-alert {
    margin-bottom: 14px;
  }

  .operation-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 14px;
    color: #64748b;
    font-size: 13px;
  }
}

@media (max-width: 900px) {
  .campus-admin-page {
    .page-hero,
    .summary-top,
    .table-heading {
      flex-direction: column;
      align-items: flex-start;
    }

    .operation-footer {
      flex-direction: column;
      align-items: flex-start;
      gap: 8px;
    }
  }
}
</style>
