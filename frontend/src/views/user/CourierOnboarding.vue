<template>
  <UserLayout>
    <div v-loading="pageLoading" element-loading-text="正在加载报名信息..." class="onboarding-page">
      <section class="card status-card">
        <div>
          <div class="title-row">
            <h2>校园兼职入驻</h2>
            <span class="entry-badge">兼职报名入口</span>
          </div>
          <p>先在这里提交资料。审核通过后，就可以登录兼职端接单。</p>
        </div>
        <div class="status-pill" :class="statusClass(reviewStatus.reviewStatus)">
          {{ reviewStatus.reviewStatus || '未提交资料' }}
        </div>
      </section>

      <section class="flow-strip">
        <div class="flow-step">
          <span>1</span>
          <strong>提交资料</strong>
          <p>填写姓名、手机号、学号和宿舍信息。</p>
        </div>
        <div class="flow-step">
          <span>2</span>
          <strong>等待审核</strong>
          <p>看看资料有没有通过，哪里还需要补。</p>
        </div>
        <div class="flow-step">
          <span>3</span>
          <strong>开通接单资格</strong>
          <p>通过审核后，用当前账号登录兼职端接单。</p>
        </div>
      </section>

      <section class="card">
        <div class="section-heading compact-heading">
          <div>
            <h3>审核与资格概览</h3>
            <p>这里能看到报名审核结果，以及现在能不能去兼职端接单。</p>
          </div>
        </div>
        <div class="summary-grid">
          <div class="summary-item">
            <span>审核说明</span>
            <strong>{{ reviewStatus.reviewRemark || '暂无' }}</strong>
          </div>
          <div class="summary-item">
            <span>审核时间</span>
            <strong>{{ reviewStatus.reviewedAt || '暂无' }}</strong>
          </div>
          <div class="summary-item">
            <span>接单资格</span>
            <strong>{{ eligibility.eligible ? '已具备资格' : '暂不可申请' }}</strong>
          </div>
          <div class="summary-item">
            <span>资格提示</span>
            <strong>{{ eligibility.message || '暂无提示' }}</strong>
          </div>
        </div>
      </section>

      <section class="card" :class="{ 'token-section-eligible': eligibility.eligible }">
        <div class="section-heading">
          <div>
            <h3>开通兼职端登录</h3>
            <p>审核通过并启用后，输入当前账号密码，就能进入兼职工作台。</p>
          </div>
          <div class="status-pill" :class="eligibility.eligible ? 'approved' : 'pending'">
            {{ eligibility.eligible ? '可申请' : '暂不可申请' }}
          </div>
        </div>

        <el-alert
          :title="eligibility.eligible ? '现在可以开通兼职端登录' : '暂时还不能开通兼职端登录'"
          :description="eligibility.message || '暂无提示'"
          :type="eligibility.eligible ? 'success' : 'info'"
          :closable="false"
          show-icon
        />

        <div class="token-guide">
          <div class="guide-item">
            <span>开放条件</span>
            <strong>审核通过且账号已启用</strong>
          </div>
          <div class="guide-item">
            <span>登录方式</span>
            <strong>当前手机号 + 登录密码</strong>
          </div>
          <div class="guide-item">
            <span>开通成功后</span>
            <strong>直接进入兼职工作台</strong>
          </div>
        </div>

        <div class="token-meta">
          <div class="summary-item">
            <span>当前登录手机号</span>
            <strong>{{ currentLoginPhone || '暂无' }}</strong>
          </div>
          <div class="summary-item">
            <span>当前资格</span>
            <strong>{{ eligibility.eligible ? '可直接申请' : '请先完成审核' }}</strong>
          </div>
        </div>

        <template v-if="eligibility.eligible">
          <el-form label-position="top" class="token-form">
            <el-form-item label="登录密码确认">
              <el-input
                v-model="courierAuth.password"
                type="password"
                show-password
                placeholder="请输入当前登录账号密码"
                @keyup.enter="applyToken"
              />
            </el-form-item>
          </el-form>

          <div class="form-actions">
            <el-button type="primary" :loading="tokenApplying" @click="applyToken">开通兼职端登录</el-button>
          </div>
        </template>

        <div v-if="tokenResult.token" class="token-result result-panel">
          <div class="summary-grid">
            <div class="summary-item">
              <span>开通结果</span>
              <strong>兼职端登录已开通</strong>
            </div>
            <div class="summary-item">
              <span>兼职资料编号</span>
              <strong>{{ tokenResult.courierProfile?.id || '暂无' }}</strong>
            </div>
            <div class="summary-item">
              <span>审核状态</span>
              <strong>{{ tokenResult.courierProfile?.reviewStatus || '暂无' }}</strong>
            </div>
            <div class="summary-item">
              <span>登录状态</span>
              <strong>已保存，可直接进入工作台</strong>
            </div>
          </div>
          <el-form label-position="top" class="token-form">
            <el-form-item label="登录凭证">
              <el-input :model-value="tokenResult.token" type="textarea" :rows="3" readonly />
            </el-form-item>
          </el-form>
          <div class="form-actions">
            <el-button type="primary" @click="goToCourierWorkbench">前往兼职工作台</el-button>
          </div>
        </div>
      </section>

      <section class="card">
        <div class="section-heading compact-heading">
          <div>
            <h3>资料表单</h3>
            <p>资料有变化可以重新提交，管理员会按最新资料审核。</p>
          </div>
        </div>
        <el-form label-position="top" class="onboarding-form">
          <div class="form-section-title">
            <h4>基础与校区信息</h4>
            <p>用于管理员审核，也方便后续联系你。</p>
          </div>
          <div class="form-grid">
            <el-form-item label="真实姓名">
              <el-input v-model="form.realName" />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="form.phone" />
            </el-form-item>
            <el-form-item label="性别">
              <el-select v-model="form.gender" placeholder="请选择性别">
                <el-option label="男" value="MALE" />
                <el-option label="女" value="FEMALE" />
                <el-option label="其他" value="OTHER" />
              </el-select>
            </el-form-item>
            <el-form-item label="学号">
              <el-input v-model="form.studentNo" />
            </el-form-item>
            <el-form-item label="校区">
              <el-select v-model="form.campusZone" placeholder="请选择校区">
                <el-option v-for="item in campusZones" :key="item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
            <el-form-item label="宿舍楼栋">
              <el-select v-model="form.dormBuilding" placeholder="请选择宿舍楼栋">
                <el-option v-for="item in dormBuildings" :key="item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
            <el-form-item label="紧急联系人">
              <el-input v-model="form.emergencyContactName" />
            </el-form-item>
            <el-form-item label="紧急联系人电话">
              <el-input v-model="form.emergencyContactPhone" />
            </el-form-item>
          </div>

          <div class="form-section-title">
            <h4>接单偏好与备注</h4>
            <p>告诉平台你更愿意接哪些单，后续安排会更清楚。</p>
          </div>
          <el-form-item label="优先接本楼栋订单">
            <el-switch v-model="form.enabledWorkInOwnBuildingSwitch" />
          </el-form-item>

          <el-form-item label="备注">
            <el-input v-model="form.remark" type="textarea" :rows="3" maxlength="255" show-word-limit />
          </el-form-item>
        </el-form>

        <div class="form-actions">
          <el-button @click="loadAll">重新加载</el-button>
          <el-button type="primary" @click="submitProfile">提交/重提资料</el-button>
        </div>
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
  applyCourierToken,
  getCourierOnboardingProfile,
  getCourierOnboardingReviewStatus,
  getCourierTokenEligibility,
  submitCourierOnboardingProfile
} from '../../api/campus-customer'
import { useCustomerStore } from '../../stores/customer'
import { useCourierStore } from '../../stores/courier'

const campusZones = ['渝中校区']
const dormBuildings = ['竹园', '杏园', '李园', '桃园', '梅园', '馨园']
const router = useRouter()
const customerStore = useCustomerStore()
const courierStore = useCourierStore()

const reviewStatus = ref({
  reviewStatus: null,
  reviewRemark: '',
  reviewedAt: null,
  canApplyCourierToken: false
})

const eligibility = ref({
  eligible: false,
  reviewStatus: null,
  enabled: 0,
  message: ''
})

const pageLoading = ref(true)
const tokenApplying = ref(false)
const tokenResult = reactive({
  token: '',
  courierProfile: null
})

const courierAuth = reactive({
  password: ''
})

const form = reactive({
  realName: '',
  phone: '',
  gender: 'MALE',
  studentNo: '',
  campusZone: '渝中校区',
  dormBuilding: '',
  enabledWorkInOwnBuildingSwitch: true,
  remark: '',
  emergencyContactName: '',
  emergencyContactPhone: ''
})

const currentLoginPhone = computed(() => customerStore.userInfo?.phone || form.phone || '')

const fillForm = (profile) => {
  form.realName = profile.realName || ''
  form.phone = profile.phone || ''
  form.gender = profile.gender || 'MALE'
  form.studentNo = profile.studentNo || ''
  form.campusZone = profile.campusZone || '渝中校区'
  form.dormBuilding = profile.dormBuilding || ''
  form.enabledWorkInOwnBuildingSwitch = profile.enabledWorkInOwnBuilding !== 0
  form.remark = profile.remark || ''
  form.emergencyContactName = profile.emergencyContactName || ''
  form.emergencyContactPhone = profile.emergencyContactPhone || ''
}

const loadAll = async () => {
  pageLoading.value = true
  try {
    const [profileRes, reviewRes, eligibilityRes] = await Promise.all([
      getCourierOnboardingProfile(),
      getCourierOnboardingReviewStatus(),
      getCourierTokenEligibility()
    ])
    fillForm(profileRes)
    reviewStatus.value = reviewRes
    eligibility.value = eligibilityRes
  } finally {
    pageLoading.value = false
  }
}

const goToCourierWorkbench = () => {
  router.push('/parttime/workbench')
}

const applyToken = async () => {
  if (!eligibility.value.eligible) {
    ElMessage.warning(eligibility.value.message || '现在还不能开通兼职端登录')
    return
  }
  const password = courierAuth.password.trim()
  if (!password) {
    ElMessage.warning('请输入当前登录账号密码')
    return
  }
  if (!currentLoginPhone.value) {
    ElMessage.error('当前账号缺少手机号，暂时无法开通兼职端登录')
    return
  }

  tokenApplying.value = true
  try {
    const result = await applyCourierToken({
      phone: currentLoginPhone.value,
      password
    })
    tokenResult.token = result.token || ''
    tokenResult.courierProfile = result.courierProfile || null
    courierStore.login(tokenResult.token, tokenResult.courierProfile || {})
    courierAuth.password = ''
    ElMessage.success('兼职端登录已开通')
  } finally {
    tokenApplying.value = false
  }
}

const submitProfile = async () => {
  const payload = {
    realName: form.realName,
    phone: form.phone,
    gender: form.gender,
    studentNo: form.studentNo,
    campusZone: form.campusZone,
    dormBuilding: form.dormBuilding,
    enabledWorkInOwnBuilding: form.enabledWorkInOwnBuildingSwitch ? 1 : 0,
    remark: form.remark,
    emergencyContactName: form.emergencyContactName,
    emergencyContactPhone: form.emergencyContactPhone
  }
  await submitCourierOnboardingProfile(payload)
  ElMessage.success('兼职配送入驻资料已提交')
  await loadAll()
}

const statusClass = (status) => ({
  APPROVED: 'approved',
  PENDING: 'pending',
  REJECTED: 'rejected',
  DISABLED: 'disabled'
}[status] || 'pending')

onMounted(() => loadAll())
</script>

<style scoped lang="scss">
.onboarding-page {
  padding: 14px 14px 24px;
  overflow-x: hidden;
}

.card {
  background: #ffffff;
  border: 1px solid #e4e4e7;
  border-radius: 14px;
  padding: 16px;
  margin-bottom: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}

.status-card {
  position: relative;
  overflow: hidden;
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
  background: linear-gradient(135deg, #eefdfa, #f0fdfa);
  border-radius: 16px;
}

.status-card h2 {
  margin: 0 0 6px;
  color: #0f172a;
}

.status-card p {
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

.entry-badge {
  display: inline-flex;
  align-items: center;
  border-radius: 999px;
  padding: 5px 12px;
  background: rgba(15, 118, 110, 0.1);
  color: #0f766e;
  font-size: 12px;
  font-weight: 700;
}

.flow-strip {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-bottom: 14px;
}

.flow-step {
  background: #ffffff;
  border-radius: 12px;
  padding: 14px;
  border: 1px solid #e4e4e7;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.flow-step span {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border-radius: 8px;
  background: linear-gradient(135deg, #14b8a6, #38bdf8);
  color: #fff;
  font-size: 12px;
  font-weight: 800;
  margin-bottom: 8px;
}

.flow-step strong {
  display: block;
  margin-bottom: 6px;
  color: #0f172a;
}

.flow-step p {
  margin: 0;
  color: #64748b;
  font-size: 13px;
  line-height: 1.5;
}

.status-pill {
  padding: 8px 14px;
  border-radius: 999px;
  font-weight: 700;
  font-size: 13px;
}

.status-pill.approved {
  background: rgba(16, 185, 129, 0.1);
  color: #059669;
}

.status-pill.pending {
  background: rgba(14, 165, 233, 0.1);
  color: #0284c7;
}

.status-pill.rejected {
  background: rgba(239, 68, 68, 0.1);
  color: #dc2626;
}

.status-pill.disabled {
  background: rgba(100, 116, 139, 0.1);
  color: #64748b;
}

.summary-grid,
.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 12px;
}

.section-heading {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 16px;
}

.section-heading h3 {
  margin: 0 0 6px;
  color: #0f172a;
}

.section-heading p {
  margin: 0;
  color: #64748b;
  font-size: 14px;
}

.compact-heading {
  align-items: flex-start;
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

.token-guide {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-top: 14px;
}

.guide-item {
  background: #f8fafc;
  border-radius: 12px;
  padding: 14px;
}

.guide-item span {
  display: block;
  color: #94a3b8;
  font-size: 13px;
  margin-bottom: 6px;
}

.guide-item strong {
  color: #0f172a;
}

.onboarding-form {
  margin-top: 16px;
}

.onboarding-form :deep(.el-form-item__label),
.token-form :deep(.el-form-item__label) {
  font-weight: 800;
  color: #26364d;
}

.onboarding-form :deep(.el-input__wrapper),
.onboarding-form :deep(.el-select__wrapper),
.onboarding-form :deep(.el-textarea__inner),
.token-form :deep(.el-input__wrapper),
.token-form :deep(.el-textarea__inner) {
  min-height: 44px;
  border-radius: 10px;
}

.token-form,
.token-meta,
.token-result {
  margin-top: 16px;
}

.result-panel {
  border-radius: 12px;
  border: 1px solid rgba(16, 185, 129, 0.16);
  background: #f0fdfa;
  padding: 16px;
}

.form-section-title {
  margin: 14px 0 12px;
  padding: 12px 14px;
  border-radius: 12px;
  background: rgba(240, 253, 250, 0.58);
}

.form-section-title:first-child {
  margin-top: 0;
}

.form-section-title h4 {
  margin: 0 0 6px;
  color: #0f766e;
}

.form-section-title p {
  margin: 0;
  color: #64748b;
  font-size: 13px;
  line-height: 1.6;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.token-section-eligible {
  border: 2px solid rgba(16, 185, 129, 0.2);
  background: #ffffff;
}

.onboarding-page :deep(.el-alert--info) {
  border-radius: 14px;
  border: 1px solid rgba(14, 165, 233, 0.2);
  background: rgba(236, 248, 255, 0.9);
}

.onboarding-page :deep(.el-alert--success) {
  border-radius: 14px;
  border: 1px solid rgba(16, 185, 129, 0.2);
  background: rgba(236, 253, 245, 0.9);
}

@media (max-width: 480px) {
  .status-card {
    flex-direction: column;
    align-items: flex-start;
  }

  .flow-strip,
  .token-guide {
    grid-template-columns: 1fr;
  }

  .form-actions {
    flex-direction: column;
  }
}

@media (max-width: 360px) {
  .summary-grid,
  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
