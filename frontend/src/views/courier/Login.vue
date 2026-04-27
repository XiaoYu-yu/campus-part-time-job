<template>
  <div class="parttime-login-page">
    <div class="login-shell">
      <section class="brand-panel">
        <span class="entry-badge">兼职端入口</span>
        <h1>兼职端登录</h1>
        <p>日常接单、取餐、送达和异常上报都从这里进入。首次报名和审核仍保留在用户端入驻入口。</p>

        <div class="guide-grid">
          <div class="guide-item">
            <span>首次入驻</span>
            <strong>用户端 onboarding</strong>
          </div>
          <div class="guide-item">
            <span>日常工作</span>
            <strong>手机号 + 密码换取 courier token</strong>
          </div>
          <div class="guide-item">
            <span>成功后承接</span>
            <strong>直接进入兼职工作台</strong>
          </div>
        </div>
      </section>

      <section class="form-panel">
        <div class="panel-heading">
          <h2>使用已审核通过的兼职账号登录</h2>
          <p>本页复用现有 `/api/campus/courier/auth/token`，不新增第二套认证流。</p>
        </div>

        <el-alert
          title="测试推荐账号"
          description="优先使用 13900139001 / 123456。该账号在本地 H2 数据中已具备兼职资料、审核通过且启用。"
          type="info"
          :closable="false"
          show-icon
          class="page-alert"
        />

        <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="form.phone" placeholder="请输入兼职账号手机号" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input
              v-model="form.password"
              type="password"
              show-password
              placeholder="请输入登录密码"
              @keyup.enter="handleLogin"
            />
          </el-form-item>
          <el-button type="primary" class="login-btn" :loading="loading" @click="handleLogin">
            登录兼职端
          </el-button>
        </el-form>

        <div class="tips">
          <p>测试账号：13900139001</p>
          <p>测试密码：123456</p>
        </div>

        <div class="secondary-actions">
          <el-button @click="goToOnboarding">首次报名 / 重新提交资料</el-button>
          <el-button text type="primary" @click="goToUserLogin">返回用户端登录</el-button>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { applyCourierToken } from '../../api/campus-customer'
import { useCourierStore } from '../../stores/courier'

const router = useRouter()
const route = useRoute()
const courierStore = useCourierStore()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  phone: '13900139001',
  password: '123456'
})

const rules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

const resolveRedirect = () => {
  const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : ''
  return redirect || '/parttime/workbench'
}

const handleLogin = async () => {
  try {
    await formRef.value.validate()
    loading.value = true
    const result = await applyCourierToken({
      phone: form.phone,
      password: form.password
    })
    courierStore.login(result.token, result.courierProfile)
    ElMessage.success('兼职端登录成功')
    router.push(resolveRedirect())
  } catch (error) {
    console.error('兼职端登录失败:', error)
  } finally {
    loading.value = false
  }
}

const goToOnboarding = () => {
  router.push('/user/campus/courier-onboarding')
}

const goToUserLogin = () => {
  router.push('/user/login')
}
</script>

<style scoped lang="scss">
.parttime-login-page {
  min-height: 100vh;
  padding: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  background:
    radial-gradient(circle at top left, rgba(19, 152, 173, 0.12), transparent 36%),
    radial-gradient(circle at bottom right, rgba(91, 192, 190, 0.14), transparent 30%),
    linear-gradient(180deg, #f5fbfc 0%, #eef7f7 100%);
}

.login-shell {
  width: min(1040px, 100%);
  display: grid;
  grid-template-columns: minmax(320px, 1.1fr) minmax(320px, 0.9fr);
  gap: 18px;
}

.brand-panel,
.form-panel {
  background: rgba(255, 255, 255, 0.88);
  border: 1px solid rgba(255, 255, 255, 0.82);
  border-radius: 28px;
  box-shadow: 0 18px 44px rgba(26, 87, 100, 0.1);
  backdrop-filter: blur(18px);
}

.brand-panel {
  padding: 32px;
}

.entry-badge {
  display: inline-flex;
  align-items: center;
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba(33, 166, 180, 0.12);
  color: #0f7f8e;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.04em;
}

.brand-panel h1 {
  margin: 16px 0 10px;
  font-size: 40px;
  line-height: 1.1;
  color: #102a43;
}

.brand-panel > p {
  margin: 0;
  color: #486581;
  line-height: 1.8;
  font-size: 15px;
}

.guide-grid {
  margin-top: 28px;
  display: grid;
  gap: 12px;
}

.guide-item {
  border-radius: 18px;
  padding: 16px 18px;
  background: rgba(255, 255, 255, 0.76);
  border: 1px solid rgba(180, 216, 220, 0.86);
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.guide-item span {
  color: #7b8794;
  font-size: 13px;
}

.guide-item strong {
  color: #102a43;
  font-size: 16px;
}

.form-panel {
  padding: 28px;
}

.panel-heading h2 {
  margin: 0 0 8px;
  font-size: 24px;
  color: #102a43;
}

.panel-heading p {
  margin: 0 0 18px;
  color: #7b8794;
  line-height: 1.7;
}

.page-alert {
  margin-bottom: 18px;
}

.login-btn {
  width: 100%;
  margin-top: 8px;
  border: none;
  border-radius: 16px;
  min-height: 44px;
  font-weight: 800;
  background: linear-gradient(135deg, #0f766e 0%, #0ea5e9 100%);
  box-shadow: 0 14px 28px rgba(14, 165, 233, 0.2);
}

.tips {
  margin-top: 18px;
  border-radius: 16px;
  padding: 14px 16px;
  background: #f6fbfb;
  color: #486581;
  line-height: 1.8;
  font-size: 13px;
}

.secondary-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 18px;
}

@media (max-width: 900px) {
  .login-shell {
    grid-template-columns: 1fr;
  }

  .brand-panel h1 {
    font-size: 32px;
  }
}

@media (max-width: 640px) {
  .parttime-login-page {
    padding: 16px;
  }

  .brand-panel,
  .form-panel {
    padding: 22px;
    border-radius: 22px;
  }

  .secondary-actions {
    flex-direction: column;
  }
}
</style>
