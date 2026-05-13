<template>
  <MainLayout>
    <div class="campus-admin-page exception-ops">
      <section class="page-hero">
        <div>
          <span class="eyebrow">Campus Ops</span>
          <h2>校园异常处理</h2>
          <p>监控并处理校园代送订单异常，包括超时、取消、配送失败等情况。</p>
        </div>
        <div class="hero-notes">
          <span>运营模块</span>
          <strong>exceptions</strong>
        </div>
      </section>

      <el-alert
        title="该页只接入异常历史列表、详情和 resolve 三个既有接口；不会修改订单主状态、settlement 或 latest exception 摘要。"
        type="info"
        :closable="false"
        show-icon
        class="page-alert"
      />

      <section class="ops-guide">
        <div class="guide-card">
          <span class="guide-kicker">列表</span>
          <strong>历史留痕</strong>
          <p>按订单号、配送员和处理状态查看每次异常上报记录。</p>
        </div>
        <div class="guide-card">
          <span class="guide-kicker">详情</span>
          <strong>关联回读</strong>
          <p>drawer 中展示订单、配送员、latest 摘要和处理字段。</p>
        </div>
        <div class="guide-card">
          <span class="guide-kicker">处理</span>
          <strong>最小 resolve</strong>
          <p>仅对 REPORTED 记录写入处理结果和备注，不做 reopen 或删除。</p>
        </div>
      </section>

      <section class="filter-card">
        <div class="panel-header">
          <div>
            <span class="section-kicker">筛选条件</span>
            <h3>按条件定位异常记录</h3>
            <p>筛选只影响异常历史读取，不触发任何处理动作。</p>
          </div>
        </div>
        <el-form :inline="true" class="filter-form">
          <el-form-item label="订单号">
            <el-input v-model="filters.relayOrderId" placeholder="按订单号筛选" clearable style="width: 220px" />
          </el-form-item>
          <el-form-item label="配送员ID">
            <el-input v-model="filters.courierProfileId" placeholder="按配送员ID筛选" clearable style="width: 180px" />
          </el-form-item>
          <el-form-item label="处理状态">
            <el-select v-model="filters.processStatus" placeholder="全部" clearable style="width: 180px">
              <el-option label="已上报" value="REPORTED" />
              <el-option label="已处理" value="RESOLVED" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" class="primary-btn" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </section>

      <section class="table-card">
        <div class="table-heading">
          <div>
            <span class="section-kicker">异常历史</span>
            <h3>异常历史列表</h3>
          </div>
        </div>

        <div class="table-note">
          <strong>处理边界</strong>
          <span>resolve 只更新异常历史处理字段，不清空订单 latest exception 摘要，也不推进订单主状态。</span>
        </div>

        <el-table
          v-loading="listLoading"
          :data="records"
          class="campus-table"
          empty-text="当前筛选条件下暂无异常历史记录，可调整订单号、配送员或处理状态后重新查询"
        >
          <el-table-column prop="id" label="异常ID" width="96" align="center" />
          <el-table-column prop="relayOrderId" label="订单号" min-width="200" />
          <el-table-column prop="orderStatus" label="订单状态" width="140" />
          <el-table-column prop="courierProfileId" label="配送员ID" width="110" align="center" />
          <el-table-column prop="courierName" label="配送员" width="120">
            <template #default="{ row }">
              {{ displayText(row.courierName) }}
            </template>
          </el-table-column>
          <el-table-column label="异常类型" width="140">
            <template #default="{ row }">
              <el-tag type="warning">{{ exceptionTypeText(row.exceptionType) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="异常说明" min-width="220" show-overflow-tooltip>
            <template #default="{ row }">
              {{ displayText(row.exceptionRemark) }}
            </template>
          </el-table-column>
          <el-table-column label="上报时间" min-width="170">
            <template #default="{ row }">
              {{ formatDateTime(row.reportedAt) }}
            </template>
          </el-table-column>
          <el-table-column label="处理状态" width="120" align="center">
            <template #default="{ row }">
              <el-tag :type="processStatusTagType(row.processStatus)">
                {{ processStatusText(row.processStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="处理结果" width="140">
            <template #default="{ row }">
              {{ processResultText(row.processResult) }}
            </template>
          </el-table-column>
          <el-table-column label="处理时间" min-width="170">
            <template #default="{ row }">
              {{ formatDateTime(row.processedAt) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button type="primary" link @click="openDetail(row.id)">查看详情</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-section">
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

      <el-drawer v-model="detailVisible" size="760px" title="异常历史详情" destroy-on-close>
        <div v-loading="detailLoading" class="detail-wrapper">
          <template v-if="detail">
            <div class="detail-status-card">
              <div>
                <span>当前异常记录</span>
                <strong>{{ detail.relayOrderId }}</strong>
              </div>
              <div class="detail-tags">
                <el-tag type="warning">{{ exceptionTypeText(detail.exceptionType) }}</el-tag>
                <el-tag :type="processStatusTagType(detail.processStatus)">
                  {{ processStatusText(detail.processStatus) }}
                </el-tag>
                <el-tag type="info">{{ detail.orderStatus || '暂无订单状态' }}</el-tag>
              </div>
            </div>

            <div class="detail-section">
              <div class="detail-section-header">
                <span class="section-kicker">异常与订单摘要</span>
                <p>以下字段来自 `GET /api/campus/admin/exceptions/{id}`，用于回读历史记录和订单 latest 摘要。</p>
              </div>
              <el-descriptions :column="2" border>
                <el-descriptions-item label="异常ID">{{ detail.id }}</el-descriptions-item>
                <el-descriptions-item label="订单号">{{ detail.relayOrderId }}</el-descriptions-item>
                <el-descriptions-item label="订单状态">{{ displayText(detail.orderStatus) }}</el-descriptions-item>
                <el-descriptions-item label="用户ID">{{ displayText(detail.customerUserId) }}</el-descriptions-item>
                <el-descriptions-item label="配送员ID">{{ displayText(detail.courierProfileId) }}</el-descriptions-item>
                <el-descriptions-item label="配送员姓名">{{ displayText(detail.courierName) }}</el-descriptions-item>
                <el-descriptions-item label="异常类型">{{ exceptionTypeText(detail.exceptionType) }}</el-descriptions-item>
                <el-descriptions-item label="上报时间">{{ formatDateTime(detail.reportedAt) }}</el-descriptions-item>
                <el-descriptions-item label="异常说明" :span="2">{{ displayText(detail.exceptionRemark) }}</el-descriptions-item>
                <el-descriptions-item label="来源">{{ displayText(detail.source) }}</el-descriptions-item>
                <el-descriptions-item label="创建时间">{{ formatDateTime(detail.createdAt) }}</el-descriptions-item>
                <el-descriptions-item label="更新时间">{{ formatDateTime(detail.updatedAt) }}</el-descriptions-item>
              </el-descriptions>
            </div>

            <div class="detail-section">
              <div class="detail-section-header">
                <span class="section-kicker">latest exception 兼容摘要</span>
                <p>订单表上的 latest 摘要继续保留，旧页面仍可按最近异常语义读取。</p>
              </div>
              <el-descriptions :column="2" border>
                <el-descriptions-item label="最近异常类型">{{ exceptionTypeText(detail.latestExceptionType) }}</el-descriptions-item>
                <el-descriptions-item label="最近异常时间">{{ formatDateTime(detail.latestExceptionReportedAt) }}</el-descriptions-item>
                <el-descriptions-item label="最近异常说明" :span="2">{{ displayText(detail.latestExceptionRemark) }}</el-descriptions-item>
              </el-descriptions>
            </div>

            <div class="detail-section">
              <div class="detail-section-header">
                <span class="section-kicker">处理状态</span>
                <p>处理字段只属于异常历史记录，不反向修改订单主状态、结算或 latest 摘要。</p>
              </div>
              <el-descriptions :column="2" border>
                <el-descriptions-item label="处理状态">{{ processStatusText(detail.processStatus) }}</el-descriptions-item>
                <el-descriptions-item label="处理结果">{{ processResultText(detail.processResult) }}</el-descriptions-item>
                <el-descriptions-item label="处理人ID">{{ displayText(detail.processedByEmployeeId) }}</el-descriptions-item>
                <el-descriptions-item label="处理时间">{{ formatDateTime(detail.processedAt) }}</el-descriptions-item>
                <el-descriptions-item label="处理备注" :span="2">{{ displayText(detail.adminNote) }}</el-descriptions-item>
              </el-descriptions>
            </div>

            <div v-if="detail.processStatus === 'REPORTED'" class="resolve-card">
              <div class="resolve-header">
                <div>
                  <span class="section-kicker">最小 resolve</span>
                  <h3>处理异常记录</h3>
                  <p>只允许将 REPORTED 标记为 RESOLVED，不做 reopen、delete、通知或订单状态推进。</p>
                </div>
              </div>
              <el-form label-position="top">
                <el-form-item label="处理结果">
                  <el-select v-model="resolveForm.processResult" placeholder="请选择处理结果" style="width: 100%">
                    <el-option label="已处理" value="HANDLED" />
                    <el-option label="标记无效" value="MARKED_INVALID" />
                    <el-option label="已跟进" value="FOLLOWED_UP" />
                  </el-select>
                </el-form-item>
                <el-form-item label="处理备注">
                  <el-input
                    v-model="resolveForm.adminNote"
                    type="textarea"
                    :rows="4"
                    maxlength="200"
                    show-word-limit
                    placeholder="填写本次处理说明，便于后续回读"
                  />
                </el-form-item>
                <el-button type="primary" class="primary-btn" :loading="resolveLoading" @click="handleResolve">
                  标记为已处理
                </el-button>
              </el-form>
            </div>

            <div v-else class="resolved-note">
              <strong>已处理</strong>
              <span>该记录已进入 RESOLVED，不允许重复处理；如需复核，请在后续工单方案中设计 reopen，而不是在本页扩展。</span>
            </div>
          </template>
          <el-empty v-else-if="!detailLoading" description="请选择一条异常记录查看详情" />
        </div>
      </el-drawer>
    </div>
  </MainLayout>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import MainLayout from '../layout/MainLayout.vue'
import {
  getCampusExceptionDetail,
  getCampusExceptionRecords,
  resolveCampusException
} from '../api/campus-admin'

const listLoading = ref(false)
const detailLoading = ref(false)
const detailVisible = ref(false)
const resolveLoading = ref(false)
const records = ref([])
const detail = ref(null)

const filters = reactive({
  relayOrderId: '',
  courierProfileId: '',
  processStatus: ''
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const resolveForm = reactive({
  processResult: 'HANDLED',
  adminNote: ''
})

const displayText = (value) => (value === null || value === undefined || value === '' ? '暂无' : value)
const formatDateTime = (value) => value || '暂无'

const exceptionTypeText = (type) => ({
  DELIVERY_BLOCKED: '配送受阻',
  CONTACT_FAILED: '联系失败',
  ITEM_MISSING: '餐品缺失',
  ADDRESS_ISSUE: '地址异常',
  OTHER: '其他异常'
}[type] || displayText(type))

const processStatusText = (status) => ({
  REPORTED: '已上报',
  RESOLVED: '已处理'
}[status] || displayText(status))

const processStatusTagType = (status) => ({
  REPORTED: 'warning',
  RESOLVED: 'success'
}[status] || 'info')

const processResultText = (result) => ({
  HANDLED: '已处理',
  MARKED_INVALID: '标记无效',
  FOLLOWED_UP: '已跟进'
}[result] || displayText(result))

const buildQueryParams = () => ({
  relayOrderId: filters.relayOrderId || undefined,
  courierProfileId: filters.courierProfileId || undefined,
  processStatus: filters.processStatus || undefined
})

const loadExceptions = async () => {
  listLoading.value = true
  try {
    const response = await getCampusExceptionRecords({
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

const resetResolveForm = () => {
  resolveForm.processResult = 'HANDLED'
  resolveForm.adminNote = ''
}

const openDetail = async (id) => {
  detailVisible.value = true
  detailLoading.value = true
  detail.value = null
  resetResolveForm()
  try {
    detail.value = await getCampusExceptionDetail(id)
  } finally {
    detailLoading.value = false
  }
}

const refreshDetail = async () => {
  if (!detail.value?.id) return
  detail.value = await getCampusExceptionDetail(detail.value.id)
}

const handleResolve = async () => {
  if (!detail.value?.id || detail.value.processStatus !== 'REPORTED') {
    return
  }
  if (!resolveForm.adminNote.trim()) {
    ElMessage.warning('请填写处理备注')
    return
  }

  try {
    await ElMessageBox.confirm(
      '确认将该异常记录标记为已处理？该动作不会修改订单主状态或结算记录。',
      '确认处理异常',
      { type: 'warning' }
    )
  } catch {
    return
  }

  resolveLoading.value = true
  try {
    await resolveCampusException(detail.value.id, {
      processResult: resolveForm.processResult,
      adminNote: resolveForm.adminNote.trim()
    })
    ElMessage.success('异常记录已处理')
    await Promise.all([refreshDetail(), loadExceptions()])
    resetResolveForm()
  } finally {
    resolveLoading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadExceptions()
}

const handleReset = () => {
  filters.relayOrderId = ''
  filters.courierProfileId = ''
  filters.processStatus = ''
  pagination.page = 1
  loadExceptions()
}

const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.page = 1
  loadExceptions()
}

const handleCurrentChange = (page) => {
  pagination.page = page
  loadExceptions()
}

onMounted(() => loadExceptions())
</script>

<style lang="scss" scoped>
.exception-ops {
  display: flex;
  flex-direction: column;
  gap: 22px;
  padding: 20px;

  /* ====== Hero ====== */
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

  /* ====== Shared typography tokens ====== */
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

  /* ====== Page alert ====== */
  .page-alert {
    border-radius: 16px;
  }

  /* ====== Ops guide cards ====== */
  .ops-guide {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 16px;
  }

  .guide-card {
    position: relative;
    overflow: hidden;
    border: 1px solid rgba(15, 118, 110, 0.1);
    border-radius: 24px;
    background: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(14px);
    padding: 22px 24px;
    box-shadow: 0 18px 42px rgba(15, 23, 42, 0.08);

    .guide-kicker {
      display: inline-flex;
      align-items: center;
      border-radius: 999px;
      padding: 4px 12px;
      background: #f0fdfa;
      color: #0f766e;
      font-size: 12px;
      font-weight: 800;
      letter-spacing: 0.06em;
      margin-bottom: 10px;
      border: 1px solid rgba(15, 118, 110, 0.14);
    }

    strong {
      display: block;
      margin-bottom: 8px;
      color: #0f172a;
      font-size: 16px;
      font-weight: 900;
    }

    p {
      margin: 0;
      color: #64748b;
      font-size: 13px;
      line-height: 1.7;
    }
  }

  /* ====== Shared card base ====== */
  .filter-card,
  .table-card {
    border: 1px solid rgba(15, 118, 110, 0.1);
    border-radius: 24px;
    background: rgba(255, 255, 255, 0.9);
    box-shadow: 0 18px 42px rgba(15, 23, 42, 0.08);
    padding: 22px 24px;
  }

  /* ====== Filter card ====== */
  .panel-header {
    margin-bottom: 16px;

    h3 {
      margin: 6px 0 6px;
      color: #0f172a;
      font-size: 18px;
      font-weight: 900;
    }

    p {
      margin: 0;
      color: #64748b;
      font-size: 13px;
    }
  }

  .filter-form {
    margin-bottom: -18px;
  }

  .primary-btn {
    border: none;
    background: linear-gradient(135deg, #0f766e, #0ea5e9);
    box-shadow: 0 12px 24px rgba(14, 165, 233, 0.22);
  }

  /* ====== Table card ====== */
  .table-heading {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 16px;
    margin-bottom: 16px;

    h3 {
      margin: 6px 0 0;
      color: #0f172a;
      font-size: 20px;
      font-weight: 900;
    }
  }

  .table-note {
    display: flex;
    align-items: center;
    gap: 10px;
    border-radius: 14px;
    background: #f0fdfa;
    color: #64748b;
    padding: 12px 16px;
    margin-bottom: 14px;
    line-height: 1.6;
    border: 1px solid rgba(15, 118, 110, 0.1);

    strong {
      color: #0f766e;
      white-space: nowrap;
      font-weight: 800;
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

  .pagination-section {
    padding: 18px 0 0;
    display: flex;
    justify-content: flex-end;
  }

  /* ====== Drawer detail ====== */
  .detail-wrapper {
    padding: 0 8px 0 0;
  }

  .detail-status-card {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 14px;
    border-radius: 22px;
    background:
      radial-gradient(circle at 90% 30%, rgba(14, 165, 233, 0.12), transparent 40%),
      linear-gradient(135deg, #f0fdfa 0%, #ecfdf5 50%, #e0f2fe 100%);
    border: 1px solid rgba(15, 118, 110, 0.12);
    padding: 18px 22px;
    margin-bottom: 20px;

    span {
      display: block;
      color: #64748b;
      font-size: 13px;
      margin-bottom: 6px;
    }

    strong {
      color: #0f172a;
      font-size: 16px;
    }
  }

  .detail-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    justify-content: flex-end;
  }

  .detail-section {
    margin-bottom: 20px;
    border: 1px solid rgba(15, 118, 110, 0.1);
    border-radius: 20px;
    background: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(14px);
    padding: 20px 22px;
    box-shadow: 0 8px 24px rgba(15, 23, 42, 0.05);
  }

  .detail-section-header {
    margin-bottom: 14px;

    p {
      margin: 6px 0 0;
      color: #64748b;
      font-size: 13px;
      line-height: 1.6;
    }
  }

  /* ====== Resolve card ====== */
  .resolve-card {
    border: 1px solid rgba(15, 118, 110, 0.14);
    border-radius: 22px;
    padding: 22px 24px;
    background:
      radial-gradient(circle at 80% 20%, rgba(14, 165, 233, 0.08), transparent 40%),
      linear-gradient(135deg, #f0fdfa 0%, #ecfdf5 60%, #e0f2fe 100%);
    margin-bottom: 20px;

    .resolve-header {
      margin-bottom: 16px;

      h3 {
        margin: 6px 0 6px;
        color: #0f172a;
        font-size: 18px;
        font-weight: 900;
      }

      p {
        margin: 0;
        color: #64748b;
        font-size: 13px;
        line-height: 1.6;
      }
    }
  }

  /* ====== Resolved note ====== */
  .resolved-note {
    display: flex;
    align-items: center;
    gap: 10px;
    border-radius: 14px;
    background: #f0fdfa;
    color: #64748b;
    padding: 14px 18px;
    margin-bottom: 12px;
    line-height: 1.6;
    border: 1px solid rgba(15, 118, 110, 0.1);

    strong {
      color: #0f766e;
      white-space: nowrap;
      font-weight: 800;
    }
  }
}

/* ====== Responsive ====== */
@media (max-width: 900px) {
  .exception-ops {
    .page-hero {
      align-items: flex-start;
      flex-direction: column;
    }

    .ops-guide {
      grid-template-columns: 1fr;
    }

    .table-note,
    .resolved-note,
    .detail-status-card {
      flex-direction: column;
      align-items: flex-start;
    }

    .detail-tags {
      justify-content: flex-start;
    }
  }
}
</style>
