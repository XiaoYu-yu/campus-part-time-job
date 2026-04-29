<template>
  <MainLayout>
    <div class="campus-admin-page">
      <div class="page-header">
        <div>
          <div class="title-row">
            <h2>校园异常处理</h2>
            <span class="ops-badge">最小处理闭环</span>
          </div>
          <p>承接 courier 异常上报历史，支持 admin 只读查看与最小 resolve，不扩完整工单系统。</p>
        </div>
      </div>

      <el-alert
        title="该页只接入异常历史列表、详情和 resolve 三个既有接口；不会修改订单主状态、settlement 或 latest exception 摘要。"
        type="info"
        :closable="false"
        show-icon
        class="page-alert"
      />

      <section class="ops-guide">
        <div class="guide-item">
          <span>列表</span>
          <strong>历史留痕</strong>
          <p>按订单号、配送员和处理状态查看每次异常上报记录。</p>
        </div>
        <div class="guide-item">
          <span>详情</span>
          <strong>关联回读</strong>
          <p>drawer 中展示订单、配送员、latest 摘要和处理字段。</p>
        </div>
        <div class="guide-item">
          <span>处理</span>
          <strong>最小 resolve</strong>
          <p>仅对 REPORTED 记录写入处理结果和备注，不做 reopen 或删除。</p>
        </div>
      </section>

      <section class="filter-card">
        <div class="panel-header compact-header">
          <div>
            <h3>筛选条件</h3>
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
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </section>

      <section class="table-card">
        <div class="panel-header table-header">
          <div>
            <h3>异常历史列表</h3>
            <p>列表区调用 `GET /api/campus/admin/exceptions`，默认按上报时间倒序查看。</p>
          </div>
        </div>

        <div class="table-note">
          <strong>处理边界</strong>
          <span>resolve 只更新异常历史处理字段，不清空订单 latest exception 摘要，也不推进订单主状态。</span>
        </div>

        <el-table
          v-loading="listLoading"
          :data="records"
          border
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

            <div class="detail-section-title">
              <h3>异常与订单摘要</h3>
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

            <div class="detail-section">
              <h3>latest exception 兼容摘要</h3>
              <p>订单表上的 latest 摘要继续保留，旧页面仍可按最近异常语义读取。</p>
              <el-descriptions :column="2" border>
                <el-descriptions-item label="最近异常类型">{{ exceptionTypeText(detail.latestExceptionType) }}</el-descriptions-item>
                <el-descriptions-item label="最近异常时间">{{ formatDateTime(detail.latestExceptionReportedAt) }}</el-descriptions-item>
                <el-descriptions-item label="最近异常说明" :span="2">{{ displayText(detail.latestExceptionRemark) }}</el-descriptions-item>
              </el-descriptions>
            </div>

            <div class="detail-section">
              <h3>处理状态</h3>
              <p>处理字段只属于异常历史记录，不反向修改订单主状态、结算或 latest 摘要。</p>
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
                  <h3>最小 resolve</h3>
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
                <el-button type="primary" :loading="resolveLoading" @click="handleResolve">
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

.ops-badge {
  display: inline-flex;
  align-items: center;
  border-radius: 999px;
  padding: 4px 10px;
  background: #fff7ed;
  color: #c2410c;
  font-size: 12px;
  font-weight: 600;
}

.page-alert {
  margin-bottom: 16px;
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

.compact-header {
  margin-bottom: 12px;
}

.filter-form {
  margin-bottom: -18px;
}

.table-note,
.resolved-note {
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
  background: linear-gradient(180deg, #fff7ed 0%, #fff1f2 100%);
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

.detail-section-title,
.detail-section,
.resolve-card {
  margin-bottom: 16px;

  h3 {
    margin: 0 0 6px;
    font-size: 16px;
    color: #18181b;
  }

  p {
    margin: 0 0 12px;
    color: #71717a;
    font-size: 13px;
    line-height: 1.6;
  }
}

.resolve-card {
  border-radius: 14px;
  padding: 16px;
  border: 1px solid #fed7aa;
  background: #fff7ed;
}

.resolve-header {
  margin-bottom: 12px;
}

@media (max-width: 900px) {
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
</style>
