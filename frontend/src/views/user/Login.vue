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
      <p class="subtitle">进入校园代送、订单回看、兼职入驻申请入口。</p>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" @keyup.enter="handleLogin" />
        </el-form-item>
        <el-button type="primary" class="login-btn" :loading="loading" @click="handleLogin">登录进入校园兼职</el-button>
      </el-form>
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
    await formRef.value.validate()
    loading.value = true
    const res = await customerLogin(form)
    customerStore.login(res.user, res.token)
    ElMessage.success('欢迎回来，进入校园兼职用户端')
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
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  background: #f5f5f5;
  padding: 24px;
}

.campus-scene {
  display: none;
}

.login-card {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 420px;
  background: #ffffff;
  border: 1px solid #e4e4e7;
  border-radius: 16px;
  padding: 32px 28px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

  .brand-pill {
    display: inline-flex;
    margin-bottom: 14px;
    padding: 6px 12px;
    border-radius: 6px;
    background: rgba(15, 159, 143, 0.1);
    color: #0f766e;
    font-size: 12px;
    font-weight: 800;
  }

  h1 {
    margin: 0 0 8px;
    font-size: 24px;
    color: #18181b;
  }

  .subtitle {
    margin: 0 0 24px;
    color: #71717a;
    line-height: 1.6;
    font-size: 14px;
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
</style>
