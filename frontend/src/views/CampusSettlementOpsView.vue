<template>
  <MainLayout>
    <div class="campus-admin-page">
      <section class="page-hero">
        <div>
          <span class="eyebrow">Campus Ops</span>
          <h2>校园结算运营</h2>
          <p>管理配送员结算记录，核对金额差异，生成结算批次。</p>
        </div>
        <div class="hero-notes">
          <span>运营模块</span>
          <strong>settlements</strong>
        </div>
      </section>

      <section class="ops-guide">
        <div class="guide-item">
          <span class="section-kicker">摘要</span>
          <strong>对账聚合</strong>
          <p>先看当前筛选条件下的记录总数和金额分布。</p>
        </div>
        <div class="guide-item">
          <span class="section-kicker">筛选</span>
          <strong>状态定位</strong>
          <p>按结算状态、打款状态、配送员或订单号收窄列表。</p>
        </div>
        <div class="guide-item">
          <span class="section-kicker">详情</span>
          <strong>单笔核对</strong>
          <p>通过 drawer 只读查看打款、核对和备注字段。</p>
        </div>
      </section>

      <section class="summary-card" v-loading="summaryLoading">
        <div class="panel-header">
          <div>
            <span class="section-kicker">概览</span>
            <h3>对账摘要</h3>
          </div>
          <span class="readonly-badge">只读运营</span>
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
        <div class="panel-header compact-header">
          <div>
            <span class="section-kicker">筛选与定位</span>
            <h3>筛选条件</h3>
            <p>筛选只影响摘要和列表读取，不触发任何结算、打款或核对写操作。</p>
          </div>
        </div>
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
            <el-button type="primary" class="search-button" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </section>

      <section class="table-card">
        <div class="table-heading">
          <div>
            <span class="section-kicker">记录列表</span>
            <h3>结算记录</h3>
          </div>
          <el-tag type="info" effect="plain">共 {{ pagination.total }} 条</el-tag>
        </div>

        <div class="table-note">
          <strong>只读提示</strong>
          <span>本页只展示当前 settlement 记录，不提供确认结算、打款、撤回或核对写操作。</span>
        </div>

        <el-table v-loading="listLoading" :data="records" class="campus-table" empty-text="当前筛选条件下暂无结算记录，可调整状态或订单号后重新查询">
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
              <el-tag :type="settlementTagType(row.settlementStatus)">
                {{ settlementStatusText(row.settlementStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="打款状态" width="110" align="center">
            <template #default="{ row }">
              <el-tag :type="payoutTagType(row.payoutStatus)">
                {{ payoutStatusText(row.payoutStatus) }}
              </el-tag>
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
          <el-table-column label="操作" width="120">
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
            <div class="detail-status-card">
              <div>
                <span>当前结算记录</span>
                <strong>{{ detail.relayOrderId }}</strong>
              </div>
              <div class="detail-tags">
                <el-tag :type="settlementTagType(detail.settlementStatus)">
                  {{ settlementStatusText(detail.settlementStatus) }}
                </el-tag>
                <el-tag :type="payoutTagType(detail.payoutStatus)">
                  {{ payoutStatusText(detail.payoutStatus) }}
                </el-tag>
              </div>
            </div>

            <div class="detail-section-title">
              <span class="section-kicker">结算信息</span>
              <h3>单笔结算详情</h3>
              <p>以下字段全部来自 `GET /api/campus/admin/settlements/{id}`，本轮只做展示分组，不改变读取逻辑。</p>
            </div>

            <el-descriptions :column="2" border>
              <el-descriptions-item label="结算ID">{{ detail.id }}</el-descriptions-item>
              <el-descriptions-item label="订单号">{{ detail.relayOrderId }}</el-descriptions-item>
              <el-descriptions-item label="配送员ID">{{ displayText(detail.courierProfileId) }}</el-descriptions-item>
              <el-descriptions-item label="结算状态">{{ settlementStatusText(detail.settlementStatus) }}</el-descriptions-item>
              <el-descriptions-item label="总金额">{{ formatAmount(detail.grossAmount) }}</el-descriptions-item>
              <el-descriptions-item label="平台佣金">{{ formatAmount(detail.platformCommission) }}</el-descriptions-item>
              <el-descriptions-item label="待结算金额">{{ formatAmount(detail.pendingAmount) }}</el-descriptions-item>
              <el-descriptions-item label="打款状态">{{ payoutStatusText(detail.payoutStatus) }}</el-descriptions-item>
              <el-descriptions-item label="结算备注" :span="2">{{ displayText(detail.remark) }}</el-descriptions-item>
              <el-descriptions-item label="打款备注" :span="2">{{ displayText(detail.payoutRemark) }}</el-descriptions-item>
              <el-descriptions-item label="打款参考号">{{ displayText(detail.payoutReferenceNo) }}</el-descriptions-item>
              <el-descriptions-item label="是否已核对">
                {{ Number(detail.payoutVerified) === 1 ? '是' : '否' }}
              </el-descriptions-item>
              <el-descriptions-item label="核对时间">{{ formatDateTime(detail.payoutVerifiedAt) }}</el-descriptions-item>
              <el-descriptions-item label="核对备注" :span="2">{{ displayText(detail.payoutVerifyRemark) }}</el-descriptions-item>
              <el-descriptions-item label="结算时间">{{ formatDateTime(detail.settledAt) }}</el-descriptions-item>
              <el-descriptions-item label="打款记录时间">{{ formatDateTime(detail.payoutRecordedAt) }}</el-descriptions-item>
              <el-descriptions-item label="创建时间">{{ formatDateTime(detail.createdAt) }}</el-descriptions-item>
              <el-descriptions-item label="更新时间">{{ formatDateTime(detail.updatedAt) }}</el-descriptions-item>
            </el-descriptions>

            <div class="detail-section-title reconcile-title">
              <div>
                <span class="section-kicker">对账审计</span>
                <h3>对账差异记录</h3>
                <p>只读展示按当前结算ID返回的差异记录，不提供创建或处理操作。</p>
              </div>
              <el-tag type="info" effect="plain">只读差异</el-tag>
            </div>

            <el-alert
              v-if="reconcileDifferenceError"
              :title="reconcileDifferenceError"
              type="error"
              show-icon
              :closable="false"
              class="drawer-alert"
            />

            <el-table
              v-loading="reconcileDifferenceLoading"
              :data="reconcileDifferenceRecords"
              class="campus-table"
              empty-text="暂无对账差异记录，当前 settlement 仍按原 payout 摘要展示"
            >
              <el-table-column prop="id" label="差异ID" width="92" align="center" />
              <el-table-column prop="payoutBatchNo" label="批次号" min-width="150">
                <template #default="{ row }">
                  {{ displayText(row.payoutBatchNo) }}
                </template>
              </el-table-column>
              <el-table-column label="差异类型" min-width="150" align="center">
                <template #default="{ row }">
                  <el-tag :type="differenceTypeTag(row.differenceType)">
                    {{ differenceTypeText(row.differenceType) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="期望金额" width="120" align="right">
                <template #default="{ row }">
                  {{ formatOptionalAmount(row.expectedAmount) }}
                </template>
              </el-table-column>
              <el-table-column label="实际金额" width="120" align="right">
                <template #default="{ row }">
                  {{ formatOptionalAmount(row.actualAmount) }}
                </template>
              </el-table-column>
              <el-table-column prop="differenceRemark" label="差异说明" min-width="200" show-overflow-tooltip>
                <template #default="{ row }">
                  {{ displayText(row.differenceRemark) }}
                </template>
              </el-table-column>
              <el-table-column label="处理状态" width="110" align="center">
                <template #default="{ row }">
                  <el-tag :type="processStatusTag(row.processStatus)">
                    {{ processStatusText(row.processStatus) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="处理结果" width="120" align="center">
                <template #default="{ row }">
                  {{ processResultText(row.processResult) }}
                </template>
              </el-table-column>
              <el-table-column label="来源" width="130" align="center">
                <template #default="{ row }">
                  {{ sourceText(row.source) }}
                </template>
              </el-table-column>
              <el-table-column label="登记时间" min-width="170">
                <template #default="{ row }">
                  {{ formatDateTime(row.reportedAt) }}
                </template>
              </el-table-column>
              <el-table-column label="处理时间" min-width="170">
                <template #default="{ row }">
                  {{ formatDateTime(row.processedAt) }}
                </template>
              </el-table-column>
            </el-table>

            <div class="reconcile-footer">
              <span>共 {{ reconcileDifferenceTotal }} 条差异记录</span>
              <span>差异记录仅作审计展示，不改变当前 settlement payout 摘要。</span>
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
import {
  getCampusSettlementDetail,
  getCampusSettlementReconcileDifferences,
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
const reconcileDifferenceLoading = ref(false)
const reconcileDifferenceRecords = ref([])
const reconcileDifferenceTotal = ref(0)
const reconcileDifferenceError = ref('')

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
const formatOptionalAmount = (value) => (value === null || value === undefined || value === '' ? '暂无' : formatAmount(value))
const formatDateTime = (value) => value || '暂无'
const displayText = (value) => (value === null || value === undefined || value === '' ? '暂无' : value)

const settlementTagType = (status) => ({
  PENDING: 'warning',
  SETTLED: 'success'
}[status] || 'info')

const payoutTagType = (status) => ({
  UNPAID: 'warning',
  PAID: 'success',
  FAILED: 'danger'
}[status] || 'info')

const settlementStatusText = (status) => ({
  PENDING: '待结算',
  SETTLED: '已结算'
}[status] || '待结算')

const payoutStatusText = (status) => ({
  UNPAID: '未打款',
  PAID: '已打款',
  FAILED: '打款失败'
}[status] || '未打款')

const differenceTypeTag = (type) => ({
  AMOUNT_MISMATCH: 'danger',
  STATUS_MISMATCH: 'warning',
  UNVERIFIED_PAID: 'warning',
  FAILED_NEEDS_RETRY: 'danger',
  MANUAL_NOTE: 'info'
}[type] || 'info')

const differenceTypeText = (type) => ({
  AMOUNT_MISMATCH: '金额不一致',
  STATUS_MISMATCH: '状态不一致',
  UNVERIFIED_PAID: '已打款未核对',
  FAILED_NEEDS_RETRY: '失败待重试',
  MANUAL_NOTE: '人工备注'
}[type] || type || '暂无')

const processStatusTag = (status) => ({
  OPEN: 'warning',
  RESOLVED: 'success'
}[status] || 'info')

const processStatusText = (status) => ({
  OPEN: '待处理',
  RESOLVED: '已处理'
}[status] || status || '暂无')

const processResultText = (result) => ({
  CONFIRMED: '已确认',
  MARKED_INVALID: '标记无效',
  FOLLOWED_UP: '已跟进'
}[result] || result || '暂无')

const sourceText = (source) => ({
  MANUAL_ADMIN: '人工登记',
  SIMULATED_RECONCILE: '模拟对账'
}[source] || source || '暂无')

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
  reconcileDifferenceRecords.value = []
  reconcileDifferenceTotal.value = 0
  reconcileDifferenceError.value = ''
  try {
    const [detailData] = await Promise.all([
      getCampusSettlementDetail(id),
      loadReconcileDifferences(id)
    ])
    detail.value = detailData
  } finally {
    detailLoading.value = false
  }
}

const loadReconcileDifferences = async (settlementRecordId) => {
  reconcileDifferenceLoading.value = true
  reconcileDifferenceError.value = ''
  try {
    const data = await getCampusSettlementReconcileDifferences({
      settlementRecordId,
      page: 1,
      pageSize: 10
    })
    reconcileDifferenceRecords.value = data?.records || []
    reconcileDifferenceTotal.value = data?.total || 0
  } catch (error) {
    reconcileDifferenceRecords.value = []
    reconcileDifferenceTotal.value = 0
    reconcileDifferenceError.value = error?.message || '对账差异记录加载失败'
  } finally {
    reconcileDifferenceLoading.value = false
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
  display: flex;
  flex-direction: column;
  gap: 22px;
}

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

.hero-notes {
  position: relative;
  z-index: 1;
  display: grid;
  gap: 4px;
  min-width: 142px;
  padding: 18px 20px;
  border: 1px solid rgba(15, 118, 110, 0.12);
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.72);
  backdrop-filter: blur(14px);

  span {
    color: #64748b;
    font-size: 12px;
  }

  strong {
    color: #0f172a;
    font-size: 20px;
    letter-spacing: 0.02em;
  }
}

.summary-card,
.filter-card,
.table-card {
  border: 1px solid rgba(15, 118, 110, 0.1);
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0 18px 42px rgba(15, 23, 42, 0.08);
  backdrop-filter: blur(18px);
  padding: 22px 24px;
}

.ops-guide {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 14px;
}

.guide-item {
  border: 1px solid rgba(15, 118, 110, 0.1);
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.055);
  backdrop-filter: blur(14px);
  padding: 18px 20px;

  span {
    display: inline-flex;
    align-items: center;
    border-radius: 999px;
    padding: 3px 10px;
    background: #f0fdfa;
    color: #0f766e;
    font-size: 12px;
    font-weight: 800;
    margin-bottom: 10px;
  }

  strong {
    display: block;
    margin-bottom: 6px;
    color: #0f172a;
    font-weight: 900;
  }

  p {
    margin: 0;
    color: #64748b;
    font-size: 13px;
    line-height: 1.7;
  }
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;

  h3 {
    margin: 6px 0 6px;
    font-size: 18px;
    font-weight: 900;
    color: #0f172a;
  }

  p {
    margin: 0;
    color: #64748b;
    font-size: 13px;
    line-height: 1.7;
  }
}

.compact-header {
  margin-bottom: 12px;
}

.readonly-badge {
  display: inline-flex;
  align-items: center;
  border-radius: 999px;
  padding: 4px 12px;
  border: 1px solid rgba(15, 118, 110, 0.14);
  background: #f0fdfa;
  color: #0f766e;
  font-size: 12px;
  font-weight: 800;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  gap: 12px;
}

.summary-item {
  background: #f8fffe;
  border: 1px solid rgba(15, 118, 110, 0.06);
  border-radius: 14px;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;

  span {
    color: #64748b;
    font-size: 13px;
  }

  strong {
    color: #0f172a;
    font-size: 18px;
  }
}

.filter-form {
  margin-bottom: -18px;
}

.table-note {
  display: flex;
  align-items: center;
  gap: 10px;
  border: 1px solid rgba(15, 118, 110, 0.08);
  border-radius: 14px;
  background: linear-gradient(135deg, #f8fffe, #f0fdfa);
  color: #64748b;
  padding: 12px 16px;
  margin-bottom: 14px;
  line-height: 1.6;

  strong {
    color: #0f766e;
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
  border: 1px solid rgba(15, 118, 110, 0.1);
  border-radius: 20px;
  background:
    radial-gradient(circle at 90% 20%, rgba(132, 204, 22, 0.12), transparent 40%),
    linear-gradient(180deg, #f8fffe 0%, #f0fdfa 100%);
  padding: 18px 20px;
  margin-bottom: 18px;

  span {
    display: block;
    color: #64748b;
    font-size: 13px;
    margin-bottom: 6px;
  }

  strong {
    color: #0f172a;
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
    font-weight: 900;
    color: #0f172a;
  }

  p {
    margin: 0;
    color: #64748b;
    font-size: 13px;
    line-height: 1.7;
  }
}

.reconcile-title {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  margin-top: 22px;
}

.drawer-alert {
  margin-bottom: 12px;
}

.reconcile-footer {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-top: 12px;
  color: #64748b;
  font-size: 13px;
  line-height: 1.6;
}

@media (max-width: 900px) {
  .page-hero {
    align-items: flex-start;
    flex-direction: column;
  }

  .ops-guide {
    grid-template-columns: 1fr;
  }

  .table-note,
  .detail-status-card,
  .reconcile-title,
  .reconcile-footer {
    flex-direction: column;
    align-items: flex-start;
  }

  .detail-tags {
    justify-content: flex-start;
  }
}
</style>
