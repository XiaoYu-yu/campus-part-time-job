<template>
  <UserLayout>
    <div class="workbench-page">
      <section class="card hero-card">
        <div>
          <h2>配送员工作台</h2>
          <p>这是 courier token 获取后的最小承接页。当前只做资料状态确认和可接单预览，不扩完整 courier 主工作流。</p>
        </div>
        <div class="token-pill" :class="hasCourierToken ? 'active' : 'inactive'">
          {{ hasCourierToken ? 'courier token 已就绪' : 'courier token 缺失' }}
        </div>
      </section>

      <el-alert
        :title="hasCourierToken ? '当前浏览器已检测到 courier token，可继续查看 courier 侧只读承接信息。' : '当前浏览器未检测到 courier token，请先返回 onboarding 页面申请。'"
        :description="hasCourierToken ? '页面会自动读取 courier 资料、审核状态和可接单预览。' : '没有 courier token 时，本页不会调用 courier 业务接口，只展示回退入口。'"
        :type="hasCourierToken ? 'success' : 'warning'"
        :closable="false"
        show-icon
        class="page-alert"
      />

      <template v-if="hasCourierToken">
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
              <p>本轮只做最小承接，不新增复杂 courier 页面群。</p>
            </div>
          </div>
          <div class="action-group">
            <el-button type="primary" @click="loadWorkbench">刷新工作台</el-button>
            <el-button @click="goToOnboarding">回到 onboarding 页面</el-button>
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
              <div class="summary-grid">
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
            </template>
            <el-empty v-else description="当前没有可展示的订单详情" />
          </div>
        </el-drawer>
      </template>

      <section v-else class="card">
        <el-empty description="当前浏览器还没有 courier_token，暂时无法进入 courier 工作台。">
          <el-button type="primary" @click="goToOnboarding">返回 onboarding 页面</el-button>
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
  pickupCourierOrder
} from '../../api/campus-courier'

const router = useRouter()
const loading = ref(false)
const acceptingOrderId = ref('')
const detailVisible = ref(false)
const detailLoading = ref(false)
const pickupSubmitting = ref(false)
const deliverSubmitting = ref(false)
const availableOrders = ref([])

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

const hasCourierToken = computed(() => Boolean(localStorage.getItem('courier_token')))
const canPickupOrder = computed(() => orderDetail.status === 'ACCEPTED')
const canDeliverOrder = computed(() => ['PICKED_UP', 'DELIVERING'].includes(orderDetail.status))
const deliverActionLabel = computed(() => (orderDetail.status === 'PICKED_UP' ? '开始配送' : '确认送达'))
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
  orderDetail.createdAt = ''
  orderDetail.updatedAt = ''
  orderDetail.courierProfileId = ''
  pickupForm.pickupProofImageUrl = ''
  pickupForm.courierRemark = ''
  deliverForm.courierRemark = ''
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
    ElMessage.warning('当前没有 courier token，请先返回 onboarding 页面申请')
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
    ElMessage.warning('当前没有 courier token，请先返回 onboarding 页面申请')
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
    ElMessage.warning('当前没有 courier token，请先返回 onboarding 页面申请')
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
    ElMessage.warning('当前没有 courier token，请先返回 onboarding 页面申请')
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

const goToOnboarding = () => {
  router.push('/user/campus/courier-onboarding')
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

.detail-content {
  min-height: 160px;
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
}
</style>
