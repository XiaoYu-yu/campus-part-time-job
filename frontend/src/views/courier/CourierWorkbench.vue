<template>
  <ParttimeLayout>
    <div class="workbench-page">
      <section class="card hero-card">
        <div>
          <div class="title-row">
            <h2>兼职工作台</h2>
            <span class="readonly-badge">接单中</span>
          </div>
          <p>这里可以看可接任务、接单、取餐、送达，也能在路上遇到问题时上报异常。</p>
        </div>
        <div class="token-pill" :class="hasCourierToken ? 'active' : 'inactive'">
          {{ hasCourierToken ? '已登录兼职端' : '请先登录' }}
        </div>
      </section>

      <el-alert
        :title="hasCourierToken ? '登录状态正常，可以继续接单。' : '还没有登录兼职端，请先登录或去报名。'"
        :description="hasCourierToken ? '页面会自动刷新你的资料、审核状态和可接任务。' : '登录后才能查看可接任务和订单详情。'"
        :type="hasCourierToken ? 'success' : 'warning'"
        :closable="false"
        show-icon
        class="page-alert"
      />

      <template v-if="hasCourierToken">
        <section class="status-tiles">
          <div class="status-tile available">
            <span>可接任务</span>
            <strong>{{ availableOrders.length }}</strong>
          </div>
          <div class="status-tile progress">
            <span>资料状态</span>
            <strong>{{ displayText(reviewStatus.reviewStatus || profile.reviewStatus) }}</strong>
          </div>
          <div class="status-tile done">
            <span>启用状态</span>
            <strong>{{ Number(reviewStatus.enabled ?? profile.enabled) === 1 ? '可工作' : '待启用' }}</strong>
          </div>
          <div class="status-tile issue">
            <span>登录</span>
            <strong>有效</strong>
          </div>
        </section>

        <section class="card">
          <div class="section-header">
            <div>
              <h3>身份状态卡</h3>
              <p>这里展示你的兼职资料和最新审核状态。</p>
            </div>
            <el-button type="primary" plain :loading="loading" @click="loadWorkbench">刷新工作台</el-button>
          </div>

          <div class="summary-grid">
            <div class="summary-item">
              <span>兼职资料编号</span>
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
              <span>登录状态</span>
              <strong>{{ tokenPreview }}</strong>
            </div>
          </div>
        </section>

        <section class="card">
          <div class="section-header">
            <div>
              <h3>可接任务</h3>
              <p>附近还没人接的代送单会出现在这里，接单成功后列表会自动刷新。</p>
            </div>
            <el-button text type="primary" :loading="loading" @click="loadWorkbench">刷新列表</el-button>
          </div>

          <div class="table-note">
            <strong>接单提示</strong>
            <span>看到合适的任务就可以接；暂时没有的话，稍后刷新看看。</span>
          </div>

          <div
            v-loading="loading"
            class="task-list"
          >
            <el-empty
              v-if="!loading && availableOrders.length === 0"
              description="当前暂无可接任务，稍后刷新看看"
            />

            <article v-for="order in availableOrders" :key="order.id" class="task-card">
              <div class="task-card__top">
                <div>
                  <span class="task-id">#{{ order.id }}</span>
                  <h4>{{ displayText(order.pickupPointName) }} → {{ displayText(order.deliveryBuilding) }}</h4>
                  <p>{{ displayText(order.deliveryDetail) }}</p>
                </div>
                <strong class="task-price">{{ formatAmount(order.totalAmount) }}</strong>
              </div>
              <div class="task-meta">
                <span>状态：{{ displayText(order.status) }}</span>
                <span>可接单</span>
              </div>
              <div class="task-actions">
                <el-button @click="openOrderDetail(order.id)">查看详情</el-button>
                <el-button
                  type="primary"
                  :loading="acceptingOrderId === order.id"
                  @click="acceptOrder(order.id)"
                >
                  接单
                </el-button>
              </div>
            </article>
          </div>
        </section>

        <section class="card">
          <div class="section-header">
            <div>
              <h3>快捷入口</h3>
              <p>可以刷新工作台，也可以输入订单号直接查看这单的详情。</p>
            </div>
          </div>
          <div class="lookup-panel">
            <el-input
              v-model="detailLookupId"
              placeholder="输入订单号查看详情，例如 CR202604060001"
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
            <el-button @click="goToProfile">查看兼职资料</el-button>
          </div>
        </section>

        <el-drawer
          v-model="detailVisible"
          title="订单详情"
          :size="drawerSize"
          destroy-on-close
        >
          <div v-loading="detailLoading" class="detail-content">
            <template v-if="orderDetail.id">
              <div class="detail-status-card">
                <div>
                  <span>订单号</span>
                  <strong>{{ displayText(orderDetail.id) }}</strong>
                </div>
                <el-tag :type="isCompletedOrder ? 'success' : isAwaitingConfirmation ? 'warning' : 'info'">
                  {{ displayText(orderDetail.status) }}
                </el-tag>
              </div>

              <div class="drawer-section-title">
                <h4>订单基本信息</h4>
                <p>这单的取餐、送达、金额和时间信息都在这里。</p>
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
                  <span>下单备注</span>
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
                  <span>兼职资料编号</span>
                  <strong>{{ displayText(orderDetail.courierProfileId) }}</strong>
                </div>
              </div>

              <div class="pickup-section">
                <div class="pickup-header">
                  <div>
                    <h4>确认取餐</h4>
                    <p>拿到餐后，上传或填写一张取餐凭证，方便后面核对。</p>
                  </div>
                  <el-tag :type="canPickupOrder ? 'success' : 'info'">
                    {{ canPickupOrder ? '现在可以确认取餐' : '这单现在还不能取餐' }}
                  </el-tag>
                </div>

                <el-form label-position="top" class="pickup-form">
                  <el-form-item label="取餐凭证图片地址">
                    <el-input
                      v-model="pickupForm.pickupProofImageUrl"
                      :disabled="!canPickupOrder || pickupSubmitting"
                      placeholder="测试时可填 /api/files/android-smoke-pickup.jpg"
                      clearable
                    />
                  </el-form-item>
                  <el-form-item label="取餐备注">
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
                    <h4>配送与送达</h4>
                    <p>取餐后先开始配送，送到后再确认送达。用户确认后订单才会最终完成。</p>
                  </div>
                  <el-tag :type="canDeliverOrder ? 'success' : 'info'">
                    {{ canDeliverOrder ? deliverActionLabel : '这单现在还不能操作配送' }}
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
                    <span>订单状态</span>
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
                    <span>下单备注</span>
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
                    <h4>上报异常</h4>
                    <p>联系不上、地址不清楚、商家缺货等情况，都可以先在这里说明。</p>
                  </div>
                  <el-tag :type="canReportException ? 'warning' : 'info'">
                    {{ canReportException ? '这单可以上报异常' : '这单现在不能上报异常' }}
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
                      placeholder="例如：联系不上 / 商家缺货 / 地址不清楚"
                      clearable
                    />
                  </el-form-item>
                  <el-form-item label="异常说明">
                    <el-input
                      v-model="exceptionForm.exceptionRemark"
                      type="textarea"
                      :rows="3"
                      :disabled="!canReportException || exceptionSubmitting"
                      placeholder="简单说清楚发生了什么，方便后续处理"
                    />
                  </el-form-item>
                  <div class="pickup-actions">
                    <el-button
                      type="warning"
                      :disabled="!canReportException"
                      :loading="exceptionSubmitting"
                      @click="reportException"
                    >
                      提交异常
                    </el-button>
                  </div>
                </el-form>
              </div>
            </template>
            <el-empty v-else description="还没有打开订单详情。可以从任务列表点进来，也可以输入订单号查看。" />
          </div>
        </el-drawer>
      </template>

      <section v-else class="card no-token-card">
        <el-empty description="还没有登录兼职端，暂时不能查看工作台。">
          <el-button type="primary" @click="goToParttimeLogin">前往兼职端登录</el-button>
          <el-button @click="goToOnboarding">去报名页</el-button>
          <el-button @click="goToProfile">查看兼职资料</el-button>
        </el-empty>
      </section>
    </div>
  </ParttimeLayout>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import ParttimeLayout from '../../layout/ParttimeLayout.vue'
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
  enabled: null
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
const drawerSize = computed(() => (window.innerWidth <= 640 ? '100%' : '420px'))
const canPickupOrder = computed(() => orderDetail.status === 'ACCEPTED')
const canDeliverOrder = computed(() => ['PICKED_UP', 'DELIVERING'].includes(orderDetail.status))
const canReportException = computed(() => ['ACCEPTED', 'PICKED_UP', 'DELIVERING', 'AWAITING_CONFIRMATION'].includes(orderDetail.status))
const isAwaitingConfirmation = computed(() => orderDetail.status === 'AWAITING_CONFIRMATION')
const isCompletedOrder = computed(() => orderDetail.status === 'COMPLETED')
const showPostDeliveryStatus = computed(() => isAwaitingConfirmation.value || isCompletedOrder.value)
const deliverActionLabel = computed(() => (orderDetail.status === 'PICKED_UP' ? '开始配送' : '确认送达'))
const postDeliverySectionTitle = computed(() => (isCompletedOrder.value ? '订单已完成' : '等待用户确认'))
const postDeliveryMessage = computed(() => {
  if (isAwaitingConfirmation.value) {
    return '订单已经送达，正在等用户确认收货。你可以先关注是否有异常记录。'
  }
  if (isCompletedOrder.value) {
    return '这单已经完成，这里保留送达时间、完成时间和异常摘要，方便回头核对。'
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
    return '未登录'
  }
  return '已登录'
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
    reviewStatus.enabled = reviewRes.enabled ?? null

    availableOrders.value = ordersRes.records || []
  } finally {
    loading.value = false
  }
}

const openOrderDetail = async (orderId) => {
  if (!hasCourierToken.value) {
    ElMessage.warning('请先登录兼职端，或回报名页完成开通')
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
    ElMessage.warning('请先登录兼职端，或回报名页完成开通')
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
    ElMessage.warning('请先登录兼职端，或回报名页完成开通')
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
    ElMessage.warning('请先登录兼职端，或回报名页完成开通')
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
    ElMessage.warning('请先登录兼职端，或回报名页完成开通')
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
  router.push('/parttime/profile')
}

onMounted(() => {
  hydrateStoredProfile()
  loadWorkbench()
})
</script>

<style scoped lang="scss">
.workbench-page {
  padding: 2px 0 16px;
  overflow-x: hidden;
}

.card {
  background: #ffffff;
  border: 1px solid #e4e4e7;
  border-radius: 14px;
  padding: 18px;
  margin-bottom: 14px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}

.hero-card {
  position: relative;
  overflow: hidden;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  border-radius: 16px;
  background: linear-gradient(135deg, #eefdfa, #f0fdfa);

  > * {
    position: relative;
    z-index: 1;
  }
}

.hero-card h2 {
  margin: 0 0 6px;
  color: #0f172a;
  font-size: 22px;
}

.hero-card p {
  margin: 0;
  color: #64748b;
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
  padding: 5px 12px;
  background: rgba(15, 118, 110, 0.1);
  color: #0f766e;
  font-size: 12px;
  font-weight: 700;
}

.token-pill {
  padding: 8px 14px;
  border-radius: 999px;
  font-weight: 700;
  font-size: 13px;
}

.token-pill.active {
  background: rgba(16, 185, 129, 0.1);
  color: #059669;
}

.token-pill.inactive {
  background: rgba(245, 158, 11, 0.1);
  color: #d97706;
}

.page-alert {
  margin-bottom: 14px;
}

.status-tiles {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
  margin-bottom: 14px;
}

.status-tile {
  min-height: 78px;
  border-radius: 14px;
  padding: 14px 12px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  border: 1px solid #e4e4e7;
  background: #ffffff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}

.status-tile span {
  color: #6b7a90;
  font-size: 12px;
  font-weight: 700;
}

.status-tile strong {
  color: #0f172a;
  font-size: 18px;
  font-weight: 800;
}

.status-tile.available {
  background: #f0f9ff;
}

.status-tile.progress {
  background: #f0fdf4;
}

.status-tile.done {
  background: #ecfeff;
}

.status-tile.issue {
  background: #fff7ed;
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
  color: #0f172a;
}

.section-header p {
  margin: 0;
  color: #64748b;
  font-size: 14px;
}

.table-note {
  display: flex;
  align-items: center;
  gap: 10px;
  border-radius: 14px;
  background: rgba(240, 253, 250, 0.7);
  color: #475569;
  padding: 12px 14px;
  margin-bottom: 12px;
  line-height: 1.5;
}

.table-note strong {
  color: #0f766e;
  white-space: nowrap;
}

.task-list {
  min-height: 120px;
  display: grid;
  gap: 12px;
}

.task-card {
  border: 1px solid #e4e4e7;
  border-radius: 14px;
  padding: 15px;
  background: #ffffff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}

.task-card__top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;

  h4 {
    margin: 4px 0 5px;
    color: #0f172a;
    font-size: 16px;
  }

  p {
    margin: 0;
    color: #6b7a90;
    line-height: 1.5;
  }
}

.task-id {
  color: #64748b;
  font-size: 12px;
  font-weight: 800;
}

.task-price {
  color: #ff5c22;
  font-size: 18px;
  font-weight: 800;
  white-space: nowrap;
  background: rgba(255, 92, 34, 0.08);
  padding: 4px 10px;
  border-radius: 10px;
}

.task-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;

  span {
    border-radius: 999px;
    padding: 6px 10px;
    background: rgba(20, 184, 166, 0.1);
    color: #0f766e;
    font-size: 12px;
    font-weight: 800;
  }
}

.task-actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
  margin-top: 14px;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 12px;
}

.summary-item {
  background: #f8fafc;
  border-radius: 12px;
  padding: 14px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.summary-item span {
  color: #94a3b8;
  font-size: 13px;
}

.summary-item strong {
  color: #0f172a;
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
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
}

.detail-status-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  border-radius: 12px;
  background: linear-gradient(180deg, #f0fdfa, #ecfeff);
  border: 1px solid #e4e4e7;
  padding: 14px;
  margin-bottom: 16px;
}

.detail-status-card span {
  display: block;
  color: #94a3b8;
  font-size: 13px;
  margin-bottom: 6px;
}

.detail-status-card strong {
  color: #0f172a;
}

.drawer-section-title {
  margin-bottom: 12px;
}

.drawer-section-title h4 {
  margin: 0 0 6px;
  color: #0f172a;
}

.drawer-section-title p {
  margin: 0;
  color: #94a3b8;
  font-size: 13px;
  line-height: 1.6;
}

.detail-summary-grid {
  margin-bottom: 2px;
}

.pickup-section,
.deliver-section,
.exception-section,
.confirm-visual-section {
  margin-top: 20px;
  border-top: 1px solid rgba(15, 118, 110, 0.12);
  padding-top: 18px;

  h4 {
    position: relative;
    padding-left: 12px;

    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 2px;
      bottom: 2px;
      width: 3px;
      border-radius: 2px;
      background: linear-gradient(180deg, #14b8a6, #38bdf8);
    }
  }
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
  padding: 12px 14px;
  border-radius: 12px;
  background: #f8fafc;
}

.node-label {
  color: #94a3b8;
  font-size: 13px;
}

.node-value {
  text-align: right;
  color: #0f172a;
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
  color: #0f172a;
}

.pickup-header p {
  margin: 0;
  color: #64748b;
  font-size: 13px;
  line-height: 1.6;
}

.pickup-form {
  margin-top: 8px;

  :deep(.el-input__wrapper),
  :deep(.el-textarea__inner) {
    min-height: 44px;
  }
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

  .status-tiles {
    grid-template-columns: repeat(2, 1fr);
  }

  .table-note,
  .detail-status-card {
    flex-direction: column;
    align-items: flex-start;
  }

  .summary-grid {
    grid-template-columns: repeat(auto-fit, minmax(130px, 1fr));
  }
}

.no-token-card {
  text-align: center;
  padding: 32px 18px;

  .el-empty {
    padding: 24px 0;
  }
}

@media (max-width: 380px) {
  .status-tiles {
    grid-template-columns: repeat(2, 1fr);
  }

  .status-tile {
    min-height: 64px;
    padding: 10px;
  }

  .status-tile strong {
    font-size: 16px;
  }

  .card {
    padding: 14px;
  }

  .summary-grid {
    grid-template-columns: 1fr;
  }
}
</style>
