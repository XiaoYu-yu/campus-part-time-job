<template>
  <div class="login-container">
    <div class="login-bg-decoration" aria-hidden="true">
      <div class="glass-orb glass-orb-1"></div>
      <div class="glass-orb glass-orb-2"></div>
      <div class="grid-plane"></div>
    </div>

    <div class="login-wrapper">
      <div class="login-left">
        <div class="brand-section">
          <div class="logo-icon">
            <svg viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M24 4L4 16L24 28L44 16L24 4Z" fill="url(#gradient1)" />
              <path d="M4 32L24 44L44 32" stroke="url(#gradient2)" stroke-width="3" stroke-linecap="round" stroke-linejoin="round" />
              <path d="M4 16L24 28L44 16" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
              <defs>
                <linearGradient id="gradient1" x1="4" y1="4" x2="44" y2="44" gradientUnits="userSpaceOnUse">
                  <stop stop-color="#8DEBFF" />
                  <stop offset="1" stop-color="#9BFFB8" />
                </linearGradient>
                <linearGradient id="gradient2" x1="4" y1="32" x2="44" y2="32" gradientUnits="userSpaceOnUse">
                  <stop stop-color="#8DEBFF" />
                  <stop offset="1" stop-color="#9BFFB8" />
                </linearGradient>
              </defs>
            </svg>
          </div>
          <div>
            <h1 class="brand-title">校内兼职运营台</h1>
            <p class="brand-subtitle">Campus Part-time Console</p>
          </div>
        </div>

        <div class="login-form-section">
          <span class="form-kicker">Admin access</span>
          <h2 class="form-title">欢迎回来</h2>
          <p class="form-subtitle">管理校园代送、兼职任务、异常和结算审计的试运营后台。</p>

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
                <span v-if="!loading">进入运营台</span>
                <span v-else>登录中...</span>
              </el-button>
            </el-form-item>
          </el-form>

          <div class="login-tips">
            <span>本地演示账号</span>
            <p>手机号：13800138000</p>
            <p>密码：123456</p>
          </div>
        </div>
      </div>

      <div class="login-right">
        <div class="right-content">
          <div class="preview-card">
            <span class="preview-label">Trial Operations</span>
            <h3>校园任务与配送闭环</h3>
            <p>从学生下单、兼职接单到异常、售后和结算审计，集中在一套轻量运营后台。</p>
          </div>

          <div class="feature-list">
            <div class="feature-item">
              <div class="feature-icon">
                <el-icon :size="30"><DataAnalysis /></el-icon>
              </div>
              <div class="feature-text">
                <h4>运营总览</h4>
                <p>订单、异常、售后和结算只读观察</p>
              </div>
            </div>

            <div class="feature-item">
              <div class="feature-icon">
                <el-icon :size="30"><ShoppingCart /></el-icon>
              </div>
              <div class="feature-text">
                <h4>代送任务</h4>
                <p>覆盖接单、取餐、送达与确认</p>
              </div>
            </div>

            <div class="feature-item">
              <div class="feature-icon">
                <el-icon :size="30"><Goods /></el-icon>
              </div>
              <div class="feature-text">
                <h4>异常闭环</h4>
                <p>历史记录、最小处理与审计留痕</p>
              </div>
            </div>

            <div class="feature-item">
              <div class="feature-icon">
                <el-icon :size="30"><UserFilled /></el-icon>
              </div>
              <div class="feature-text">
                <h4>兼职身份</h4>
                <p>资料审核、token 申请和 workbench 承接</p>
              </div>
            </div>
          </div>

          <div class="stat-number">
            <div class="stat-item">
              <span class="stat-value">1</span>
              <span class="stat-label">本地完整闭环</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">13+</span>
              <span class="stat-label">演示入口</span>
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
  position: fixed;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  width: auto;
  height: auto;
  overflow: hidden;
  background:
    radial-gradient(circle at 18% 18%, rgba(141, 235, 255, 0.26), transparent 28%),
    radial-gradient(circle at 78% 12%, rgba(155, 255, 184, 0.18), transparent 30%),
    linear-gradient(135deg, #0c1117 0%, #151d24 46%, #0e2023 100%);
}

.login-bg-decoration {
  position: absolute;
  inset: 0;
  z-index: 0;
  pointer-events: none;

  .glass-orb {
    position: absolute;
    border-radius: 50%;
    filter: blur(48px);
    opacity: 0.7;
  }

  .glass-orb-1 {
    top: -90px;
    right: 8%;
    width: 360px;
    height: 360px;
    background: rgba(45, 212, 191, 0.32);
  }

  .glass-orb-2 {
    bottom: -80px;
    left: 12%;
    width: 330px;
    height: 330px;
    background: rgba(56, 189, 248, 0.26);
  }

  .grid-plane {
    position: absolute;
    inset: 0;
    background-image:
      linear-gradient(rgba(255, 255, 255, 0.055) 1px, transparent 1px),
      linear-gradient(90deg, rgba(255, 255, 255, 0.055) 1px, transparent 1px);
    background-size: 54px 54px;
    mask-image: linear-gradient(180deg, rgba(0, 0, 0, 0.78), transparent 82%);
  }
}

.login-wrapper {
  position: relative;
  z-index: 1;
  display: flex;
  width: min(1080px, calc(100vw - 48px));
  min-height: 620px;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.16);
  border-radius: 30px;
  background: rgba(255, 255, 255, 0.08);
  box-shadow: 0 35px 90px rgba(0, 0, 0, 0.35);
  backdrop-filter: blur(26px);
}

.login-left {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 46px 54px;
  background: rgba(255, 255, 255, 0.76);
  backdrop-filter: blur(24px);
}

.brand-section {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 42px;

  .logo-icon {
    width: 58px;
    height: 58px;
    padding: 10px;
    border-radius: 20px;
    background: rgba(15, 23, 42, 0.88);
    box-shadow: 0 18px 40px rgba(15, 23, 42, 0.22);
  }

  .brand-title {
    margin: 0 0 4px 0;
    color: #101820;
    font-size: 24px;
    font-weight: 900;
    letter-spacing: -0.04em;
  }

  .brand-subtitle {
    margin: 0;
    color: #64748b;
    font-size: 12px;
    font-weight: 800;
    letter-spacing: 0.14em;
    text-transform: uppercase;
  }
}

.login-form-section {
  .form-kicker {
    display: inline-flex;
    margin-bottom: 12px;
    color: #0f766e;
    font-size: 12px;
    font-weight: 900;
    letter-spacing: 0.14em;
    text-transform: uppercase;
  }

  .form-title {
    margin: 0 0 10px 0;
    color: #0f172a;
    font-size: 34px;
    font-weight: 950;
    letter-spacing: -0.05em;
  }

  .form-subtitle {
    max-width: 360px;
    margin: 0 0 34px 0;
    color: #64748b;
    font-size: 14px;
    line-height: 1.7;
  }
}

.login-form {
  :deep(.el-form-item) {
    margin-bottom: 24px;

    .el-form-item__label {
      margin-bottom: 8px;
      padding: 0;
      color: #334155;
      font-size: 14px;
      font-weight: 700;
    }
  }

  :deep(.el-input__wrapper) {
    padding: 8px 16px;
    border-radius: 16px;
    background: rgba(248, 250, 252, 0.78);
    box-shadow: 0 0 0 1px rgba(148, 163, 184, 0.28) inset;
    transition: all 0.2s ease;

    &:hover {
      box-shadow: 0 0 0 1px rgba(20, 184, 166, 0.56) inset;
    }

    &.is-focus {
      box-shadow: 0 0 0 3px rgba(20, 184, 166, 0.14), 0 0 0 1px #14b8a6 inset;
    }

    .el-input__inner {
      font-size: 15px;
    }

    .el-input__prefix {
      color: #64748b;
    }
  }
}

.login-button {
  width: 100%;
  height: 48px;
  margin-top: 8px;
  border: none;
  border-radius: 16px;
  background: linear-gradient(135deg, #0f172a 0%, #0f766e 52%, #38bdf8 100%);
  box-shadow: 0 20px 44px rgba(15, 118, 110, 0.26);
  font-size: 16px;
  font-weight: 850;
  letter-spacing: 1px;
}

.login-tips {
  margin-top: 24px;
  padding: 16px;
  border: 1px dashed rgba(15, 118, 110, 0.22);
  border-radius: 16px;
  background: rgba(15, 23, 42, 0.04);

  span {
    display: inline-flex;
    margin-bottom: 6px;
    color: #0f766e;
    font-size: 12px;
    font-weight: 900;
    letter-spacing: 0.08em;
  }

  p {
    margin: 0;
    color: #64748b;
    font-size: 13px;
    line-height: 1.8;
  }
}

.login-right {
  position: relative;
  width: 470px;
  overflow: hidden;
  background:
    linear-gradient(160deg, rgba(15, 23, 42, 0.42), rgba(15, 118, 110, 0.28)),
    radial-gradient(circle at 72% 24%, rgba(190, 242, 100, 0.28), transparent 28%),
    linear-gradient(135deg, rgba(15, 23, 42, 0.92), rgba(17, 94, 89, 0.82));

  &::before {
    content: '';
    position: absolute;
    inset: -50%;
    background:
      linear-gradient(rgba(255,255,255,0.05) 1px, transparent 1px),
      linear-gradient(90deg, rgba(255,255,255,0.05) 1px, transparent 1px);
    background-size: 48px 48px;
    opacity: 0.35;
    animation: rotate 32s linear infinite;
  }

  @keyframes rotate {
    from { transform: rotate(0deg); }
    to { transform: rotate(360deg); }
  }
}

.right-content {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: 100%;
  padding: 44px 38px;
}

.preview-card {
  padding: 22px;
  margin-bottom: 22px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.12);
  color: #ffffff;
  backdrop-filter: blur(16px);

  .preview-label {
    display: inline-flex;
    margin-bottom: 12px;
    color: #bef264;
    font-size: 12px;
    font-weight: 900;
    letter-spacing: 0.12em;
    text-transform: uppercase;
  }

  h3 {
    margin: 0 0 10px;
    color: #ffffff;
    font-size: 24px;
    font-weight: 950;
    letter-spacing: -0.04em;
  }

  p {
    margin: 0;
    color: rgba(241, 245, 249, 0.78);
    line-height: 1.7;
  }
}

.feature-list {
  .feature-item {
    display: flex;
    align-items: center;
    gap: 14px;
    padding: 16px;
    margin-bottom: 12px;
    border: 1px solid rgba(255, 255, 255, 0.14);
    border-radius: 16px;
    background: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(10px);
    transition: all 0.3s ease;

    &:hover {
      background: rgba(255, 255, 255, 0.16);
      transform: translateX(6px);
    }

    .feature-icon {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 48px;
      height: 48px;
      border-radius: 14px;
      background: rgba(255, 255, 255, 0.18);
      color: #ffffff;
    }

    .feature-text {
      h4 {
        margin: 0 0 4px 0;
        color: #ffffff;
        font-size: 16px;
        font-weight: 750;
      }

      p {
        margin: 0;
        color: rgba(255, 255, 255, 0.76);
        font-size: 13px;
      }
    }
  }
}

.stat-number {
  display: flex;
  gap: 28px;
  padding-top: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.2);

  .stat-item {
    display: flex;
    flex-direction: column;

    .stat-value {
      color: #ffffff;
      font-size: 30px;
      font-weight: 900;
      line-height: 1.2;
    }

    .stat-label {
      margin-top: 4px;
      color: rgba(255, 255, 255, 0.76);
      font-size: 13px;
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
