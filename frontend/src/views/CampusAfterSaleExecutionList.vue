<template>
  <MainLayout>
    <div class="campus-admin-page">
      <div class="page-header">
        <div>
          <div class="title-row">
            <h2>校园售后执行</h2>
            <span class="readonly-badge">只读执行</span>
          </div>
          <p>只读演示页，查看售后执行状态、纠正审计与单笔售后结果汇总。</p>
        </div>
      </div>

      <el-alert
        title="该页聚焦售后执行结果和人工纠正审计，详情 drawer 继续复用现有 after-sale-result 接口。"
        type="info"
        :closable="false"
        show-icon
        class="page-alert"
      />

      <section class="ops-guide">
        <div class="guide-item">
          <span>列表</span>
          <strong>执行记录分页</strong>
          <p>按执行状态、决策类型和人工纠正条件定位售后执行记录。</p>
        </div>
        <div class="guide-item">
          <span>状态</span>
          <strong>结果与纠正</strong>
          <p>直接查看执行状态、执行备注和是否经过人工纠正。</p>
        </div>
        <div class="guide-item">
          <span>详情</span>
          <strong>售后结果汇总</strong>
          <p>通过 drawer 只读回看申请、处理、决策和执行字段。</p>
        </div>
      </section>

      <section class="filter-card">
        <div class="panel-header compact-header">
          <div>
            <h3>筛选条件</h3>
            <p>筛选只影响售后执行记录读取，不触发任何退款、补偿或纠正写操作。</p>
          </div>
        </div>
        <el-form :inline="true" class="filter-form">
          <el-form-item label="执行状态">
            <el-select v-model="filters.afterSaleExecutionStatus" placeholder="全部" clearable style="width: 180px">
              <el-option label="待执行" value="PENDING" />
              <el-option label="执行成功" value="SUCCESS" />
              <el-option label="执行失败" value="FAILED" />
              <el-option label="无需执行" value="NOT_REQUIRED" />
            </el-select>
          </el-form-item>
          <el-form-item label="决策类型">
            <el-select v-model="filters.decisionType" placeholder="全部" clearable style="width: 180px">
              <el-option label="退款" value="REFUND" />
              <el-option label="补偿" value="COMPENSATE" />
              <el-option label="无需金额处理" value="NONE" />
            </el-select>
          </el-form-item>
          <el-form-item label="只看人工纠正">
            <el-switch v-model="filters.correctedOnly" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </section>

      <section class="table-card">
        <div class="panel-header table-header">
          <div>
            <h3>售后执行记录</h3>
            <p>当前页只展示执行结果、执行备注和纠正审计字段，详情继续复用现有 after-sale-result 读取接口。</p>
          </div>
        </div>

        <div class="table-note">
          <strong>只读提示</strong>
          <span>本页不提供售后处理、执行纠正或状态推进按钮，只用于运营演示和结果核对。</span>
        </div>

        <el-table v-loading="loading" :data="records" border empty-text="当前筛选条件下暂无售后执行记录，可调整执行状态、决策类型或纠正条件后重新查询">
          <el-table-column prop="relayOrderId" label="订单号" min-width="200" :resizable="false" />
          <el-table-column prop="orderStatus" label="订单状态" width="150" :resizable="false" />
          <el-table-column prop="customerUserId" label="用户ID" width="100" align="center" :resizable="false" />
          <el-table-column prop="courierProfileId" label="配送员ID" width="110" align="center" :resizable="false" />
          <el-table-column label="决策类型" width="130" align="center" :resizable="false">
            <template #default="{ row }">
              <el-tag :type="decisionTagType(row.decisionType)">
                {{ decisionTypeText(row.decisionType) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="决策金额" width="130" align="right" :resizable="false">
            <template #default="{ row }">
              {{ formatAmount(row.decisionAmount) }}
            </template>
          </el-table-column>
          <el-table-column label="执行状态" width="130" align="center" :resizable="false">
            <template #default="{ row }">
              <el-tag :type="executionTagType(row.afterSaleExecutionStatus)">
                {{ executionStatusText(row.afterSaleExecutionStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="执行备注" min-width="220" show-overflow-tooltip :resizable="false">
            <template #default="{ row }">
              {{ displayText(row.afterSaleExecutionRemark) }}
            </template>
          </el-table-column>
          <el-table-column label="人工纠正" width="110" align="center" :resizable="false">
            <template #default="{ row }">
              <el-tag :type="Number(row.afterSaleExecutionCorrected) === 1 ? 'warning' : 'info'">
                {{ correctionText(row.afterSaleExecutionCorrected) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="执行时间" min-width="170" :resizable="false">
            <template #default="{ row }">
              {{ formatDateTime(row.afterSaleExecutedAt) }}
            </template>
          </el-table-column>
          <el-table-column label="纠正时间" min-width="170" :resizable="false">
            <template #default="{ row }">
              {{ formatDateTime(row.afterSaleExecutionCorrectedAt) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" fixed="right" :resizable="false">
            <template #default="{ row }">
              <el-button type="primary" link @click="openDetail(row.relayOrderId)">查看详情</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="pagination.page"
            v-model:page-size="pagination.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="pagination.total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </section>

      <el-drawer v-model="detailVisible" size="720px" title="售后结果详情" destroy-on-close>
        <div v-loading="detailLoading" class="detail-wrapper">
          <template v-if="detail">
            <div class="detail-status-card">
              <div>
                <span>当前售后订单</span>
                <strong>{{ detail.relayOrderId }}</strong>
              </div>
              <div class="detail-tags">
                <el-tag type="info">{{ detail.orderStatus || '暂无状态' }}</el-tag>
                <el-tag :type="decisionTagType(detail.afterSaleDecisionType)">
                  {{ decisionTypeText(detail.afterSaleDecisionType) }}
                </el-tag>
                <el-tag :type="executionTagType(detail.afterSaleExecutionStatus)">
                  {{ executionStatusText(detail.afterSaleExecutionStatus) }}
                </el-tag>
                <el-tag :type="Number(detail.afterSaleExecutionCorrected) === 1 ? 'warning' : 'info'">
                  {{ correctionText(detail.afterSaleExecutionCorrected) }}
                </el-tag>
              </div>
            </div>

            <div class="detail-section-title">
              <h3>订单与配送摘要</h3>
              <p>以下字段来自 `GET /api/campus/admin/orders/{id}/after-sale-result`，本轮只做展示层分组。</p>
            </div>

            <el-descriptions :column="2" border>
              <el-descriptions-item label="订单号">{{ detail.relayOrderId }}</el-descriptions-item>
              <el-descriptions-item label="订单状态">{{ detail.orderStatus }}</el-descriptions-item>
              <el-descriptions-item label="支付状态">{{ detail.paymentStatus || '暂无' }}</el-descriptions-item>
              <el-descriptions-item label="用户ID">{{ displayText(detail.customerUserId) }}</el-descriptions-item>
              <el-descriptions-item label="配送员ID">{{ displayText(detail.courierProfileId) }}</el-descriptions-item>
              <el-descriptions-item label="配送员姓名">{{ displayText(detail.courierName) }}</el-descriptions-item>
              <el-descriptions-item label="用户姓名">{{ displayText(detail.customerName) }}</el-descriptions-item>
              <el-descriptions-item label="用户手机号">{{ displayText(detail.customerPhone) }}</el-descriptions-item>
              <el-descriptions-item label="取餐点">{{ displayText(detail.pickupPointName) }}</el-descriptions-item>
              <el-descriptions-item label="配送目标">{{ displayText(detail.deliveryBuilding) }}</el-descriptions-item>
              <el-descriptions-item label="总金额">{{ formatAmount(detail.totalAmount) }}</el-descriptions-item>
              <el-descriptions-item label="最近更新时间">{{ formatDateTime(detail.updatedAt) }}</el-descriptions-item>
            </el-descriptions>

            <div class="detail-section">
              <h3>售后申请</h3>
              <p>展示用户提交的售后原因和申请时间，不在本页处理售后。</p>
              <el-descriptions :column="2" border>
                <el-descriptions-item label="申请时间">{{ formatDateTime(detail.afterSaleAppliedAt) }}</el-descriptions-item>
                <el-descriptions-item label="申请原因">{{ displayText(detail.afterSaleReason) }}</el-descriptions-item>
              </el-descriptions>
            </div>

            <div class="detail-section">
              <h3>售后处理与决策</h3>
              <p>展示 admin 已记录的处理动作、决策类型和金额，不在本页修改决策。</p>
              <el-descriptions :column="2" border>
                <el-descriptions-item label="处理动作">{{ displayText(detail.afterSaleHandleAction) }}</el-descriptions-item>
                <el-descriptions-item label="处理时间">{{ formatDateTime(detail.afterSaleHandledAt) }}</el-descriptions-item>
                <el-descriptions-item label="处理备注">{{ displayText(detail.afterSaleHandleRemark) }}</el-descriptions-item>
                <el-descriptions-item label="决策类型">{{ decisionTypeText(detail.afterSaleDecisionType) }}</el-descriptions-item>
                <el-descriptions-item label="决策金额">{{ formatAmount(detail.afterSaleDecisionAmount) }}</el-descriptions-item>
                <el-descriptions-item label="决策时间">{{ formatDateTime(detail.afterSaleDecidedAt) }}</el-descriptions-item>
                <el-descriptions-item label="决策备注" :span="2">{{ displayText(detail.afterSaleDecisionRemark) }}</el-descriptions-item>
              </el-descriptions>
            </div>

            <div class="detail-section">
              <h3>售后执行</h3>
              <p>展示执行结果和一次人工纠正审计字段，不在本页追加执行记录。</p>
              <el-descriptions :column="2" border>
                <el-descriptions-item label="执行状态">{{ executionStatusText(detail.afterSaleExecutionStatus) }}</el-descriptions-item>
                <el-descriptions-item label="执行时间">{{ formatDateTime(detail.afterSaleExecutedAt) }}</el-descriptions-item>
                <el-descriptions-item label="执行备注">{{ displayText(detail.afterSaleExecutionRemark) }}</el-descriptions-item>
                <el-descriptions-item label="执行参考号">{{ displayText(detail.afterSaleExecutionReferenceNo) }}</el-descriptions-item>
                <el-descriptions-item label="人工纠正">
                  {{ correctionText(detail.afterSaleExecutionCorrected) }}
                </el-descriptions-item>
                <el-descriptions-item label="纠正时间">
                  {{ formatDateTime(detail.afterSaleExecutionCorrectedAt) }}
                </el-descriptions-item>
              </el-descriptions>
            </div>

            <div class="detail-section">
              <h3>执行历史</h3>
              <p>以下记录来自售后执行历史表，只读展示每次执行尝试；当前摘要仍以订单上的最新执行字段为准。</p>
              <el-alert
                v-if="historyError"
                :title="historyError"
                type="warning"
                :closable="false"
                show-icon
                class="history-alert"
              />
              <el-table
                v-loading="historyLoading"
                :data="executionHistory"
                border
                size="small"
                empty-text="当前订单暂无售后执行历史，可能尚未发生执行动作"
              >
                <el-table-column prop="id" label="记录ID" width="90" align="center" :resizable="false" />
                <el-table-column label="上次状态" width="110" align="center" :resizable="false">
                  <template #default="{ row }">
                    <el-tag :type="executionTagType(row.previousExecutionStatus)">
                      {{ executionStatusText(row.previousExecutionStatus) }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="本次状态" width="110" align="center" :resizable="false">
                  <template #default="{ row }">
                    <el-tag :type="executionTagType(row.executionStatus)">
                      {{ executionStatusText(row.executionStatus) }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="纠正" width="90" align="center" :resizable="false">
                  <template #default="{ row }">
                    <el-tag :type="Number(row.corrected) === 1 ? 'warning' : 'info'">
                      {{ correctionText(row.corrected) }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="执行备注" min-width="180" show-overflow-tooltip :resizable="false">
                  <template #default="{ row }">
                    {{ displayText(row.executionRemark) }}
                  </template>
                </el-table-column>
                <el-table-column label="参考号" min-width="150" show-overflow-tooltip :resizable="false">
                  <template #default="{ row }">
                    {{ displayText(row.executionReferenceNo) }}
                  </template>
                </el-table-column>
                <el-table-column label="执行人" width="100" align="center" :resizable="false">
                  <template #default="{ row }">
                    {{ displayText(row.executedByEmployeeId) }}
                  </template>
                </el-table-column>
                <el-table-column label="执行时间" min-width="160" :resizable="false">
                  <template #default="{ row }">
                    {{ formatDateTime(row.executedAt) }}
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </template>
          <el-empty v-else-if="!detailLoading" description="暂无售后结果详情，请从列表选择一条记录查看" />
        </div>
      </el-drawer>
    </div>
  </MainLayout>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import MainLayout from '../layout/MainLayout.vue'
import {
  getCampusAdminAfterSaleResult,
  getCampusAfterSaleExecutionRecords,
  getCampusAfterSaleExecutions
} from '../api/campus-admin'

const loading = ref(false)
const detailLoading = ref(false)
const historyLoading = ref(false)
const detailVisible = ref(false)
const records = ref([])
const detail = ref(null)
const executionHistory = ref([])
const historyError = ref('')

const filters = reactive({
  afterSaleExecutionStatus: '',
  decisionType: '',
  correctedOnly: false
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const formatAmount = (value) => {
  if (value === null || value === undefined || value === '') {
    return '无金额型处理'
  }
  return `¥${Number(value).toFixed(2)}`
}

const formatDateTime = (value) => value || '暂无'
const displayText = (value) => (value === null || value === undefined || value === '' ? '暂无' : value)

const executionTagType = (status) => ({
  PENDING: 'warning',
  SUCCESS: 'success',
  FAILED: 'danger',
  NOT_REQUIRED: 'info'
}[status] || 'info')

const decisionTagType = (type) => ({
  REFUND: 'danger',
  COMPENSATE: 'warning',
  NONE: 'info'
}[type] || 'info')

const executionStatusText = (status) => ({
  PENDING: '待执行',
  SUCCESS: '执行成功',
  FAILED: '执行失败',
  NOT_REQUIRED: '无需执行'
}[status] || '待执行')

const decisionTypeText = (type) => ({
  REFUND: '退款',
  COMPENSATE: '补偿',
  NONE: '无需金额处理'
}[type] || '无需金额处理')

const correctionText = (value) => (Number(value) === 1 ? '已纠正' : '未纠正')

const loadExecutions = async () => {
  loading.value = true
  try {
    const response = await getCampusAfterSaleExecutions({
      page: pagination.page,
      pageSize: pagination.pageSize,
      afterSaleExecutionStatus: filters.afterSaleExecutionStatus || undefined,
      decisionType: filters.decisionType || undefined,
      correctedOnly: filters.correctedOnly || undefined
    })
    records.value = response.records || []
    pagination.total = response.total || 0
  } finally {
    loading.value = false
  }
}

const loadExecutionHistory = async (relayOrderId) => {
  historyLoading.value = true
  historyError.value = ''
  executionHistory.value = []
  try {
    const response = await getCampusAfterSaleExecutionRecords({
      relayOrderId,
      page: 1,
      pageSize: 20
    })
    executionHistory.value = response.records || []
  } catch (error) {
    historyError.value = '执行历史加载失败，请稍后重试或保留当前摘要结果'
  } finally {
    historyLoading.value = false
  }
}

const openDetail = async (relayOrderId) => {
  detailVisible.value = true
  detailLoading.value = true
  detail.value = null
  executionHistory.value = []
  historyError.value = ''
  try {
    detail.value = await getCampusAdminAfterSaleResult(relayOrderId)
    await loadExecutionHistory(relayOrderId)
  } finally {
    detailLoading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadExecutions()
}

const handleReset = () => {
  filters.afterSaleExecutionStatus = ''
  filters.decisionType = ''
  filters.correctedOnly = false
  pagination.page = 1
  loadExecutions()
}

const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.page = 1
  loadExecutions()
}

const handleCurrentChange = (page) => {
  pagination.page = page
  loadExecutions()
}

onMounted(() => loadExecutions())
</script>

<style scoped lang="scss">
.campus-admin-page {
  padding: 20px;
}

.page-header,
.filter-card,
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

.title-row {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.readonly-badge {
  display: inline-flex;
  align-items: center;
  border-radius: 999px;
  padding: 4px 10px;
  background: #fff7ed;
  color: #c2410c;
  font-size: 12px;
  font-weight: 600;
}

.ops-guide {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-bottom: 16px;
}

.guide-item {
  background: #fff;
  border-radius: 14px;
  padding: 14px;
  border: 1px solid #ffedd5;

  span {
    display: inline-flex;
    align-items: center;
    border-radius: 999px;
    padding: 3px 9px;
    background: #f8fafc;
    color: #71717a;
    font-size: 12px;
    margin-bottom: 8px;
  }

  strong {
    display: block;
    margin-bottom: 6px;
    color: #18181b;
  }

  p {
    margin: 0;
    color: #71717a;
    font-size: 13px;
    line-height: 1.5;
  }
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;

  h3 {
    margin: 0 0 6px;
    font-size: 17px;
    color: #18181b;
  }

  p {
    margin: 0;
    color: #71717a;
    font-size: 13px;
    line-height: 1.6;
  }
}

.compact-header,
.table-header {
  margin-bottom: 12px;
}

.filter-form {
  margin-bottom: -18px;
}

.table-note {
  display: flex;
  align-items: center;
  gap: 10px;
  border-radius: 12px;
  background: #f8fafc;
  color: #71717a;
  padding: 12px;
  margin-bottom: 12px;
  line-height: 1.5;

  strong {
    color: #c2410c;
    white-space: nowrap;
  }
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.detail-wrapper {
  padding-right: 8px;
}

.detail-status-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  border-radius: 14px;
  background: linear-gradient(180deg, #fff7ed 0%, #fff 100%);
  padding: 14px;
  margin-bottom: 16px;

  span {
    display: block;
    color: #71717a;
    font-size: 13px;
    margin-bottom: 6px;
  }

  strong {
    color: #18181b;
  }
}

.detail-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: flex-end;
}

.detail-section-title {
  margin-bottom: 12px;

  h3 {
    margin: 0 0 6px;
    font-size: 16px;
    color: #18181b;
  }

  p {
    margin: 0;
    color: #71717a;
    font-size: 13px;
    line-height: 1.6;
  }
}

.detail-section {
  margin-top: 20px;

  h3 {
    margin: 0 0 12px;
    font-size: 16px;
    color: #18181b;
  }

  p {
    margin: -4px 0 12px;
    color: #71717a;
    font-size: 13px;
    line-height: 1.6;
  }
}

.history-alert {
  margin-bottom: 12px;
}

@media (max-width: 900px) {
  .ops-guide {
    grid-template-columns: 1fr;
  }

  .table-note,
  .detail-status-card {
    flex-direction: column;
    align-items: flex-start;
  }

  .detail-tags {
    justify-content: flex-start;
  }
}
</style>
