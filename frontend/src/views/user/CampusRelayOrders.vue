<template>
  <UserLayout>
    <div class="relay-orders-page">
      <section class="hero-card">
        <div class="hero-kicker">校园代送</div>
        <h1>发布代送需求</h1>
        <p>填写取件点、送达楼栋和备注，创建后可执行模拟支付进入待接单。</p>
        <div class="hero-meta">
          <span>基础代送 {{ formatAmount(deliveryRules.baseFee) }}</span>
          <span>加急窗口 {{ deliveryRules.priorityWindowMinutes || 5 }} 分钟</span>
        </div>
      </section>

      <section class="card guide-card">
        <div class="guide-item">
          <span>1</span>
          <strong>填写取餐与送达信息</strong>
          <p>取餐点来自 public 取餐点接口，楼栋来自配送规则。</p>
        </div>
        <div class="guide-item">
          <span>2</span>
          <strong>创建待支付订单</strong>
          <p>创建后状态为待支付，订单号可用于结果回看。</p>
        </div>
        <div class="guide-item">
          <span>3</span>
          <strong>模拟支付进入待接单</strong>
          <p>只调用 mock-pay，不接第三方真实支付。</p>
        </div>
      </section>

      <section class="card form-card">
        <div class="section-heading">
          <div>
            <span>代送信息</span>
            <h2>创建校园代送单</h2>
          </div>
          <button class="text-button" type="button" @click="resetForm">重填</button>
        </div>

        <el-alert
          title="本页创建校园代送单并支持模拟支付，不改旧模块语义。"
          type="info"
          :closable="false"
          show-icon
          class="page-alert"
        />

        <el-form label-position="top" class="order-form">
          <el-form-item label="取餐点">
            <el-select
              v-model="form.pickupPointId"
              :loading="pickupLoading"
              placeholder="选择取餐点"
              style="width: 100%"
            >
              <el-option
                v-for="point in pickupPoints"
                :key="point.id"
                :label="point.name"
                :value="point.id"
              />
            </el-select>
          </el-form-item>

          <div class="two-column">
            <el-form-item label="送达类型">
              <el-select v-model="form.targetType" style="width: 100%" @change="handleTargetTypeChange">
                <el-option label="宿舍楼" value="DORMITORY" />
                <el-option label="教学楼" value="TEACHING_BUILDING" />
                <el-option label="图书馆" value="LIBRARY" />
              </el-select>
            </el-form-item>
            <el-form-item label="送达楼栋">
              <el-select v-model="form.deliveryBuilding" style="width: 100%" @change="handleBuildingChange">
                <el-option
                  v-for="item in buildingOptions"
                  :key="item"
                  :label="item"
                  :value="item"
                />
              </el-select>
            </el-form-item>
          </div>

          <el-form-item label="送达详情">
            <el-input v-model="form.deliveryDetail" placeholder="例如：竹园2栋门口 / 图书馆二楼门口" maxlength="80" show-word-limit />
          </el-form-item>

          <div class="two-column">
            <el-form-item label="联系人">
              <el-input v-model="form.contactName" placeholder="联系人姓名" maxlength="20" />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="form.contactPhone" placeholder="联系人手机号" maxlength="20" />
            </el-form-item>
          </div>

          <el-form-item label="代送内容">
            <el-input
              v-model="form.foodDescription"
              type="textarea"
              :rows="3"
              placeholder="请描述需要代送的物品，例如：汉堡套餐 + 奶茶"
              maxlength="120"
              show-word-limit
            />
          </el-form-item>

          <div class="two-column">
            <el-form-item label="平台">
              <el-input v-model="form.externalPlatformName" placeholder="美团 / 饿了么 / 其他" maxlength="30" />
            </el-form-item>
            <el-form-item label="外部订单号">
              <el-input v-model="form.externalOrderRef" placeholder="外部平台订单号" maxlength="40" />
            </el-form-item>
          </div>

          <div class="two-column">
            <el-form-item label="取餐码">
              <el-input v-model="form.pickupCode" placeholder="取餐码" maxlength="30" />
            </el-form-item>
            <el-form-item label="打赏金额">
              <el-input-number
                v-model="form.tipFee"
                :min="0"
                :max="10"
                :precision="2"
                :step="1"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </div>

        <div class="fee-card">
            <div>
              <span>费用预览</span>
            <strong>{{ formatAmount(amountPreview.total) }}</strong>
          </div>
          <p>基础 {{ formatAmount(amountPreview.base) }} + 加急 {{ formatAmount(amountPreview.priority) }} + 打赏 {{ formatAmount(amountPreview.tip) }}</p>
            <el-switch
              v-model="form.urgent"
              active-text="加急"
              inactive-text="普通"
              inline-prompt
            />
          </div>

          <el-form-item label="备注">
            <el-input v-model="form.remark" placeholder="给兼职配送员的补充说明" maxlength="80" show-word-limit />
          </el-form-item>

          <el-button type="primary" class="wide-button" :loading="createLoading" @click="handleCreate">
            提交订单
          </el-button>
        </el-form>

        <div v-if="createdOrderId" class="created-card">
          <div>
            <span>刚创建的订单</span>
            <strong>{{ createdOrderId }}</strong>
            <p>可先模拟支付进入待接单，也可去结果回看页只读查看。</p>
          </div>
          <div class="created-actions">
            <el-button type="primary" :loading="payingOrderId === createdOrderId" @click="handleMockPay(createdOrderId)">去模拟支付</el-button>
            <el-button @click="openResult(createdOrderId)">结果回看</el-button>
          </div>
        </div>
      </section>

      <section class="card list-card">
        <div class="section-heading">
          <div>
            <span>我的代送单</span>
            <h2>最近校园代送订单</h2>
          </div>
          <button class="text-button" type="button" @click="loadOrders">刷新</button>
        </div>

        <div class="status-tabs">
          <button
            v-for="tab in statusTabs"
            :key="tab.value"
            class="status-tab"
            :class="{ active: filters.status === tab.value }"
            @click="handleTabChange(tab.value)"
          >{{ tab.label }}</button>
        </div>

        <div class="filter-row">
          <el-input v-model="filters.orderNo" placeholder="按订单号筛选" clearable @keyup.enter="handleSearch" />
          <el-button type="primary" @click="handleSearch">查询</el-button>
        </div>

        <div v-if="ordersLoading" class="state-card">
          <div class="state-icon"><span class="spinner-ring"></span></div>
          <div>
            <strong>正在读取我的代送单</strong>
            <p>调用 customer 订单分页接口读取最近订单。</p>
          </div>
        </div>

        <div v-else-if="ordersError" class="state-card error-state">
          <div class="state-icon error-icon">!</div>
          <div>
            <strong>订单加载失败</strong>
            <p>{{ ordersError }}</p>
          </div>
        </div>

        <el-empty v-else-if="orders.length === 0" description="暂无校园代送单，先创建一笔用于试运营联调" />

        <div v-else class="order-list">
          <article v-for="order in orders" :key="order.id" class="order-card">
            <div class="order-head">
              <div class="order-head__left">
                <span class="order-head__label">订单号</span>
                <strong class="order-head__id">{{ order.id }}</strong>
              </div>
              <el-tag :type="statusTagType(order.status)" size="small">{{ statusText(order.status) }}</el-tag>
            </div>

            <div class="order-route">
              <div class="route-point">
                <span class="route-dot pickup-dot"></span>
                <div>
                  <span class="route-label">取餐点</span>
                  <strong>{{ displayText(order.pickupPointName) }}</strong>
                </div>
              </div>
              <div class="route-line"></div>
              <div class="route-point">
                <span class="route-dot deliver-dot"></span>
                <div>
                  <span class="route-label">送达地</span>
                  <strong>{{ displayText(order.deliveryBuilding) }} {{ displayText(order.deliveryDetail) }}</strong>
                </div>
              </div>
            </div>

            <div class="order-foot">
              <div class="order-foot__info">
                <span v-if="order.pickupCode" class="pickup-code-tag">取餐码 {{ order.pickupCode }}</span>
                <span class="order-amount">{{ formatAmount(order.totalAmount) }}</span>
              </div>
              <div class="order-actions">
                <el-button
                  v-if="canMockPay(order)"
                  type="primary"
                  size="small"
                  :loading="payingOrderId === order.id"
                  @click="handleMockPay(order.id)"
                >
                  模拟支付
                </el-button>
                <el-button size="small" @click="openResult(order.id)">结果回看</el-button>
              </div>
            </div>
          </article>
        </div>

        <div class="pagination-row">
          <el-pagination
            v-model:current-page="pagination.page"
            v-model:page-size="pagination.pageSize"
            size="small"
            layout="prev, pager, next"
            :total="pagination.total"
            @current-change="loadOrders"
          />
        </div>
      </section>
    </div>
  </UserLayout>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import UserLayout from '../../layout/UserLayout.vue'
import {
  createCampusCustomerOrder,
  getCampusCustomerOrders,
  getCampusDeliveryRules,
  getCampusPickupPoints,
  mockPayCampusCustomerOrder
} from '../../api/campus-customer'

const router = useRouter()

const pickupLoading = ref(false)
const createLoading = ref(false)
const ordersLoading = ref(false)
const ordersError = ref('')
const payingOrderId = ref('')
const pickupPoints = ref([])
const deliveryRules = ref({})
const orders = ref([])
const createdOrderId = ref('')

const defaultForm = () => ({
  pickupPointId: null,
  targetType: 'DORMITORY',
  deliveryBuilding: '竹园',
  deliveryDetail: '竹园2栋楼下',
  contactName: '张三',
  contactPhone: '13900139000',
  foodDescription: '美团订单：汉堡套餐 + 奶茶',
  externalPlatformName: '美团',
  externalOrderRef: `MT-${Date.now().toString().slice(-8)}`,
  pickupCode: 'A18',
  tipFee: 1,
  urgent: false,
  remark: '送到楼下即可'
})

const form = reactive(defaultForm())
const filters = reactive({
  orderNo: '',
  status: ''
})
const pagination = reactive({
  page: 1,
  pageSize: 5,
  total: 0
})

const buildingOptions = computed(() => {
  if (form.targetType === 'TEACHING_BUILDING') {
    return deliveryRules.value.teachingBuildings || ['一教学楼', '二教学楼']
  }
  if (form.targetType === 'LIBRARY') {
    return ['图书馆']
  }
  return deliveryRules.value.dormitoryBuildings || ['竹园', '梅园']
})

const amountPreview = computed(() => {
  const base = Number(deliveryRules.value.baseFee ?? 3)
  const priority = form.urgent ? Number(deliveryRules.value.priorityFeeMin ?? 3) : 0
  const tip = Number(form.tipFee || 0)
  return {
    base,
    priority,
    tip,
    total: base + priority + tip
  }
})

const displayText = (value) => (value === null || value === undefined || value === '' ? '暂无' : value)
const formatAmount = (value) => {
  if (value === null || value === undefined || value === '') return '¥0.00'
  return `¥${Number(value).toFixed(2)}`
}

const statusTabs = [
  { label: '全部', value: '' },
  { label: '待支付', value: 'PENDING_PAYMENT' },
  { label: '待接单', value: 'WAITING_ACCEPT' },
  { label: '配送中', value: 'DELIVERING' },
  { label: '已完成', value: 'COMPLETED' }
]

const handleTabChange = (value) => {
  filters.status = value
  pagination.page = 1
  loadOrders()
}

const statusText = (status) => ({
  PENDING_PAYMENT: '待支付',
  BUILDING_PRIORITY_PENDING: '楼栋优先',
  WAITING_ACCEPT: '待接单',
  ACCEPTED: '已接单',
  PICKED_UP: '已取餐',
  DELIVERING: '配送中',
  AWAITING_CONFIRMATION: '待确认',
  COMPLETED: '已完成',
  CANCELLED: '已取消',
  AFTER_SALE_OPEN: '售后中',
  AFTER_SALE_RESOLVED: '售后完成',
  AFTER_SALE_REJECTED: '售后驳回'
}[status] || displayText(status))

const statusTagType = (status) => ({
  PENDING_PAYMENT: 'warning',
  BUILDING_PRIORITY_PENDING: 'warning',
  WAITING_ACCEPT: 'info',
  ACCEPTED: 'primary',
  PICKED_UP: 'primary',
  DELIVERING: 'primary',
  AWAITING_CONFIRMATION: 'warning',
  COMPLETED: 'success',
  CANCELLED: 'info',
  AFTER_SALE_OPEN: 'danger',
  AFTER_SALE_RESOLVED: 'success',
  AFTER_SALE_REJECTED: 'info'
}[status] || 'info')

const paymentText = (status) => ({
  UNPAID: '未支付',
  PAID: '已支付',
  REFUNDED: '已退款'
}[status] || displayText(status))

const canMockPay = (order) => order?.paymentStatus === 'UNPAID' && order?.status === 'PENDING_PAYMENT'

const loadRules = async () => {
  const rules = await getCampusDeliveryRules()
  deliveryRules.value = rules || {}
}

const loadPickupPoints = async () => {
  pickupLoading.value = true
  try {
    pickupPoints.value = await getCampusPickupPoints()
    if (!form.pickupPointId && pickupPoints.value.length > 0) {
      form.pickupPointId = pickupPoints.value[0].id
    }
  } finally {
    pickupLoading.value = false
  }
}

const loadOrders = async () => {
  ordersLoading.value = true
  ordersError.value = ''
  try {
    const response = await getCampusCustomerOrders({
      page: pagination.page,
      pageSize: pagination.pageSize,
      orderNo: filters.orderNo || undefined,
      status: filters.status || undefined
    })
    orders.value = response.records || []
    pagination.total = response.total || 0
  } catch (error) {
    ordersError.value = error?.response?.data?.msg || error?.message || '加载订单列表失败'
  } finally {
    ordersLoading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadOrders()
}

const handleTargetTypeChange = () => {
  form.deliveryBuilding = buildingOptions.value[0] || ''
  handleBuildingChange()
}

const handleBuildingChange = () => {
  if (form.targetType === 'LIBRARY') {
    form.deliveryDetail = deliveryRules.value.libraryDeliveryPoints?.[0] || '图书馆二楼门口'
    return
  }
  if (form.targetType === 'TEACHING_BUILDING') {
    form.deliveryDetail = `${form.deliveryBuilding}楼下`
    return
  }
  form.deliveryDetail = `${form.deliveryBuilding}2栋楼下`
}

const resetForm = () => {
  const next = defaultForm()
  Object.assign(form, next)
  if (pickupPoints.value.length > 0) {
    form.pickupPointId = pickupPoints.value[0].id
  }
}

const validateForm = () => {
  const requiredFields = [
    ['pickupPointId', '请选择取餐点'],
    ['targetType', '请选择送达类型'],
    ['deliveryBuilding', '请选择送达楼栋'],
    ['deliveryDetail', '请填写送达详情'],
    ['contactName', '请填写联系人'],
    ['contactPhone', '请填写手机号'],
    ['foodDescription', '请填写代送内容'],
    ['externalPlatformName', '请填写外部平台'],
    ['externalOrderRef', '请填写外部订单号'],
    ['pickupCode', '请填写取餐码']
  ]
  for (const [field, message] of requiredFields) {
    if (!form[field]) {
      ElMessage.warning(message)
      return false
    }
  }
  if (Number(form.tipFee) > 0 && Number(form.tipFee) < 1) {
    ElMessage.warning('打赏金额为 0 或 1 到 10 元')
    return false
  }
  return true
}

const buildCreatePayload = () => ({
  pickupPointId: form.pickupPointId,
  targetType: form.targetType,
  deliveryBuilding: form.deliveryBuilding,
  deliveryDetail: form.deliveryDetail,
  contactName: form.contactName,
  contactPhone: form.contactPhone,
  foodDescription: form.foodDescription,
  externalPlatformName: form.externalPlatformName,
  externalOrderRef: form.externalOrderRef,
  pickupCode: form.pickupCode,
  remark: form.remark,
  tipFee: Number(form.tipFee || 0),
  urgentFlag: form.urgent ? 1 : 0
})

const handleCreate = async () => {
  if (!validateForm()) return
  createLoading.value = true
  try {
    const orderId = await createCampusCustomerOrder(buildCreatePayload())
    createdOrderId.value = orderId
    ElMessage.success(`代送单已创建：${orderId}`)
    await loadOrders()
  } finally {
    createLoading.value = false
  }
}

const handleMockPay = async (orderId) => {
  if (!orderId) return
  try {
    await ElMessageBox.confirm(
      '确认对该校园代送单执行模拟支付？这不会接入第三方真实支付。',
      '模拟支付',
      { type: 'warning' }
    )
  } catch {
    return
  }

  payingOrderId.value = orderId
  try {
    await mockPayCampusCustomerOrder(orderId)
    ElMessage.success('模拟支付成功，订单已进入待接单链路')
    await loadOrders()
  } finally {
    payingOrderId.value = ''
  }
}

const openResult = (orderId) => {
  router.push({
    path: '/user/campus/order-result',
    query: { orderId }
  })
}

onMounted(async () => {
  await Promise.all([loadRules(), loadPickupPoints()])
  await loadOrders()
})
</script>

<style scoped lang="scss">
.relay-orders-page {
  padding: 14px 14px 24px;
}

.hero-card,
.card {
  border: 1px solid rgba(15, 118, 110, 0.1);
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.08);
  backdrop-filter: blur(18px);
}

.hero-card {
  position: relative;
  overflow: hidden;
  border-radius: 28px;
  padding: 24px 22px;
  margin-bottom: 16px;
  background:
    radial-gradient(circle at 92% 10%, rgba(45, 212, 191, 0.28), transparent 30%),
    linear-gradient(135deg, rgba(226, 250, 255, 0.98), rgba(255, 255, 255, 0.88));

  &::after {
    content: "";
    position: absolute;
    right: -36px;
    bottom: -32px;
    width: 132px;
    height: 132px;
    border-radius: 50%;
    background: rgba(20, 184, 166, 0.1);
  }

  h1 {
    position: relative;
    z-index: 1;
    margin: 8px 0 10px;
    font-size: 27px;
    line-height: 1.18;
    letter-spacing: -0.8px;
    color: #0f172a;
  }

  p {
    position: relative;
    z-index: 1;
    margin: 0;
    color: #475569;
    line-height: 1.65;
  }
}

.hero-kicker,
.section-heading span {
  color: #0f766e;
  font-size: 12px;
  font-weight: 800;
  letter-spacing: 0.08em;
}

.hero-meta {
  position: relative;
  z-index: 1;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 16px;

  span {
    border-radius: 999px;
    background: rgba(15, 118, 110, 0.08);
    color: #0f766e;
    padding: 7px 10px;
    font-size: 12px;
    font-weight: 800;
  }
}

.card {
  border-radius: 24px;
  padding: 18px;
  margin-bottom: 16px;
}

.form-card {
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.96), rgba(255, 255, 255, 0.86));
  padding-bottom: 24px;
}

.order-form :deep(.el-form-item__label) {
  font-weight: 800;
  color: #26364d;
}

.order-form :deep(.el-input__wrapper),
.order-form :deep(.el-select__wrapper),
.order-form :deep(.el-textarea__inner) {
  min-height: 44px;
  border-radius: 15px;
}

.order-form :deep(.el-textarea__inner) {
  min-height: 88px;
}

.guide-card {
  display: grid;
  gap: 10px;
}

.guide-item {
  display: grid;
  grid-template-columns: 30px 1fr;
  column-gap: 10px;
  border-radius: 18px;
  background: rgba(248, 250, 252, 0.82);
  padding: 12px;

  span {
    grid-row: span 2;
    width: 30px;
    height: 30px;
    border-radius: 12px;
    display: grid;
    place-items: center;
    background: linear-gradient(135deg, #14b8a6 0%, #38bdf8 100%);
    color: #fff;
    font-weight: 900;
  }

  strong {
    color: #0f172a;
  }

  p {
    grid-column: 2;
    margin: 4px 0 0;
    color: #64748b;
    line-height: 1.5;
  }
}

.section-heading {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 14px;

  h2 {
    margin: 4px 0 0;
    font-size: 19px;
    color: #0f172a;
  }
}

.text-button {
  border: none;
  background: transparent;
  color: #0f766e;
  font-weight: 800;
  cursor: pointer;
}

.page-alert {
  margin-bottom: 14px;
}

.two-column {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.fee-card,
.created-card,
.state-card {
  border-radius: 18px;
  background:
    radial-gradient(circle at right, rgba(45, 212, 191, 0.18), transparent 34%),
    rgba(240, 253, 250, 0.78);
  padding: 14px;
  margin-bottom: 18px;
}

.fee-card {
  display: grid;
  gap: 8px;
  border: 1px solid rgba(20, 184, 166, 0.16);

  div {
    display: flex;
    justify-content: space-between;
    gap: 12px;
  }

  span,
  p {
    color: #64748b;
  }

  p {
    margin: 0;
    line-height: 1.5;
  }

  strong {
    color: #0f766e;
    font-size: 32px;
    letter-spacing: -0.04em;
  }
}

.wide-button {
  width: 100%;
  min-height: 48px;
  border-radius: 16px;
  font-weight: 800;
  margin-bottom: 8px;
}

.created-card {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
  margin: 16px 0 0;

  span {
    display: block;
    color: #64748b;
    font-size: 12px;
  }

  strong {
    display: block;
    color: #0f172a;
    margin: 4px 0;
  }

  p {
    margin: 0;
    color: #64748b;
    line-height: 1.5;
  }
}

.created-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex-shrink: 0;
}

.status-tabs {
  display: flex;
  gap: 6px;
  margin-bottom: 12px;
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
  scrollbar-width: none;
  &::-webkit-scrollbar { display: none; }
}

.status-tab {
  flex-shrink: 0;
  border: 1px solid rgba(15, 118, 110, 0.12);
  border-radius: 999px;
  padding: 7px 16px;
  background: rgba(255, 255, 255, 0.8);
  color: #64748b;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.18s ease;

  &.active {
    background: linear-gradient(135deg, #0f766e, #14b8a6);
    color: #fff;
    border-color: transparent;
    box-shadow: 0 4px 14px rgba(15, 118, 110, 0.2);
  }
}

.filter-row {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 10px;
  margin-bottom: 14px;
}

.state-card {
  display: flex;
  gap: 12px;
  align-items: flex-start;

  strong {
    color: #0f172a;
  }

  p {
    margin: 6px 0 0;
    color: #64748b;
  }
}

.state-icon {
  width: 38px;
  height: 38px;
  border-radius: 14px;
  display: grid;
  place-items: center;
  background: #ecfeff;
  color: #0891b2;
  font-weight: 900;
}

.spinner-ring {
  display: inline-block;
  width: 16px;
  height: 16px;
  border: 2.5px solid rgba(100, 116, 139, 0.2);
  border-top-color: #64748b;
  border-radius: 50%;
  animation: campus-spin 0.7s linear infinite;
}

@keyframes campus-spin {
  to { transform: rotate(360deg); }
}

.error-state {
  border-color: rgba(239, 68, 68, 0.15);
  background: rgba(254, 242, 242, 0.86);
}

.error-icon {
  background: rgba(254, 226, 226, 0.86);
  color: #ef4444;
}

.order-list {
  display: grid;
  gap: 12px;
}

.order-card {
  border: 1px solid rgba(15, 118, 110, 0.1);
  border-radius: 20px;
  padding: 14px;
  background: rgba(248, 250, 252, 0.86);
}

.order-head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: flex-start;
  margin-bottom: 12px;

  span {
    color: #64748b;
    font-size: 12px;
  }

  strong {
    display: block;
    margin-top: 4px;
    color: #0f172a;
  }
}

.order-route {
  background: rgba(255, 255, 255, 0.78);
  border-radius: 14px;
  padding: 12px;
  margin: 8px 0;
}

.route-point {
  display: flex;
  align-items: flex-start;
  gap: 10px;

  strong {
    display: block;
    color: #0f172a;
    font-size: 14px;
    line-height: 1.4;
  }
}

.route-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  flex-shrink: 0;
  margin-top: 4px;
}

.pickup-dot {
  background: #14b8a6;
  box-shadow: 0 0 0 3px rgba(20, 184, 166, 0.2);
}

.deliver-dot {
  background: #f59e0b;
  box-shadow: 0 0 0 3px rgba(245, 158, 11, 0.2);
}

.route-line {
  width: 2px;
  height: 18px;
  margin: 2px 0 2px 4px;
  background: linear-gradient(180deg, #14b8a6, #f59e0b);
  border-radius: 1px;
}

.route-label {
  display: block;
  color: #94a3b8;
  font-size: 11px;
  margin-bottom: 2px;
}

.order-foot {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
  margin-top: 10px;
  flex-wrap: wrap;
}

.order-foot__info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.pickup-code-tag {
  display: inline-flex;
  align-items: center;
  border-radius: 8px;
  background: rgba(14, 165, 233, 0.1);
  color: #0284c7;
  padding: 4px 10px;
  font-size: 12px;
  font-weight: 700;
}

.order-amount {
  font-size: 20px;
  font-weight: 900;
  color: #0f766e;
  letter-spacing: -0.03em;
}

.order-actions {
  display: flex;
  gap: 8px;
}

.pagination-row {
  display: flex;
  justify-content: center;
  margin-top: 16px;
}

@media (max-width: 640px) {
  .two-column,
  .filter-row {
    grid-template-columns: 1fr;
  }

  .created-card {
    flex-direction: column;
    align-items: stretch;
  }

  .created-actions {
    flex-direction: row;
  }

  .order-foot {
    flex-direction: column;
    align-items: stretch;
  }

  .order-actions {
    justify-content: flex-end;
  }
}

@media (max-width: 420px) {
  .two-column {
    grid-template-columns: 1fr;
  }
}

.order-form :deep(.el-form-item__error) {
  font-weight: 700;
  font-size: 13px;
  padding-top: 4px;
}

@media (min-width: 768px) {
  .relay-orders-page {
    max-width: 560px;
    margin: 0 auto;
  }
}
</style>
