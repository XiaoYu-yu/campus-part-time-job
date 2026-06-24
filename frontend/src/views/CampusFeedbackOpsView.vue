<template>
  <MainLayout>
    <div class="feedback-ops">
      <section class="page-hero">
        <div>
          <span class="eyebrow">Feedback Ops</span>
          <h2>用户反馈</h2>
          <p>集中查看用户端和兼职端提交的问题、建议与订单反馈，并留下处理进度。</p>
        </div>
        <div class="hero-stat">
          <span>当前筛选</span>
          <strong>{{ pagination.total }}</strong>
          <small>条反馈</small>
        </div>
      </section>

      <section class="filter-card">
        <div class="section-heading">
          <div>
            <span class="section-kicker">筛选条件</span>
            <h3>定位需要处理的反馈</h3>
          </div>
        </div>
        <el-form :inline="true" class="filter-form">
          <el-form-item label="来源角色">
            <el-select v-model="filters.submitterRole" clearable placeholder="全部" style="width: 150px">
              <el-option label="用户端" value="USER" />
              <el-option label="兼职端" value="PARTTIME" />
            </el-select>
          </el-form-item>
          <el-form-item label="反馈类型">
            <el-select v-model="filters.category" clearable placeholder="全部" style="width: 160px">
              <el-option label="功能异常" value="BUG" />
              <el-option label="体验建议" value="SUGGESTION" />
              <el-option label="订单问题" value="ORDER" />
              <el-option label="账号问题" value="ACCOUNT" />
              <el-option label="其他" value="OTHER" />
            </el-select>
          </el-form-item>
          <el-form-item label="处理状态">
            <el-select v-model="filters.status" clearable placeholder="全部" style="width: 160px">
              <el-option label="待处理" value="PENDING" />
              <el-option label="处理中" value="IN_PROGRESS" />
              <el-option label="已完成" value="RESOLVED" />
            </el-select>
          </el-form-item>
          <el-form-item label="订单号">
            <el-input v-model="filters.orderId" clearable placeholder="关联订单号" style="width: 220px" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </section>

      <section class="table-card">
        <div class="section-heading">
          <div>
            <span class="section-kicker">反馈队列</span>
            <h3>提交记录</h3>
          </div>
          <el-button :loading="loading" @click="loadFeedback">刷新</el-button>
        </div>

        <el-table
          v-loading="loading"
          :data="records"
          class="feedback-table"
          empty-text="当前筛选条件下暂无反馈"
        >
          <el-table-column prop="id" label="ID" width="80" align="center" />
          <el-table-column label="来源" width="110">
            <template #default="{ row }">{{ roleText(row.submitterRole) }}</template>
          </el-table-column>
          <el-table-column label="类型" width="120">
            <template #default="{ row }">
              <el-tag type="info">{{ categoryText(row.category) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="反馈内容" min-width="280" show-overflow-tooltip>
            <template #default="{ row }">{{ displayText(row.content) }}</template>
          </el-table-column>
          <el-table-column label="关联订单" min-width="180">
            <template #default="{ row }">{{ displayText(row.orderId) }}</template>
          </el-table-column>
          <el-table-column label="状态" width="110" align="center">
            <template #default="{ row }">
              <el-tag :type="statusTagType(row.status)">{{ statusText(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="提交时间" min-width="170">
            <template #default="{ row }">{{ formatDateTime(row.createdAt) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="110">
            <template #default="{ row }">
              <el-button type="primary" link @click="openDetail(row.id)">查看处理</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination">
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

      <el-drawer v-model="detailVisible" title="反馈详情" size="680px" destroy-on-close>
        <div v-loading="detailLoading" class="detail-wrapper">
          <template v-if="detail">
            <div class="detail-summary">
              <div>
                <span>反馈 #{{ detail.id }}</span>
                <strong>{{ categoryText(detail.category) }}</strong>
              </div>
              <el-tag :type="statusTagType(detail.status)">{{ statusText(detail.status) }}</el-tag>
            </div>

            <el-descriptions :column="2" border>
              <el-descriptions-item label="来源角色">{{ roleText(detail.submitterRole) }}</el-descriptions-item>
              <el-descriptions-item label="反馈类型">{{ categoryText(detail.category) }}</el-descriptions-item>
              <el-descriptions-item label="联系方式">{{ displayText(detail.contact) }}</el-descriptions-item>
              <el-descriptions-item label="关联订单">{{ displayText(detail.orderId) }}</el-descriptions-item>
              <el-descriptions-item label="来源页面" :span="2">{{ displayText(detail.page) }}</el-descriptions-item>
              <el-descriptions-item label="反馈内容" :span="2">{{ displayText(detail.content) }}</el-descriptions-item>
              <el-descriptions-item label="提交时间">{{ formatDateTime(detail.createdAt) }}</el-descriptions-item>
              <el-descriptions-item label="更新时间">{{ formatDateTime(detail.updatedAt) }}</el-descriptions-item>
              <el-descriptions-item label="处理人ID">{{ displayText(detail.processedByEmployeeId) }}</el-descriptions-item>
              <el-descriptions-item label="处理时间">{{ formatDateTime(detail.processedAt) }}</el-descriptions-item>
              <el-descriptions-item label="处理备注" :span="2">{{ displayText(detail.adminNote) }}</el-descriptions-item>
            </el-descriptions>

            <div v-if="detail.status !== 'RESOLVED'" class="process-card">
              <span class="section-kicker">运营处理</span>
              <h3>更新反馈进度</h3>
              <p>处理中表示已接手；已完成会关闭该反馈，关闭后不允许重复修改。</p>
              <el-form label-position="top">
                <el-form-item label="目标状态">
                  <el-select v-model="processForm.status" style="width: 100%">
                    <el-option label="处理中" value="IN_PROGRESS" :disabled="detail.status === 'IN_PROGRESS'" />
                    <el-option label="已完成" value="RESOLVED" />
                  </el-select>
                </el-form-item>
                <el-form-item label="处理备注">
                  <el-input
                    v-model="processForm.adminNote"
                    type="textarea"
                    :rows="4"
                    maxlength="500"
                    show-word-limit
                    placeholder="记录排查结果、处理动作或后续安排"
                  />
                </el-form-item>
                <el-button type="primary" :loading="processLoading" @click="handleProcess">
                  保存处理结果
                </el-button>
              </el-form>
            </div>

            <el-alert
              v-else
              title="该反馈已处理完成。如需继续跟进，请创建新的内部任务，不在此处覆盖历史结果。"
              type="success"
              :closable="false"
              show-icon
              class="resolved-alert"
            />
          </template>
          <el-empty v-else-if="!detailLoading" description="请选择一条反馈查看详情" />
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
  getCampusFeedbackDetail,
  getCampusFeedbackRecords,
  processCampusFeedback
} from '../api/campus-admin'

const loading = ref(false)
const detailLoading = ref(false)
const processLoading = ref(false)
const detailVisible = ref(false)
const records = ref([])
const detail = ref(null)

const filters = reactive({
  submitterRole: '',
  category: '',
  status: '',
  orderId: ''
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const processForm = reactive({
  status: 'IN_PROGRESS',
  adminNote: ''
})

const displayText = (value) => (value === null || value === undefined || value === '' ? '暂无' : value)
const formatDateTime = (value) => value || '暂无'

const roleText = (role) => ({
  USER: '用户端',
  PARTTIME: '兼职端'
}[role] || displayText(role))

const categoryText = (category) => ({
  BUG: '功能异常',
  SUGGESTION: '体验建议',
  ORDER: '订单问题',
  ACCOUNT: '账号问题',
  OTHER: '其他'
}[category] || displayText(category))

const statusText = (status) => ({
  PENDING: '待处理',
  IN_PROGRESS: '处理中',
  RESOLVED: '已完成'
}[status] || displayText(status))

const statusTagType = (status) => ({
  PENDING: 'warning',
  IN_PROGRESS: 'primary',
  RESOLVED: 'success'
}[status] || 'info')

const buildQueryParams = () => ({
  submitterRole: filters.submitterRole || undefined,
  category: filters.category || undefined,
  status: filters.status || undefined,
  orderId: filters.orderId.trim() || undefined
})

const loadFeedback = async () => {
  loading.value = true
  try {
    const response = await getCampusFeedbackRecords({
      page: pagination.page,
      pageSize: pagination.pageSize,
      ...buildQueryParams()
    })
    records.value = response.records || []
    pagination.total = response.total || 0
  } finally {
    loading.value = false
  }
}

const resetProcessForm = (status = 'PENDING') => {
  processForm.status = status === 'IN_PROGRESS' ? 'RESOLVED' : 'IN_PROGRESS'
  processForm.adminNote = ''
}

const openDetail = async (id) => {
  detailVisible.value = true
  detailLoading.value = true
  detail.value = null
  try {
    detail.value = await getCampusFeedbackDetail(id)
    resetProcessForm(detail.value.status)
  } finally {
    detailLoading.value = false
  }
}

const refreshDetail = async () => {
  if (!detail.value?.id) return
  detail.value = await getCampusFeedbackDetail(detail.value.id)
  resetProcessForm(detail.value.status)
}

const handleProcess = async () => {
  if (!detail.value?.id || detail.value.status === 'RESOLVED') return
  if (!processForm.adminNote.trim()) {
    ElMessage.warning('请填写处理备注')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确认将反馈更新为“${statusText(processForm.status)}”？`,
      '确认处理反馈',
      { type: 'warning' }
    )
  } catch {
    return
  }

  processLoading.value = true
  try {
    await processCampusFeedback(detail.value.id, {
      status: processForm.status,
      adminNote: processForm.adminNote.trim()
    })
    ElMessage.success('反馈处理状态已更新')
    await Promise.all([refreshDetail(), loadFeedback()])
  } finally {
    processLoading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadFeedback()
}

const handleReset = () => {
  filters.submitterRole = ''
  filters.category = ''
  filters.status = ''
  filters.orderId = ''
  pagination.page = 1
  loadFeedback()
}

const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.page = 1
  loadFeedback()
}

const handleCurrentChange = (page) => {
  pagination.page = page
  loadFeedback()
}

onMounted(loadFeedback)
</script>

<style scoped lang="scss">
.feedback-ops {
  display: flex;
  flex-direction: column;
  gap: 22px;
  padding: 20px;
}

.page-hero,
.filter-card,
.table-card {
  border: 1px solid rgba(15, 118, 110, 0.12);
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.92);
  box-shadow: 0 18px 42px rgba(15, 23, 42, 0.08);
}

.page-hero {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 24px;
  min-height: 160px;
  padding: 32px 38px;
  background:
    radial-gradient(circle at 88% 16%, rgba(14, 165, 233, 0.2), transparent 28%),
    linear-gradient(135deg, #ffffff 0%, #f0fdfa 56%, #ecfeff 100%);

  h2 {
    margin: 8px 0 10px;
    color: #0f172a;
    font-size: 34px;
  }

  p {
    max-width: 620px;
    margin: 0;
    color: #64748b;
    line-height: 1.8;
  }
}

.eyebrow,
.section-kicker {
  color: #0f766e;
  font-size: 12px;
  font-weight: 900;
  letter-spacing: 0.14em;
  text-transform: uppercase;
}

.hero-stat {
  min-width: 130px;
  padding: 16px 20px;
  border: 1px solid rgba(15, 118, 110, 0.12);
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.8);

  span,
  small {
    display: block;
    color: #64748b;
  }

  strong {
    color: #0f766e;
    font-size: 30px;
  }
}

.filter-card,
.table-card {
  padding: 22px 24px;
}

.section-heading {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;

  h3 {
    margin: 5px 0 0;
    color: #0f172a;
    font-size: 18px;
  }
}

.filter-form {
  margin-bottom: -18px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.detail-wrapper {
  padding-right: 8px;
}

.detail-summary {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 18px;
  padding: 16px 18px;
  border: 1px solid rgba(15, 118, 110, 0.12);
  border-radius: 18px;
  background: #f0fdfa;

  span {
    display: block;
    margin-bottom: 4px;
    color: #64748b;
    font-size: 13px;
  }

  strong {
    color: #0f172a;
    font-size: 18px;
  }
}

.process-card {
  margin-top: 22px;
  padding: 20px;
  border: 1px solid rgba(15, 118, 110, 0.14);
  border-radius: 18px;
  background: linear-gradient(180deg, #f0fdfa 0%, #ffffff 100%);

  h3 {
    margin: 6px 0;
    color: #0f172a;
  }

  p {
    margin: 0 0 16px;
    color: #64748b;
    line-height: 1.6;
  }
}

.resolved-alert {
  margin-top: 22px;
}

@media (max-width: 900px) {
  .page-hero {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
