<template>
  <div class="user-login-page">
    <div class="login-card">
      <div class="brand-pill">校内兼职用户端</div>
      <h1>用户端登录</h1>
      <p class="subtitle">进入校园代送结果回看、兼职入驻和旧模块兼容入口。</p>
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
        <p>测试账号：13900139000</p>
        <p>测试密码：123456</p>
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
  background:
    radial-gradient(circle at 18% 12%, rgba(45, 212, 191, 0.28), transparent 30%),
    linear-gradient(180deg, #f0fdfa 0%, #f8fafc 100%);
  padding: 24px;
}

.login-card {
  width: 100%;
  max-width: 420px;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(15, 118, 110, 0.12);
  border-radius: 28px;
  padding: 32px 28px;
  box-shadow: 0 24px 56px rgba(15, 118, 110, 0.14);
  backdrop-filter: blur(18px);

  .brand-pill {
    display: inline-flex;
    margin-bottom: 14px;
    padding: 7px 12px;
    border-radius: 999px;
    background: rgba(20, 184, 166, 0.12);
    color: #0f766e;
    font-size: 12px;
    font-weight: 800;
  }

  h1 {
    margin: 0 0 8px;
    font-size: 28px;
    color: #0f172a;
  }

  .subtitle {
    margin: 0 0 24px;
    color: #64748b;
    line-height: 1.6;
  }

  .login-btn {
    width: 100%;
    margin-top: 8px;
    border: none;
    background: linear-gradient(135deg, #0f766e 0%, #0ea5e9 100%);
    box-shadow: 0 14px 28px rgba(14, 165, 233, 0.2);
  }

  .tips {
    margin-top: 20px;
    font-size: 13px;
    color: #475569;
    line-height: 1.8;
  }
}
</style>
