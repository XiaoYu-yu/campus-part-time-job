<template>
  <UserLayout>
    <div class="onboarding-page">
      <section class="card status-card">
        <div>
          <h2>配送员入驻</h2>
          <p>通过 customer 侧 onboarding 新入口提交资料，不影响现有 courier token 链路。</p>
        </div>
        <div class="status-pill" :class="statusClass(reviewStatus.reviewStatus)">
          {{ reviewStatus.reviewStatus || '未提交资料' }}
        </div>
      </section>

      <section class="card">
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

        <div v-if="tokenResult.token" class="token-result">
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
        </div>
      </section>

      <section class="card">
        <h3>资料表单</h3>
        <el-form label-position="top" class="onboarding-form">
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

.onboarding-form {
  margin-top: 16px;
}

.token-form,
.token-meta,
.token-result {
  margin-top: 16px;
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

  .form-actions {
    flex-direction: column;
  }
}
</style>
