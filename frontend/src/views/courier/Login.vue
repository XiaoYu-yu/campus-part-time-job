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
  padding: 22px 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
}

.login-shell {
  width: min(430px, 100%);
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.brand-panel,
.form-panel {
  background: #ffffff;
  border: 1px solid #e4e4e7;
  border-radius: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}

.brand-panel {
  position: relative;
  overflow: hidden;
  padding: 28px 22px 24px;
  min-height: auto;
  background: linear-gradient(135deg, #eefdfa, #f0fdfa);

  > * {
    position: relative;
    z-index: 1;
  }
}

.entry-badge {
  display: inline-flex;
  align-items: center;
  padding: 5px 12px;
  border-radius: 6px;
  background: rgba(15, 159, 143, 0.1);
  color: #0f9f8f;
  font-size: 12px;
  font-weight: 700;
}

.brand-panel h1 {
  margin: 14px 0 8px;
  font-size: 26px;
  line-height: 1.15;
  color: #18181b;
}

.brand-panel > p {
  margin: 0;
  color: #52525b;
  line-height: 1.7;
  font-size: 14px;
}

.guide-grid {
  margin-top: 20px;
  display: grid;
  gap: 10px;
}

.guide-item {
  border-radius: 12px;
  padding: 14px 16px;
  background: #ffffff;
  border: 1px solid #e4e4e7;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.guide-item span {
  color: #71717a;
  font-size: 13px;
}

.guide-item strong {
  color: #18181b;
  font-size: 14px;
}

.form-panel {
  padding: 22px;
}

.panel-heading h2 {
  margin: 0 0 8px;
  font-size: 20px;
  color: #18181b;
}

.panel-heading p {
  margin: 0 0 18px;
  color: #71717a;
  line-height: 1.6;
  font-size: 14px;
}

.page-alert {
  margin-bottom: 18px;
}

.login-btn {
  width: 100%;
  margin-top: 8px;
  border: none;
  border-radius: 12px;
  min-height: 48px;
  font-weight: 700;
  background: #0f9f8f;
  font-size: 15px;
}

.tips {
  margin-top: 18px;
  border-radius: 12px;
  padding: 14px 16px;
  background: #fafafa;
  border: 1px solid #e4e4e7;
  color: #52525b;
  line-height: 1.8;
  font-size: 13px;
}

.secondary-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 18px;
}

@media (max-width: 640px) {
  .parttime-login-page {
    padding: 16px;
  }

  .brand-panel,
  .form-panel {
    padding: 20px;
    border-radius: 14px;
  }

  .secondary-actions {
    flex-direction: column;
  }
}
</style>
