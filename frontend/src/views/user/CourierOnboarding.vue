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

        <div v-if="eligibility.eligible" class="eligibility-banner">
          当前账号已通过审核，可继续申请 courier token。
        </div>
      </section>
    </div>
  </UserLayout>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import UserLayout from '../../layout/UserLayout.vue'
import {
  getCourierOnboardingProfile,
  getCourierOnboardingReviewStatus,
  getCourierTokenEligibility,
  submitCourierOnboardingProfile
} from '../../api/campus-customer'

const campusZones = ['渝中校区']
const dormBuildings = ['竹园', '杏园', '李园', '桃园', '梅园', '馨园']

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

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.eligibility-banner {
  margin-top: 16px;
  padding: 12px 14px;
  border-radius: 12px;
  background: #f0f9eb;
  color: #2f7d32;
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
