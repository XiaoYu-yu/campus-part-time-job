<template>
  <UserLayout>
    <div class="workbench-page">
      <section class="card hero-card">
        <div>
          <div class="title-row">
            <h2>兼职工作台</h2>
            <span class="readonly-badge">最小承接</span>
          </div>
          <p>这是兼职端独立登录后的最小承接页。当前已补到接单、详情、取餐、送达和异常上报的最小闭环，但仍不扩完整兼职主工作流。</p>
        </div>
        <div class="token-pill" :class="hasCourierToken ? 'active' : 'inactive'">
          {{ hasCourierToken ? '兼职 token 已就绪' : '兼职 token 缺失' }}
        </div>
      </section>

      <el-alert
        :title="hasCourierToken ? '当前浏览器已检测到兼职 token，可继续查看兼职侧只读承接信息。' : '当前浏览器未检测到兼职 token，请先登录兼职端或返回入驻入口申请。'"
        :description="hasCourierToken ? '页面会自动读取兼职资料、审核状态和可接单预览。' : '没有兼职 token 时，本页不会调用 courier 业务接口，只展示回退入口。'"
        :type="hasCourierToken ? 'success' : 'warning'"
        :closable="false"
        show-icon
        class="page-alert"
      />

      <template v-if="hasCourierToken">
        <section class="flow-strip">
          <div class="flow-step">
            <span>身份</span>
            <strong>profile / review-status</strong>
          </div>
          <div class="flow-step">
            <span>订单</span>
            <strong>可接单预览 / 详情</strong>
          </div>
          <div class="flow-step">
            <span>动作</span>
            <strong>接单 / 取餐 / 送达</strong>
          </div>
          <div class="flow-step">
            <span>结果</span>
            <strong>异常 / 确认 / 完成</strong>
          </div>
        </section>

        <section class="card">
          <div class="section-header">
            <div>
              <h3>身份状态卡</h3>
              <p>优先展示本地 courier_profile 摘要，并在页面加载时刷新最新 profile / review-status。</p>
            </div>
            <el-button type="primary" plain :loading="loading" @click="loadWorkbench">刷新工作台</el-button>
          </div>

          <div class="summary-grid">
            <div class="summary-item">
              <span>courierProfileId</span>
              <strong>{{ displayText(profile.id) }}</strong>
            </div>
            <div class="summary-item">
              <span>真实姓名</span>
              <strong>{{ displayText(profile.realName) }}</strong>
            </div>
            <div class="summary-item">
              <span>审核状态</span>
              <strong>{{ displayText(reviewStatus.reviewStatus || profile.reviewStatus) }}</strong>
            </div>
            <div class="summary-item">
              <span>启用状态</span>
              <strong>{{ Number(reviewStatus.enabled ?? profile.enabled) === 1 ? '已启用' : '未启用' }}</strong>
            </div>
            <div class="summary-item">
              <span>审核说明</span>
              <strong>{{ displayText(reviewStatus.reviewComment || profile.reviewComment) }}</strong>
            </div>
            <div class="summary-item">
              <span>token 状态</span>
              <strong>{{ tokenPreview }}</strong>
            </div>
          </div>
        </section>

        <section class="card">
          <div class="section-header">
            <div>
              <h3>可接单预览</h3>
              <p>调用 `GET /api/campus/courier/orders/available`，本轮补一个最小接单动作，成功后直接刷新当前预览列表。</p>
            </div>
            <el-button text type="primary" :loading="loading" @click="loadWorkbench">刷新列表</el-button>
          </div>

          <div class="table-note">
            <strong>演示提示</strong>
            <span>列表只展示当前可接单记录；为空时不代表链路失败，可通过下方订单号回读入口查看已完成样本。</span>
          </div>

          <el-table
            v-loading="loading"
            :data="availableOrders"
            border
            empty-text="当前没有可接单记录，可能是暂时无单或当前筛选结果为空"
          >
            <el-table-column prop="id" label="订单号" min-width="180" />
            <el-table-column prop="pickupPointName" label="取餐点" min-width="150" />
            <el-table-column prop="deliveryBuilding" label="配送楼栋" min-width="140" />
            <el-table-column label="配送费用" width="120" align="right">
              <template #default="{ row }">
                {{ formatAmount(row.totalAmount) }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="订单状态" width="150" />
            <el-table-column label="操作" width="120" align="center">
              <template #default="{ row }">
                <el-button type="primary" link @click="openOrderDetail(row.id)">
                  详情
                </el-button>
                <el-button
                  type="primary"
                  link
                  :loading="acceptingOrderId === row.id"
                  @click="acceptOrder(row.id)"
                >
                  接单
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </section>

        <section class="card">
          <div class="section-header">
            <div>
              <h3>快捷入口</h3>
              <p>本轮继续保持最小承接，除了刷新和回退，还补一个“按订单号查看详情”的只读回读入口，便于验证 completed 态。</p>
            </div>
          </div>
          <div class="lookup-panel">
            <el-input
              v-model="detailLookupId"
              placeholder="输入订单号后直接查看详情，例如 CR202604060001"
              clearable
            />
            <el-button
              type="primary"
              :disabled="!hasCourierToken || !detailLookupId.trim()"
              @click="openOrderDetail(detailLookupId.trim())"
            >
              查看订单详情
            </el-button>
          </div>
          <div class="action-group">
            <el-button type="primary" @click="loadWorkbench">刷新工作台</el-button>
            <el-button @click="goToOnboarding">回到入驻页面</el-button>
            <el-button @click="goToProfile">回到个人中心</el-button>
          </div>
        </section>

        <el-drawer
          v-model="detailVisible"
          title="订单详情"
          size="420px"
          destroy-on-close
        >
          <div v-loading="detailLoading" class="detail-content">
            <template v-if="orderDetail.id">
              <div class="detail-status-card">
                <div>
                  <span>当前订单</span>
                  <strong>{{ displayText(orderDetail.id) }}</strong>
                </div>
                <el-tag :type="isCompletedOrder ? 'success' : isAwaitingConfirmation ? 'warning' : 'info'">
                  {{ displayText(orderDetail.status) }}
                </el-tag>
              </div>

              <div class="drawer-section-title">
                <h4>订单基本信息</h4>
                <p>以下字段均来自 courier 订单详情读取接口，本轮只做展示分组，不改变字段来源。</p>
              </div>

              <div class="summary-grid detail-summary-grid">
                <div class="summary-item">
                  <span>订单号</span>
                  <strong>{{ displayText(orderDetail.id) }}</strong>
                </div>
                <div class="summary-item">
                  <span>订单状态</span>
                  <strong>{{ displayText(orderDetail.status) }}</strong>
                </div>
                <div class="summary-item">
                  <span>取餐点</span>
                  <strong>{{ displayText(orderDetail.pickupPointName) }}</strong>
                </div>
                <div class="summary-item">
                  <span>配送楼栋</span>
                  <strong>{{ displayText(orderDetail.deliveryBuilding) }}</strong>
                </div>
                <div class="summary-item">
                  <span>配送详情</span>
                  <strong>{{ displayText(orderDetail.deliveryDetail) }}</strong>
                </div>
                <div class="summary-item">
                  <span>订单金额</span>
                  <strong>{{ formatAmount(orderDetail.totalAmount) }}</strong>
                </div>
                <div class="summary-item">
                  <span>取餐码</span>
                  <strong>{{ displayText(orderDetail.pickupCode) }}</strong>
                </div>
                <div class="summary-item">
                  <span>customer 备注</span>
                  <strong>{{ displayText(orderDetail.customerRemark) }}</strong>
                </div>
                <div class="summary-item">
                  <span>接单时间</span>
                  <strong>{{ displayText(orderDetail.acceptedAt) }}</strong>
                </div>
                <div class="summary-item">
                  <span>取餐时间</span>
                  <strong>{{ displayText(orderDetail.pickedUpAt) }}</strong>
                </div>
                <div class="summary-item">
                  <span>送达时间</span>
                  <strong>{{ displayText(orderDetail.deliveredAt) }}</strong>
                </div>
                <div class="summary-item">
                  <span>完成时间</span>
                  <strong>{{ displayText(orderDetail.autoCompleteAt) }}</strong>
                </div>
                <div class="summary-item">
                  <span>创建时间</span>
                  <strong>{{ displayText(orderDetail.createdAt) }}</strong>
                </div>
                <div class="summary-item">
                  <span>更新时间</span>
                  <strong>{{ displayText(orderDetail.updatedAt) }}</strong>
                </div>
                <div class="summary-item">
                  <span>当前 courierProfileId</span>
                  <strong>{{ displayText(orderDetail.courierProfileId) }}</strong>
                </div>
              </div>

              <div class="pickup-section">
                <div class="pickup-header">
                  <div>
                    <h4>最小取餐承接</h4>
                    <p>当前后端要求上传受控文件路径作为取餐凭证，例如 `/api/files/...`。本轮只补一个最小输入区，不新开页面。</p>
                  </div>
                  <el-tag :type="canPickupOrder ? 'success' : 'info'">
                    {{ canPickupOrder ? '当前订单可尝试取餐' : '当前订单状态暂不支持取餐' }}
                  </el-tag>
                </div>

                <el-form label-position="top" class="pickup-form">
                  <el-form-item label="取餐凭证路径">
                    <el-input
                      v-model="pickupForm.pickupProofImageUrl"
                      :disabled="!canPickupOrder || pickupSubmitting"
                      placeholder="请输入受控文件路径，例如 /api/files/campus/pickup-proof/xxx.jpg"
                      clearable
                    />
                  </el-form-item>
                  <el-form-item label="courier 备注">
                    <el-input
                      v-model="pickupForm.courierRemark"
                      type="textarea"
                      :rows="3"
                      :disabled="!canPickupOrder || pickupSubmitting"
                      placeholder="可选，填写取餐说明或补充信息"
                    />
                  </el-form-item>
                  <div class="pickup-actions">
                    <el-button
                      type="primary"
                      :disabled="!canPickupOrder"
                      :loading="pickupSubmitting"
                      @click="pickupOrder"
                    >
                      确认取餐
                    </el-button>
                  </div>
                </el-form>
              </div>

              <div class="deliver-section">
                <div class="pickup-header">
                  <div>
                    <h4>最小送达承接</h4>
                    <p>当前后端 `deliver` 接口会按真实状态推进：`PICKED_UP -> DELIVERING`，`DELIVERING -> AWAITING_CONFIRMATION`。本轮只补一个最小 remark 输入区。</p>
                  </div>
                  <el-tag :type="canDeliverOrder ? 'success' : 'info'">
                    {{ canDeliverOrder ? deliverActionLabel : '当前订单状态暂不支持推进送达流程' }}
                  </el-tag>
                </div>

                <el-form label-position="top" class="pickup-form">
                  <el-form-item label="配送备注">
                    <el-input
                      v-model="deliverForm.courierRemark"
                      type="textarea"
                      :rows="3"
                      :disabled="!canDeliverOrder || deliverSubmitting"
                      placeholder="可选，填写配送中的补充说明"
                    />
                  </el-form-item>
                  <div class="pickup-actions">
                    <el-button
                      type="primary"
                      :disabled="!canDeliverOrder"
                      :loading="deliverSubmitting"
                      @click="deliverOrder"
                    >
                      {{ deliverActionLabel }}
                    </el-button>
                  </div>
                </el-form>
              </div>

              <div v-if="showPostDeliveryStatus" class="confirm-visual-section">
                <div class="pickup-header">
                  <div>
                    <h4>{{ postDeliverySectionTitle }}</h4>
                    <p>{{ postDeliveryMessage }}</p>
                  </div>
                  <el-tag :type="isAwaitingConfirmation ? 'warning' : 'success'">
                    {{ isAwaitingConfirmation ? '等待用户确认' : '订单已完成' }}
                  </el-tag>
                </div>

                <div class="summary-grid exception-grid">
                  <div class="summary-item">
                    <span>当前订单状态</span>
                    <strong>{{ displayText(orderDetail.status) }}</strong>
                  </div>
                  <div class="summary-item">
                    <span>已送达时间</span>
                    <strong>{{ displayText(orderDetail.deliveredAt) }}</strong>
                  </div>
                  <div class="summary-item">
                    <span>最近异常类型</span>
                    <strong>{{ displayText(orderDetail.exceptionType) }}</strong>
                  </div>
                  <div class="summary-item">
                    <span>最近异常说明</span>
                    <strong>{{ displayText(orderDetail.exceptionRemark) }}</strong>
                  </div>
                </div>

                <div class="summary-grid completion-summary-grid">
                  <div class="summary-item">
                    <span>取餐点</span>
                    <strong>{{ displayText(orderDetail.pickupPointName) }}</strong>
                  </div>
                  <div class="summary-item">
                    <span>配送楼栋</span>
                    <strong>{{ displayText(orderDetail.deliveryBuilding) }}</strong>
                  </div>
                  <div class="summary-item">
                    <span>配送详情</span>
                    <strong>{{ displayText(orderDetail.deliveryDetail) }}</strong>
                  </div>
                  <div class="summary-item">
                    <span>订单金额</span>
                    <strong>{{ formatAmount(orderDetail.totalAmount) }}</strong>
                  </div>
                  <div class="summary-item">
                    <span>customer 备注</span>
                    <strong>{{ displayText(orderDetail.customerRemark) }}</strong>
                  </div>
                  <div class="summary-item">
                    <span>完成时间</span>
                    <strong>{{ displayText(orderDetail.autoCompleteAt) }}</strong>
                  </div>
                </div>

                <div class="node-list">
                  <div
                    v-for="node in postDeliveryNodes"
                    :key="node.label"
                    class="node-item"
                  >
                    <span class="node-label">{{ node.label }}</span>
                    <strong class="node-value">{{ displayText(node.value) }}</strong>
                  </div>
                </div>
              </div>

              <div class="exception-section">
                <div class="pickup-header">
                  <div>
                    <h4>最小异常上报承接</h4>
                    <p>当前后端 `exception-report` 仅要求 `exceptionType` 和 `exceptionRemark`。本轮不扩历史表，只支持在允许阶段上报最新一次异常信息。</p>
                  </div>
                  <el-tag :type="canReportException ? 'warning' : 'info'">
                    {{ canReportException ? '当前订单可上报异常' : '当前订单状态暂不支持异常上报' }}
                  </el-tag>
                </div>

                <div class="summary-grid exception-grid">
                  <div class="summary-item">
                    <span>最新异常类型</span>
                    <strong>{{ displayText(orderDetail.exceptionType) }}</strong>
                  </div>
                  <div class="summary-item">
                    <span>最新异常说明</span>
                    <strong>{{ displayText(orderDetail.exceptionRemark) }}</strong>
                  </div>
                  <div class="summary-item">
                    <span>最近上报时间</span>
                    <strong>{{ displayText(orderDetail.exceptionReportedAt) }}</strong>
                  </div>
                </div>

                <el-form label-position="top" class="pickup-form">
                  <el-form-item label="异常类型">
                    <el-input
                      v-model="exceptionForm.exceptionType"
                      :disabled="!canReportException || exceptionSubmitting"
                      placeholder="请输入最小异常类型，例如 联系不上 / 商家缺货 / 地址异常"
                      clearable
                    />
                  </el-form-item>
                  <el-form-item label="异常说明">
                    <el-input
                      v-model="exceptionForm.exceptionRemark"
                      type="textarea"
                      :rows="3"
                      :disabled="!canReportException || exceptionSubmitting"
                      placeholder="请输入异常说明，失败时会直接展示后端原错误信息"
                    />
                  </el-form-item>
                  <div class="pickup-actions">
                    <el-button
                      type="warning"
                      :disabled="!canReportException"
                      :loading="exceptionSubmitting"
                      @click="reportException"
                    >
                      提交异常上报
                    </el-button>
                  </div>
                </el-form>
              </div>
            </template>
            <el-empty v-else description="当前没有可展示的订单详情；可从可接单列表进入，或在快捷入口输入订单号回读。" />
          </div>
        </el-drawer>
      </template>

      <section v-else class="card">
        <el-empty description="当前浏览器还没有兼职 token，暂时无法进入兼职工作台。">
          <el-button type="primary" @click="goToParttimeLogin">前往兼职端登录</el-button>
          <el-button @click="goToOnboarding">返回入驻页面</el-button>
          <el-button @click="goToProfile">回到个人中心</el-button>
        </el-empty>
      </section>
    </div>
  </UserLayout>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import UserLayout from '../../layout/UserLayout.vue'
import {
  acceptCourierOrder,
  deliverCourierOrder,
  getCourierAvailableOrders,
  getCourierOrderDetail,
  getCourierProfile,
  getCourierReviewStatus,
  pickupCourierOrder,
  reportCourierOrderException
} from '../../api/campus-courier'

const router = useRouter()
const loading = ref(false)
const acceptingOrderId = ref('')
const detailVisible = ref(false)
const detailLoading = ref(false)
const pickupSubmitting = ref(false)
const deliverSubmitting = ref(false)
const exceptionSubmitting = ref(false)
const availableOrders = ref([])
const detailLookupId = ref('')

const profile = reactive({
  id: null,
  realName: '',
  reviewStatus: '',
  reviewComment: '',
  enabled: 0
})

const reviewStatus = reactive({
  reviewStatus: '',
  reviewComment: '',
  enabled: 0
})

const orderDetail = reactive({
  id: '',
  status: '',
  pickupPointName: '',
  deliveryBuilding: '',
  deliveryDetail: '',
  totalAmount: 0,
  pickupCode: '',
  customerRemark: '',
  acceptedAt: '',
  pickedUpAt: '',
  deliveredAt: '',
  autoCompleteAt: '',
  exceptionType: '',
  exceptionRemark: '',
  exceptionReportedAt: '',
  createdAt: '',
  updatedAt: '',
  courierProfileId: ''
})

const pickupForm = reactive({
  pickupProofImageUrl: '',
  courierRemark: ''
})

const deliverForm = reactive({
  courierRemark: ''
})

const exceptionForm = reactive({
  exceptionType: '',
  exceptionRemark: ''
})

const hasCourierToken = computed(() => Boolean(localStorage.getItem('courier_token')))
const canPickupOrder = computed(() => orderDetail.status === 'ACCEPTED')
const canDeliverOrder = computed(() => ['PICKED_UP', 'DELIVERING'].includes(orderDetail.status))
const canReportException = computed(() => ['ACCEPTED', 'PICKED_UP', 'DELIVERING', 'AWAITING_CONFIRMATION'].includes(orderDetail.status))
const isAwaitingConfirmation = computed(() => orderDetail.status === 'AWAITING_CONFIRMATION')
const isCompletedOrder = computed(() => orderDetail.status === 'COMPLETED')
const showPostDeliveryStatus = computed(() => isAwaitingConfirmation.value || isCompletedOrder.value)
const deliverActionLabel = computed(() => (orderDetail.status === 'PICKED_UP' ? '开始配送' : '确认送达'))
const postDeliverySectionTitle = computed(() => (isCompletedOrder.value ? '完成后最小只读承接' : '送达后状态可视化'))
const postDeliveryMessage = computed(() => {
  if (isAwaitingConfirmation.value) {
    return '订单已送达，当前等待 customer 侧确认。此阶段不再补新动作，只做最小状态提示和最近异常只读展示。'
  }
  if (isCompletedOrder.value) {
    return '订单已完成，当前 drawer 继续保留送达后节点、异常摘要和订单关键字段的最小只读承接。'
  }
  return ''
})
const postDeliveryNodes = computed(() => {
  const nodes = [
    { label: '接单时间', value: orderDetail.acceptedAt },
    { label: '取餐时间', value: orderDetail.pickedUpAt },
    { label: '送达时间', value: orderDetail.deliveredAt },
    { label: '完成时间', value: orderDetail.autoCompleteAt },
    { label: '最近更新时间', value: orderDetail.updatedAt }
  ]
  return nodes.filter(node => node.value)
})
const tokenPreview = computed(() => {
  const token = localStorage.getItem('courier_token') || ''
  if (!token) {
    return '未获取'
  }
  if (token.length <= 20) {
    return token
  }
  return `${token.slice(0, 10)}...${token.slice(-8)}`
})

const displayText = (value) => (value === null || value === undefined || value === '' ? '暂无' : value)
const formatAmount = (value) => `¥${Number(value || 0).toFixed(2)}`

const resetOrderDetail = () => {
  orderDetail.id = ''
  orderDetail.status = ''
  orderDetail.pickupPointName = ''
  orderDetail.deliveryBuilding = ''
  orderDetail.deliveryDetail = ''
  orderDetail.totalAmount = 0
  orderDetail.pickupCode = ''
  orderDetail.customerRemark = ''
  orderDetail.acceptedAt = ''
  orderDetail.pickedUpAt = ''
  orderDetail.deliveredAt = ''
  orderDetail.autoCompleteAt = ''
  orderDetail.exceptionType = ''
  orderDetail.exceptionRemark = ''
  orderDetail.exceptionReportedAt = ''
  orderDetail.createdAt = ''
  orderDetail.updatedAt = ''
  orderDetail.courierProfileId = ''
  pickupForm.pickupProofImageUrl = ''
  pickupForm.courierRemark = ''
  deliverForm.courierRemark = ''
  exceptionForm.exceptionType = ''
  exceptionForm.exceptionRemark = ''
}

const hydrateStoredProfile = () => {
  try {
    const stored = JSON.parse(localStorage.getItem('courier_profile') || '{}')
    profile.id = stored.id || null
    profile.realName = stored.realName || ''
    profile.reviewStatus = stored.reviewStatus || ''
    profile.reviewComment = stored.reviewComment || ''
    profile.enabled = stored.enabled ?? 0
  } catch (error) {
    profile.id = null
    profile.realName = ''
    profile.reviewStatus = ''
    profile.reviewComment = ''
    profile.enabled = 0
  }
}

const loadWorkbench = async () => {
  if (!hasCourierToken.value) {
    availableOrders.value = []
    return
  }

  loading.value = true
  try {
    const [profileRes, reviewRes, ordersRes] = await Promise.all([
      getCourierProfile(),
      getCourierReviewStatus(),
      getCourierAvailableOrders({ page: 1, pageSize: 5 })
    ])

    profile.id = profileRes.id || null
    profile.realName = profileRes.realName || ''
    profile.reviewStatus = profileRes.reviewStatus || ''
    profile.reviewComment = profileRes.reviewComment || ''
    profile.enabled = profileRes.enabled ?? 0
    localStorage.setItem('courier_profile', JSON.stringify(profileRes || {}))

    reviewStatus.reviewStatus = reviewRes.reviewStatus || ''
    reviewStatus.reviewComment = reviewRes.reviewComment || ''
    reviewStatus.enabled = reviewRes.enabled ?? 0

    availableOrders.value = ordersRes.records || []
  } finally {
    loading.value = false
  }
}

const openOrderDetail = async (orderId) => {
  if (!hasCourierToken.value) {
    ElMessage.warning('当前没有兼职 token，请先登录兼职端或返回入驻页面申请')
    return
  }

  detailVisible.value = true
  detailLoading.value = true
  resetOrderDetail()
  try {
    const detail = await getCourierOrderDetail(orderId)
    orderDetail.id = detail.id || ''
    orderDetail.status = detail.status || ''
    orderDetail.pickupPointName = detail.pickupPointName || ''
    orderDetail.deliveryBuilding = detail.deliveryBuilding || ''
    orderDetail.deliveryDetail = detail.deliveryDetail || ''
    orderDetail.totalAmount = detail.totalAmount || 0
    orderDetail.pickupCode = detail.pickupCode || ''
    orderDetail.customerRemark = detail.customerRemark || ''
    orderDetail.acceptedAt = detail.acceptedAt || ''
    orderDetail.pickedUpAt = detail.pickedUpAt || ''
    orderDetail.deliveredAt = detail.deliveredAt || ''
    orderDetail.autoCompleteAt = detail.autoCompleteAt || ''
    orderDetail.exceptionType = detail.exceptionType || ''
    orderDetail.exceptionRemark = detail.exceptionRemark || ''
    orderDetail.exceptionReportedAt = detail.exceptionReportedAt || ''
    orderDetail.createdAt = detail.createdAt || ''
    orderDetail.updatedAt = detail.updatedAt || ''
    orderDetail.courierProfileId = detail.courierProfileId || ''
  } catch (error) {
    detailVisible.value = false
  } finally {
    detailLoading.value = false
  }
}

const acceptOrder = async (orderId) => {
  if (!hasCourierToken.value) {
    ElMessage.warning('当前没有兼职 token，请先登录兼职端或返回入驻页面申请')
    return
  }

  acceptingOrderId.value = orderId
  try {
    await acceptCourierOrder(orderId)
    ElMessage.success('接单成功，已刷新当前可接单列表')
    await loadWorkbench()
    await openOrderDetail(orderId)
  } catch (error) {
    // 请求层已经处理错误提示，这里只吞掉异常，避免控制台出现未处理 Promise
  } finally {
    acceptingOrderId.value = ''
  }
}

const pickupOrder = async () => {
  if (!hasCourierToken.value) {
    ElMessage.warning('当前没有兼职 token，请先登录兼职端或返回入驻页面申请')
    return
  }

  if (!orderDetail.id) {
    ElMessage.warning('当前没有可取餐的订单详情')
    return
  }

  pickupSubmitting.value = true
  try {
    await pickupCourierOrder(orderDetail.id, {
      pickupProofImageUrl: pickupForm.pickupProofImageUrl,
      courierRemark: pickupForm.courierRemark
    })
    ElMessage.success('取餐成功，已刷新订单详情')
    await openOrderDetail(orderDetail.id)
    await loadWorkbench()
  } catch (error) {
    // 请求层已经处理错误提示，这里吞掉异常避免未处理 Promise。
  } finally {
    pickupSubmitting.value = false
  }
}

const deliverOrder = async () => {
  if (!hasCourierToken.value) {
    ElMessage.warning('当前没有兼职 token，请先登录兼职端或返回入驻页面申请')
    return
  }

  if (!orderDetail.id) {
    ElMessage.warning('当前没有可推进送达流程的订单详情')
    return
  }

  deliverSubmitting.value = true
  try {
    await deliverCourierOrder(orderDetail.id, {
      courierRemark: deliverForm.courierRemark
    })
    ElMessage.success(`${deliverActionLabel.value}成功，已刷新订单详情`)
    await openOrderDetail(orderDetail.id)
    await loadWorkbench()
  } catch (error) {
    // 请求层已经处理错误提示，这里吞掉异常避免未处理 Promise。
  } finally {
    deliverSubmitting.value = false
  }
}

const reportException = async () => {
  if (!hasCourierToken.value) {
    ElMessage.warning('当前没有兼职 token，请先登录兼职端或返回入驻页面申请')
    return
  }

  if (!orderDetail.id) {
    ElMessage.warning('当前没有可上报异常的订单详情')
    return
  }

  exceptionSubmitting.value = true
  try {
    await reportCourierOrderException(orderDetail.id, {
      exceptionType: exceptionForm.exceptionType,
      exceptionRemark: exceptionForm.exceptionRemark
    })
    ElMessage.success('异常上报成功，已刷新订单详情')
    await openOrderDetail(orderDetail.id)
    await loadWorkbench()
  } catch (error) {
    // 请求层已经处理错误提示，这里吞掉异常避免未处理 Promise。
  } finally {
    exceptionSubmitting.value = false
  }
}

const goToOnboarding = () => {
  router.push('/user/campus/courier-onboarding')
}

const goToParttimeLogin = () => {
  router.push('/parttime/login')
}

const goToProfile = () => {
  router.push('/user/profile')
}

onMounted(() => {
  hydrateStoredProfile()
  loadWorkbench()
})
</script>

<style scoped lang="scss">
.workbench-page {
  padding: 16px;
}

.card {
  background: #fff;
  border-radius: 16px;
  padding: 16px;
  margin-bottom: 14px;
}

.hero-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.hero-card h2 {
  margin: 0 0 6px;
}

.hero-card p {
  margin: 0;
  color: #909399;
  font-size: 14px;
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
  background: #ecf5ff;
  color: #337ecc;
  font-size: 12px;
  font-weight: 600;
}

.token-pill {
  padding: 8px 14px;
  border-radius: 999px;
  font-weight: 600;
}

.token-pill.active {
  background: #f0f9eb;
  color: #67c23a;
}

.token-pill.inactive {
  background: #fdf6ec;
  color: #e6a23c;
}

.page-alert {
  margin-bottom: 14px;
}

.flow-strip {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  margin-bottom: 14px;
}

.flow-step {
  background: #fff;
  border-radius: 14px;
  padding: 14px;
  border: 1px solid #eef2ff;
}

.flow-step span {
  display: block;
  color: #909399;
  font-size: 12px;
  margin-bottom: 6px;
}

.flow-step strong {
  color: #18181b;
  font-size: 14px;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 16px;
}

.section-header h3 {
  margin: 0 0 6px;
}

.section-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.table-note {
  display: flex;
  align-items: center;
  gap: 10px;
  border-radius: 12px;
  background: #f8fafc;
  color: #606266;
  padding: 12px;
  margin-bottom: 12px;
  line-height: 1.5;
}

.table-note strong {
  color: #337ecc;
  white-space: nowrap;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 12px;
}

.summary-item {
  background: #f8fafc;
  border-radius: 12px;
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.summary-item span {
  color: #909399;
  font-size: 13px;
}

.action-group {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.lookup-panel {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
}

.detail-content {
  min-height: 160px;
}

.detail-status-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  border-radius: 14px;
  background: linear-gradient(180deg, #f8fafc 0%, #eef5ff 100%);
  padding: 14px;
  margin-bottom: 16px;
}

.detail-status-card span {
  display: block;
  color: #909399;
  font-size: 13px;
  margin-bottom: 6px;
}

.detail-status-card strong {
  color: #18181b;
}

.drawer-section-title {
  margin-bottom: 12px;
}

.drawer-section-title h4 {
  margin: 0 0 6px;
}

.drawer-section-title p {
  margin: 0;
  color: #909399;
  font-size: 13px;
  line-height: 1.6;
}

.detail-summary-grid {
  margin-bottom: 2px;
}

.pickup-section {
  margin-top: 18px;
  border-top: 1px solid #ebeef5;
  padding-top: 16px;
}

.deliver-section {
  margin-top: 18px;
  border-top: 1px solid #ebeef5;
  padding-top: 16px;
}

.exception-section {
  margin-top: 18px;
  border-top: 1px solid #ebeef5;
  padding-top: 16px;
}

.confirm-visual-section {
  margin-top: 18px;
  border-top: 1px solid #ebeef5;
  padding-top: 16px;
}

.exception-grid {
  margin-bottom: 12px;
}

.completion-summary-grid {
  margin-bottom: 12px;
}

.node-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.node-item {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  padding: 10px 12px;
  border-radius: 10px;
  background: #f8fafc;
}

.node-label {
  color: #909399;
  font-size: 13px;
}

.node-value {
  text-align: right;
}

.pickup-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}

.pickup-header h4 {
  margin: 0 0 6px;
}

.pickup-header p {
  margin: 0;
  color: #909399;
  font-size: 13px;
  line-height: 1.6;
}

.pickup-form {
  margin-top: 8px;
}

.pickup-actions {
  display: flex;
  justify-content: flex-end;
}

@media (max-width: 640px) {
  .hero-card,
  .section-header,
  .pickup-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .lookup-panel {
    flex-direction: column;
  }

  .flow-strip {
    grid-template-columns: 1fr;
  }

  .table-note,
  .detail-status-card {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
