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
              <p>调用 `GET /api/campus/courier/orders/available`，这里只展示前 5 条，只做只读预览。</p>
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
import UserLayout from '../../layout/UserLayout.vue'
import {
  getCourierAvailableOrders,
  getCourierProfile,
  getCourierReviewStatus
} from '../../api/campus-courier'

const router = useRouter()
const loading = ref(false)
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

const hasCourierToken = computed(() => Boolean(localStorage.getItem('courier_token')))
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

@media (max-width: 640px) {
  .hero-card,
  .section-header {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
