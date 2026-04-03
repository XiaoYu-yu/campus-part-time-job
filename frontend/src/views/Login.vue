<template>
  <div class="login-container">
    <div class="login-bg-decoration">
      <div class="bg-circle bg-circle-1"></div>
      <div class="bg-circle bg-circle-2"></div>
      <div class="bg-circle bg-circle-3"></div>
    </div>
    
    <div class="login-wrapper">
      <div class="login-left">
        <div class="brand-section">
          <div class="logo-icon">
            <svg viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M24 4L4 16L24 28L44 16L24 4Z" fill="url(#gradient1)"/>
              <path d="M4 32L24 44L44 32" stroke="url(#gradient2)" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M4 16L24 28L44 16" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <defs>
                <linearGradient id="gradient1" x1="4" y1="4" x2="44" y2="44" gradientUnits="userSpaceOnUse">
                  <stop stop-color="#ff6b35"/>
                  <stop offset="1" stop-color="#ec4f1a"/>
                </linearGradient>
                <linearGradient id="gradient2" x1="4" y1="32" x2="44" y2="32" gradientUnits="userSpaceOnUse">
                  <stop stop-color="#ff6b35"/>
                  <stop offset="1" stop-color="#ec4f1a"/>
                </linearGradient>
              </defs>
            </svg>
          </div>
          <h1 class="brand-title">智慧餐饮管理系统</h1>
          <p class="brand-subtitle">专业 · 高效 · 智能</p>
        </div>
        
        <div class="login-form-section">
          <h2 class="form-title">欢迎回来</h2>
          <p class="form-subtitle">请登录您的账号</p>
          
          <el-form
            ref="loginFormRef"
            :model="loginForm"
            :rules="rules"
            label-position="top"
            class="login-form"
          >
            <el-form-item label="手机号" prop="phone">
              <el-input
                v-model="loginForm.phone"
                placeholder="请输入手机号"
                size="large"
                clearable
              >
                <template #prefix>
                  <el-icon><User /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            
            <el-form-item label="密码" prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                size="large"
                show-password
                clearable
                @keyup.enter="handleLogin"
              >
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            
            <el-form-item>
              <el-button
                type="primary"
                size="large"
                class="login-button"
                :loading="loading"
                @click="handleLogin"
              >
                <span v-if="!loading">登 录</span>
                <span v-else>登录中...</span>
              </el-button>
            </el-form-item>
          </el-form>
          
          <div class="login-tips">
            <p>测试账号：13800138000</p>
            <p>测试密码：123456</p>
          </div>
        </div>
      </div>
      
      <div class="login-right">
        <div class="right-content">
          <div class="feature-list">
            <div class="feature-item">
              <div class="feature-icon">
                <el-icon :size="32"><DataAnalysis /></el-icon>
              </div>
              <div class="feature-text">
                <h4>数据可视化</h4>
                <p>实时数据分析与报表</p>
              </div>
            </div>
            
            <div class="feature-item">
              <div class="feature-icon">
                <el-icon :size="32"><ShoppingCart /></el-icon>
              </div>
              <div class="feature-text">
                <h4>订单管理</h4>
                <p>高效处理订单流程</p>
              </div>
            </div>
            
            <div class="feature-item">
              <div class="feature-icon">
                <el-icon :size="32"><Goods /></el-icon>
              </div>
              <div class="feature-text">
                <h4>商品管理</h4>
                <p>灵活配置菜品与套餐</p>
              </div>
            </div>
            
            <div class="feature-item">
              <div class="feature-icon">
                <el-icon :size="32"><UserFilled /></el-icon>
              </div>
              <div class="feature-text">
                <h4>员工管理</h4>
                <p>完善的权限管理系统</p>
              </div>
            </div>
          </div>
          
          <div class="stat-number">
            <div class="stat-item">
              <span class="stat-value">1000+</span>
              <span class="stat-label">门店使用</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">50K+</span>
              <span class="stat-label">日处理订单</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'
import { User, Lock, DataAnalysis, ShoppingCart, Goods, UserFilled } from '@element-plus/icons-vue'
import { login } from '../api/employee'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
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
    await loginFormRef.value.validate()
    loading.value = true
    
    const res = await login({
      phone: loginForm.phone,
      password: loginForm.password
    })
    
    userStore.login(res.employee, res.token)
    
    ElMessage.success('登录成功，欢迎回来！')
    router.push('/dashboard')
  } catch (error) {
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-container {
  width: 100vw;
  height: 100vh;
  background: linear-gradient(135deg, #fef7f5 0%, #fff5f0 50%, #fff 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.login-bg-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;

  .bg-circle {
    position: absolute;
    border-radius: 50%;
    filter: blur(60px);
    opacity: 0.4;
  }

  .bg-circle-1 {
    width: 400px;
    height: 400px;
    background: #ff6b35;
    top: -100px;
    right: -100px;
  }

  .bg-circle-2 {
    width: 300px;
    height: 300px;
    background: #f59e0b;
    bottom: -50px;
    left: -50px;
  }

  .bg-circle-3 {
    width: 250px;
    height: 250px;
    background: #ff874d;
    top: 50%;
    left: 30%;
  }
}

.login-wrapper {
  width: 1000px;
  height: 600px;
  background-color: #fff;
  border-radius: 24px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.15);
  display: flex;
  overflow: hidden;
  position: relative;
  z-index: 1;
}

.login-left {
  flex: 1;
  padding: 40px 50px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.brand-section {
  margin-bottom: 40px;
  text-align: center;

  .logo-icon {
    width: 64px;
    height: 64px;
    margin: 0 auto 16px;
    animation: float 3s ease-in-out infinite;
  }

  .brand-title {
    font-size: 26px;
    font-weight: 700;
    color: #18181b;
    margin: 0 0 8px 0;
    letter-spacing: -0.5px;
  }

  .brand-subtitle {
    font-size: 14px;
    color: #71717a;
    margin: 0;
    font-weight: 500;
  }
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-8px); }
}

.login-form-section {
  .form-title {
    font-size: 24px;
    font-weight: 700;
    color: #18181b;
    margin: 0 0 6px 0;
  }

  .form-subtitle {
    font-size: 14px;
    color: #71717a;
    margin: 0 0 32px 0;
  }
}

.login-form {
  :deep(.el-form-item) {
    margin-bottom: 24px;

    .el-form-item__label {
      font-size: 14px;
      font-weight: 600;
      color: #3f3f46;
      margin-bottom: 8px;
      padding: 0;
    }
  }

  :deep(.el-input__wrapper) {
    padding: 8px 16px;
    box-shadow: 0 0 0 1px #e4e4e7 inset;
    transition: all 0.2s ease;

    &:hover {
      box-shadow: 0 0 0 1px #ff874d inset;
    }

    &.is-focus {
      box-shadow: 0 0 0 2px rgba(255, 107, 53, 0.15), 0 0 0 1px #ff6b35 inset;
    }

    .el-input__inner {
      font-size: 15px;
    }

    .el-input__prefix {
      color: #a1a1aa;
    }
  }
}

.login-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  margin-top: 8px;
  letter-spacing: 1px;
}

.login-tips {
  margin-top: 24px;
  padding: 16px;
  background: #fafafa;
  border-radius: 12px;
  border: 1px dashed #e4e4e7;

  p {
    margin: 0;
    font-size: 13px;
    color: #71717a;
    line-height: 1.8;

    &:first-child {
      margin-bottom: 4px;
    }
  }
}

.login-right {
  width: 420px;
  background: linear-gradient(135deg, #ff6b35 0%, #ec4f1a 50%, #c63c10 100%);
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 60%);
    animation: rotate 30s linear infinite;
  }

  @keyframes rotate {
    from { transform: rotate(0deg); }
    to { transform: rotate(360deg); }
  }
}

.right-content {
  position: relative;
  z-index: 1;
  height: 100%;
  padding: 50px 40px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.feature-list {
  .feature-item {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 20px;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 16px;
    margin-bottom: 16px;
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.15);
    transition: all 0.3s ease;

    &:hover {
      background: rgba(255, 255, 255, 0.18);
      transform: translateX(8px);
    }

    .feature-icon {
      width: 56px;
      height: 56px;
      background: rgba(255, 255, 255, 0.2);
      border-radius: 14px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
    }

    .feature-text {
      h4 {
        font-size: 16px;
        font-weight: 600;
        color: white;
        margin: 0 0 4px 0;
      }

      p {
        font-size: 13px;
        color: rgba(255, 255, 255, 0.8);
        margin: 0;
      }
    }
  }
}

.stat-number {
  display: flex;
  gap: 40px;
  padding-top: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.2);

  .stat-item {
    display: flex;
    flex-direction: column;

    .stat-value {
      font-size: 32px;
      font-weight: 700;
      color: white;
      line-height: 1.2;
    }

    .stat-label {
      font-size: 13px;
      color: rgba(255, 255, 255, 0.8);
      margin-top: 4px;
    }
  }
}

@media (max-width: 992px) {
  .login-wrapper {
    width: 90%;
    max-width: 500px;
  }

  .login-right {
    display: none;
  }

  .login-left {
    padding: 40px 30px;
  }
}
</style>
