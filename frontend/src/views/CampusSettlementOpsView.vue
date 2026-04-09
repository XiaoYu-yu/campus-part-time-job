<template>
  <MainLayout>
    <div class="campus-admin-page">
      <div class="page-header">
        <div>
          <h2>校园结算运营</h2>
          <p>只读演示页，联动查看结算对账摘要、单笔结算记录和详情 drawer，不做任何写操作。</p>
        </div>
      </div>

      <section class="summary-card" v-loading="summaryLoading">
        <div class="panel-header">
          <div>
            <h3>对账摘要</h3>
            <p>摘要区调用 `GET /api/campus/admin/settlements/reconcile-summary`，复用当前筛选条件。</p>
          </div>
        </div>
        <div class="summary-grid">
          <div class="summary-item">
            <span>记录总数</span>
            <strong>{{ summary.totalCount ?? 0 }}</strong>
          </div>
          <div class="summary-item">
            <span>待打款数</span>
            <strong>{{ summary.pendingPayoutCount ?? 0 }}</strong>
          </div>
          <div class="summary-item">
            <span>已打款数</span>
            <strong>{{ summary.paidCount ?? 0 }}</strong>
          </div>
          <div class="summary-item">
            <span>打款失败数</span>
            <strong>{{ summary.failedPayoutCount ?? 0 }}</strong>
          </div>
          <div class="summary-item">
            <span>待打款金额</span>
            <strong>{{ formatAmount(summary.totalPendingAmount) }}</strong>
          </div>
          <div class="summary-item">
            <span>已打款金额</span>
            <strong>{{ formatAmount(summary.totalPaidAmount) }}</strong>
          </div>
          <div class="summary-item">
            <span>失败金额</span>
            <strong>{{ formatAmount(summary.totalFailedAmount) }}</strong>
          </div>
        </div>
      </section>

      <section class="filter-card">
        <el-form :inline="true" class="filter-form">
          <el-form-item label="结算状态">
            <el-select v-model="filters.settlementStatus" placeholder="全部" clearable style="width: 180px">
              <el-option label="待结算" value="PENDING" />
              <el-option label="已结算" value="SETTLED" />
            </el-select>
          </el-form-item>
          <el-form-item label="打款状态">
            <el-select v-model="filters.payoutStatus" placeholder="全部" clearable style="width: 180px">
              <el-option label="未打款" value="UNPAID" />
              <el-option label="已打款" value="PAID" />
              <el-option label="打款失败" value="FAILED" />
            </el-select>
          </el-form-item>
          <el-form-item label="配送员ID">
            <el-input v-model="filters.courierProfileId" placeholder="按配送员ID筛选" clearable style="width: 180px" />
          </el-form-item>
          <el-form-item label="订单号">
            <el-input v-model="filters.relayOrderId" placeholder="按订单号筛选" clearable style="width: 220px" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </section>

      <section class="table-card">
        <div class="panel-header">
          <div>
            <h3>结算记录列表</h3>
            <p>列表区调用 `GET /api/campus/admin/settlements`，为空时摘要区仍保留、表格展示空态。</p>
          </div>
        </div>

        <el-table v-loading="listLoading" :data="records" border>
          <el-table-column prop="id" label="结算ID" width="96" align="center" />
          <el-table-column prop="relayOrderId" label="订单号" min-width="200" />
          <el-table-column prop="courierProfileId" label="配送员ID" width="110" align="center" />
          <el-table-column label="总金额" width="130" align="right">
            <template #default="{ row }">
              {{ formatAmount(row.grossAmount) }}
            </template>
          </el-table-column>
          <el-table-column label="平台佣金" width="130" align="right">
            <template #default="{ row }">
              {{ formatAmount(row.platformCommission) }}
            </template>
          </el-table-column>
          <el-table-column label="待结算金额" width="140" align="right">
            <template #default="{ row }">
              {{ formatAmount(row.pendingAmount) }}
            </template>
          </el-table-column>
          <el-table-column label="结算状态" width="110" align="center">
            <template #default="{ row }">
              <el-tag :type="settlementTagType(row.settlementStatus)">{{ row.settlementStatus || 'PENDING' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="打款状态" width="110" align="center">
            <template #default="{ row }">
              <el-tag :type="payoutTagType(row.payoutStatus)">{{ row.payoutStatus || 'UNPAID' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="打款记录时间" min-width="170">
            <template #default="{ row }">
              {{ formatDateTime(row.payoutRecordedAt) }}
            </template>
          </el-table-column>
          <el-table-column label="结算时间" min-width="170">
            <template #default="{ row }">
              {{ formatDateTime(row.settledAt) }}
            </template>
          </el-table-column>
          <el-table-column label="创建时间" min-width="170">
            <template #default="{ row }">
              {{ formatDateTime(row.createdAt) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link @click="openDetail(row.id)">查看详情</el-button>
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

      <el-drawer v-model="detailVisible" size="720px" title="结算记录详情" destroy-on-close>
        <div v-loading="detailLoading" class="detail-wrapper">
          <template v-if="detail">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="结算ID">{{ detail.id }}</el-descriptions-item>
              <el-descriptions-item label="订单号">{{ detail.relayOrderId }}</el-descriptions-item>
              <el-descriptions-item label="配送员ID">{{ detail.courierProfileId ?? '暂无' }}</el-descriptions-item>
              <el-descriptions-item label="结算状态">{{ detail.settlementStatus || 'PENDING' }}</el-descriptions-item>
              <el-descriptions-item label="总金额">{{ formatAmount(detail.grossAmount) }}</el-descriptions-item>
              <el-descriptions-item label="平台佣金">{{ formatAmount(detail.platformCommission) }}</el-descriptions-item>
              <el-descriptions-item label="待结算金额">{{ formatAmount(detail.pendingAmount) }}</el-descriptions-item>
              <el-descriptions-item label="打款状态">{{ detail.payoutStatus || 'UNPAID' }}</el-descriptions-item>
              <el-descriptions-item label="结算备注" :span="2">{{ detail.remark || '暂无' }}</el-descriptions-item>
              <el-descriptions-item label="打款备注" :span="2">{{ detail.payoutRemark || '暂无' }}</el-descriptions-item>
              <el-descriptions-item label="打款参考号">{{ detail.payoutReferenceNo || '暂无' }}</el-descriptions-item>
              <el-descriptions-item label="是否已核对">
                {{ Number(detail.payoutVerified) === 1 ? '是' : '否' }}
              </el-descriptions-item>
              <el-descriptions-item label="核对时间">{{ formatDateTime(detail.payoutVerifiedAt) }}</el-descriptions-item>
              <el-descriptions-item label="核对备注" :span="2">{{ detail.payoutVerifyRemark || '暂无' }}</el-descriptions-item>
              <el-descriptions-item label="结算时间">{{ formatDateTime(detail.settledAt) }}</el-descriptions-item>
              <el-descriptions-item label="打款记录时间">{{ formatDateTime(detail.payoutRecordedAt) }}</el-descriptions-item>
              <el-descriptions-item label="创建时间">{{ formatDateTime(detail.createdAt) }}</el-descriptions-item>
              <el-descriptions-item label="更新时间">{{ formatDateTime(detail.updatedAt) }}</el-descriptions-item>
            </el-descriptions>
          </template>
        </div>
      </el-drawer>
    </div>
  </MainLayout>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import MainLayout from '../layout/MainLayout.vue'
import {
  getCampusSettlementDetail,
  getCampusSettlementReconcileSummary,
  getCampusSettlements
} from '../api/campus-admin'

const summaryLoading = ref(false)
const listLoading = ref(false)
const detailLoading = ref(false)
const detailVisible = ref(false)
const summary = ref({})
const records = ref([])
const detail = ref(null)

const filters = reactive({
  settlementStatus: '',
  payoutStatus: '',
  courierProfileId: '',
  relayOrderId: ''
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const formatAmount = (value) => `¥${Number(value || 0).toFixed(2)}`
const formatDateTime = (value) => value || '暂无'

const settlementTagType = (status) => ({
  PENDING: 'warning',
  SETTLED: 'success'
}[status] || 'info')

const payoutTagType = (status) => ({
  UNPAID: 'warning',
  PAID: 'success',
  FAILED: 'danger'
}[status] || 'info')

const buildQueryParams = () => ({
  settlementStatus: filters.settlementStatus || undefined,
  payoutStatus: filters.payoutStatus || undefined,
  courierProfileId: filters.courierProfileId || undefined,
  relayOrderId: filters.relayOrderId || undefined
})

const loadSummary = async () => {
  summaryLoading.value = true
  try {
    summary.value = await getCampusSettlementReconcileSummary(buildQueryParams())
  } finally {
    summaryLoading.value = false
  }
}

const loadSettlements = async () => {
  listLoading.value = true
  try {
    const response = await getCampusSettlements({
      page: pagination.page,
      pageSize: pagination.pageSize,
      ...buildQueryParams()
    })
    records.value = response.records || []
    pagination.total = response.total || 0
  } finally {
    listLoading.value = false
  }
}

const openDetail = async (id) => {
  detailVisible.value = true
  detailLoading.value = true
  detail.value = null
  try {
    detail.value = await getCampusSettlementDetail(id)
  } finally {
    detailLoading.value = false
  }
}

const loadPageData = async () => {
  await Promise.all([loadSummary(), loadSettlements()])
}

const handleSearch = () => {
  pagination.page = 1
  loadPageData()
}

const handleReset = () => {
  filters.settlementStatus = ''
  filters.payoutStatus = ''
  filters.courierProfileId = ''
  filters.relayOrderId = ''
  pagination.page = 1
  loadPageData()
}

const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.page = 1
  loadSettlements()
}

const handleCurrentChange = (page) => {
  pagination.page = page
  loadSettlements()
}

onMounted(() => loadPageData())
</script>

<style scoped lang="scss">
.campus-admin-page {
  padding: 20px;
}

.page-header,
.summary-card,
.filter-card,
.table-card {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: 0 6px 20px rgba(15, 23, 42, 0.05);
}

.page-header {
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

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;

  h3 {
    margin: 0 0 6px;
    font-size: 18px;
    color: #18181b;
  }

  p {
    margin: 0;
    color: #71717a;
    line-height: 1.5;
  }
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
</style>
