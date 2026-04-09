<template>
  <MainLayout>
    <div class="campus-admin-page">
      <div class="page-header">
        <div>
          <h2>校园售后执行</h2>
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

      <section class="filter-card">
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
        <el-table v-loading="loading" :data="records" border empty-text="当前筛选条件下暂无售后执行记录">
          <el-table-column prop="relayOrderId" label="订单号" min-width="200" />
          <el-table-column prop="orderStatus" label="订单状态" width="150" />
          <el-table-column prop="customerUserId" label="用户ID" width="100" align="center" />
          <el-table-column prop="courierProfileId" label="配送员ID" width="110" align="center" />
          <el-table-column label="决策类型" width="130" align="center">
            <template #default="{ row }">
              <el-tag :type="decisionTagType(row.decisionType)">{{ row.decisionType || 'NONE' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="决策金额" width="130" align="right">
            <template #default="{ row }">
              {{ formatAmount(row.decisionAmount) }}
            </template>
          </el-table-column>
          <el-table-column label="执行状态" width="130" align="center">
            <template #default="{ row }">
              <el-tag :type="executionTagType(row.afterSaleExecutionStatus)">
                {{ row.afterSaleExecutionStatus || 'PENDING' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="执行备注" min-width="220" show-overflow-tooltip>
            <template #default="{ row }">
              {{ displayText(row.afterSaleExecutionRemark) }}
            </template>
          </el-table-column>
          <el-table-column label="人工纠正" width="110" align="center">
            <template #default="{ row }">
              <el-tag :type="Number(row.afterSaleExecutionCorrected) === 1 ? 'warning' : 'info'">
                {{ Number(row.afterSaleExecutionCorrected) === 1 ? '是' : '否' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="执行时间" min-width="170">
            <template #default="{ row }">
              {{ formatDateTime(row.afterSaleExecutedAt) }}
            </template>
          </el-table-column>
          <el-table-column label="纠正时间" min-width="170">
            <template #default="{ row }">
              {{ formatDateTime(row.afterSaleExecutionCorrectedAt) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" fixed="right">
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
              <el-descriptions :column="2" border>
                <el-descriptions-item label="申请时间">{{ formatDateTime(detail.afterSaleAppliedAt) }}</el-descriptions-item>
                <el-descriptions-item label="申请原因">{{ displayText(detail.afterSaleReason) }}</el-descriptions-item>
              </el-descriptions>
            </div>

            <div class="detail-section">
              <h3>售后处理与决策</h3>
              <el-descriptions :column="2" border>
                <el-descriptions-item label="处理动作">{{ displayText(detail.afterSaleHandleAction) }}</el-descriptions-item>
                <el-descriptions-item label="处理时间">{{ formatDateTime(detail.afterSaleHandledAt) }}</el-descriptions-item>
                <el-descriptions-item label="处理备注">{{ displayText(detail.afterSaleHandleRemark) }}</el-descriptions-item>
                <el-descriptions-item label="决策类型">{{ displayText(detail.afterSaleDecisionType) }}</el-descriptions-item>
                <el-descriptions-item label="决策金额">{{ formatAmount(detail.afterSaleDecisionAmount) }}</el-descriptions-item>
                <el-descriptions-item label="决策时间">{{ formatDateTime(detail.afterSaleDecidedAt) }}</el-descriptions-item>
                <el-descriptions-item label="决策备注" :span="2">{{ displayText(detail.afterSaleDecisionRemark) }}</el-descriptions-item>
              </el-descriptions>
            </div>

            <div class="detail-section">
              <h3>售后执行</h3>
              <el-descriptions :column="2" border>
                <el-descriptions-item label="执行状态">{{ displayText(detail.afterSaleExecutionStatus) }}</el-descriptions-item>
                <el-descriptions-item label="执行时间">{{ formatDateTime(detail.afterSaleExecutedAt) }}</el-descriptions-item>
                <el-descriptions-item label="执行备注">{{ displayText(detail.afterSaleExecutionRemark) }}</el-descriptions-item>
                <el-descriptions-item label="执行参考号">{{ displayText(detail.afterSaleExecutionReferenceNo) }}</el-descriptions-item>
                <el-descriptions-item label="人工纠正">
                  {{ Number(detail.afterSaleExecutionCorrected) === 1 ? '是' : '否' }}
                </el-descriptions-item>
                <el-descriptions-item label="纠正时间">
                  {{ formatDateTime(detail.afterSaleExecutionCorrectedAt) }}
                </el-descriptions-item>
              </el-descriptions>
            </div>
          </template>
        </div>
      </el-drawer>
    </div>
  </MainLayout>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import MainLayout from '../layout/MainLayout.vue'
import { getCampusAdminAfterSaleResult, getCampusAfterSaleExecutions } from '../api/campus-admin'

const loading = ref(false)
const detailLoading = ref(false)
const detailVisible = ref(false)
const records = ref([])
const detail = ref(null)

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

const openDetail = async (relayOrderId) => {
  detailVisible.value = true
  detailLoading.value = true
  detail.value = null
  try {
    detail.value = await getCampusAdminAfterSaleResult(relayOrderId)
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

.filter-form {
  margin-bottom: -18px;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.detail-wrapper {
  padding-right: 8px;
}

.detail-section {
  margin-top: 20px;

  h3 {
    margin: 0 0 12px;
    font-size: 16px;
    color: #18181b;
  }
}
</style>
