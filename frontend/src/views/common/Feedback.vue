<template>
  <div class="feedback-page">
    <header class="feedback-hero">
      <button type="button" @click="goBack">返回</button>
      <span>{{ roleLabel }}反馈</span>
      <h1>遇到问题，直接告诉我</h1>
      <p>描述越具体，越容易定位。比如订单号、页面、发生了什么、你希望怎么处理。</p>
    </header>

    <main class="feedback-card">
      <el-form label-position="top">
        <el-form-item label="问题类型">
          <el-select v-model="form.category" class="full-width">
            <el-option label="订单问题" value="ORDER" />
            <el-option label="账号/登录" value="ACCOUNT" />
            <el-option label="功能异常" value="BUG" />
            <el-option label="改进建议" value="SUGGESTION" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="反馈内容">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="6"
            maxlength="1000"
            show-word-limit
            placeholder="请写清楚你在哪个页面遇到什么问题，越具体越好。"
          />
        </el-form-item>
        <el-form-item label="联系方式（选填）">
          <el-input v-model="form.contact" maxlength="100" placeholder="手机号、微信或邮箱，方便后续联系你" />
        </el-form-item>
        <el-form-item label="关联订单号（选填）">
          <el-input v-model="form.orderId" maxlength="32" placeholder="例如 CR2026..." />
        </el-form-item>
        <el-button type="primary" class="submit-btn" :loading="submitting" @click="submit">提交反馈</el-button>
      </el-form>
      <p class="feedback-note">反馈会进入平台待处理列表。紧急情况仍建议同时联系线下负责人。</p>
    </main>
  </div>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { submitCampusFeedback } from '../../api/campus-feedback'

const route = useRoute()
const router = useRouter()
const submitting = ref(false)

const role = computed(() => {
  const value = typeof route.query.role === 'string' ? route.query.role.toUpperCase() : 'USER'
  return value === 'PARTTIME' ? 'PARTTIME' : 'USER'
})
const roleLabel = computed(() => role.value === 'PARTTIME' ? '兼职端' : '用户端')
const pagePath = computed(() => typeof route.query.from === 'string' ? route.query.from : route.fullPath)

const form = reactive({
  category: 'OTHER',
  content: '',
  contact: '',
  orderId: ''
})

const submit = async () => {
  const content = form.content.trim()
  if (!content) {
    ElMessage.warning('先写一下遇到的问题吧')
    return
  }
  submitting.value = true
  try {
    await submitCampusFeedback({
      submitterRole: role.value,
      category: form.category,
      content,
      contact: form.contact.trim(),
      orderId: form.orderId.trim(),
      page: pagePath.value
    })
    ElMessage.success('已收到反馈，我会按优先级处理')
    form.content = ''
    form.contact = ''
    form.orderId = ''
  } finally {
    submitting.value = false
  }
}

const goBack = () => {
  if (window.history.length > 1) {
    router.back()
    return
  }
  router.push(role.value === 'PARTTIME' ? '/parttime/profile' : '/user/profile')
}
</script>

<style scoped lang="scss">
.feedback-page {
  min-height: 100vh;
  min-height: 100dvh;
  padding: calc(18px + env(safe-area-inset-top, 0px)) 16px calc(24px + env(safe-area-inset-bottom, 0px));
  background: linear-gradient(180deg, #f0fdfa 0%, #f8fafc 42%, #ffffff 100%);
  color: #18181b;
}

.feedback-hero,
.feedback-card {
  max-width: 520px;
  margin: 0 auto;
}

.feedback-hero {
  margin-bottom: 16px;

  button {
    border: 1px solid #d4d4d8;
    background: #ffffff;
    border-radius: 999px;
    padding: 8px 12px;
    color: #0f766e;
    font-weight: 700;
  }

  span {
    display: block;
    margin-top: 18px;
    color: #0f766e;
    font-size: 12px;
    font-weight: 800;
  }

  h1 {
    margin: 6px 0 8px;
    font-size: 25px;
  }

  p {
    margin: 0;
    color: #52525b;
    line-height: 1.7;
    font-size: 14px;
  }
}

.feedback-card {
  background: #ffffff;
  border: 1px solid #e4e4e7;
  border-radius: 18px;
  padding: 18px;
  box-shadow: 0 10px 28px rgba(15, 23, 42, 0.08);
}

.full-width,
.submit-btn {
  width: 100%;
}

.submit-btn {
  min-height: 46px;
  border-radius: 12px;
  font-weight: 800;
}

.feedback-note {
  margin: 14px 0 0;
  color: #71717a;
  font-size: 13px;
  line-height: 1.7;
}

:deep(.el-input__wrapper),
:deep(.el-select__wrapper),
:deep(.el-textarea__inner) {
  border-radius: 12px;
}
</style>
