<template>
  <div class="user-login-page">
    <div class="campus-scene" aria-hidden="true">
      <span class="scene-building"></span>
      <span class="scene-pin"></span>
      <span class="scene-box"></span>
    </div>
    <div class="login-card">
      <div class="brand-pill">校内兼职用户端</div>
      <h1>用户端登录</h1>
      <p class="subtitle">登录后可以发代送单、查订单，也可以报名做兼职。</p>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" @keyup.enter="handleLogin" />
        </el-form-item>
        <el-button type="primary" class="login-btn" :loading="loading" @click="handleLogin">登录</el-button>
      </el-form>
      <label class="agreement-line">
        <el-checkbox v-model="agreed" />
        <span>
          我已阅读并同意
          <router-link to="/legal/terms">《用户协议》</router-link>
          和
          <router-link to="/legal/privacy">《隐私政策》</router-link>
        </span>
      </label>
      <div class="tips">
        <strong>测试账号</strong>
        <p><span>手机号</span>13900139000</p>
        <p><span>密码</span>123456</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { customerLogin } from '../../api/customer'
import { useCustomerStore } from '../../stores/customer'

const router = useRouter()
const customerStore = useCustomerStore()
const formRef = ref(null)
const loading = ref(false)
const agreed = ref(false)

const form = reactive({
  phone: '',
  password: ''
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

const handleLogin = async () => {
  try {
    if (!agreed.value) {
      ElMessage.warning('请先阅读并同意用户协议和隐私政策')
      return
    }
    await formRef.value.validate()
    loading.value = true
    const res = await customerLogin(form)
    customerStore.login(res.user, res.token)
    ElMessage.success('欢迎回来')
    router.push('/user')
  } catch (error) {
    console.error('用户登录失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.user-login-page {
  min-height: 100vh;
  min-height: 100dvh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  background: linear-gradient(160deg, #f0fdfa 0%, #f5f5f5 40%, #f8fafc 100%);
  padding: calc(24px + env(safe-area-inset-top, 0px)) 24px calc(24px + env(safe-area-inset-bottom, 0px));
  overflow-x: hidden;
}

.campus-scene {
  display: none;
}

.login-card {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 420px;
  background: rgba(255, 255, 255, 0.82);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border: 1px solid #e4e4e7;
  border-radius: 16px;
  padding: 32px 28px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.06), 0 1px 4px rgba(0, 0, 0, 0.04);

  .brand-pill {
    display: inline-flex;
    margin-bottom: 14px;
    padding: 6px 14px;
    border-radius: 8px;
    background: rgba(15, 159, 143, 0.1);
    color: #0f766e;
    font-size: 12px;
    font-weight: 800;
    letter-spacing: 0.04em;
  }

  h1 {
    margin: 0 0 8px;
    font-size: 24px;
    color: #18181b;
    font-weight: 700;
  }

  .subtitle {
    margin: 0 0 24px;
    color: #71717a;
    line-height: 1.6;
    font-size: 14px;
  }

  :deep(.el-form-item__label) {
    font-size: 14px;
    color: #3f3f46;
    font-weight: 600;
  }

  :deep(.el-input__wrapper) {
    min-height: 44px;
    border-radius: 10px;
    box-shadow: 0 0 0 1px #e4e4e7 inset;
    transition: box-shadow 0.2s;
  }

  :deep(.el-input__wrapper:hover) {
    box-shadow: 0 0 0 1px #0f9f8f inset;
  }

  :deep(.el-input__wrapper.is-focus) {
    box-shadow: 0 0 0 1px #0f9f8f inset, 0 0 0 3px rgba(15, 159, 143, 0.12);
  }

  .login-btn {
    width: 100%;
    margin-top: 8px;
    border: none;
    min-height: 48px;
    border-radius: 12px;
    background: #0f9f8f;
    font-size: 15px;
    font-weight: 700;
    letter-spacing: 0.02em;
    transition: background 0.2s, box-shadow 0.2s;

    &:hover {
      background: #0d8a7e;
      box-shadow: 0 4px 14px rgba(15, 159, 143, 0.3);
    }

    &:active {
      background: #0b7a70;
    }
  }

  .tips {
    margin-top: 20px;
    border-radius: 12px;
    background: #fafafa;
    padding: 14px 16px;
    font-size: 13px;
    color: #52525b;
    line-height: 2;
    border: 1px solid #e4e4e7;

    strong {
      display: block;
      margin-bottom: 2px;
      color: #18181b;
    }

    span {
      display: inline-block;
      width: 70px;
      color: #0f9f8f;
      font-weight: 700;
    }
  }
}

.agreement-line {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  margin-top: 14px;
  color: #52525b;
  font-size: 12px;
  line-height: 1.7;

  span {
    display: inline-block;
    padding-top: 1px;
  }

  a {
    color: #0f766e;
    font-weight: 700;
    text-decoration: none;
  }
}

@media (max-width: 420px) {
  .user-login-page {
    padding: 16px;
    align-items: flex-start;
    padding-top: 48px;
  }

  .login-card {
    padding: 24px 18px;
    border-radius: 14px;
  }
}
</style>
