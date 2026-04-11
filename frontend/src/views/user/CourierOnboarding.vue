<template>
  <UserLayout>
    <div class="onboarding-page">
      <section class="card status-card">
        <div>
          <div class="title-row">
            <h2>配送员入驻</h2>
            <span class="entry-badge">customer 前置入口</span>
          </div>
          <p>通过 customer 侧 onboarding 新入口提交资料，不影响现有 courier token 链路。</p>
        </div>
        <div class="status-pill" :class="statusClass(reviewStatus.reviewStatus)">
          {{ reviewStatus.reviewStatus || '未提交资料' }}
        </div>
      </section>

      <section class="flow-strip">
        <div class="flow-step">
          <span>1</span>
          <strong>提交资料</strong>
          <p>使用当前 customer 登录态填写配送员资料。</p>
        </div>
        <div class="flow-step">
          <span>2</span>
          <strong>等待审核</strong>
          <p>查看审核状态、审核说明和 token 资格。</p>
        </div>
        <div class="flow-step">
          <span>3</span>
          <strong>申请 token</strong>
          <p>审核通过且启用后，沿用现有 courier token 接口。</p>
        </div>
      </section>

      <section class="card">
        <div class="section-heading compact-heading">
          <div>
            <h3>审核与资格概览</h3>
            <p>这里只读展示当前入驻资料的审核结果和 token 申请资格，便于演示前置状态。</p>
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
            <span>token 资格</span>
            <strong>{{ eligibility.eligible ? '已具备资格' : '暂不可申请' }}</strong>
          </div>
          <div class="summary-item">
            <span>资格提示</span>
            <strong>{{ eligibility.message || '暂无提示' }}</strong>
          </div>
        </div>
      </section>

      <section class="card">
        <div class="section-heading">
          <div>
            <h3>courier token 申请</h3>
            <p>沿用现有 `/api/campus/courier/auth/token`。仅在审核通过且启用后开放，仍使用当前登录账号手机号 + 密码申请。</p>
          </div>
          <div class="status-pill" :class="eligibility.eligible ? 'approved' : 'pending'">
            {{ eligibility.eligible ? '可申请' : '暂不可申请' }}
          </div>
        </div>

        <el-alert
          :title="eligibility.eligible ? '已满足 courier token 申请条件' : '当前还不满足 courier token 申请条件'"
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
            <span>申请凭证</span>
            <strong>当前手机号 + 登录密码</strong>
          </div>
          <div class="guide-item">
            <span>成功后承接</span>
            <strong>写入 courier_token 并跳转 workbench</strong>
          </div>
        </div>

        <div class="token-meta">
          <div class="summary-item">
            <span>当前登录手机号</span>
            <strong>{{ currentLoginPhone || '暂无' }}</strong>
          </div>
          <div class="summary-item">
            <span>申请资格</span>
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
            <el-button type="primary" :loading="tokenApplying" @click="applyToken">申请 courier token</el-button>
          </div>
        </template>

        <div v-if="tokenResult.token" class="token-result result-panel">
          <div class="summary-grid">
            <div class="summary-item">
              <span>申请结果</span>
              <strong>已成功获取 courier token</strong>
            </div>
            <div class="summary-item">
              <span>配送员资料ID</span>
              <strong>{{ tokenResult.courierProfile?.id || '暂无' }}</strong>
            </div>
            <div class="summary-item">
              <span>审核状态</span>
              <strong>{{ tokenResult.courierProfile?.reviewStatus || '暂无' }}</strong>
            </div>
            <div class="summary-item">
              <span>本地存储</span>
              <strong>courier_token / courier_profile 已更新</strong>
            </div>
          </div>
          <el-form label-position="top" class="token-form">
            <el-form-item label="返回的 token">
              <el-input :model-value="tokenResult.token" type="textarea" :rows="3" readonly />
            </el-form-item>
          </el-form>
          <div class="form-actions">
            <el-button type="primary" @click="goToCourierWorkbench">前往 courier 工作台</el-button>
          </div>
        </div>
      </section>

      <section class="card">
        <div class="section-heading compact-heading">
          <div>
            <h3>资料表单</h3>
            <p>提交或重提资料仍沿用现有 onboarding 接口；本轮只优化表单说明层级，不改变提交体和按钮行为。</p>
          </div>
        </div>
        <el-form label-position="top" class="onboarding-form">
          <div class="form-section-title">
            <h4>基础与校区信息</h4>
            <p>用于管理员审核和后续 workbench 身份展示。</p>
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
            <p>只影响资料记录展示；`enabledWorkInOwnBuilding` 提交时仍按后端 DTO 期望转为 1 / 0。</p>
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

const campusZones = ['渝中校区']
const dormBuildings = ['竹园', '杏园', '李园', '桃园', '梅园', '馨园']
const router = useRouter()
const customerStore = useCustomerStore()

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
  const [profileRes, reviewRes, eligibilityRes] = await Promise.all([
    getCourierOnboardingProfile(),
    getCourierOnboardingReviewStatus(),
    getCourierTokenEligibility()
  ])
  fillForm(profileRes)
  reviewStatus.value = reviewRes
  eligibility.value = eligibilityRes
}

const goToCourierWorkbench = () => {
  router.push('/courier/workbench')
}

const applyToken = async () => {
  if (!eligibility.value.eligible) {
    ElMessage.warning(eligibility.value.message || '当前暂不具备申请 courier token 的资格')
    return
  }
  const password = courierAuth.password.trim()
  if (!password) {
    ElMessage.warning('请输入当前登录账号密码')
    return
  }
  if (!currentLoginPhone.value) {
    ElMessage.error('当前登录手机号缺失，无法申请 courier token')
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
    localStorage.setItem('courier_token', tokenResult.token)
    localStorage.setItem('courier_profile', JSON.stringify(tokenResult.courierProfile || {}))
    courierAuth.password = ''
    ElMessage.success('courier token 申请成功')
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
  ElMessage.success('配送员入驻资料已提交')
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
  padding: 16px;
}

.card {
  background: white;
  border-radius: 16px;
  padding: 16px;
  margin-bottom: 14px;
}

.status-card {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
}

.status-card h2 {
  margin: 0 0 6px;
}

.status-card p {
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

.entry-badge {
  display: inline-flex;
  align-items: center;
  border-radius: 999px;
  padding: 4px 10px;
  background: #ecf5ff;
  color: #337ecc;
  font-size: 12px;
  font-weight: 600;
}

.flow-strip {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
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
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 22px;
  height: 22px;
  border-radius: 999px;
  background: #ecf5ff;
  color: #337ecc;
  font-size: 12px;
  font-weight: 700;
  margin-bottom: 8px;
}

.flow-step strong {
  display: block;
  margin-bottom: 6px;
}

.flow-step p {
  margin: 0;
  color: #909399;
  font-size: 13px;
  line-height: 1.5;
}

.status-pill {
  padding: 8px 14px;
  border-radius: 999px;
  font-weight: 600;
}

.status-pill.approved {
  background: #f0f9eb;
  color: #67c23a;
}

.status-pill.pending {
  background: #ecf5ff;
  color: #409eff;
}

.status-pill.rejected {
  background: #fef0f0;
  color: #f56c6c;
}

.status-pill.disabled {
  background: #f4f4f5;
  color: #909399;
}

.summary-grid,
.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
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
}

.section-heading p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.compact-heading {
  align-items: flex-start;
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

.token-guide {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-top: 14px;
}

.guide-item {
  background: #f8fafc;
  border-radius: 12px;
  padding: 12px;
}

.guide-item span {
  display: block;
  color: #909399;
  font-size: 13px;
  margin-bottom: 6px;
}

.guide-item strong {
  color: #18181b;
}

.onboarding-form {
  margin-top: 16px;
}

.token-form,
.token-meta,
.token-result {
  margin-top: 16px;
}

.result-panel {
  border-radius: 14px;
  border: 1px solid #e1f3d8;
  background: #fbfff8;
  padding: 14px;
}

.form-section-title {
  margin: 14px 0 12px;
}

.form-section-title:first-child {
  margin-top: 0;
}

.form-section-title h4 {
  margin: 0 0 6px;
}

.form-section-title p {
  margin: 0;
  color: #909399;
  font-size: 13px;
  line-height: 1.6;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

@media (max-width: 640px) {
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
</style>
