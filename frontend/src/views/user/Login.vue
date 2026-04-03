<template>
  <div class="user-login-page">
    <div class="login-card">
      <h1>用户登录</h1>
      <p class="subtitle">使用测试用户账号进入点餐端</p>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" @keyup.enter="handleLogin" />
        </el-form-item>
        <el-button type="primary" class="login-btn" :loading="loading" @click="handleLogin">登录并开始点餐</el-button>
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
    ElMessage.success('欢迎回来，开始点餐吧')
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
  background: linear-gradient(135deg, #fff5ec 0%, #fff 100%);
  padding: 24px;
}

.login-card {
  width: 100%;
  max-width: 420px;
  background: #fff;
  border-radius: 20px;
  padding: 32px 28px;
  box-shadow: 0 20px 40px rgba(255, 125, 0, 0.12);

  h1 {
    margin: 0 0 8px;
    font-size: 28px;
    color: #303133;
  }

  .subtitle {
    margin: 0 0 24px;
    color: #909399;
  }

  .login-btn {
    width: 100%;
    margin-top: 8px;
  }

  .tips {
    margin-top: 20px;
    font-size: 13px;
    color: #606266;
    line-height: 1.8;
  }
}
</style>
