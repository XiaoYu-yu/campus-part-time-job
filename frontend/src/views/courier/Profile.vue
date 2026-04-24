<template>
  <ParttimeLayout>
    <div class="parttime-profile-page">
      <section class="profile-hero card">
        <div>
          <span class="eyebrow">兼职身份</span>
          <h2>{{ displayText(profile.realName, '兼职人员') }}</h2>
          <p>这里展示当前兼职端 token 对应的资料、审核状态和启用状态。日常接单从工作台进入，资料提交仍回用户端入驻入口。</p>
        </div>
        <el-tag :type="isApproved ? 'success' : 'warning'" size="large">
          {{ displayText(reviewStatus.reviewStatus || profile.reviewStatus, '待确认') }}
        </el-tag>
      </section>

      <section class="card">
        <div class="section-header">
          <div>
            <h3>身份状态</h3>
            <p>读取 `/api/campus/courier/profile` 与 `/api/campus/courier/review-status`，不新增后端接口。</p>
          </div>
          <el-button type="primary" plain :loading="loading" @click="loadProfile">刷新</el-button>
        </div>

        <div class="summary-grid">
          <div class="summary-item">
            <span>兼职资料 ID</span>
            <strong>{{ displayText(profile.id) }}</strong>
          </div>
          <div class="summary-item">
            <span>真实姓名</span>
            <strong>{{ displayText(profile.realName) }}</strong>
          </div>
          <div class="summary-item">
            <span>手机号</span>
            <strong>{{ displayText(profile.phone) }}</strong>
          </div>
          <div class="summary-item">
            <span>学号</span>
            <strong>{{ displayText(profile.studentNo) }}</strong>
          </div>
          <div class="summary-item">
            <span>校区</span>
            <strong>{{ displayText(profile.campusZone) }}</strong>
          </div>
          <div class="summary-item">
            <span>宿舍楼栋</span>
            <strong>{{ displayText(profile.dormBuilding) }}</strong>
          </div>
          <div class="summary-item">
            <span>启用状态</span>
            <strong>{{ enabledText }}</strong>
          </div>
          <div class="summary-item">
            <span>本楼栋优先</span>
            <strong>{{ Number(profile.enabledWorkInOwnBuilding) === 1 ? '已开启' : '未开启' }}</strong>
          </div>
        </div>
      </section>

      <section class="card">
        <div class="section-header compact">
          <div>
            <h3>审核说明</h3>
            <p>审核状态决定是否可以进入兼职端工作台接单。</p>
          </div>
        </div>
        <div class="notice-panel" :class="{ approved: isApproved }">
          <strong>{{ isApproved ? '当前账号可用于兼职端日常工作' : '当前账号仍需等待审核或启用' }}</strong>
          <span>{{ displayText(reviewStatus.reviewRemark || reviewStatus.reviewComment || profile.reviewRemark || profile.reviewComment, '暂无审核说明') }}</span>
        </div>
      </section>

      <section class="card">
        <div class="section-header compact">
          <div>
            <h3>快捷操作</h3>
            <p>只提供当前兼职端最小入口，不扩完整个人中心。</p>
          </div>
        </div>
        <div class="action-grid">
          <el-button type="primary" @click="goToWorkbench">进入工作台</el-button>
          <el-button @click="goToOnboarding">更新入驻资料</el-button>
          <el-button type="danger" plain @click="logout">退出兼职端</el-button>
        </div>
      </section>
    </div>
  </ParttimeLayout>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import ParttimeLayout from '../../layout/ParttimeLayout.vue'
import { getCourierProfile, getCourierReviewStatus } from '../../api/campus-courier'
import { useCourierStore } from '../../stores/courier'

const router = useRouter()
const courierStore = useCourierStore()
const loading = ref(false)

const profile = reactive({
  id: null,
  realName: '',
  phone: '',
  studentNo: '',
  campusZone: '',
  dormBuilding: '',
  enabled: 0,
  enabledWorkInOwnBuilding: 0,
  reviewStatus: '',
  reviewRemark: '',
  reviewComment: ''
})

const reviewStatus = reactive({
  reviewStatus: '',
  reviewRemark: '',
  reviewComment: '',
  enabled: 0
})

const isApproved = computed(() => (reviewStatus.reviewStatus || profile.reviewStatus) === 'APPROVED')
const enabledText = computed(() => Number(reviewStatus.enabled ?? profile.enabled) === 1 ? '已启用' : '未启用')

const displayText = (value, fallback = '暂无') => {
  if (value === null || value === undefined || value === '') {
    return fallback
  }
  return value
}

const assignProfile = (data = {}) => {
  Object.assign(profile, {
    id: data.id ?? null,
    realName: data.realName || '',
    phone: data.phone || '',
    studentNo: data.studentNo || '',
    campusZone: data.campusZone || '',
    dormBuilding: data.dormBuilding || '',
    enabled: data.enabled ?? 0,
    enabledWorkInOwnBuilding: data.enabledWorkInOwnBuilding ?? 0,
    reviewStatus: data.reviewStatus || '',
    reviewRemark: data.reviewRemark || '',
    reviewComment: data.reviewComment || ''
  })
  courierStore.setProfile(data)
}

const assignReviewStatus = (data = {}) => {
  Object.assign(reviewStatus, {
    reviewStatus: data.reviewStatus || '',
    reviewRemark: data.reviewRemark || '',
    reviewComment: data.reviewComment || '',
    enabled: data.enabled ?? 0
  })
}

const loadProfile = async () => {
  loading.value = true
  try {
    const [profileRes, reviewRes] = await Promise.all([
      getCourierProfile(),
      getCourierReviewStatus()
    ])
    assignProfile(profileRes)
    assignReviewStatus(reviewRes)
  } finally {
    loading.value = false
  }
}

const goToWorkbench = () => {
  router.push('/parttime/workbench')
}

const goToOnboarding = () => {
  router.push('/user/campus/courier-onboarding')
}

const logout = () => {
  courierStore.logout()
  ElMessage.success('已退出兼职端')
  router.push('/parttime/login')
}

onMounted(() => loadProfile())
</script>

<style scoped lang="scss">
.parttime-profile-page {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.card {
  background: rgba(255, 255, 255, 0.88);
  border: 1px solid rgba(211, 230, 232, 0.82);
  border-radius: 22px;
  padding: 18px;
  box-shadow: 0 16px 38px rgba(26, 87, 100, 0.08);
}

.profile-hero {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  background:
    radial-gradient(circle at right, rgba(137, 232, 213, 0.22), transparent 38%),
    rgba(255, 255, 255, 0.9);
}

.eyebrow {
  display: inline-flex;
  margin-bottom: 8px;
  color: #0f7f8e;
  font-size: 13px;
  font-weight: 800;
}

.profile-hero h2,
.section-header h3 {
  margin: 0;
  color: #102a43;
}

.profile-hero p,
.section-header p {
  margin: 8px 0 0;
  color: #627d98;
  line-height: 1.7;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 16px;
}

.section-header.compact {
  margin-bottom: 12px;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 12px;
}

.summary-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
  border-radius: 16px;
  padding: 14px;
  background: #f7fbfb;
  border: 1px solid #e4f0f2;
}

.summary-item span {
  color: #7b8794;
  font-size: 13px;
}

.summary-item strong {
  color: #102a43;
}

.notice-panel {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 14px 16px;
  border-radius: 16px;
  background: #fff8eb;
  border: 1px solid #f3dfb8;
  color: #8a5a00;
}

.notice-panel.approved {
  background: #eefcf8;
  border-color: #c3eee3;
  color: #067a68;
}

.action-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

@media (max-width: 640px) {
  .profile-hero,
  .section-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .action-grid {
    flex-direction: column;
  }
}
</style>
